package no.amirhjelperdeg.norwegianskiresort.activities;

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
import no.amirhjelperdeg.norwegianskiresort.R;

import java.util.regex.Matcher;

public class SignUpActivity extends AppCompatActivity {


    private TextView txt_emailId, txt_password, txt_confirmPassowrd;
    private Button btn_signUp;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firebaseAuth=FirebaseAuth.getInstance();// initialize firebase object


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

       String emailId=txt_emailId.getText().toString();
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

        try {

            firebaseAuth.createUserWithEmailAndPassword(emailId, password).
                    addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "User is registered successfully !!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "user is not registered  !!", Toast.LENGTH_LONG).show();
                            }

                            finish();// finish signup activity
                            // will start login activity
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));

                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Log.d("==============",e.getMessage());
                    //Toast.makeText(getApplicationContext(), "Error : "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
        catch (Throwable e)
        {
            Log.d("----Error : ----",e.getMessage());
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
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
