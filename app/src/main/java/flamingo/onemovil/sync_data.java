package flamingo.onemovil;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class sync_data extends AppCompatActivity {

    private Button btn_proc, btn_regr, btn_json;
    private CheckBox ock_rene, ock_delc;
    private SQLiteDatabase db2;
    private EditText memo;
    private TextView lurl, InternetStatus;
    private String myurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        ock_rene = (CheckBox) findViewById(R.id.ock_renew);
        ock_delc = (CheckBox) findViewById(R.id.ock_del_capt);

        btn_proc = (Button) findViewById(R.id.obtn_proc);
        btn_regr = (Button) findViewById(R.id.obtn_regr);
        btn_json = (Button) findViewById(R.id.obtn_json);
        memo = (EditText) findViewById(R.id.omemo);
        lurl = (TextView) findViewById(R.id.olurl);
        InternetStatus = (TextView) findViewById(R.id.oInternetStatus);

        ock_rene.setChecked(true);
        //InternetStatus.setTextColor(Color.rgb(200,0,0));
        //InternetStatus.setTextColor(Color.parseColor("#009900"));

        String cInternetAnswer = "";
        Boolean bInternetConnected = false;
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                cInternetAnswer = "CONEXION INTERNET WIFI.";
                bInternetConnected = true;
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                cInternetAnswer = "CONEXION INTERNET MOBIL DATA.";
            }
            bInternetConnected = true;
        } else {
            cInternetAnswer = "SIN CONEXION A INTERNET.";
            bInternetConnected = false;
        }

        InternetStatus.setText(cInternetAnswer);
        if (bInternetConnected == true) {
            InternetStatus.setTextColor(Color.parseColor("#009900"));
            getWindow().getDecorView().getRootView().setBackgroundColor(Color.parseColor("#ffffff"));
            btn_proc.setEnabled(true);
        } else {
            getWindow().getDecorView().getRootView().setBackgroundColor(Color.parseColor("#ffc2b3"));
            InternetStatus.setTextColor(Color.parseColor("#ff0000"));
            btn_proc.setEnabled(false);
        }

        myurl = Global.SERVER_URL;
        lurl.setText("CONECTADO A:[" + Global.SERVER_URL + "]");

        String databasePath = getDatabasePath("one2009.db").getPath();
        db2 = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);

        btn_proc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String cParsString = "";
                String cSql_Ln = "";

                memo.setText("");
                btn_proc.setEnabled(false);
                btn_regr.setEnabled(false);
                Global.Create_Sql_Tables(ock_rene.isChecked(), ock_delc.isChecked());

                db2.execSQL("DELETE FROM empresas");
                db2.execSQL("DELETE FROM clientes");
                db2.execSQL("DELETE FROM denominaciones");
                db2.execSQL("DELETE FROM maquinastc");
                db2.execSQL("DELETE FROM maquinas_lnk");
                db2.execSQL("DELETE FROM municipios");
                db2.execSQL("DELETE FROM rutas");

                //    0:'TODOS'
                //    1:'empresas'
                //    2:'clientes'
                //    3:'denominaciones'
                //    4:'maquinas'
                //    5:'maquinas_lnk'
                //    6:'municipios'
                //    7:'rutas'
                String cCmd = "";

                cSql_Ln = "SELECT emp_id FROM device_db.dispositivos WHERE serial='" + Global.cid_device + "'";
                String myEmp = Global.Rem_Query_Result("device_db", cSql_Ln, 0, "emp_id");

                cParsString = "";
                cParsString += "table_no=0";
                cParsString += "&emp_id=" + myEmp;
                String script = Global.gen_execute_post(Global.SERVER_URL, "/flam/get_all_data.php", cParsString);

                /*
                execute_post();

                String script = memo.getText().toString();
                memo.setText("");
                */

                String[] queries = script.split(";");
                for (String query : queries) {
                    cCmd = query;
                    try {
                        if (query != "") {
                            db2.execSQL(query);
                        }
                    } catch (Exception e) {
                        memo.append("error:" + cCmd);
                        Toast.makeText(getApplicationContext(), "Error al ejecutar el comando:" + cCmd, Toast.LENGTH_LONG).show();
                    }
                }
                script = null;
                queries = null;

                Toast.makeText(getApplicationContext(), "PROCESO FINALIZADO.", Toast.LENGTH_LONG).show();
                memo.setText("");
                memo.append(Global.repeat('-', 80) + "\n");
                memo.append(Global.CenterString("RESUMEN DE SINCRONIZACION", 30) + "\n");
                memo.append(Global.repChar('-', 80) + "\n");
                memo.append("REGISTROS EN EMPRESA       :" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM empresas") + "\n");
                memo.append("REGISTROS EN CLIENTES      :" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM clientes") + "\n");
                memo.append("REGISTROS EN DENOMINACIONES:" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM denominaciones") + "\n");
                memo.append("REGISTROS EN MAQUINAS      :" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM maquinastc") + "\n");
                memo.append("REGISTROS EN MAQUINAS LNK  :" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM maquinas_lnk") + "\n");
                memo.append("REGISTROS EN MINUCIPIOS    :" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM municipios") + "\n");
                memo.append("REGISTROS EN RUTAS         :" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM rutas") + "\n");
                memo.append(Global.repeat('-', 80) + "\n");

                cSql_Ln = "SELECT * FROM device_db.dispositivos WHERE serial='" + Global.cid_device + "'";
                String cJsonResult = Global.Rem_Query_Result("device_db", cSql_Ln, 1, "");

                JSONArray mainObject = null;
                try {
                    mainObject = new JSONArray(cJsonResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cJsonResult = null;

                try {
                    JSONObject c = mainObject.getJSONObject(0);

                    String cRem_Emp_id = c.getString("emp_id");
                    String cRem_Db_Name = c.getString("dbname");
                    String cRem_Corre_Act = c.getString("corre_act");
                    String cRem_Clave_Metros = c.getString("clave_metros");
                    String cRem_Clave_Montos = c.getString("clave_montos");

                    cSql_Ln = "";
                    cSql_Ln += "UPDATE dispositivos SET ";
                    cSql_Ln += " emp_id      ='" + cRem_Emp_id + "',";
                    cSql_Ln += " clave_metros='" + cRem_Clave_Metros + "',";
                    cSql_Ln += " clave_montos='" + cRem_Clave_Montos + "',";
                    cSql_Ln += " dbname      ='" + cRem_Db_Name + "' ";
                    cSql_Ln += "WHERE serial ='" + Global.cid_device + "'";
                    Global.Query_Update(cSql_Ln);

                    String cActual_Corre = Global.Query_Result("SELECT IFNULL(corre_act,0) AS corre_act FROM dispositivos WHERE serial ='" + Global.cid_device + "'", "corre_act");
                    if (cActual_Corre == "") {
                        cActual_Corre = "0";
                    }
                    int iRem_Corre_Act = Integer.parseInt(cRem_Corre_Act);
                    int iActual_Corre = Integer.parseInt(cActual_Corre);

                    if (iActual_Corre < iRem_Corre_Act) {
                        cSql_Ln = "";
                        cSql_Ln += "UPDATE dispositivos SET ";
                        cSql_Ln += " corre_act ='" + cRem_Corre_Act + "' ";
                        cSql_Ln += "WHERE serial ='" + Global.cid_device + "'";
                        Global.Query_Update(cSql_Ln);
                    }
                    cParsString = "device=" + Global.cid_device;
                    cJsonResult = Global.gen_execute_post(Global.SERVER_URL, "/flam/update_last_access.php", cParsString);
                    Global.logLargeString(cJsonResult);

                    Global.Get_Config();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                btn_regr.setEnabled(true);

            }
        });

        btn_regr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();
                db2.close();
                finish();
            }
        });

        btn_json.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String Sql = "SELECT * FROM operacion limit 200";
                //String cValue=Global.Cursor_To_MySql_Sentense(Sql,"operacion",2);
                String aValue = Global.getJsonResults2(Sql, "operacion");
                memo.setText(aValue);
            }
        });


    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    protected void execute_post() {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;
        String parameters = "table_no=0";
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            //url = new URL("http://192.168.2.82/flam/get_all_data.php");
            url = new URL(myurl + "/flam/get_all_data.php");
            lurl.setText("CONECTADO A:[" + myurl + "]");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");

            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(parameters);
            request.flush();
            request.close();
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                //sb.append(line + "\n");
                sb.append(line);
            }
            // Response from server after login process will be stored in response variable.
            response = sb.toString();
            // You can perform UI operations here
            //Toast.makeText(getApplicationContext(), "Message from Server: \n" + response, Toast.LENGTH_LONG).show();
            memo.setText(response);
            isr.close();
            reader.close();

        } catch (IOException e) {
            // Error
        }

    }

    public String Lite_Query_Result(String cSqlCmd) {
        Cursor mCount = db2.rawQuery(cSqlCmd, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        String sCount = mCount.getString(0);
        mCount.close();
        return sCount;
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet

            //Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            //Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    public void Create_Sql_Tables2() {
        String cSql_Ln = "";

        if (ock_rene.isChecked() == true) {

            // -----------------------------------CLIENTES------------------------------------------------------//
            cSql_Ln = "DROP TABLE IF EXISTS clientes;";
            db2.execSQL(cSql_Ln);

            // -----------------------------------DENOMINACIONES------------------------------------------------//
            cSql_Ln = "DROP TABLE IF EXISTS denominaciones;";
            db2.execSQL(cSql_Ln);

            // ---------------------------------EMPRESA----------------------------------------------------------------//
            cSql_Ln = "DROP TABLE IF EXISTS empresas;";
            db2.execSQL(cSql_Ln);

            // ---------------------------------MAQUINASTC------------------------------------------------------//
            cSql_Ln = "DROP TABLE IF EXISTS maquinastc;";
            db2.execSQL(cSql_Ln);

            // ---------------------------------MAQUINAS LNK----------------------------------------------------//
            cSql_Ln = "DROP TABLE IF EXISTS maquinas_lnk;";
            db2.execSQL(cSql_Ln);

            // ---------------------------------MUNICIPIOS  ----------------------------------------------------//
            cSql_Ln = "DROP TABLE IF EXISTS municipios;";
            db2.execSQL(cSql_Ln);

            // --------------------------------- RUTAS ---------------------------------------------------------//
            cSql_Ln = "DROP TABLE IF EXISTS rutas;";
            db2.execSQL(cSql_Ln);
        }

        if (ock_delc.isChecked() == true) {
            cSql_Ln = "DROP TABLE IF EXISTS operacion;";
            db2.execSQL(cSql_Ln);
        }

        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS clientes (";
        cSql_Ln += "cte_id INTEGER NOT NULL,";
        cSql_Ln += "mun_id INTEGER NULL DEFAULT 1,";
        cSql_Ln += "rut_id INTEGER NULL DEFAULT 1,";
        cSql_Ln += "cte_nombre_loc CHAR(80) NULL DEFAULT '',";
        cSql_Ln += "cte_nombre_com CHAR(80) NULL DEFAULT '',";
        cSql_Ln += "cte_telefono1 CHAR(25) NULL DEFAULT '',";
        cSql_Ln += "cte_telefono2 CHAR(25) NULL DEFAULT '',";
        cSql_Ln += "cte_Fax CHAR(25) NULL DEFAULT '',";
        cSql_Ln += "cte_contacto_nom CHAR(35) NULL DEFAULT '',";
        cSql_Ln += "cte_contacto_movil CHAR(25) NULL DEFAULT '',";
        cSql_Ln += "cte_contacto_movil_bbpin CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "cte_contacto_movil_email CHAR(120) NULL DEFAULT '',";
        cSql_Ln += "cte_email CHAR(120) NULL DEFAULT '',";
        cSql_Ln += "cte_webpage VARCHAR(130) NULL DEFAULT '',";
        cSql_Ln += "cte_notas TEXT NULL,";
        cSql_Ln += "cte_inactivo INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_direccion TEXT NULL, ";
        cSql_Ln += "cte_pag_impm INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_pag_jcj INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_pag_spac INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_poc_ret NUMERIC(12, 2) NULL DEFAULT 0,";
        cSql_Ln += "cte_fecha_alta DATETIME NULL ,";
        cSql_Ln += "cte_fecha_modif DATETIME NULL ,";
        cSql_Ln += "cte_emp_id INTEGER NULL DEFAULT 0,";
        cSql_Ln += "u_usuario_alta CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "cte_unico_emp INTEGER NULL DEFAULT 0,";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "CONSTRAINT clientes_PRIMARY PRIMARY KEY (cte_id))";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_inactivo ON clientes('cte_inactivo')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_nombre_loc ON clientes('cte_nombre_loc')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS mun_id ON clientes('mun_id')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_nombre_com ON clientes('cte_nombre_com')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS rut_id ON clientes('rut_id')";
        db2.execSQL(cSql_Ln);
        // --------------------------------FIN CLIENTES-----------------------------------------------------//

        // -----------------------------------DENOMINACIONES------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS denominaciones (";
        cSql_Ln += "den_id INTEGER NOT NULL,";
        cSql_Ln += "den_descripcion CHAR(30) NULL DEFAULT '',";
        cSql_Ln += "den_inactiva INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_fact_e INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_fact_s INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_valor NUMERIC(12, 2) NULL DEFAULT 0,";
        cSql_Ln += "den_fecha_alta DATETIME NULL ,";
        cSql_Ln += "den_fecha_modif DATETIME NULL ,";
        cSql_Ln += "u_usuario_alta CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "CONSTRAINT denominaciones_PRIMARY PRIMARY KEY (den_id))";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS den_inactiva ON denominaciones('den_inactiva')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS den_descripcion ON denominaciones('den_descripcion')";
        db2.execSQL(cSql_Ln);
        // -------------------------------FIN DENOMINACIONES------------------------------------------------//

        // ---------------------------------EMPRESA----------------------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS empresas ( ";
        cSql_Ln += "emp_id INTEGER NOT NULL,";
        cSql_Ln += "emp_descripcion CHAR(85) NULL DEFAULT '',";
        cSql_Ln += "emp_ruc CHAR(85) NULL DEFAULT '',";
        cSql_Ln += "emp_dv CHAR(10) NULL DEFAULT '',";
        cSql_Ln += "emp_carpeta_reportes VARCHAR (120) NULL DEFAULT '',";
        cSql_Ln += "emp_telefono1 CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "emp_telefono2 CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "emp_fax CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "emp_direccion TEXT NULL,";
        cSql_Ln += "emp_apartado TEXT NULL,";
        cSql_Ln += "emp_email CHAR(100) NULL DEFAULT '',";
        cSql_Ln += "separa_mil CHAR(1) NULL DEFAULT ',',";
        cSql_Ln += "separa_dec CHAR(1) NULL DEFAULT '.',";
        cSql_Ln += "emp_imagen BLOB NULL,";
        cSql_Ln += "emp_imagen_path VARCHAR(120) NULL DEFAULT '',";
        cSql_Ln += "emp_inactivo INTEGER NULL DEFAULT 0,";
        cSql_Ln += "emp_cargo_jcj NUMERIC(12, 2) NULL DEFAULT 0,";
        cSql_Ln += "emp_cargo_spac NUMERIC(12, 2) NULL DEFAULT 0,";
        cSql_Ln += "emp_fecha_alta DATETIME NULL ,";
        cSql_Ln += "emp_fecha_modif DATETIME NULL ,";
        cSql_Ln += "u_usuario_alta CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "CONSTRAINT empresas_PRIMARY PRIMARY KEY (emp_id))";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS emp_inactivo ON empresas ('emp_inactivo')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS emp_descripcion ON empresas ('emp_descripcion')";
        db2.execSQL(cSql_Ln);
        // ----------------------------------FIN EMPRESAS---------------------------------------------------//

        // ---------------------------------MAQUINASTC------------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS maquinastc (";
        cSql_Ln += "maqtc_id INTEGER NOT NULL,";
        cSql_Ln += "maqtc_cod CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "maqtc_modelo CHAR(60) NULL DEFAULT '',";
        cSql_Ln += "maqtc_chapa CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "maqtc_inactivo INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_metros INTEGER NULL DEFAULT 1,";
        cSql_Ln += "emp_id INTEGER NULL DEFAULT 1,";
        cSql_Ln += "maqtc_denom_e INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_tipomaq INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_denom_s INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m1e_act INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m1e_ant INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m1s_ant INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m2e_act INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m2e_ant INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m2s_act INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m2s_ant INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m1s_act INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_fecha_alta DATETIME NULL ,";
        cSql_Ln += "maqtc_fecha_modif DATETIME NULL ,";
        cSql_Ln += "u_usuario_alta CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "CONSTRAINT maquinastc_PRIMARY PRIMARY KEY (maqtc_id))";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS maqtc_cod ON maquinastc('maqtc_cod')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS maqtc_chapa ON maquinastc('maqtc_chapa')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS maqtc_inactivo ON maquinastc('maqtc_inactivo')";
        db2.execSQL(cSql_Ln);
        // -----------------------------FIN MAQUINASTC------------------------------------------------------//

        // ---------------------------------MAQUINAS LNK----------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS maquinas_lnk (";
        cSql_Ln += "MaqLnk_Id INTEGER NOT NULL,";
        cSql_Ln += "emp_id INTEGER NULL DEFAULT 1,";
        cSql_Ln += "cte_id INTEGER NULL DEFAULT 1,";
        cSql_Ln += "maqtc_id INTEGER NULL DEFAULT 1,";
        cSql_Ln += "MaqLnk_fecha_alta DATETIME NULL ,";
        cSql_Ln += "MaqLnk_fecha_modif DATETIME NULL ,";
        cSql_Ln += "CONSTRAINT maquinas_lnk_PRIMARY PRIMARY KEY (MaqLnk_Id))";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS emp_id ON maquinas_lnk('emp_id')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS maqtc_id ON maquinas_lnk('maqtc_id')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_id ON maquinas_lnk('cte_id')";
        db2.execSQL(cSql_Ln);
        // -----------------------------FIN MAQUINAS LNK----------------------------------------------------//

        // ---------------------------------MUNICIPIOS  ----------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS municipios (";
        cSql_Ln += "mun_id INTEGER NOT NULL,";
        cSql_Ln += "mun_inactivo INTEGER NULL DEFAULT 0,";
        cSql_Ln += "mun_nombre CHAR(80) NULL DEFAULT '',";
        cSql_Ln += "mun_impuesto NUMERIC(12, 2) NULL DEFAULT 0,";
        cSql_Ln += "mun_notas TEXT,";
        cSql_Ln += "mun_fecha_alta DATETIME NULL ,";
        cSql_Ln += "mun_fecha_modif DATETIME NULL ,";
        cSql_Ln += "u_usuario_alta CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "CONSTRAINT municipios_PRIMARY PRIMARY KEY (mun_id))";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS mun_nombre ON municipios ('mun_nombre')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS mun_inactivo ON municipios ('mun_inactivo')";
        db2.execSQL(cSql_Ln);
        // -----------------------------FIN MUNICIPIOS  ----------------------------------------------------//

        // --------------------------------- RUTAS ---------------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS rutas (";
        cSql_Ln += "rut_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,";
        cSql_Ln += "rut_inactivo INT(1) NULL DEFAULT '0',";
        cSql_Ln += "rut_nombre CHAR(80) NULL DEFAULT '',";
        cSql_Ln += "rut_notas LONGTEXT NULL,";
        cSql_Ln += "rut_fecha_alta DATETIME NULL DEFAULT '2010-01-01 00:00:00',";
        cSql_Ln += "rut_fecha_modif DATETIME NULL DEFAULT '2010-01-01 00:00:00',";
        cSql_Ln += "u_usuario_alta VARCHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif VARCHAR(20) NULL DEFAULT 'ANONIMO')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS rut_id ON rutas ('rut_id')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS rut_inactivo ON rutas ('rut_inactivo')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS rut_nombre ON rutas ('rut_nombre')";
        db2.execSQL(cSql_Ln);
        // -------------------------------FIN RUTAS --------------------------------------------------------//

        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS operacion (";
        cSql_Ln += "id_op  INTEGER NOT NULL,";
        cSql_Ln += "MaqLnk_Id  INTEGER NOT NULL DEFAULT 0,";
        cSql_Ln += "cte_id INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_nombre_loc  CHAR(60) NOT NULL DEFAULT '',";
        cSql_Ln += "cte_nombre_com  CHAR(60) NOT NULL DEFAULT '',";
        cSql_Ln += "cte_pag_jcj  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_pag_spac  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_pag_impm  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_denom_e  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_denom_s  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_valore  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "den_valors  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "den_fact_e  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_fact_s  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_tipomaq  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_cporc_Loc  NUMERIC(6, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_serie  INTEGER NULL DEFAULT 1,";
        cSql_Ln += "op_chapa  CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "op_fecha  DATETIME NULL,";
        cSql_Ln += "op_nodoc  CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "op_modelo  CHAR(60) NULL DEFAULT '',";
        cSql_Ln += "op_e_pantalla  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_ea_metroan  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_ea_metroac  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_ea_met  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_sa_metroan  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_sa_metroac  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_sa_met  INTEGER NOT NULL DEFAULT 0,";
        cSql_Ln += "op_eb_metroan  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_eb_metroac  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_eb_met  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_sb_metroan  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_sb_metroac  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_sb_met  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_s_pantalla  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_cal_colect  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_colect  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_impmunic  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_impjcj  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_timbres  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_spac  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_tec  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_dev  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_otros  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_cred  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_cal_cred  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_sub  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_itbm  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_tot  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_brutoloc  NUMERIC(6, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_brutoemp  NUMERIC(6, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_netoloc  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_netoemp  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_observ  TEXT,";
        cSql_Ln += "op_num_sem  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_fecha_alta  DATETIME NULL,";
        cSql_Ln += "op_fecha_modif  DATETIME NULL,";
        cSql_Ln += "u_usuario_alta  CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif  CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "op_emp_id  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "id_device VARCHAR(30) NOT NULL DEFAULT 'MANUAL',";
        cSql_Ln += "op_semanas INTEGER NULL DEFAULT 1,";
        cSql_Ln += "op_image_name CHAR(80) NULL DEFAULT NULL,";
        cSql_Ln += "CONSTRAINT  operacion_PRIMARY PRIMARY KEY(id_op))";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_id ON operacion ('cte_id')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS id_device ON operacion ('id_device')";
        db2.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS MaqLnk_Id ON operacion ('MaqLnk_Id')";
        db2.execSQL(cSql_Ln);
    }

}
