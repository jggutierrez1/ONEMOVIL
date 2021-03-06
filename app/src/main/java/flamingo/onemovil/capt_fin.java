package flamingo.onemovil;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;

import io.requery.android.database.sqlite.*;

import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class capt_fin extends AppCompatActivity {
    private final static int REQUEST_GET_PASS = 6;
    private Button obtn_print_maq, obtn_totfin_hide, obtn_totfin_save, obtn_totfin_canc, obtn_totfin_unlock;
    private EditText oOp_tot_cole, oOp_tot_timb, oOp_tot_impm, oOp_tot_jcj, oOp_tot_cons, oOp_tot_tenc, oOp_tot_devo, oOp_tot_otro,
            oOp_tot_cred, oOp_tot_subt, oOp_tot_impu, oOp_tot_tota, oOp_tot_bloc, oOp_tot_bemp, oOp_tot_nloc, oOp_tot_nemp,
            ototfin_notas, oOp_fact_global;
    private TextView olab_cte, olOp_tot_cole, olOp_tot_cred, olOp_tot_jcj, oapf_device;
    private SQLiteDatabase oDb5;
    private Cursor oData5;
    private double fPorc_Loc = 0.00;
    private int itotfin_bruto, itotfin_prem, itotfin_devo, itotfin_otros, itotfin_total, itotfin_gast, itotfin_neto, itotfin_notas;
    private Context oThis = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capt_fin);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        Locale.setDefault(new Locale("en", "US"));

        this.olab_cte = (TextView) findViewById(R.id.capf_lab_cte);
        this.olOp_tot_cole = (TextView) findViewById(R.id.lop_tot_cole);
        this.olOp_tot_cred = (TextView) findViewById(R.id.lop_tot_cred);
        this.olOp_tot_jcj = (TextView) findViewById(R.id.lop_tot_jcj);
        this.oapf_device = (TextView) findViewById(R.id.apf_device);

        this.obtn_totfin_hide = (Button) findViewById(R.id.btn_totfin_hide);
        this.obtn_totfin_save = (Button) findViewById(R.id.btn_totfin_save);
        this.obtn_totfin_canc = (Button) findViewById(R.id.btn_totfin_canc);
        this.obtn_totfin_unlock = (Button) findViewById(R.id.btn_totfin_unlock);

        this.oOp_tot_cole = (EditText) findViewById(R.id.op_tot_cole);
        this.oOp_tot_timb = (EditText) findViewById(R.id.op_tot_timb);
        this.oOp_tot_impm = (EditText) findViewById(R.id.op_tot_impm);
        this.oOp_tot_jcj = (EditText) findViewById(R.id.op_tot_jcj);
        this.oOp_tot_cons = (EditText) findViewById(R.id.op_tot_cons);

        this.oOp_tot_tenc = (EditText) findViewById(R.id.op_tot_tecn);
        this.oOp_tot_devo = (EditText) findViewById(R.id.op_tot_devo);
        this.oOp_tot_otro = (EditText) findViewById(R.id.op_tot_otro);
        this.oOp_tot_cred = (EditText) findViewById(R.id.op_tot_cred);

        this.oOp_tot_subt = (EditText) findViewById(R.id.op_tot_subt);
        this.oOp_tot_impu = (EditText) findViewById(R.id.op_tot_impu);
        this.oOp_tot_tota = (EditText) findViewById(R.id.op_tot_tota);

        this.oOp_tot_bloc = (EditText) findViewById(R.id.op_tot_bloc);
        this.oOp_tot_bemp = (EditText) findViewById(R.id.op_tot_bemp);

        this.oOp_tot_nloc = (EditText) findViewById(R.id.op_tot_nloc);
        this.oOp_tot_nemp = (EditText) findViewById(R.id.op_tot_nemp);

        this.ototfin_notas = (EditText) findViewById(R.id.totfin_notas);
        this.oOp_fact_global = (EditText) findViewById(R.id.op_fact_global);

        Global.qry_cte_info(Global.cCte_Id);
        String cPorc = Global.decimalformat(Global.fCte_Por, 5, 2).trim();

        this.olab_cte.setText("CLIENTE: [" + Global.cCte_Id + "]-[" + Global.cCte_De + "]-[" + cPorc + "%]");
        this.oapf_device.setText("ID EQUIPO:" + Global.cid_device.toUpperCase());

        this.Clear_Screen();

        String databasePath = getDatabasePath("one2009.db").getPath();
        this.oDb5 = io.requery.android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(databasePath, null, null);

        this.Find_Values();
        this.Buscar_Cte(Global.cCte_Id);

        this.Valid_Data();
        this.Calc_Sub_Tot();

        /**************************************SECTION ON FOCUSED******************************************/
        this.oOp_tot_cole.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    oOp_tot_cole.setText(oOp_tot_cole.getText().toString().isEmpty() ? "0" : oOp_tot_cole.getText().toString());
                }
            }
        });

        this.oOp_tot_timb.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    oOp_tot_timb.setText(oOp_tot_timb.getText().toString().isEmpty() ? "0" : oOp_tot_timb.getText().toString());
                }
            }
        });

        this.oOp_tot_impm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    oOp_tot_impm.setText(oOp_tot_impm.getText().toString().isEmpty() ? "0" : oOp_tot_impm.getText().toString());
                }
            }
        });


        this.oOp_tot_jcj.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    oOp_tot_jcj.setText(oOp_tot_jcj.getText().toString().isEmpty() ? "0" : oOp_tot_jcj.getText().toString());
                }
            }
        });

        this.oOp_tot_cons.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    oOp_tot_cons.setText(oOp_tot_cons.getText().toString().isEmpty() ? "0" : oOp_tot_cons.getText().toString());
                }
            }
        });

        this.oOp_tot_tenc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    oOp_tot_tenc.setText(oOp_tot_tenc.getText().toString().isEmpty() ? "0" : oOp_tot_tenc.getText().toString());
                }
            }
        });

        this.oOp_tot_cred.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    oOp_tot_cred.setText(oOp_tot_cred.getText().toString().isEmpty() ? "0" : oOp_tot_cred.getText().toString());
                }
            }
        });

        this.oOp_tot_devo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    oOp_tot_devo.setText(oOp_tot_devo.getText().toString().isEmpty() ? "0" : oOp_tot_devo.getText().toString());
                }
            }
        });

        this.oOp_tot_otro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    oOp_tot_otro.setText(oOp_tot_otro.getText().toString().isEmpty() ? "0" : oOp_tot_otro.getText().toString());
                }
            }
        });

        this.oOp_tot_subt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    oOp_tot_subt.setText(oOp_tot_subt.getText().toString().isEmpty() ? "0" : oOp_tot_subt.getText().toString());
                }
            }
        });

        this.oOp_tot_tota.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    oOp_tot_tota.setText(oOp_tot_tota.getText().toString().isEmpty() ? "0" : oOp_tot_tota.getText().toString());
                }
            }
        });
        /**************************************************************************************************/

        /**************************************SECTION ON TEXT CHANGE**************************************/
        this.oOp_tot_cole.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Sub_Tot();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.oOp_tot_timb.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Sub_Tot();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.oOp_tot_impm.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Sub_Tot();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.oOp_tot_jcj.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Sub_Tot();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.oOp_tot_cons.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Sub_Tot();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.oOp_tot_tenc.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Sub_Tot();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.oOp_tot_cred.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Sub_Tot();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.oOp_tot_devo.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Sub_Tot();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.oOp_tot_otro.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Sub_Tot();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        this.oOp_tot_impu.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Calc_Sub_Tot();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        /**************************************************************************************************/

        /**************************************SECTION ON CLIC*****************************************/
        this.ototfin_notas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ototfin_notas.getText().toString() == "")
                    ototfin_notas.setHint("No hay comentarios");
            }
        });

        this.obtn_totfin_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.PasswordTitle = "OPCION PARA DESBLOQUEO DE MONTOS";
                Global.iObj_Select = 99;
                Intent Int_GetPass = new Intent(getApplicationContext(), get_password.class);
                startActivityForResult(Int_GetPass, REQUEST_GET_PASS);
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
                String cOp_fact_global = oOp_fact_global.getText().toString().trim();
                if (cOp_fact_global.isEmpty() == true) {
                    Global.showSimpleOKAlertDialog(oThis, "AVISO IMPORTANTE.", "NO ES POSIBLE GUARDAR SIN COLOCAR EL NUMERO DE LA FACTURA GLOBAL.");
                    oOp_fact_global.requestFocus();
                    return;
                }

                int iRegs_Cnt = 0;
                int iId_Group = 0;
                String cSql_Ln = "";

                Double dtot_cole = 0.00;
                Double dtot_timb = 0.00;
                Double dtot_impm = 0.00;
                Double dtot__jcj = 0.00;
                Double dtot_cons = 0.00;
                Double dtot_tecn = 0.00;
                Double dtot_cred = 0.00;
                Double dtot_otro = 0.00;
                Double dtot_devo = 0.00;
                Double dSub_tota = 0.00;
                Double dtot_impu = 0.00;
                Double dtot_tota = 0.00;
                Double dtot_bloc = 0.00;
                Double dtot_bemp = 0.00;
                Double dtot_nloc = 0.00;
                Double dtot_nemp = 0.00;

                String cRegs_Cnt = "0";
                String ctot_cole = "0.00";
                String ctot_timb = "0.00";
                String ctot_impm = "0.00";
                String ctot__jcj = "0.00";
                String ctot_cons = "0.00";
                String ctot_tecn = "0.00";
                String ctot_cred = "0.00";
                String ctot_otro = "0.00";
                String ctot_devo = "0.00";
                String cSub_tota = "0.00";
                String ctot_impu = "0.00";
                String ctot_tota = "0.00";
                String ctot_bloc = "0.00";
                String ctot_bemp = "0.00";
                String ctot_nloc = "0.00";
                String ctot_nemp = "0.00";

                dtot_cole = Global.StrToFloat(oOp_tot_cole.getText().toString());
                dtot_timb = Global.StrToFloat(oOp_tot_timb.getText().toString());
                dtot_impm = Global.StrToFloat(oOp_tot_impm.getText().toString());
                dtot__jcj = Global.StrToFloat(oOp_tot_jcj.getText().toString());
                dtot_cons = Global.StrToFloat(oOp_tot_cons.getText().toString());
                dtot_tecn = Global.StrToFloat(oOp_tot_tenc.getText().toString());
                dtot_devo = Global.StrToFloat(oOp_tot_devo.getText().toString());
                dtot_otro = Global.StrToFloat(oOp_tot_otro.getText().toString());
                dtot_cred = Global.StrToFloat(oOp_tot_cred.getText().toString());
                dSub_tota = Global.StrToFloat(oOp_tot_subt.getText().toString());
                dtot_impu = Global.StrToFloat(oOp_tot_impu.getText().toString());
                dtot_tota = Global.StrToFloat(oOp_tot_tota.getText().toString());
                //-------------------------------------------------------------------------------------------------------//
                dtot_bloc = Global.StrToFloat(oOp_tot_bloc.getText().toString());
                dtot_bemp = Global.StrToFloat(oOp_tot_bemp.getText().toString());
                //-------------------------------------------------------------------------------------------------------//
                dtot_nloc = Global.StrToFloat(oOp_tot_nloc.getText().toString());
                dtot_nemp = Global.StrToFloat(oOp_tot_nemp.getText().toString());

                //dtot_nemp = Double.valueOf(oOp_tot_nemp.getText().toString()).doubleValue();

                ctot_cole = Global.FloatToStrFormat(dtot_cole, 12, 2);
                ctot_timb = Global.FloatToStrFormat(dtot_timb, 12, 2);
                ctot_impm = Global.FloatToStrFormat(dtot_impm, 12, 2);
                ctot__jcj = Global.FloatToStrFormat(dtot__jcj, 12, 2);
                ctot_cons = Global.FloatToStrFormat(dtot_cons, 12, 2);
                ctot_tecn = Global.FloatToStrFormat(dtot_tecn, 12, 2);
                ctot_devo = Global.FloatToStrFormat(dtot_devo, 12, 2);
                ctot_otro = Global.FloatToStrFormat(dtot_otro, 12, 2);
                ctot_cred = Global.FloatToStrFormat(dtot_cred, 12, 2);
                cSub_tota = Global.FloatToStrFormat(dSub_tota, 12, 2);
                ctot_impu = Global.FloatToStrFormat(dtot_impu, 12, 2);
                ctot_tota = Global.FloatToStrFormat(dtot_tota, 12, 2);
                //-------------------------------------------------------------------------------------------------------//
                ctot_bloc = Global.FloatToStrFormat(dtot_bloc, 12, 2);
                ctot_bemp = Global.FloatToStrFormat(dtot_bemp, 12, 2);
                //-------------------------------------------------------------------------------------------------------//
                ctot_nloc = Global.FloatToStrFormat(dtot_nloc, 12, 2);
                ctot_nemp = Global.FloatToStrFormat(dtot_nemp, 12, 2);

                Global.Genobj = new JSONObject();
                try {
                    Global.Genobj.put("tot_cole", dtot_cole);
                    Global.Genobj.put("tot_timb", dtot_timb);
                    Global.Genobj.put("tot_impm", dtot_impm);
                    Global.Genobj.put("dot__jcj", dtot__jcj);
                    Global.Genobj.put("tot_tecn", dtot_tecn);
                    Global.Genobj.put("tot_devo", dtot_devo);
                    Global.Genobj.put("tot_otro", dtot_otro);
                    Global.Genobj.put("tot_cred", dtot_cred);
                    Global.Genobj.put("Sub_tota", dSub_tota);
                    Global.Genobj.put("tot_impu", dtot_impu);
                    Global.Genobj.put("tot_cons", dtot_cons);
                    Global.Genobj.put("tot_tota", dtot_tota);
                    Global.Genobj.put("tot_bloc", dtot_bloc);
                    Global.Genobj.put("tot_bemp", dtot_bemp);
                    Global.Genobj.put("tot_nloc", dtot_nloc);
                    Global.Genobj.put("tot_nemp", dtot_nemp);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                //--------------------CUENTA CUENTOS REGISTROS HAY POR PROCESAR-----------------------------------//
                cSql_Ln = "" +
                        "SELECT " +
                        "   COUNT(op_chapa) AS cnt " +
                        "FROM operacion " +
                        "WHERE (op_emp_id ='" + Global.cEmp_Id + "') " +
                        "AND   (id_device ='" + Global.cid_device + "') " +
                        "AND   (cte_id    ='" + Global.cCte_Id + "') " +
                        "AND   (IFNULL(op_baja_prod,0)=0);";

                oData5 = oDb5.rawQuery(cSql_Ln, null);
                if (oData5.getCount() == 0) {
                } else {
                    oData5.moveToFirst();
                    iRegs_Cnt = oData5.getInt(0);
                    oData5.close();
                }
                //------------------------------------------------------------------------------------------------//

                Date d = new Date();
                SimpleDateFormat simpleDate = new SimpleDateFormat("ddMMyyyy");
                String cDateSerial = simpleDate.format(d);
                String cCorrelativ = String.format("%04d", iRegs_Cnt);
                String cNumDoc = cDateSerial.trim() + cCorrelativ.trim();

                SimpleDateFormat simpleDate2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String cDateMysql = simpleDate2.format(d);

                Log.e("date1", cDateSerial);
                Log.e("date1", cDateMysql);
                Log.e("corre", cNumDoc);

                iId_Group = Global.createID();

                //--------------------ACTUALIZA REGISTRO DE BAJA PRODUCCION---------------------------------------//
                cSql_Ln = "UPDATE operacion SET ";
                cSql_Ln += " op_tot_brutoloc = 0.00,";
                cSql_Ln += " op_tot_brutoemp = 0.00,";
                cSql_Ln += " op_tot_netoloc  = 0.00,";
                cSql_Ln += " op_tot_netoemp  = 0.00,";
                cSql_Ln += " op_tot_timbres  = 0.00,";
                cSql_Ln += " op_tot_impmunic = 0.00,";
                cSql_Ln += " op_tot_impjcj   = 0.00,";
                cSql_Ln += " op_tot_tec      = 0.00,";
                cSql_Ln += " op_tot_dev      = 0.00,";
                cSql_Ln += " op_tot_otros    = 0.00,";
                cSql_Ln += " op_tot_sub      = 0.00,";
                cSql_Ln += " op_tot_itbm     = 0.00,";
                cSql_Ln += " op_tot_tot      = 0.00,";
                cSql_Ln += " op_maq_proc_cons= 0.00,";
                cSql_Ln += " op_tot_porc_cons= 0.00,";
                //cSql_Ln += " op_nodoc        ='" + cNumDoc + "',";
                cSql_Ln += " op_nodoc        ='" + cOp_fact_global.trim() + "'||'-'|| trim(op_chapa),";
                cSql_Ln += " op_fecha        ='" + cDateMysql + "',";
                cSql_Ln += " u_usuario_modif ='TABLET',";
                cSql_Ln += " op_fecha_modif  ='" + cDateMysql + "', ";
                cSql_Ln += " op_usermodify   = 1, ";
                cSql_Ln += " id_group        ='" + Integer.toString(iId_Group) + "' ";
                cSql_Ln += "WHERE (op_emp_id ='" + Global.cEmp_Id + "') ";
                cSql_Ln += "AND   (id_device ='" + Global.cid_device + "') ";
                cSql_Ln += "AND   (cte_id    ='" + Global.cCte_Id + "') ";
                cSql_Ln += "AND   (IFNULL(op_baja_prod,0)=1);";
                Global.logLargeString(cSql_Ln);
                oDb5.execSQL(cSql_Ln);
                //------------------------------------------------------------------------------------------------//

                //----------------ACTUALIZA REGISTRO NORMALES QUE NO SON DE BAJA PRODUCCION----------------------//
                if (iRegs_Cnt > 0) {
                    cRegs_Cnt = Integer.valueOf(iRegs_Cnt).toString().trim();

                    cSql_Ln = "UPDATE operacion SET ";
                    if (dtot_timb == 0.00)
                        cSql_Ln += " op_tot_timbres =0.00, ";
                    else
                        cSql_Ln += " op_tot_timbres =ROUND(" + ctot_timb + "/" + cRegs_Cnt + ",2), ";

                    if (dtot_impm == 0.00)
                        cSql_Ln += " op_tot_impmunic=0.00, ";
                    else
                        cSql_Ln += " op_tot_impmunic=ROUND(" + ctot_impm + "/" + cRegs_Cnt + ",2), ";

                    cSql_Ln += " op_tot_impjcj   =(IFNULL(op_semanas_imp,1)*37.50), ";

                    if (dtot_tecn == 0.00)
                        cSql_Ln += " op_tot_tec     =0.00, ";
                    else
                        cSql_Ln += " op_tot_tec     =ROUND(" + ctot_tecn + "/" + cRegs_Cnt + ",2), ";

                    if (dtot_devo == 0.00)
                        cSql_Ln += " op_tot_dev     =0.00, ";
                    else
                        cSql_Ln += " op_tot_dev     =ROUND(" + ctot_devo + "/" + cRegs_Cnt + ",2), ";

                    if (dtot_otro == 0.00)
                        cSql_Ln += " op_tot_otros   =0.00, ";
                    else
                        cSql_Ln += " op_tot_otros   =ROUND(" + ctot_otro + "/" + cRegs_Cnt + ",2), ";

                    cSql_Ln += " op_tot_sub     = 0.00,";
                    cSql_Ln += " op_tot_itbm    = 0.00,";
                    cSql_Ln += " op_tot_tot     = 0.00,";
                    //cSql_Ln += " op_nodoc       ='" + cNumDoc + "',";
                    cSql_Ln += " op_nodoc        ='" + cOp_fact_global.trim() + "'||'-'|| trim(op_chapa),";
                    cSql_Ln += " op_fecha       ='" + cDateMysql + "',";
                    cSql_Ln += " u_usuario_modif ='TABLET',";
                    cSql_Ln += " op_fecha_modif ='" + cDateMysql + "', ";
                    cSql_Ln += " op_usermodify  =1, ";
                    cSql_Ln += " id_group        ='" + Integer.toString(iId_Group) + "' ";
                    cSql_Ln += "WHERE (op_emp_id ='" + Global.cEmp_Id + "') ";
                    cSql_Ln += "AND   (id_device ='" + Global.cid_device + "') ";
                    cSql_Ln += "AND   (cte_id    ='" + Global.cCte_Id + "') ";
                    cSql_Ln += "AND   (IFNULL(op_baja_prod,0)=0);";
                    Global.logLargeString(cSql_Ln);
                    oDb5.execSQL(cSql_Ln);

                    cSql_Ln = "";
                    cSql_Ln += "UPDATE operacion SET ";
                    cSql_Ln += " op_tot_sub     =ROUND(op_tot_colect - (op_tot_timbres + op_tot_impmunic + op_tot_impjcj + op_tot_tec) - (op_tot_dev + op_tot_otros + op_tot_cred),2),";
                    cSql_Ln += " op_tot_itbm    = 0.00 ";
                    cSql_Ln += "WHERE (op_emp_id ='" + Global.cEmp_Id + "') ";
                    cSql_Ln += "AND   (id_device ='" + Global.cid_device + "') ";
                    cSql_Ln += "AND   (cte_id    ='" + Global.cCte_Id + "') ";
                    cSql_Ln += "AND   (IFNULL(op_baja_prod,0)=0);";
                    Global.logLargeString(cSql_Ln);
                    oDb5.execSQL(cSql_Ln);

                    cSql_Ln = "";
                    cSql_Ln += "UPDATE operacion SET ";
                    cSql_Ln += " op_tot_tot     = ROUND(op_tot_sub - op_tot_itbm,2) ";
                    cSql_Ln += "WHERE (op_emp_id ='" + Global.cEmp_Id + "') ";
                    cSql_Ln += "AND   (id_device ='" + Global.cid_device + "') ";
                    cSql_Ln += "AND   (cte_id    ='" + Global.cCte_Id + "') ";
                    cSql_Ln += "AND   (IFNULL(op_baja_prod,0)=0);";
                    Global.logLargeString(cSql_Ln);
                    oDb5.execSQL(cSql_Ln);

                    Global.qry_cte_info(Global.cCte_Id);
                    String cpCte_Porc = Global.decimalformat(Global.fCte_Por, 5, 2).trim();
                    //----------------------------CALCULO DE BRUTOS-------------------------------------------------//
                    cSql_Ln = "" +
                            "UPDATE operacion SET " +
                            "    op_tot_brutoloc = ROUND( (CASE WHEN (" + cpCte_Porc + "<100) THEN (op_tot_tot * (" + cpCte_Porc + "/100)) ELSE 000000000000.00 END),2) " +
                            "WHERE (op_emp_id ='" + Global.cEmp_Id + "') " +
                            "AND   (id_device ='" + Global.cid_device + "') " +
                            "AND   (cte_id    ='" + Global.cCte_Id + "') " +
                            "AND   (IFNULL(op_baja_prod,0)=0);";
                    Global.logLargeString(cSql_Ln);
                    oDb5.execSQL(cSql_Ln);

                    cSql_Ln = "" +
                            "UPDATE operacion SET " +
                            "   op_tot_brutoemp = ROUND( (CASE WHEN  (" + cpCte_Porc + "<100) THEN (op_tot_tot - op_tot_brutoloc) ELSE (op_tot_tot) END),2) " +
                            "WHERE (op_emp_id ='" + Global.cEmp_Id + "') " +
                            "AND   (id_device ='" + Global.cid_device + "') " +
                            "AND   (cte_id    ='" + Global.cCte_Id + "') " +
                            "AND   (IFNULL(op_baja_prod,0)=0);";
                    Global.logLargeString(cSql_Ln);
                    oDb5.execSQL(cSql_Ln);
                    //----------------------------CALCULO DE NETOS-------------------------------------------------//
                    //dtot_nloc = (dtot_devo + dtot_otro + dtot_cred + dtot_bloc);

                    cSql_Ln = "" +
                            "UPDATE operacion SET " +
                            "   op_tot_netoloc	 = ROUND(op_tot_dev + op_tot_otros + op_tot_cred + op_tot_brutoloc,2) " +
                            "WHERE (op_emp_id ='" + Global.cEmp_Id + "') " +
                            "AND   (id_device ='" + Global.cid_device + "') " +
                            "AND   (cte_id    ='" + Global.cCte_Id + "') " +
                            "AND   (IFNULL(op_baja_prod,0)=0);";
                    Global.logLargeString(cSql_Ln);
                    oDb5.execSQL(cSql_Ln);
                    //dtot_nemp = (dtot_timb + dtot_impm + dtot__jcj + dtot_tecn + dtot_bemp);

                    cSql_Ln = "" +
                            "UPDATE operacion SET " +
                            "   op_tot_netoemp	 = ROUND(op_tot_timbres + op_tot_impmunic + op_tot_impjcj + op_tot_tec + op_tot_brutoemp,2) " +
                            "WHERE (op_emp_id = '" + Global.cEmp_Id + "') " +
                            "AND   (id_device = '" + Global.cid_device + "') " +
                            "AND   (cte_id    = '" + Global.cCte_Id + "') " +
                            "AND   (IFNULL(op_baja_prod,0)=0);";
                    Global.logLargeString(cSql_Ln);
                    oDb5.execSQL(cSql_Ln);
                }

                cSql_Ln = "";
                cSql_Ln += "" +
                        "DELETE FROM operaciong " +
                        "WHERE  (op_emp_id ='" + Global.cEmp_Id + "') " +
                        "AND 	(id_device ='" + Global.cid_device + "') " +
                        "AND 	(cte_id    ='" + Global.cCte_Id + "'); ";
                Global.logLargeString(cSql_Ln);
                oDb5.execSQL(cSql_Ln);

                cSql_Ln = "";
                cSql_Ln += "INSERT INTO operaciong ( ";
                cSql_Ln += "cte_id          ,op_fecha,";
                cSql_Ln += "cte_nombre_loc  ,cte_nombre_com,";
                cSql_Ln += "op_cal_colect   ,op_tot_colect  ,";
                cSql_Ln += "op_tot_impmunic ,op_tot_impjcj  ,";
                cSql_Ln += "op_tot_timbres  ,op_tot_spac    ,";
                cSql_Ln += "op_tot_tec      ,op_tot_dev    ,";
                cSql_Ln += "op_tot_otros    ,op_tot_porc_cons,";
                cSql_Ln += "op_tot_cred     ,op_cal_cred    ,";
                cSql_Ln += "op_tot_sub      ,op_tot_itbm    ,";
                cSql_Ln += "op_tot_tot      ,";
                cSql_Ln += "op_tot_brutoloc ,op_tot_brutoemp,";
                cSql_Ln += "op_tot_netoloc  ,op_tot_netoemp,";
                cSql_Ln += "op_emp_id       ,id_device,";
                cSql_Ln += "id_group        ,op_usermodify,";
                cSql_Ln += "op_fecha_alta   ,op_fecha_modif,";
                cSql_Ln += "op_observ       ,op_fact_global,";
                cSql_Ln += "op_usuario_alta ,op_usuario_modif) VALUES (";
                cSql_Ln += "'" + Global.cCte_Id + "',";
                cSql_Ln += "'" + cDateMysql + "',";
                cSql_Ln += "'" + Global.cCte_De + "',";
                cSql_Ln += "'" + Global.cCte_De + "',";
                cSql_Ln += "'0.00',";
                cSql_Ln += "'" + ctot_cole + "',";
                cSql_Ln += "'" + ctot_impm + "',";
                cSql_Ln += "'" + ctot__jcj + "',";
                cSql_Ln += "'" + ctot_timb + "',";
                cSql_Ln += "'0.00',";
                cSql_Ln += "'" + ctot_tecn + "',";
                cSql_Ln += "'" + ctot_devo + "',";
                cSql_Ln += "'" + ctot_otro + "',";
                cSql_Ln += "'" + ctot_otro + "',";
                cSql_Ln += "'" + ctot_cred + "',";
                cSql_Ln += "'0.00',";
                cSql_Ln += "'" + cSub_tota + "',";
                cSql_Ln += "'" + ctot_impu + "',";
                cSql_Ln += "'" + ctot_tota + "',";
                cSql_Ln += "'" + ctot_bloc + "',";
                cSql_Ln += "'" + ctot_bemp + "',";
                cSql_Ln += "'" + ctot_nloc + "',";
                cSql_Ln += "'" + ctot_nemp + "',";
                cSql_Ln += "'" + Global.cEmp_Id + "',";
                cSql_Ln += "'" + Global.cid_device + "',";
                cSql_Ln += "'" + Integer.toString(iId_Group) + "',";
                cSql_Ln += "'1',";
                cSql_Ln += "'" + cDateMysql + "',";
                cSql_Ln += "'" + cDateMysql + "',";
                cSql_Ln += "'" + ototfin_notas.getText().toString().trim() + "',";
                cSql_Ln += "'" + oOp_fact_global.getText().toString().trim() + "',";
                cSql_Ln += "'TABLET',";
                cSql_Ln += "'TABLET')";
                Global.logLargeString(cSql_Ln);
                oDb5.execSQL(cSql_Ln);

                Global.iPrn_Data = 2;
                Intent Int_PrnMaqScreen = new Intent(getApplicationContext(), print_data.class);
                startActivityForResult(Int_PrnMaqScreen, Global.REQUEST_PRINT);

                oDb5.close();
                //Global.ExportDB();

                finish();
            }
        });

        this.obtn_totfin_canc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Toast.makeText(getApplicationContext(), "regresando:..", Toast.LENGTH_LONG).show();

                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);

                oDb5.close();
                finish();
            }
        });
        /**************************************************************************************************/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GET_PASS) {
            switch (resultCode) {
                case RESULT_OK:
                    int ipass = data.getIntExtra("PASSWORD", -1);
                    Log.e("TAG", Integer.valueOf(ipass).toString());

                    if (ipass == Integer.valueOf(Global.PasswChgAmmout)) {
                        switch (Global.iObj_Select) {
                            case 99:
                                this.oOp_tot_cole.setEnabled(true);
                                this.oOp_tot_cred.setEnabled(true);
                                this.oOp_tot_jcj.setEnabled(true);
                                this.oOp_tot_cons.setEnabled(true);
                                break;
                            case 0:
                                this.oOp_tot_cole.setEnabled(false);
                                this.oOp_tot_cred.setEnabled(false);
                                this.oOp_tot_jcj.setEnabled(false);
                                this.oOp_tot_cons.setEnabled(false);
                                break;
                            case 1:
                                this.oOp_tot_cole.setEnabled(true);
                                this.oOp_tot_cole.selectAll();
                                this.oOp_tot_cole.requestFocus();
                                break;
                            case 2:
                                this.oOp_tot_cred.setEnabled(true);
                                this.oOp_tot_cred.selectAll();
                                this.oOp_tot_cred.requestFocus();
                                break;
                            case 3:
                                this.oOp_tot_jcj.setEnabled(true);
                                this.oOp_tot_jcj.selectAll();
                                this.oOp_tot_jcj.requestFocus();
                                break;
                            case 4:
                                this.oOp_tot_cons.setEnabled(true);
                                this.oOp_tot_cons.selectAll();
                                this.oOp_tot_cons.requestFocus();
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
                            this.oOp_tot_cole.setEnabled(false);
                            this.oOp_tot_cred.setEnabled(false);
                            this.oOp_tot_jcj.setEnabled(false);
                            this.oOp_tot_cons.setEnabled(false);
                            break;
                        case 1:
                            this.oOp_tot_cole.setEnabled(false);
                            break;
                        case 2:
                            this.oOp_tot_cred.setEnabled(false);
                            break;
                        case 3:
                            this.oOp_tot_jcj.setEnabled(false);
                            break;
                        case 4:
                            this.oOp_tot_cons.setEnabled(false);
                            break;
                    }
                    Toast.makeText(this, "Canceló la operación.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void Clear_Screen() {
        this.oOp_tot_cole.setText("0.00");
        this.oOp_tot_cole.setEnabled(false);

        this.oOp_tot_timb.setText("0.00");
        this.oOp_tot_timb.setEnabled(true);

        this.oOp_tot_impm.setText("0.00");
        this.oOp_tot_impm.setEnabled(true);

        this.oOp_tot_jcj.setText("0.00");
        this.oOp_tot_jcj.setEnabled(false);

        this.oOp_tot_cons.setText("0.00");
        this.oOp_tot_cons.setEnabled(false);

        this.oOp_tot_tenc.setText("0.00");
        this.oOp_tot_tenc.setEnabled(true);

        this.oOp_tot_devo.setText("0.00");
        this.oOp_tot_devo.setEnabled(true);

        this.oOp_tot_otro.setText("0.00");
        this.oOp_tot_otro.setEnabled(true);

        this.oOp_tot_cred.setText("0.00");
        this.oOp_tot_cred.setEnabled(false);

        this.oOp_tot_subt.setText("0.00");
        this.oOp_tot_subt.setEnabled(false);

        this.oOp_tot_impu.setText("0.00");
        this.oOp_tot_impu.setEnabled(false);

        this.oOp_tot_tota.setText("0.00");
        this.oOp_tot_tota.setEnabled(false);

        this.oOp_tot_bloc.setText("0.00");
        this.oOp_tot_bloc.setEnabled(false);

        this.oOp_tot_bemp.setText("0.00");
        this.oOp_tot_bemp.setEnabled(false);

        this.oOp_tot_nloc.setText("0.00");
        this.oOp_tot_nloc.setEnabled(false);

        this.oOp_tot_nemp.setText("0.00");
        this.oOp_tot_nemp.setEnabled(false);

        this.ototfin_notas.setText("");
    }

    private void Find_Values() {
        String cSql_Ln = "";

        cSql_Ln = "";
        cSql_Ln += "SELECT ";
        cSql_Ln += "    SUM(op_tot_colect) AS op_tot_colect, ";
        cSql_Ln += "    SUM(op_tot_cred) AS op_tot_cred, ";
        cSql_Ln += "    SUM(op_semanas_imp)*37.50 AS op_tot_jcj, ";
        cSql_Ln += "    SUM(op_tot_porc_cons) AS op_tot_porc_cons ";
        cSql_Ln += "FROM operacion ";
        cSql_Ln += "WHERE (op_emp_id ='" + Global.cEmp_Id + "') ";
        cSql_Ln += "AND   (id_device ='" + Global.cid_device + "') ";
        cSql_Ln += "AND   (cte_id    ='" + Global.cCte_Id + "') ";
        cSql_Ln += "AND   (IFNULL(op_baja_prod,0) =0); ";

        Log.e("SQL", cSql_Ln);
        oData5 = oDb5.rawQuery(cSql_Ln, null);
        oData5.moveToFirst();

        if ((oData5 == null) || (oData5.getCount() == 0)) {
            this.oOp_tot_cole.setText("0.00");
            this.oOp_tot_cred.setText("0.00");
            this.oOp_tot_jcj.setText("0.00");
            this.oOp_tot_cons.setText("0.00");
        } else {

            Double ftotfin_bruto = oData5.getDouble(oData5.getColumnIndex("op_tot_colect"));
            Double ftotfin_prem = oData5.getDouble(oData5.getColumnIndex("op_tot_cred"));
            Double dtot_imp_jcj = oData5.getDouble(oData5.getColumnIndex("op_tot_jcj"));
            Double dtot_consecion = oData5.getDouble(oData5.getColumnIndex("op_tot_porc_cons"));

            String ctotfin_bruto = Global.FloatToStrFormat(ftotfin_bruto, 12, 2);
            String ctotfin_prem = Global.FloatToStrFormat(ftotfin_prem, 12, 2);
            String ctot_imp_jcj = Global.FloatToStrFormat(dtot_imp_jcj, 12, 2);
            String ctot_consecion = Global.FloatToStrFormat(dtot_consecion, 12, 2);

            this.oOp_tot_cole.setText(ctotfin_bruto);
            this.oOp_tot_cred.setText(ctotfin_prem);
            this.oOp_tot_jcj.setText(ctot_imp_jcj);
            this.oOp_tot_cons.setText(ctot_consecion);
        }
        oData5.close();
    }

    private void Valid_Data() {
        if (oOp_tot_cole.getText().toString().length() == 0) {
            oOp_tot_cole.setError(null);
            oOp_tot_cole.setText("0.00");
        }

        if (oOp_tot_timb.getText().toString().length() == 0) {
            oOp_tot_timb.setError(null);
            oOp_tot_timb.setText("0.00");
        }

        if (oOp_tot_impm.getText().toString().length() == 0) {
            oOp_tot_impm.setError(null);
            oOp_tot_impm.setText("0.00");
        }

        if (oOp_tot_jcj.getText().toString().length() == 0) {
            oOp_tot_jcj.setError(null);
            oOp_tot_jcj.setText("0.00");
        }

        if (oOp_tot_cons.getText().toString().length() == 0) {
            oOp_tot_cons.setError(null);
            oOp_tot_cons.setText("0.00");
        }

        if (oOp_tot_tenc.getText().toString().length() == 0) {
            oOp_tot_tenc.setError(null);
            oOp_tot_tenc.setText("0.00");
        }

        if (oOp_tot_devo.getText().toString().length() == 0) {
            oOp_tot_devo.setError(null);
            oOp_tot_devo.setText("0.00");
        }

        if (oOp_tot_otro.getText().toString().length() == 0) {
            oOp_tot_otro.setError(null);
            oOp_tot_otro.setText("0.00");
        }

        if (oOp_tot_cred.getText().toString().length() == 0) {
            oOp_tot_cred.setError(null);
            oOp_tot_cred.setText("0.00");
        }

        if (oOp_tot_subt.getText().toString().length() == 0) {
            oOp_tot_subt.setError(null);
            oOp_tot_subt.setText("0.00");
        }

        if (oOp_tot_impu.getText().toString().length() == 0) {
            oOp_tot_impu.setError(null);
            oOp_tot_impu.setText("0.00");
        }

        if (oOp_tot_tota.getText().toString().length() == 0) {
            oOp_tot_tota.setError(null);
            oOp_tot_tota.setText("0.00");
        }

        if (oOp_tot_bloc.getText().toString().length() == 0) {
            oOp_tot_bloc.setError(null);
            oOp_tot_bloc.setText("0.00");
        }

        if (oOp_tot_bemp.getText().toString().length() == 0) {
            oOp_tot_bemp.setError(null);
            oOp_tot_bemp.setText("0.00");
        }

        if (oOp_tot_nloc.getText().toString().length() == 0) {
            oOp_tot_nloc.setError(null);
            oOp_tot_nloc.setText("0.00");
        }

        if (oOp_tot_nemp.getText().toString().length() == 0) {
            oOp_tot_nemp.setError(null);
            oOp_tot_nemp.setText("0.00");
        }

        if (ototfin_notas.getText().toString().length() == 0) {
            ototfin_notas.setError(null);
            ototfin_notas.setHint("Sin Comentarios");
        }
    }

    private boolean Buscar_Cte(String pId_Cte) {
        String cSqlLn = "";
        SQLiteDatabase oDb8;
        Cursor oData8;
        String databasePath = getDatabasePath("one2009.db").getPath();
        oDb8 = io.requery.android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(databasePath, null, null);

        cSqlLn += "SELECT ";
        cSqlLn += "  clientes.cte_id, ";
        cSqlLn += "  clientes.cte_nombre_loc, ";
        cSqlLn += "  clientes.cte_nombre_com, ";
        cSqlLn += "  clientes.cte_inactivo, ";
        cSqlLn += "  clientes.cte_pag_jcj, ";
        cSqlLn += "  clientes.cte_pag_impm, ";
        cSqlLn += "  clientes.cte_pag_spac, ";
        cSqlLn += "  clientes.cte_poc_ret, ";
        cSqlLn += "  clientes.mun_id, ";
        cSqlLn += "  clientes.rut_id ";
        cSqlLn += "FROM clientes ";
        cSqlLn += "WHERE cte_id ='" + pId_Cte + "' ";
        Log.d("SQL", cSqlLn);

        oData8 = oDb8.rawQuery(cSqlLn, null);

        if ((oData8 == null) || (oData8.getCount() == 0)) {
            Global.showSimpleOKAlertDialog(oThis, "AVISO IMPORTANTE", "El código de máquina no existe o no tiene un cliente asignado.");
            oData8.close();
            return false;
        } else {
            oData8.moveToFirst();
            if (oData8.getDouble(oData8.getColumnIndex("cte_poc_ret")) == 0)
                this.fPorc_Loc = 1;
            else
                this.fPorc_Loc = oData8.getDouble(oData8.getColumnIndex("cte_poc_ret"));
            oData8.close();
            return true;
        }
    }

    private void Calc_Sub_Tot() {
        double ipK_porc = 0.00;

        Double dtot_cole = 0.00;
        Double dtot_timb = 0.00;
        Double dtot_impm = 0.00;
        Double dtot__jcj = 0.00;
        Double dtot_cons = 0.00;
        Double dtot_tecn = 0.00;
        Double dtot_cred = 0.00;
        Double dtot_otro = 0.00;
        Double dtot_devo = 0.00;
        Double dSub_tota = 0.00;
        Double dtot_impu = 0.00;
        Double dtot_tota = 0.00;
        Double dtot_bloc = 0.00;
        Double dtot_bemp = 0.00;
        Double dtot_nloc = 0.00;
        Double dtot_nemp = 0.00;
        Double dtot_sum1 = 0.00;
        Double dtot_Imp0 = 0.00;
        Double dtot_sum2 = 0.00;

        String stot_cole = (this.oOp_tot_cole.getText().toString().isEmpty() ? "0" : this.oOp_tot_cole.getText().toString());
        String stot_timb = (this.oOp_tot_timb.getText().toString().isEmpty() ? "0" : this.oOp_tot_timb.getText().toString());
        String stot_impm = (this.oOp_tot_impm.getText().toString().isEmpty() ? "0" : this.oOp_tot_impm.getText().toString());
        String stot__jcj = (this.oOp_tot_jcj.getText().toString().isEmpty() ? "0" : this.oOp_tot_jcj.getText().toString());
        String stot_cons = (this.oOp_tot_cons.getText().toString().isEmpty() ? "0" : this.oOp_tot_cons.getText().toString());
        String stot_tecn = (this.oOp_tot_tenc.getText().toString().isEmpty() ? "0" : this.oOp_tot_tenc.getText().toString());
        String stot_cred = (this.oOp_tot_cred.getText().toString().isEmpty() ? "0" : this.oOp_tot_cred.getText().toString());
        String stot_devo = (this.oOp_tot_devo.getText().toString().isEmpty() ? "0" : this.oOp_tot_devo.getText().toString());
        String stot_otro = (this.oOp_tot_otro.getText().toString().isEmpty() ? "0" : this.oOp_tot_otro.getText().toString());

        String cSub_tota = "";
        String ctot_tota = "";
        String ctot_bloc = "";
        String ctot_bemp = "";
        String ctot_nloc = "";
        String ctot_nemp = "";

        //-------------------------------------------------------------------------------------------------------//
        dtot_cole = Global.StrToFloat(stot_cole);
        dtot_timb = Global.StrToFloat(stot_timb);
        dtot_impm = Global.StrToFloat(stot_impm);
        dtot__jcj = Global.StrToFloat(stot__jcj);
        dtot_cons = Global.StrToFloat(stot_cons);
        dtot_tecn = Global.StrToFloat(stot_tecn);

        dtot_Imp0 = (dtot_timb + dtot_impm + dtot__jcj + dtot_cons);

        dtot_cred = Global.StrToFloat(stot_cred);
        dtot_devo = Global.StrToFloat(stot_devo);
        dtot_otro = Global.StrToFloat(stot_otro);
        dtot_sum2 = (dtot_devo + dtot_otro + dtot_cred);
        dtot_sum1 = (dtot_Imp0 + dtot_tecn);
        //-------------------------------------------------------------------------------------------------------//
        //-------------------------------------------------------------------------------------------------------//
        //-------------------------------------------------------------------------------------------------------//
        dSub_tota = (dtot_cole - dtot_sum1 - dtot_sum2);
        cSub_tota = Global.FloatToStrFormat(dSub_tota, 12, 2);
        //-------------------------------------------------------------------------------------------------------//
        this.oOp_tot_subt.setText(cSub_tota);

        dtot_impu = Double.valueOf(this.oOp_tot_impu.getText().toString()).doubleValue();

        dtot_tota = dSub_tota - dtot_impu;
        ctot_tota = Global.FloatToStrFormat(dtot_tota, 12, 2);
        this.oOp_tot_tota.setText(ctot_tota);

        //ipK_porc =( (fPorc_Loc == 0.00) ? 1.00 : fPorc_Loc);
        ipK_porc = fPorc_Loc;

        if (ipK_porc == 100) {
            dtot_bloc = 0.00;
            dtot_bemp = dtot_tota;

            ctot_bloc = Global.FloatToStrFormat(dtot_bloc, 12, 2);
            ctot_bemp = Global.FloatToStrFormat(dtot_bemp, 12, 2);

            this.oOp_tot_bloc.setText(ctot_bloc);
            this.oOp_tot_bemp.setText(ctot_bemp);
        } else {
            dtot_bloc = (dtot_tota * (ipK_porc / 100));
            dtot_bemp = dtot_tota - dtot_bloc;

            ctot_bloc = Global.FloatToStrFormat(dtot_bloc, 12, 2);
            ctot_bemp = Global.FloatToStrFormat(dtot_bemp, 12, 2);

            this.oOp_tot_bloc.setText(ctot_bloc);
            this.oOp_tot_bemp.setText(ctot_bemp);
        }

        dtot_nloc = (dtot_devo + dtot_otro + dtot_cred + dtot_bloc);
        ctot_nloc = Global.FloatToStrFormat(dtot_nloc, 12, 2);

        dtot_nemp = (dtot_Imp0 + dtot_tecn + dtot_bemp);
        ctot_nemp = Global.FloatToStrFormat(dtot_nemp, 12, 2);

        this.oOp_tot_nloc.setText(ctot_nloc);
        this.oOp_tot_nemp.setText(ctot_nemp);
    }

}