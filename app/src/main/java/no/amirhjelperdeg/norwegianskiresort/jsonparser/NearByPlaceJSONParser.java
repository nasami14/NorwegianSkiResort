package no.amirhjelperdeg.norwegianskiresort.jsonparser;

import android.util.Log;

import no.amirhjelperdeg.norwegianskiresort.activities.NearByResortActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by apple on 2/14/18.
 */

public class NearByPlaceJSONParser
{

    public static String TAG= NearByPlaceJSONParser.class.getSimpleName();
    /**
     *  it will take json object as argument/parmaeter and will parse that entire json object then it will create the list of the key values in hash map
     * @param jsonObject
     * @return
     */


    public List<HashMap<String,String>>  parse(JSONObject jsonObject)
    {

        Log.d(TAG, jsonObject.toString());

        JSONArray places=null;
        List<HashMap<String,String>> placesList=null;

        try {

            // get all the elements in the 'places' array
            places=jsonObject.getJSONArray("results");

            placesList=getPlaces(places);

        }catch (JSONException e)
        {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

return placesList;

}
private List<HashMap<String,String>> getPlaces(JSONArray places)
{
    int placesCount=places.length();

    Log.d(TAG, placesCount+"");

    List<HashMap<String,String>>placesList= new ArrayList<HashMap<String, String>>();

    HashMap<String,String>place=null;

    for (int i=0; i<placesCount;i++)
    {
        try {

            place = getPlace((JSONObject) places.get(i));
            placesList.add(place);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
    }

    return  placesList;
}

public HashMap<String,String>getPlace(JSONObject jsonPlaceObject)
{

    HashMap<String,String>place= new HashMap<>();
    String placeName="-NA-";
    String vicinity="-NA-";
    String latitude="";
    String longitude="";
    String reference="";

    try {
        // extracting place name if its not a null
        if(!jsonPlaceObject.isNull("name"))
        {
            placeName=jsonPlaceObject.getString("name");
        }
        // extracting vicinity if its not a null
        if(!jsonPlaceObject.isNull("vicinity"))
        {
            vicinity=jsonPlaceObject.getString("vicinity");
        }

        latitude=""+jsonPlaceObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        longitude=""+jsonPlaceObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        reference=jsonPlaceObject.getString("reference");

        place.put("place_name",placeName);
        place.put("vicinity",vicinity);
        place.put("lat",latitude.trim());
        place.put("lng",longitude.trim());
        place.put("reference",reference);


    }
    catch (JSONException e)
    {
        e.printStackTrace();
        Log.d(TAG, e.toString());
    }

return  place;

}

}