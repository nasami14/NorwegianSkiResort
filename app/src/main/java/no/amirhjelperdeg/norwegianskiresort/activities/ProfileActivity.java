package no.amirhjelperdeg.norwegianskiresort.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import no.amirhjelperdeg.norwegianskiresort.R;
import no.amirhjelperdeg.norwegianskiresort.utility.CircleTransform;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {


    private ImageView  profileImage,profileImageDialod;
    private TextView txtName,txtEmail, txtPhone;
    private Button btnEditProfile;

    FirebaseDatabase fdb;
    FirebaseAuth fAuth;
    DatabaseReference mydDBRef;
    FirebaseUser firebaseUser;

    FirebaseStorage storage ;
    Uri imgFilePath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // firebase initialization
        fAuth= FirebaseAuth.getInstance();
        firebaseUser= fAuth.getCurrentUser();
        fdb=FirebaseDatabase.getInstance();

        storage= FirebaseStorage.getInstance();

        profileImage=(ImageView)findViewById(R.id.img_profile);
        txtName =(TextView)findViewById(R.id.profile_name);
        txtEmail=(TextView) findViewById(R.id.textViewEmail);
        txtPhone=(TextView)findViewById(R.id.txtViewPhone);

        btnEditProfile=(Button)findViewById(R.id.edit_profile);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editProfileDialog();
            }
        });



        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileImageDialog();
            }
        });


        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StorageReference checkProfile=storage.getReference();

            checkProfile.child("images/"+firebaseUser.getUid()+"/profile.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                        Glide
                                .with(getApplicationContext())
                                .load(uri)
                                .crossFade().thumbnail(0.5f)
                                .bitmapTransform(new CircleTransform(getApplicationContext())).diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(profileImage);
                    showUserData(progressDialog);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Failed to fetch profile pic",Toast.LENGTH_LONG).show();
                }
            });

    }

    /**
     *  method to set user  information
     */
    public void showUserData(final  ProgressDialog progressDialog) {

        DatabaseReference dbRef  =fdb.getReference("users/"+firebaseUser.getUid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    //Toast.makeText(getApplicationContext(),"user name : "+dataSnapshot.child("name").getValue(String.class),Toast.LENGTH_LONG).show();
                    txtName.setText(dataSnapshot.child("name").getValue(String.class).toString());
                    txtEmail.setText(dataSnapshot.child("email").getValue(String.class).toString());
                    txtEmail.setText(dataSnapshot.child("phone").getValue(String.class).toString());

                progressDialog.dismiss();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Failed to get user data.",Toast.LENGTH_LONG).show();
            }

        });

    }

    private  void editProfileImageDialog()
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.edit_profile_image, null);
        final ImageView img_profile = (ImageView) mView.findViewById(R.id.img_profile);
        Button btn_upload = (Button) mView.findViewById(R.id.btn_upload_image);
        profileImageDialod=img_profile;
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgFilePath!=null) {
                    dialog.dismiss();
                    uploadProfileImage();

                } else {

                    Toast.makeText(ProfileActivity.this,"Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadProfileImage()
    {

        //displaying progress dialog while image is uploading
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.show();

        StorageReference uploadFile=storage.getReference("images/"+firebaseUser.getUid()+"/profile.jpg");
        if(imgFilePath!=null) {
            uploadFile.putFile(imgFilePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "Uploaded Successfully ", Toast.LENGTH_LONG).show();

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
        startActivityForResult(Intent.createChooser(intent,"Select Image"),250);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==250 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            imgFilePath=   data.getData();
            try {

                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), imgFilePath);
                profileImageDialod.setImageBitmap(image);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void saveProfileData(final String name, final String email, final String phone)
    {

        final ProgressDialog  progressDialog= new ProgressDialog(this);
        if(!progressDialog.isShowing()) {
            progressDialog.setMessage("Saving...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
        mydDBRef=fdb.getReference("users");
        DatabaseReference myData=mydDBRef.child(firebaseUser.getUid());
        myData.child("name").setValue(name.toString());
        myData.child("email").setValue(email.toString());
        myData.child("phone").setValue(phone.toString());
        myData.child("role").setValue("user");

        // insert into the database
        myData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtEmail.setText(email.toString());
                txtName.setText(name.toString());
                txtPhone.setText(phone.toString());
                updateEmailId(email,progressDialog);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Failed to save the data.",Toast.LENGTH_LONG).show();
                Log.w("ProfileActivity : ", "Failed to save the user data.", error.toException());
            }
        });

    }

    private  void updateEmailId(String emailId,final ProgressDialog progressDialog)
    {
        fAuth.getCurrentUser().updateEmail(emailId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Saved Successfully.",Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.e("ProfileActivity : ", "Failed to save the user data."+e.toString());
            }
        });

    }

    public void editProfileDialog()
    {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.edit_profile_dialog, null);
                final EditText name = (EditText) mView.findViewById(R.id.txtName);
                final EditText email = (EditText) mView.findViewById(R.id.txtEmail);
                final EditText phone = (EditText) mView.findViewById(R.id.txtPhone);

                name.setText(txtName.getText().toString());
                email.setText(txtEmail.getText().toString());
                phone.setText(txtPhone.getText().toString());

                Button btn_save_profile = (Button) mView.findViewById(R.id.btn_save_profile);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                btn_save_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!txtEmail.getText().toString().isEmpty() && !txtName.getText().toString().isEmpty() && !txtPhone.getText().toString().isEmpty()) {
                            dialog.dismiss();
                            saveProfileData(name.getText().toString(),email.getText().toString(),phone.getText().toString());

                        } else {

                            Toast.makeText(ProfileActivity.this,"Please do not enter blank text.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

