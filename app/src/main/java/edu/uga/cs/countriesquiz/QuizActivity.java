package edu.uga.cs.countriesquiz;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

public class QuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewPager2 pager = findViewById( R.id.viewpager );
        QuizPagerAdapter avpAdapter = new
                QuizPagerAdapter(
                getSupportFragmentManager(), getLifecycle() );
        pager.setOrientation(
                ViewPager2.ORIENTATION_HORIZONTAL );
        pager.setAdapter( avpAdapter );

        Quiz quiz = new Quiz();
        new QuizActivity.QuizDBWriter(this).execute(quiz);
    }
    public static class QuizDBWriter extends AsyncTask<Quiz, Quiz> {

        private static final String DEBUG_TAG = "QuizDBWriter";
        private CountryData countryData;
        private QuizData quizData;
        public QuizDBWriter(Context context) {
            countryData = new CountryData(context);
            quizData = new QuizData(context);
        }

        @Override
        protected Quiz doInBackground(Quiz... quizzes) {
            countryData.open();
            quizData.open();

            Quiz quiz = quizzes[0];
            quiz = quiz.makeQuiz(countryData.getCountries());
            quizData.storeQuizzes(quiz);

            quizData.close();
            countryData.close();
            return quiz;
        }

        @Override
        protected void onPostExecute(Quiz quiz) {
            Log.d(DEBUG_TAG, "Quiz saved: " + quiz);
        }
    }
}
