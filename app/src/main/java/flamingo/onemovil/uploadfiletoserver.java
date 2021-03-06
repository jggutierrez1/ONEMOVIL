package flamingo.onemovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class uploadfiletoserver extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_FILE_REQUEST = 1;
    private static final String TAG = uploadfiletoserver.class.getSimpleName();
    private String selectedFilePath;
    //private String SERVER_URL = "http://coderefer.com/extras/UploadToServer.php";
    ImageView ivAttachment;
    Button bUpload, bExit;
    TextView tvFileName;
    ProgressDialog dialog;
    String cFilename;
    private static Context oContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadfiletoserver);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();
        oContext = this.getApplicationContext();

        Locale.setDefault(new Locale("en", "US"));

        ivAttachment = (ImageView) findViewById(R.id.ivAttachment);
        bUpload = (Button) findViewById(R.id.b_upload);
        bExit = (Button) findViewById(R.id.b_salir);

        tvFileName = (TextView) findViewById(R.id.tv_file_name);
        ivAttachment.setOnClickListener(this);
        bUpload.setOnClickListener(this);

        if (Global.iSend_Img==1){
            //on upload button Click
            if (selectedFilePath != null) {
                dialog = ProgressDialog.show(uploadfiletoserver.this, "", "Subiendo archivo: " + cFilename + "...", true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //creating new thread to handle Http Operations
                        uploadFile(selectedFilePath);
                    }
                }).start();
            } else {
                Toast.makeText(uploadfiletoserver.this, "Porfavor, seleccione el archivo a subir PRIMERO.", Toast.LENGTH_SHORT).show();
            }

        }

        bExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == ivAttachment) {

            //on attachment icon click
            showFileChooser();
        }
        if (v == bUpload) {

            //on upload button Click
            if (selectedFilePath != null) {
                dialog = ProgressDialog.show(uploadfiletoserver.this, "", "Subiendo archivo: " + cFilename + "...", true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //creating new thread to handle Http Operations
                        uploadFile(selectedFilePath);
                    }
                }).start();
            } else {
                Toast.makeText(uploadfiletoserver.this, "Porfavor, seleccione el archivo a subir PRIMERO.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent, "seleccione el archivo a subir.."), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }


                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this, selectedFileUri);
                Log.i(TAG, "Selected File Path:" + selectedFilePath);

                File file = new File(selectedFilePath);
                cFilename = file.getName();
                file = null;

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    tvFileName.setText(selectedFilePath);
                } else {
                    Toast.makeText(this, "No es posible subir el archivo al servidor.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //android upload file to server
    public int uploadFile(final String selectedFilePath) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            dialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });
            return 0;
        } else {
            Global.Check_Ip_Disp();
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(Global.SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvFileName.setText("Archivo subido exitosamente.\n\n Puedes ver el archivo en:: \n\n" + Global.SERVER_DIR_IMGS + fileName);
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(uploadfiletoserver.this, "Archivo no encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(uploadfiletoserver.this, "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(uploadfiletoserver.this, "No se cuenta con los permidos de: Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            return serverResponseCode;
        }

    }
}