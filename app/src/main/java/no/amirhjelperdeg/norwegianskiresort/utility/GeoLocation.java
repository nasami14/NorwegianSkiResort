package no.amirhjelperdeg.norwegianskiresort.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by apple on 2/4/18.
 */

public class GeoLocation
{
 String  address="";
 String city="";
 String zipCode="";
 String countryCode="";
 String TAG="GeoLocation";

 String strURL="https://maps.googleapis.com/maps/api/geocode/json?address=" ;
 //=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyB_m97yImGJsZeoIkuhzTyqcRQc8Ha-VeQ"


 public void getLocation(String address, String city , String zipcode, String countryCode, String APIKey)
 {

  try {

   String response ="";
   strURL=strURL+address+"+"+city+"+"+zipcode+"+"+countryCode+"&key="+APIKey;
   //System.out.println(strURL);
   URL url= new URL(strURL);

   HttpURLConnection urlConnection= (HttpURLConnection)url.openConnection();

   if(urlConnection.getResponseCode()==200)
   {
    InputStreamReader inputStreamReader= new InputStreamReader(urlConnection.getInputStream());
    BufferedReader reader= new BufferedReader(inputStreamReader);

    String line="";

    while ((line=reader.readLine())!=null)
    {
     response+=line+"\n";

    }
    System.out.println("response : "+response);

    reader.close();
    urlConnection.disconnect();

    parseResponse(response);
   }

   else if(urlConnection.getResponseCode()==429)
   {
   }
   else
   {
   }

  } catch (MalformedURLException e) {
   e.printStackTrace();
  }
  catch (IOException e) {
   e.printStackTrace();
  }

 }

 public void parseResponse(String response)
 {
  try {

   JSONObject json= new JSONObject(response);
   JSONArray results= json.getJSONArray("results");


    JSONObject geometry=(JSONObject)results.get(0);
    JSONArray address_components=(JSONArray)geometry.optJSONArray("address_components");
    JSONObject geometryArray=(JSONObject)geometry.opt("geometry");
    JSONObject location= (JSONObject)geometryArray.opt("location");
    Double lat=(Double)location.opt("lat");
    Double lng=(Double)location.opt("lng");
    System.out.println(lat + " , "+lng+ " , "+address_components.length());


   //System.out.println(json.getJSONObject("status").toString());


  } catch (JSONException e) {
   e.printStackTrace();
  }

 }

 public static void main(String[]args)
 {
  new GeoLocation().getLocation("chitrakoot","Jaipur","302021","IN","AIzaSyB_m97yImGJsZeoIkuhzTyqcRQc8Ha-VeQ");

 }



}