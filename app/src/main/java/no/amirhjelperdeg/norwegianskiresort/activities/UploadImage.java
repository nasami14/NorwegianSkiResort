package no.amirhjelperdeg.norwegianskiresort.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import no.amirhjelperdeg.norwegianskiresort.R;

import java.io.IOException;

public class UploadImage extends AppCompatActivity {


    private Button selectImage, uploadImage;
    private ImageView imagePreview;

    FirebaseDatabase fdb;
    FirebaseAuth fAuth;
    DatabaseReference mydDBRef;
    FirebaseUser firebaseUser;

    FirebaseStorage storage ;
    Uri imgFilePath;
    String ImageName;
    String imgFileFirebasePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth= FirebaseAuth.getInstance();
        firebaseUser= fAuth.getCurrentUser();
        fdb=FirebaseDatabase.getInstance();

        storage= FirebaseStorage.getInstance();

        selectImage = (Button)findViewById(R.id.btn_selectImage);
        uploadImage = (Button)findViewById(R.id.btn_upload_resortImage);

        imagePreview=(ImageView)findViewById(R.id.img_upload_preview);

        ImageName=getIntent().getStringExtra("ImageName");



        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();

            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();

            }
        });


    }



    public void uploadImage()
    {
    //displaying progress dialog while image is uploading
    final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.show();

        imgFileFirebasePath="resortimages/"+ImageName+".jpg";

        StorageReference uploadFile=storage.getReference("resortimages/"+ImageName+".jpg");
        if(imgFilePath!=null) {
        uploadFile.putFile(imgFilePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //dismissing the progress dialog
                        progressDialog.dismiss();

                        //displaying success toast
                        Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //displaying the upload progress
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setProgress(((int) progress));
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");

                    }
                });

    }else
    {
        Toast.makeText(getApplicationContext(), "To select image ,click on image icon ", Toast.LENGTH_LONG).show();
    }

}

    public void selectImage()
    {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),251);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==251 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            imgFilePath=   data.getData();
            try {

                Log.d("Upload Image",imgFilePath.toString());
                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), imgFilePath);
                imagePreview.setImageBitmap(image);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            Intent intent= new Intent(); // create new intent
            intent.putExtra("ImageFilePath",imgFileFirebasePath);
             setResult(RESULT_OK,intent);
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
