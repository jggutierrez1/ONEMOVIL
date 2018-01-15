package flamingo.onemovil;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class take_photo extends AppCompatActivity {

    private static final String TAG = capt_data.class.getSimpleName();

    private String mCurrentPhotoPath;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int PICK_IMAGE_REQUEST = 3;
    private static Context oContext;

    private Uri fileUri; // file url to store image/video

    private ImageView imgPreview;
    private TextView txtSDK;
    private TextView txtUriPath;
    private TextView txtRealPath;

    private int pShow_Image;

    private Button btnCapturePicture, btnDeletePhoto, btnOpenPhotos, btnSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        if (Global.cCte_Id == "") {
            Toast.makeText(getApplicationContext(), "Debe seleccionar un <CLIENTE>, PARA PODER TOMAR LAS FOTOS.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (Global.cMaq_Id == "") {
            Toast.makeText(getApplicationContext(), "Debe seleccionar una <MAQUINA>, PARA PODER TOMAR LAS FOTOS.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();
        oContext = this.getApplicationContext();

        Global.callBroadCast();

        txtSDK = (TextView) findViewById(R.id.txtSDK);
        txtUriPath = (TextView) findViewById(R.id.txtUriPath);
        txtRealPath = (TextView) findViewById(R.id.txtRealPath);

        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);
        btnDeletePhoto = (Button) findViewById(R.id.btn_deletephoto);
        btnOpenPhotos = (Button) findViewById(R.id.btn_openphotos);

        btnSalir = (Button) findViewById(R.id.btnSalir);

        Intent i = getIntent();
        pShow_Image = i.getIntExtra("show_image", 0);
        i = null;

        if (pShow_Image == 1) {
            load_image_Last_from();
        }

       /*
         * Capture image button click event
		 */
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        btnDeletePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Global.cLastFilePhoto != "") {
                    if (Global.DeleteLastFilePhotoFile() == true) {
                        txtSDK.setText("ELEMENTO ELIMINADO: ");
                        txtUriPath.setText("URI Path: " + Global.oLastSelectedImageId.toString());
                        txtRealPath.setText("Real Path: " + Global.cLastFullPathFilePhoto);

                        imgPreview.setImageResource(android.R.color.transparent);

                        refreshAndroidGallery(Global.oLastSelectedImageId);

                        Global.callBroadCast();
                    }
                }
            }
        });

        btnOpenPhotos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Global.cStorageDirectoryPhoto + "/");

                if (Build.VERSION.SDK_INT < 19) {
                    //Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.getContentUri(uri.toString()));
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Thumbnails.getContentUri(uri.toString()));
                    //Intent intent = new Intent();
                    intent.setType("image/jpeg");
                    //intent.setData(uri);
                    //intent.setDataAndType(uri,"image/jpeg");
                    //intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivity(Intent.createChooser(intent, "CONTENEDOR DE IMAGENES FLAMINGO"));
                } else {
/*
                    Global.oPictureActionIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    Global.oPictureActionIntent.setType("image/jpeg");
                    Global.oPictureActionIntent.putExtra("return-data", true);
                    startActivityForResult(Global.oPictureActionIntent, PICK_IMAGE_REQUEST);
*/
                    //*Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.getContentUri(uri.toString()));
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/jpeg");


                    //intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    //intent.addCategory(Intent.CATEGORY_OPENABLE);
                    //intent.setType("*/*");
                    //String[] extraMimeTypes = {"image/*", "video/*"};
                    //intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes);
                    //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }

            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(), "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivity(intent);
    }

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /*
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /*
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            try {
                String realPath;
                // SDK < API11
                if (Build.VERSION.SDK_INT < 11)
                    realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
                    // SDK >= 11 && SDK < 19
                else if (Build.VERSION.SDK_INT <= 19)
                    realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
                    // SDK > 19 (Android 4.4)
                else
                    realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());

                Global.cLastFullPathFilePhoto = realPath;
                Global.oLastSelectedImageId = data.getData();

                File file = new File(realPath);
                Global.cLastFilePhoto = file.getName();
                file = null;

                imgPreview.setVisibility(View.VISIBLE);
                setTextViews1(Build.VERSION.SDK_INT, Global.oLastSelectedImageId.toString(), Global.cLastFullPathFilePhoto);
/*
*/
            } catch (NullPointerException e) {
                imgPreview.setImageResource(android.R.color.transparent);
                e.printStackTrace();
                Global.oLastSelectedImageId = null;
                Global.cLastFilePhoto = "";
                Global.cLastFullPathFilePhoto = "";
            }
        }

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Global.oLastSelectedImageId = getImageContentUri(oContext, Global.cLastFullPathFilePhoto);
                setTextViews2();
                // display it in image view
                //previewCapturedImage();
                super.onResume();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void load_image_Last_from() {
        try {
            if (Global.cLastFilePhoto.trim() != "") {
                String CadenaMaq = "[" + Global.cCte_Id + "]_[" + Global.cMaq_Id + "]_IMG_";
                if (Global.cLastFilePhoto.toLowerCase().contains(CadenaMaq.trim().toLowerCase())) {
                    imgPreview.setVisibility(View.VISIBLE);
                    setTextViews1(Build.VERSION.SDK_INT, Global.oLastSelectedImageId.toString(), Global.cLastFullPathFilePhoto);
                }
            }
        } catch (NullPointerException e) {
            imgPreview.setImageResource(android.R.color.transparent);
            e.printStackTrace();
        }

    }

    private void setTextViews2() {
        Integer sdk = Build.VERSION.SDK_INT;

        try {
            imgPreview.setVisibility(View.VISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 6;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            imgPreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        this.txtSDK.setText("Build.VERSION.SDK_INT: " + sdk);
        this.txtUriPath.setText("URI Path: " + Global.oLastSelectedImageId.toString());
        this.txtRealPath.setText("Real Path: " + Global.cLastFullPathFilePhoto);

        Log.d("HMKCODE", "Build.VERSION.SDK_INT:" + sdk);
        Log.d("HMKCODE", "URI Path:" + Global.oLastSelectedImageId.toString());
        Log.d("HMKCODE", "Path File: " + Global.cLastFilePhoto);
        Log.d("HMKCODE", "Real Path: " + Global.cLastFullPathFilePhoto);
    }

    private void setTextViews1(int sdk, String uriPath, String realPath) {

        this.txtSDK.setText("Build.VERSION.SDK_INT: " + sdk);
        this.txtUriPath.setText("URI Path: " + uriPath);
        this.txtRealPath.setText("Real Path: " + realPath);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        imgPreview.setImageBitmap(BitmapFactory.decodeFile(realPath, options));


        Log.d("HMKCODE", "Build.VERSION.SDK_INT:" + sdk);
        Log.d("HMKCODE", "URI Path:" + uriPath);
        Log.d("HMKCODE", "Real Path: " + realPath);
    }

    /*
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {
            imgPreview.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            imgPreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void refreshAndroidGallery(Uri fileUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(fileUri);
            this.getApplicationContext().sendBroadcast(mediaScanIntent);
        } else {
            this.getApplicationContext().sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    /**
     * ------------ Helper Methods ----------------------
     */

	/*
     * Creating file uri to store image/video
	 */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        File mediaStorageDir = new File(Global.cStorageDirectoryPhoto);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(Global.cStorageDirectory, "Oops! Failed create " + Global.cStorageDirectoryPhoto + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        String cImageFileName = "";
        Global.cLastFilePhoto = "";

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            cImageFileName = "[" + Global.cCte_Id + "]_[" + Global.cMaq_Id + "]_IMG_" + timeStamp + ".jpg";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + cImageFileName);

            Global.cLastFilePhoto = cImageFileName;
            Global.cLastFullPathFilePhoto = mediaFile.getAbsolutePath();
        } else {
            return null;
        }
        return mediaFile;
    }

    private void scanFile(String path, final boolean isDelete) {
        try {
            MediaScannerConnection.scanFile(Global.oActual_Context, new String[]{path},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            if (isDelete) {
                                if (uri != null) {
                                    Global.oActual_Context.getContentResolver().delete(uri,
                                            null, null);
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Uri getImageContentUri(Context context, String cFullPath) {
        File imageFile = new File(cFullPath);
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}
