package flamingo.onemovil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;

import io.requery.android.database.sqlite.*;
import io.requery.android.database.DatabaseErrorHandler;
import io.requery.android.database.DefaultDatabaseErrorHandler;

import android.arch.persistence.db.SupportSQLiteDatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class activity_lista_text_finalizados extends AppCompatActivity {
    private Button obtn_hide_lst2_t, obtn_do_lst2_t, obtn_cancel_lst2_t, obtn_print_lst2_t;
    private TextView otext_lst2_t;
    private SQLiteDatabase db05;
    private Cursor data05;
    private String cSqlLn;
    String cText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_text_finalizados);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        Locale.setDefault(new Locale("en", "US"));

        this.obtn_hide_lst2_t = (Button) findViewById(R.id.obtn_hide_lst2_t);
        this.obtn_do_lst2_t = (Button) findViewById(R.id.obtn_do_lst2_t);
        this.obtn_cancel_lst2_t = (Button) findViewById(R.id.obtn_cancel_lst2_t);
        this.otext_lst2_t = (TextView) findViewById(R.id.otext_lst2_t);
        this.obtn_print_lst2_t = (Button) findViewById(R.id.btn_print_lst2_t);

        this.Clear2_Screen();

        String databasePath = getDatabasePath("one2009.db").getPath();
        this.db05 = io.requery.android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(databasePath, null, null);

        Global.save_in_textfile(Global.cFileRepPathDest, "", false);
        this.Imprimir2_Titles();
        if (this.Listar2_Maquinas() == true) {
            this.Imprimir2_Maquinas();
            if (this.Listar2_Montos() == true) {
                this.Imprimir2_Montos();
            }
            this.Mostrar_Listado();
        }

        this.obtn_print_lst2_t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Global.ExportDB();
                Global.iPrn_Data = 2;
                Intent Int_PrnLstScreen = new Intent(getApplicationContext(), print_data.class);
                startActivityForResult(Int_PrnLstScreen, Global.REQUEST_PRINT);
            }
        });

        this.obtn_do_lst2_t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent Int_CteScreen = new Intent(getApplicationContext(), select_cte.class);
                startActivityForResult(Int_CteScreen, Global.REQUEST_SEL_CTE);
            }

        });

        this.obtn_hide_lst2_t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        });

        this.obtn_cancel_lst2_t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);

                db05.close();

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
                    Clear2_Screen();

                    if (this.Listar2_Montos() == true) {
                        this.Imprimir2_Montos();
                        if (this.Listar2_Maquinas() == true) {
                            this.Imprimir2_Maquinas();
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

    private void Mostrar_Listado() {
        String sline = "";
        this.otext_lst2_t.setText("");
        try {
            FileReader fReader = new FileReader(Global.cFileRepPathDest);
            BufferedReader bReader = new BufferedReader(fReader);
            while ((sline = bReader.readLine()) != null) {
                this.otext_lst2_t.append(sline + "\n");
            }
            fReader = null;
            bReader = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Clear2_Screen() {
        this.otext_lst2_t.setText("");
    }

    private boolean Listar2_Montos() {
        cSqlLn = "";
        cSqlLn += "SELECT ";
        cSqlLn += " SUM(op.op_tot_colect)   AS tot_cole         , ";
        cSqlLn += " SUM(op.op_tot_impmunic) AS op_tot_impmunic  , ";
        cSqlLn += " SUM(op.op_tot_impjcj)   AS op_tot_impjcj    , ";
        cSqlLn += " SUM(op.op_tot_timbres)  AS op_tot_timbres   , ";
        cSqlLn += " SUM(op.op_tot_tec)      AS op_tot_tec       , ";
        cSqlLn += " SUM(op.op_tot_dev)      AS op_tot_dev       , ";
        cSqlLn += " SUM(op.op_tot_otros)    AS op_tot_otros     , ";
        cSqlLn += " SUM(op.op_tot_cred)     AS op_tot_cred      , ";
        cSqlLn += " SUM(op.op_tot_sub)      AS op_tot_sub       , ";
        cSqlLn += " SUM(op.op_tot_itbm)     AS op_tot_itbm      , ";
        cSqlLn += " SUM(op.op_tot_tot)      AS op_tot_tot       , ";
        cSqlLn += " SUM(op.op_tot_brutoloc) AS op_tot_brutoloc  , ";
        cSqlLn += " SUM(op.op_tot_brutoemp) AS op_tot_brutoemp  , ";
        cSqlLn += " SUM(op.op_tot_netoloc)  AS op_tot_netoloc   , ";
        cSqlLn += " SUM(op.op_tot_netoemp)  AS op_tot_netoemp    ";
        cSqlLn += "FROM operaciong op ";
        cSqlLn += "WHERE (op.id_device='" + Global.cid_device + "') ";
        cSqlLn += "AND   (op.op_emp_id='" + Global.cEmp_Id + "') ";
        cSqlLn += "AND   (op.cte_id   ='" + Global.cCte_Id + "') ";
        cSqlLn += "AND   (op.op_usermodify ='1') ";
        cSqlLn += "GROUP BY op.op_emp_id, op.cte_id ";
        Log.d("SQL", cSqlLn);
        data05 = db05.rawQuery(cSqlLn, null);

        if ((data05 == null) || (data05.getCount() == 0)) {
            return false;
        } else {
            data05.moveToFirst();
            return true;
        }
    }

    private boolean Listar2_Maquinas() {

        cSqlLn = "";
        cSqlLn += "SELECT ";
        cSqlLn += " em.emp_abrev , ";
        cSqlLn += " op.op_fecha  , ";
        cSqlLn += " op.op_chapa, ";
        cSqlLn += " op.op_modelo, ";
        cSqlLn += " op.op_tot_colect   AS tot_cole         , ";
        cSqlLn += " op.op_tot_impmunic AS op_tot_impmunic  , ";
        cSqlLn += " op.op_tot_impjcj   AS op_tot_impjcj    , ";
        cSqlLn += " op.op_tot_timbres  AS op_tot_timbres   , ";
        cSqlLn += " op.op_tot_tec      AS op_tot_tec       , ";
        cSqlLn += " op.op_tot_dev      AS op_tot_dev       , ";
        cSqlLn += " op.op_tot_otros    AS op_tot_otros     , ";
        cSqlLn += " op.op_tot_cred     AS op_tot_cred      , ";
        cSqlLn += " op.op_tot_sub      AS op_tot_sub       , ";
        cSqlLn += " op.op_tot_itbm     AS op_tot_itbm      , ";
        cSqlLn += " op.op_tot_tot      AS op_tot_tot       , ";
        cSqlLn += " op.op_tot_brutoloc AS op_tot_brutoloc  , ";
        cSqlLn += " op.op_tot_brutoemp AS op_tot_brutoemp  , ";
        cSqlLn += " op.op_tot_netoloc  AS op_tot_netoloc   , ";
        cSqlLn += " op.op_tot_netoemp  AS op_tot_netoemp   , ";
        cSqlLn += " op.op_baja_prod    AS op_baja_prod       ";
        //cSqlLn += " SUM(op.op_tot_colect)   AS tot_cole         , ";
        //cSqlLn += " SUM(op.op_tot_impmunic) AS op_tot_impmunic  , ";
        //cSqlLn += " SUM(op.op_tot_impjcj)   AS op_tot_impjcj    , ";
        //cSqlLn += " SUM(op.op_tot_timbres)  AS op_tot_timbres   , ";
        //cSqlLn += " SUM(op.op_tot_tec)      AS op_tot_tec       , ";
        //cSqlLn += " SUM(op.op_tot_dev)      AS op_tot_dev       , ";
        //cSqlLn += " SUM(op.op_tot_otros)    AS op_tot_otros     , ";
        //cSqlLn += " SUM(op.op_tot_cred)     AS op_tot_cred      , ";
        //cSqlLn += " SUM(op.op_tot_sub)      AS op_tot_sub       , ";
        //cSqlLn += " SUM(op.op_tot_itbm)     AS op_tot_itbm      , ";
        //cSqlLn += " SUM(op.op_tot_tot)      AS op_tot_tot       , ";
        //cSqlLn += " SUM(op.op_tot_brutoloc) AS op_tot_brutoloc  , ";
        //cSqlLn += " SUM(op.op_tot_brutoemp) AS op_tot_brutoemp  , ";
        //cSqlLn += " SUM(op.op_tot_netoloc)  AS op_tot_netoloc   , ";
        //cSqlLn += " SUM(op.op_tot_netoemp)  AS op_tot_netoemp   , ";
        //cSqlLn += " SUM(op.op_baja_prod)    AS op_baja_prod       ";
        cSqlLn += "FROM operacion op ";
        cSqlLn += "LEFT JOIN empresas em ON op.op_emp_id = em.emp_id ";
        cSqlLn += "WHERE (op.id_device='" + Global.cid_device + "') ";
        cSqlLn += "AND   (op.op_emp_id='" + Global.cEmp_Id + "') ";
        cSqlLn += "AND   (op.cte_id   ='" + Global.cCte_Id + "') ";
        cSqlLn += "AND   (op.op_usermodify ='1') ";
        //cSqlLn += "GROUP BY op.op_emp_id, op.cte_id, op.op_chapa ";
        cSqlLn += "ORDER BY op.op_emp_id, op.cte_id, op.op_chapa ";
        Log.d("SQL", cSqlLn);
        data05 = db05.rawQuery(cSqlLn, null);

        if ((data05 == null) || (data05.getCount() == 0)) {
            return false;
        } else {
            data05.moveToFirst();
            return true;
        }
    }

    private void Imprimir2_Titles() {
        cText = Global.center("***" + Global.cEmp_De.toUpperCase().trim() + "***", 180, ' ') + "\n";
        Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
        cText = Global.center("LISTADO DE MAQUINAS COLECTADAS", 180, ' ') + "\n";
        Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
        cText = Global.center("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De + "]", 180, ' ') + "\n";
        Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
        cText = Global.repeat('=', 180) + "\n";
        Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
    }

    private boolean Imprimir2_Maquinas() {
        String cMaq_Chap = "", cMaq_Mode = "";
        String ctot_cole, ctot_impu, ctot__jcj, ctot_timb, ctot_tecn, ctot_otos, ctot_cred, ctot_subt, ctot_itbm, ctot_tota, ctot_bloc, ctot_bemp, ctot_nloc, ctot_nemp, ctot_bajp;
        String cLine = "";

        if ((data05 == null) || (data05.getCount() == 0)) {
            return false;
        } else {
            data05.moveToFirst();
            cLine = String.format("%8s %18s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s %10s",
                    "CHAPA", "MODELO",
                    "COL", "MUN", "JCJ", "TIM", "TEC", "OTR", "CRED.", "SUBT", "IMP.", "TOT", "BLOC", "BEMP", "NLOC", "NEMP");
            cText = cLine + "\n";
            Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
            cText = Global.repeat('=', 180) + "\n";
            Global.save_in_textfile(Global.cFileRepPathDest, cText, true);

            do {
                cMaq_Chap = String.format("%8s", data05.getString(data05.getColumnIndex("op_chapa")).trim());
                cMaq_Mode = String.format("%18s", data05.getString(data05.getColumnIndex("op_modelo")).trim());
                ctot_cole = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("tot_cole")));
                ctot_impu = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_impmunic")));
                ctot__jcj = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_impjcj")));
                ctot_timb = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_timbres")));
                ctot_tecn = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_tec")));
                ctot_otos = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_otros")));
                ctot_cred = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_cred")));
                ctot_subt = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_sub")));
                ctot_itbm = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_itbm")));
                ctot_tota = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_tot")));
                ctot_bloc = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_brutoloc")));
                ctot_bemp = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_brutoemp")));
                ctot_nloc = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_netoloc")));
                ctot_nemp = String.format(Locale.US, "%10.2f", data05.getDouble(data05.getColumnIndex("op_tot_netoemp")));
                ctot_bajp = String.format(Locale.US, "%2d", data05.getInt(data05.getColumnIndex("op_baja_prod")));

                cLine = String.format("%s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s",
                        cMaq_Chap, cMaq_Mode,
                        ctot_cole, ctot_impu, ctot__jcj, ctot_timb, ctot_tecn, ctot_otos, ctot_cred,
                        ctot_subt, ctot_itbm, ctot_tota, ctot_bloc, ctot_bemp, ctot_nloc, ctot_nemp);

                cText = cLine + "\n";
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
            } while (data05.moveToNext());
            cText = Global.repeat('=', 180) + "\n";
            Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
            cLine = String.format("%s %6s", "TOTAL DE MAQUNAS:", data05.getCount());
            cText = cLine + "\n";
            Global.save_in_textfile(Global.cFileRepPathDest, cText, true);

            return true;
        }
    }

    private boolean Imprimir2_Montos() {

        if ((data05 == null) || (data05.getCount() == 0)) {
            return false;
        } else {

            cText = "\n";
            Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
            cText = Global.repeat('=', 180) + "\n";
            Global.save_in_textfile(Global.cFileRepPathDest, cText, true);


            data05.moveToFirst();
            Global.Genobj = new JSONObject();

            do {

                try {
                    Global.Genobj.put("tot_cole", data05.getDouble(data05.getColumnIndex("tot_cole")));
                    Global.Genobj.put("tot_timb", data05.getDouble(data05.getColumnIndex("op_tot_timbres")));
                    Global.Genobj.put("tot_impm", data05.getDouble(data05.getColumnIndex("op_tot_impmunic")));
                    Global.Genobj.put("dot__jcj", data05.getDouble(data05.getColumnIndex("op_tot_impjcj")));
                    Global.Genobj.put("tot_tecn", data05.getDouble(data05.getColumnIndex("op_tot_tec")));
                    Global.Genobj.put("tot_devo", data05.getDouble(data05.getColumnIndex("op_tot_dev")));
                    Global.Genobj.put("tot_otro", data05.getDouble(data05.getColumnIndex("op_tot_otros")));
                    Global.Genobj.put("tot_cred", data05.getDouble(data05.getColumnIndex("op_tot_cred")));
                    Global.Genobj.put("Sub_tota", data05.getDouble(data05.getColumnIndex("op_tot_sub")));
                    Global.Genobj.put("tot_impu", data05.getDouble(data05.getColumnIndex("op_tot_itbm")));
                    Global.Genobj.put("tot_tota", data05.getDouble(data05.getColumnIndex("op_tot_tot")));
                    Global.Genobj.put("tot_bloc", data05.getDouble(data05.getColumnIndex("op_tot_brutoloc")));
                    Global.Genobj.put("tot_bemp", data05.getDouble(data05.getColumnIndex("op_tot_brutoemp")));
                    Global.Genobj.put("tot_nloc", data05.getDouble(data05.getColumnIndex("op_tot_netoloc")));
                    Global.Genobj.put("tot_nemp", data05.getDouble(data05.getColumnIndex("op_tot_netoemp")));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                cText = "COLECTADO :" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("tot_cole")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "TIMBRES   :" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_timbres")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "IMUESTOS  :" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_impmunic")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "J.C.J     :" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_impjcj")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "SERV. TEC.:" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_tec")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "TOT. DEVO.:" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_dev")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "TOT. OTRO :" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_otros")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "TOT. CRED.:" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_cred")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "SUB-TOTAL :" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_sub")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "TOT. IMP. :" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_itbm")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "TOTAL     :" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_tot")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "BRUTO CTE.:" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_brutoloc")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "BRUTO EMP.:" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_brutoemp")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "NETO  CTE.:" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_netoloc")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
                cText = "NETO  EMP.:" + String.format(Locale.US, "%10.2f%n", data05.getDouble(data05.getColumnIndex("op_tot_netoemp")));
                Global.save_in_textfile(Global.cFileRepPathDest, cText, true);
            } while (data05.moveToNext());
            cText = Global.repeat('=', 180) + "\n";
            Global.save_in_textfile(Global.cFileRepPathDest, cText, true);

            return true;
        }

    }
}
