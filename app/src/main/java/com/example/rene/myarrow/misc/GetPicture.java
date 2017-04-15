package com.example.rene.myarrow.misc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import com.example.rene.myarrow.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by nily on 24.12.15.
 */
public class GetPicture extends Activity {

    /** Kuerzel fuers Logging. */
    private static final String TAG = GetPicture.class.getSimpleName();

    // lokale Variablen
    private Uri currentImageUri;
    private String mDatei;
    private Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Context/Intent abspeichern
        myIntent = getIntent();

        // Übergangparameter abholen
        final Bundle extras = getIntent().getExtras();
        if (extras!=null && extras.containsKey(Konstante.IN_PARAM_DATEINAME_ID)) {
            mDatei = extras.getString(Konstante.IN_PARAM_DATEINAME_ID);
            mDatei = mDatei.replace(" ", "_");
        } else {
            Log.w(TAG, "oncreate(): Nichts übergeben");
        }

        // URI für Dateiname erstellen
        if(currentImageUri==null && savedInstanceState==null){
            currentImageUri = getImageFileUri(mDatei);
            // es wurde kein Dateinamen übergeben
            if (currentImageUri==null) return;
        }

        String currentImagePath;
        if(savedInstanceState!=null){
            currentImagePath = savedInstanceState.getString("currentImagePath");
            currentImageUri = Uri.fromFile(new File(currentImagePath));
        }else{
            // Camera/Galerie starten
            startActivityForResult(getPickImageIntent(this, currentImageUri), 1234);
        }
    }

    private static Uri getImageFileUri(String m){
        // TODO: Klasse für den Dateinamen erstellen
        // Create a storage directory for the images
        // To be safe(er), you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this
        // Falls Verzeichnis nicht existiert, anlegen
        File imagePath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "MyArrow");
        if (!imagePath.exists()){
            if (!imagePath.mkdirs()){
                Log.d(TAG, "getImageFileUri(): CameraTestIntent, failed to create directory");
                return null;
            }else{
                Log.d(TAG,"create new MyArrow folder");
            }
        }
        //
        // Create an image file name
        //
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).format(new Date());
        String mFile = "MYA_" + m + "_" + timeStamp + ".jpg";
        File image = new File(imagePath, mFile);
        //
        // Create an File Uri
        //
        return Uri.fromFile(image);
    }

    private void galleryAddPic() {
        Log.d(TAG, "galleryAddPic(): Start");
        /*
          copy current image to Galerry
         */
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(currentImageUri);
        this.sendBroadcast(mediaScanIntent);
        Log.d(TAG, "galleryAddPic(): End");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onSaveInstanceState(): Start");
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState(): putString");
        if (currentImageUri!=null) {
            savedInstanceState.putString("currentImagePath", currentImageUri.getPath());
            Log.d(TAG, "onSaveInstanceState(): currentImagePath - " + currentImageUri.getPath());
        }
        Log.d(TAG, "onSaveInstanceState(): End");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //data available only when default environment is setting. null for customize filename.
        switch (requestCode) {
            case 1234:
                if (resultCode == Activity.RESULT_OK) {
                    // Kamera oder Gallerie ?
                    final boolean isCamera;
                    if (data == null) {
                        // Keine Daten zurückbekommen, also wurde die Kamera benutzt
                        isCamera = true;
                    } else {
                        final String action = data.getAction();
                        // getAction hat nichts zurückgemeldet, also wurde die Kamera NICHT benutzt
                        isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
// keine Ahnung.....
                    }
                    // Path- und Dateinamen auslesen
                    String selectedImage;
                    if (isCamera) {
                        // Kamera - keine Änderung
                        // Dateiname
                        String mTemp = currentImageUri.toString();
                        mTemp = mTemp.replace("file://", "");
                        selectedImage = mTemp;
                    } else {
                        // Gallerie, wurde ausgewählt
                        // Let's read picked image data - its URI
                        Uri pickedImage = data.getData();
                        // Let's read picked image path using content resolver
                        String[] filePath = { MediaStore.Images.Media.DATA };
                        Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
                        if (cursor.moveToFirst()) {
                            selectedImage = cursor.getString(cursor.getColumnIndex(filePath[0]));
                        } else {
                            selectedImage = null;
                        }
                        cursor.close();
                    }
                    //
                    // Dateiname ist nicht leer....
                    //
                    if (selectedImage!=null && !selectedImage.equals("")) {
                        //
                        // In der Gallerie bekanntmachen
                        //
                        galleryAddPic();
                        //
                        // nur den Dateinamen zurückgeben, nicht den Pathnamen
                        //
                        myIntent.putExtra(Konstante.OUT_PARAM_DATEINAME_ID,
                                selectedImage.substring(selectedImage.indexOf("MYA_")));
                        setResult(Activity.RESULT_OK, myIntent);
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User cancelled the image capture
                    setResult(Activity.RESULT_CANCELED, myIntent);
                } else {
                    // Image capture failed, advise user
                    setResult(Activity.RESULT_CANCELED, myIntent);
                }
                break;
        }
        finish();
    }

    public static Intent getPickImageIntent(Context context, Uri currentImageUri) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        // aus der Gallerie auswählen
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        // ein Foto aufnehmen
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);

        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.txt_bitte_waehlen));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

}