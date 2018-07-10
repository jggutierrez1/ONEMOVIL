package flamingo.onemovil;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class install_check extends AppCompatActivity {
    private Button obtn_check_ok, obtn_check_exit;
    private EditText ocheck_code;
    private TextView InternetStatus, DeviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_check);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        obtn_check_ok = (Button) findViewById(R.id.btn_check_ok);
        obtn_check_exit = (Button) findViewById(R.id.btn_check_exit);
        ocheck_code = (EditText) findViewById(R.id.check_code);
        InternetStatus = (TextView) findViewById(R.id.check_InternetStatus);
        DeviceId = (TextView) findViewById(R.id.install_device);
        DeviceId.setText("ID EQUIPO:" + Global.cid_device.toUpperCase());

        //this.create_database();
        //Global.Create_Sql_Tables(true, true);
        //Global.check_tables_device(true);

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
        } else {
            cInternetAnswer = "SIN CONEXION A INTERNET.";
        }
        InternetStatus.setText(cInternetAnswer);
        if (bInternetConnected == true) {
            Global.Check_Ip_Disp();
            InternetStatus.setTextColor(Color.parseColor("#009900"));
            getWindow().getDecorView().getRootView().setBackgroundColor(Color.parseColor("#ffffff"));
            obtn_check_ok.setEnabled(true);
        } else {
            getWindow().getDecorView().getRootView().setBackgroundColor(Color.parseColor("#ffc2b3"));
            InternetStatus.setTextColor(Color.parseColor("#ff0000"));
            obtn_check_ok.setEnabled(false);
        }

        obtn_check_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String cClave = ocheck_code.getText().toString();
                String cParsString = "";
                String cResult = "";

                if (cClave == "") {
                    Toast.makeText(getApplicationContext(), "DEBE INGRESAR EL CODIGO DE ACTIVACION.", Toast.LENGTH_LONG).show();
                    Global.ValidateOk = false;
                    return;
                } else {
                    cParsString = "device=" + Global.cid_device + "&clavee=" + cClave;
                    cResult = Global.gen_execute_post(Global.SERVER_URL, "/flam/register_device.php", cParsString);
                    if (cResult == "") {
                        cResult = "0";
                    }
                    int iResult = Integer.valueOf(cResult);
                    if (iResult == 1) {
                        Global.ValidateOk = true;

                        //Global.clear_tables_device();
                        Global.active_device(cClave);

                        finish();
                        return;
                    } else {
                        Global.ValidateOk = false;
                        Toast.makeText(getApplicationContext(), "CLAVE INVALIDA.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
        });

        obtn_check_exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Global.ValidateOk = false;
                finishAffinity();
                System.exit(0);
            }
        });

    }

    private void create_database() {
        String myPath = Global.oActual_Context.getDatabasePath("one2009.db").getPath();
        Global.oGen_Db = openOrCreateDatabase(myPath, Context.MODE_PRIVATE, null);
    }
}
