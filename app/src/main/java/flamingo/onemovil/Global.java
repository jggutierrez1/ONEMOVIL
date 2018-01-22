package flamingo.onemovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.lang.String;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.content.ContentValues.TAG;
import static java.lang.System.exit;

/**
 * Created by Usuario on 01/01/2018.
 */

public class Global {
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static Integer iPrn_Data = 0;

    public static String cEmp_Id = "1";
    public static String cEmp_De = "";

    public static String cCte_Id = "";
    public static String cCte_De = "";

    public static String cMaq_Id = "";
    public static String cMaq_De = "";
    public static String cid_device = "";

    public static String PACKAGE_NAME = "";
    public static String cApp_Folder_Storage_0 = "";
    public static String cApp_Folder_Storage = "";
    public static String cStorageDirectory_0 = "";
    public static String cStorageDirectory = "";
    public static String cStorageDirectoryPhoto = "";
    public static String cApp_Data_Storage = "";
    public static String cDataStorageDirectory = "";
    public static String cFileDbPathDest = "";
    public static String cFileDbPathOrig = "";
    public static String cLastFilePhoto = "";
    public static String cLastFullPathFilePhoto = "";
    public static Uri oLastSelectedImageId = null;
    public static Context oActual_Context = null;
    public static Intent oPictureActionIntent = null;
    public static SQLiteDatabase oGen_Db;
    public static Cursor oGen_Cursor;
    public static String SERVER_URL = "http://190.140.40.242/flam/UploadToServer.php";
    public static String SERVER_URL_IMGS = "http://190.140.40.242/flam/images/";
    public static String stringResult = "";
    public static int iObj_Select = 0;

    public static void Init_Vars() {
        PACKAGE_NAME = "flamingo.onemovil";
        cApp_Folder_Storage_0 = "/FLAMINGO_APP";
        cApp_Folder_Storage = "/FLAMINGO_APP/" + cid_device;

        cStorageDirectory_0 = Environment.getExternalStorageDirectory() + "";
        cStorageDirectory = Environment.getExternalStorageDirectory() + cApp_Folder_Storage;

        cStorageDirectoryPhoto = Environment.getExternalStorageDirectory() + cApp_Folder_Storage + "/IMG_METROS";

        cApp_Data_Storage = "/data/" + PACKAGE_NAME + "/databases/";
        cDataStorageDirectory = Environment.getDataDirectory() + cApp_Data_Storage;

        cFileDbPathOrig = cApp_Data_Storage + "/one2009.db";
        cFileDbPathDest = cApp_Folder_Storage + "/one2009.db";
    }

    public static void Chech_App_Folders() {

        if (!dir_exists(cStorageDirectory_0 + cApp_Folder_Storage_0)) {
            dir_create(cStorageDirectory_0 + cApp_Folder_Storage_0);
        }

        if (!dir_exists(cStorageDirectory_0 + cApp_Folder_Storage)) {
            dir_create(cStorageDirectory_0 + cApp_Folder_Storage);
        }

        if (!dir_exists(cStorageDirectoryPhoto)) {
            dir_create(cStorageDirectoryPhoto);
        }
    }

    public static Boolean DeleteLastFilePhotoFile() {
        boolean deleted0 = false;
        boolean deleted = false;
        boolean deleted2 = false;
        boolean deleted3 = false;
        if (cLastFilePhoto.trim() == "") {
            return false;
        }
/*
        Global.oActual_Context.getContentResolver().delete(Global.oLastSelectedImageId, null, null);
        File oPhotoFile = new File(Global.cLastFullPathFilePhoto);

        if (oPhotoFile.exists()) {
            return true;
        } else {
            return false;
        }
         */

/*
        File oFile = new File(String.valueOf(oLastSelectedImageId));
        return oFile.delete();
*/
        File oPhotoFile = new File(Global.cLastFullPathFilePhoto);
        if (oPhotoFile.exists()) {
            deleted0 = oPhotoFile.getAbsoluteFile().delete();
            if (!deleted0) {
                deleted = oPhotoFile.delete();
            }
            if (!deleted) {
                try {
                    deleted2 = oPhotoFile.getCanonicalFile().delete();
                } catch (IOException e) {
                    deleted2 = false;
                }
                if (!deleted2) {
                    deleted3 = oActual_Context.getApplicationContext().deleteFile(oPhotoFile.getName());
                } else {
                    deleted3 = false;
                }
            }
            return ((deleted0 == true) || (deleted == true) || (deleted2 == true) || (deleted3 == true));
        } else {
            return false;
        }
    }

    public static void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(Global.oActual_Context, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("ExternalStorage", "Scanned " + path + ":");
                    Log.e("ExternalStorage", "-> uri=" + uri);
                }
            });
        } else {
            Log.e("-->", " < 14");
            Global.oActual_Context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    public static Boolean ExportDB() {


//        String currentDBPath = "/data/" + PACKAGE_NAME + "/databases/one2009.db";
//        String backupDBPath = cApp_Folder_Storage + "one2009.db";
//        String dir_path = cStorageDirectory;

        if (!isSDCardWriteable()) {
            //Toast.makeText(getApplicationContext(), "NO HAY MONTADO SD, NO ES POSIBLE HACER RESPALDO!", Toast.LENGTH_LONG).show();
            return false;
        }

        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        FileChannel source = null;
        FileChannel destination = null;

        File currentDB = new File(data, cFileDbPathOrig);
        File backupDB = new File(sd, cFileDbPathDest);
        if (backupDB.exists()) {
            backupDB.delete();
        }

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            //Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean ImportDB() {

        //String currentDBPath = "/data/" + PACKAGE_NAME + "/databases/one2009.db";
        //String backupDBPath = cApp_Folder_Storage + "one2009.db";

        //String dir_path = cStorageDirectory;

        if (!isSDCardWriteable()) {
            //Toast.makeText(getApplicationContext(), "NO HAY MONTADO SD, NO ES POSIBLE HACER RESPALDO!", Toast.LENGTH_LONG).show();
            return false;
        }

        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        FileChannel source = null;
        FileChannel destination = null;

        File currentDB = new File(data, cFileDbPathOrig);
        File backupDB = new File(sd, cFileDbPathDest);
        if (backupDB.exists()) {
            backupDB.delete();
        }

        try {
            source = new FileInputStream(backupDB).getChannel();
            destination = new FileOutputStream(currentDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            //Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean dir_exists(String dir_path) {
        boolean ret = false;
        File dir = new File(dir_path);
        if (dir.exists() && dir.isDirectory())
            ret = true;
        return ret;
    }

    public static void dir_create(String dir_path) {
        File directory = new File(dir_path);
        directory.mkdirs();
    }

    public static boolean isSDCardWriteable() {
        boolean rc = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            rc = true;
        }
        return rc;
    }

    public static Bitmap decodeFile(File oFile) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(oFile), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(oFile), null, o2);
        } catch (FileNotFoundException e) {
            Log.e("decodeFile", "" + e);
        }
        return null;
    }

    public static File copyFile(File oCurrent_location, File oDestination_location, int actionChoice) {
        // 1 = move the file, 2 = copy the file
        if (actionChoice == 0) {
            actionChoice = 2;
        }
        File oCopy_sourceLocation;
        File oPaste_Target_Location;

        oCopy_sourceLocation = new File("" + oCurrent_location);
        oPaste_Target_Location = new File("" + oDestination_location + "/" + Global.Get_Random_ImageFile_Name() + ".jpg");

        Log.v("Purchase-File", "sourceLocation: " + oCopy_sourceLocation);
        Log.v("Purchase-File", "targetLocation: " + oPaste_Target_Location);
        try {
            // moving the file to another directory
            if (actionChoice == 1) {
                if (oCopy_sourceLocation.renameTo(oPaste_Target_Location)) {
                    Log.i("Purchase-File", "Move file successful.");
                } else {
                    Log.i("Purchase-File", "Move file failed.");
                }
            }

            // we will copy the file
            else {
                // make sure the target file exists
                if (oCopy_sourceLocation.exists()) {

                    InputStream in = new FileInputStream(oCopy_sourceLocation);
                    OutputStream out = new FileOutputStream(oPaste_Target_Location);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    Log.i("copyFile", "Copy file successful.");

                } else {
                    Log.i("copyFile", "Copy file failed. Source file missing.");
                }
            }

        } catch (NullPointerException e) {
            Log.i("copyFile", "" + e);

        } catch (Exception e) {
            Log.i("copyFile", "" + e);
        }
        return oPaste_Target_Location;
    }

    public static String Get_Random_ImageFile_Name() {
        final Calendar oCalendar = Calendar.getInstance();
        int myYear = oCalendar.get(Calendar.YEAR);
        int myMonth = oCalendar.get(Calendar.MONTH);
        int myDay = oCalendar.get(Calendar.DAY_OF_MONTH);
        String cRandom_Image_Text = "" + myDay + myMonth + myYear + "_" + Math.random();
        return cRandom_Image_Text;
    }

    public Uri getImageUri(Context oInContext, Bitmap oInImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        oInImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String cPath = MediaStore.Images.Media.insertImage(oInContext.getContentResolver(), oInImage, "Title", null);
        return Uri.parse(cPath);
    }

    public static String getRealPathFromURI(Uri oUri) {
        Cursor oCursor = Global.oActual_Context.getContentResolver().query(oUri, null, null, null, null);
        oCursor.moveToFirst();
        int idx = oCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return oCursor.getString(idx);
    }

    private static String bytesToHexString(byte[] bytes) {
        char[] digits = DIGITS;
        char[] buf = new char[bytes.length * 2];
        int c = 0;
        for (byte b : bytes) {
            buf[c++] = digits[(b >> 4) & 0xf];
            buf[c++] = digits[b & 0xf];
        }
        return new String(buf);
    }

    public static JSONArray cur2Json(Cursor cursor) {
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int nColumns = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < nColumns; i++) {
                String colName = cursor.getColumnName(i);
                if (colName != null) {
                    String val = "";
                    try {
                        switch (cursor.getType(i)) {
                            case Cursor.FIELD_TYPE_BLOB:
                                rowObject.put(colName, cursor.getBlob(i).toString());
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                rowObject.put(colName, cursor.getDouble(i));
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                rowObject.put(colName, cursor.getLong(i));
                                break;
                            case Cursor.FIELD_TYPE_NULL:
                                rowObject.put(colName, null);
                                break;
                            case Cursor.FIELD_TYPE_STRING: {
                                String sValue1 = cursor.getString(i);
                                String sValue2 = sValue1.trim().replaceAll(" ", "_");
                                rowObject.put(colName, sValue2);
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            if (!cursor.moveToNext())
                break;
        }
        cursor.close(); // close the cursor
        return resultSet;
    }

    public static String cursorToString(Cursor crs) {
        JSONArray arr = new JSONArray();
        crs.moveToFirst();
        while (!crs.isAfterLast()) {
            int nColumns = crs.getColumnCount();
            JSONObject row = new JSONObject();
            for (int i = 0; i < nColumns; i++) {
                String colName = crs.getColumnName(i);
                if (colName != null) {
                    String val = "";
                    try {
                        switch (crs.getType(i)) {
                            case Cursor.FIELD_TYPE_BLOB:
                                row.put(colName, crs.getBlob(i).toString());
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                row.put(colName, crs.getDouble(i));
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                row.put(colName, crs.getLong(i));
                                break;
                            case Cursor.FIELD_TYPE_NULL:
                                row.put(colName, null);
                                break;
                            case Cursor.FIELD_TYPE_STRING: {
                                String sValue1 = crs.getString(i);
                                String sValue2 = sValue1.trim().replaceAll(" ", "_");
                                row.put(colName, sValue2);
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }
            arr.put(row);
            if (!crs.moveToNext())
                break;
        }
        crs.close(); // close the cursor
        return arr.toString();
    }

    public static JSONArray getJsonResults(String cSql_Cmd) {

        Cursor data;

        String myPath = Global.oActual_Context.getDatabasePath("one2009.db").getPath();

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        String searchQuery = cSql_Cmd;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);

        JSONArray resultSet = new JSONArray();

        resultSet = Global.cur2Json(cursor);
        return resultSet;
    }

    public static String getJsonResults2(String cSql_Cmd, String cTableName) {
        JSONArray resultSet = new JSONArray();
        JSONObject resultSet2 = new JSONObject();

        resultSet = getJsonResults(cSql_Cmd);
        try {
            resultSet2.put(cTableName, resultSet);
            Global.logLargeString(resultSet.toString());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return resultSet2.toString();
    }

    public static void logLargeString(String str) {
        if (str.length() > 3000) {
            Log.i(TAG, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(TAG, str); // continuation
        }
    }

    public static String INCOMPLETE_Cursor_To_MySql_Sentense(String cSql_Ln, String cTblName, int iLimitInserts) {
        String myPath = Global.oActual_Context.getDatabasePath("one2009.db").getPath();
        Global.oGen_Db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        Global.oGen_Cursor = oGen_Db.rawQuery(cSql_Ln, null);


        int iIdx = 0;
        String cStr_Fields = "";
        String cStr_Values = "";
        String cSql_String = "";

        String cSql_Insert_Cab = "";
        String cSql_Insert_Val = "";
        String cSql_Insert_Fin = "";

        String cColumnName = "";
        int iRegs_Index = 0;
        int iRows_Count = 0;
        int iFields_Cnt = 0;
        int iNext_Limit = 0;

        iRows_Count = oGen_Cursor.getCount();
        iFields_Cnt = oGen_Cursor.getColumnCount();

        if (iRows_Count == 0) {
            Toast.makeText(Global.oActual_Context, "No hay registros que procesar!!!", Toast.LENGTH_LONG).show();
        } else {
            //cStr_Fields += "(";
            for (iIdx = 0; iIdx <= iFields_Cnt - 1; iIdx++) {
                cColumnName = oGen_Cursor.getColumnName(iIdx);
                if (cColumnName != null) {
                    cColumnName = oGen_Cursor.getColumnName(iIdx);
                    cStr_Fields += cColumnName;
                    if (iIdx != iFields_Cnt)
                        cStr_Fields += ",";
                }

            }
            cStr_Values = "";
            cSql_Insert_Val = "";
            if (oGen_Cursor != null && oGen_Cursor.moveToFirst()) {
                iRegs_Index = 1;
                iNext_Limit = 1;
                do {
                    iIdx = 0;
                    //cStr_Values += "(";
                    for (iIdx = 0; iIdx <= iFields_Cnt - 1; iIdx++) {
                        try {
                            switch (oGen_Cursor.getType(iIdx)) {
                                case Cursor.FIELD_TYPE_INTEGER:
                                    if (oGen_Cursor.getString(iIdx) == null)
                                        cStr_Values += "\"" + "0" + "\"";
                                    else
                                        cStr_Values += "\"" + Integer.valueOf(oGen_Cursor.getString(iIdx)).toString() + "\"";
                                    break;
                                case Cursor.FIELD_TYPE_FLOAT:
                                    if (oGen_Cursor.getString(iIdx) == null)
                                        cStr_Values += "\"" + "0.00" + "\"";
                                    else
                                        cStr_Values += "\"" + Double.valueOf(oGen_Cursor.getString(iIdx)).toString() + "\"";
                                    break;
                                case Cursor.FIELD_TYPE_STRING:
                                    if (oGen_Cursor.getString(iIdx) == null)
                                        cStr_Values += "NULL";
                                    else
                                        cStr_Values += "\"" + oGen_Cursor.getString(iIdx) + "\"";
                                    break;
                                case Cursor.FIELD_TYPE_BLOB:
                                    if (oGen_Cursor.getString(iIdx) == null)
                                        cStr_Values += "NULL";
                                    else
                                        cStr_Values += "\"" + bytesToHexString(oGen_Cursor.getBlob(iIdx)) + "\"";
                                    break;
                            }
                        } catch (Exception ex) {
                            //Log.e(TAG, "Exception converting cursor column to json field: " + cName);
                        }

                        if (iIdx != iFields_Cnt)
                            cStr_Values += ",";
                    }
                    String cStr_Values2 = cStr_Values.substring(1, cStr_Values.length() - 1);

                    cSql_Insert_Val += "(" + cStr_Values2 + ")," + (char) 13;
                    if ((iRegs_Index == 1) || (iRegs_Index == iRows_Count) || (iNext_Limit > iLimitInserts)) {
                        iNext_Limit = 1;
                        cSql_Insert_Val += ";";
                        cSql_Insert_Cab = "INSERT INTO " + cTblName + " (" + cStr_Fields + ") VALUES " + (char) 13 + "(" + cStr_Values2 + ");" + (char) 13;
                    } else {
                        cSql_Insert_Cab = "";
                    }

                    cSql_Insert_Fin += cSql_Insert_Cab + cSql_Insert_Val + (char) 13;
                    Log.d("TAG_NAME", cSql_Insert_Fin);

                    iRegs_Index++;
                    iNext_Limit++;

                } while (oGen_Cursor.moveToNext());
            }
            //ON DUPLICATE KEY UPDATE ".$str_update. "";
        }
        Log.d("TAG_NAME", cSql_Insert_Fin);
        return cSql_Insert_Fin;


    }

    public static String POST_Send_Json(String url_server, String Json) {
        try {
//            URL url = new URL("http://[ip]:[port]"); //in the real code, there is an ip and a port
            URL url = new URL(url_server); //in the real code, there is an ip and a port
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("EMPRESA", 0);
            jsonParam.put("MACHINE", Global.cid_device);
            jsonParam.put("DATA", Json);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            String encode = URLEncoder.encode(jsonParam.toString(), "UTF-8");
            //os.writeBytes(encode);
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG", conn.getResponseMessage());
            String Response = conn.getResponseMessage();

            conn.disconnect();
            return Response;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return "ERROR AL ENVIAR EL JSON ";
        }
    }

    public void UploadFile2() {
        try {
            // Set your file path here
            FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + "/DCIM/file.mp4");

            // Set your server page url (and the file title/description)
            HttpFileUpload hfu = new HttpFileUpload("http://www.myurl.com/fileup.aspx", "my file title", "my file description");

            hfu.Send_Now(fstrm);

        } catch (FileNotFoundException e) {
            // Error: File not found
        }
    }

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
            Global.logLargeString("Source File Doesn't Exist: " + selectedFilePath);
            return 0;
        } else {
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

                    Runnable runnable = new Runnable() {
                        public void run() {
                            Toast.makeText(Global.oActual_Context, "File Upload completed.\n\n You can see the uploaded file here: \n\n" + Global.SERVER_URL + fileName, Toast.LENGTH_SHORT).show();
                        }
                    };

                    Thread mythread = new Thread(runnable);
                    mythread.start();
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();

                Runnable runnable = new Runnable() {
                    public void run() {
                        Toast.makeText(Global.oActual_Context, "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                };
                Thread mythread = new Thread(runnable);
                mythread.start();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(Global.oActual_Context, "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(Global.oActual_Context, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            return serverResponseCode;
        }

    }

    // return password
    public static String ShowPasswordBox(
            Context context, String title, String message, String positiveMessage, String negativeMessage) {
        Global.stringResult = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        // Set up the input
        final EditText input = new EditText(context);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(positiveMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Global.stringResult = input.getText().toString();

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(negativeMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });


        builder.create().show();

        return Global.stringResult;
    }

}

