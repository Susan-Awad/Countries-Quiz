package edu.uga.cs.countriesquiz;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;

public class CountryDBWriter extends AsyncTask<Country, Country> {
    public static final String DEBUG_TAG = "CountryDBWriter";

    private CountryData data1;
    private Context context;

    public CountryDBWriter(Context c) {
        data1 = new CountryData(c);
        context = c;
    }

    @Override
    protected Country doInBackground( Country... countries ) {
        Country country = countries[0];
        try {
            data1.open();
            InputStream r = context.getAssets().open("countries_data.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(r));
            String[] nextRow;


            if (data1.getCountries().isEmpty()) {
                while ((nextRow = reader.readNext()) != null) {
                    if (nextRow.length == 4) {
                        String country_name = nextRow[0];
                        String capital = nextRow[1];
                        String continent = nextRow[2];
                        String abbreviation = nextRow[3];

                        data1.storeCountries(new Country(country_name, capital, continent, abbreviation));
                    }
                }
            }
            data1.close();
            reader.close();
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "MainActivity: Exception reading = " + e);
        }

        return country;
    }

    @Override
    protected void onPostExecute( Country country ) {
        Log.d( DEBUG_TAG, "Country saved: " + country );
    }
}
