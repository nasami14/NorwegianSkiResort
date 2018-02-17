package no.amirhjelperdeg.norwegianskiresort.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import no.amirhjelperdeg.norwegianskiresort.R;
import no.amirhjelperdeg.norwegianskiresort.models.APITypes;
import no.amirhjelperdeg.norwegianskiresort.utility.Constants;


public abstract class GenericAPIRequest extends AsyncTask<String, String, TaskOutput> {

    protected ProgressDialog progressDialog;
    Context context;
    Activity activity;
    SharedPreferences sharedPreferences;
    String APIKey="";

    // constructor
    public GenericAPIRequest(Context context, Activity activity, ProgressDialog progressDialog) {
        this.context = context;
        this.activity = activity;
        this.progressDialog = progressDialog;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this.activity.getApplicationContext());
    }

    @Override
    protected void onPreExecute() {

        if(!progressDialog.isShowing()) {
            progressDialog.setMessage(context.getString(R.string.message_while_calling_api));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
        String prefKey= sharedPreferences.getString(context.getResources().getString(R.string.pref_key_weather_api),"");
        APIKey= (prefKey.isEmpty())?context.getResources().getString(R.string.googleAPIKey):prefKey;
    }

    @Override
    protected TaskOutput doInBackground(String... params) {
        TaskOutput output = new TaskOutput();
        URL url=null;
        String response = "";

        try {
            if (params != null && params.length > 0) {
                final String zeroParam = params[0]; // take first parameter , it will identify the api type OR action .

                if (zeroParam.toString().equalsIgnoreCase(APITypes.GEOCODE_BY_ADDRESS_COUNTRY_ZIPCODE.toString())) {
                    if (params.length >= 6) {
                        url=provideGeocodeAPIURL(params[1], params[2], params[3], params[4], params[5]);
                    } else {
                        output.taskResult = TaskResult.BAD_INPUTES;
                    }
                } else if (zeroParam.toString().equalsIgnoreCase(APITypes.WEATHER_BY_LAT_LONG.toString())) {
                    if (params.length >= 3) {
                        url=provideWeatherLatLongAPIURL(params[1], params[2]);
                    } else {
                        output.taskResult = TaskResult.BAD_INPUTES;
                    }
                }
                else if (zeroParam.toString().equalsIgnoreCase(APITypes.GOOGLE_NEARBY_PLACES.toString())) {
                    if (params.length >= 5) {
                        url=getNearByPlaceUrl(params[1], params[2], params[3], params[4]);
                    } else {
                        output.taskResult = TaskResult.BAD_INPUTES;
                    }
                }


            }
            if (response.isEmpty()) {

                Log.i("URL", url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200)
                {
                    InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader r = new BufferedReader(inputStreamReader);

                    String line = null;
                    while ((line = r.readLine()) != null) {
                        response += line + "\n";
                    }
                    close(r);
                    urlConnection.disconnect();
                    // Background work finished successfully
                    Log.i("Task", "done successfully");
                    output.taskResult = TaskResult.SUCCESS;
                    // Save date/time for latest successful result
                    //activity.saveLastUpdateTime(PreferenceManager.getDefaultSharedPreferences(context));
                } else if (urlConnection.getResponseCode() == 429) {
                    // Too many requests
                    Log.i("Task", "too many requests");
                    output.taskResult = TaskResult.TOO_MANY_REQUESTS;
                }
                else if (urlConnection.getResponseCode() == 401) {
                    // Too many requests
                    Log.i("Task", "UN_AUTHORIZED_ACCESS");
                    output.taskResult = TaskResult.UN_AUTHORIZED_ACCESS;
                }
                else {
                    // Bad response from server
                    Log.i("Task", "bad response " + urlConnection.getResponseCode());
                    output.taskResult = TaskResult.BAD_RESPONSE;
                }
            }
        }catch (IOException e) {
                    Log.e("IOException Data", response);
                    e.printStackTrace();
                    // Exception while reading data from url connection
                    output.taskResult = TaskResult.IO_EXCEPTION;
                }

        if (TaskResult.SUCCESS.equals(output.taskResult)) {
            // Parse JSON data
            JSONParseResult JSONParseResult = parseResponse(response);
            output.JSONParseResult = JSONParseResult;
        }

        return output;
    }

    @Override
    protected void onPostExecute(TaskOutput output) {
        progressDialog.dismiss();
        handleTaskOutput(output);
        updateUI();//Update the  UI part from json response data
    }

    protected final void handleTaskOutput(TaskOutput output) {
        switch (output.taskResult) {
            case SUCCESS: {
                JSONParseResult JSONParseResult = output.JSONParseResult;

                if (JSONParseResult.CITY_NOT_FOUND.equals(JSONParseResult)) {
                    Snackbar.make(activity.findViewById(android.R.id.content), context.getString(R.string.msg_city_not_found), Snackbar.LENGTH_LONG).show();
                } else if (JSONParseResult.JSON_EXCEPTION.equals(JSONParseResult)) {
                    Snackbar.make(activity.findViewById(android.R.id.content), context.getString(R.string.msg_err_parsing_json), Snackbar.LENGTH_LONG).show();
                }
                break;
            }
            case TOO_MANY_REQUESTS: {
                Snackbar.make(activity.findViewById(android.R.id.content), context.getString(R.string.msg_too_many_requests), Snackbar.LENGTH_LONG).show();
                break;
            }
            case BAD_RESPONSE: {
                Snackbar.make(activity.findViewById(android.R.id.content), context.getString(R.string.msg_bad_response), Snackbar.LENGTH_LONG).show();
                break;
            }
            case IO_EXCEPTION: {
                Snackbar.make(activity.findViewById(android.R.id.content), context.getString(R.string.msg_connection_not_available), Snackbar.LENGTH_LONG).show();
                break;
            }
            case BAD_INPUTES: {
                Snackbar.make(activity.findViewById(android.R.id.content), context.getString(R.string.msg_bad_inputs), Snackbar.LENGTH_LONG).show();
                break;
            }
            case UN_AUTHORIZED_ACCESS: {
                Snackbar.make(activity.findViewById(android.R.id.content), context.getString(R.string.msg_un_auth_access), Snackbar.LENGTH_LONG).show();
                break;
            }

        }
    }

    private URL provideGeocodeAPIURL(String address, String city, String province, String zipcode, String countryCode) throws UnsupportedEncodingException,MalformedURLException {
        StringBuilder urlBuilder= new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?address=");

        /*urlBuilder.append(URLEncoder.encode(address,"UTF-8")+"+");
        urlBuilder.append(URLEncoder.encode(city,"UTF-8")+"+");*/
        urlBuilder.append((address.isEmpty())?"":address.replaceAll(" ","%20")+"+");
        urlBuilder.append((city.isEmpty())?"":city.trim()+"+");
        urlBuilder.append((province.isEmpty())?"":province.trim()+"+");
        urlBuilder.append(zipcode.trim()+"+");
        urlBuilder.append(countryCode.trim());
        urlBuilder.append("&key=").append(APIKey);

        return new URL(urlBuilder.toString());

    }

    private URL provideWeatherLatLongAPIURL(String lat, String lng) throws UnsupportedEncodingException,MalformedURLException {


        StringBuilder urlBuilder= new StringBuilder("http://api.openweathermap.org/data/2.5/weather?");
        String prefKey= sharedPreferences.getString(context.getResources().getString(R.string.pref_key_weather_api),"");
        String APIKey= (prefKey.isEmpty())?context.getResources().getString(R.string.weatherAPIKey):prefKey;
        urlBuilder.append("lat="+lat).append("&lon="+lng).append("&mode=json").append("&appid=").append(APIKey);

        return new URL(urlBuilder.toString());
    }

    private URL getNearByPlaceUrl(String latitude , String longitude , String PROXIMITY_RADIUS,String nearbyPlace) throws UnsupportedEncodingException,MalformedURLException
    {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&keyword="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+APIKey);
        Log.d("MapsURL", "url = "+googlePlaceUrl.toString());
        Log.w("MapsURL", "url = "+googlePlaceUrl.toString());
        Log.i("MapsActivity", "url = "+googlePlaceUrl.toString());
        return new URL(googlePlaceUrl.toString());
    }
    private static void close(Closeable x) {
        try {
            if (x != null) {
                x.close();
            }
        } catch (IOException e) {
            Log.e("IOException Data", "Error occurred while closing stream");
        }
    }



    protected void updateUI() { }

    protected abstract JSONParseResult parseResponse(String response);
}
