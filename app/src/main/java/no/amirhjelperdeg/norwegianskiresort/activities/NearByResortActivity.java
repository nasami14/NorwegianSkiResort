package no.amirhjelperdeg.norwegianskiresort.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import no.amirhjelperdeg.norwegianskiresort.R;
import no.amirhjelperdeg.norwegianskiresort.database_models.ResortData;
import no.amirhjelperdeg.norwegianskiresort.jsonparser.NearByPlaceJSONParser;
import no.amirhjelperdeg.norwegianskiresort.models.APITypes;
import no.amirhjelperdeg.norwegianskiresort.tasks.GenericAPIRequest;
import no.amirhjelperdeg.norwegianskiresort.tasks.JSONParseResult;
import no.amirhjelperdeg.norwegianskiresort.tasks.TaskOutput;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearByResortActivity  extends AppCompatActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks ,
        GoogleApiClient.OnConnectionFailedListener
{

    public static String TAG= NearByResortActivity.class.getSimpleName();
    private GoogleMap mMap;
    private  GoogleApiClient client;
    private LocationRequest locationRequest;


    private Marker currentLocationMarker;
    int DEFAULT_RADIUS=50000;

    public  static final int REQUEST_LOCATION_CODE=88;

    Double latitude,longitude;

    private CameraPosition mCameraPosition;
    Location lastLocation;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_MAP_UPDATED = "isMapUpdated";

    List<HashMap<String,String>> placeList=null;
    ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    //private boolean isMapUpdated=false;

    private  FirebaseAuth fAuth;
    private FirebaseDatabase firebaseDatabase;
    ResortData resortData;
    List<ResortData>resortDataList=new ArrayList<>();

    private ArrayList<String>arrResortNames= new ArrayList<String>();
    private HashMap<String,HashMap<String,String>>mapResortData= new HashMap<String,HashMap<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fAuth= FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();



        setContentView(R.layout.activity_near_by_resort);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            //isMapUpdated = savedInstanceState.getBoolean(KEY_MAP_UPDATED);
            if (lastLocation!=null)
            {
                latitude=lastLocation.getLatitude();
                longitude=lastLocation.getLongitude();
            }

           // Toast.makeText(getApplicationContext(),"savedInstanceState : getting",Toast.LENGTH_LONG).show();
        }


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       /* map=(MapView)findViewById(R.id.mapView);
        map.getMapAsync(this);*/
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastLocation);
            //outState.putBoolean("isMapUpdated",isMapUpdated);
            super.onSaveInstanceState(outState);
            //Toast.makeText(getApplicationContext(),"onSaveInstanceState : saving",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       // Toast.makeText(getApplicationContext(),"onMap : First",Toast.LENGTH_LONG).show();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            //Toast.makeText(getApplicationContext(), "onMap : if", Toast.LENGTH_LONG).show();

            // Use a custom info window adapter to handle multiple lines of text in the
            // info window contents.
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                // Return null here, so that getInfoContents() is called next.
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    // Inflate the layouts for the info window, title and snippet.
                    View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                            (FrameLayout) findViewById(R.id.map), false);

                    TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                    title.setText(marker.getTitle());

                    TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                    snippet.setText(marker.getSnippet());

                    return infoWindow;
                }
            });




        }

    }


    // to check location permission
    public  boolean checkLocationPermission()
    {
       // Toast.makeText(getApplicationContext(),"CheckPermission : First",Toast.LENGTH_LONG).show();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_LOCATION_CODE);
                //Toast.makeText(getApplicationContext(),"check Permission : request",Toast.LENGTH_LONG).show();

            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_LOCATION_CODE);
               // Toast.makeText(getApplicationContext(),"check permisssion : request Else",Toast.LENGTH_LONG).show();
            }
            return  false;
        }
        else {
            return true;
        }
    }

    // handle or check permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_LOCATION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                {

                    if(client==null)
                    {

                        buildGoogleApiClient();
                    }
                }

            }
        }

    }

    protected synchronized void buildGoogleApiClient() {

        client= new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).
                addApi(LocationServices.API).build();
        client.connect();

        //Toast.makeText(getApplicationContext(),"buildClient : last",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //Toast.makeText(getApplicationContext(),"onLocationChanged : First",Toast.LENGTH_LONG).show();
        lastLocation=location;
        latitude= location.getLatitude();
        longitude=location.getLongitude();

        if(currentLocationMarker!=null)
        {

           // currentLocationMarker.remove();

        }
        LatLng latLng= new LatLng(latitude,longitude);
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(latLng);
            markerOption.title("Your Location");
            markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            currentLocationMarker = mMap.addMarker(markerOption);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
           // mMap.animateCamera(CameraUpdateFactory.zoomBy(10.5f));

        if(client!=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
            //Toast.makeText(getApplicationContext(),"onLocationChanged : removeLoacUpdates",Toast.LENGTH_LONG).show();
        }
        new GetResortData(getApplicationContext(),firebaseDatabase,new ProgressDialog(this)).execute(resortData);


       // Toast.makeText(getApplicationContext(),"onLocationChanged : Last",Toast.LENGTH_LONG).show();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //Toast.makeText(getApplicationContext(),"Onconnected : First",Toast.LENGTH_LONG).show();
        locationRequest= new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client,locationRequest,this);

            //Toast.makeText(getApplicationContext(),"onConnected : Fuse requetLocationUpdates",Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void showResortList() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                String resortName = arrResortNames.get(which);
                Intent intent = new Intent(getApplicationContext(), ResortDetail.class);
                intent.putExtra("resortName",resortName);
                intent.putExtra(resortName+"_lat",mapResortData.get(resortName).get("lat"));
                intent.putExtra(resortName+"_long",mapResortData.get(resortName).get("lng"));
                startActivity(intent);

            }
        };

        String[] arrNames= new String[(arrResortNames.size()>0)?arrResortNames.size():1];
        int i=0;
        if(arrResortNames.size()==0)
            arrNames[i]="No Data";
        for (String name:arrResortNames) {
            arrNames[i]=name;
            i++;
        }
        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(arrNames, listener)
                .show();
    }

    public  JSONParseResult parseNearByPlaceAPIResponse(String response)
    {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        NearByPlaceJSONParser nearByPlaceJSONParser= new NearByPlaceJSONParser();
        String status="";
        try {
            JSONObject json = new JSONObject(response);
            //Log.d(TAG, response.toString());
            status= json.optString("status");
            placeList=nearByPlaceJSONParser.parse(json);
            if(placeList==null ||  placeList.size()==0)
            {
                return JSONParseResult.JSON_EXCEPTION;
            }

        }catch (JSONException ex)
        {
            Log.e(TAG , "JSON parsing error ..."+ex.toString());
            ex.printStackTrace();
            return JSONParseResult.JSON_EXCEPTION;
        }
        catch (Exception ex)
        {
            if (status.equalsIgnoreCase("REQUEST_DENIED")) {
                Log.e(TAG, "JSON parsing error ..."+ ex.toString());
                return JSONParseResult.UN_AUTHORIZED_ACCESS;
            }else
            {
                Log.e(TAG, "JSON parsing error ..."+ex.toString());
                return JSONParseResult.UN_AUTHORIZED_ACCESS;
            }
        }

        return  JSONParseResult.OK;

    }

    /**
     * update MAP after getting response from nearby API
     */

    private  void updateMapUI()
    {
        //Toast.makeText(getApplicationContext(),"updateMapUI",Toast.LENGTH_LONG).show();
        if(placeList!=null) {

            boolean bFound= false;
            int counter = 0;

            for (ResortData resort : resortDataList) {

            //Toast.makeText(getApplicationContext(),"Name : "+resort.getName(),Toast.LENGTH_LONG).show();
                for (int i = 0; i < placeList.size(); i++) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    HashMap<String, String> googlePlace = placeList.get(i);

                    String placeName = googlePlace.get("place_name");
                    String vicinity = googlePlace.get("vicinity");
                    // Toast.makeText(getApplicationContext()," "+placeName,Toast.LENGTH_LONG).show();
                    if (isResortAvailableInFirebaseDatabase(placeName,resort.getName())||
                            vicinity.toLowerCase().contains(resort.getAddress().toLowerCase()
                    )) {


                        double lat = Double.parseDouble(googlePlace.get("lat"));
                        double lng = Double.parseDouble(googlePlace.get("lng"));


                        HashMap<String,String>hashMap= new HashMap<>();
                        bFound=true;
                        // store  resort info to create list
                        arrResortNames.add(resort.getName());
                        hashMap.put("name",resort.getName());
                        hashMap.put("lat",String.valueOf(lat));
                        hashMap.put("lng",String.valueOf(lng));

                        mapResortData.put(resort.getName(),hashMap);

                        // Add a marker for the selected place, with an info window
                        // showing information about that place.
                        LatLng latLng = new LatLng(lat, lng);
                        markerOptions.position(latLng);
                        markerOptions.title(placeName);
                        markerOptions.snippet(vicinity);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                        mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomBy(10.5f));


                        if (counter > 8)// we need only 10 , index : 0 1 2 3 4 5 6 7 8 9
                            break;

                        counter++;
                    }
                }

            }
            if(!bFound)
               Toast.makeText(getApplicationContext(), "No ski resort found", Toast.LENGTH_LONG).show();
        }
        else
            {
                Toast.makeText(getApplicationContext(), "No ski resort found", Toast.LENGTH_LONG).show();
            }

    }

    public boolean isResortAvailableInFirebaseDatabase(String api_ResortName, String db_ResortName)
    {
        boolean bFound=false;

        // api : oslo winterpark ski senter
        // db: winterpark

        String[]api_keyword=api_ResortName.split(" ");
        String[]db_keyword=db_ResortName.split(" ");

        for (String db_name:db_keyword) {

            for(String api_name:api_keyword)
            {
                if(db_name.toLowerCase().contains(api_name.toLowerCase()))
                {
                    bFound=true;
                    break;
                }
            }


        }

        return  bFound;

    }


    private void setResortData(ResortData resortData)
    {
        this.resortData= resortData;
        getNearByPlaces();


    }
    private void getNearByPlaces()
    {
        String PROXIMITY_RADIUS=sharedPreferences.getString(getString(R.string.pref_key_map_api_search_radius),String.valueOf(DEFAULT_RADIUS));
        new NearbyPlaces(getApplicationContext(),this,new ProgressDialog(this)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,APITypes.GOOGLE_NEARBY_PLACES.toString()
                ,latitude.toString(),longitude.toString(),PROXIMITY_RADIUS,"Ski Resort");
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showResortList();
            return true;
        }
        else if (item.getItemId() == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // Background class/thread , it will process the api call in back ground
    class NearbyPlaces extends GenericAPIRequest
    {

        public NearbyPlaces(Context context, Activity activity, ProgressDialog progressDialog)
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
        protected void updateUI() {
            updateMapUI();
        }

        @Override
        protected JSONParseResult parseResponse(String response) {
            return parseNearByPlaceAPIResponse(response);
        }
    }

    //////////////////////////
    class GetResortData extends AsyncTask<ResortData,String,ResortData>
    {
        ProgressDialog progressDialog;
        FirebaseDatabase fdb;
        String resortName;
        Context context;
        ResortData resortData;
        long lCounter=0;


        // constructor
        public GetResortData(Context context,FirebaseDatabase fdb, ProgressDialog progressDialog) {
            this.fdb = fdb;
            this.context=context;
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            if(!progressDialog.isShowing()) {
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
                            resortDataList.add(resortData);
                        }
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

            if(resortData!=null) {
                progressDialog.dismiss();
                setResortData(resortData);
            }

        }
    }




}
