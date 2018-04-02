package flamingo.onemovil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class lista_text_capturas extends AppCompatActivity {
    private Button btn_hide_lst_t, btn_do_lst_t, btn_cancel_lst_t;
    private TextView text_lst_t;
    private SQLiteDatabase db04;
    private Cursor data04;
    private String cSqlLn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_text_capturas);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        Locale.setDefault(new Locale("en", "US"));

        this.btn_hide_lst_t = (Button) findViewById(R.id.obtn_hide_lst_t);
        this.btn_do_lst_t = (Button) findViewById(R.id.obtn_do_lst_t);
        this.btn_cancel_lst_t = (Button) findViewById(R.id.obtn_cancel_lst_t);
        this.text_lst_t = (TextView) findViewById(R.id.otext_lst_t);

        this.Clear_Screen();

        String databasePath = getDatabasePath("one2009.db").getPath();
        this.db04 = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);

        if (this.Listar_Maquinas() == true) {
            this.Imprimir_Maquinas();

            if (this.Listar_Montos() == true) {
                //this.Imprimir_Montos();
            }
        }

        btn_do_lst_t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent Int_CteScreen = new Intent(getApplicationContext(), select_cte.class);
                startActivityForResult(Int_CteScreen, Global.REQUEST_SEL_CTE);
            }

        });

        btn_hide_lst_t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        });

        btn_cancel_lst_t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);

                db04.close();

                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Global.REQUEST_SEL_EMP) {
            switch (resultCode) {
                case RESULT_OK:
                    if ((Global.cCte_Id != "") || (Global.cCte_Id == "0")) {
                        Intent Int_CteScreen = new Intent(getApplicationContext(), select_cte.class);
                        startActivityForResult(Int_CteScreen, Global.REQUEST_SEL_CTE);
                    }

                    break;
                case RESULT_CANCELED:
                    Global.cEmp_Id = "";
                    Global.cEmp_De = "";
                    break;
            }
        }

        if (requestCode == Global.REQUEST_SEL_CTE) {
            //Valida la seleccion del cliente.
            switch (resultCode) {
                case RESULT_OK:
                    Clear_Screen();

                    if (this.Listar_Maquinas() == true) {
                        this.Imprimir_Maquinas();

                        if (this.Listar_Montos() == true) {
                            //this.Imprimir_Montos();
                        }
                    }
                    break;
                case RESULT_CANCELED:
                    Global.cCte_Id = "";
                    Global.cCte_De = "";
                    break;
            }
        }

    }

    private void Clear_Screen() {
        this.text_lst_t.setText("");
    }

    private boolean Listar_Maquinas() {

        cSqlLn = "";
        cSqlLn += "SELECT ";
        cSqlLn += " op.op_chapa, ";
        cSqlLn += " op.op_modelo, ";
        cSqlLn += " SUM(op.op_tot_colect) AS tot_cole, ";
        cSqlLn += " SUM(op.op_tot_cred)   AS tot_cred, ";
        cSqlLn += " (SUM(op.op_tot_colect)-SUM(op.op_tot_cred)) AS tot_dife ";
        cSqlLn += "FROM operacion op ";
        cSqlLn += "WHERE (op.id_device='" + Global.cid_device + "') ";
        cSqlLn += "AND   (op.op_emp_id='" + Global.cEmp_Id + "') ";
        cSqlLn += "AND   (op.cte_id   ='" + Global.cCte_Id + "') ";
        cSqlLn += "GROUP BY op.op_emp_id,op.op_chapa ";
        cSqlLn += "ORDER BY op.op_emp_id,op.op_modelo ";

        Log.d("SQL", cSqlLn);
        data04 = db04.rawQuery(cSqlLn, null);

        if ((data04 == null) || (data04.getCount() == 0)) {
            return false;
        } else {
            data04.moveToFirst();
            return true;
        }
    }

    private boolean Listar_Montos() {

        cSqlLn = "";
        cSqlLn += "SELECT ";
        cSqlLn += " SUM(op.op_tot_colect) AS tot_cole, ";
        cSqlLn += " SUM(op.op_tot_cred)   AS tot_cred, ";
        cSqlLn += " (SUM(op.op_tot_colect)-SUM(op.op_tot_cred)) AS tot_dife ";
        cSqlLn += "FROM operacion op ";
        cSqlLn += "WHERE op.id_device='" + Global.cid_device + "' ";
        cSqlLn += "AND   op.op_emp_id='" + Global.cEmp_Id + "' ";
        cSqlLn += "AND   op.cte_id   ='" + Global.cCte_Id + "' ";
        cSqlLn += "GROUP BY op.op_emp_id,id_device ";
        Log.d("SQL", cSqlLn);
        data04 = db04.rawQuery(cSqlLn, null);

        if ((data04 == null) || (data04.getCount() == 0)) {
            return false;
        } else {
            data04.moveToFirst();
            return true;
        }
    }

    private boolean Imprimir_Maquinas() {
        String cMaq_Chap = "", cMaq_Mode = "", ctot_cole = "", ctot_cred = "", ctot_dife = "";
        String cMet_DlE = "", cMet_DlS = "", cMet_DlD = "";
        String cLine = "";

        if ((data04 == null) || (data04.getCount() == 0)) {
            return false;
        } else {
            this.text_lst_t.append(Global.center("***" + Global.cEmp_De.toUpperCase().trim() + "***", 75 - 3, ' ') + "\n");
            this.text_lst_t.append(Global.center("LISTADO PRELIMIANR DE MAQUINAS COLECTADAS", 75 - 3, ' ') + "\n");
            this.text_lst_t.append(Global.center("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De + "]" + "\n", 75 - 3, ' ') + "\n");
            this.text_lst_t.append(Global.center("INFORMACION DE ->METROS/MONTOS<- CAPTURADOS\n", 75 - 3, ' ') + "\n");
            this.text_lst_t.append(Global.repeat('=', 75 - 3) + "\n");
            data04.moveToFirst();
            cLine = String.format("%8s %20s %12s %12s %12s", "CHAPA", "MODELO", "ENTRADAS", "SALIDAS", "DIFERENCIA");
            this.text_lst_t.append(cLine + "\n");
            this.text_lst_t.append(Global.repeat('=', 75 - 3) + "\n");

            do {
                cMaq_Chap = String.format(Locale.US, "%8s", data04.getString(data04.getColumnIndex("op_chapa")).trim());
                cMaq_Mode = String.format(Locale.US, "%20s", data04.getString(data04.getColumnIndex("op_modelo")).trim());
                ctot_cole = String.format(Locale.US, "%12.2f", data04.getDouble(data04.getColumnIndex("tot_cole")));
                ctot_cred = String.format(Locale.US, "%12.2f", data04.getDouble(data04.getColumnIndex("tot_cred")));
                ctot_dife = String.format(Locale.US, "%12.2f", data04.getDouble(data04.getColumnIndex("tot_dife")));
                cLine = String.format("%s %s %s %s %s", cMaq_Chap, cMaq_Mode, ctot_cole, ctot_cred, ctot_dife);
                this.text_lst_t.append(cLine + "\n");
            } while (data04.moveToNext());
            this.text_lst_t.append(Global.repeat('=', 75 - 3) + "\n");
            cLine = String.format("%s %6s", "TOTAL DE MAQUNAS:", data04.getCount());
            this.text_lst_t.append(cLine + "\n");

            return true;
        }
    }

    private boolean Imprimir_Montos() {
        String ctot_cole = "", ctot_cred = "", ctot_dife = "";
        String cLine = "";

        if ((data04 == null) || (data04.getCount() == 0)) {
            return false;
        } else {
            data04.moveToFirst();
            do {
                ctot_cole = String.format(Locale.US, "%12.2f", data04.getDouble(data04.getColumnIndex("tot_cole")));
                ctot_cred = String.format(Locale.US, "%12.2f", data04.getDouble(data04.getColumnIndex("tot_cred")));
                ctot_dife = String.format(Locale.US, "%12.2f", data04.getDouble(data04.getColumnIndex("tot_dife")));

                //text_lst_t.append("Colectado : " + String.format(Locale.US, "%12.2f", data04.getDouble(0))+ "\n");
                //text_lst_t.append("Credito   : " + String.format(Locale.US, "%12.2f", data04.getDouble(1))+ "\n");
                //text_lst_t.append("Diferencia: " + String.format(Locale.US,"%12.2f", data04.getDouble(2))+ "\n");
                cLine = String.format("%8s %20s %s %s %s", "", "", ctot_cole, ctot_cred, ctot_dife);
                text_lst_t.append(cLine + "\n");
            } while (data04.moveToNext());
            this.text_lst_t.append(Global.repeat('=', 75 - 3) + "\n");

            return true;
        }
    }

    private boolean Imprimir_Montos2() {
        if (Global.iPrn_Data == 2) {
            try {

                this.text_lst_t.append(Global.repeat('=', 75 - 3) + "\n");
                text_lst_t.append("COLECTADO :" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_cole")) + "\n");
                text_lst_t.append("TIMBRES   :" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_timb")) + "\n");
                text_lst_t.append("IMUESTOS  :" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_impm")) + "\n");
                text_lst_t.append("J.C.J     :" + String.format("%12.2f%n", Global.Genobj.getDouble("dot__jcj")) + "\n");
                text_lst_t.append("SERV. TEC.:" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_tecn")) + "\n");
                text_lst_t.append("TOT. DEVO.:" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_devo")) + "\n");
                text_lst_t.append("TOT. OTRO :" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_otro")) + "\n");
                text_lst_t.append("TOT. CRED.:" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_cred")) + "\n");
                text_lst_t.append("SUB-TOTAL :" + String.format("%12.2f%n", Global.Genobj.getDouble("Sub_tota")) + "\n");
                text_lst_t.append("TOT. IMP. :" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_impu")) + "\n");
                text_lst_t.append("TOTAL     :" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_tota")) + "\n");
                text_lst_t.append("BRUTO CTE.:" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_bloc")) + "\n");
                text_lst_t.append("BRUTO EMP.:" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_bemp")) + "\n");
                text_lst_t.append("NETO  CTE.:" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_nloc")) + "\n");
                text_lst_t.append("NETO  EMP.:" + String.format("%12.2f%n", Global.Genobj.getDouble("tot_nemp")) + "\n");
                this.text_lst_t.append(Global.repeat('=', 75 - 3) + "\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
