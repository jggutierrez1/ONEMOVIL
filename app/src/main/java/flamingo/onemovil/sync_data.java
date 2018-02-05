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

}
