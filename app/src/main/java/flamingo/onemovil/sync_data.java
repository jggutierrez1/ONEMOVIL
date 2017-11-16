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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class sync_data extends AppCompatActivity {

    private Button btn_proc, btn_regr;
    private SQLiteDatabase db2;
    private EditText memo;
    private TextView lurl;
    private String myurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);
        getWindow().getDecorView().getRootView().setBackgroundColor(Color.rgb(255, 230, 230));

        btn_proc = (Button) findViewById(R.id.obtn_proc);
        btn_regr = (Button) findViewById(R.id.obtn_regr);
        memo = (EditText) findViewById(R.id.omemo);
        lurl = (TextView) findViewById(R.id.olurl);
        //myurl = "http://192.168.2.82";
        myurl = "http://190.140.40.242";
        lurl.setText("CONECTADO A:[" + myurl + "]");

        String databasePath = getDatabasePath("one2009.db").getPath();
        db2 = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);

        btn_proc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
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

                execute_post();

                String script = memo.getText().toString();
                memo.setText("");

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
                Toast.makeText(getApplicationContext(), "PROCESO FINALIZADO.", Toast.LENGTH_LONG).show();
                memo.setText("");
                memo.append(repeat('-', 30));
                memo.append(CenterString("RESUMEN DE SINCRONIZACION", 30));
                memo.append(repChar('-', 30));
                memo.append("REGISTROS EN EMPRESA       :" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM empresas") + "\n");
                memo.append("REGISTROS EN CLIENTES      :" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM clientes") + "\n");
                memo.append("REGISTROS EN DENOMINACIONES:" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM denominaciones") + "\n");
                memo.append("REGISTROS EN MAQUINAS      :" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM maquinastc") + "\n");
                memo.append("REGISTROS EN MAQUINAS LNK  :" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM maquinas_lnk") + "\n");
                memo.append("REGISTROS EN MINUCIPIOS    :" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM municipios") + "\n");
                memo.append("REGISTROS EN RUTAS         :" + Lite_Query_Result("SELECT COUNT(*) AS CNT FROM rutas") + "\n");
                memo.append(repeat('-', 30));

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

    public String repChar(char c, int reps) {
        String adder = Character.toString(c);
        String result = "";
        while (reps > 0) {
            if (reps % 2 == 1) {
                result += adder;
            }
            adder += adder;
            reps /= 2;
        }
        return result;
    }

    public String CenterString(String s, int size) {
        return center(s, size, ' ');
    }

    public String center(String s, int size, char pad) {
        if (s == null || size <= s.length())
            return s;

        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while (sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
    }

    public String repeat(char what, int howmany) {
        char[] chars = new char[howmany];
        Arrays.fill(chars, what);
        return new String(chars);
    }

    public String Lite_Query_Result(String cSqlCmd) {
        Cursor mCount = db2.rawQuery(cSqlCmd, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        String sCount = mCount.getString(0);
        mCount.close();
        return sCount;
    }
}
