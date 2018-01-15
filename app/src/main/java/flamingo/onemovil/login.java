package flamingo.onemovil;

import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity {
    Button btn_entar, btn_cance;
    EditText User, Pass;

    TextView Intentos;
    int counter = 3;
    public final static int REQUEST_MEN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        Intentos = (TextView) findViewById(R.id.ologin_intentos);
        Intentos.setVisibility(View.GONE);

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
                finish();
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
                    break;
            }
        }

    }
}
