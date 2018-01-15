package flamingo.onemovil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.lang.String;
import java.util.Calendar;

/**
 * Created by Usuario on 01/01/2018.
 */

public class Global {
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

}
