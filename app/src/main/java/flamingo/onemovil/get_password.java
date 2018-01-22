package flamingo.onemovil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class get_password extends AppCompatActivity {
    private Button obtn_acept, obtn_cancel;
    private EditText oPassw_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_password);

        this.obtn_acept = (Button) findViewById(R.id.btn_acept);
        this.obtn_cancel = (Button) findViewById(R.id.btn_cancel);

        this.oPassw_value = (EditText) findViewById(R.id.Passw_value);

        obtn_acept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = getIntent();
                i.putExtra("PASSWORD", Integer.parseInt(oPassw_value.getText().toString()));
                setResult(RESULT_OK, i);

                Global.stringResult = oPassw_value.getText().toString();
                finish();
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
