package flamingo.onemovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.lang.String;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;
import static java.lang.System.exit;

/**
 * Created by Usuario on 01/01/2018.
 */

public class Global {
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static Integer iPrn_Data = 0;
    public static Integer iSend_Img = 0;

    public static String cEmp_Id = "0";
    public static String cEmp_De = "SIN EMPRESA!!!!";

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
    public static String SERVER_URL = "http://190.140.40.242";
    //public static String SERVER_URL = "http://192.168.2.161";
    public static String SERVER_URL_FLES = SERVER_URL + "/flam/UploadToServer.php";
    public static String SERVER_DIR_IMGS = SERVER_URL + "/flam/images/";
    public static String stringResult = "";
    public static int iObj_Select = 0;
    public static Boolean ValidateOk = false;
    public static String PasswChgAmmout = "";
    public static String PasswChgMeters = "";
    public static int Correl_Device;
    public static JSONObject Genobj;
    public static String DialogConfirmText = "";

    public static void Init_Vars() {
        PACKAGE_NAME = "flamingo.onemovil";
        cApp_Folder_Storage_0 = "/FLAMINGO_APP";
        cApp_Folder_Storage = "/FLAMINGO_APP/" + cid_device;

        cStorageDirectory_0 = Environment.getExternalStorageDirectory() + "";
        cStorageDirectory = Environment.getExternalStorageDirectory() + cApp_Folder_Storage;

        cStorageDirectoryPhoto = Environment.getExternalStorageDirectory() + cApp_Folder_Storage + "/IMG_METROS";

        cApp_Data_Storage = "/data/data/" + PACKAGE_NAME + "/databases/";
        cDataStorageDirectory = Environment.getDataDirectory() + cApp_Data_Storage;

        cFileDbPathOrig = cApp_Data_Storage + "one2009.db";
        cFileDbPathDest = cApp_Folder_Storage + "/one2009.db";
    }

    public static void Get_Config() {

        String cSql_Ln = "" +
                "SELECT clave_metros, clave_montos, emp_id " +
                "FROM dispositivos " +
                "WHERE serial='" + Global.cid_device + "' ";

        Global.oGen_Cursor = Global.oGen_Db.rawQuery(cSql_Ln, null);
        Global.oGen_Cursor.moveToFirst();
        int iRecords = Global.oGen_Cursor.getCount();
        if (iRecords > 0) {
            //Global.Correl_Device = Global.oGen_Cursor.getInt(Global.oGen_Cursor.getColumnIndex("corre_act"));
            Global.PasswChgAmmout = Global.oGen_Cursor.getString(Global.oGen_Cursor.getColumnIndex("clave_montos"));
            Global.PasswChgMeters = Global.oGen_Cursor.getString(Global.oGen_Cursor.getColumnIndex("clave_metros"));
            //Global.cEmp_Id = Global.oGen_Cursor.getString(Global.oGen_Cursor.getColumnIndex("emp_id"));
        } else {
            //Global.Correl_Device = 0;
            Global.PasswChgAmmout = Global.GenerateRandomNumber(5);
            Global.PasswChgMeters = Global.GenerateRandomNumber(5);
        }
        Global.oGen_Cursor.close();
        Locale.setDefault(new Locale("en", "US"));
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
                                String sValue2 = sValue1.trim().replaceAll(" ", "^");
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
                                String sValue2 = sValue1.trim().replaceAll(" ", "^");
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
            resultSet2.put("tot_regs", resultSet.length());
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
                URL url = new URL(SERVER_URL_FLES);
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
                            Toast.makeText(Global.oActual_Context, "File Upload completed.\n\n You can see the uploaded file here: \n\n" + SERVER_URL_FLES + fileName, Toast.LENGTH_SHORT).show();
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

    public static String gen_execute_post(String cMyurl, String cWebServices, String parameters) {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;
        //String parameters = "table_no=0";
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            //url = new URL("http://192.168.2.82/flam/get_all_data.php");

;            url = new URL(cMyurl + cWebServices);
            Log.d(TAG, "CONECTADO A:[" + cMyurl + "]");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json;charset=UTF-8");

            connection.setRequestMethod("POST");

            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(parameters);
            request.flush();
            request.close();

            int responseCode = connection.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + parameters.toString());
            System.out.println("Response Code : " + responseCode);


            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            response = sb.toString().trim();
            // Response from server after login process will be stored in response variable.
            System.out.println("Response String : " + response);

            // You can perform UI operations here
            //Toast.makeText(getApplicationContext(), "Message from Server: \n" + response, Toast.LENGTH_LONG).show();
            isr.close();
            reader.close();

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;
    }

    public static boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(cFileDbPathOrig, null, SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
            return true;
        } catch (SQLiteException e) {
            return false;
            // database doesn't exist yet.
        }
    }

    public static void check_tables_device(Boolean bDrop) {
        String cSql_Ln = "";
        if (bDrop == true) {
            cSql_Ln = "";
            cSql_Ln += "DROP TABLE IF EXISTS dispositivos;";
            try {
                Global.oGen_Db.execSQL(cSql_Ln);
            } catch (SQLiteException e) {
                Global.logLargeString(e.toString());
            }

            cSql_Ln = "";
            cSql_Ln += "CREATE TABLE IF NOT EXISTS dispositivos (";
            cSql_Ln += "id              INTEGER NOT NULL,";
            cSql_Ln += "serial          VARCHAR(50) NOT NULL DEFAULT '',";
            cSql_Ln += "clave_install   VARCHAR(30) NULL DEFAULT 'FLAM111118',";
            cSql_Ln += "fecha_pacceso   DATETIME NULL DEFAULT NULL,";
            cSql_Ln += "fecha_uacces    DATETIME NULL DEFAULT NULL,";
            cSql_Ln += "dbname          VARCHAR(30) NULL DEFAULT 'one2009_1',";
            cSql_Ln += "corre_ant       INT(10) NULL DEFAULT 0,";
            cSql_Ln += "corre_act       INT(10) NULL DEFAULT 0,";
            cSql_Ln += "clave_metros    VARCHAR(30) NULL DEFAULT '6545465465465546',";
            cSql_Ln += "clave_montos    VARCHAR(30) NULL DEFAULT '6545465465465530',";
            cSql_Ln += "emp_id          INT(3) NULL DEFAULT 0,";
            cSql_Ln += "CONSTRAINT dispositivos PRIMARY KEY (id))";
            try {
                Global.oGen_Db.execSQL(cSql_Ln);
            } catch (SQLiteException e) {
                Global.logLargeString(e.toString());
            }
        }
    }

    public static void active_device(String cClave) {
        String cSql_Ln = "";
        String cNow = Global.getNow();

        Global.oGen_Db.execSQL("DELETE FROM dispositivos");

        cSql_Ln += "INSERT INTO dispositivos(serial, fecha_pacceso,clave_install,corre_ant,corre_act) VALUES ";
        cSql_Ln += "('" + Global.cid_device + "','" + cNow + "','" + cClave + "',0,0)";
        Global.oGen_Db.execSQL(cSql_Ln);
    }

    public static void clear_tables_device() {
        String cSql_Ln = "";

        Global.oGen_Db.execSQL("DROP TABLE IF EXISTS dispositivos");
        Global.oGen_Db.execSQL("DROP TABLE IF EXISTS empresas");
        Global.oGen_Db.execSQL("DROP TABLE IF EXISTS clientes");
        Global.oGen_Db.execSQL("DROP TABLE IF EXISTS denominaciones");
        Global.oGen_Db.execSQL("DROP TABLE IF EXISTS maquinastc");
        Global.oGen_Db.execSQL("DROP TABLE IF EXISTS maquinas_lnk");
        Global.oGen_Db.execSQL("DROP TABLE IF EXISTS municipios");
        Global.oGen_Db.execSQL("DROP TABLE IF EXISTS rutas");
     }

    public static int check_device() {
        String cNow = Global.getNow();

        String cSql_Ln = "SELECT serial, fecha_pacceso, clave_install FROM dispositivos WHERE serial='" + Global.cid_device + "'";
        Global.oGen_Cursor = Global.oGen_Db.rawQuery(cSql_Ln, null);
        Global.oGen_Cursor.moveToFirst();
        int iRecords = Global.oGen_Cursor.getCount();
        Global.oGen_Cursor.close();
        return iRecords;
    }

    public static boolean isFilePresent(String fileNamePath) {
        //tring path = Global.oActual_Context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(fileNamePath);
        return file.exists();
    }

    public static String CenterString(String s, int size) {
        return center(s, size, ' ');
    }

    public static String center(String s, int size, char pad) {
        if (s == null || size <= s.length())
            return s;

        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while (sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
    }

    public static String repeat(char what, int howmany) {
        char[] chars = new char[howmany];
        Arrays.fill(chars, what);
        return new String(chars);
    }

    public static String repChar(char c, int reps) {
        String adder = Character.toString(c);
        String result = "";
        while (reps > 0) {
            if (reps % 2 == 1) {
                result += adder;
            }
            adder += adder;
            reps /= 2;
        }
        return result;
    }

    public static String Query_Result(String cSql, String FieldResult) {
        String cValue = "";
        Global.oGen_Cursor = Global.oGen_Db.rawQuery(cSql, null);
        Global.oGen_Cursor.moveToFirst();
        int iRecords = Global.oGen_Cursor.getCount();
        int iColumIndex = Global.oGen_Cursor.getColumnIndex(FieldResult);
        if (iRecords > 0) {
            if (Global.oGen_Cursor.isNull(iColumIndex) == true) {
                cValue = "";
            } else {
                cValue = Global.oGen_Cursor.getString(iColumIndex);
            }
        } else {
            cValue = "";
        }
        Global.oGen_Cursor.close();
        return cValue;
    }

    public static String Rem_Query_Result(String cDatabase, String Sql_Cmd, int All_rows, String FieldResult) {
        String cParsString = "";
        cParsString += "database=" + cDatabase;
        cParsString += "&sql=" + Sql_Cmd;
        cParsString += "&key=1";
        cParsString += "&all_rows=" + String.valueOf(All_rows);
        cParsString += "&fieldname=" + FieldResult;

        String RetValue = Global.gen_execute_post(Global.SERVER_URL, "/flam/execute_sql.php", cParsString);
        return RetValue;
    }

    public static void Query_Update(String Sql) {
        Global.oGen_Db.execSQL(Sql);
    }

    public static String GenerateRandomNumber(int charLength) {
        return String.valueOf(charLength < 1 ? 0 : new Random()
                .nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
                + (int) Math.pow(10, charLength - 1));
    }

    public static String getNow() {
        // set the format to sql date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static Boolean Clients_Is_Empty() {
        String cValue = "";
        String Sql = "SELECT cte_id FROM clientes LIMIT 1";
        Boolean bResult = true;
        Global.oGen_Cursor = Global.oGen_Db.rawQuery(Sql, null);
        Global.oGen_Cursor.moveToFirst();
        int iRecords = Global.oGen_Cursor.getCount();
        if (iRecords > 0) {
            bResult = false;
        } else {
            bResult = true;
        }
        Global.oGen_Cursor.close();
        return bResult;
    }

    public static void Create_Sql_Tables(Boolean bDropTables, Boolean bDropColects) {
        String cSql_Ln = "";

        if (bDropTables == true) {
            Global.oGen_Db.execSQL("DROP TABLE IF EXISTS dispositivos");
            Global.oGen_Db.execSQL("DROP TABLE IF EXISTS empresas");
            Global.oGen_Db.execSQL("DROP TABLE IF EXISTS clientes");
            Global.oGen_Db.execSQL("DROP TABLE IF EXISTS denominaciones");
            Global.oGen_Db.execSQL("DROP TABLE IF EXISTS maquinastc");
            Global.oGen_Db.execSQL("DROP TABLE IF EXISTS maquinas_lnk");
            Global.oGen_Db.execSQL("DROP TABLE IF EXISTS municipios");
            Global.oGen_Db.execSQL("DROP TABLE IF EXISTS rutas");
        }

        if (bDropColects == true) {
            Global.oGen_Db.execSQL("DROP TABLE IF EXISTS operacion");
            Global.oGen_Db.execSQL("DROP TABLE IF EXISTS operaciong");
        }

        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS dispositivos (";
        cSql_Ln += "id              INTEGER NOT NULL,";
        cSql_Ln += "serial          VARCHAR(50) NOT NULL DEFAULT '',";
        cSql_Ln += "clave_install   VARCHAR(30) NULL DEFAULT 'FLAM111118',";
        cSql_Ln += "fecha_pacceso   DATETIME NULL DEFAULT NULL,";
        cSql_Ln += "fecha_uacces    DATETIME NULL DEFAULT NULL,";
        cSql_Ln += "dbname          VARCHAR(30) NULL DEFAULT 'one2009_1',";
        cSql_Ln += "corre_ant       INT(10) NULL DEFAULT 0,";
        cSql_Ln += "corre_act       INT(10) NULL DEFAULT 0,";
        cSql_Ln += "clave_metros    VARCHAR(30) NULL DEFAULT '6545465465465546',";
        cSql_Ln += "clave_montos    VARCHAR(30) NULL DEFAULT '6545465465465530',";
        cSql_Ln += "emp_id          INT(3) NULL DEFAULT 0,";
        cSql_Ln += "CONSTRAINT dispositivos PRIMARY KEY (id))";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS clientes (";
        cSql_Ln += "cte_id          INTEGER NOT NULL,";
        cSql_Ln += "mun_id          INTEGER NULL DEFAULT 1,";
        cSql_Ln += "rut_id          INTEGER NULL DEFAULT 1,";
        cSql_Ln += "cte_nombre_loc  CHAR(80) NULL DEFAULT '',";
        cSql_Ln += "cte_nombre_com  CHAR(80) NULL DEFAULT '',";
        cSql_Ln += "cte_telefono1   CHAR(25) NULL DEFAULT '',";
        cSql_Ln += "cte_telefono2   CHAR(25) NULL DEFAULT '',";
        cSql_Ln += "cte_Fax         CHAR(25) NULL DEFAULT '',";
        cSql_Ln += "cte_contacto_nom    CHAR(35) NULL DEFAULT '',";
        cSql_Ln += "cte_contacto_movil  CHAR(25) NULL DEFAULT '',";
        cSql_Ln += "cte_contacto_movil_bbpin CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "cte_contacto_movil_email CHAR(120) NULL DEFAULT '',";
        cSql_Ln += "cte_email       CHAR(120) NULL DEFAULT '',";
        cSql_Ln += "cte_webpage     VARCHAR(130) NULL DEFAULT '',";
        cSql_Ln += "cte_notas       TEXT NULL,";
        cSql_Ln += "cte_inactivo    INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_direccion   TEXT NULL, ";
        cSql_Ln += "cte_pag_impm    INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_pag_jcj     INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_pag_spac    INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_poc_ret     NUMERIC(12, 2) NULL DEFAULT 0,";
        cSql_Ln += "cte_fecha_alta  DATETIME NULL ,";
        cSql_Ln += "cte_fecha_modif DATETIME NULL ,";
        cSql_Ln += "cte_emp_id      INTEGER NULL DEFAULT 0,";
        cSql_Ln += "u_usuario_alta  CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "cte_unico_emp   INTEGER NULL DEFAULT 0,";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "cte_mod_metro_ant INTEGER NULL DEFAULT 0,";
        cSql_Ln += "CONSTRAINT clientes_PRIMARY PRIMARY KEY (cte_id))";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_inactivo ON clientes('cte_inactivo')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_nombre_loc ON clientes('cte_nombre_loc')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS mun_id ON clientes('mun_id')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_nombre_com ON clientes('cte_nombre_com')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS rut_id ON clientes('rut_id')";
        Global.oGen_Db.execSQL(cSql_Ln);
        // --------------------------------FIN CLIENTES-----------------------------------------------------//

        // -----------------------------------DENOMINACIONES------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS denominaciones (";
        cSql_Ln += "den_id          INTEGER NOT NULL,";
        cSql_Ln += "den_descripcion CHAR(30) NULL DEFAULT '',";
        cSql_Ln += "den_inactiva    INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_fact_e      INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_fact_s      INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_valor       NUMERIC(12, 2) NULL DEFAULT 0,";
        cSql_Ln += "den_fecha_alta  DATETIME NULL ,";
        cSql_Ln += "den_fecha_modif DATETIME NULL ,";
        cSql_Ln += "u_usuario_alta  CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "CONSTRAINT denominaciones_PRIMARY PRIMARY KEY (den_id))";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS den_inactiva ON denominaciones('den_inactiva')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS den_descripcion ON denominaciones('den_descripcion')";
        Global.oGen_Db.execSQL(cSql_Ln);
        // -------------------------------FIN DENOMINACIONES------------------------------------------------//

        // ---------------------------------EMPRESA----------------------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS empresas ( ";
        cSql_Ln += "emp_id      INTEGER NOT NULL,";
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
        cSql_Ln += "emp_corre_ant INT(10) NULL DEFAULT '0',";
        cSql_Ln += "emp_corre_act INT(10) NULL DEFAULT '0',";
        cSql_Ln += "u_usuario_alta CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "emp_clave_metros CHAR(30) NULL DEFAULT '112233',";
        cSql_Ln += "emp_clave_montos CHAR(30) NULL DEFAULT '332211',";
        cSql_Ln += "CONSTRAINT empresas_PRIMARY PRIMARY KEY (emp_id))";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS emp_inactivo ON empresas ('emp_inactivo')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS emp_descripcion ON empresas ('emp_descripcion')";
        Global.oGen_Db.execSQL(cSql_Ln);
        // ----------------------------------FIN EMPRESAS---------------------------------------------------//

        // ---------------------------------MAQUINASTC------------------------------------------------------//
        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS maquinastc (";
        cSql_Ln += "maqtc_id        INTEGER NOT NULL,";
        cSql_Ln += "maqtc_cod       CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "maqtc_modelo    CHAR(60) NULL DEFAULT '',";
        cSql_Ln += "maqtc_chapa     CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "maqtc_inactivo  INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_metros    INTEGER NULL DEFAULT 1,";
        cSql_Ln += "emp_id          INTEGER NULL DEFAULT 1,";
        cSql_Ln += "maqtc_denom_e   INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_tipomaq   INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_denom_s   INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_m1e_act   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "maqtc_m1e_ant   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "maqtc_m1s_ant   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "maqtc_m2e_act   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "maqtc_m2e_ant   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "maqtc_m2s_act   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "maqtc_m2s_ant   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "maqtc_m1s_act   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "maqtc_mfecha    DATETIME NULL ,";
        cSql_Ln += "maqtc_fecha_alta    DATETIME NULL ,";
        cSql_Ln += "maqtc_fecha_modif   DATETIME NULL ,";
        cSql_Ln += "u_usuario_alta  CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "maqtc_sem_jcj   INTEGER NULL DEFAULT '1',";
        cSql_Ln += "CONSTRAINT maquinastc_PRIMARY PRIMARY KEY (maqtc_id))";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS maqtc_cod ON maquinastc('maqtc_cod')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS maqtc_chapa ON maquinastc('maqtc_chapa')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS maqtc_inactivo ON maquinastc('maqtc_inactivo')";
        Global.oGen_Db.execSQL(cSql_Ln);
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
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS emp_id ON maquinas_lnk('emp_id')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS maqtc_id ON maquinas_lnk('maqtc_id')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_id ON maquinas_lnk('cte_id')";
        Global.oGen_Db.execSQL(cSql_Ln);
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
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS mun_nombre ON municipios ('mun_nombre')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS mun_inactivo ON municipios ('mun_inactivo')";
        Global.oGen_Db.execSQL(cSql_Ln);
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
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS rut_id ON rutas ('rut_id')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS rut_inactivo ON rutas ('rut_inactivo')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS rut_nombre ON rutas ('rut_nombre')";
        Global.oGen_Db.execSQL(cSql_Ln);
        // -------------------------------FIN RUTAS --------------------------------------------------------//

        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS operacion (";
        cSql_Ln += "id_op           INTEGER NOT NULL,";
        cSql_Ln += "MaqLnk_Id       INTEGER NOT NULL DEFAULT 0,";
        cSql_Ln += "cte_id          INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_nombre_loc  CHAR(60) NOT NULL DEFAULT '',";
        cSql_Ln += "cte_nombre_com  CHAR(60) NOT NULL DEFAULT '',";
        cSql_Ln += "cte_pag_jcj     INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_pag_spac    INTEGER NULL DEFAULT 0,";
        cSql_Ln += "cte_pag_impm    INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_denom_e   INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_denom_s   INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_valore      NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "den_valors      NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "den_fact_e      INTEGER NULL DEFAULT 0,";
        cSql_Ln += "den_fact_s      INTEGER NULL DEFAULT 0,";
        cSql_Ln += "maqtc_tipomaq   INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_cporc_Loc    NUMERIC(6, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_serie        INTEGER NULL DEFAULT 1,";
        cSql_Ln += "op_chapa        CHAR(12) NULL DEFAULT '',";
        cSql_Ln += "op_fecha        DATETIME NULL,";
        cSql_Ln += "op_nodoc        CHAR (12) NULL DEFAULT '',";
        cSql_Ln += "op_modelo       CHAR(60) NULL DEFAULT '',";
        cSql_Ln += "op_e_pantalla   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_ea_metroan   NUMERIC(12, 2) DEFAULT 0.000,";
        cSql_Ln += "op_ea_metroac   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_ea_met       NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_sa_metroan   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_sa_metroac   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_sa_met       NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_eb_metroan   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_eb_metroac   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_eb_met       NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_sb_metroan   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_sb_metroac   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_sb_met       NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_s_pantalla   NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_cal_colect   NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_colect   NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_impmunic  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_impjcj   NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_timbres  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_spac     NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_tec      NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_dev      NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_otros    NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_cred     NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_cal_cred     NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_sub      NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_itbm     NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_tot      NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_brutoloc NUMERIC(6, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_brutoemp NUMERIC(6, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_netoloc  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_tot_netoemp  NUMERIC(12, 2) NULL DEFAULT 0.00,";
        cSql_Ln += "op_observ       TEXT,";
        cSql_Ln += "op_num_sem      INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_fecha_alta   DATETIME NULL,";
        cSql_Ln += "op_fecha_modif  DATETIME NULL,";
        cSql_Ln += "u_usuario_alta  CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "op_emp_id       INTEGER NULL DEFAULT 0,";
        cSql_Ln += "op_baja_prod    INTEGER(1) NOT NULL DEFAULT 0,";
        cSql_Ln += "id_device       VARCHAR(30) NOT NULL DEFAULT 'MANUAL',";
        cSql_Ln += "id_group        VARCHAR(30) NULL DEFAULT 0,";
        cSql_Ln += "op_semanas_imp  INTEGER NULL DEFAULT 1,";
        cSql_Ln += "op_image_name   CHAR(80) NULL DEFAULT NULL,";
        cSql_Ln += "op_usermodify   INTEGER(1) NULL DEFAULT 0,";
        cSql_Ln += "CONSTRAINT  operacion_PRIMARY PRIMARY KEY(id_op))";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_id ON operacion ('cte_id')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS id_device ON operacion ('id_device')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS MaqLnk_Id ON operacion ('MaqLnk_Id')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS op_usermodify ON operacion ('op_usermodify')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS id_group ON operacion ('id_group')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "";
        cSql_Ln += "CREATE TABLE IF NOT EXISTS operaciong (";
        cSql_Ln += "id_autoin   INTEGER NOT NULL,";
        cSql_Ln += "cte_id INTEGER unsigned DEFAULT 0,";
        cSql_Ln += "cte_nombre_loc CHAR(60) DEFAULT '',";
        cSql_Ln += "cte_nombre_com CHAR(60) DEFAULT '',";
        cSql_Ln += "op_cal_colect NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_colect NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_impmunic NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_impjcj NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_timbres NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_spac NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_tec NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_dev NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_otros NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_cred NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_cal_cred NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_sub NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_itbm NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_tot NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_brutoloc NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_brutoemp NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_netoloc NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_tot_netoemp NUMERIC(12, 2) DEFAULT 0.00,";
        cSql_Ln += "op_observ       TEXT,";
        cSql_Ln += "op_emp_id INTEGER(1) DEFAULT 0,";
        cSql_Ln += "id_device VARCHAR(30) DEFAULT 'MANUAL',";
        cSql_Ln += "id_group VARCHAR(30) NULL DEFAULT 0,";
        cSql_Ln += "op_usermodify INTEGER(1) DEFAULT 0,";
        cSql_Ln += "op_fecha_alta  DATETIME NULL,";
        cSql_Ln += "op_fecha_modif  DATETIME NULL,";
        cSql_Ln += "CONSTRAINT  operaciong_PRIMARY PRIMARY KEY(id_autoin))";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS cte_id ON operaciong ('cte_id')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS op_emp_id ON operaciong ('op_emp_id')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS id_device ON operaciong ('id_device')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS op_usermodify ON operaciong ('op_usermodify')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS id_group ON operaciong ('id_group')";
        Global.oGen_Db.execSQL(cSql_Ln);
    }

    public static void Create_Sql_Tables_Emp() {
        String cSql_Ln = "";
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
        cSql_Ln += "emp_corre_ant INT(10) NULL DEFAULT '0',";
        cSql_Ln += "emp_corre_act INT(10) NULL DEFAULT '0',";
        cSql_Ln += "u_usuario_alta CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "u_usuario_modif CHAR(20) NULL DEFAULT 'ANONIMO',";
        cSql_Ln += "emp_clave_metros CHAR(30) NULL DEFAULT '112233',";
        cSql_Ln += "emp_clave_montos CHAR(30) NULL DEFAULT '332211',";
        cSql_Ln += "CONSTRAINT empresas_PRIMARY PRIMARY KEY (emp_id))";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS emp_inactivo ON empresas ('emp_inactivo')";
        Global.oGen_Db.execSQL(cSql_Ln);

        cSql_Ln = "CREATE INDEX IF NOT EXISTS emp_descripcion ON empresas ('emp_descripcion')";
        Global.oGen_Db.execSQL(cSql_Ln);
        // ----------------------------------FIN EMPRESAS---------------------------------------------------//

    }

    public static int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }
}
