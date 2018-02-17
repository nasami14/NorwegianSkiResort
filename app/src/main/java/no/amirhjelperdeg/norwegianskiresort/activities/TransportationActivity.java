package no.amirhjelperdeg.norwegianskiresort.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import no.amirhjelperdeg.norwegianskiresort.R;
import no.amirhjelperdeg.norwegianskiresort.adapters.MyListAdapter;
import no.amirhjelperdeg.norwegianskiresort.database_models.ResortData;
import no.amirhjelperdeg.norwegianskiresort.models.APITypes;
import no.amirhjelperdeg.norwegianskiresort.tasks.GenericAPIRequest;
import no.amirhjelperdeg.norwegianskiresort.tasks.JSONParseResult;
import no.amirhjelperdeg.norwegianskiresort.tasks.TaskOutput;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TransportationActivity extends AppCompatActivity {


    Double lat, lng;

    private String TAG = TransportationActivity.class.getSimpleName();

    // declare firebasedb objects

    private Spinner source, destination;

    private Button searchBtn;

    private ArrayList<String> sourceList = new ArrayList<String>();
    private ArrayList<String> destinationList = new ArrayList<String>();

    private FirebaseAuth fAuth;
    private FirebaseDatabase firebaseDatabase;

    String resortName;
    ResortData resortData;
    HashMap<String, ResortData> mapResortData= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_transportation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        sourceList.add("Your Location");
        //destinationList.add("Your Location");

        source = (Spinner) findViewById(R.id.source);
        destination = (Spinner) findViewById(R.id.destination);

        searchBtn = (Button) findViewById(R.id.btn_search);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchTransportation();
            }
        });
        new GetResortData(this,firebaseDatabase,resortName,new ProgressDialog(this)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,resortData);

    }


    public void fillDropdownList(ArrayList<String> sourceList, ArrayList<String> destinationList)
    {
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sourceList);
        ArrayAdapter<String> destAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, destinationList);

        source.setAdapter(sourceAdapter);
        destination.setAdapter(destAdapter);

    }

    /**
     * searchTransportation
     */
    public void searchTransportation() {

        String sourceName = (String) source.getSelectedItem();
        String destname = (String) destination.getSelectedItem();

        ProgressDialog progressDialog = new ProgressDialog(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(!destname.equalsIgnoreCase("Your Location") ) {

            resortData=mapResortData.get(destname);
            String latitude = sharedPreferences.getString(destname + "_lat", "");
            String longitude = sharedPreferences.getString(destname + "_long", "");

            if (latitude.isEmpty() || longitude.isEmpty()) {

                new Geocode(getApplicationContext(), this, progressDialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        APITypes.GEOCODE_BY_ADDRESS_COUNTRY_ZIPCODE.toString(),
                        resortData.getAddress(), "", resortData.getProvince(), resortData.getZipcode(), "IN");

            } else {
                lat = Double.valueOf(latitude);
                lng = Double.valueOf(longitude);
                openNavigation(lat ,lng);
            }
        }

    }

    public  void openNavigation(Double lat, Double lng)
    {

         //Uri mapURI= Uri.parse("google.navigation:q="+lat+","+lng+"&mode=d");
        //Uri mapURI= Uri.parse("google.streetview:cbll="+lat+","+lng);

        Uri mapURI = Uri.parse("geo:"+lat+","+lng+"?q=" + Uri.encode(resortData.getAddress()));

        //Uri mapURI= Uri.parse("google.navigation:q="+resortData.getAddress());

        Intent mapIntent= new Intent(Intent.ACTION_VIEW, mapURI);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);


    }



    public JSONParseResult parseGeocodeAPIResponse(String response) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {
            JSONObject json = new JSONObject(response);
            JSONArray results = json.getJSONArray("results");

            JSONObject geometry = (JSONObject) results.get(0);
            JSONArray address_components = (JSONArray) geometry.optJSONArray("address_components");
            JSONObject geometryArray = (JSONObject) geometry.opt("geometry");
            JSONObject location = (JSONObject) geometryArray.opt("location");
            lat = (Double) location.opt("lat");
            lng = (Double) location.opt("lng");
            Log.i(TAG, lat + " , " + lng + " , " + address_components.length());
            SharedPreferences.Editor editor = sharedPreferences.edit();

            String destname = (String) destination.getSelectedItem();

            editor.putString(destname + "_lat", String.valueOf(lat));
            editor.putString(destname + "_long", String.valueOf(lng));
            editor.commit();

            // open google map navigation
            openNavigation(lat ,lng);

        }catch (JSONException ex)
        {
            Log.e(TAG , "JSON parsing error ...");
            ex.printStackTrace();
            return JSONParseResult.JSON_EXCEPTION;
        }
        return JSONParseResult.OK;
    }

    class Geocode extends GenericAPIRequest
    {

        public Geocode(Context context, Activity activity, ProgressDialog progressDialog)
        {
            super(context,activity,progressDialog);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(TaskOutput output) {
            super.onPostExecute(output);
        }

        @Override
        protected JSONParseResult parseResponse(String response) {
            return parseGeocodeAPIResponse(response);
        }
    }
    //////////////////////////
    class GetResortData extends AsyncTask<ResortData,String,ResortData>
    {
        ProgressDialog progressDialog;
        FirebaseDatabase fdb;
        String resortName;
        TransportationActivity activity;
        ResortData resortData;
        String popupType;
        long lCounter=0;


        // constructor
        public GetResortData(TransportationActivity activity,FirebaseDatabase fdb,String resortName, ProgressDialog progressDialog) {
            this.fdb = fdb;
            this.activity=activity;
            this.resortName = resortName;
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            if(!progressDialog.isShowing() && activity.resortData==null) {
                progressDialog.setMessage(getApplicationContext().getString(R.string.message_while_calling_api));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            }
        }

        @Override
        protected ResortData doInBackground(ResortData... params) {

            resortData=params[0];
            if(resortData==null ) {

                DatabaseReference dbRef = firebaseDatabase.getReference("resorts");

                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot resort : dataSnapshot.getChildren()) {
                            resortData = resort.getValue(ResortData.class);
                            //sourceList.add(resortData.getName());
                            destinationList.add(resortData.getName());
                            activity.resortData=resortData;
                            mapResortData.put(resortData.getName(),resortData);
                        }

                        fillDropdownList(sourceList, destinationList);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                while (resortData == null) {
                    //Log.i("ResortDetails : ","-------------*****----"+lCounter);
                    if(lCounter>=999999990)
                        break;
                    lCounter++;
                }


            }

            return resortData;

        }

        @Override
        protected void onPostExecute(ResortData resortData) {

            if(resortData!=null)
                progressDialog.dismiss();


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
