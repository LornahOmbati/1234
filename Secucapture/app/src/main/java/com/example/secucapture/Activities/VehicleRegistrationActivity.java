package com.example.secucapture.Activities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.secucapture.R;
import com.example.secucapture.Utils.DbHelper;
import com.example.secucapture.Utils.GlobalVariables;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VehicleRegistrationActivity extends AppCompatActivity {


    Activity act;
    Context ctx;
    String storeFilename;
    String currentPhotoPath;

    ImageView id_photo_vehicle;

    public Spinner spntransporter;

    static ArrayAdapter<String> transporterAdapter;
    String transporter_selected_name;

    String ba1 = "";
    String trans_no;

    Boolean saved,VehicleRegData;

    EditText truck_no_edt,inspection_no_edt,insurance_no_edt, speed_governor_license_edt;

    TextView save_btn;


    String truckNo, transporter,inspectionNo,insuranceNo,speedGovernorLicense;
    private DbHelper dao;

    private static final int PERMISSION_REQUEST_CODE = 200;

    static final int REQUEST_IMAGE_CAPTURE_1 = 1;

    public static SQLiteDatabase db = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_registration);

        ctx = this;
        act = this;
        trans_no =""+System.currentTimeMillis();

        truck_no_edt = findViewById(R.id.truck_no_edt);
        inspection_no_edt = findViewById(R.id.inspection_no_edt);
        insurance_no_edt = findViewById(R.id.insurance_no_edt);
        speed_governor_license_edt = findViewById(R.id.speed_governor_license_edt);
        spntransporter= findViewById(R.id.spntransporter);
        id_photo_vehicle = findViewById(R.id.id_photo_vehicle);
        save_btn = findViewById(R.id.save_btn);

        dao = new DbHelper(this);

        reset_misc();

        trans_no = ""+System.currentTimeMillis()+"_"+dao.getUSERID();

        spntransporter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String item_selected_prog = ((TextView) view).getText().toString();
                transporter_selected_name = item_selected_prog;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO code to submit entries to database.

                truckNo = truck_no_edt.getText().toString();
                transporter = spntransporter.getSelectedItem().toString();
                inspectionNo = inspection_no_edt.getText().toString();
                insuranceNo = insurance_no_edt.getText().toString();
                speedGovernorLicense = speed_governor_license_edt.getText().toString();
                //ID_Photo = id_photo_vehicle.??

                if (truckNo.equals("") || transporter.equals("") || inspectionNo.equals("") || insuranceNo.equals("") || speedGovernorLicense.equals("")) { //checks if any of the edt are empty
                    Toast.makeText(VehicleRegistrationActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    VehicleRegData = dao.VehicleRegInputData(truckNo, transporter, inspectionNo, insuranceNo, speedGovernorLicense);
                    if (VehicleRegData) {//if the new details are able to be inserted
                        Toast.makeText(VehicleRegistrationActivity.this, "submitted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VehicleRegistrationActivity.this, "submission failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        id_photo_vehicle.setOnClickListener(v -> {

            //check for permissions on clicking the photo

            Log.e("TAG", "onClick: " + "CLICKED");

            try {

                if (!checkPermission()) {
                    //request for permissions if they are not granted
                    requestPermission();
                } else {
                    //if permissions are given then take the picture
                    takePicture(ctx);
                }
                //code to catch the error
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void reset_misc() {

        //transporter
        transporterAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        transporterAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);

        transporterAdapter.add("-- SELECT --");
        transporterAdapter.add("DHL");
        transporterAdapter.add("Transporter 2");
        transporterAdapter.add("Transporter 3");

        spntransporter.setAdapter(transporterAdapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE_1 && resultCode == RESULT_OK) {

            File f = new File(currentPhotoPath);
            Bitmap bitmap = getDownsampledBitmap(ctx, Uri.fromFile(f), 720, 1024);
            id_photo_vehicle.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            ba1 = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                    byteArray.length);

            saved = saveImageData(ba1);
            if (saved) {
                Toast.makeText(this, "Image saved to database", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save image to database", Toast.LENGTH_SHORT).show();
            }

            // Save the bitmap image to file
            saveImage(bitmap, "ID_IMG_" + trans_no + ".jpg");
        }
    }

    private Boolean saveImageData(String ba1) {
        ContentValues ctx = new ContentValues();
        ctx.put("vehicle ID ", ba1);

        long newRowId = db.insert("vehicles", null, ctx);

        return newRowId != -1;
    }
    //saveImage(bitmap, "STD_IMG_"+trans_no+".jpg")

    private void saveImage(Bitmap finalBitmap, String fi_name) {

        String root = Environment.getExternalStorageDirectory().toString();

        File myDir = new File(GlobalVariables.id_pics);

        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File file = new File(myDir, fi_name);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takePicture(Context ctx) {
        try {
            dispatchTakePictureIntent();
        } catch (Exception e){
            Log.e("Not easy",">>>>>>>>>>>"+e.getMessage());
        }
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        Log.e("nonononono",">>>>>>>>>>>>>>>>>>> "+takePictureIntent.resolveActivity(getPackageManager()));

        if (takePictureIntent.resolveActivity(getPackageManager()) != null){

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            Log.e("picha file",">>>>>>>>>>>"+photoFile);

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.secucapture.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_1);
            }

        }
    }
    private File createImageFile() throws IOException{
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        storeFilename = imageFileName;
        Log.e("TAG", imageFileName+"createImageFile: "+currentPhotoPath );
        Log.e("TAG", imageFileName+"storeFilename: "+storeFilename );
        return image;
    }

    private Bitmap getDownsampledBitmap(Context ctx, Uri uri, int targetWidth, int targetHeight) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options outDimens = getBitmapDimensions(uri);

            int sampleSize = calculateSampleSize(outDimens.outWidth, outDimens.outHeight, targetWidth, targetHeight);

            bitmap = downsampleBitmap(uri, sampleSize);

        } catch (Exception e) {
            //handle the exception(s)
        }

        return bitmap;
    }

    private BitmapFactory.Options getBitmapDimensions(Uri uri) throws FileNotFoundException, IOException {
        BitmapFactory.Options outDimens = new BitmapFactory.Options();
        outDimens.inJustDecodeBounds = true; // the decoder will return null (no bitmap)

        InputStream is= getContentResolver().openInputStream(uri);
        // if Options requested only the size will be returned
        BitmapFactory.decodeStream(is, null, outDimens);
        is.close();

        return outDimens;
    }

    private int calculateSampleSize(int width, int height, int targetWidth, int targetHeight) {
        int inSampleSize = 1;

        if (height > targetHeight || width > targetWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) targetHeight);
            final int widthRatio = Math.round((float) width / (float) targetWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee.
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private Bitmap downsampleBitmap(Uri uri, int sampleSize) throws FileNotFoundException, IOException {
        Bitmap resizedBitmap;
        BitmapFactory.Options outBitmap = new BitmapFactory.Options();
        outBitmap.inJustDecodeBounds = false; // the decoder will return a bitmap
        outBitmap.inSampleSize = sampleSize;

        InputStream is = getContentResolver().openInputStream(uri);
        resizedBitmap = BitmapFactory.decodeStream(is, null, outBitmap);
        is.close();

        return resizedBitmap;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, android.Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

}