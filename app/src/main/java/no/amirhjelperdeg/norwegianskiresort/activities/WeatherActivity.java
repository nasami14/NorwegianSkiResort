package no.amirhjelperdeg.norwegianskiresort.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import no.amirhjelperdeg.norwegianskiresort.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class WeatherActivity extends AppCompatActivity {

    Typeface weatherFont;
    TextView todayTemperature,todayDescription,todayWind,todayPressure,todayHumidity,todaySunrise,
            todaySunset,todayDate,todayIcon;
        
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.weather_popup);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Initialize textboxes
            Intent intent= getIntent();
            getSupportActionBar().setTitle("Weather");

            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            //   DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());

            todayTemperature = (TextView) findViewById(R.id.todayTemperature);
            todayDescription = (TextView) findViewById(R.id.todayDescription);
            todayWind = (TextView) findViewById(R.id.todayWind);
            todayPressure = (TextView) findViewById(R.id.todayPressure);
            todayHumidity = (TextView) findViewById(R.id.todayHumidity);
            todaySunrise = (TextView) findViewById(R.id.todaySunrise);
            todaySunset = (TextView) findViewById(R.id.todaySunset);
            todayDate = (TextView) findViewById(R.id.todayDate);
            todayIcon = (TextView) findViewById(R.id.todayIcon);

            // get the values from intent
            todayDescription.setText(intent.getStringExtra("weatherDesc"));
            String unitPref=sharedPreferences.getString(getString(R.string.pref_key_temp_unit),"°C");

            if(unitPref.equalsIgnoreCase("°F")) {
                todayTemperature.setText(intent.getStringExtra("temp") + " °F");
            }
            else if(unitPref.equalsIgnoreCase("K")){
                todayTemperature.setText(intent.getStringExtra("temp") + " K");
            }
            else{
                todayTemperature.setText(intent.getStringExtra("temp") + " °C");
            }
            todayWind.setText(getString(R.string.wind)+": "+intent.getStringExtra("wind")+" m/s");
            todayPressure.setText(getString(R.string.pressure)+": "+intent.getStringExtra("pressure")+" hpa");
            todayHumidity.setText(getString(R.string.humidity)+": "+intent.getStringExtra("humidity")+" %");

            weatherFont = Typeface.createFromAsset(this.getAssets(), "fonts/weather.ttf");
            todayIcon.setTypeface(weatherFont);
            todayIcon.setText(intent.getStringExtra("todayIcon"));

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
           // todayDate.setText("Last Updated:\n"+inputFormat.format(new Date()));
            try {
                todayDate.setText("Last Updated:\n"+inputFormat.format(inputFormat.parse(intent.getStringExtra("lastUpdated"))));

                SimpleDateFormat inputFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                inputFormat3.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
                inputFormat3.format(inputFormat3.parse(intent.getStringExtra("sunrise")));

                SimpleDateFormat inputFormat2 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                inputFormat2.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
                todaySunrise.setText(getString(R.string.sunrise)+": "+inputFormat2.format(inputFormat3.getCalendar().getTime()));
                inputFormat3.format(inputFormat3.parse(intent.getStringExtra("sunset")));
                todaySunset.setText(getString(R.string.sunrise)+": "+inputFormat2.format(inputFormat3.getCalendar().getTime()));
            } catch (Exception e) {
                e.printStackTrace();
                todaySunrise.setText(getString(R.string.sunrise)+": "+intent.getStringExtra("sunrise"));
                todaySunset.setText(getString(R.string.sunset)+": "+intent.getStringExtra("sunset"));
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


        @Override
        public void onResume() {
            super.onResume();

        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }

       
}
