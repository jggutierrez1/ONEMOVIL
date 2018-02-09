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
    Button btn_entar, btn_cance;
    EditText User, Pass;
    TextView DeviceId, ologin_emp, Intentos;
    ImageView oImagen;
    int counter = 3;
    public final static int REQUEST_MEN = 1;
    private final static int REQUEST_INSTALL_VALIED = 12;

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

        btn_entar = (Button) findViewById(R.id.obtn_login_entrar);
        btn_cance = (Button) findViewById(R.id.obtn_login_cancel);

        User = (EditText) findViewById(R.id.oLogin_User);
        Pass = (EditText) findViewById(R.id.oLogin_Pass);

        oImagen = (ImageView) findViewById(R.id.imageView);

        Intentos = (TextView) findViewById(R.id.ologin_intentos);
        Intentos.setVisibility(View.GONE);

        DeviceId = (TextView) findViewById(R.id.login_device);
        DeviceId.setText("ID EQUIPO:" + Global.cid_device.toUpperCase());

        ologin_emp = (TextView) findViewById(R.id.login_emp);

        if (Global.checkDataBase() == false) {
            this.create_database();
            Intent Check_Install_Screen = new Intent(getApplicationContext(), install_check.class);
            startActivityForResult(Check_Install_Screen, REQUEST_INSTALL_VALIED);
        } else {
            this.create_database();
            //Global.clear_tables_device();
            if (Global.check_device() <= 0) {
                Intent Check_Install_Screen = new Intent(getApplicationContext(), install_check.class);
                startActivityForResult(Check_Install_Screen, REQUEST_INSTALL_VALIED);
            }
        }
        Global.Get_Config();
        Global.Create_Sql_Tables_Emp();
        String Etiq_Emp = Global.Query_Result("SELECT IFNULL(emp_descripcion,'') AS emp_descripcion FROM empresas WHERE emp_id='" + Global.cEmp_Id + "'", "emp_descripcion");
        ologin_emp.setText("[" + Etiq_Emp + "]");

        btn_entar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.getText().toString().equals("admin") && Pass.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...", Toast.LENGTH_SHORT).show();

                    Intent Int_menu = new Intent(getApplicationContext(), menu.class);
                    startActivityForResult(Int_menu, REQUEST_MEN);

                } else {
                    Toast.makeText(getApplicationContext(), "Error de credenciales", Toast.LENGTH_SHORT).show();

                    Intentos.setVisibility(View.VISIBLE);
                    Intentos.setBackgroundColor(Color.RED);
                    counter--;
                    Intentos.setText(Integer.toString(counter));

                    if (counter == 0) {
                        btn_entar.setEnabled(false);
                    }
                }
            }
        });

        btn_cance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        oImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.clear_tables_device();
                finish();
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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