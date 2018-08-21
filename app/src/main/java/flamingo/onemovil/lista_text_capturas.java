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
    private Button oPrn1_btn_hide,oPrn1_btn_ctes, oPrn1_btn_canc, oPrn1_btn_prin;
    private TextView oPrn1_text_any;
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

        this.oPrn1_btn_hide = (Button) findViewById(R.id.Prn1_btn_hide);
        this.oPrn1_btn_canc = (Button) findViewById(R.id.Prn1_btn_canc);
        this.oPrn1_btn_ctes = (Button) findViewById(R.id.Prn1_btn_ctes);
        this.oPrn1_btn_prin = (Button) findViewById(R.id.Prn1_btn_prin);

        this.oPrn1_text_any = (TextView) findViewById(R.id.Prn1_text_any);

        this.Clear_Screen();

        String databasePath = getDatabasePath("one2009.db").getPath();
        this.db04 = io.requery.android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(databasePath, null, null);

        Global.save_in_textfile(Global.cFileRepPathDestC, "", false);
        this.prn1_list_titl();
        if (this.prn1_qry_lst_maq() == true) {
            this.prn1_list_maq();

            if (this.prn1_qry_lst_mont() == true) {
            }
            this.Mostrar_Listado();
        }

        this.oPrn1_btn_prin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Global.ExportDB();
                Global.iPrn_Data = 1;
                Intent Int_PrnLstScreen = new Intent(getApplicationContext(), print_data.class);
                startActivityForResult(Int_PrnLstScreen, Global.REQUEST_PRINT);
            }
        });

        this.oPrn1_btn_hide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        });

        this.oPrn1_btn_ctes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Global.bAutoSelEmp = true;
                Global.bAutoSelCte = true;
                Global.bAutoSelMaq = false;
                Global.bAutoSelCapM = false;
                Global.bAutoSelCapF = false;
                Global.bAutoSelList = false;
                Global.bAutoSelList2 = false;
                Global.cEmp_Id = "";
                Global.cCte_Id = "";

                if ((Global.cEmp_Id == "") || (Global.cEmp_Id == "0")) {
                    Intent Int_EmpScreen = new Intent(getApplicationContext(), select_emp.class);
                    startActivityForResult(Int_EmpScreen, Global.REQUEST_SEL_EMP);
                } else {
                    if ((Global.cCte_Id == "") || (Global.cCte_Id == "0")) {
                        Intent Int_CteScreen = new Intent(getApplicationContext(), select_cte.class);
                        startActivityForResult(Int_CteScreen, Global.REQUEST_SEL_CTE);
                    } else {
                        Global.save_in_textfile(Global.cFileRepPathDestF, "", false);
                        oPrn1_text_any.setText("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De + "]");

                        Global.save_in_textfile(Global.cFileRepPathDestC, "", false);
                        prn1_list_titl();
                        if (prn1_qry_lst_maq() == true) {
                            prn1_list_maq();

                            if (prn1_qry_lst_mont() == true) {
                            }
                            Mostrar_Listado();
                        }
                    }
                }
            }

        });

        this.oPrn1_btn_canc.setOnClickListener(new View.OnClickListener() {

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
        // check if the request code is same as what is passed  here it is 2

        if (requestCode == Global.REQUEST_SEL_EMP) {
            switch (resultCode) {
                case RESULT_OK:
                    //Toast.makeText(this, "Aceptó la empresa:[" + Global.cEmp_Id.trim() + "]/" + Global.cEmp_De.trim(), Toast.LENGTH_SHORT).show();
                    if (Global.bAutoSelCte == true) {
                        Intent Int_CteScreen = new Intent(getApplicationContext(), select_cte.class);
                        startActivityForResult(Int_CteScreen, Global.REQUEST_SEL_CTE);
                        Global.bAutoSelCte = false;
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
                    //Toast.makeText(this, "Aceptó el cliente:[" + Global.cCte_Id.trim() + "]/" + Global.cCte_De.trim(), Toast.LENGTH_SHORT).show();

                    Global.save_in_textfile(Global.cFileRepPathDestF, "", false);
                    this.oPrn1_text_any.setText("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De + "]");
                    Global.save_in_textfile(Global.cFileRepPathDestC, "", false);
                    prn1_list_titl();
                    if (prn1_qry_lst_maq() == true) {
                        prn1_list_maq();

                        if (prn1_qry_lst_mont() == true) {
                        }
                        Mostrar_Listado();
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
        this.oPrn1_text_any.setText("");
    }

    private void prn1_list_titl() {
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
        this.oPrn1_text_any.setText("");
        try {
            FileReader fReader = new FileReader(Global.cFileRepPathDestC);
            BufferedReader bReader = new BufferedReader(fReader);

            while ((sline = bReader.readLine()) != null) {
                this.oPrn1_text_any.append(sline + "\n");
            }
            fReader = null;
            bReader = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean prn1_qry_lst_maq() {

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
        cSqlLn += "FROM operacion op ";
        cSqlLn += "LEFT JOIN empresas em ON op.op_emp_id = em.emp_id ";
        cSqlLn += "WHERE (op.id_device='" + Global.cid_device + "') ";
        cSqlLn += "AND   (op.op_emp_id='" + Global.cEmp_Id + "') ";
        cSqlLn += "AND   (op.cte_id   ='" + Global.cCte_Id + "') ";
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

    private boolean prn1_qry_lst_mont() {
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

    private boolean prn1_list_maq() {
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

    private boolean prn1_list_mont() {
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
