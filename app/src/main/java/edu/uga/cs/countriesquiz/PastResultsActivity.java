package edu.uga.cs.countriesquiz;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PastResultsActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_results);

        listView = findViewById(R.id.listView);

        new PastResultsDBReader(this).execute();
    }

    public static class PastResultsDBReader extends AsyncTask<Void, List<Quiz>> {

        private static final String DEBUG_TAG = "PastResultsDBReader";
        private QuizData quizData;
        public PastResultsDBReader(Context context) {
            quizData = new QuizData(context);
        }

        @Override
        protected List<Quiz> doInBackground(Void... params) {
            List<Quiz> quizzesList = new ArrayList<>();
            quizData.open();
            quizzesList = quizData.getQuizzes();
            quizData.close();
            return quizzesList;
        }

        @Override
        protected void onPostExecute(List<Quiz> quizzes) {
            Log.d(DEBUG_TAG, "Quiz saved: " + quizzes);
        }
    }
}
