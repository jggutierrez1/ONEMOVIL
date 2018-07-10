package flamingo.onemovil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Collections;
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
    private Cursor oData1, oData2;
    private String cSqlLn = "";
    private String cDatabasePath = "";
    private ArrayList<String> theList = new ArrayList<>();
    private static final int iLineNo = 69;
    private String cLineSing = String.format("%0" + iLineNo + "d", 0).replace("0", "-");
    private String cLineDoub = String.format("%0" + iLineNo + "d", 0).replace("0", "=");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_data);

        Locale.setDefault(new Locale("en", "US"));

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();

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

        this.oDb6 = openOrCreateDatabase(cDatabasePath, Context.MODE_PRIVATE, null);

        try {
            disconnectBT();
            FindBluetoothDevice();
            openBluetoothPrinter();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        switch (Global.iPrn_Data) {
            case 1: {
                Listar_Maquinas();
                Listar_Montos();
            }
            break;
            case 2: {
                Listar_Maquinas();
                Listar_Montos();
            }
            break;
            default: {
            }
            ;
        }

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
                Imprimir_Maquinas();
                if (Global.iPrn_Data == 1)
                    Imprimir_Montos();
                if (Global.iPrn_Data == 2)
                    Imprimir_Montos2();

/*                try {
                    printData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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

    private boolean Listar_Maquinas() {
        String cPrnLn="";
        cSqlLn = "";
        cSqlLn += "SELECT ";
        cSqlLn += "trim(op.op_chapa ) || SUBSTR('                   ', 1, 12-length(trim(op.op_chapa ))) || ";
        cSqlLn += "trim(op.op_modelo) || SUBSTR('                   ', 1, 20-length(trim(op.op_modelo))) AS expr1, ";
        cSqlLn += " op.op_chapa, ";
        cSqlLn += " op.op_modelo, ";
        cSqlLn += " SUM(op.op_tot_colect) AS tot_cole, ";
        cSqlLn += " SUM(op.op_tot_cred)   AS tot_cred, ";
        cSqlLn += " (SUM(op.op_tot_colect)-SUM(op.op_tot_cred)) AS tot_dife ";
        cSqlLn += "FROM operacion op ";
        cSqlLn += "WHERE (op.id_device='" + Global.cid_device + "') ";
        cSqlLn += "AND   (op.op_emp_id='" + Global.cEmp_Id + "') ";
        cSqlLn += "AND   (op.cte_id   ='" + Global.cCte_Id + "') ";
        cSqlLn += "GROUP BY op.op_emp_id,op.op_chapa ";
        cSqlLn += "ORDER BY op.op_emp_id,op.op_modelo ";

        Log.d("SQL", cSqlLn);
        oData1 = oDb6.rawQuery(cSqlLn, null);

        if ((oData1 == null) || (oData1.getCount() == 0)) {
            return false;
        } else {

            oData1.moveToFirst();
            do {
                cPrnLn = String.format("%-8s %-20s %12.2f %12.2f %12.2f",
                        oData1.getString(1).trim(),
                        oData1.getString(2).trim(),
                        oData1.getDouble(3),
                        oData1.getDouble(4),
                        oData1.getDouble(5)
                );

                //theList.add(oData1.getString(0));
                theList.add(cPrnLn);
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
                ListMaq.setAdapter(listAdapter);

            } while (oData1.moveToNext());
            return true;
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
        if ((oData1 == null) || (oData1.getCount() == 0)) {
            return false;
        } else {
            printString("");
            printString(Global.center(Global.cEmp_De.toUpperCase(), iLineNo, ' '));
            printString(Global.center("LISTADO DE MAQUINAS COLECTADAS", iLineNo, ' '));
            printString(Global.center("CLIENTE: [" + Global.cCte_Id + "]/[" + Global.cCte_De + "]", iLineNo, ' '));
            cPrnLn = String.format("%-8s %-20s %12s %12s %12s", "CHAPA", "JUEGO", "ENTARDA", "SALIDA", "DIF.");
            printString(cPrnLn);
            printString(cLineSing);
            oData1.moveToFirst();
            do {
                cPrnLn = String.format(Locale.US, "%-8s %-20s %12.2f %12.2f %12.2f",
                        oData1.getString(1).trim(),
                        oData1.getString(2).trim(),
                        oData1.getDouble(3),
                        oData1.getDouble(4),
                        oData1.getDouble(5)
                );
                printString(cPrnLn);
            } while (oData1.moveToNext());
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
                cPrnLn = String.format(Locale.US,"%-8s %-20s %12.2f %12.2f %12.2f",
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
