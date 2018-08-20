package flamingo.onemovil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import io.requery.android.database.sqlite.*;

import android.graphics.Color;
import android.provider.Settings;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

// Import neccessor namespace
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.RunnableFuture;
import java.util.logging.LogRecord;

public class print_data extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    TextView lblPrinterName;
    ListView ListMaq;
    TextView textBox;
    TextView olab_cte;
    private SQLiteDatabase oDb6;
    private Cursor oData6, oData2;
    private String cSqlLn = "";
    private String cDatabasePath = "";
    private ArrayList<String> theList = new ArrayList<>();
    private static final int iLineNo = 69;
    private String cLineSing = String.format("%0" + iLineNo + "d", 0).replace("0", "-");
    private String cLineDoub = String.format("%0" + iLineNo + "d", 0).replace("0", "=");
    private Context oThis = this;
    private String cText = "";
    private int iTotChars = 65;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_data);

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

        Locale.setDefault(new Locale("en", "US"));

        getWindow().getDecorView().getRootView().setBackgroundColor(Color.parseColor("#ffc2b3"));

        // Create object of controls
        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        Button btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        Button btnPrint = (Button) findViewById(R.id.btnPrint);
        Button btnRegr = (Button) findViewById(R.id.btnRegr);

        this.lblPrinterName = (TextView) findViewById(R.id.lblPrinterName);
        this.olab_cte = (TextView) findViewById(R.id.lab_cte3);

        this.ListMaq = (ListView) findViewById(R.id.oListMaq);
        this.ListMaq.setClickable(true);

        this.olab_cte.setText("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De + "]");

        this.cDatabasePath = getDatabasePath("one2009.db").getPath();

        this.oDb6 = io.requery.android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(cDatabasePath, null, null);

        try {
            disconnectBT();
            FindBluetoothDevice();
            openBluetoothPrinter();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Global.save_in_textfile(Global.cFileRepPathDestF, "", false);
        this.prn_titles();
        this.qry_lst_maq();
        this.list_maq_fact();
        /*
        switch (Global.iPrn_Data) {
            case 1: {
                Listar_Montos();
            }
            break;
            case 2: {
                Listar_Montos();
            }
            break;
            default: {
            }
            ;

        }
           */

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FindBluetoothDevice();
                    openBluetoothPrinter();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    disconnectBT();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prn_maq_fact();
                /*
                    Imprimir_Maquinas();
                    if (Global.iPrn_Data == 1)
                        Imprimir_Montos();
                    if (Global.iPrn_Data == 2)
                        Imprimir_Montos2();
                */
            }
        });

        btnRegr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    disconnectBT();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);
                oDb6.close();
                finish();
            }
        });

    }

    void FindBluetoothDevice() {

        try {

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                lblPrinterName.setText("NO SE ENCONTRO DISPOSITIBO BLUETOOTH");
                getWindow().getDecorView().getRootView().setBackgroundColor(Color.parseColor("#ffc2b3"));
            }
            if (bluetoothAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, 0);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            lblPrinterName.setText("CONECTADO A: [SIN IMPRESORA]");
            lblPrinterName.setTextColor(Color.parseColor("#ff0000"));

            if (pairedDevice.size() > 0) {
                for (BluetoothDevice pairedDev : pairedDevice) {
                    // My Bluetoth printer name is BTP_F09F1A
                    String sPrinterName = pairedDev.getName().toUpperCase();
                    if (sPrinterName.equals("BlueTooth Printer") || sPrinterName.equals("AB-341M_ZX1606")) {
                        bluetoothDevice = pairedDev;
                        lblPrinterName.setText("CONECTADO A: [" + pairedDev.getName() + "]");
                        lblPrinterName.setTextColor(Color.parseColor("#009900"));
                        getWindow().getDecorView().getRootView().setBackgroundColor(Color.parseColor("#ffffff"));
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            lblPrinterName.setText("[ERROR AL TRATAR DE DETECTAR LA IMPRESORA]");
            lblPrinterName.setTextColor(Color.parseColor("#ff0000"));
            getWindow().getDecorView().getRootView().setBackgroundColor(Color.parseColor("#ffc2b3"));
        }

    }

    // Open Bluetooth Printer

    void openBluetoothPrinter() throws IOException {
        try {

            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            beginListenData();

        } catch (Exception ex) {

        }
    }

    void beginListenData() {
        try {

            final Handler handler = new Handler();
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int byteAvailable = inputStream.available();
                            if (byteAvailable > 0) {
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for (int i = 0; i < byteAvailable; i++) {
                                    byte b = packetByte[i];
                                    if (b == delimiter) {
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedByte, 0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte, "US-ASCII");
                                        readBufferPosition = 0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                lblPrinterName.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            stopWorker = true;
                        }
                    }

                }
            });

            thread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void prn_titles() {

        Date d = new Date();
        SimpleDateFormat simpleDate2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cDateMysql = simpleDate2.format(d);

        cText = Global.center("***" + Global.cEmp_De.toUpperCase().trim() + "***", iTotChars, ' ') + "\n";
        Global.save_in_textfile(Global.cFileRepPathDestF, cText, true);

        cText = Global.center("***CONTROL DE REGISTRO***", iTotChars, ' ') + "\n";
        Global.save_in_textfile(Global.cFileRepPathDestF, cText, true);

        cText = Global.center("LISTADO DE MAQUINAS COLECTADAS", iTotChars, ' ') + "\n";
        Global.save_in_textfile(Global.cFileRepPathDestF, cText, true);

        cText = Global.center("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De.toUpperCase().trim() + "]", iTotChars, ' ') + "\n";
        Global.save_in_textfile(Global.cFileRepPathDestF, cText, true);

        cText = "FECHA :" + cDateMysql + "\n";
        Global.save_in_textfile(Global.cFileRepPathDestF, cText, true);

        cText = Global.repeat('=', iTotChars) + "\n";
        Global.save_in_textfile(Global.cFileRepPathDestF, cText, true);

        cText = " \n";
        Global.save_in_textfile(Global.cFileRepPathDestF, cText, true);
    }

    // Printing Text to Bluetooth Printer //
    void printData() throws IOException {
    /*       try {
            String msg = textBox.getText().toString();
            msg += "\n";
            outputStream.write(msg.getBytes());
            lblPrinterName.setText("IMPRIMIENDO TEXTO...");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
      */
    }

    void printString(String msg) {
        try {
            msg += "\n";
            outputStream.write(msg.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Disconnect Printer //
    void disconnectBT() throws IOException {
        try {
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            lblPrinterName.setText("IMPRESORA DESCONECTADA.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean qry_lst_maq() {
        cSqlLn = "";
        cSqlLn += "SELECT ";
        cSqlLn += " trim(op.op_chapa ) || SUBSTR('                   ', 1, 12-length(trim(op.op_chapa ))) || ";
        cSqlLn += " trim(op.op_modelo) || SUBSTR('                   ', 1, 20-length(trim(op.op_modelo))) AS expr1, ";
        cSqlLn += " em.emp_abrev , ";
        cSqlLn += " ct.cte_nombre_loc , ";
        cSqlLn += " ma.maqtc_modelo , ";
        cSqlLn += " op.op_fecha  , ";
        cSqlLn += " op.op_chapa, ";
        cSqlLn += " op.op_modelo, ";
        cSqlLn += " op.op_ea_metroan, ";
        cSqlLn += " op.op_ea_metroac, ";
        cSqlLn += " op.op_sa_metroan, ";
        cSqlLn += " op.op_sa_metroac, ";
        cSqlLn += " op.op_eb_metroan, ";
        cSqlLn += " op.op_eb_metroac, ";
        cSqlLn += " op.op_sb_metroan, ";
        cSqlLn += " op.op_sb_metroac, ";
        cSqlLn += " op.op_tot_colect   AS op_tot_colect    , ";
        cSqlLn += " op.op_tot_impmunic AS op_tot_impmunic  , ";
        cSqlLn += " op.op_tot_impjcj   AS op_tot_impjcj    , ";
        cSqlLn += " op.op_tot_timbres  AS op_tot_timbres   , ";
        cSqlLn += " op.op_tot_tec      AS op_tot_tec       , ";
        cSqlLn += " op.op_tot_dev      AS op_tot_dev       , ";
        cSqlLn += " op.op_tot_otros    AS op_tot_otros     , ";
        cSqlLn += " op.op_tot_cred     AS op_tot_cred      , ";
        cSqlLn += " op.op_tot_sub      AS op_tot_sub       , ";
        cSqlLn += " op.op_tot_itbm     AS op_tot_itbm      , ";
        cSqlLn += " op.op_tot_tot      AS op_tot_tot       , ";
        cSqlLn += " op.op_tot_brutoloc AS op_tot_brutoloc  , ";
        cSqlLn += " op.op_tot_brutoemp AS op_tot_brutoemp  , ";
        cSqlLn += " op.op_tot_netoloc  AS op_tot_netoloc   , ";
        cSqlLn += " op.op_tot_netoemp  AS op_tot_netoemp   , ";
        cSqlLn += " op.op_baja_prod    AS op_baja_prod,       ";
        //cSqlLn += " op.op_observ       AS op_observ,       ";
        cSqlLn += " (op.op_tot_colect-op.op_tot_cred) AS tot_dife, ";
        cSqlLn += "  og.op_fact_global || '-' || op.op_chapa AS num_corre ";
        cSqlLn += "FROM operacion op ";
        cSqlLn += "LEFT JOIN empresas   em ON (op.op_emp_id = em.emp_id  ) ";
        cSqlLn += "LEFT JOIN clientes   ct ON (op.cte_id    = ct.cte_id  ) ";
        cSqlLn += "LEFT JOIN maquinastc ma ON (op.op_chapa  = ma.maqtc_id) ";
        cSqlLn += "LEFT JOIN operaciong og ON ((op.id_device = og.id_device) AND (op.op_emp_id = og.op_emp_id) AND (op.cte_id = og.cte_id) AND (op.id_group = og.id_group) ) ";
        cSqlLn += "WHERE (op.id_device='" + Global.cid_device + "') ";
        cSqlLn += "AND   (op.op_emp_id='" + Global.cEmp_Id + "') ";
        cSqlLn += "AND   (op.cte_id   ='" + Global.cCte_Id + "') ";
        cSqlLn += "ORDER BY op.op_emp_id, op.cte_id, op.op_chapa ";
        Log.d("SQL", cSqlLn);
        this.oData6 = this.oDb6.rawQuery(cSqlLn, null);

        if ((this.oData6 == null) || (this.oData6.getCount() == 0)) {
            return false;
        } else {
            return true;
        }
    }


    private boolean list_maq_fact() {
        //cPrnLn = Global.center("***" + Global.cEmp_De.toUpperCase().trim() + "***", 180, ' ') + "\n";
        String cPrnLn, cPrnLn1, cPrnLn2, cLstLn = "";
        String cValue = "";
        Double fValue, fValue1, fValue2 = 0.00;
        if ((this.oData6 == null) || (this.oData6.getCount() == 0)) {
            return false;
        } else {
            oData6.moveToFirst();
            do {

                cLstLn = String.format("%-8s %-20s %12.2f %12.2f %12.2f",
                        this.oData6.getString(this.oData6.getColumnIndex("op_chapa")).trim(),
                        this.oData6.getString(this.oData6.getColumnIndex("op_modelo")).trim(),
                        this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_colect")),
                        this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_cred")),
                        this.oData6.getDouble(this.oData6.getColumnIndex("tot_dife")));

                //cPrnLn = Global.repeat('=', iTotChars) + "\n";
                //Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                cPrnLn1 = "MODELO:" + this.oData6.getString(this.oData6.getColumnIndex("op_modelo")).trim();
                cPrnLn2 = "CHAPA :" + this.oData6.getString(this.oData6.getColumnIndex("op_chapa")).trim();
                cPrnLn = Global.rightPad(cPrnLn1, (iTotChars / 2), " ") + Global.rightPad(cPrnLn2, (iTotChars / 2), " ") + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                cPrnLn = "No.   :" + this.oData6.getString(this.oData6.getColumnIndex("num_corre")).trim() + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                cPrnLn = Global.rightPad("***CONTROL DE ENTRADAS***", (iTotChars / 2), " ") + Global.rightPad("***CONTROL DE SALIDAS***", (iTotChars / 2), " ") + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                cPrnLn1 = "ACTUAL   E[A]:" + String.format(Locale.US, "%10.2f", this.oData6.getDouble(this.oData6.getColumnIndex("op_ea_metroac")));
                cPrnLn2 = "ACTUAL   S[A]:" + String.format(Locale.US, "%10.2f", this.oData6.getDouble(this.oData6.getColumnIndex("op_sa_metroac")));
                cPrnLn = Global.rightPad(cPrnLn1, (iTotChars / 2), " ") + Global.rightPad(cPrnLn2, (iTotChars / 2), " ") + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                cPrnLn1 = "ANTERIOR E[A]:" + String.format(Locale.US, "%10.2f", this.oData6.getDouble(this.oData6.getColumnIndex("op_ea_metroan")));
                cPrnLn2 = "ANTERIOR S[A]:" + String.format(Locale.US, "%10.2f", this.oData6.getDouble(this.oData6.getColumnIndex("op_sa_metroan")));
                cPrnLn = Global.rightPad(cPrnLn1, (iTotChars / 2), " ") + Global.rightPad(cPrnLn2, (iTotChars / 2), " ") + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                fValue1 = this.oData6.getDouble(this.oData6.getColumnIndex("op_eb_metroan"));
                fValue2 = this.oData6.getDouble(this.oData6.getColumnIndex("op_sb_metroan"));
                if ((fValue1 > 0) && (fValue2 > 0)) {
                    cPrnLn1 = "ACTUAL   E[B]:" + String.format(Locale.US, "%10.2f", this.oData6.getDouble(this.oData6.getColumnIndex("op_eb_metroac")));
                    cPrnLn2 = "ACTUAL   S[B]:" + String.format(Locale.US, "%10.2f", this.oData6.getDouble(this.oData6.getColumnIndex("op_sb_metroac")));
                    cPrnLn = Global.rightPad(cPrnLn1, (iTotChars / 2), " ") + Global.rightPad(cPrnLn2, (iTotChars / 2), " ") + "\n";
                    Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                    cPrnLn1 = "ANTERIOR E[B]:" + String.format(Locale.US, "%10.2f", this.oData6.getDouble(this.oData6.getColumnIndex("op_eb_metroan")));
                    cPrnLn2 = "ANTERIOR S[B]:" + String.format(Locale.US, "%10.2f", this.oData6.getDouble(this.oData6.getColumnIndex("op_sb_metroan")));
                    cPrnLn = Global.rightPad(cPrnLn1, (iTotChars / 2), " ") + Global.rightPad(cPrnLn2, (iTotChars / 2), " ") + "\n";
                    Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);
                }
                cPrnLn = Global.repeat('=', iTotChars) + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_colect"));
                cPrnLn1 = "COLECTADO :" + Global.decimalformat(fValue, 10, 2) + "\n";
                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_sub"));
                cPrnLn2 = "SUB-TOTAL :" + Global.decimalformat(fValue, 10, 2) + "\n";
                cPrnLn = Global.rightPad(cPrnLn1, (iTotChars / 2), " ") + Global.rightPad(cPrnLn2, (iTotChars / 2), " ") + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_timbres"));
                if (fValue > 0) {
                    cPrnLn = "TIMBRES   :" + Global.decimalformat(fValue, 10, 2) + "\n";
                    Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);
                }

                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_impmunic"));
                if (fValue > 0) {
                    cPrnLn = "IMPUESTOS  :" + Global.decimalformat(fValue, 10, 2) + "\n";
                    Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);
                }

                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_impjcj"));
                cPrnLn1 = "J.C.J     :" + Global.decimalformat(fValue, 10, 2) + "\n";
                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_tot"));
                cPrnLn2 = "TOTAL     :" + Global.decimalformat(fValue, 10, 2) + "\n";
                cPrnLn = Global.rightPad(cPrnLn1, (iTotChars / 2), " ") + Global.rightPad(cPrnLn2, (iTotChars / 2), " ") + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_tec"));
                cPrnLn1 = "SERV. TEC.:" + Global.decimalformat(fValue, 10, 2) + "\n";
                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_brutoloc"));
                cPrnLn2 = "BRUTO CTE.:" + Global.decimalformat(fValue, 10, 2) + "\n";
                cPrnLn = Global.rightPad(cPrnLn1, (iTotChars / 2), " ") + Global.rightPad(cPrnLn2, (iTotChars / 2), " ") + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_dev"));
                cPrnLn1 = "TOT. DEVO.:" + Global.decimalformat(fValue, 10, 2) + "\n";
                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_brutoemp"));
                cPrnLn2 = "BRUTO EMP.:" + Global.decimalformat(fValue, 10, 2) + "\n";
                cPrnLn = Global.rightPad(cPrnLn1, (iTotChars / 2), " ") + Global.rightPad(cPrnLn2, (iTotChars / 2), " ") + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_otros"));
                cPrnLn1 = "TOT. OTRO :" + Global.decimalformat(fValue, 10, 2) + "\n";
                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_netoloc"));
                cPrnLn2 = "NETO  CTE.:" + Global.decimalformat(fValue, 10, 2) + "\n";
                cPrnLn = Global.rightPad(cPrnLn1, (iTotChars / 2), " ") + Global.rightPad(cPrnLn2, (iTotChars / 2), " ") + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_cred"));
                cPrnLn1 = "TOT. CRED.:" + Global.decimalformat(fValue, 10, 2) + "\n";
                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_netoemp"));
                cPrnLn2 = "NETO  EMP.:" + Global.decimalformat(fValue, 10, 2) + "\n";
                cPrnLn = Global.rightPad(cPrnLn1, (iTotChars / 2), " ") + Global.rightPad(cPrnLn2, (iTotChars / 2), " ") + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                fValue = this.oData6.getDouble(this.oData6.getColumnIndex("op_tot_itbm"));
                if (fValue > 0) {
                    cPrnLn = "TOT. IMP. :" + Global.decimalformat(fValue, 10, 2) + "\n";
                    Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);
                }

                //cValue = this.oData6.getString(this.oData6.getColumnIndex("op_observ"));
                //cPrnLn = "OBSERVACION:" + cValue + "\n";
                //Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                cPrnLn = Global.repeat('-', iTotChars) + "\n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                cPrnLn = " \n";
                Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

                theList.add(cLstLn);
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
                ListMaq.setAdapter(listAdapter);

            } while (oData6.moveToNext());
            cPrnLn = " \n";
            Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

            cPrnLn = " \n";
            Global.save_in_textfile(Global.cFileRepPathDestF, cPrnLn, true);

            return true;
        }
    }

    private void prn_maq_fact() {
        String sline = "";
        try {
            FileReader fReader = new FileReader(Global.cFileRepPathDestF);
            BufferedReader bReader = new BufferedReader(fReader);

            while ((sline = bReader.readLine()) != null) {
                printString(sline);
                //otext_lst_t.append(sline + "\n");
            }
            fReader = null;
            bReader = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean Listar_Montos() {

        cSqlLn = "";
        cSqlLn += "SELECT ";
        cSqlLn += " SUM(op.op_tot_colect) AS tot_cole, ";
        cSqlLn += " SUM(op.op_tot_cred)   AS tot_cred, ";
        cSqlLn += " (SUM(op.op_tot_colect)-SUM(op.op_tot_cred)) AS tot_dife ";
        cSqlLn += "FROM operacion op ";
        cSqlLn += "WHERE op.id_device='" + Global.cid_device + "' ";
        cSqlLn += "AND   op.op_emp_id='" + Global.cEmp_Id + "' ";
        cSqlLn += "AND   op.cte_id   ='" + Global.cCte_Id + "' ";
        cSqlLn += "GROUP BY op.op_emp_id,id_device ";
        Log.d("SQL", cSqlLn);
        oData2 = oDb6.rawQuery(cSqlLn, null);

        if ((oData2 == null) || (oData2.getCount() == 0)) {
            return false;
        } else {
            oData2.moveToFirst();
            return true;
        }
    }

    private boolean Imprimir_Maquinas() {
        String cPrnLn = "";
        if ((oData6 == null) || (oData6.getCount() == 0)) {
            return false;
        } else {
            printString("");
            printString(Global.center(Global.cEmp_De.toUpperCase(), iLineNo, ' '));
            printString(Global.center("LISTADO DE MAQUINAS COLECTADAS", iLineNo, ' '));
            printString(Global.center("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De + "]", iLineNo, ' '));
            cPrnLn = String.format("%-8s %-20s %12s %12s %12s", "CHAPA", "JUEGO", "ENTARDA", "SALIDA", "DIF.");
            printString(cPrnLn);
            printString(cLineSing);
            oData6.moveToFirst();
            do {
                cPrnLn = String.format(Locale.US, "%-8s %-20s %12.2f %12.2f %12.2f",
                        oData6.getString(1).trim(),
                        oData6.getString(2).trim(),
                        oData6.getDouble(3),
                        oData6.getDouble(4),
                        oData6.getDouble(5)
                );
                printString(cPrnLn);
            } while (oData6.moveToNext());
            return true;
        }
    }

    private boolean Imprimir_Montos() {
        String cPrnLn = "";
        if ((oData2 == null) || (oData2.getCount() == 0)) {
            return false;
        } else {
            //printString("");
            oData2.moveToFirst();
            printString(cLineDoub);
            do {
                cPrnLn = String.format(Locale.US, "%-8s %-20s %12.2f %12.2f %12.2f",
                        "TOTALES", "",
                        oData2.getDouble(0),
                        oData2.getDouble(1),
                        oData2.getDouble(2));
                printString(cPrnLn);
                /*
                printString("Colectado : " + String.format("%12.2f", oData2.getDouble(0)).trim());
                printString("Credito   : " + String.format("%12.2f", oData2.getDouble(1)).trim());
                printString("Diferencia: " + String.format("%12.2f", oData2.getDouble(2)).trim());
                 */
            } while (oData2.moveToNext());
            printString(cLineDoub);

            printString("");
            printString("");
            printString("");
            return true;
        }
    }

    private boolean Imprimir_Montos2() {
        if (Global.iPrn_Data == 2) {
            try {
                printString(cLineDoub);
                printString("COLECTADO :" + String.format("%10.2f", Global.Genobj.getDouble("tot_cole")));
                printString("TIMBRES   :" + String.format("%10.2f", Global.Genobj.getDouble("tot_timb")));
                printString("IMUESTOS  :" + String.format("%10.2f", Global.Genobj.getDouble("tot_impm")));
                printString("J.C.J     :" + String.format("%10.2f", Global.Genobj.getDouble("dot__jcj")));
                printString("SERV. TEC.:" + String.format("%10.2f", Global.Genobj.getDouble("tot_tecn")));
                printString("TOT. DEVO.:" + String.format("%10.2f", Global.Genobj.getDouble("tot_devo")));
                printString("TOT. OTRO :" + String.format("%10.2f", Global.Genobj.getDouble("tot_otro")));
                printString("TOT. CRED.:" + String.format("%10.2f", Global.Genobj.getDouble("tot_cred")));
                printString("SUB-TOTAL :" + String.format("%10.2f", Global.Genobj.getDouble("Sub_tota")));
                printString("TOT. IMP. :" + String.format("%10.2f", Global.Genobj.getDouble("tot_impu")));
                printString("TOTAL     :" + String.format("%10.2f", Global.Genobj.getDouble("tot_tota")));
                printString("BRUTO CTE.:" + String.format("%10.2f", Global.Genobj.getDouble("tot_bloc")));
                printString("BRUTO EMP.:" + String.format("%10.2f", Global.Genobj.getDouble("tot_bemp")));
                printString("NETO  CTE.:" + String.format("%10.2f", Global.Genobj.getDouble("tot_nloc")));
                printString("NETO  EMP.:" + String.format("%10.2f", Global.Genobj.getDouble("tot_nemp")));
                printString(cLineDoub);

                printString("");
                printString("");
                printString("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
