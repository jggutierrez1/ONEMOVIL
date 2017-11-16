package flamingo.onemovil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class capt_data extends AppCompatActivity {


    private Button btn_save_capt, btn_canc_capt;
    private EditText ea_act, ea_ant, ea_dif;
    private EditText eb_act, eb_ant, eb_dif;
    private EditText sa_act, sa_ant, sa_dif;
    private EditText sb_act, sb_ant, sb_dif;
    private EditText tot_cole, tot_cred;
    private TextView ltot_cole, ltot_cred;
    private TextView lab_cte,lab_cha;

    private SQLiteDatabase db4;
    private Cursor data;
    private String cId_emp, cId_Cte, cId_Maq;
    private String cSqlLn = "";

    private String op_chapa, op_modelo, cte_nombre_loc, cte_nombre_com,
            maqtc_denom_e, maqtc_denom_s;
    private double op_cal_colect, op_cal_cred, op_cporc_Loc, Op_tot_impjcj, Op_tot_impmunic, den_valore, den_valors;
    private int op_emp_id, den_fact_e, den_fact_s, maqtc_tipomaq, Op_ea_metroac, Op_ea_metroan, Op_sa_metroac, Op_sa_metroan;
    private boolean bFoundMach;
    private int cte_pag_jcj, cte_pag_spac, cte_pag_impm, Denom_Ent_Fac, Denom_Sal_Fac;
    private double fDenom_Ent_Val, fDenom_Sal_Val, Porc_Loc;

    //private double ftot_cole, ftot_prem;
    private int iMetroA_EntDif, iMetroB_EntDif, iMetroA_SalDif, iMetroB_SalDif;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capt_data);

        this.btn_save_capt = (Button) findViewById(R.id.obtn_save_capt);
        this.btn_canc_capt = (Button) findViewById(R.id.obtn_canc_capt);

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

        Intent i = getIntent();
        this.cId_emp = i.getStringExtra("EMP_ID");
        this.cId_Cte = i.getStringExtra("CTE_ID");
        this.cId_Maq = i.getStringExtra("MAQ_ID");

        String databasePath = getDatabasePath("one2009.db").getPath();
        this.db4 = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);

        this.bFoundMach = this.Buscar_Maquina(cId_emp, cId_Cte, cId_Maq);

        this.MaquinaValid(true);

        this.ltot_cole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Factor de Conversión [" + Denom_Ent_Fac + "/ $" +fDenom_Ent_Val + "]", Toast.LENGTH_SHORT).show();
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

        this.ea_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ea_act.getText().toString()=="" )
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
                if (eb_act.getText().toString()=="" )
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
                if (sa_act.getText().toString()=="" )
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
                if (sb_act.getText().toString()=="" )
                    sb_act.setText("0");
            }
        });

        btn_save_capt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });

        btn_canc_capt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                i.putExtra("CTE_ID", "");
                setResult(RESULT_CANCELED, i);

                db4.close();
                finish();
            }
        });

    }

    private void Calc_Dif_Ent(boolean A) {
        int iea_act, iea_ant;
        int ieb_act, ieb_ant;

        iea_act = Integer.valueOf(ea_act.getText().toString()).intValue();
        iea_ant = Integer.valueOf(ea_ant.getText().toString()).intValue();

        ieb_act = Integer.valueOf(eb_act.getText().toString()).intValue();
        ieb_ant = Integer.valueOf(eb_ant.getText().toString()).intValue();

        if (A == true){
            iMetroA_EntDif = (iea_act - iea_ant);
            ea_dif.setText(String.valueOf(iMetroA_EntDif).toString());
        }
        else{
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

        if (A == true){
            iMetroA_SalDif = (isa_act - isa_ant);
            sa_dif.setText(String.valueOf(iMetroA_SalDif).toString());
        }
        else{
            iMetroB_SalDif = (isb_act - isb_ant);
            sb_dif.setText(String.valueOf(iMetroB_SalDif).toString());
        }
    }

    private void Calc_Tot(int iForce) {
        double iTot = 0.00;
        double ipFac_e = 0.00;
        double ftot_cole = 0.00;
        DecimalFormat REAL_FORMATTER = new DecimalFormat("##########0.00");

        ipFac_e =(this.Denom_Ent_Fac == 0 ? 1 : this.Denom_Ent_Fac);

        iTot = (this.iMetroA_EntDif + this.iMetroB_EntDif) / ipFac_e;

        if (iForce == 1) {
            this.tot_cole.setText(REAL_FORMATTER.format(iTot));
            this.Calc_Sub_Tot();
        } else {
            ftot_cole =  Double.valueOf(tot_cole.getText().toString()).doubleValue();
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
        DecimalFormat REAL_FORMATTER = new DecimalFormat("##########0.00");

        ipFac_s =(this.Denom_Sal_Fac == 0 ? 1 : this.Denom_Sal_Fac);

        iTot = ((this.iMetroA_SalDif + this.iMetroB_SalDif) / ipFac_s) * (this.fDenom_Sal_Val == 0 ? 1 : this.fDenom_Sal_Val);

        if (iForce == 1) {
            this.tot_cred.setText(REAL_FORMATTER.format(iTot));
            this.Calc_Sub_Tot();
        } else {
            ftot_prem = Double.valueOf(tot_cred.getText().toString()).doubleValue();
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

        this.tot_cole.setText("0.00");
        this.tot_cole.setEnabled(false);
        this.tot_cred.setText("0.00");
        this.tot_cred.setEnabled(false);
    }

    private boolean Buscar_Maquina(String pId_emp, String pId_Cte, String pId_Maq) {
        cSqlLn = "";
        cSqlLn = cSqlLn + "SELECT ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_id, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_cod, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_modelo, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_chapa, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_inactivo, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_metros, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_denom_e, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_tipomaq, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_denom_s, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_m1e_act, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_m1e_ant, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_m1s_ant, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_m2e_act, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_m2e_ant, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_m2s_act, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_m2s_ant, ";
        cSqlLn = cSqlLn + "  maquinastc.maqtc_m1s_act, ";
        cSqlLn = cSqlLn + "  maquinas_lnk.MaqLnk_Id, ";
        cSqlLn = cSqlLn + "  clientes.cte_id, ";
        cSqlLn = cSqlLn + "  clientes.cte_nombre_loc, ";
        cSqlLn = cSqlLn + "  clientes.cte_nombre_com, ";
        cSqlLn = cSqlLn + "  clientes.cte_inactivo, ";
        cSqlLn = cSqlLn + "  clientes.cte_pag_jcj, ";
        cSqlLn = cSqlLn + "  clientes.cte_pag_impm, ";
        cSqlLn = cSqlLn + "  clientes.cte_pag_spac, ";
        cSqlLn = cSqlLn + "  clientes.cte_poc_ret, ";
        cSqlLn = cSqlLn + "  clientes.mun_id, ";
        cSqlLn = cSqlLn + "  clientes.rut_id, ";
        cSqlLn = cSqlLn + "  rutas.rut_nombre, ";
        cSqlLn = cSqlLn + "  empresas.emp_id, ";
        cSqlLn = cSqlLn + "  empresas.emp_descripcion, ";
        cSqlLn = cSqlLn + "  empresas.emp_inactivo, ";
        cSqlLn = cSqlLn + "  empresas.emp_cargo_jcj, ";
        cSqlLn = cSqlLn + "  empresas.emp_cargo_spac, ";
        cSqlLn = cSqlLn + "  municipios.mun_nombre, ";
        cSqlLn = cSqlLn + "  municipios.mun_impuesto, ";
        cSqlLn = cSqlLn + "  a.den_valor AS den_valore , ";
        cSqlLn = cSqlLn + "  b.den_valor AS den_valors , ";
        cSqlLn = cSqlLn + "  a.den_fact_e , ";
        cSqlLn = cSqlLn + "  b.den_fact_s  ";
        cSqlLn = cSqlLn + "FROM maquinastc ";
        cSqlLn = cSqlLn + "  INNER JOIN maquinas_lnk ON (maquinastc.maqtc_id = maquinas_lnk.maqtc_id) ";
        cSqlLn = cSqlLn + "  LEFT JOIN clientes  ON (maquinas_lnk.cte_id = clientes.cte_id) ";
        cSqlLn = cSqlLn + "  LEFT JOIN empresas   ON (maquinas_lnk.emp_id = empresas.emp_id) ";
        cSqlLn = cSqlLn + "  LEFT JOIN municipios ON (clientes.mun_id = municipios.mun_id) ";
        cSqlLn = cSqlLn + "  LEFT JOIN rutas      ON (clientes.rut_id = rutas.rut_id) ";
        cSqlLn = cSqlLn + "  LEFT JOIN denominaciones a ON (maquinastc.maqtc_denom_e = a.den_id) ";
        cSqlLn = cSqlLn + "  LEFT JOIN denominaciones b ON (maquinastc.maqtc_denom_s = b.den_id) ";
        cSqlLn = cSqlLn + "WHERE maqtc_tipomaq = 1 ";
        cSqlLn = cSqlLn + "AND TRIM(maquinastc.maqtc_chapa) ='" + pId_Maq + "' ";
        cSqlLn = cSqlLn + "AND empresas.emp_id ='" + pId_emp + "' ";

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

        this.lab_cte.setText("CLIENTE: " + data.getString(data.getColumnIndex("cte_nombre_loc")));
        this.lab_cha.setText("CHAPA  : " + data.getString(data.getColumnIndex("maqtc_chapa")));

        op_chapa = data.getString(data.getColumnIndex("maqtc_chapa"));

        op_emp_id = data.getInt(data.getColumnIndex("emp_id"));

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
                    this.ea_act.setText("0.00");
                    this.ea_ant.setText(data.getString(data.getColumnIndex("maqtc_m1e_act")));
                    // Salidas A
                    this.sa_act.setEnabled(true);
                    this.sa_act.setText("0.00");
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

}
