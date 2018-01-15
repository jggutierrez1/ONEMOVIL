package flamingo.onemovil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

public class capt_fin extends AppCompatActivity {
    private Button obtn_print_maq, obtn_totfin_hide, obtn_totfin_save, obtn_totfin_canc;
    private EditText ototfin_bruto, ototfin_prem, ototfin_devo, ototfin_otros, ototfin_total, ototfin_gast, ototfin_neto, ototfin_notas;
    private TextView olab_cte;
    private SQLiteDatabase oDb5;
    private Cursor oData;

    private int itotfin_bruto, itotfin_prem, itotfin_devo, itotfin_otros, itotfin_total, itotfin_gast, itotfin_neto, itotfin_notas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capt_fin);

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        this.olab_cte = (TextView) findViewById(R.id.lab_cte);

        this.obtn_totfin_hide = (Button) findViewById(R.id.btn_totfin_hide);
        this.obtn_print_maq = (Button) findViewById(R.id.btn_print_maq);
        this.obtn_totfin_save = (Button) findViewById(R.id.btn_totfin_save);
        this.obtn_totfin_canc = (Button) findViewById(R.id.btn_totfin_canc);

        this.ototfin_bruto = (EditText) findViewById(R.id.totfin_bruto);
        this.ototfin_prem = (EditText) findViewById(R.id.totfin_prem);
        this.ototfin_devo = (EditText) findViewById(R.id.totfin_devo);
        this.ototfin_otros = (EditText) findViewById(R.id.totfin_otros);
        this.ototfin_total = (EditText) findViewById(R.id.totfin_total);
        this.ototfin_gast = (EditText) findViewById(R.id.totfin_gast);
        this.ototfin_neto = (EditText) findViewById(R.id.totfin_neto);
        this.ototfin_notas = (EditText) findViewById(R.id.totfin_notas);

        this.olab_cte.setText("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De + "]");

        this.Clear_Screen();

        String databasePath = getDatabasePath("one2009.db").getPath();
        this.oDb5 = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
        this.Find_Values();

        Valid_Data();

        Calc_Tot1();
        Calc_Tot2();

        this.ototfin_devo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ototfin_devo.getText().toString() == "")
                    ototfin_devo.setText("0.00");
            }
        });

        this.ototfin_otros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ototfin_otros.getText().toString() == "")
                    ototfin_otros.setText("0.00");
            }
        });

        this.ototfin_gast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ototfin_gast.getText().toString() == "")
                    ototfin_gast.setText("0.00");
            }
        });

        this.ototfin_notas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ototfin_notas.getText().toString() == "")
                    ototfin_notas.setHint("Sin comentarios");
            }
        });

        this.ototfin_devo.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Valid_Data();

                Calc_Tot1();
                Calc_Tot2();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.ototfin_otros.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Valid_Data();

                Calc_Tot1();
                Calc_Tot2();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.ototfin_gast.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Valid_Data();

                Calc_Tot1();
                Calc_Tot2();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.obtn_print_maq.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Global.ExportDB();
                Global.iPrn_Data = 1;
                Intent Int_PrnMaqScreen = new Intent(getApplicationContext(), print_data.class);
                startActivity(Int_PrnMaqScreen);
            }
        });

        this.obtn_totfin_hide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        });

        this.obtn_totfin_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Factor de Conversión [" + Denom_Ent_Fac + "/ $" + fDenom_Ent_Val + "]", Toast.LENGTH_SHORT).show();
                Global.ExportDB();
                Intent i = getIntent();
                setResult(RESULT_OK, i);
                oDb5.close();
                finish();
            }
        });

        this.obtn_totfin_canc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                Global.cCte_Id = "";
                setResult(RESULT_CANCELED, i);

                oDb5.close();
                finish();
            }
        });

    }

    private void Clear_Screen() {
        this.ototfin_bruto.setText("0.00");
        this.ototfin_bruto.setEnabled(false);
        this.ototfin_prem.setText("0.00");
        this.ototfin_prem.setEnabled(false);
        this.ototfin_devo.setText("0.00");
        this.ototfin_otros.setText("0.00");
        this.ototfin_total.setText("0.00");
        this.ototfin_total.setEnabled(false);
        this.ototfin_gast.setText("0.00");
        this.ototfin_neto.setText("0.00");
        this.ototfin_neto.setEnabled(false);
        this.ototfin_notas.setText("");
    }

    private void Find_Values() {
        String cSql_Ln = "";

        cSql_Ln = "";
        cSql_Ln += "SELECT ";
        cSql_Ln += "    SUM(op_tot_colect) AS op_tot_colect, ";
        cSql_Ln += "    SUM(op_tot_cred) AS op_tot_cred ";
        cSql_Ln += "FROM operacion ";
        cSql_Ln += "WHERE id_device='" + Global.cid_device + "' ";
        cSql_Ln += "AND   cte_id   ='" + Global.cCte_Id + "' ";

        Log.e("SQL", cSql_Ln);
        oData = oDb5.rawQuery(cSql_Ln, null);
        oData.moveToFirst();

        if ((oData == null) || (oData.getCount() == 0)) {
            this.ototfin_bruto.setText("0.00");
            this.ototfin_prem.setText("0.00");
        } else {

            Double ftotfin_bruto = oData.getDouble(oData.getColumnIndex("op_tot_colect"));
            Double ftotfin_prem = oData.getDouble(oData.getColumnIndex("op_tot_cred"));

            this.ototfin_bruto.setText(Double.valueOf(ftotfin_bruto).toString());
            this.ototfin_prem.setText(Double.valueOf(ftotfin_prem).toString());
        }
    }

    private void Valid_Data() {
        if (ototfin_bruto.getText().toString().length() == 0) {
            ototfin_bruto.setError(null);
            ototfin_bruto.setText("0.00");
        }

        if (ototfin_prem.getText().toString().length() == 0) {
            ototfin_prem.setError(null);
            ototfin_prem.setText("0.00");
        }

        if (ototfin_devo.getText().toString().length() == 0) {
            ototfin_devo.setError(null);
            ototfin_devo.setText("0.00");
        }

        if (ototfin_otros.getText().toString().length() == 0) {
            ototfin_otros.setError(null);
            ototfin_otros.setText("0.00");
        }

        if (ototfin_total.getText().toString().length() == 0) {
            ototfin_total.setError(null);
            ototfin_total.setText("0.00");
        }

        if (ototfin_gast.getText().toString().length() == 0) {
            ototfin_gast.setError(null);
            ototfin_gast.setText("0.00");
        }

        if (ototfin_neto.getText().toString().length() == 0) {
            ototfin_neto.setError(null);
            ototfin_neto.setText("0.00");
        }

        if (ototfin_notas.getText().toString().length() == 0) {
            ototfin_notas.setError(null);
            ototfin_notas.setHint("Sin Comentarios");
        }
    }

    private void Calc_Tot1() {
        Double dtotfin_bruto = 0.00;
        Double dtotfin_prem = 0.00;
        Double dtotfin_devo = 0.00;
        Double dtotfin_otros = 0.00;
        Double dtotfin_total = 0.00;

        dtotfin_bruto = Double.parseDouble(this.ototfin_bruto.getText().toString().replace(',', '.'));
        dtotfin_prem = Double.parseDouble(this.ototfin_prem.getText().toString().replace(',', '.'));
        dtotfin_devo = Double.parseDouble(this.ototfin_devo.getText().toString().replace(',', '.'));
        dtotfin_otros = Double.parseDouble(this.ototfin_otros.getText().toString().replace(',', '.'));
        dtotfin_total = (dtotfin_bruto - dtotfin_prem) + dtotfin_devo - dtotfin_otros;

        this.ototfin_total.setText(Double.valueOf(dtotfin_total).toString());
    }

    private void Calc_Tot2() {
        Double dtotfin_total = 0.00;
        Double dtotfin_gast = 0.00;
        Double dtotfin_neto = 0.00;

        dtotfin_total = Double.parseDouble(this.ototfin_total.getText().toString().replace(',', '.'));
        dtotfin_gast = Double.parseDouble(this.ototfin_gast.getText().toString().replace(',', '.'));
        dtotfin_neto = dtotfin_total - dtotfin_gast;

        this.ototfin_neto.setText(Double.valueOf(dtotfin_neto).toString());
    }

}
