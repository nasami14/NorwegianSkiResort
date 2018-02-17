package no.amirhjelperdeg.norwegianskiresort.utility;


import java.util.Locale;

public class UnitConvertor {

    public static float kelvinToCelsius(float kelvinTemp) {
        return kelvinTemp - 273.15f;
    }

    public static float kelvinToFahrenheit(float kelvinTemp)
    {
        return (((9 * kelvinToCelsius(kelvinTemp)) / 5) + 32);
    }


}
