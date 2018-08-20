package flamingo.onemovil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;

import io.requery.android.database.sqlite.SQLiteDatabase;

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
import android.widget.Toast;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class lista_text_capturas extends AppCompatActivity {
    private Button obtn_hide_lst_t, obtn_do_lst_t, obtn_cancel_lst_t, obtn_print_lst_t;
    private TextView otext_lst_t;
    private SQLiteDatabase db04;
    private Cursor data04;
    private String cSqlLn;
    String cText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_text_capturas);

        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        Locale.setDefault(new Locale("en", "US"));

        this.obtn_hide_lst_t = (Button) findViewById(R.id.obtn_hide_lst_t);
        this.obtn_do_lst_t = (Button) findViewById(R.id.obtn_do_lst_t);
        this.obtn_cancel_lst_t = (Button) findViewById(R.id.obtn_cancel_lst_t);
        this.otext_lst_t = (TextView) findViewById(R.id.otext_lst_t);
        this.obtn_print_lst_t = (Button) findViewById(R.id.btn_print_lst_t);

        this.Clear_Screen();

        String databasePath = getDatabasePath("one2009.db").getPath();
        this.db04 = io.requery.android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(databasePath, null, null);

        Global.save_in_textfile(Global.cFileRepPathDestC, "", false);

        this.Imprimir2_Titles();
        if (this.Listar_Maquinas() == true) {
            this.Imprimir_Maquinas();

            if (this.Listar_Montos() == true) {
            }
            this.Mostrar_Listado();
        }

        this.obtn_print_lst_t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Global.ExportDB();
                Global.iPrn_Data = 1;
                Intent Int_PrnLstScreen = new Intent(getApplicationContext(), print_data.class);
                startActivityForResult(Int_PrnLstScreen, Global.REQUEST_PRINT);
            }
        });

        this.obtn_do_lst_t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent Int_CteScreen = new Intent(getApplicationContext(), select_cte.class);
                startActivityForResult(Int_CteScreen, Global.REQUEST_SEL_CTE);
            }

        });

        this.obtn_hide_lst_t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        });

        this.obtn_cancel_lst_t.setOnClickListener(new View.OnClickListener() {

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
        this.otext_lst_t.setText("");
    }

    private void Imprimir2_Titles() {
        cText = Global.center("***" + Global.cEmp_De.toUpperCase().trim() + "***", 110, ' ') + "\n";
        Global.save_in_textfile(Global.cFileRepPathDestC, cText, true);
        cText = Global.center("LISTADO PRELIMIANR DE MAQUINAS COLECTADAS", 110, ' ') + "\n";
        Global.save_in_textfile(Global.cFileRepPathDestC, cText, true);
        cText = Global.center("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De + "]", 110, ' ') + "\n";
        Global.save_in_textfile(Global.cFileRepPathDestC, cText, true);
        cText = Global.center("INFORMACION DE ->METROS/MONTOS<- CAPTURADOS", 110, ' ') + "\n";
        Global.save_in_textfile(Global.cFileRepPathDestC, cText, true);
        cText = Global.repeat('=', 110) + "\n";
        Global.save_in_textfile(Global.cFileRepPathDestC, cText, true);
    }

    private void Mostrar_Listado() {
        String sline = "";
        this.otext_lst_t.setText("");
        try {
            FileReader fReader = new FileReader(Global.cFileRepPathDestC);
            BufferedReader bReader = new BufferedReader(fReader);

            while ((sline = bReader.readLine()) != null) {
                otext_lst_t.append(sline + "\n");
            }
            fReader = null;
            bReader = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean Listar_Maquinas() {

        cSqlLn = "";
        cSqlLn += "SELECT ";
        cSqlLn += " em.emp_abrev , ";
        cSqlLn += " op.op_fecha  , ";
        cSqlLn += " op.op_chapa  , ";
        cSqlLn += " op.op_modelo , ";
        cSqlLn += " op.op_semanas_imp, ";
        cSqlLn += " op.op_tot_colect AS tot_cole, ";
        cSqlLn += " op.op_tot_cred   AS tot_cred, ";
        cSqlLn += " (op.op_tot_colect - op.op_tot_cred) AS tot_dife ";
        //cSqlLn += " SUM(op.op_tot_colect) AS tot_cole, ";
        //cSqlLn += " SUM(op.op_tot_cred)   AS tot_cred, ";
        //cSqlLn += " (SUM(op.op_tot_colect)-SUM(op.op_tot_cred)) AS tot_dife ";
        cSqlLn += "FROM operacion op ";
        cSqlLn += "LEFT JOIN empresas em ON op.op_emp_id = em.emp_id ";
        cSqlLn += "WHERE (op.id_device='" + Global.cid_device + "') ";
        cSqlLn += "AND   (op.op_emp_id='" + Global.cEmp_Id + "') ";
        cSqlLn += "AND   (op.cte_id   ='" + Global.cCte_Id + "') ";
        //cSqlLn += "GROUP BY op.op_emp_id, op.cte_id, op.op_chapa ";
        cSqlLn += "ORDER BY op.op_emp_id, op.cte_id, op.op_chapa ";

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
        cSqlLn += "WHERE (op.id_device='" + Global.cid_device + "') ";
        cSqlLn += "AND   (op.op_emp_id='" + Global.cEmp_Id + "') ";
        cSqlLn += "AND   (op.cte_id   ='" + Global.cCte_Id + "') ";
        cSqlLn += "GROUP BY op.op_emp_id, op.cte_id ";
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
        String cFec_Capt1, cFec_Capt, cNom_Empr, cSem_Impu, cMaq_Chap = "", cMaq_Mode = "", ctot_cole = "", ctot_cred = "", ctot_dife = "";
        String cMet_DlE = "", cMet_DlS = "", cMet_DlD = "";
        String cLine = "";

        if ((data04 == null) || (data04.getCount() == 0)) {
            return false;
        } else {
            data04.moveToFirst();

            cLine = String.format("%20s %4s %8s %20s %6s %12s %12s %12s", "FECHA-HORA", "EMP.", "CHAPA", "MODELO", "S.IMP.", "ENTRADAS", "SALIDAS", "DIFERENCIA");
            cText = cLine + "\n";
            Global.save_in_textfile(Global.cFileRepPathDestC, cText, true);
            cText = Global.repeat('=', 110) + "\n";
            Global.save_in_textfile(Global.cFileRepPathDestC, cText, true);

            do {
                cFec_Capt1 = data04.getString(data04.getColumnIndex("op_fecha")).trim();
                cFec_Capt = String.format(Locale.US, "%20s", cFec_Capt1);
                cNom_Empr = String.format(Locale.US, "%4s", data04.getString(data04.getColumnIndex("emp_abrev")).trim());
                cSem_Impu = String.format(Locale.US, "%4s", data04.getString(data04.getColumnIndex("op_semanas_imp")).trim());
                cMaq_Chap = String.format(Locale.US, "%8s", data04.getString(data04.getColumnIndex("op_chapa")).trim());
                cMaq_Mode = String.format(Locale.US, "%20s", data04.getString(data04.getColumnIndex("op_modelo")).trim());
                ctot_cole = String.format(Locale.US, "%12.2f", data04.getDouble(data04.getColumnIndex("tot_cole")));
                ctot_cred = String.format(Locale.US, "%12.2f", data04.getDouble(data04.getColumnIndex("tot_cred")));
                ctot_dife = String.format(Locale.US, "%12.2f", data04.getDouble(data04.getColumnIndex("tot_dife")));
                cLine = String.format("%s %s %s %s %s %s %s %s", cFec_Capt, cNom_Empr, cMaq_Chap, cMaq_Mode, "S[" + cSem_Impu.trim() + "]", ctot_cole, ctot_cred, ctot_dife);
                cText = cLine + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestC, cText, true);
            } while (data04.moveToNext());
            cText = Global.repeat('=', 110) + "\n";
            Global.save_in_textfile(Global.cFileRepPathDestC, cText, true);
            cLine = String.format("%s %6s", "TOTAL DE MAQUNAS:", data04.getCount());
            cText = cLine + "\n";
            Global.save_in_textfile(Global.cFileRepPathDestC, cText, true);

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
                cText = cLine + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestC, cText, true);
            } while (data04.moveToNext());
            cText = Global.repeat('=', 110) + "\n";
            Global.save_in_textfile(Global.cFileRepPathDestC, cText, true);

            return true;
        }
    }
}
