package no.amirhjelperdeg.norwegianskiresort.activities;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;

import no.amirhjelperdeg.norwegianskiresort.R;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompactPrefrenceActivity {

    SharedPreferences userLoginInfo;
    private static String roleId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// home button
        userLoginInfo =getApplicationContext().getSharedPreferences("logindata", Context.MODE_PRIVATE);
        roleId=userLoginInfo.getString("roleId",null);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPrefrenceFragment()).commit();
    }



    // to hanlde back button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)// home---> back button
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

   public static void bindPrefrenceSummaryToValue(Preference preference)
   {
       preference.setOnPreferenceChangeListener(bindPrefrenceSummaryToValueListner);
       bindPrefrenceSummaryToValueListner.onPreferenceChange(preference,PreferenceManager
       .getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(),""));

   }


   private static Preference.OnPreferenceChangeListener bindPrefrenceSummaryToValueListner=new Preference.OnPreferenceChangeListener() {
       @Override
       public boolean onPreferenceChange(Preference preference, Object newValue) {
           String stringValue=newValue.toString();

           if (preference instanceof  ListPreference)
           {
               // for list prefernces , look up correct display value in prefrences entries list
               ListPreference listPreference=(ListPreference)preference;
               int index=listPreference.findIndexOfValue(stringValue);

               // set prefernece value to reflect new value
               preference.setSummary(index>=0?listPreference.getEntries()[index]:null);

           }
           else
           {
               preference.setSummary(stringValue);
           }

           return true;
       }
   };


   // fragment class to render setting layout
    public static class MainPrefrenceFragment extends  PreferenceFragment
    {


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if(roleId.equalsIgnoreCase("admin")) {
                addPreferencesFromResource(R.xml.pref_apikey_setting);
            }else{
                addPreferencesFromResource(R.xml.pref_main_setting);
            }

     /*       PreferenceScreen screen = getPreferenceScreen();
            if(roleId.equalsIgnoreCase("admin")) {
                bindPrefrenceSummaryToValue((findPreference(getString(R.string.pref_key_google_api))));
                bindPrefrenceSummaryToValue((findPreference(getString(R.string.pref_key_weather_api))));
            }
            else
            {
                Preference pref = getPreferenceManager().findPreference(getString(R.string.pref_key_google_api));
                screen.removePreference(pref);
                Preference pref2 = getPreferenceManager().findPreference(getString(R.string.pref_key_weather_api));
                screen.removePreference(pref2);
            }*/

            bindPrefrenceSummaryToValue((findPreference(getString(R.string.pref_key_temp_unit))));
            bindPrefrenceSummaryToValue((findPreference(getString(R.string.pref_key_map_api_search_radius))));


        }
    }


}
