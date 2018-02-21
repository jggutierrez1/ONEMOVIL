package flamingo.onemovil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class borrar_colectas extends AppCompatActivity {

    private Button obtn_confirm_si, obtn_confirm_no;
    private TextView oconfirm_mensage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar_colectas);


        this.obtn_confirm_si = (Button) findViewById(R.id.btn_confirm_si);
        this.obtn_confirm_no = (Button) findViewById(R.id.btn_confirm_no);

        this.oconfirm_mensage = (TextView) findViewById(R.id.confirm_mensage);
        this.oconfirm_mensage.setText(Global.DialogConfirmText);

        obtn_confirm_si.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = getIntent();
                setResult(RESULT_OK, i);

                finish();
            }
        });

        obtn_confirm_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);

                finish();
            }
        });
    }
}
