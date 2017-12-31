package flamingo.onemovil;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class capt_fin extends AppCompatActivity {
    private Button btn_totfin_hide, btn_totfin_save, btn_totfin_canc;
    private EditText totfin_bruto, totfin_prem, totfin_devo, totfin_otros, totfin_total, totfin_gast, totfin_neto, totfin_notas;
    private TextView lab_cte, lab_cha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capt_fin);

        this.lab_cte = (TextView) findViewById(R.id.olab_cte);
        this.lab_cha = (TextView) findViewById(R.id.olab_cha);

        this.btn_totfin_hide = (Button) findViewById(R.id.obtn_totfin_hide);
        this.btn_totfin_save = (Button) findViewById(R.id.obtn_totfin_save);
        this.btn_totfin_canc = (Button) findViewById(R.id.obtn_totfin_canc);

        this.totfin_bruto = (EditText) findViewById(R.id.ototfin_bruto);
        this.totfin_prem = (EditText) findViewById(R.id.ototfin_prem);
        this.totfin_devo = (EditText) findViewById(R.id.ototfin_devo);
        this.totfin_otros = (EditText) findViewById(R.id.ototfin_otros);
        this.totfin_total = (EditText) findViewById(R.id.ototfin_total);
        this.totfin_gast = (EditText) findViewById(R.id.ototfin_gast);
        this.totfin_neto = (EditText) findViewById(R.id.ototfin_neto);
        this.totfin_notas = (EditText) findViewById(R.id.ototfin_notas);


        btn_totfin_hide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        });

        this.btn_totfin_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Factor de Conversión [" + Denom_Ent_Fac + "/ $" + fDenom_Ent_Val + "]", Toast.LENGTH_SHORT).show();
            }
        });

        btn_totfin_canc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                i.putExtra("CTE_ID", "");
                setResult(RESULT_CANCELED, i);

                //db4.close();
                finish();
            }
        });

    }
}
