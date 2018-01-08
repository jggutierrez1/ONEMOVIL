package flamingo.onemovil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private SQLiteDatabase db5;
    private Cursor data;
    private String cid_device2 = "";

    private int itotfin_bruto, itotfin_prem, itotfin_devo, itotfin_otros, itotfin_total, itotfin_gast, itotfin_neto, itotfin_notas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capt_fin);

        cid_device2 = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

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

        this.Clear_Screen();

        String databasePath = getDatabasePath("one2009.db").getPath();
        this.db5 = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
        this.Find_Values();

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
                Intent i = getIntent();
                setResult(RESULT_OK, i);
                db5.close();
                finish();
            }
        });

        btn_totfin_canc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                Global.cCte_Id = "";
                setResult(RESULT_CANCELED, i);

                db5.close();
                finish();
            }
        });

    }

    private void Clear_Screen() {
        this.totfin_bruto.setText("0");
        this.totfin_bruto.setEnabled(false);
        this.totfin_prem.setText("0");
        this.totfin_prem.setEnabled(false);
        this.totfin_devo.setText("0");
        this.totfin_otros.setText("0");
        this.totfin_total.setText("0");
        this.totfin_total.setEnabled(false);
        this.totfin_gast.setText("0");
        this.totfin_neto.setText("0");
        this.totfin_neto.setEnabled(false);
        this.totfin_notas.setText("");
    }

    private void Find_Values() {
        String cSql_Ln = "";

        cSql_Ln = "";
        cSql_Ln += "SELECT ";
        cSql_Ln += "    SUM(op_tot_colect) AS op_tot_colect, ";
        cSql_Ln += "    SUM(op_tot_cred) AS op_tot_cred ";
        cSql_Ln += "FROM operacion ";
        cSql_Ln += "WHERE id_device='" + cid_device2 + "' ";
        cSql_Ln += "AND   cte_id   ='" + Global.cCte_Id + "' ";

        Log.e("SQL", cSql_Ln);
        data = db5.rawQuery(cSql_Ln, null);
        data.moveToFirst();

        if ((data == null) || (data.getCount() == 0)) {
            this.totfin_bruto.setText('0');
            this.totfin_prem.setText('0');
        } else {
            this.totfin_bruto.setText(data.getString(data.getColumnIndex("op_tot_colect")));
            this.totfin_prem.setText(data.getString(data.getColumnIndex("op_tot_cred")));
        }
    }

}
