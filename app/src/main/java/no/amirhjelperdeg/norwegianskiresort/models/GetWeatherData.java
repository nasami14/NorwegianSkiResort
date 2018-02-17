package no.amirhjelperdeg.norwegianskiresort.models;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import no.amirhjelperdeg.norwegianskiresort.R;


/**
 * Created by apple on 2/4/18.
 */

public class GetWeatherData {

    private String city;
    private String country;
    private Date date;
    private String temperature;
    private String description;
    private String wind;
    private Double windDirectionDegree;
    private String pressure;
    private String humidity;
    private String rain;
    private String id;
    private String icon;
    private String lastUpdated;
    private String sunrise;
    private String sunset;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public Double getWindDirectionDegree() {
        return windDirectionDegree;
    }

    public void setWindDirectionDegree(Double windDirectionDegree) {
        this.windDirectionDegree = windDirectionDegree;
    }

    public WindDirection getWindDirection() {
        return WindDirection.byDegree(windDirectionDegree);
    }

    public WindDirection getWindDirection(int numberOfDirections) {
        return WindDirection.byDegree(windDirectionDegree, numberOfDirections);
    }

    public boolean isWindDirectionAvailable() {
        return windDirectionDegree != null;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getSunrise(){
        return this.sunrise;
    }

    public void setSunrise(String dateString) {
        try {
            //LocalDateTime ldt= Instant.ofEpochSecond(1518018992).atZone(ZoneId.of("Europe/Oslo")).toLocalDateTime();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
            sunrise=inputFormat.format(new Date(Long.parseLong(dateString) * 1000));

        }
        catch (Exception e) {
               sunrise = new Date().toString(); // make the error somewhat obvious
                e.printStackTrace();

        }
    }

    public String getSunset(){
        return this.sunset;
    }

    public void setSunset(String dateString) {
        try {
            //LocalDateTime ldt= Instant.ofEpochSecond(1518018992).atZone(ZoneId.of("Europe/Oslo")).toLocalDateTime();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
            sunset=inputFormat.format(new Date(Long.parseLong(dateString) * 1000));
        }
        catch (Exception e) {
            sunset = new Date().toString(); // make the error somewhat obvious
            e.printStackTrace();

        }
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Date getDate(){
        return this.date;
    }

    public void setDate(String dateString) {
        try {
            setDate(new Date(Long.parseLong(dateString) * 1000));
        }
        catch (Exception e) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            try {
                setDate(inputFormat.parse(dateString));
            }
            catch (ParseException e2) {
                setDate(new Date()); // make the error somewhat obvious
                e2.printStackTrace();
            }
        }
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public void setLastUpdated(String lastUpdated) {

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
            this.lastUpdated =inputFormat.format(new Date(Long.parseLong(lastUpdated) * 1000));
        }
        catch (Exception e) {
            this.lastUpdated = new Date().toString(); // make the error somewhat obvious
            e.printStackTrace();

        }

    }

    public String getLastUpdated() {
        return lastUpdated;
    }



    public enum WindDirection {
        // don't change order
        NORTH, NORTH_NORTH_EAST, NORTH_EAST, EAST_NORTH_EAST,
        EAST, EAST_SOUTH_EAST, SOUTH_EAST, SOUTH_SOUTH_EAST,
        SOUTH, SOUTH_SOUTH_WEST, SOUTH_WEST, WEST_SOUTH_WEST,
        WEST, WEST_NORTH_WEST, NORTH_WEST, NORTH_NORTH_WEST;

        public static WindDirection byDegree(double degree) {
            return byDegree(degree, WindDirection.values().length);
        }

        public static WindDirection byDegree(double degree, int numberOfDirections) {
            WindDirection[] directions = WindDirection.values();
            int availableNumberOfDirections = directions.length;

            int direction = windDirectionDegreeToIndex(degree, numberOfDirections)
                    * availableNumberOfDirections / numberOfDirections;

            return directions[direction];
        }

        public String getLocalizedString(Context context) {
            // usage of enum.ordinal() is not recommended, but whatever
            return context.getResources().getStringArray(R.array.windDirections)[ordinal()];
        }

        public String getArrow(Context context) {
            // usage of enum.ordinal() is not recommended, but whatever
            return context.getResources().getStringArray(R.array.windDirectionArrows)[ordinal() / 2];
        }
    }

    // you may use values like 4, 8, etc. for numberOfDirections
    public static int windDirectionDegreeToIndex(double degree, int numberOfDirections) {
        // to be on the safe side
        degree %= 360;
        if(degree < 0) degree += 360;
        degree += 180 / numberOfDirections; // add offset to make North start from 0
        int direction = (int)Math.floor(degree * numberOfDirections / 360);
        return direction % numberOfDirections;
    }
}

