package flamingo.onemovil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.hardware.Camera;
import android.net.Uri;

import android.os.Environment;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.provider.Settings;

import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;

import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class capt_data extends AppCompatActivity {

    public final static int REQUEST_CAMERA = 1;
    private static final String TAG = capt_data.class.getSimpleName();
    private Button btn_hide_capt, btn_save_capt, btn_canc_capt;
    private EditText ea_act, ea_ant, ea_dif;
    private EditText eb_act, eb_ant, eb_dif;
    private EditText sa_act, sa_ant, sa_dif;
    private EditText sb_act, sb_ant, sb_dif;
    private EditText tot_cole, tot_cred;
    private EditText semanas;
    private TextView ltot_cole, ltot_cred;
    private TextView lab_cte, lab_cha;
    private TextView leb_act, leb_ant, leb_dif;
    private TextView lsb_act, lsb_ant, lsb_dif;
    private LinearLayout layoutb_eact, layoutb_eant, layoutb_edif, layoutb_sact, layoutb_sant, layoutb_sdif;
    private Space Spaceb_esep, Spaceb_ssep;
    private SQLiteDatabase db4;
    private Cursor data;
    //private String cId_emp, cId_Cte, cId_Maq;
    private String cSqlLn = "";
    private String op_chapa, op_modelo, op_serie, maqlnk_id, cte_nombre_loc, cte_nombre_com,
            maqtc_denom_e, maqtc_denom_s;
    private double op_colect, op_cred, op_cal_colect, op_cal_cred, op_cporc_Loc, Op_tot_impjcj, Op_tot_impmunic, den_valore, den_valors;
    private int op_emp_id, den_fact_e, den_fact_s, maqtc_tipomaq, Op_ea_metroac, Op_ea_metroan, Op_sa_metroac, Op_sa_metroan;
    private boolean bFoundMach;
    private int cte_pag_jcj, cte_pag_spac, cte_pag_impm, Denom_Ent_Fac, Denom_Sal_Fac;
    private double fDenom_Ent_Val, fDenom_Sal_Val, Porc_Loc;
    //private double ftot_cole, ftot_prem;
    private int iMetroA_EntDif, iMetroB_EntDif, iMetroA_SalDif, iMetroB_SalDif;
    private String cid_device2 = "";
    private int iSemanas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capt_data);
        cid_device2 = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        this.btn_hide_capt = (Button) findViewById(R.id.obtn_hide_capt);
        this.btn_save_capt = (Button) findViewById(R.id.obtn_save_capt);
        this.btn_canc_capt = (Button) findViewById(R.id.obtn_canc_capt);

        /*-------------------ETIQUETAS------------------------*/
        this.leb_act = (TextView) findViewById(R.id.oleb_act);
        this.leb_ant = (TextView) findViewById(R.id.oleb_ant);
        this.leb_dif = (TextView) findViewById(R.id.oleb_dif);

        this.lsb_act = (TextView) findViewById(R.id.olsb_act);
        this.lsb_ant = (TextView) findViewById(R.id.olsb_ant);
        this.lsb_dif = (TextView) findViewById(R.id.olsb_dif);
        /*-------------------------------------------------------*/

       /*-------------------LAYOUTS------------------------*/
        this.layoutb_eact = (LinearLayout) findViewById(R.id.olayoutb_eact);
        this.layoutb_eant = (LinearLayout) findViewById(R.id.olayoutb_eant);
        this.layoutb_edif = (LinearLayout) findViewById(R.id.olayoutb_edif);
        this.Spaceb_esep = (Space) findViewById(R.id.oSpaceb_esep);

        this.layoutb_sact = (LinearLayout) findViewById(R.id.olayoutb_sact);
        this.layoutb_sant = (LinearLayout) findViewById(R.id.olayoutb_sant);
        this.layoutb_sdif = (LinearLayout) findViewById(R.id.olayoutb_sdif);
        this.Spaceb_ssep = (Space) findViewById(R.id.oSpaceb_ssep);

        /*-------------------------------------------------------*/

        this.semanas = (EditText) findViewById(R.id.osemanas);
        this.semanas.setSelectAllOnFocus(true);

        this.ea_act = (EditText) findViewById(R.id.oea_act);
        this.ea_act.setSelectAllOnFocus(true);
        this.ea_ant = (EditText) findViewById(R.id.oea_ant);
        this.ea_ant.setSelectAllOnFocus(true);
        this.ea_dif = (EditText) findViewById(R.id.oea_dif);

        this.eb_act = (EditText) findViewById(R.id.oeb_act);
        this.eb_act.setSelectAllOnFocus(true);
        this.eb_ant = (EditText) findViewById(R.id.oeb_ant);
        this.eb_ant.setSelectAllOnFocus(true);
        this.eb_dif = (EditText) findViewById(R.id.oeb_dif);

        this.sa_act = (EditText) findViewById(R.id.osa_act);
        this.sa_act.setSelectAllOnFocus(true);
        this.sa_ant = (EditText) findViewById(R.id.osa_ant);
        this.sa_ant.setSelectAllOnFocus(true);
        this.sa_dif = (EditText) findViewById(R.id.osa_dif);

        this.sb_act = (EditText) findViewById(R.id.osb_act);
        this.sb_act.setSelectAllOnFocus(true);
        this.sb_ant = (EditText) findViewById(R.id.osb_ant);
        this.sb_ant.setSelectAllOnFocus(true);
        this.sb_dif = (EditText) findViewById(R.id.osb_dif);

        this.tot_cole = (EditText) findViewById(R.id.otot_cole);
        this.tot_cred = (EditText) findViewById(R.id.otot_cred);

        this.lab_cte = (TextView) findViewById(R.id.olab_cte);
        this.lab_cha = (TextView) findViewById(R.id.olab_cha);

        this.ltot_cole = (TextView) findViewById(R.id.oltot_cole);
        this.ltot_cred = (TextView) findViewById(R.id.oltot_cred);

        this.Clear_Screen();

        String databasePath = getDatabasePath("one2009.db").getPath();
        this.db4 = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);

        this.bFoundMach = this.Buscar_Maquina(Global.cEmp_Id, Global.cCte_Id, Global.cMaq_Id);

        this.MaquinaValid(true);

        this.ltot_cole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Factor de Conversión [" + Denom_Ent_Fac + "/ $" + fDenom_Ent_Val + "]", Toast.LENGTH_SHORT).show();
            }
        });

        this.ltot_cred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Factor de Conversión [" + Denom_Sal_Fac + "/ $" + fDenom_Sal_Val + "]", Toast.LENGTH_SHORT).show();
            }
        });


        this.ea_act.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Dif_Ent(true);
                Calc_Tot(1);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.semanas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (semanas.getText().toString() == "")
                    semanas.setText("1");
            }
        });

        this.ea_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ea_act.getText().toString() == "")
                    ea_act.setText("0");
            }
        });

        this.eb_act.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Dif_Ent(true);
                Calc_Tot(1);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.eb_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eb_act.getText().toString() == "")
                    eb_act.setText("0");
            }
        });

        this.sa_act.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Dif_Sal(true);
                Tot_Cred(1);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.sa_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sa_act.getText().toString() == "")
                    sa_act.setText("0");
            }
        });

        this.sb_act.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Dif_Sal(true);
                Tot_Cred(1);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.sb_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sb_act.getText().toString() == "")
                    sb_act.setText("0");
            }
        });

        btn_hide_capt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        });

        btn_save_capt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                android.text.format.DateFormat cNow = new android.text.format.DateFormat();
                cNow.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());

                String cNow2 = getNow();

                String cSql_Ln = "";

                cSql_Ln = "";
                cSql_Ln += "DELETE FROM operacion WHERE id_device='" + cid_device2 + "' and  op_chapa='" + op_chapa + "';";
                Log.e("SQL", cSql_Ln);
                //System.out.print(cSql_Ln);
                db4.execSQL(cSql_Ln);
                iSemanas = Integer.parseInt(semanas.getText().toString());

                double Op_tot_impjcj2 = 0.00;
                double Op_tot_impmunic2 = 0.00;

                if (cte_pag_jcj == 0) {
                    Op_tot_impjcj2 = (Op_tot_impjcj * iSemanas);
                }

                if (cte_pag_impm == 0) {
                    Op_tot_impmunic2 = (Op_tot_impmunic * iSemanas);
                }

                cSql_Ln = "";
                cSql_Ln += "INSERT INTO operacion ( ";
                cSql_Ln += "cte_nombre_loc, cte_nombre_com, op_cporc_Loc,";
                cSql_Ln += "cte_pag_jcj, cte_pag_spac, cte_pag_impm,";
                cSql_Ln += "maqtc_denom_e, maqtc_denom_s,";
                cSql_Ln += "den_valore, den_valors,";
                cSql_Ln += "den_fact_e, den_fact_s,";
                cSql_Ln += "MaqLnk_Id,maqtc_tipomaq,op_serie,op_chapa,op_modelo,";
                cSql_Ln += "op_fecha,";
                cSql_Ln += "op_e_pantalla,";
                cSql_Ln += "op_ea_metroan,op_ea_metroac,op_ea_met,";
                cSql_Ln += "op_sa_metroan,op_sa_metroac,op_sa_met,";
                cSql_Ln += "op_eb_metroan,op_eb_metroac,op_eb_met,";
                cSql_Ln += "op_sb_metroan,op_sb_metroac,op_sb_met,";
                cSql_Ln += "op_s_pantalla,";
                cSql_Ln += "op_cal_colect,op_tot_colect,";
                cSql_Ln += "op_tot_cred,op_cal_cred,";
                cSql_Ln += "op_fecha_alta,op_fecha_modif,";
                cSql_Ln += "op_tot_impmunic,op_tot_impjcj,";
                cSql_Ln += "op_emp_id,id_device) VALUES (";
                cSql_Ln += "'" + cte_nombre_loc + "',";
                cSql_Ln += "'" + cte_nombre_com + "',";
                cSql_Ln += "'" + op_cporc_Loc + "',";
                cSql_Ln += "'" + cte_pag_jcj + "',";
                cSql_Ln += "'" + cte_pag_spac + "',";
                cSql_Ln += "'" + cte_pag_impm + "',";
                cSql_Ln += "'" + maqtc_denom_e + "',";
                cSql_Ln += "'" + maqtc_denom_s + "',";
                cSql_Ln += "'" + den_valore + "',";
                cSql_Ln += "'" + den_valors + "',";
                cSql_Ln += "'" + Denom_Ent_Fac + "',";
                cSql_Ln += "'" + Denom_Sal_Fac + "',";
                cSql_Ln += "'" + maqlnk_id + "',";
                cSql_Ln += "'" + maqtc_tipomaq + "',";
                cSql_Ln += "'" + op_serie + "',";
                cSql_Ln += "'" + op_chapa + "',";
                cSql_Ln += "'" + op_modelo + "',";
                cSql_Ln += "'" + cNow2 + "',";
                cSql_Ln += "'0',";

                cSql_Ln += "'" + String.format("%d", Integer.parseInt(ea_ant.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%d", Integer.parseInt(ea_act.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%d", Integer.parseInt(ea_dif.getText().toString())) + "',";

                cSql_Ln += "'" + String.format("%d", Integer.parseInt(sa_ant.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%d", Integer.parseInt(sa_act.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%d", Integer.parseInt(sa_dif.getText().toString())) + "',";

                cSql_Ln += "'" + String.format("%d", Integer.parseInt(eb_ant.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%d", Integer.parseInt(eb_act.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%d", Integer.parseInt(eb_dif.getText().toString())) + "',";

                cSql_Ln += "'" + String.format("%d", Integer.parseInt(sb_ant.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%d", Integer.parseInt(sb_act.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%d", Integer.parseInt(eb_dif.getText().toString())) + "',";
                cSql_Ln += "'0',";

                cSql_Ln += "'" + op_cal_colect + "',";
                cSql_Ln += "'" + String.format("%d", Integer.parseInt(tot_cole.getText().toString())) + "',";

                cSql_Ln += "'" + op_cal_cred + "',";
                cSql_Ln += "'" + String.format("%d", Integer.parseInt(tot_cred.getText().toString())) + "',";
                cSql_Ln += "'" + cNow2 + "',";
                cSql_Ln += "'" + cNow2 + "',";
                cSql_Ln += "'" + Op_tot_impjcj2 + "',";
                cSql_Ln += "'" + Op_tot_impmunic2 + "',";

                cSql_Ln += "'" + op_emp_id + "',";
                cSql_Ln += "'" + cid_device2 + "');";
                Log.e("SQL", cSql_Ln);
                //System.out.print(cSql_Ln);
                db4.execSQL(cSql_Ln);
                db4.close();

                Clear_Screen();

                Intent i = getIntent();
                setResult(RESULT_OK, i);

                finish();
            }
        });

        btn_canc_capt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);

                db4.close();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        {
            if (requestCode == REQUEST_CAMERA) {
                switch (resultCode) {
                    case RESULT_OK:
                        Toast.makeText(this, "PROCESADO", Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(this, "ERROR INESPERADO", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        }
    }

    private void Calc_Dif_Ent(boolean A) {
        int iea_act, iea_ant;
        int ieb_act, ieb_ant;

        iea_act = Integer.valueOf(ea_act.getText().toString()).intValue();
        iea_ant = Integer.valueOf(ea_ant.getText().toString()).intValue();

        ieb_act = Integer.valueOf(eb_act.getText().toString()).intValue();
        ieb_ant = Integer.valueOf(eb_ant.getText().toString()).intValue();

        if (A == true) {
            iMetroA_EntDif = (iea_act - iea_ant);
            ea_dif.setText(String.valueOf(iMetroA_EntDif).toString());
        } else {
            iMetroB_EntDif = (ieb_act - ieb_ant);
            eb_dif.setText(String.valueOf(iMetroB_EntDif).toString());
        }
    }

    private void Calc_Dif_Sal(boolean A) {
        int isa_act, isa_ant;
        int isb_act, isb_ant;

        isa_act = Integer.valueOf(sa_act.getText().toString()).intValue();
        isa_ant = Integer.valueOf(sa_ant.getText().toString()).intValue();

        isb_act = Integer.valueOf(sb_act.getText().toString()).intValue();
        isb_ant = Integer.valueOf(sb_ant.getText().toString()).intValue();

        if (A == true) {
            iMetroA_SalDif = (isa_act - isa_ant);
            sa_dif.setText(String.valueOf(iMetroA_SalDif).toString());
        } else {
            iMetroB_SalDif = (isb_act - isb_ant);
            sb_dif.setText(String.valueOf(iMetroB_SalDif).toString());
        }
    }

    private void Calc_Tot(int iForce) {
        double iTot = 0.00;
        double ipFac_e = 0.00;
        double ftot_cole = 0.00;
        DecimalFormat REAL_FORMATTER = new DecimalFormat("#############0");

        ipFac_e = (this.Denom_Ent_Fac == 0 ? 1 : this.Denom_Ent_Fac);

        iTot = (this.iMetroA_EntDif + this.iMetroB_EntDif) / ipFac_e;

        if (iForce == 1) {
            this.tot_cole.setText(REAL_FORMATTER.format(iTot));
            this.Calc_Sub_Tot();
        } else {
            ftot_cole = Double.valueOf(tot_cole.getText().toString()).doubleValue();
            op_colect = ftot_cole;
            if (ftot_cole <= 0.00) {
                this.tot_cole.setText(REAL_FORMATTER.format(iTot));
                this.Calc_Sub_Tot();
            }
        }
        op_cal_colect = iTot;
    }

    private void Tot_Cred(int iForce) {
        double iTot = 0.00;
        double ipFac_s = 0.00;
        double ftot_prem = 0.00;
        DecimalFormat REAL_FORMATTER = new DecimalFormat("#############0");

        ipFac_s = (this.Denom_Sal_Fac == 0 ? 1 : this.Denom_Sal_Fac);

        iTot = ((this.iMetroA_SalDif + this.iMetroB_SalDif) / ipFac_s) * (this.fDenom_Sal_Val == 0 ? 1 : this.fDenom_Sal_Val);

        if (iForce == 1) {
            this.tot_cred.setText(REAL_FORMATTER.format(iTot));
            this.Calc_Sub_Tot();
        } else {
            ftot_prem = Double.valueOf(tot_cred.getText().toString()).doubleValue();
            op_cred = ftot_prem;
            if (ftot_prem <= 0) {
                this.tot_cred.setText(REAL_FORMATTER.format(iTot));
                this.Calc_Sub_Tot();
            }
        }
        op_cal_cred = iTot;
    }

    private void Calc_Sub_Tot() {
    }

    private void Clear_Screen() {
        this.ea_act.setText("0");
        this.ea_ant.setText("0");
        this.ea_ant.setEnabled(false);
        this.ea_dif.setText("0");
        this.ea_dif.setEnabled(false);

        this.eb_act.setText("0");
        this.eb_ant.setText("0");
        this.eb_ant.setEnabled(false);
        this.eb_dif.setText("0");
        this.eb_dif.setEnabled(false);

        this.sa_act.setText("0");
        this.sa_ant.setText("0");
        this.sa_ant.setEnabled(false);
        this.sa_dif.setText("0");
        this.sa_dif.setEnabled(false);

        this.sb_act.setText("0");
        this.sb_ant.setText("0");
        this.sb_ant.setEnabled(false);
        this.sb_dif.setText("0");
        this.sb_dif.setEnabled(false);

        this.tot_cole.setText("0");
        this.tot_cole.setEnabled(false);
        this.tot_cred.setText("0");
        this.tot_cred.setEnabled(false);
    }

    private boolean Buscar_Maquina(String pId_emp, String pId_Cte, String pId_Maq) {
        cSqlLn = "";
        cSqlLn += "SELECT ";
        cSqlLn += "  maquinastc.maqtc_id, ";
        cSqlLn += "  maquinastc.maqtc_cod, ";
        cSqlLn += "  maquinastc.maqtc_modelo, ";
        cSqlLn += "  maquinastc.maqtc_chapa, ";
        cSqlLn += "  maquinastc.maqtc_inactivo, ";
        cSqlLn += "  maquinastc.maqtc_metros, ";
        cSqlLn += "  maquinastc.maqtc_denom_e, ";
        cSqlLn += "  maquinastc.maqtc_tipomaq, ";
        cSqlLn += "  maquinastc.maqtc_denom_s, ";
        cSqlLn += "  maquinastc.maqtc_m1e_act, ";
        cSqlLn += "  maquinastc.maqtc_m1e_ant, ";
        cSqlLn += "  maquinastc.maqtc_m1s_ant, ";
        cSqlLn += "  maquinastc.maqtc_m2e_act, ";
        cSqlLn += "  maquinastc.maqtc_m2e_ant, ";
        cSqlLn += "  maquinastc.maqtc_m2s_act, ";
        cSqlLn += "  maquinastc.maqtc_m2s_ant, ";
        cSqlLn += "  maquinastc.maqtc_m1s_act, ";
        cSqlLn += "  maquinas_lnk.MaqLnk_Id, ";
        cSqlLn += "  clientes.cte_id, ";
        cSqlLn += "  clientes.cte_nombre_loc, ";
        cSqlLn += "  clientes.cte_nombre_com, ";
        cSqlLn += "  clientes.cte_inactivo, ";
        cSqlLn += "  clientes.cte_pag_jcj, ";
        cSqlLn += "  clientes.cte_pag_impm, ";
        cSqlLn += "  clientes.cte_pag_spac, ";
        cSqlLn += "  clientes.cte_poc_ret, ";
        cSqlLn += "  clientes.mun_id, ";
        cSqlLn += "  clientes.rut_id, ";
        cSqlLn += "  rutas.rut_nombre, ";
        cSqlLn += "  empresas.emp_id, ";
        cSqlLn += "  empresas.emp_descripcion, ";
        cSqlLn += "  empresas.emp_inactivo, ";
        cSqlLn += "  empresas.emp_cargo_jcj, ";
        cSqlLn += "  empresas.emp_cargo_spac, ";
        cSqlLn += "  municipios.mun_nombre, ";
        cSqlLn += "  municipios.mun_impuesto, ";
        cSqlLn += "  a.den_valor AS den_valore , ";
        cSqlLn += "  b.den_valor AS den_valors , ";
        cSqlLn += "  a.den_fact_e , ";
        cSqlLn += "  b.den_fact_s  ";
        cSqlLn += "FROM maquinastc ";
        cSqlLn += "  INNER JOIN maquinas_lnk ON (maquinastc.maqtc_id = maquinas_lnk.maqtc_id) ";
        cSqlLn += "  LEFT JOIN clientes   ON (maquinas_lnk.cte_id = clientes.cte_id) ";
        cSqlLn += "  LEFT JOIN empresas   ON (maquinas_lnk.emp_id = empresas.emp_id) ";
        cSqlLn += "  LEFT JOIN municipios ON (clientes.mun_id = municipios.mun_id) ";
        cSqlLn += "  LEFT JOIN rutas      ON (clientes.rut_id = rutas.rut_id) ";
        cSqlLn += "  LEFT JOIN denominaciones a ON (maquinastc.maqtc_denom_e = a.den_id) ";
        cSqlLn += "  LEFT JOIN denominaciones b ON (maquinastc.maqtc_denom_s = b.den_id) ";
        cSqlLn += "WHERE maqtc_tipomaq = 1 ";
        cSqlLn += "AND TRIM(maquinastc.maqtc_chapa) ='" + pId_Maq + "' ";
        cSqlLn += "AND empresas.emp_id ='" + pId_emp + "' ";
        Log.d("SQL", cSqlLn);
        //System.out.print(cSqlLn);

        data = db4.rawQuery(cSqlLn, null);

        if ((data == null) || (data.getCount() == 0)) {
            Toast.makeText(getApplicationContext(), "El código de máquina no existe o no tiene un cliente asignado", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private void MaquinaValid(Boolean bIsNew) {
        data.moveToFirst();
        op_serie = "0";

        //this.lab_cte.setText("CLIENTE: " + data.getString(data.getColumnIndex("cte_nombre_loc")));
        //this.lab_cha.setText("CHAPA  : " + data.getString(data.getColumnIndex("maqtc_chapa")));

        this.lab_cte.setText("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De + "]");
        this.lab_cha.setText("CHAPA  : [" + Global.cMaq_Id + "]/[" + Global.cMaq_De + "]");

        op_chapa = data.getString(data.getColumnIndex("maqtc_chapa"));

        op_emp_id = data.getInt(data.getColumnIndex("emp_id"));
        maqlnk_id = data.getString(data.getColumnIndex("MaqLnk_Id"));

        Denom_Ent_Fac = data.getInt(data.getColumnIndex("den_fact_e"));
        Denom_Sal_Fac = data.getInt(data.getColumnIndex("den_fact_s"));

        fDenom_Ent_Val = data.getDouble(data.getColumnIndex("den_valore"));
        fDenom_Sal_Val = data.getDouble(data.getColumnIndex("den_valors"));

        if (data.getDouble(data.getColumnIndex("cte_poc_ret")) == 0)
            Porc_Loc = 1;
        else
            Porc_Loc = data.getDouble(data.getColumnIndex("cte_poc_ret"));

        op_modelo = data.getString(data.getColumnIndex("maqtc_modelo"));
        cte_nombre_loc = data.getString(data.getColumnIndex("cte_nombre_loc"));
        cte_nombre_com = data.getString(data.getColumnIndex("cte_nombre_com"));
        cte_pag_jcj = data.getInt(data.getColumnIndex("cte_pag_jcj"));
        cte_pag_spac = data.getInt(data.getColumnIndex("cte_pag_spac"));

        cte_pag_impm = data.getInt(data.getColumnIndex("cte_pag_impm"));
        op_cporc_Loc = data.getDouble(data.getColumnIndex("cte_poc_ret"));

        maqtc_denom_e = data.getString(data.getColumnIndex("maqtc_denom_e"));
        maqtc_denom_s = data.getString(data.getColumnIndex("maqtc_denom_s"));
        maqtc_tipomaq = data.getInt(data.getColumnIndex("maqtc_tipomaq"));
        den_fact_e = data.getInt(data.getColumnIndex("den_fact_e"));
        den_fact_s = data.getInt(data.getColumnIndex("den_fact_s"));
        den_valore = data.getDouble(data.getColumnIndex("den_valore"));
        den_valors = data.getDouble(data.getColumnIndex("den_valors"));

        //if (self.otOperaciones.State = dsInsert) {
        if (bIsNew == true) {

            if (cte_pag_jcj == 0)
                Op_tot_impjcj = (data.getDouble(data.getColumnIndex("emp_cargo_jcj")) / 4);

            if (cte_pag_impm == 0)
                Op_tot_impmunic = data.getDouble(data.getColumnIndex("mun_impuesto"));
        }

        switch (data.getInt(data.getColumnIndex("maqtc_metros"))) {
            case 1:
                if (bIsNew == true) {
                    // Entradas A
                    this.ea_act.setEnabled(true);
                    this.ea_act.setText("0");
                    this.ea_ant.setText(data.getString(data.getColumnIndex("maqtc_m1e_act")));
                    // Salidas A
                    this.sa_act.setEnabled(true);
                    this.sa_act.setText("0");
                    this.sa_ant.setText(data.getString(data.getColumnIndex("maqtc_m1s_act")));

                    if (data.getInt(data.getColumnIndex("maqtc_m1e_ant")) == 0)
                        ea_ant.setEnabled(true);
                    else
                        ea_ant.setEnabled(false);

                    if (data.getInt(data.getColumnIndex("maqtc_m1s_ant")) == 0)
                        sa_ant.setEnabled(true);
                    else
                        sa_ant.setEnabled(false);

                } else {
                    // Entradas A
                    this.ea_act.setEnabled(true);
                    this.ea_ant.setEnabled(true);

                    this.ea_act.setText(data.getString(data.getColumnIndex("maqtc_m1e_act")));
                    this.ea_ant.setText(data.getString(data.getColumnIndex("maqtc_m1e_ant")));
                    // Salidas A
                    this.sa_act.setEnabled(true);
                    this.sa_ant.setEnabled(true);

                    this.sa_act.setText(data.getString(data.getColumnIndex("maqtc_m1s_act")));
                    this.sa_ant.setText(data.getString(data.getColumnIndex("maqtc_m1s_ant")));
                }

                // Entradas B
                this.eb_act.setEnabled(false);
                this.eb_ant.setEnabled(false);
                this.eb_act.setText("0");
                this.eb_ant.setText("0");
                this.eb_dif.setText("0");

                // Salidas B
                this.sb_act.setEnabled(false);
                this.sb_ant.setEnabled(false);
                this.sb_act.setText("0");
                this.sb_ant.setText("0");
                this.sb_dif.setText("0");

                // Entradas B
                this.eb_act.setVisibility(View.GONE);
                this.leb_act.setVisibility(View.GONE);

                this.eb_ant.setVisibility(View.GONE);
                this.leb_ant.setVisibility(View.GONE);

                // Salida B
                this.sb_act.setVisibility(View.GONE);
                this.lsb_act.setVisibility(View.GONE);

                this.sb_ant.setVisibility(View.GONE);
                this.lsb_ant.setVisibility(View.GONE);

                // Diferencia B
                this.eb_dif.setVisibility(View.GONE);
                this.leb_dif.setVisibility(View.GONE);

                this.sb_dif.setVisibility(View.GONE);
                this.lsb_dif.setVisibility(View.GONE);

                this.layoutb_eact.setVisibility(View.GONE);
                this.layoutb_eant.setVisibility(View.GONE);
                this.layoutb_edif.setVisibility(View.GONE);
                this.Spaceb_esep.setVisibility(View.GONE);

                this.layoutb_sact.setVisibility(View.GONE);
                this.layoutb_sant.setVisibility(View.GONE);
                this.layoutb_sdif.setVisibility(View.GONE);
                this.Spaceb_ssep.setVisibility(View.GONE);

                break;
            case 2:
                if (bIsNew == true) {
                    // Entradas A
                    this.ea_act.setEnabled(true);
                    this.ea_act.setText("0");
                    this.ea_ant.setText(data.getString(data.getColumnIndex("maqtc_m1e_act")));
                    // Salidas A
                    this.sa_act.setEnabled(true);
                    this.sa_act.setText("0");
                    this.sa_ant.setText(data.getString(data.getColumnIndex("maqtc_m1s_act")));

                    if (data.getInt(data.getColumnIndex("maqtc_m1e_ant")) == 0)
                        this.ea_ant.setEnabled(true);
                    else
                        this.ea_ant.setEnabled(false);

                    if (data.getInt(data.getColumnIndex("maqtc_m1s_ant")) == 0)
                        this.sa_ant.setEnabled(true);
                    else
                        this.sa_ant.setEnabled(false);
                    // --------------------------------------------------------
                    // Entradas B
                    this.eb_act.setEnabled(true);
                    this.eb_act.setText("0");
                    this.eb_ant.setText(data.getString(data.getColumnIndex("maqtc_m2e_act")));
                    // Salidas B

                    this.sb_act.setEnabled(true);
                    this.sb_act.setText("0");
                    this.sb_ant.setText(data.getString(data.getColumnIndex("maqtc_m2s_act")));

                    if (data.getInt(data.getColumnIndex("maqtc_m2e_ant")) == 0)
                        this.eb_ant.setEnabled(true);
                    else
                        this.eb_ant.setEnabled(false);

                    if (data.getInt(data.getColumnIndex("maqtc_m2s_ant")) == 0)
                        this.sb_ant.setEnabled(true);
                    else
                        this.sb_ant.setEnabled(false);
                } else {
                    // Entradas A
                    this.ea_ant.setEnabled(true);
                    this.ea_act.setEnabled(true);
                    // Salidas A
                    this.sa_ant.setEnabled(true);
                    this.sa_act.setEnabled(true);
                    // --------------------------------------------------------
                    // Entradas B
                    this.eb_ant.setEnabled(true);
                    this.eb_act.setEnabled(true);
                    // Salidas B
                    this.sb_ant.setEnabled(true);
                    this.sb_act.setEnabled(true);
                }
                break;
            default:
                this.dialogBox("A las máquinas no se le han sido asiganado la información de metros, favor asignar la información de metros");
                // Entradas A
                this.ea_ant.setEnabled(false);
                this.ea_act.setEnabled(false);
                // Salidas A
                this.sa_ant.setEnabled(false);
                this.sa_act.setEnabled(false);
                // --------------------------------------------------------
                // Entradas B
                this.eb_ant.setEnabled(false);
                this.eb_act.setEnabled(false);
                // Salidas B
                this.sb_ant.setEnabled(false);
                this.sb_act.setEnabled(false);
                break;
        }
    }

    private void dialogBox(String cMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(cMessage);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        alertDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private String getNow() {
        // set the format to sql date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
