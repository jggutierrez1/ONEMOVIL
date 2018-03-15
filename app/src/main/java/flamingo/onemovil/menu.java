package flamingo.onemovil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class menu extends AppCompatActivity {

    private Button btn_upd, btn_emp, btn_cte, btn_maq, btn_capt, btn_send, btn_exit, btn_ccol, btn_fcol;
    private ImageView mImageView;
    private TextView DeviceId, lempresa;

    private SQLiteDatabase db;
    private String cSql_Ln;
    private String mCurrentPhotoPath;

    private final static int REQUEST_SEL_EMP = 5;
    private final static int REQUEST_SEL_CTE = 1;
    private final static int REQUEST_SEL_MAQ = 2;
    private final static int REQUEST_INI_CAP = 3;
    private final static int REQUEST_END_CAP = 4;
    private final static int REQUEST_DEL_CAP = 6;

    private int iGlobalRes = 0;
    boolean exist = false;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    // will show the statuses like bluetooth open, close or data sent
    TextView myLabel;

    // will enable user to enter any text to be printed
    EditText myTextbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        Locale.setDefault(new Locale("en", "US"));

        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File appFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "fotos_metros");
            exist = appFolder.exists();
        }

        btn_upd = (Button) findViewById(R.id.obtn_upd);
        btn_emp = (Button) findViewById(R.id.obtn_emp);
        btn_cte = (Button) findViewById(R.id.obtn_cte);
        btn_maq = (Button) findViewById(R.id.obtn_maq);
        btn_capt = (Button) findViewById(R.id.obtn_capt);
        btn_send = (Button) findViewById(R.id.obtn_send);

        btn_ccol = (Button) findViewById(R.id.obtn_canc_colec);
        btn_fcol = (Button) findViewById(R.id.obtn_fin_colec);
        btn_exit = (Button) findViewById(R.id.obtn_exit);

        DeviceId = (TextView) findViewById(R.id.oDeviceId);
        DeviceId.setText("ID EQUIPO:" + Global.cid_device.toUpperCase());
        myLabel = (TextView) findViewById(R.id.oLabel);
        myTextbox = (EditText) findViewById(R.id.oEntry);
        lempresa = (TextView) findViewById(R.id.olempresa);
        lempresa.setText(Global.Query_Result("SELECT emp_descripcion FROM empresas WHERE emp_id='" + Global.cEmp_Id + "'", "emp_descripcion"));

        createDatabase();
        Global.Create_Sql_Tables(false, false);

        Global.cEmp_Id = "";
        //Create_Sql_Tables();
        this.Color_Sig_Paso(0);

        btn_upd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent Int_SyncScreen = new Intent(getApplicationContext(), sync_data.class);
                startActivity(Int_SyncScreen);
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);

                db.close();
                finish();
                System.exit(0);
            }
        });

        btn_emp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Int_EmpScreen = new Intent(getApplicationContext(), select_emp.class);
                startActivityForResult(Int_EmpScreen, REQUEST_SEL_EMP);
            }
        });

        btn_cte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.cEmp_Id == "") {
                    Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR UNA EMPRESA PRIMERO.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent Int_CteScreen = new Intent(getApplicationContext(), select_cte.class);
                startActivityForResult(Int_CteScreen, REQUEST_SEL_CTE);
            }
        });

        btn_maq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.cEmp_Id == "") {
                    Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR UNA EMPRESA PRIMERO.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Global.cCte_Id == "") {
                    Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR UN CLIENTE PRIMERO.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent Int_MaqScreen = new Intent(getApplicationContext(), select_maq.class);
                startActivityForResult(Int_MaqScreen, REQUEST_SEL_MAQ);
            }
        });

        btn_capt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.cEmp_Id == "") {
                    Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR UNA EMPRESA PRIMERO.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Global.cCte_Id == "") {
                    Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR UN CLIENTE PRIMERO.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Global.cMaq_Id == "") {
                    Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR UNA MAQUINA RIMERO.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Global.iObj_Select = 0;
                Intent Int_CapScreen = new Intent(getApplicationContext(), capt_data.class);
                startActivityForResult(Int_CapScreen, REQUEST_INI_CAP);
            }
        });

        btn_fcol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.cEmp_Id == "") {
                    Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR UNA EMPRESA PRIMERO.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Global.cCte_Id == "") {
                    Toast.makeText(getApplicationContext(), "DEBE SELECCIONAR UN CLIENTE PRIMERO.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Global.iObj_Select = 0;
                Intent Capf_MaqScreen = new Intent(getApplicationContext(), capt_fin.class);
                startActivityForResult(Capf_MaqScreen, REQUEST_END_CAP);


                Toast.makeText(getApplicationContext(), "FINALIZO.", Toast.LENGTH_SHORT).show();
            }
        });

        btn_ccol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Global.iObj_Select = 0;
                Global.DialogConfirmText = "ESTA SEGURO QUE DESEA BORRA LOS DATOS DE TODAS LAS COLECTAS REALIZADAS?";
                Intent BorrarColectScreen = new Intent(getApplicationContext(), borrar_colectas.class);
                startActivityForResult(BorrarColectScreen, REQUEST_DEL_CAP);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cJsonResult = null;
                String Sql_Ln = "";
                String PEMP_ID = "";

                Sql_Ln = "" +
                        "SELECT " +
                        "IFNULL(cte_id         ,' ')  AS cte_id, " +
                        "IFNULL(cte_nombre_loc ,' ')  AS cte_nombre_loc, " +
                        "IFNULL(cte_nombre_com ,' ')  AS cte_nombre_com, " +
                        "IFNULL(op_cporc_Loc   ,0.00) AS op_cporc_Loc  , " +
                        "IFNULL(cte_pag_jcj    ,0)    AS cte_pag_jcj   , " +
                        "IFNULL(cte_pag_spac   ,0)    AS cte_pag_spac  , " +
                        "IFNULL(cte_pag_impm   ,0)    AS cte_pag_impm  , " +
                        "IFNULL(maqtc_denom_e  ,0)    AS maqtc_denom_e , " +
                        "IFNULL(maqtc_denom_s  ,0)    AS maqtc_denom_s , " +
                        "IFNULL(den_valore     ,0.00) AS den_valore    , " +
                        "IFNULL(den_valors     ,0.00) AS den_valors    , " +
                        "IFNULL(den_fact_e     ,0)    AS den_fact_e    , " +
                        "IFNULL(den_fact_s     ,0)    AS den_fact_s    , " +
                        "IFNULL(MaqLnk_Id      ,0)    AS MaqLnk_Id     , " +
                        "IFNULL(maqtc_tipomaq  ,0)    AS maqtc_tipomaq , " +
                        "IFNULL(op_serie       ,0)    AS op_serie      , " +
                        "IFNULL(op_chapa       ,' ')  AS op_chapa      , " +
                        "IFNULL(op_modelo      ,' ')  AS op_modelo     , " +
                        "IFNULL(op_fecha,date('now')) AS op_fecha    , " +
                        "IFNULL(op_e_pantalla  ,0)    AS op_e_pantalla , " +
                        "IFNULL(op_ea_metroan  ,0)    AS op_ea_metroan , " +
                        "IFNULL(op_ea_metroac  ,0)    AS op_ea_metroac , " +
                        "IFNULL(op_ea_met      ,0)    AS op_ea_met     , " +
                        "IFNULL(op_sa_metroan  ,0)    AS op_sa_metroan , " +
                        "IFNULL(op_sa_metroac  ,0)    AS op_sa_metroac , " +
                        "IFNULL(op_sa_met      ,0)    AS op_sa_met     , " +
                        "IFNULL(op_eb_metroan  ,0)    AS op_eb_metroan , " +
                        "IFNULL(op_eb_metroac  ,0)    AS op_eb_metroac , " +
                        "IFNULL(op_eb_met      ,0)    AS op_eb_met     , " +
                        "IFNULL(op_sb_metroan  ,0)    AS op_sb_metroan , " +
                        "IFNULL(op_sb_metroac  ,0)    AS op_sb_metroac , " +
                        "IFNULL(op_sb_met      ,0)    AS op_sb_met     , " +
                        "IFNULL(op_s_pantalla  ,0)    AS op_s_pantalla , " +
                        "IFNULL(op_cal_colect  ,0.00) AS op_cal_colect , " +
                        "IFNULL(op_tot_colect  ,0.00) AS op_tot_colect , " +
                        "IFNULL(op_tot_cred    ,0.00) AS op_tot_cred   , " +
                        "IFNULL(op_cal_cred    ,0.00) AS op_cal_cred   , " +
                        "IFNULL(op_tot_brutoloc ,0.00) AS op_tot_brutoloc, " +
                        "IFNULL(op_tot_brutoemp,0.00) AS op_tot_brutoemp, " +
                        "IFNULL(op_tot_netoloc ,0.00) AS op_tot_netoloc, " +
                        "IFNULL(op_tot_netoemp ,0.00) AS op_tot_netoemp, " +
                        "IFNULL(op_tot_timbres ,0.00) AS op_tot_timbres, " +
                        "IFNULL(op_tot_impmunic,0.00) AS op_tot_impmunic, " +
                        "IFNULL(op_tot_impjcj  ,0.00) AS op_tot_impjcj , " +
                        "IFNULL(op_tot_tec     ,0.00) AS op_tot_tec    , " +
                        "IFNULL(op_tot_dev     ,0.00) AS op_tot_dev    , " +
                        "IFNULL(op_tot_otros   ,0.00) AS op_tot_otros  , " +
                        "IFNULL(op_tot_sub     ,0.00) AS op_tot_sub    , " +
                        "IFNULL(op_tot_itbm    ,0.00) AS op_tot_itbm   , " +
                        "IFNULL(op_tot_tot     ,0.00) AS op_tot_tot    , " +
                        "IFNULL(op_fecha_alta ,date('now')) AS op_fecha_alta, " +
                        "IFNULL(op_fecha_modif,date('now')) AS op_fecha_modif, " +
                        "IFNULL(op_emp_id,0)          AS op_emp_id     , " +
                        "IFNULL(id_device,' ')        AS id_device     , " +
                        "IFNULL(op_semanas_imp,1)     AS op_semanas_imp, " +
                        "IFNULL(op_nodoc,' ')         AS op_nodoc      , " +
                        "IFNULL(op_baja_prod,0)       AS op_baja_prod  , " +
                        "IFNULL(op_image_name,' ')    AS op_image_name , " +
                        "IFNULL(op_usermodify,0)      AS op_usermodify , " +
                        "IFNULL(id_group,'0')         AS id_group        " +
                        "FROM operacion " +
                        "WHERE id_device   ='" + Global.cid_device + "' " +
                        "AND  op_usermodify='1' ";

                Global.logLargeString(Sql_Ln);

                cJsonResult = Global.getJsonResults2(Sql_Ln, "operacion");
                Global.logLargeString(Sql_Ln);
                Global.logLargeString(cJsonResult);

                String cParsString;
                String script;

                cParsString = "";
                cParsString += "dbname=one2009_1";
                cParsString += "&device=" + Global.cid_device;
                cParsString += "&Json=" + cJsonResult;
                cParsString += "&optn=1";
                script = Global.gen_execute_post(Global.SERVER_URL, "/flam/subir_datos.php", cParsString);
                Global.logLargeString(script);
                if (script.contentEquals("1")) {
                    Toast.makeText(getApplicationContext(), "LOS DATOS SE SUBIERON CORRECTAMENTE.", Toast.LENGTH_SHORT).show();
                    String cSql_Ln = "DELETE FROM operacion " +
                            "WHERE id_device ='" + Global.cid_device + "' " +
                            "AND   op_emp_id ='" + PEMP_ID + "' ";
                    db.execSQL(cSql_Ln);
                } else {
                    Toast.makeText(getApplicationContext(), "NO SE PUDIERON SUBIR LOS DATOS CORRECTAMENTE.", Toast.LENGTH_SHORT).show();
                }

                Sql_Ln = "" +
                        "SELECT " +
                        "IFNULL(cte_id         ,' ')  AS cte_id, " +
                        "IFNULL(cte_nombre_loc ,' ')  AS cte_nombre_loc, " +
                        "IFNULL(cte_nombre_com ,' ')  AS cte_nombre_com, " +
                        "IFNULL(op_cal_colect  ,0.00) AS op_cal_colect , " +
                        "IFNULL(op_tot_colect  ,0.00) AS op_tot_colect , " +
                        "IFNULL(op_tot_cred    ,0.00) AS op_tot_cred   , " +
                        "IFNULL(op_cal_cred    ,0.00) AS op_cal_cred   , " +
                        "IFNULL(op_tot_brutoloc ,0.00) AS op_tot_brutoloc, " +
                        "IFNULL(op_tot_brutoemp,0.00) AS op_tot_brutoemp, " +
                        "IFNULL(op_tot_netoloc ,0.00) AS op_tot_netoloc, " +
                        "IFNULL(op_tot_netoemp ,0.00) AS op_tot_netoemp, " +
                        "IFNULL(op_tot_timbres ,0.00) AS op_tot_timbres, " +
                        "IFNULL(op_tot_spac ,0.00) AS op_tot_spac, " +
                        "IFNULL(op_tot_impmunic,0.00) AS op_tot_impmunic, " +
                        "IFNULL(op_tot_impjcj  ,0.00) AS op_tot_impjcj , " +
                        "IFNULL(op_tot_tec     ,0.00) AS op_tot_tec    , " +
                        "IFNULL(op_tot_dev     ,0.00) AS op_tot_dev    , " +
                        "IFNULL(op_tot_otros   ,0.00) AS op_tot_otros  , " +
                        "IFNULL(op_tot_sub     ,0.00) AS op_tot_sub    , " +
                        "IFNULL(op_tot_itbm    ,0.00) AS op_tot_itbm   , " +
                        "IFNULL(op_tot_tot     ,0.00) AS op_tot_tot    , " +
                        "IFNULL(op_fecha_alta ,date('now')) AS op_fecha_alta, " +
                        "IFNULL(op_fecha_modif,date('now')) AS op_fecha_modif, " +
                        "IFNULL(op_emp_id,0)          AS op_emp_id     , " +
                        "IFNULL(id_device,' ')        AS id_device     , " +
                        "IFNULL(id_group,'0')         AS id_group     , " +
                        "IFNULL(op_usermodify,0)      AS op_usermodify   " +
                        "FROM operaciong " +
                        "WHERE id_device   ='" + Global.cid_device + "' " +
                        "AND  op_usermodify='1' ";

                Global.logLargeString(Sql_Ln);

                cJsonResult = Global.getJsonResults2(Sql_Ln, "operaciong");
                Global.logLargeString(Sql_Ln);
                Global.logLargeString(cJsonResult);

                cParsString = "";
                cParsString += "dbname=one2009_1";
                cParsString += "&device=" + Global.cid_device;
                cParsString += "&Json=" + cJsonResult;
                cParsString += "&optn=2";
                script = Global.gen_execute_post(Global.SERVER_URL, "/flam/subir_datos_v2.php", cParsString);
                Global.logLargeString(script);
                if (script.contentEquals("1")) {
                    Toast.makeText(getApplicationContext(), "LOS DATOS SE SUBIERON CORRECTAMENTE.", Toast.LENGTH_SHORT).show();
                    String cSql_Ln = "DELETE FROM operaciong " +
                            "WHERE id_device ='" + Global.cid_device + "' " +
                            "AND   op_emp_id ='" + PEMP_ID + "' ";
                    db.execSQL(cSql_Ln);
                } else {
                    Toast.makeText(getApplicationContext(), "NO SE PUDIERON SUBIR LOS DATOS CORRECTAMENTE.", Toast.LENGTH_SHORT).show();
                }

                cSql_Ln = "" +
                        "SELECT IFNULL(op_image_name,'') AS op_image_name " +
                        "FROM operacion " +
                        "WHERE id_device ='" + Global.cid_device + "' " +
                        "AND   op_emp_id ='" + PEMP_ID + "' " +
                        "AND   IFNULL(op_image_name,'')<>''";
/*
                Global.Genobj = null;
                Global.Genobj = new JSONObject();

                Cursor oCur_Im = db.rawQuery(cSql_Ln, null);
                oCur_Im.moveToFirst();

                do {
                    try {
                        Global.Genobj.put("img", Global.cStorageDirectoryPhoto + "" + oCur_Im.getString(0));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } while (oCur_Im.moveToNext());
*/
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2

        {

            if (requestCode == REQUEST_DEL_CAP) {
                switch (resultCode) {
                    case RESULT_OK:

                        Global.DialogConfirmText = "";

                        String cSql_Ln = "DELETE FROM operacion WHERE id_device='" + Global.cid_device + "'";
                        db.execSQL(cSql_Ln);
                        Toast.makeText(getApplicationContext(), "LOS DATOS FUERON ELIMINADOS.", Toast.LENGTH_SHORT).show();

                        break;
                    case RESULT_CANCELED:
                        Global.DialogConfirmText = "";

                        Toast.makeText(this, "OPCION CANCELADA POR EL USUARIO.", Toast.LENGTH_SHORT).show();
                        break;
                }

            }

            if (requestCode == REQUEST_SEL_EMP) {
                //Valida la seleccion del cliente.
                switch (resultCode) {
                    case RESULT_OK:
                        //Toast.makeText(this, "Aceptó la empresa:[" + Global.cEmp_Id.trim() + "]/" + Global.cEmp_De.trim(), Toast.LENGTH_SHORT).show();
                        //btn_emp.setEnabled(false);

                        this.btn_cte.setEnabled(true);

                        this.lempresa.setText(Global.cEmp_De);
                        this.btn_emp.setText("INICIAR COLECTA [" + Global.cEmp_De + "]");
                        this.btn_cte.setText("SELECCIONAR CLIENTE [?]");
                        this.btn_maq.setText("SELECCIONAR MAQUINA [?]");

                        this.Color_Sig_Paso(1);
                        //Pasa a seleccionar el cliente
                        this.Mostrat_Status_Subir();

                        break;
                    case RESULT_CANCELED:

                        Global.cEmp_Id = "";
                        Global.cEmp_De = "";
                        this.btn_cte.setEnabled(false);

                        //Toast.makeText(this, "Rechazó la empresa.", Toast.LENGTH_SHORT).show();
                        this.btn_emp.setText("INICIAR COLECTA [?]");
                        this.btn_cte.setText("SELECCIONAR CLIENTE [?]");
                        this.btn_maq.setText("SELECCIONAR MAQUINA [?]");

                        this.Color_Sig_Paso(0);
                        //Pasa a seleccionar la empresa

                        break;
                }
            }

            //Se no ha habido fallo:
            if (requestCode == REQUEST_SEL_CTE) {
                //Valida la seleccion del cliente.
                switch (resultCode) {
                    case RESULT_OK:
                        //Toast.makeText(this, "Aceptó el cliente:[" + Global.cCte_Id.trim() + "]/" + Global.cCte_De.trim(), Toast.LENGTH_SHORT).show();
                        //btn_emp.setEnabled(false);

                        this.btn_maq.setEnabled(true);
                        this.btn_capt.setEnabled(true);
                        this.btn_ccol.setEnabled(true);
                        this.btn_fcol.setEnabled(true);

                        this.btn_cte.setText("CLIENTE [" + Global.cCte_De + "]");
                        this.btn_fcol.setText("FINALIZAR COLECTA [" + Global.cCte_De + "]");

                        this.Mostrat_Colectas();

                        //Pasa a seleccionar la maquina
                        this.Color_Sig_Paso(2);
                        this.Mostrat_Status_Subir();

                        break;
                    case RESULT_CANCELED:

                        Global.cCte_Id = "";
                        Global.cCte_De = "";

                        //Toast.makeText(this, "Rechazó el cliente.", Toast.LENGTH_SHORT).show();

                        this.btn_maq.setEnabled(false);
                        this.btn_capt.setEnabled(false);
                        this.btn_ccol.setEnabled(false);
                        this.btn_fcol.setEnabled(false);

                        this.Mostrat_Colectas();

                        //Pasa a seleccionar el cliente
                        this.Color_Sig_Paso(2);

                        break;
                }
            }

            if (requestCode == REQUEST_SEL_MAQ) {
                //Se procesa la devolución
                switch (resultCode) {
                    case RESULT_OK:
                        //Toast.makeText(this, "Aceptó la máquina: [" + Global.cMaq_Id + "]/[" + Global.cMaq_De + "]", Toast.LENGTH_SHORT).show();
                        this.btn_maq.setText("MAQUINA: [" + Global.cMaq_Id + "]/[" + Global.cMaq_De + "]");
                        this.Mostrat_Colectas();

                        //Pasa a seleccionar finalizar colecta
                        this.Color_Sig_Paso(3);
                        this.Mostrat_Colectas();
                        this.Mostrat_Status_Subir();

                        break;
                    case RESULT_CANCELED:
                        Global.cMaq_Id = "";
                        Global.cMaq_De = "";

                        //Toast.makeText(this, "Rechazó la máquina.", Toast.LENGTH_SHORT).show();
                        //btn_capt.setEnabled(true);

                        this.Mostrat_Colectas();

                        //Pasa a seleccionar la maquina
                        this.Color_Sig_Paso(2);

                        break;
                }
            }

            if (requestCode == REQUEST_INI_CAP) {
                //Se procesa la devolución
                switch (resultCode) {
                    case RESULT_OK:
                        //cId_Maq = data.getStringExtra("MAQ_ID");
                        Toast.makeText(this, "SE GUARDARON LOS DATOS DE MANERA SATISFACTORIAMENTE [" + Global.cCte_Id + "]+[" + Global.cMaq_Id + "]", Toast.LENGTH_SHORT).show();
                        //btn_emp.setEnabled(false);
                        //btn_maq.setEnabled(true);
                        this.Color_Sig_Paso(2);
                        this.Mostrat_Status_Subir();

                        break;
                    case RESULT_CANCELED:
                        //btn_emp.setEnabled(false);
                        //btn_maq.setEnabled(true);
                        Toast.makeText(this, "SE CANCELO LA CAPTURA", Toast.LENGTH_SHORT).show();

                        break;
                }
            }

            if (requestCode == REQUEST_END_CAP) {
                //Se procesa la devolución
                switch (resultCode) {
                    case RESULT_OK:
                        //Toast.makeText(this, "Aceptó las condiciones [" + cId_Maq + "]", Toast.LENGTH_SHORT).show();
                        this.btn_emp.setEnabled(true);
                        this.btn_maq.setEnabled(true);

                        this.Color_Sig_Paso(2);

                        break;
                    case RESULT_CANCELED:
                        this.btn_emp.setEnabled(true);
                        this.btn_maq.setEnabled(true);
                        //Toast.makeText(this, "Rechazó las condiciones", Toast.LENGTH_SHORT).show();

                        this.Color_Sig_Paso(2);
                        break;
                }
            }

        }


    }

    @Override
    public void onResume() {
        super.onResume();
/*
        if (Global.Clients_Is_Empty() == false) {
            btn_emp.setEnabled(true);
            btn_maq.setEnabled(true);
            btn_capt.setEnabled(true);
            btn_ccol.setEnabled(true);
            btn_fcol.setEnabled(true);

        }
*/
    }

    private void Color_Sig_Paso(int Opt) {

        switch (Opt) {
            case 0:
                Global.cEmp_Id = "0";
                Global.cEmp_De = "";

                Global.cCte_Id = "NINGUNO";
                Global.cMaq_Id = "NINGUNO";

                btn_emp.setTextColor(getApplication().getResources().getColor(R.color.Blue));
                btn_emp.setTextSize(18);
                btn_emp.setText("INICIAR COLECTA [?]");

                btn_cte.setTextColor(getApplication().getResources().getColor(R.color.Black));
                btn_cte.setTextSize(14);
                btn_cte.setText("SELECCIONAR CLIENTE");

                btn_maq.setTextColor(getApplication().getResources().getColor(R.color.Black));
                btn_maq.setTextSize(14);
                btn_maq.setText("SELECCIONAR MAQUINA");

                btn_capt.setTextColor(getApplication().getResources().getColor(R.color.Black));
                btn_capt.setTextSize(14);
                break;
            case 1:
                Global.cCte_Id = "NINGUNO";
                Global.cMaq_Id = "NINGUNO";
                btn_emp.setTextColor(getApplication().getResources().getColor(R.color.Black));
                btn_emp.setTextSize(14);

                btn_cte.setTextColor(getApplication().getResources().getColor(R.color.Blue));
                btn_cte.setTextSize(18);
                btn_cte.setText("SELECCIONAR CLIENTE [?]");
                btn_fcol.setText("FINALIZAR COLECTA");

                btn_maq.setTextColor(getApplication().getResources().getColor(R.color.Black));
                btn_maq.setTextSize(14);

                btn_capt.setTextColor(getApplication().getResources().getColor(R.color.Black));
                btn_capt.setTextSize(14);
                break;
            case 2:
                Global.cMaq_Id = "NINGUNO";
                btn_emp.setTextColor(getApplication().getResources().getColor(R.color.Black));
                btn_emp.setTextSize(14);

                btn_cte.setTextColor(getApplication().getResources().getColor(R.color.Black));
                btn_cte.setTextSize(14);

                btn_maq.setTextColor(getApplication().getResources().getColor(R.color.Blue));
                btn_maq.setTextSize(18);
                btn_maq.setText("SELECCIONAR MAQUINA [?]");

                btn_capt.setTextColor(getApplication().getResources().getColor(R.color.Black));
                btn_capt.setTextSize(14);
                break;
            case 3:
                btn_emp.setTextColor(getApplication().getResources().getColor(R.color.Black));
                btn_emp.setTextSize(14);

                btn_cte.setTextColor(getApplication().getResources().getColor(R.color.Black));
                btn_cte.setTextSize(14);

                btn_maq.setTextColor(getApplication().getResources().getColor(R.color.Black));
                btn_maq.setTextSize(14);

                btn_capt.setTextColor(getApplication().getResources().getColor(R.color.Blue));
                btn_capt.setTextSize(18);
                break;
        }
    }

    protected void createDatabase() {

        String databasePath = getDatabasePath("one2009.db").getPath();
        db = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
    }

    private void myFunction(int result) {
        // Now the data has been "returned" (as pointed out, that's not
        // the right terminology)
        iGlobalRes = result;
    }

    private int dialogBox(String cMessage) {
        int iresp = 0;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(cMessage);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        myFunction(1);
                    }

                });

        alertDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                myFunction(0);

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return 1;
    }

    /*
* after opening a connection to bluetooth printer device,
* we have to listen and check if a data were sent to be printed.
*/
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    void sendData() throws IOException {
        try {

            // the text typed by the user
            String msg = myTextbox.getText().toString();
            msg += "\n";

            mmOutputStream.write(msg.getBytes());

            // tell the user data were sent
            myLabel.setText("Data sent.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Mostrat_Colectas() {
        String cSql_ln = "";
        String cCount = "";
        String cChapa = "";

        cSql_ln = "" +
                "SELECT " +
                "   IFNULL(COUNT(IFNULL(op_chapa,0)),0) as cnt " +
                "FROM operacion " +
                "WHERE (id_device='" + Global.cid_device + "') " +
                "AND   (op_emp_id='" + Global.cEmp_Id + "')";
        cCount = Global.Query_Result(cSql_ln, "cnt");
        if (cCount == "") {
            cCount = "0";
        }

        cSql_ln = "" +
                "SELECT " +
                "   IFNULL(op_chapa,'') as op_chapa " +
                "FROM operacion " +
                "WHERE (id_device='" + Global.cid_device + "') " +
                "AND   (op_emp_id='" + Global.cEmp_Id + "') " +
                "AND   (cte_id   ='" + Global.cCte_Id + "') " +
                "AND   (op_chapa ='" + Global.cMaq_Id + "')";
        cChapa = Global.Query_Result(cSql_ln, "op_chapa");

        if (cChapa != "")
            btn_capt.setText("REGISTRAR METROS MAQUINAS [" + cCount + "] *" + cChapa + " YA FUE REGISTRADA*");
        else
            btn_capt.setText("REGISTRAR METROS MAQUINAS [" + cCount + "] ");

    }

    private void Mostrat_Colectas_Completas() {
        String cSql_ln = "" +
                "SELECT " +
                "   COUNT(op_chapa) as cnt " +
                "FROM operacion " +
                "WHERE (id_device='" + Global.cid_device + "') " +
                "AND   (op_emp_id='" + Global.cEmp_Id + "')";
        String cValue = Global.Query_Result(cSql_ln, "cnt");

        btn_capt.setText("REGISTRAR METROS MAQUINAS [" + cValue + "]");

    }

    private void Mostrat_Status_Subir() {
        String cSql_ln = "" +
                "SELECT " +
                "   COUNT(op_chapa) as cnt " +
                "FROM operacion " +
                "WHERE (id_device='" + Global.cid_device + "') " +
                "AND   (op_emp_id='" + Global.cEmp_Id + "') " +
                "AND   (op_usermodify=1)";
        String cValue = Global.Query_Result(cSql_ln, "cnt");

        btn_send.setEnabled(true);
        btn_send.setText("ENVIAR COLECTAS [" + cValue + "]");
    }


}
