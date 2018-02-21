package flamingo.onemovil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity {
    Button obtn_entar, obtn_cance;
    EditText oUser, oPass;
    TextView oDeviceId, oIntentos, oLogin_device;
    ImageView oImagen;
    int counter = 3;
    public final static int REQUEST_MEN = 1;
    private final static int REQUEST_INSTALL_VALIED = 12;
    public final static int REQUEST_GET_PASS1 = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Global.cid_device = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Global.cid_device = Global.cid_device.toUpperCase();
        Global.PACKAGE_NAME = getApplicationContext().getPackageName();

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        Global.Init_Vars();
        Global.Chech_App_Folders();

        obtn_entar = (Button) findViewById(R.id.obtn_login_entrar);
        obtn_cance = (Button) findViewById(R.id.obtn_login_cancel);

        oUser = (EditText) findViewById(R.id.oLogin_User);
        oPass = (EditText) findViewById(R.id.oLogin_Pass);

        oImagen = (ImageView) findViewById(R.id.imageView);

        oLogin_device = (TextView) findViewById(R.id.login_device);

        oIntentos = (TextView) findViewById(R.id.ologin_intentos);
        oIntentos.setVisibility(View.GONE);

        oDeviceId = (TextView) findViewById(R.id.login_device);
        oDeviceId.setText("ID EQUIPO:" + Global.cid_device.toUpperCase());


        if (Global.checkDataBase() == false) {
            this.create_database();
            Global.Create_Sql_Tables(false, false);

            Intent Check_Install_Screen = new Intent(getApplicationContext(), install_check.class);
            startActivityForResult(Check_Install_Screen, REQUEST_INSTALL_VALIED);
        } else {
            this.create_database();
            Global.Create_Sql_Tables(false, false);

            //Global.clear_tables_device();
            if (Global.check_device() <= 0) {
                Intent Check_Install_Screen = new Intent(getApplicationContext(), install_check.class);
                startActivityForResult(Check_Install_Screen, REQUEST_INSTALL_VALIED);
            }
        }
        //Global.Create_Sql_Tables_Emp();
        Global.Get_Config();

        obtn_entar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oUser.getText().toString().equals("admin") && oPass.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...", Toast.LENGTH_SHORT).show();

                    Intent Int_menu = new Intent(getApplicationContext(), menu.class);
                    startActivityForResult(Int_menu, REQUEST_MEN);

                } else {
                    Toast.makeText(getApplicationContext(), "Error de credenciales", Toast.LENGTH_SHORT).show();

                    oIntentos.setVisibility(View.VISIBLE);
                    oIntentos.setBackgroundColor(Color.RED);
                    counter--;
                    oIntentos.setText(Integer.toString(counter));

                    if (counter == 0) {
                        obtn_entar.setEnabled(false);
                    }
                }
            }
        });

        obtn_cance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        oLogin_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Int_GetPass = new Intent(getApplicationContext(), get_password.class);
                startActivityForResult(Int_GetPass, REQUEST_GET_PASS1);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GET_PASS1) {
            switch (resultCode) {
                case RESULT_OK:
                    int ipass = data.getIntExtra("PASSWORD", -1);
                    if (ipass == 123) {
                        Global.Create_Sql_Tables(true, true);
                        finish();
                        System.exit(0);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }

        if (requestCode == REQUEST_MEN) {
            //Se procesa la devolución
            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(this, "Aceptó las condiciones ", Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    finish();
                    System.exit(0);
                    break;
            }
        }

        if (requestCode == REQUEST_INSTALL_VALIED) {
        }
        switch (resultCode) {
            case RESULT_OK:
                Toast.makeText(this, "Se valido la clave", Toast.LENGTH_SHORT).show();

                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "CLAVE INVALIDA", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void create_database() {
        String myPath = Global.oActual_Context.getDatabasePath("one2009.db").getPath();
        Global.oGen_Db = openOrCreateDatabase(myPath, Context.MODE_PRIVATE, null);
    }
}