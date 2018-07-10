package flamingo.onemovil;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class get_password extends AppCompatActivity {
    private Button obtn_acept, obtn_cancel;
    private EditText oPassw_value;
    private TextView oPassw_device, opassw_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_password);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        this.opassw_title = (TextView) findViewById(R.id.passw_title);
        this.obtn_acept = (Button) findViewById(R.id.btn_acept);
        this.obtn_cancel = (Button) findViewById(R.id.btn_cancel);

        this.oPassw_value = (EditText) findViewById(R.id.Passw_value);
        this.oPassw_device = (TextView) findViewById(R.id.Passw_device);

        this.oPassw_device.setText("ID EQUIPO:" + Global.cid_device.toUpperCase());
        this.opassw_title.setText(Global.PasswordTitle);

        obtn_acept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = getIntent();
                int iPassword = -1;
                String cPassword = "";
                cPassword = oPassw_value.getText().toString();
                if (!cPassword.isEmpty()) {

                    try {
                        iPassword = Integer.parseInt(cPassword);
                        System.out.println(cPassword + ": is a number");

                        i.putExtra("PASSWORD", iPassword);
                        setResult(RESULT_OK, i);

                        Global.stringResult = oPassw_value.getText().toString();
                        finish();

                    } catch (NumberFormatException e) {
                        System.out.println(cPassword + ": is not a number");
                        Toast.makeText(Global.oActual_Context, "EL VALOR DEBE SER UN NUMERO", Toast.LENGTH_SHORT).show();
                        oPassw_value.setText("");
                    }

                }
            }
        });

        obtn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = getIntent();
                i.putExtra("PASSWORD", -1);
                setResult(RESULT_CANCELED, i);

                Global.stringResult = "";
                finish();
            }
        });
    }
}
