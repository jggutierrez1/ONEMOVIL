package flamingo.onemovil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;
import android.content.Context;

import java.io.FileInputStream;

import java.io.File;
import java.io.IOException;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.json.JSONArray;

public class menu extends AppCompatActivity {

    private Button btn_conf, btn_upd, btn_start, btn_maq, btn_capt, btn_repo, btn_exit, btn_canc_colec, btn_fin_colec;
    private ImageView mImageView;
    private TextView DeviceId;
    private SQLiteDatabase db;
    private String cSql_Ln;
    private String mCurrentPhotoPath;

    private final static int REQUEST_SEL_CTE = 1;
    private final static int REQUEST_SEL_MAQ = 2;
    private final static int REQUEST_INI_CAP = 3;
    private final static int REQUEST_END_CAP = 4;

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

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File appFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "fotos_metros");
            exist = appFolder.exists();
        }

        btn_conf = (Button) findViewById(R.id.obtn_conf);
        btn_upd = (Button) findViewById(R.id.obtn_upd);
        btn_start = (Button) findViewById(R.id.obtn_start);
        btn_maq = (Button) findViewById(R.id.obtn_maq);
        btn_capt = (Button) findViewById(R.id.obtn_capt);
        btn_repo = (Button) findViewById(R.id.obtn_repo);
        btn_exit = (Button) findViewById(R.id.obtn_exit);
        btn_canc_colec = (Button) findViewById(R.id.obtn_canc_colec);
        btn_fin_colec = (Button) findViewById(R.id.obtn_fin_colec);

        DeviceId = (TextView) findViewById(R.id.oDeviceId);
        DeviceId.setText("ID EQUIPO:" + Global.cid_device.toUpperCase());
        myLabel = (TextView) findViewById(R.id.oLabel);
        myTextbox = (EditText) findViewById(R.id.oEntry);

        createDatabase();
        Create_Sql_Tables();
        findBT();
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


        btn_conf.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*
                String Sql="SELECT * FROM operacion limit 200";
                String aValue=Global.getJsonResults2(Sql,"operacion");
                Toast.makeText(getApplicationContext(), aValue , Toast.LENGTH_SHORT).show();
                */

                /*
                Intent Int_TakePhoto = new Intent(getApplicationContext(), take_photo.class);
                startActivity(Int_TakePhoto);
                */
                Intent Int_Uploadfiles = new Intent(getApplicationContext(), uploadfiletoserver.class);
                startActivity(Int_Uploadfiles);


            }
        });

        btn_upd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent Int_SyncScreen = new Intent(getApplicationContext(), sync_data.class);
                startActivity(Int_SyncScreen);
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Int_CteScreen = new Intent(getApplicationContext(), select_cte.class);
                startActivityForResult(Int_CteScreen, REQUEST_SEL_CTE);
            }
        });

        btn_maq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Int_MaqScreen = new Intent(getApplicationContext(), select_maq.class);
                startActivityForResult(Int_MaqScreen, REQUEST_SEL_MAQ);
            }
        });

        btn_capt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Int_CapScreen = new Intent(getApplicationContext(), capt_data.class);
                startActivityForResult(Int_CapScreen, REQUEST_INI_CAP);
            }
        });
        btn_fin_colec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Capf_MaqScreen = new Intent(getApplicationContext(), capt_fin.class);
                startActivityForResult(Capf_MaqScreen, REQUEST_END_CAP);


                Toast.makeText(getApplicationContext(), "FINALIZO.", Toast.LENGTH_SHORT).show();
            }
        });

        btn_canc_colec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBox("ESTA SEGURO QUE DESEA DESCARTAR TODAS LAS ENTRADAS REALIZADAS EN EL EQUIPO.");
                if (iGlobalRes == 1) {
                    String cSql_Ln = "DELETE FROM operacion WHERE id_device='" + Global.cid_device + "'";
                    db.execSQL(cSql_Ln);
                    Toast.makeText(getApplicationContext(), "LOS DATOS FUERON ELIMINADOS.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "SE CANCELO LA ACCION.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2

        {
            //Se no ha habido fallo:
            if (requestCode == REQUEST_SEL_CTE) {
                //Valida la seleccion del cliente.
                switch (resultCode) {
                    case RESULT_OK:
                        Toast.makeText(this, "Aceptó el cliente:[" + Global.cCte_Id.trim() + "]/" + Global.cCte_De.trim(), Toast.LENGTH_SHORT).show();
                        //btn_start.setEnabled(false);
                        btn_start.setTextColor(getApplication().getResources().getColor(R.color.Blue));

                        break;
                    case RESULT_CANCELED:

                        Global.cCte_Id = "";
                        Global.cCte_De = "";

                        Toast.makeText(this, "Rechazó el cliente.", Toast.LENGTH_SHORT).show();
                        btn_start.setTextColor(getApplication().getResources().getColor(R.color.Black));
                        //btn_start.setEnabled(true);
                        break;
                }
            }

            if (requestCode == REQUEST_SEL_MAQ) {
                //Se procesa la devolución
                switch (resultCode) {
                    case RESULT_OK:
                        Toast.makeText(this, "Aceptó la máquina: [" + Global.cMaq_Id + "]/[" + Global.cMaq_De + "]", Toast.LENGTH_SHORT).show();
                        //btn_start.setEnabled(false);
                        //btn_maq.setEnabled(false);
                        btn_maq.setTextColor(getApplication().getResources().getColor(R.color.Blue));
                        break;
                    case RESULT_CANCELED:
                        Global.cMaq_Id = "";
                        Global.cMaq_De = "";

                        Toast.makeText(this, "Rechazó la máquina.", Toast.LENGTH_SHORT).show();
                        //btn_capt.setEnabled(true);
                        btn_maq.setTextColor(getApplication().getResources().getColor(R.color.Black));
                        break;
                }
            }

            if (requestCode == REQUEST_INI_CAP) {
                //Se procesa la devolución
                switch (resultCode) {
                    case RESULT_OK:
                        //cId_Maq = data.getStringExtra("MAQ_ID");
                        Toast.makeText(this, "SE GUARDARON LOS DATOS DE MANERA SATISFACTORIAMENTE [" + Global.cCte_Id + "]+[" + Global.cMaq_Id + "]", Toast.LENGTH_SHORT).show();
                        //btn_start.setEnabled(false);
                        //btn_maq.setEnabled(true);
                        break;
                    case RESULT_CANCELED:
                        //btn_start.setEnabled(false);
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
                        btn_start.setEnabled(true);
                        btn_maq.setEnabled(true);
                        btn_start.setTextColor(getApplication().getResources().getColor(R.color.Black));
                        break;
                    case RESULT_CANCELED:
                        btn_start.setEnabled(true);
                        btn_maq.setEnabled(true);
                        //Toast.makeText(this, "Rechazó las condiciones", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        }
    }

    protected void createDatabase() {

        String databasePath = getDatabasePath("one2009.db").getPath();
        db = openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
    }

    public void Create_Sql_Tables() {
        // -----------------------------------CLIENTES------------------------------------------------------//

        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS clientes (";
        cSql_Ln += "cte_id INTEGER NOT NULL,";
        cSql_Ln += "mun_id INTEGER NULL DEFAULT 1,";
        cSql_Ln += "rut_id INTEGER NULL DEFAULT 1,";
        cSql_Ln += "cte_nombre_loc CHAR(80) NULL DEFAULT '',";
        cSql_Ln += "cte_nombre_com CHAR(80) NULL DEFAULT '',";
        cSql_Ln += "cte_telefono1 CHAR(25) NULL DEFAULT '',";
        cSql_Ln += "cte_telefono2 CHAR(25) NULL DEFAULT '',";
        cSql_Ln += "cte_Fax CHAR(25) NULL DEFAULT '',";
        cSql_Ln += "cte_contacto_nom CHAR(35) NULL DEFAULT '',";
        cSql_Ln += "cte_contacto_movil CHAR(25) NULL DEFAULT '',";
        cSql_Ln += "cte_contacto_movil_bbpin CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "cte_contacto_movil_email CHAR(120) NULL DEFAULT '',";
        cSql_Ln += "cte_email CHAR(120) NULL DEFAULT '',";
        cSql_Ln += "cte_webpage VARCHAR(130) NULL DEFAULT '',";
        cSql_Ln += "cte_notas TEXT NULL,";
        cSql_Ln += "cte_inactivo INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_direccion TEXT NULL, ";
        cSql_Ln += "cte_pag_impm INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_pag_jcj INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_pag_spac INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_poc_ret NUMERIC(12, 2) NULL DEFAULT 0,";
        cSql_Ln += "cte_fecha_alta DATETIME NULL ,";
        cSql_Ln += "cte_fecha_modif DATETIME NULL ,";
        cSql_Ln += "cte_emp_id INTEGER NULL DEFAULT 0,";
        cSql_Ln += "u_usuario_alta CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "cte_unico_emp INTEGER NULL DEFAULT 0,";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "CONSTRAINT clientes_PRIMARY PRIMARY KEY (cte_id))";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_inactivo ON clientes('cte_inactivo')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_nombre_loc ON clientes('cte_nombre_loc')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS mun_id ON clientes('mun_id')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_nombre_com ON clientes('cte_nombre_com')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS rut_id ON clientes('rut_id')";
        db.execSQL(cSql_Ln);
        // --------------------------------FIN CLIENTES-----------------------------------------------------//

        // -----------------------------------DENOMINACIONES------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS denominaciones (";
        cSql_Ln += "den_id INTEGER NOT NULL,";
        cSql_Ln += "den_descripcion CHAR(30) NULL DEFAULT '',";
        cSql_Ln += "den_inactiva INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_fact_e INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_fact_s INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_valor NUMERIC(12, 2) NULL DEFAULT 0,";
        cSql_Ln += "den_fecha_alta DATETIME NULL ,";
        cSql_Ln += "den_fecha_modif DATETIME NULL ,";
        cSql_Ln += "u_usuario_alta CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "CONSTRAINT denominaciones_PRIMARY PRIMARY KEY (den_id))";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS den_inactiva ON denominaciones('den_inactiva')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS den_descripcion ON denominaciones('den_descripcion')";
        db.execSQL(cSql_Ln);
        // -------------------------------FIN DENOMINACIONES------------------------------------------------//

        // ---------------------------------EMPRESA----------------------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS empresas ( ";
        cSql_Ln += "emp_id INTEGER NOT NULL,";
        cSql_Ln += "emp_descripcion CHAR(85) NULL DEFAULT '',";
        cSql_Ln += "emp_ruc CHAR(85) NULL DEFAULT '',";
        cSql_Ln += "emp_dv CHAR(10) NULL DEFAULT '',";
        cSql_Ln += "emp_carpeta_reportes VARCHAR (120) NULL DEFAULT '',";
        cSql_Ln += "emp_telefono1 CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "emp_telefono2 CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "emp_fax CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "emp_direccion TEXT NULL,";
        cSql_Ln += "emp_apartado TEXT NULL,";
        cSql_Ln += "emp_email CHAR(100) NULL DEFAULT '',";
        cSql_Ln += "separa_mil CHAR(1) NULL DEFAULT ',',";
        cSql_Ln += "separa_dec CHAR(1) NULL DEFAULT '.',";
        cSql_Ln += "emp_imagen BLOB NULL,";
        cSql_Ln += "emp_imagen_path VARCHAR(120) NULL DEFAULT '',";
        cSql_Ln += "emp_inactivo INTEGER NULL DEFAULT 0,";
        cSql_Ln += "emp_cargo_jcj NUMERIC(12, 2) NULL DEFAULT 0,";
        cSql_Ln += "emp_cargo_spac NUMERIC(12, 2) NULL DEFAULT 0,";
        cSql_Ln += "emp_fecha_alta DATETIME NULL ,";
        cSql_Ln += "emp_fecha_modif DATETIME NULL ,";
        cSql_Ln += "u_usuario_alta CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "CONSTRAINT empresas_PRIMARY PRIMARY KEY (emp_id))";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS emp_inactivo ON empresas ('emp_inactivo')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS emp_descripcion ON empresas ('emp_descripcion')";
        db.execSQL(cSql_Ln);
        // ----------------------------------FIN EMPRESAS---------------------------------------------------//

        // ---------------------------------MAQUINASTC------------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS maquinastc (";
        cSql_Ln += "maqtc_id INTEGER NOT NULL,";
        cSql_Ln += "maqtc_cod CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "maqtc_modelo CHAR(60) NULL DEFAULT '',";
        cSql_Ln += "maqtc_chapa CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "maqtc_inactivo INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_metros INTEGER NULL DEFAULT 1,";
        cSql_Ln += "emp_id INTEGER NULL DEFAULT 1,";
        cSql_Ln += "maqtc_denom_e INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_tipomaq INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_denom_s INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m1e_act INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m1e_ant INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m1s_ant INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m2e_act INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m2e_ant INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m2s_act INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m2s_ant INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m1s_act INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_fecha_alta DATETIME NULL ,";
        cSql_Ln += "maqtc_fecha_modif DATETIME NULL ,";
        cSql_Ln += "u_usuario_alta CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "CONSTRAINT maquinastc_PRIMARY PRIMARY KEY (maqtc_id))";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS maqtc_cod ON maquinastc('maqtc_cod')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS maqtc_chapa ON maquinastc('maqtc_chapa')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS maqtc_inactivo ON maquinastc('maqtc_inactivo')";
        db.execSQL(cSql_Ln);
        // -----------------------------FIN MAQUINASTC------------------------------------------------------//

        // ---------------------------------MAQUINAS LNK----------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS maquinas_lnk (";
        cSql_Ln += "MaqLnk_Id INTEGER NOT NULL,";
        cSql_Ln += "emp_id INTEGER NULL DEFAULT 1,";
        cSql_Ln += "cte_id INTEGER NULL DEFAULT 1,";
        cSql_Ln += "maqtc_id INTEGER NULL DEFAULT 1,";
        cSql_Ln += "MaqLnk_fecha_alta DATETIME NULL ,";
        cSql_Ln += "MaqLnk_fecha_modif DATETIME NULL ,";
        cSql_Ln += "CONSTRAINT maquinas_lnk_PRIMARY PRIMARY KEY (MaqLnk_Id))";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS emp_id ON maquinas_lnk('emp_id')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS maqtc_id ON maquinas_lnk('maqtc_id')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_id ON maquinas_lnk('cte_id')";
        db.execSQL(cSql_Ln);
        // -----------------------------FIN MAQUINAS LNK----------------------------------------------------//

        // ---------------------------------MUNICIPIOS  ----------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS municipios (";
        cSql_Ln += "mun_id INTEGER NOT NULL,";
        cSql_Ln += "mun_inactivo INTEGER NULL DEFAULT 0,";
        cSql_Ln += "mun_nombre CHAR(80) NULL DEFAULT '',";
        cSql_Ln += "mun_impuesto NUMERIC(12, 2) NULL DEFAULT 0,";
        cSql_Ln += "mun_notas TEXT,";
        cSql_Ln += "mun_fecha_alta DATETIME NULL ,";
        cSql_Ln += "mun_fecha_modif DATETIME NULL ,";
        cSql_Ln += "u_usuario_alta CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "CONSTRAINT municipios_PRIMARY PRIMARY KEY (mun_id))";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS mun_nombre ON municipios ('mun_nombre')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS mun_inactivo ON municipios ('mun_inactivo')";
        db.execSQL(cSql_Ln);
        // -----------------------------FIN MUNICIPIOS  ----------------------------------------------------//

        // --------------------------------- RUTAS ---------------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS rutas (";
        cSql_Ln += "rut_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,";
        cSql_Ln += "rut_inactivo INT(1) NULL DEFAULT '0',";
        cSql_Ln += "rut_nombre CHAR(80) NULL DEFAULT '',";
        cSql_Ln += "rut_notas LONGTEXT NULL,";
        cSql_Ln += "rut_fecha_alta DATETIME NULL DEFAULT '2010-01-01 00:00:00',";
        cSql_Ln += "rut_fecha_modif DATETIME NULL DEFAULT '2010-01-01 00:00:00',";
        cSql_Ln += "u_usuario_alta VARCHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif VARCHAR(20) NULL DEFAULT 'ANONIMO')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS rut_id ON rutas ('rut_id')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS rut_inactivo ON rutas ('rut_inactivo')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS rut_nombre ON rutas ('rut_nombre')";
        db.execSQL(cSql_Ln);
        // -------------------------------FIN RUTAS --------------------------------------------------------//

        cSql_Ln = "DROP TABLE IF EXISTS operacion;";
        //db.execSQL(cSql_Ln);

        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS operacion (";
        cSql_Ln += "id_op  INTEGER NOT NULL,";
        cSql_Ln += "MaqLnk_Id  INTEGER NOT NULL DEFAULT 0,";
        cSql_Ln += "cte_id INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_nombre_loc  CHAR(60) NOT NULL DEFAULT '',";
        cSql_Ln += "cte_nombre_com  CHAR(60) NOT NULL DEFAULT '',";
        cSql_Ln += "cte_pag_jcj  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_pag_spac  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_pag_impm  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_denom_e  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_denom_s  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_valore  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "den_valors  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "den_fact_e  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_fact_s  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_tipomaq  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_cporc_Loc  NUMERIC(6, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_serie  INTEGER NULL DEFAULT 1,";
        cSql_Ln += "op_chapa  CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "op_fecha  DATETIME NULL,";
        cSql_Ln += "op_nodoc  CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "op_modelo  CHAR(60) NULL DEFAULT '',";
        cSql_Ln += "op_e_pantalla  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_ea_metroan  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_ea_metroac  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_ea_met  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_sa_metroan  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_sa_metroac  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_sa_met  INTEGER NOT NULL DEFAULT 0,";
        cSql_Ln += "op_eb_metroan  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_eb_metroac  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_eb_met  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_sb_metroan  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_sb_metroac  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_sb_met  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_s_pantalla  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_cal_colect  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_colect  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_impmunic  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_impjcj  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_timbres  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_spac  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_tec  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_dev  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_otros  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_cred  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_cal_cred  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_sub  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_itbm  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_tot  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_brutoloc  NUMERIC(6, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_brutoemp  NUMERIC(6, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_netoloc  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_netoemp  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_observ  TEXT,";
        cSql_Ln += "op_num_sem  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_fecha_alta  DATETIME NULL,";
        cSql_Ln += "op_fecha_modif  DATETIME NULL,";
        cSql_Ln += "u_usuario_alta  CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif  CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "op_emp_id  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "id_device VARCHAR(30) NOT NULL DEFAULT 'MANUAL',";
        cSql_Ln += "op_semanas INTEGER NULL DEFAULT 1,";
        cSql_Ln += "CONSTRAINT  operacion_PRIMARY PRIMARY KEY(id_op))";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_id ON operacion ('cte_id')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS id_device ON operacion ('id_device')";
        db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS MaqLnk_Id ON operacion ('MaqLnk_Id')";
        db.execSQL(cSql_Ln);
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

    // this will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                myLabel.setText("No bluetooth adapter available");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // BlueTooth Printer is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals("BlueTooth Printer")) {
                        mmDevice = device;
                        break;
                    }
                }
            }

            myLabel.setText("Bluetooth device found.[" + mmDevice + "]");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            myLabel.setText("Bluetooth Opened");

        } catch (Exception e) {
            e.printStackTrace();
        }
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


}
