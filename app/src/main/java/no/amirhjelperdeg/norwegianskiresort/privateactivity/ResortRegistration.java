package no.amirhjelperdeg.norwegianskiresort.privateactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import no.amirhjelperdeg.norwegianskiresort.MainActivity;
import no.amirhjelperdeg.norwegianskiresort.R;
import no.amirhjelperdeg.norwegianskiresort.activities.UploadImage;
import no.amirhjelperdeg.norwegianskiresort.database_models.ResortData;

public class ResortRegistration extends AppCompatActivity {


    private EditText resortName, resortDesc, resortAddress, resortMobileNo,resortEmailId,resortZipcode,
            resortCountry,resortOpenHours,resortCloseHours, resortTotalSlopeDistance,
            resortTotalLifts, resortTotalCharges,
    totalSlopes, adultCharge, childrenCharge,youthCharge,
            easyDistance, intermediateDistance, advancedDistance, chairLifts,towRopeLifts,tBarJBarLifts;

    private Button uploadImage;

     private Button btnSave, btnCancel;
     private Spinner province;
     private FirebaseDatabase firebaseDatabase;
     private FirebaseAuth firebaseAuth;
     private DatabaseReference dbRef;
     private String imagePath="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resort_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //  initialize all edit text

        resortName=(EditText)findViewById(R.id.edtxt_resortName);
        resortDesc=(EditText)findViewById(R.id.edtxt_resort_description);
        resortAddress=(EditText)findViewById(R.id.edtxt_resort_address);
        resortZipcode=(EditText)findViewById(R.id.edtxt_zipcode);
        resortMobileNo=(EditText)findViewById(R.id.edtxt_mobile);
        resortEmailId=(EditText)findViewById(R.id.edtxt_resort_emaild);
        resortCountry=(EditText)findViewById(R.id.edtxt_country);
        resortOpenHours=(EditText)findViewById(R.id.edtxt_openHours);
        resortCloseHours=(EditText)findViewById(R.id.edtxt_closeHours);
        resortTotalSlopeDistance=(EditText)findViewById(R.id.edtxt_slope_distance);
        resortTotalLifts=(EditText)findViewById(R.id.edtxt_no_of_lifts);
        resortTotalCharges=(EditText)findViewById(R.id.edtxt_charges);
        province=(Spinner)findViewById(R.id.edtxt_resort_province);

        totalSlopes=(EditText)findViewById(R.id.edtxt_slope_distance);
        adultCharge=(EditText)findViewById(R.id.chargesAdults);
        childrenCharge=(EditText)findViewById(R.id.chargesChildren);
        youthCharge=(EditText)findViewById(R.id.chargesYouth);
        easyDistance=(EditText)findViewById(R.id.slope_easy);
        intermediateDistance=(EditText)findViewById(R.id.slope_Intermediate);
        advancedDistance=(EditText)findViewById(R.id.slope_Advanced);

        chairLifts=(EditText)findViewById(R.id.edtxt_ChairLifts);
        towRopeLifts=(EditText)findViewById(R.id.edtxt_tow_ropeLifts);
        tBarJBarLifts=(EditText)findViewById(R.id.edtxt_t_Bar_J_Bar_Lifts);

        uploadImage =(Button)findViewById(R.id.uploadResortImage);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        btnSave=(Button) findViewById(R.id.btn_resort_registration);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext()," Save Button",Toast.LENGTH_LONG).show();
                addResortData();
            }
        });

        btnCancel=(Button)findViewById(R.id.btn_cancel_resort_registration);




        firebaseDatabase= FirebaseDatabase.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();

    }

    // method to  upload the image
    public void uploadImage()
    {

        Intent intent=new Intent(getApplicationContext(), UploadImage.class);
        intent.putExtra("ImageName",resortName.getText().toString());
        startActivityForResult(intent,500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==500)
        {
            Toast.makeText(getApplicationContext()," 500 ",Toast.LENGTH_LONG).show();
            imagePath= data.getStringExtra("ImageFilePath");
        }
        else{
            imagePath= data.getStringExtra("ImageFilePath");
        }
    }

    /**
     *  method to insert the resort data oin to firebase data base
     */
    public void addResortData()
    {
        dbRef= firebaseDatabase.getReference("resorts/"+resortName.getText());// table name

        // creating the ResortData object and passing all resort info in constructor
        ResortData resortData= new ResortData(resortName.getText().toString(),resortDesc.getText().toString(),resortAddress.getText().toString(),
                resortMobileNo.getText().toString(),resortEmailId.getText().toString(),resortZipcode.getText().toString(),
                province.getSelectedItem().toString(),resortCountry.getText().toString(),resortOpenHours.getText().toString(),
                resortCloseHours.getText().toString(),resortTotalLifts.getText().toString(),resortTotalSlopeDistance.getText().toString(),
                totalSlopes.getText().toString(),resortTotalCharges.getText().toString(), imagePath,
                easyDistance.getText().toString(),intermediateDistance.getText().toString(),advancedDistance.getText().toString(),

                adultCharge.getText().toString(),childrenCharge.getText().toString(),youthCharge.getText().toString(),

                chairLifts.getText().toString(),towRopeLifts.getText().toString(),tBarJBarLifts.getText().toString()

                );

        dbRef.setValue(resortData);

        // this is the way of adding column's using child object

        /*DatabaseReference dbResortId=  dbRef.child(resortName.getText().toString()); // 2nd node
        dbResortId.child("name").setValue(resortName.getText().toString()); // resortname column
        dbResortId.child("description").setValue(resortDesc.getText().toString());
        dbResortId.child("address").setValue(resortAddress.getText().toString());
        dbResortId.child("zipcode").setValue(resortZipcode.getText().toString());
        dbResortId.child("province").setValue(province.getSelectedItem().toString());
        dbResortId.child("country").setValue(resortCountry.getText().toString());
        dbResortId.child("openHours").setValue(resortOpenHours.getText().toString());
        dbResortId.child("closeHours").setValue(resortCloseHours.getText().toString());
        dbResortId.child("totalLifts").setValue(resortTotalLifts.getText().toString());
        dbResortId.child("slopeDistance").setValue(resortTotalSlopeDistance.getText().toString());
        dbResortId.child("totalSlopes").setValue(totalSlopes.getText().toString());
        dbResortId.child("totalCharges").setValue(resortTotalCharges.getText().toString());*/







        /*dbResortId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Toast.makeText(getApplicationContext(),"Resort Added successfully",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_LONG).show();
            }

        });*/
/*
        dbResortId.setValue("dbResult", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(getApplicationContext(),"Failed to add resort "+ databaseError.getMessage(),Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Resort Added successfully",Toast.LENGTH_LONG).show();
                }
            }
        });*/

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
