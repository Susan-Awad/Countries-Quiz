package edu.uga.cs.countriesquiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CountriesDBHelper extends SQLiteOpenHelper{
    private static final String DEBUG_TAG = "CountriesDBHelper";

    private static final String DB_NAME = "countriesinfo.db";
    private static final int DB_VERSION = 1;

    // Define all names (strings) for table and column names.
    // This will be useful if we want to change these names later.
    public static final String TABLE_COUNTRIESINFO = "countriesinfo";
    public static final String COUNTRIESINFO_COLUMN_ID = "_id";
    public static final String COUNTRIESINFO_COLUMN_COUNTRY = "country";
    public static final String COUNTRIESINFO_COLUMN_CAPITAL = "capital";
    public static final String COUNTRIESINFO_COLUMN_CONTINENT = "continent";
    public static final String COUNTRIESINFO_COLUMN_ABBREVIATION = "abbreviation";

    // This is a reference to the only instance for the helper.
    private static CountriesDBHelper helperInstance;

    // A Create table SQL statement to create a table for job leads.
    // Note that _id is an auto increment primary key, i.e. the database will
    // automatically generate unique id values as keys.
    private static final String CREATE_JOBLEADS =
            "create table " + TABLE_COUNTRIESINFO + " ("
                    + COUNTRIESINFO_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COUNTRIESINFO_COLUMN_COUNTRY + " TEXT, "
                    + COUNTRIESINFO_COLUMN_CAPITAL + " TEXT, "
                    + COUNTRIESINFO_COLUMN_CONTINENT + " TEXT, "
                    + COUNTRIESINFO_COLUMN_ABBREVIATION + " TEXT"
                    + ")";

    // Note that the constructor is private!
    // So, it can be called only from
    // this class, in the getInstance method.
    private CountriesDBHelper( Context context ) {
        super( context, DB_NAME, null, DB_VERSION );
    }

    // Access method to the single instance of the class.
    // It is synchronized, so that only one thread can executes this method, at a time.
    public synchronized static CountriesDBHelper getInstance( Context context ) {
        // check if the instance already exists and if not, create the instance
        if( helperInstance == null ) {
            helperInstance = new CountriesDBHelper( context.getApplicationContext() );
        }
        return helperInstance;
    }

    // We must override onCreate method, which will be used to create the database if
    // it does not exist yet.
    @Override
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL( CREATE_JOBLEADS );
        Log.d( DEBUG_TAG, "Table " + TABLE_COUNTRIESINFO + " created" );
    }

    // We should override onUpgrade method, which will be used to upgrade the database if
    // its version (DB_VERSION) has changed.  This will be done automatically by Android
    // if the version will be bumped up, as we modify the database schema.
    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL( "drop table if exists " + TABLE_COUNTRIESINFO );
        onCreate( db );
        Log.d( DEBUG_TAG, "Table " + TABLE_COUNTRIESINFO + " upgraded" );
    }
}
