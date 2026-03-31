package edu.uga.cs.countriesquiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuizzesDBHelper extends SQLiteOpenHelper {

    private static final String DEBUG_TAG = "QuizzesDBHelper";

    private static final String DB_NAME = "quizresults.db";
    private static final int DB_VERSION = 1;

    // Define all names for table and column names.
    public static final String TABLE_QUIZZESINFO = "quizresults";
    public static final String QUIZZESINFO_COLUMN_ID = "_id";
    public static final String QUIZZESINFO_COLUMN_DATE = "date";
    public static final String QUIZZESINFO_COLUMN_RESULT = "result";
    public static final String QUIZZESINFO_COLUMN_NUMANSW = "num_answered";

    private static QuizzesDBHelper helperInstance;

    // A Create table SQL statement to create a table for countries information
    private static final String CREATE_QUIZZES =
            "create table " + TABLE_QUIZZESINFO + " ("
                    + QUIZZESINFO_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QUIZZESINFO_COLUMN_DATE + " TEXT, "
                    + QUIZZESINFO_COLUMN_RESULT + " INTEGER, "
                    + QUIZZESINFO_COLUMN_NUMANSW + " INTEGER "
                    + ")";

    private QuizzesDBHelper(Context context ) {
        super( context, DB_NAME, null, DB_VERSION );
    }

    public synchronized static QuizzesDBHelper getInstance( Context context ) {
        // check if the instance already exists and if not, create the instance
        if( helperInstance == null ) {
            helperInstance = new QuizzesDBHelper( context.getApplicationContext() );
        }
        return helperInstance;
    }

    // We must override onCreate method, which will be used to create the database if
    // it does not exist yet.
    @Override
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL( CREATE_QUIZZES );
        Log.d( DEBUG_TAG, "Table " + TABLE_QUIZZESINFO + " created" );
    }

    // We should override onUpgrade method, which will be used to upgrade the database if
    // its version (DB_VERSION) has changed.
    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL( "drop table if exists " + TABLE_QUIZZESINFO );
        onCreate( db );
        Log.d( DEBUG_TAG, "Table " + TABLE_QUIZZESINFO + " upgraded" );
    }
}

