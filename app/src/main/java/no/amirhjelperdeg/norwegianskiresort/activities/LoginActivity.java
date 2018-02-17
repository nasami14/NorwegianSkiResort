package no.amirhjelperdeg.norwegianskiresort.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import no.amirhjelperdeg.norwegianskiresort.MainActivity;
import no.amirhjelperdeg.norwegianskiresort.R;

public class LoginActivity extends AppCompatActivity {


    private Button btn_login, btn_signup;

    private TextView txt_userName,txt_password;

    FirebaseAuth firebaseAuth;

    SharedPreferences userLoginInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth= FirebaseAuth.getInstance();

        btn_login=(Button)findViewById(R.id.btn_singin);
        btn_signup=(Button)findViewById(R.id.btn_singup);

        txt_userName=(TextView)findViewById(R.id.edtxt_singin_email_id);
        txt_password=(TextView)findViewById(R.id.edtxt_singin_password);


        // when user will click on the signup button then we will call this listener with onclick method
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startSignUpActivity();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doLogin();
            }
        });


    }

    public void doLogin()
    {

        final String emailId=txt_userName.getText().toString();
        final String password= txt_password.getText().toString();

        if(emailId.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please enter emailId ", Toast.LENGTH_LONG).show();

            return;//
        }

       else if(password.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please enter password ", Toast.LENGTH_LONG).show();
            return;
        }

        // here we have to match user details in our database

        firebaseAuth.signInWithEmailAndPassword(emailId,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {

                    //userLoginInfo= getSharedPreferences("preferences", MODE_PRIVATE);
                    userLoginInfo= getSharedPreferences("logindata", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor=userLoginInfo.edit();
                    editor.putString("emailId",emailId);
                    editor.putString("password",password);
                    editor.commit();

                    finish();// close login activity
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Login is failed, please try again",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void  startSignUpActivity()
    {
        Intent intent= new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(intent);
    }



}
