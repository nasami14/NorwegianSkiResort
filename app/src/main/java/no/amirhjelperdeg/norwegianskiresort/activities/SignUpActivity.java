package no.amirhjelperdeg.norwegianskiresort.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import no.amirhjelperdeg.norwegianskiresort.R;

import java.util.regex.Matcher;

public class SignUpActivity extends AppCompatActivity {


    private TextView txt_emailId, txt_password, txt_confirmPassowrd;
    private Button btn_signUp;

    FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firebaseAuth=FirebaseAuth.getInstance();// initialize firebase object
        firebaseDatabase=FirebaseDatabase.getInstance();

        // get objects of TextBoxes
        txt_emailId=(TextView)findViewById(R.id.edtxt_email_id);
        txt_password=(TextView)findViewById(R.id.edtxt_password);
        txt_confirmPassowrd=(TextView)findViewById(R.id.edtxt_confirm_password);

        btn_signUp=(Button)findViewById(R.id.btn_singup);

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doRegister();
            }
        });


    }

    public  void doRegister()
    {

       final String emailId=txt_emailId.getText().toString();
        String password= txt_password.getText().toString();
        String conf_password= txt_confirmPassowrd.getText().toString();
        Matcher result=Patterns.EMAIL_ADDRESS.matcher(emailId);

        if(emailId.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please enter emailId ", Toast.LENGTH_LONG).show();

            return;//
        }
        //

        else if(!result.find())
        {
            Toast.makeText(getApplicationContext(),"Please enter valid email Id ", Toast.LENGTH_LONG).show();
        }
        else if(password.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please enter password ", Toast.LENGTH_LONG).show();
            return;
        }
        else if(conf_password.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please enter confirm password ", Toast.LENGTH_LONG).show();
            return;
        }
        else if(password.length()<6 && password.length()>15)
        {
            Toast.makeText(getApplicationContext(),"Please enter at least 6 chars for password ", Toast.LENGTH_LONG).show();
            return;
        }
        else if(password.length()>15)
        {
            Toast.makeText(getApplicationContext(),"Please do not enter password more than 15 chars. ", Toast.LENGTH_LONG).show();
            return;
        }
        //  check password and confirm password
        else if(!password.equals(conf_password))
        {
            Toast.makeText(getApplicationContext(),"confirm password is not matched with password", Toast.LENGTH_LONG).show();
            return;
        }
        /// here we will store the users info in database

        final ProgressDialog progressDialog= new ProgressDialog(this);
        if(!progressDialog.isShowing()) {
            progressDialog.setMessage("Saving...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        try {

            firebaseAuth.createUserWithEmailAndPassword(emailId, password).
                    addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                saveProfileData("Your Name",emailId,"Update Your phone",progressDialog);
                            } else {
                                Toast.makeText(getApplicationContext(), "user is not registered  !!", Toast.LENGTH_LONG).show();
                            }


                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Log.d("==============",e.getMessage());
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "SignUp Error : "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
        catch (Throwable e)
        {
            progressDialog.dismiss();
            Log.d("Signup Error : ----",e.toString());
            Toast.makeText(getApplicationContext(), "SignUp Error : "+e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    public void saveProfileData(final String name, final String email, final String phone,final ProgressDialog progressDialog)
    {
        firebaseUser=firebaseAuth.getCurrentUser();
        DatabaseReference mydDBRef=firebaseDatabase.getReference("users");
        DatabaseReference myData=mydDBRef.child(firebaseUser.getUid());
        myData.child("name").setValue(name.toString());
        myData.child("email").setValue(email.toString());
        myData.child("phone").setValue(phone.toString());
        myData.child("role").setValue("user");

        // insert into the database
        myData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressDialog.dismiss();
                finish();// finish signup activity
                // will start login activity
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                Toast.makeText(getApplicationContext(), "User Created.", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Signup Failed", Toast.LENGTH_LONG).show();
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
