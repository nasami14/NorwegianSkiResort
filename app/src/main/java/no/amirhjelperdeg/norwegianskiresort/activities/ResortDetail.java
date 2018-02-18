package no.amirhjelperdeg.norwegianskiresort.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import no.amirhjelperdeg.norwegianskiresort.R;
import no.amirhjelperdeg.norwegianskiresort.database_models.ResortData;
import no.amirhjelperdeg.norwegianskiresort.models.APITypes;
import no.amirhjelperdeg.norwegianskiresort.models.GetWeatherData;
import no.amirhjelperdeg.norwegianskiresort.tasks.GenericAPIRequest;
import no.amirhjelperdeg.norwegianskiresort.tasks.JSONParseResult;
import no.amirhjelperdeg.norwegianskiresort.tasks.TaskOutput;
import no.amirhjelperdeg.norwegianskiresort.utility.UnitConvertor;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ResortDetail extends AppCompatActivity {

    String TAG="ResortDetail";
    String imagePath=""; // variaable for imagePath;

    GetWeatherData todayWeather = new GetWeatherData();
    Double lat,lng;

    Dialog popupDialog;
    ImageView imgSlopePopup,imgLiftsPopup,imgTimePopup,imgWeatherPopup,imgContactPopup,
            imgChargePopup,imgResortInfo;
    TextView textResortName,textResortAddress;

    private ArrayList<HashMap<String,String>> resortDataList=new ArrayList<HashMap<String,String>>();
    private  FirebaseAuth fAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage storage;
    String resortName;
    ResortData resortData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        resortName=getIntent().getStringExtra("resortName");

        if(getIntent().hasExtra(resortName+"_lat") && getIntent().hasExtra(resortName+"_long")) {
            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor=sharedPreferences.edit();
            String latitude = getIntent().getStringExtra(resortName + "_lat");
            String longitude = getIntent().getStringExtra(resortName + "_long");
            editor.putString(resortName+"_lat",latitude);
            editor.putString(resortName+"_long",longitude);
            editor.commit();
        }


        fAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        ProgressDialog p= new ProgressDialog(this);
        new GetResortData("",this,firebaseDatabase,resortName,p).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,resortData);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resort_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        popupDialog= new Dialog(this);

        imgResortInfo=(ImageView) findViewById(R.id.imgResortDescription);
        imgSlopePopup=(ImageView)findViewById(R.id.rd_img_slope);
        imgTimePopup=(ImageView) findViewById(R.id.rd_img_times);
        imgWeatherPopup=(ImageView) findViewById(R.id.rd_img_weather);
        imgContactPopup=(ImageView) findViewById(R.id.rd_img_telephone);
        imgChargePopup=(ImageView) findViewById(R.id.rd_img_charge);
        imgLiftsPopup=(ImageView) findViewById(R.id.rd_img_lifts);

        textResortName=(TextView)findViewById(R.id.rd_resortName);
        textResortAddress=(TextView)findViewById(R.id.rd_address);

        // set name
        textResortName.setText(resortName);

        imgSlopePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup("displaySlopeDetails");
            }
        });

        imgResortInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup("displayDisplayResortDescription");

            }
        });
        imgLiftsPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup("displayLifts");
            }
        });
        imgTimePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup("displayOpeningHours");
            }
        });
        imgContactPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup("displayContactDetails");
            }
        });

        imgChargePopup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPopup("displayChargesDetails");
            }
        });

        imgWeatherPopup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                displayWeatherData();
            }
        });


    }
    public void setResortAddress()
    {
        textResortAddress.setText(resortData.getAddress());
        setResortLocation();
    }
    public  void showPopup(String popupType)
    {
        ProgressDialog p= new ProgressDialog(this);
        new GetResortData(popupType,this,firebaseDatabase,resortName,p).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,resortData);

    }

    public void closePopup()
    {
        if(popupDialog!=null)
        {
            popupDialog.dismiss();
        }

    }
    public void displayDisplayResortDescription()
    {

        // declaration
        TextView resortDesc;
        final ImageView closePopup,resortImage;
        popupDialog.setContentView(R.layout.popup_resortinfo); // set layout
        closePopup=(ImageView)popupDialog.findViewById(R.id.closePopup);

        //initialize
        resortDesc=(TextView) popupDialog.findViewById(R.id.resortDescription);
        resortImage=(ImageView)popupDialog.findViewById(R.id.resortImage);

        resortDesc.setText(resortData.getDescription());
        if(imagePath!=null && !imagePath.trim().isEmpty()) {

            StorageReference checkProfile = storage.getReference();
            checkProfile.child(imagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Glide
                            .with(getApplicationContext())
                            .load(uri)
                            .into(resortImage);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Toast.makeText(getApplicationContext(), "Failed ", Toast.LENGTH_LONG).show();
                    exception.printStackTrace();
                }
            });
        }
        popupDialog.show();
        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopup();
            }
        });

    }

    public  void displaySlopeDetails()
    {

        TextView totalSlope,easySloope,intermediateSlop,advancedSlope;
        final ImageView closePopup;
        popupDialog.setContentView(R.layout.pupup_slope);

        totalSlope=(TextView)popupDialog.findViewById(R.id.popup_total_slope);
        easySloope=(TextView)popupDialog.findViewById(R.id.easykm);
        intermediateSlop=(TextView)popupDialog.findViewById(R.id.intermediateKM);
        advancedSlope=(TextView)popupDialog.findViewById(R.id.advancedKM);
        closePopup=(ImageView)popupDialog.findViewById(R.id.closePopup);

        totalSlope.setText(resortData.getTotalSlopes() + " KM");
        easySloope.setText(resortData.getEasySlope()+ " KM");
        intermediateSlop.setText(resortData.getIntermediateSlope()+" KM");
        advancedSlope.setText(resortData.getAdvancedSlope()+" KM");

        popupDialog.show();

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopup();
            }
        });

    }

    public void displayLifts()
    {

        TextView chairLift,towRopes,tBarJBar,totalLifts;
        final ImageView closePopup;
        popupDialog.setContentView(R.layout.popup_lift);

        totalLifts=(TextView)popupDialog.findViewById(R.id.popup_total_slope);
        chairLift=(TextView)popupDialog.findViewById(R.id.chairLift);
        towRopes=(TextView)popupDialog.findViewById(R.id.towRopes);
        tBarJBar=(TextView)popupDialog.findViewById(R.id.tBarLift);
        closePopup=(ImageView)popupDialog.findViewById(R.id.closePopup);

        totalLifts.setText(resortData.getTotalLifts() );
        chairLift.setText("Chair Lifts : "+resortData.getChairlLifts());
        towRopes.setText("Tow Rope Lifts : "+resortData.getTowRopeLifts());
        tBarJBar.setText("T-Bar/J-Bar Lifts : "+resortData.gettBarJBarLifts());

        popupDialog.show();

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopup();
            }
        });

    }

    public void displayOpeningHours()
    {

        TextView currentSeason,openingTimes,notes;
        final ImageView closePopup;
        popupDialog.setContentView(R.layout.popup_openinghours);

        currentSeason=(TextView)popupDialog.findViewById(R.id.currentSeason);
        openingTimes=(TextView)popupDialog.findViewById(R.id.openingTimes);
        notes=(TextView)popupDialog.findViewById(R.id.notes);
        closePopup=(ImageView)popupDialog.findViewById(R.id.closePopup);

        currentSeason.setText("Current Season: November to April" );
        openingTimes.setText("Opening times: "+resortData.getOpenHours()+"-"+resortData.getCloseHOurs());
        notes.setText("Notes : The Opening times are provided by the ski resort "+resortName+" and can vary based on external conditions, day of the week, school holidays and public holidays.");

        popupDialog.show();

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopup();
            }
        });

    }

    public void displayContactDetails()
    {

        TextView telePhone,addressLine1,addressLine2,addressLine3,emailId;
        final ImageView closePopup;
        popupDialog.setContentView(R.layout.popup_contacts);

        telePhone=(TextView)popupDialog.findViewById(R.id.telephone);
        addressLine1=(TextView)popupDialog.findViewById(R.id.addressLine1);
        addressLine2=(TextView)popupDialog.findViewById(R.id.addressLine2);
        addressLine3=(TextView)popupDialog.findViewById(R.id.addressLine3);
        emailId=(TextView)popupDialog.findViewById(R.id.email);

        closePopup=(ImageView)popupDialog.findViewById(R.id.closePopup);

        telePhone.setText(resortData.getMobile());
        addressLine1.setText(resortData.getAddress());
        addressLine2.setText(resortData.getZipcode()+" , "+resortData.getProvince());
        addressLine3.setText(resortData.getCountry());
        emailId.setText("Email :"+resortData.getEmail());

        popupDialog.show();

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopup();
            }
        });

    }
    public void displayChargesDetails()
    {

        TextView chargesChildren,chargesYouth,chargesAdults;
        final ImageView closePopup;
        popupDialog.setContentView(R.layout.popup_ski_charge);

        chargesChildren=(TextView)popupDialog.findViewById(R.id.chargesChildren);
        chargesYouth=(TextView)popupDialog.findViewById(R.id.chargesYouth);
        chargesAdults=(TextView)popupDialog.findViewById(R.id.chargesAdults);

        closePopup=(ImageView)popupDialog.findViewById(R.id.closePopup);

        chargesChildren.setText("Children : "+resortData.getChildrenCharges());
        chargesYouth.setText("Youth : "+resortData.getYouthChildren());
        chargesAdults.setText("Adults : "+resortData.getAdultCharges());

        popupDialog.show();

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopup();
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

//  call the Geocode api and weather api after click on  weather popup
    public void displayWeatherData()
    {
        if(lat!=null && lng!=null) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        new WeatherData(getApplicationContext(), this, progressDialog).execute(APITypes.WEATHER_BY_LAT_LONG.toString(),
                lat.toString(), lng.toString());
    }else{
            Toast.makeText(getApplicationContext(),"Location Not found",Toast.LENGTH_LONG).show();
        }

    }
    public void setResortLocation()
    {
        ProgressDialog progressDialog= new ProgressDialog(this);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String latitude=sharedPreferences.getString(resortName+"_lat","");
        String longitude=sharedPreferences.getString(resortName+"_long","");
        if(latitude.isEmpty() || longitude.isEmpty()) {
            new Geocode(getApplicationContext(), this, progressDialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    APITypes.GEOCODE_BY_ADDRESS_COUNTRY_ZIPCODE.toString(),
                  resortData.getAddress(), "", resortData.getProvince(), resortData.getZipcode(), "NO");
                    //"Chitrakoot", "", "Jaipur", "302021", "IN");
        }
        else
        {
            lat= Double.valueOf(latitude);
            lng= Double.valueOf(longitude);
        }
    }



    public JSONParseResult parseGeocodeAPIResponse(String response)
    {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String status="";
        try {

            JSONObject json = new JSONObject(response);
            status= json.optString("status");
            JSONArray results = json.getJSONArray("results");

            JSONObject geometry = (JSONObject) results.get(0);
            JSONArray address_components = (JSONArray) geometry.optJSONArray("address_components");
            JSONObject geometryArray = (JSONObject) geometry.opt("geometry");
            JSONObject location = (JSONObject) geometryArray.opt("location");
            lat = (Double) location.opt("lat");
            lng = (Double) location.opt("lng");
            Log.i(TAG, lat + " , " + lng + " , " + address_components.length());
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString(resortName+"_lat",String.valueOf(lat));
            editor.putString(resortName+"_long",String.valueOf(lng));
            editor.commit();
        }catch (JSONException ex)
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
        return JSONParseResult.OK;
    }

    public JSONParseResult parseWeatherAPIResponse(String response)
    {

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            JSONObject reader = new JSONObject(response);

            final String code = reader.optString("cod");
            if ("404".equals(code)) {
                return JSONParseResult.CITY_NOT_FOUND;
            }
            if ("401".equals(code)) {
                return JSONParseResult.UN_AUTHORIZED_ACCESS;
            }

            String city = reader.getString("name");
            String country = "";
            JSONObject countryObj = reader.optJSONObject("sys");
            if (countryObj != null) {
                country = countryObj.getString("country");
                String sunrise=String.valueOf(countryObj.getLong("sunrise"));
                String sunset=String.valueOf(countryObj.getLong("sunset"));
                todayWeather.setSunrise(sunrise);
                todayWeather.setSunset(sunset);
            }
            todayWeather.setCity(city);
            todayWeather.setCountry(country);

            JSONObject coordinates = reader.getJSONObject("coord");
            if (coordinates != null) {
                // we can store lat and long in cache as favorite location
                coordinates.getDouble("lon");
                coordinates.getDouble("lat");
            }

            JSONObject main = reader.getJSONObject("main");

            String unitPref=sharedPreferences.getString(getString(R.string.pref_key_temp_unit),"°C");

            if(unitPref.equalsIgnoreCase("°F"))
            {
                todayWeather.setTemperature(String.valueOf(new DecimalFormat("#.0").
                        format(UnitConvertor.kelvinToFahrenheit(new Double(main.getDouble("temp")).floatValue()))));
            }
            else if (unitPref.equalsIgnoreCase("K"))
            {
                todayWeather.setTemperature(String.valueOf(new DecimalFormat("#.0").
                        format(new Double(main.getDouble("temp")).floatValue())));
            }
            else{
                todayWeather.setTemperature(String.valueOf(new DecimalFormat("#.0").
                        format(UnitConvertor.kelvinToCelsius(new Double(main.getDouble("temp")).floatValue()))));
            }



            todayWeather.setPressure(String.valueOf(new DecimalFormat("#.0").format(main.getDouble("pressure"))));
            todayWeather.setHumidity(String.valueOf(new DecimalFormat("#.0").format(main.getDouble("humidity"))));

            // get weather arrayObject from API json response ,then from this array object get description
            todayWeather.setDescription(reader.getJSONArray("weather").getJSONObject(0).getString("description"));
            // wind json object
            JSONObject windObj = reader.getJSONObject("wind");
            todayWeather.setWind(String.valueOf(windObj.getDouble("speed")));// set speed
            if (windObj.has("deg")) {
                todayWeather.setWindDirectionDegree(windObj.getDouble("deg"));
            } else {
                System.out.println("No wind direction available");
                todayWeather.setWindDirectionDegree(null);
            }

            // check for rain and snow condition
            JSONObject rainObj = reader.optJSONObject("rain");
            String rain;
            if (rainObj != null) {
                rain = getRainString(rainObj);
            } else {
                JSONObject snowObj = reader.optJSONObject("snow");
                if (snowObj != null) {
                    rain = getRainString(snowObj);
                } else {
                    rain = "0";
                }
            }
            todayWeather.setRain(rain);

            final String idString = String.valueOf(reader.getJSONArray("weather").getJSONObject(0).getLong("id"));
            todayWeather.setId(idString);
            todayWeather.setIcon(setWeatherIcon(Integer.parseInt(idString), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
            String lastUpdated=String.valueOf(reader.optLong("dt"));
            todayWeather.setLastUpdated(lastUpdated);
            //

        } catch (JSONException e) {
            Log.d("WeatherAPI Exception",e.toString());
            e.printStackTrace();
            return JSONParseResult.JSON_EXCEPTION;
        }

        return JSONParseResult.OK;
    }
    private String setWeatherIcon(int actualId, int hourOfDay) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            if (hourOfDay >= 7 && hourOfDay < 20) {
                icon = this.getString(R.string.weather_sunny);
            } else {
                icon = this.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = this.getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = this.getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = this.getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = this.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = this.getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = this.getString(R.string.weather_rainy);
                    break;
            }
        }
        return icon;
    }

    public static String getRainString(JSONObject rainObj) {
        String rain = "0";
        if (rainObj != null) {
            rain = rainObj.optString("3h", "fail");
            if ("fail".equals(rain)) {
                rain = rainObj.optString("1h", "0");
            }
        }
        return rain;
    }

    public void  updateWeatherPopupUI()
    {
        Intent intent = new Intent(getApplicationContext(),WeatherActivity.class);
        intent.putExtra("weatherDesc",todayWeather.getDescription());
        intent.putExtra("temp",todayWeather.getTemperature());
        intent.putExtra("wind",todayWeather.getWind());
        intent.putExtra("pressure",todayWeather.getPressure());
        intent.putExtra("humidity",todayWeather.getHumidity());
        intent.putExtra("sunrise",todayWeather.getSunrise());
        intent.putExtra("sunset",todayWeather.getSunset());
        intent.putExtra("todayIcon",todayWeather.getIcon());
        intent.putExtra("lastUpdated",todayWeather.getLastUpdated());
        startActivity(intent);
    }
    @Override
    public void onResume() {
        super.onResume();
    // Restart activity to apply theme
   /* overridePendingTransition(0, 0);
    finish();
    overridePendingTransition(0, 0);
    startActivity(getIntent());
*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class WeatherData extends  GenericAPIRequest
    {

        public WeatherData(Context context, Activity activity, ProgressDialog progressDialog)
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
            updateWeatherPopupUI();
        }

        @Override
        protected JSONParseResult parseResponse(String response) {
            return parseWeatherAPIResponse(response);
        }
    }

    class Geocode extends  GenericAPIRequest
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
        ResortDetail activity;
        ResortData resortData;
        String popupType;
        long lCounter=0;


        // constructor
        public GetResortData(String popupType,ResortDetail activity,FirebaseDatabase fdb,String resortName, ProgressDialog progressDialog) {
            this.fdb = fdb;
            this.activity=activity;
            this.resortName = resortName;
            this.progressDialog = progressDialog;
            this.popupType=popupType;
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
                            if (resort.child("name").getValue(String.class).toString().trim().equalsIgnoreCase(resortName.trim())) {
                                resortData = resort.getValue(ResortData.class);
                                activity.resortData=resortData;
                                activity.imagePath=resort.child("imagePath").getValue(String.class).toString();
                                setResortAddress();
                            }

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

            if(resortData!=null)
                progressDialog.dismiss();

            if (popupType.equalsIgnoreCase("displaySlopeDetails"))
            {
                displaySlopeDetails();

            }
            else if (popupType.equalsIgnoreCase("displayDisplayResortDescription"))
            {
                displayDisplayResortDescription();

            }
            else if (popupType.equalsIgnoreCase("displayLifts"))
            {
                displayLifts();

            }
            else if (popupType.equalsIgnoreCase("displayChargesDetails"))
            {
                displayChargesDetails();

            }
            else if (popupType.equalsIgnoreCase("displayContactDetails"))
            {
                displayContactDetails();

            }
            else if (popupType.equalsIgnoreCase("displayOpeningHours"))
            {
                displayOpeningHours();

            }


        }
    }


}
