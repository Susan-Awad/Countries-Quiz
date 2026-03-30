package edu.uga.cs.countriesquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CountryData {
    public static final String DEBUS_TAG = "CountriesData";

    // reference to db
    private SQLiteDatabase db;
    private SQLiteOpenHelper countriesDBHelper;

    private static final String[] columns = {
            CountriesDBHelper.COUNTRIESINFO_COLUMN_ID,
            CountriesDBHelper.COUNTRIESINFO_COLUMN_COUNTRY,
            CountriesDBHelper.COUNTRIESINFO_COLUMN_CAPITAL,
            CountriesDBHelper.COUNTRIESINFO_COLUMN_CONTINENT,
            CountriesDBHelper.COUNTRIESINFO_COLUMN_ABBREVIATION
    };

    public CountryData(Context context) {
        countriesDBHelper = CountriesDBHelper.getInstance(context);
    }

    public void open() {
        db = countriesDBHelper.getWritableDatabase();
        Log.d(DEBUS_TAG, "CountryData: db open");
    }

    public void close() {
        if (countriesDBHelper != null) {
            countriesDBHelper.close();
            Log.d(DEBUS_TAG, "CountryData: db close");
        }
    }

    public boolean isDBOpen() {
        return db.isOpen();
    }

    // returns all the countries in List format
    public List<Country> getCountries() {
        ArrayList<Country> countries = new ArrayList<>();
        Cursor cursor = null;
        int colIndex;

        try {
            // executes the select query
            cursor = db.query(CountriesDBHelper.TABLE_COUNTRIESINFO, columns,
                    null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                while(cursor.moveToNext()) {
                    if (cursor.getColumnCount() >= 5) {

                        // gets all attributes
                        colIndex = cursor.getColumnIndex(CountriesDBHelper.COUNTRIESINFO_COLUMN_ID);
                        long id = cursor.getLong(colIndex);
                        colIndex = cursor.getColumnIndex(CountriesDBHelper.COUNTRIESINFO_COLUMN_COUNTRY);
                        String country_name = cursor.getString(colIndex);
                        colIndex = cursor.getColumnIndex(CountriesDBHelper.COUNTRIESINFO_COLUMN_CAPITAL);
                        String capital = cursor.getString(colIndex);
                        colIndex = cursor.getColumnIndex(CountriesDBHelper.COUNTRIESINFO_COLUMN_CONTINENT);
                        String continent = cursor.getString(colIndex);
                        colIndex = cursor.getColumnIndex(CountriesDBHelper.COUNTRIESINFO_COLUMN_ABBREVIATION);
                        String abbreviation = cursor.getString(colIndex);

                        // creates new country and adds it to the list
                        Country country = new Country(country_name, capital, continent, abbreviation);
                        country.setId(id);
                        countries.add(country);
                        Log.d(DEBUS_TAG, "CountryData: Retrieved Country = " + country);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(DEBUS_TAG, "CountryData: Exception caught = " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return countries;
    }

    // stores the countries in the db
    public void storeCountries(Country country) {
        // sets the column info to the variables given by the country
        ContentValues values = new ContentValues();
        values.put(CountriesDBHelper.COUNTRIESINFO_COLUMN_COUNTRY, country.getCountry());
        values.put(CountriesDBHelper.COUNTRIESINFO_COLUMN_CAPITAL, country.getCapital());
        values.put(CountriesDBHelper.COUNTRIESINFO_COLUMN_CONTINENT, country.getContinent());
        values.put(CountriesDBHelper.COUNTRIESINFO_COLUMN_ABBREVIATION, country.getAbbreviation());

        // insert row into db
        long id = db.insert(CountriesDBHelper.TABLE_COUNTRIESINFO, null, values);

        // sets the id given from the db to our country variable
        country.setId(id);
        Log.d(DEBUS_TAG, "Stored new country in db with id: " + id);
    }
}
