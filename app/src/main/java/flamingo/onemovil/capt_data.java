package flamingo.onemovil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.text.InputType;
import android.text.TextWatcher;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.CheckBox;
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

import android.text.format.DateFormat;

public class capt_data extends AppCompatActivity {

    private static final String TAG = capt_data.class.getSimpleName();
    private Button btn_hide_capt, btn_foto_capt, btn_save_capt, btn_cancel_capt, btn_unlock_capt, btn_metd_capt;
    private CheckBox baja_prod;
    private EditText ea_act, ea_ant, ea_dif;
    private EditText eb_act, eb_ant, eb_dif;
    private EditText sa_act, sa_ant, sa_dif;
    private EditText sb_act, sb_ant, sb_dif;
    private EditText tot_cole, tot_cred;
    private EditText oSemanas, tot_notas;
    private TextView ltot_cole, ltot_cred;
    private TextView lab_cte, lab_cha;
    private TextView lea_act, lea_ant, leb_act, leb_ant, lsa_act, lsa_ant, lsb_act, lsb_ant, leb_dif;
    private TextView lsb_dif;
    private TextView capt_device;
    private LinearLayout layoutb_eact, layoutb_eant, layoutb_edif, layoutb_sact, layoutb_sant, layoutb_sdif;
    private Space Spaceb_esep, Spaceb_ssep;
    private SQLiteDatabase db4;
    private Cursor data4;
    private String cSqlLn = "";
    private String op_chapa, op_modelo, op_serie, maqlnk_id, cte_nombre_loc, cte_nombre_com,
            maqtc_denom_e, maqtc_denom_s;
    private double op_colect, op_cred, op_cal_colect, op_cal_cred, op_cporc_Loc, Op_tot_impjcj, Op_tot_impmunic, den_valore, den_valors;
    private int op_emp_id, den_fact_e, den_fact_s, maqtc_tipomaq, op_semanas_imp;
    private double Op_ea_metroac, Op_ea_metroan, Op_sa_metroac, Op_sa_metroan;
    private boolean bFoundMach;
    private int cte_pag_jcj, cte_pag_spac, cte_pag_impm, Denom_Ent_Fac, Denom_Sal_Fac, cte_mod_metro_ant;
    private double fDenom_Ent_Val, fDenom_Sal_Val, Porc_Loc;
    //private double ftot_cole, ftot_prem;
    private double iMetro_EntDif, iMetroA_EntDif, iMetroB_EntDif, iMetro_SalDif, iMetroA_SalDif, iMetroB_SalDif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capt_data);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        cte_mod_metro_ant = 0;

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        Locale.setDefault(new Locale("en", "US"));

        this.btn_hide_capt = (Button) findViewById(R.id.obtn_hide_capt);
        this.btn_foto_capt = (Button) findViewById(R.id.obtn_foto_capt);
        this.btn_save_capt = (Button) findViewById(R.id.obtn_save_capt);
        this.btn_cancel_capt = (Button) findViewById(R.id.obtn_cancel_capt);
        this.btn_unlock_capt = (Button) findViewById(R.id.obtn_unlock_capt);
        this.btn_metd_capt = (Button) findViewById(R.id.obtn_metd_capt);

        this.capt_device = (TextView) findViewById(R.id.ocapt_device);

        /*-------------------ETIQUETAS------------------------*/
        this.lea_ant = (TextView) findViewById(R.id.olea_ant);
        this.lsa_ant = (TextView) findViewById(R.id.olsa_ant);
        this.leb_ant = (TextView) findViewById(R.id.oleb_ant);
        this.lsb_ant = (TextView) findViewById(R.id.olsb_ant);

        this.leb_act = (TextView) findViewById(R.id.oleb_act);
        this.leb_dif = (TextView) findViewById(R.id.oleb_dif);

        this.lsb_act = (TextView) findViewById(R.id.olsb_act);
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

        this.oSemanas = (EditText) findViewById(R.id.osemanas);
        this.oSemanas.setSelectAllOnFocus(true);

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

        this.tot_notas = (EditText) findViewById(R.id.otot_notas);

        this.baja_prod = (CheckBox) findViewById(R.id.obaja_prod);

        this.capt_device.setText("ID EQUIPO:" + Global.cid_device.toUpperCase());

        this.Clear_Screen();

        String databasePath = getDatabasePath("one2009.db").getPath();
        this.db4 = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);

        this.bFoundMach = this.Buscar_Maquina(Global.cEmp_Id, Global.cCte_Id, Global.cMaq_Id);

        this.MaquinaValid(true);

        this.Valid_Data();

        this.Calc_Dif_Ent(true);
        this.Calc_Dif_Sal(true);
        this.Calc_Tot(1);

        this.btn_metd_capt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Denom_Ent_Fac = 0;
                Denom_Sal_Fac = 0;
                fDenom_Ent_Val = 0;
                fDenom_Sal_Val = 0;

                Mask_Tetros();
                Mask_Ent();
                Mask_Sal();

                //Calc_Dif_Ent(true);
                //Calc_Dif_Sal(true);
                //Calc_Tot(1);
            }
        });

        /**************************************SECTION ON FOCUSED******************************************/
        this.ea_act.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ea_act.setText(ea_act.getText().toString().isEmpty() ? "0" : ea_act.getText().toString());
                }
            }
        });

        this.eb_act.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    eb_act.setText(eb_act.getText().toString().isEmpty() ? "0" : eb_act.getText().toString());
                }
            }
        });

        this.sa_act.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    sa_act.setText(sa_act.getText().toString().isEmpty() ? "0" : sa_act.getText().toString());
                }
            }
        });

        this.sb_act.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    sb_act.setText(sb_act.getText().toString().isEmpty() ? "0" : sb_act.getText().toString());
                }
            }
        });

        this.ea_ant.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ea_ant.setText(ea_ant.getText().toString().isEmpty() ? "0" : ea_ant.getText().toString());
                    if (cte_mod_metro_ant == 0)
                        ea_ant.setEnabled(false);
                    else
                        ea_ant.setEnabled(true);
                }
            }
        });

        this.eb_ant.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    eb_ant.setText(eb_ant.getText().toString().isEmpty() ? "0" : eb_ant.getText().toString());
                    if (cte_mod_metro_ant == 0)
                        eb_ant.setEnabled(false);
                    else
                        eb_ant.setEnabled(true);
                }
            }
        });

        this.sa_ant.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    sa_ant.setText(sa_ant.getText().toString().isEmpty() ? "0" : sa_ant.getText().toString());
                    if (cte_mod_metro_ant == 0)
                        sa_ant.setEnabled(false);
                    else
                        sa_ant.setEnabled(true);
                }
            }
        });

        this.sb_ant.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    sb_ant.setText(sb_ant.getText().toString().isEmpty() ? "0" : sb_ant.getText().toString());
                    if (cte_mod_metro_ant == 0)
                        sb_ant.setEnabled(false);
                    else
                        sb_ant.setEnabled(true);
                }
            }
        });
        /**************************************************************************************************/

        /**************************************SECTION ON CHANGES******************************************/
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

        this.ea_ant.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Dif_Ent(true);
                Calc_Tot(1);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

        this.eb_ant.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Dif_Ent(true);
                Calc_Tot(1);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

        this.sa_ant.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Dif_Sal(true);
                Tot_Cred(1);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

        this.sb_ant.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Dif_Sal(true);
                Tot_Cred(1);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        /**************************************************************************************************/

        /**************************************SECTION ON CLIC******************************************/
        this.oSemanas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oSemanas.getText().toString().isEmpty()){
                    oSemanas.setText("1");
                }
            }
        });

        this.baja_prod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                do_Baja_ProdClick();
            }
        });

        this.tot_notas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tot_notas.getText().toString().isEmpty())
                    tot_notas.setHint("Sin comentarios");
            }
        });

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

        this.btn_unlock_capt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.iObj_Select = 99;
                Intent Int_GetPass = new Intent(getApplicationContext(), get_password.class);
                startActivityForResult(Int_GetPass, Global.REQUEST_GET_PASS);
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
                String cNow2 = Global.getNow();

                String cSql_Ln = "";
                String CadenaMaq = "[" + Global.cCte_Id + "]_[" + Global.cMaq_Id + "]_IMG_";

                Double dtot_cole = Double.valueOf(tot_cole.getText().toString()).doubleValue();
                Double dtot_cred = Double.valueOf(tot_cred.getText().toString()).doubleValue();

                if (iMetro_EntDif < 0) {
                    Toast.makeText(getApplicationContext(), "PARA GUARDAR COLOQUE CORRECTAMENTE LOS METROS DE ENTRADA.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (iMetro_SalDif < 0) {
                    Toast.makeText(getApplicationContext(), "PARA GUARDAR COLOQUE CORRECTAMENTE LOS METROS DE SALIDA.", Toast.LENGTH_LONG).show();
                    return;
                }
/*
                Date date = new Date();
                String day          = (String) DateFormat.format("dd",   date); // 20
                String monthNumber  = (String) DateFormat.format("MM",   date); // 06
                String year         = (String) DateFormat.format("yyyy", date()); // 2013
*/
                cSql_Ln = "";
                cSql_Ln += "" +
                        "DELETE FROM operacion " +
                        "WHERE id_device='" + Global.cid_device + "' " +
                        "AND  op_chapa  ='" + op_chapa + "'; ";

                Log.e("SQL", cSql_Ln);
                //System.out.print(cSql_Ln);
                db4.execSQL(cSql_Ln);
                op_semanas_imp = Integer.parseInt(oSemanas.getText().toString());

                double Op_tot_impjcj2 = 0.00;
                double Op_tot_impmunic2 = 0.00;

                if (cte_pag_jcj == 0) {
                    Op_tot_impjcj2 = (Op_tot_impjcj * op_semanas_imp);
                }

                if (cte_pag_impm == 0) {
                    Op_tot_impmunic2 = (Op_tot_impmunic * op_semanas_imp);
                }
                Global.Correl_Device++;
                String Op_nodoc = Global.cid_device.substring(Global.cid_device.length() - 3, Global.cid_device.length()) + String.format("%08d", Global.Correl_Device);

                if (Denom_Ent_Fac == 0) {
                    maqtc_denom_e = "1";
                    den_valore = 0.00;
                }
                ;
                if (Denom_Sal_Fac == 0) {
                    maqtc_denom_e = "1";
                    den_valors = 0.00;
                }
                ;

                cSql_Ln = "";
                cSql_Ln += "INSERT INTO operacion ( ";
                cSql_Ln += "cte_id,";
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
                cSql_Ln += "op_emp_id,id_device,";
                cSql_Ln += "op_semanas_imp,op_nodoc,";
                cSql_Ln += "op_baja_prod, op_observ, op_image_name) VALUES (";
                cSql_Ln += "'" + Global.cCte_Id + "',";
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

                cSql_Ln += "'" + String.format("%.2f", Double.parseDouble(ea_ant.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%.2f", Double.parseDouble(ea_act.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%.2f", Double.parseDouble(ea_dif.getText().toString())) + "',";

                cSql_Ln += "'" + String.format("%.2f", Double.parseDouble(sa_ant.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%.2f", Double.parseDouble(sa_act.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%.2f", Double.parseDouble(sa_dif.getText().toString())) + "',";

                cSql_Ln += "'" + String.format("%.2f", Double.parseDouble(eb_ant.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%.2f", Double.parseDouble(eb_act.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%.2f", Double.parseDouble(eb_dif.getText().toString())) + "',";

                cSql_Ln += "'" + String.format("%.2f", Double.parseDouble(sb_ant.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%.2f", Double.parseDouble(sb_act.getText().toString())) + "',";
                cSql_Ln += "'" + String.format("%.2f", Double.parseDouble(eb_dif.getText().toString())) + "',";
                cSql_Ln += "'0',";

                cSql_Ln += "'" + String.format("%.2f", op_cal_colect) + "',";
                cSql_Ln += "'" + String.format("%.2f", dtot_cole) + "',";

                cSql_Ln += "'" + String.format("%.2f", op_cal_cred) + "',";
                cSql_Ln += "'" + String.format("%.2f", dtot_cred) + "',";
                cSql_Ln += "'" + cNow2 + "',";
                cSql_Ln += "'" + cNow2 + "',";
                cSql_Ln += "'" + Op_tot_impmunic2 + "',";
                cSql_Ln += "'" + Op_tot_impjcj2 + "',";

                cSql_Ln += "'" + op_emp_id + "',";
                cSql_Ln += "'" + Global.cid_device + "',";
                cSql_Ln += "'" + op_semanas_imp + "',";

                cSql_Ln += "'" + Op_nodoc + "',";
                cSql_Ln += "'" + (baja_prod.isChecked() == true ? "1" : "0") + "',";
                cSql_Ln += "'" + tot_notas.getText().toString() + "',";

                if (Global.cLastFilePhoto.toLowerCase().contains(CadenaMaq.trim().toLowerCase())) {
                    cSql_Ln += "'" + Global.cLastFilePhoto + "'";
                } else {
                    cSql_Ln += "''";
                }
                cSql_Ln += ");";

                Log.e("SQL", cSql_Ln);
                //System.out.print(cSql_Ln);
                db4.execSQL(cSql_Ln);
                db4.close();

                cSql_Ln = "";
                cSql_Ln += "UPDATE dispositivos SET ";
                cSql_Ln += " corre_act ='" + String.valueOf(Global.Correl_Device) + "' ";
                cSql_Ln += "WHERE serial ='" + Global.cid_device + "'";
                Global.Query_Update(cSql_Ln);

                Clear_Screen();

                Intent i = getIntent();
                setResult(RESULT_OK, i);

                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                finish();
            }
        });

        btn_cancel_capt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);

                db4.close();

                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                finish();
            }
        });

        btn_foto_capt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent Int_TakePhoto = new Intent(getApplicationContext(), take_photo.class);
                Int_TakePhoto.putExtra("show_image", 1);
                startActivity(Int_TakePhoto);
            }
        });
        /**************************************************************************************************/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        {
            if (requestCode == Global.REQUEST_GET_PASS) {
                //Se procesa la devolución
                switch (resultCode) {
                    case RESULT_OK:
                        int ipass = data.getIntExtra("PASSWORD", -1);
                        Log.e("TAG", Integer.valueOf(ipass).toString());

                        if (ipass == Integer.valueOf(Global.PasswChgMeters)) {
                            switch (Global.iObj_Select) {
                                case 99:
                                    this.ea_ant.setEnabled(true);
                                    this.sa_ant.setEnabled(true);
                                    this.eb_ant.setEnabled(true);
                                    this.sb_ant.setEnabled(true);
                                    break;
                                case 0:
                                    this.ea_ant.setEnabled(false);
                                    this.sa_ant.setEnabled(false);
                                    this.eb_ant.setEnabled(false);
                                    this.sb_ant.setEnabled(false);
                                    break;
                                case 1:
                                    this.ea_ant.setEnabled(true);
                                    this.ea_ant.selectAll();
                                    this.ea_ant.requestFocus();
                                    break;
                                case 2:
                                    this.sa_ant.setEnabled(true);
                                    this.sa_ant.selectAll();
                                    this.sa_ant.requestFocus();
                                    break;
                                case 3:
                                    this.eb_ant.setEnabled(true);
                                    this.eb_ant.selectAll();
                                    this.eb_ant.requestFocus();
                                    break;
                                case 4:
                                    this.sb_ant.setEnabled(true);
                                    this.sb_ant.selectAll();
                                    this.sb_ant.requestFocus();
                                    break;
                            }
                            //Toast.makeText(this, "CONTRASEÑA VALIDA", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case RESULT_CANCELED:
                        switch (Global.iObj_Select) {
                            case 0:
                                this.ea_ant.setEnabled(false);
                                this.sa_ant.setEnabled(false);
                                this.eb_ant.setEnabled(false);
                                this.sb_ant.setEnabled(false);
                                break;
                            case 1:
                                this.ea_ant.setEnabled(false);
                                break;
                            case 2:
                                this.sa_ant.setEnabled(false);
                                break;
                            case 3:
                                this.eb_ant.setEnabled(false);
                                break;
                            case 4:
                                this.sb_ant.setEnabled(false);
                                break;
                        }
                        Toast.makeText(this, "Canceló la operación.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            if (requestCode == Global.REQUEST_CAMERA) {
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

    private void Mask_Ent() {
        String sea_act, sea_ant;
        String seb_act, seb_ant;
        String sea_dif, seb_dif;

        Double fea_act, fea_ant;
        Double feb_act, feb_ant;
        Double fea_dif, feb_dif;

        if (Denom_Ent_Fac == 0) {
            sea_act = this.ea_act.getText().toString();
            sea_act = (this.ea_act.getText().toString().isEmpty() ? "0" : sea_act);

            sea_ant = this.ea_ant.getText().toString();
            sea_ant = (this.ea_ant.getText().toString().isEmpty() ? "0" : sea_ant);

            seb_act = this.eb_act.getText().toString();
            seb_act = (this.eb_act.getText().toString().isEmpty() ? "0" : seb_act);

            seb_ant = this.eb_ant.getText().toString();
            seb_ant = (this.eb_ant.getText().toString().isEmpty() ? "0" : seb_ant);

            sea_dif = this.ea_dif.getText().toString();
            sea_dif = (this.ea_dif.getText().toString().isEmpty() ? "0" : sea_dif);

            seb_dif = this.eb_dif.getText().toString();
            seb_dif = (this.eb_dif.getText().toString().isEmpty() ? "0" : seb_dif);

            fea_act = Double.valueOf(sea_act).doubleValue();
            fea_ant = Double.valueOf(sea_ant).doubleValue();

            feb_act = Double.valueOf(seb_act).doubleValue();
            feb_ant = Double.valueOf(seb_ant).doubleValue();

            fea_dif = Double.valueOf(sea_dif).doubleValue();
            feb_dif = Double.valueOf(seb_dif).doubleValue();

            sea_act = String.format(Locale.US, "%.2f", fea_act);
            sea_ant = String.format(Locale.US, "%.2f", fea_ant);

            seb_act = String.format(Locale.US, "%.2f", feb_act);
            seb_ant = String.format(Locale.US, "%.2f", feb_ant);

            sea_dif = String.format(Locale.US, "%.2f", fea_dif);
            seb_dif = String.format(Locale.US, "%.2f", feb_dif);

            this.ea_act.setText(sea_act);
            this.ea_ant.setText(sea_ant);
            this.ea_dif.setText(sea_dif);

            this.eb_act.setText(seb_act);
            this.eb_ant.setText(seb_ant);
            this.eb_dif.setText(seb_dif);
        }
    }

    private void Mask_Sal() {
        String ssa_act, ssa_ant;
        String ssb_act, ssb_ant;
        String ssa_dif, ssb_dif;

        Double fsa_act, fsa_ant;
        Double fsb_act, fsb_ant;
        Double fsa_dif, fsb_dif;


        if (Denom_Sal_Fac == 0) {

            ssa_act = this.sa_act.getText().toString();
            ssa_act = (this.sa_act.getText().toString().isEmpty() ? "0" : ssa_act);

            ssa_ant = this.sa_ant.getText().toString();
            ssa_ant = (this.sa_ant.getText().toString().isEmpty() ? "0" : ssa_ant);

            ssb_act = this.sb_act.getText().toString();
            ssb_act = (this.sb_act.getText().toString().isEmpty() ? "0" : ssb_act);

            ssb_ant = this.sb_ant.getText().toString();
            ssb_ant = (this.sb_ant.getText().toString().isEmpty() ? "0" : ssb_ant);

            ssa_dif = this.sa_dif.getText().toString();
            ssa_dif = (this.sa_dif.getText().toString().isEmpty() ? "0" : ssa_dif);

            ssb_dif = this.sb_dif.getText().toString();
            ssb_dif = (this.sb_dif.getText().toString().isEmpty() ? "0" : ssb_dif);

            fsa_act = Double.valueOf(ssa_act).doubleValue();
            fsa_ant = Double.valueOf(ssa_ant).doubleValue();

            fsb_act = Double.valueOf(ssb_act).doubleValue();
            fsb_ant = Double.valueOf(ssb_ant).doubleValue();

            fsa_dif = Double.valueOf(ssa_dif).doubleValue();
            fsb_dif = Double.valueOf(ssb_dif).doubleValue();

            ssa_act = String.format(Locale.US, "%.2f", fsa_act);
            ssa_ant = String.format(Locale.US, "%.2f", fsa_ant);

            ssb_act = String.format(Locale.US, "%.2f", fsb_act);
            ssb_ant = String.format(Locale.US, "%.2f", fsb_ant);

            ssa_dif = String.format(Locale.US, "%.2f", fsa_dif);
            ssb_dif = String.format(Locale.US, "%.2f", fsb_dif);

            this.sa_act.setText(ssa_act);
            this.sa_ant.setText(ssa_ant);
            this.sa_dif.setText(ssa_dif);

            this.sb_act.setText(ssb_act);
            this.sb_ant.setText(ssb_ant);
            this.sb_dif.setText(ssb_dif);
        }
    }

    private void Calc_Dif_Ent(boolean A) {
        double fea_act, fea_ant;
        double feb_act, feb_ant;

        String sea_act, sea_ant;
        String seb_act, seb_ant;

        sea_act = this.ea_act.getText().toString();
        sea_act = (this.ea_act.getText().toString().isEmpty() ? "0" : sea_act);

        sea_ant = this.ea_ant.getText().toString();
        sea_ant = (this.ea_ant.getText().toString().isEmpty() ? "0" : sea_ant);

        seb_act = this.eb_act.getText().toString();
        seb_act = (this.eb_act.getText().toString().isEmpty() ? "0" : seb_act);

        seb_ant = this.eb_ant.getText().toString();
        seb_ant = (this.eb_ant.getText().toString().isEmpty() ? "0" : seb_ant);

        fea_act = Double.valueOf(sea_act).doubleValue();
        fea_ant = Double.valueOf(sea_ant).doubleValue();

        feb_act = Double.valueOf(seb_act).doubleValue();
        feb_ant = Double.valueOf(seb_ant).doubleValue();

        iMetroA_EntDif = (fea_act - fea_ant);
        iMetroB_EntDif = (feb_act - feb_ant);

        if (A == true) {
            ea_dif.setText(String.format("%.2f", iMetroA_EntDif));
        } else {
            eb_dif.setText(String.format("%.2f", iMetroB_EntDif));
        }
        iMetro_EntDif = iMetroA_EntDif + iMetroB_EntDif;
    }

    private void Calc_Dif_Sal(boolean A) {
        Double fsa_act, fsa_ant;
        Double fsb_act, fsb_ant;

        String ssa_act, ssa_ant;
        String ssb_act, ssb_ant;

        ssa_act = this.sa_act.getText().toString();
        ssa_act = (this.sa_act.getText().toString().isEmpty() ? "0" : ssa_act);

        ssa_ant = this.sa_ant.getText().toString();
        ssa_ant = (this.sa_ant.getText().toString().isEmpty() ? "0" : ssa_ant);

        ssb_act = this.sb_act.getText().toString();
        ssb_act = (this.sb_act.getText().toString().isEmpty() ? "0" : ssb_act);

        ssb_ant = this.sb_ant.getText().toString();
        ssb_ant = (this.sb_ant.getText().toString().isEmpty() ? "0" : ssb_ant);

        fsa_act = Double.valueOf(ssa_act).doubleValue();
        fsa_ant = Double.valueOf(ssa_ant).doubleValue();

        fsb_act = Double.valueOf(ssb_act).doubleValue();
        fsb_ant = Double.valueOf(ssb_ant).doubleValue();

        iMetroA_SalDif = (fsa_act - fsa_ant);
        iMetroB_SalDif = (fsb_act - fsb_ant);

        if (A == true) {
            sa_dif.setText(String.format("%.2f", iMetroA_SalDif));
        } else {
            sb_dif.setText(String.format("%.2f", iMetroB_SalDif));
        }
        iMetro_SalDif = iMetroA_SalDif + iMetroB_SalDif;

    }

    private void Calc_Tot(int iForce) {
        double fpule = (this.Denom_Ent_Fac == 0 ? 1 : this.Denom_Ent_Fac);
        double fdsal = (this.fDenom_Ent_Val == 0 ? 1 : this.fDenom_Ent_Val);
        double fdif1 = (this.iMetroA_EntDif + this.iMetroB_EntDif);
        double fdif2 = (fdif1 / fpule);
        double fTot = (fdif2 * fdsal);

        String cTot = String.format("%.2f", fTot);

        if (iForce == 1) {
            this.tot_cole.setText(String.format("%.2f", fTot));
            this.Calc_Sub_Tot();
        } else {
            double ftot_cole = Double.valueOf(tot_cole.getText().toString()).doubleValue();
            op_colect = ftot_cole;
            if (ftot_cole <= 0.00) {
                this.tot_cole.setText(String.format("%.2f", fTot));
                this.Calc_Sub_Tot();
            }
        }
        op_cal_colect = fTot;
    }

    private void Tot_Cred(int iForce) {

        double fpule = (this.Denom_Sal_Fac == 0 ? 1 : this.Denom_Sal_Fac);
        double fdsal = (this.fDenom_Sal_Val == 0 ? 1 : this.fDenom_Sal_Val);
        double fdif1 = (this.iMetroA_SalDif + this.iMetroB_SalDif);
        double fdif2 = (fdif1 / fpule);
        double fTot = (fdif2 * fdsal);
        String cTot = String.format("%.2f", fTot);

        if (iForce == 1) {
            this.tot_cred.setText(String.format("%.2f", fTot));
            this.Calc_Sub_Tot();
        } else {
            double ftot_prem = Double.valueOf(tot_cred.getText().toString()).doubleValue();
            op_cred = (ftot_prem);
            if (ftot_prem <= 0) {
                this.tot_cred.setText(String.format("%.2f", fTot));
                this.Calc_Sub_Tot();
            }
        }
        op_cal_cred = fTot;
    }

    private void Calc_Sub_Tot() {
    }

    private void Clear_Screen() {
        this.baja_prod.setChecked(false);

        this.ea_act.setText((this.Denom_Ent_Fac == 0 ? "0.00" : "0"));
        this.ea_ant.setText((this.Denom_Ent_Fac == 0 ? "0.00" : "0"));
        if (this.cte_mod_metro_ant == 0)
            this.ea_ant.setEnabled(false);
        else
            this.ea_ant.setEnabled(true);
        this.ea_dif.setText((this.Denom_Ent_Fac == 0 ? "0.00" : "0"));
        this.ea_dif.setEnabled(false);

        this.eb_act.setText((this.Denom_Ent_Fac == 0 ? "0.00" : "0"));
        this.eb_ant.setText((this.Denom_Ent_Fac == 0 ? "0.00" : "0"));
        if (this.cte_mod_metro_ant == 0)
            this.eb_ant.setEnabled(false);
        else
            this.eb_ant.setEnabled(true);
        this.eb_dif.setText((this.Denom_Ent_Fac == 0 ? "0.00" : "0"));
        this.eb_dif.setEnabled(false);

        this.sa_act.setText((this.Denom_Sal_Fac == 0 ? "0.00" : "0"));
        this.sa_ant.setText((this.Denom_Sal_Fac == 0 ? "0.00" : "0"));
        if (this.cte_mod_metro_ant == 0)
            this.sa_ant.setEnabled(false);
        else
            this.sa_ant.setEnabled(true);
        this.sa_dif.setText((this.Denom_Sal_Fac == 0 ? "0.00" : "0"));
        this.sa_dif.setEnabled(false);

        this.sb_act.setText((this.Denom_Sal_Fac == 0 ? "0.00" : "0"));
        this.sb_ant.setText((this.Denom_Sal_Fac == 0 ? "0.00" : "0"));
        if (this.cte_mod_metro_ant == 0)
            this.sb_ant.setEnabled(false);
        else
            this.sb_ant.setEnabled(true);
        this.sb_dif.setText((this.Denom_Sal_Fac == 0 ? "0.00" : "0"));
        this.sb_dif.setEnabled(false);

        this.tot_cole.setText("0.00");
        this.tot_cole.setEnabled(false);
        this.tot_cred.setText("0.00");
        this.tot_cred.setEnabled(false);
        this.tot_notas.setText("");
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
        cSqlLn += "  IFNULL(maquinastc.maqtc_sem_jcj,1) AS maqtc_sem_jcj, ";
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
        cSqlLn += "  clientes.cte_mod_metro_ant, ";
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

        data4 = db4.rawQuery(cSqlLn, null);

        if ((data4 == null) || (data4.getCount() == 0)) {
            Toast.makeText(getApplicationContext(), "El código de máquina no existe o no tiene un cliente asignado", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private void Mask_Tetros() {
        if (Denom_Ent_Fac == 0) {
            // Entradas A
            this.ea_ant.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            this.ea_act.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            this.ea_dif.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            this.ea_ant.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            this.ea_act.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            this.ea_dif.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        } else {
            // Entradas A
            this.ea_ant.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            this.ea_act.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            this.ea_dif.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);

            // Entradas B
            this.eb_ant.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            this.eb_act.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            this.eb_dif.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            this.eb_dif.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
        }

        if (Denom_Sal_Fac == 0) {
            // Salidas A
            this.sa_act.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            this.sa_ant.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            this.sa_dif.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            // Salidas B
            this.sb_act.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            this.sb_ant.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            this.sb_dif.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else {
            // Salidas A
            this.sa_act.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            this.sa_ant.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            this.sa_dif.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            // Salidas B
            this.sb_act.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            this.sb_ant.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            this.sb_dif.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
        }
        this.Calc_Dif_Ent(true);
        this.Calc_Dif_Sal(true);
        this.Calc_Tot(1);
    }

    private void MaquinaValid(Boolean bIsNew) {
        data4.moveToFirst();
        op_serie = "0";

        //this.lab_cte.setText("CLIENTE: " + data4.getString(data.getColumnIndex("cte_nombre_loc")));
        //this.lab_cha.setText("CHAPA  : " + data4.getString(data.getColumnIndex("maqtc_chapa")));

        this.lab_cte.setText("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De + "]");
        this.lab_cha.setText("CHAPA  : [" + Global.cMaq_Id + "]/[" + Global.cMaq_De + "]");

        op_chapa = data4.getString(data4.getColumnIndex("maqtc_chapa"));

        op_emp_id = data4.getInt(data4.getColumnIndex("emp_id"));
        maqlnk_id = data4.getString(data4.getColumnIndex("MaqLnk_Id"));

        Denom_Ent_Fac = data4.getInt(data4.getColumnIndex("den_fact_e"));
        Denom_Sal_Fac = data4.getInt(data4.getColumnIndex("den_fact_s"));

        fDenom_Ent_Val = data4.getDouble(data4.getColumnIndex("den_valore"));
        fDenom_Sal_Val = data4.getDouble(data4.getColumnIndex("den_valors"));

        this.Mask_Tetros();

        if (data4.getDouble(data4.getColumnIndex("cte_poc_ret")) == 0)
            Porc_Loc = 1;
        else
            Porc_Loc = data4.getDouble(data4.getColumnIndex("cte_poc_ret"));

        op_modelo = data4.getString(data4.getColumnIndex("maqtc_modelo"));
        cte_nombre_loc = data4.getString(data4.getColumnIndex("cte_nombre_loc"));
        cte_nombre_com = data4.getString(data4.getColumnIndex("cte_nombre_com"));
        cte_pag_jcj = data4.getInt(data4.getColumnIndex("cte_pag_jcj"));
        cte_pag_spac = data4.getInt(data4.getColumnIndex("cte_pag_spac"));

        cte_mod_metro_ant = data4.getInt(data4.getColumnIndex("cte_mod_metro_ant"));

        cte_pag_impm = data4.getInt(data4.getColumnIndex("cte_pag_impm"));
        op_cporc_Loc = data4.getDouble(data4.getColumnIndex("cte_poc_ret"));

        maqtc_denom_e = data4.getString(data4.getColumnIndex("maqtc_denom_e"));
        maqtc_denom_s = data4.getString(data4.getColumnIndex("maqtc_denom_s"));
        maqtc_tipomaq = data4.getInt(data4.getColumnIndex("maqtc_tipomaq"));
        den_fact_e = data4.getInt(data4.getColumnIndex("den_fact_e"));
        den_fact_s = data4.getInt(data4.getColumnIndex("den_fact_s"));
        den_valore = data4.getDouble(data4.getColumnIndex("den_valore"));
        den_valors = data4.getDouble(data4.getColumnIndex("den_valors"));

        this.oSemanas.setText(data4.getString(data4.getColumnIndex("maqtc_sem_jcj")));

        //if (self.otOperaciones.State = dsInsert) {
        if (bIsNew == true) {

            if (cte_pag_jcj == 0)
                Op_tot_impjcj = (data4.getDouble(data4.getColumnIndex("emp_cargo_jcj")) / 4);

            if (cte_pag_impm == 0)
                Op_tot_impmunic = data4.getDouble(data4.getColumnIndex("mun_impuesto"));
       }

        switch (data4.getInt(data4.getColumnIndex("maqtc_metros"))) {
            case 1:
                if (bIsNew == true) {
                    // Entradas A
                    this.ea_act.setEnabled(true);
                    this.ea_act.setText("0");
                    this.ea_ant.setText(data4.getString(data4.getColumnIndex("maqtc_m1e_act")));
                    // Salidas A
                    this.sa_act.setEnabled(true);
                    this.sa_act.setText("0");
                    this.sa_ant.setText(data4.getString(data4.getColumnIndex("maqtc_m1s_act")));

                    if (data4.getDouble(data4.getColumnIndex("maqtc_m1e_ant")) == 0.00)
                        this.ea_ant.setEnabled(true);
                    else {
                        if (cte_mod_metro_ant == 0)
                            this.ea_ant.setEnabled(false);
                        else
                            this.ea_ant.setEnabled(true);
                    }

                    if (data4.getDouble(data4.getColumnIndex("maqtc_m1s_ant")) == 0)
                        this.sa_ant.setEnabled(true);
                    else {
                        if (cte_mod_metro_ant == 0)
                            this.sa_ant.setEnabled(false);
                        else
                            this.sa_ant.setEnabled(true);
                    }

                } else {
                    // Entradas A
                    this.ea_act.setEnabled(true);
                    this.ea_ant.setEnabled(true);

                    this.ea_act.setText(data4.getString(data4.getColumnIndex("maqtc_m1e_act")));
                    this.ea_ant.setText(data4.getString(data4.getColumnIndex("maqtc_m1e_ant")));
                    // Salidas A
                    this.sa_act.setEnabled(true);
                    this.sa_ant.setEnabled(true);

                    this.sa_act.setText(data4.getString(data4.getColumnIndex("maqtc_m1s_act")));
                    this.sa_ant.setText(data4.getString(data4.getColumnIndex("maqtc_m1s_ant")));
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
                    this.ea_ant.setText(data4.getString(data4.getColumnIndex("maqtc_m1e_act")));
                    // Salidas A
                    this.sa_act.setEnabled(true);
                    this.sa_act.setText("0");
                    this.sa_ant.setText(data4.getString(data4.getColumnIndex("maqtc_m1s_act")));

                    if (data4.getDouble(data4.getColumnIndex("maqtc_m1e_ant")) == 0)
                        this.ea_ant.setEnabled(true);
                    else {
                        if (cte_mod_metro_ant == 0)
                            this.ea_ant.setEnabled(false);
                        else
                            this.ea_ant.setEnabled(true);
                    }
                    if (data4.getDouble(data4.getColumnIndex("maqtc_m1s_ant")) == 0)
                        this.sa_ant.setEnabled(true);
                    else {
                        if (cte_mod_metro_ant == 0)
                            this.sa_ant.setEnabled(false);
                        else
                            this.sa_ant.setEnabled(true);
                    }
                    // --------------------------------------------------------
                    // Entradas B
                    this.eb_act.setEnabled(true);
                    this.eb_act.setText("0");
                    this.eb_ant.setText(data4.getString(data4.getColumnIndex("maqtc_m2e_act")));
                    // Salidas B

                    this.sb_act.setEnabled(true);
                    this.sb_act.setText("0");
                    this.sb_ant.setText(data4.getString(data4.getColumnIndex("maqtc_m2s_act")));

                    if (data4.getDouble(data4.getColumnIndex("maqtc_m2e_ant")) == 0)
                        this.eb_ant.setEnabled(true);
                    else {
                        if (cte_mod_metro_ant == 0)
                            this.eb_ant.setEnabled(false);
                        else
                            this.eb_ant.setEnabled(true);
                    }
                    if (data4.getDouble(data4.getColumnIndex("maqtc_m2s_ant")) == 0)
                        this.sb_ant.setEnabled(true);
                    else {
                        if (cte_mod_metro_ant == 0)
                            this.sb_ant.setEnabled(false);
                        else
                            this.sb_ant.setEnabled(true);
                    }
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

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void Valid_Data() {
        if (this.ea_ant.getText().toString().length() == 0) {
            this.ea_ant.setError(null);
            this.ea_ant.setText("0");
        }

        if (this.ea_act.getText().toString().length() == 0) {
            this.ea_act.setError(null);
            this.ea_act.setText("0");
        }

        if (this.eb_ant.getText().toString().length() == 0) {
            this.eb_ant.setError(null);
            this.eb_ant.setText("0");
        }

        if (this.eb_ant.getText().toString().length() == 0) {
            this.eb_ant.setError(null);
            this.eb_ant.setText("0");
        }

        if (this.sa_ant.getText().toString().length() == 0) {
            this.sa_ant.setError(null);
            this.sa_ant.setText("0");
        }

        if (this.sa_act.getText().toString().length() == 0) {
            this.sa_act.setError(null);
            this.sa_act.setText("0");
        }

        if (this.sb_ant.getText().toString().length() == 0) {
            this.sb_ant.setError(null);
            this.sb_ant.setText("0");
        }

        if (this.sb_act.getText().toString().length() == 0) {
            this.sb_act.setError(null);
            this.sb_act.setText("0");
        }
    }

    private void do_Baja_ProdClick() {

        if (this.baja_prod.isChecked() == true) {

            this.tot_cole.setText("0.00");
            op_cal_colect = 0.00;
            this.tot_cred.setText("0.00");
            op_cal_cred = 0.00;

            this.ea_act.setText(this.ea_ant.getText().toString());
            this.sa_act.setText(this.sa_ant.getText().toString());

            this.eb_act.setText(this.eb_ant.getText().toString());
            this.sb_act.setText(this.eb_ant.getText().toString());
            // ----------------------------------------------------------------------------------
            this.Calc_Sub_Tot();

            // ----------------------------------------------------------------------------------

            this.eb_act.setEnabled(false);
            this.sb_act.setEnabled(false);
            this.ea_act.setEnabled(false);
            this.sa_act.setEnabled(false);
        } else {

            this.tot_cole.setText("0.00");
            op_cal_colect = 0.00;
            this.tot_cred.setText("0.00");
            op_cal_cred = 0.00;

            this.ea_act.setText("0");
            this.sa_act.setText("0");

            this.eb_act.setText("0");
            this.sb_act.setText("0");

            this.eb_act.setEnabled(true);
            this.sb_act.setEnabled(true);
            this.ea_act.setEnabled(true);
            this.sa_act.setEnabled(true);
        }
    }


}
