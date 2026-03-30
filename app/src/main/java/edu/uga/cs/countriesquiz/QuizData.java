package edu.uga.cs.countriesquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class QuizData {
    public static final String DEBUG_TAG = "QuizData";

    // reference to db
    private SQLiteDatabase db;
    private SQLiteOpenHelper quizDBHelper;

    private static final String[] columns = {
            QuizzesDBHelper.QUIZZESINFO_COLUMN_ID,
            QuizzesDBHelper.QUIZZESINFO_COLUMN_DATE,
            QuizzesDBHelper.QUIZZESINFO_COLUMN_RESULT,
            QuizzesDBHelper.QUIZZESINFO_COLUMN_NUMANSW
    };

    public QuizData(Context context) {
        quizDBHelper = QuizzesDBHelper.getInstance(context);
    }

    public void open() {
        db = quizDBHelper.getWritableDatabase();
        Log.d(DEBUG_TAG, "QuizData: db open");
    }

    public void close() {
        if (quizDBHelper != null) {
            quizDBHelper.close();
            Log.d(DEBUG_TAG, "QuizData: db close");
        }
    }

    public boolean isDBOpen() {
        return db.isOpen();
    }

    // returns all the quizzes in List format
    public List<Quiz> getQuizzes() {
        ArrayList<Quiz> quizzes = new ArrayList<>();
        Cursor cursor = null;
        int colIndex;

        try {
            // executes the select query
            cursor = db.query(QuizzesDBHelper.TABLE_QUIZZESINFO, columns,
                    null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                while(cursor.moveToNext()) {
                    if (cursor.getColumnCount() >= 3) {

                        // gets all attributes
                        colIndex = cursor.getColumnIndex(QuizzesDBHelper.QUIZZESINFO_COLUMN_ID);
                        long id = cursor.getLong(colIndex);
                        colIndex = cursor.getColumnIndex(QuizzesDBHelper.QUIZZESINFO_COLUMN_DATE);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = formatter.parse(cursor.getString(colIndex));
                        colIndex = cursor.getColumnIndex(QuizzesDBHelper.QUIZZESINFO_COLUMN_RESULT);
                        int result = cursor.getInt(colIndex);
                        colIndex = cursor.getColumnIndex(QuizzesDBHelper.QUIZZESINFO_COLUMN_NUMANSW);
                        int num_answered = cursor.getInt(colIndex);

                        // creates new quiz and adds it to the list
                        Quiz quiz = new Quiz(date, result, num_answered);
                        quiz.setId(id);
                        quizzes.add(quiz);
                        Log.d(DEBUG_TAG, "QuizData: Retrieved quiz = " + quiz);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "QuizData: Exception caught = " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return quizzes;
    }

    // stores the quizzes in the db
    public void storeQuizzes(Quiz quiz) {
        // sets the column info to the variables given by the quiz
        ContentValues values = new ContentValues();
        values.put(QuizzesDBHelper.QUIZZESINFO_COLUMN_DATE, quiz.getDate().toString());
        values.put(QuizzesDBHelper.QUIZZESINFO_COLUMN_RESULT, quiz.getScore());
        values.put(QuizzesDBHelper.QUIZZESINFO_COLUMN_NUMANSW, quiz.getNumAnswered());

        // insert row into db
        long id = db.insert(QuizzesDBHelper.TABLE_QUIZZESINFO, null, values);

        // sets the id given from the db to our country variable
        quiz.setId(id);
        Log.d(DEBUG_TAG, "Stored new quiz in db with id: " + id);
    }
}
