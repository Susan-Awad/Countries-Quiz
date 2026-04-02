package edu.uga.cs.countriesquiz;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements QuestionFragment.AnswerListener{

    public static final String DEBUG_TAG = "QuizActivity";

    private static final String CURRENT_PAGE = "currentPage";
    private static final String PREV_PAGE = "previousPage";

    private CountryData countryData;
    private QuizData quizData;
    private Quiz currentQuiz;
    private ViewPager2 viewPager;
    private QuizPagerAdapter quizPagerAdapter;
    private List<Question> questions;
    private boolean isQuizSaved;
    private boolean[] isQuestionGraded;
    private int prevPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Log.d(DEBUG_TAG, "onCreate: QuizActivity Created");

        // find the ViewPager2
        viewPager = findViewById(R.id.viewpager);

        //db helpers
        countryData = new CountryData(this);
        quizData = new QuizData(this);

        // open db
        countryData.open();
        quizData.open();

        Log.d(DEBUG_TAG, "onCreate: CountryData isOpen: " + countryData.isDBOpen());
        Log.d(DEBUG_TAG, "onCreate: QuizData isOpen: " + quizData.isDBOpen());



        questions = new ArrayList<>();
        currentQuiz = null;
        isQuizSaved = false;

        if (savedInstanceState != null) {
            prevPage = savedInstanceState.getInt(PREV_PAGE, 0);
            Log.d(DEBUG_TAG, "onCreate: restored prevPage = " + prevPage);
        } else {
            prevPage = 0;
        } //else

        // load quiz
        new QuizLoader().execute();
    } //onCreate

    // This is an AsyncTask class (it extends AsyncTask) to perform DB writing of a job lead, asynchronously.
    private class QuizLoader extends AsyncTask<Void, Quiz> {

        @Override
        protected Quiz doInBackground(Void... arguments) {
            try {
                List<Country> countries = countryData.getCountries();
                Log.d(DEBUG_TAG, "QuizLoader.doInBackground: countries loaded: " + countries.size());
                
                // build new quiz
                Quiz quiz = new Quiz();
                quiz = quiz.makeQuiz(countries);
                Log.d(DEBUG_TAG, "QuizLoader.doInBackground: quiz load success");
                return quiz;
            } catch (Exception e) {
                Log.d(DEBUG_TAG, "QuizLoader.doInBackground: quiz load failure", e);
                return null;
            } //try-catch
        } //doInBackgorund

        // This method will be automatically called by Android once the writing to the database
        // in a background process has finished.  Note that doInBackground returns a JobLead object.
        // That object will be passed as argument to onPostExecute.
        // onPostExecute is like the notify method in an asynchronous method call discussed in class.
        @Override
        protected void onPostExecute(Quiz quiz) {
            if (quiz == null) {
                Toast.makeText(QuizActivity.this, "Could not load quiz.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            } //if

            // save quiz and question list
            currentQuiz = quiz;
            questions = currentQuiz.getQuestions();
            Log.d(DEBUG_TAG, "QuizLoader.onPostExecute: questions generated = " + questions.size());
            isQuestionGraded = new boolean[questions.size()];

            // pager and adapter set up
            quizPagerAdapter = new QuizPagerAdapter(getSupportFragmentManager(), getLifecycle(), questions);
            viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
            viewPager.setAdapter(quizPagerAdapter);
            Log.d(DEBUG_TAG, "QuizLoader.onPostExecute: ViewPager2 Adapter set up ");


            viewPager.setCurrentItem(prevPage, false);

            viewPager.setVisibility(ViewPager2.VISIBLE);

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    Log.d(DEBUG_TAG, "QuizLoader.onPostExecute.onPageSelected: moved to page " + position);

                    // grade question only after the user moves forward to next question
                    if (position > prevPage) {
                        gradeQuestion(prevPage);
                    } //if
                    prevPage = position;

                } //onPageSelected
            });
        } //onPostExcecute
    }

   // AsyncTAsk to save a finished quiz
    private class QuizWriter extends AsyncTask<Quiz, Quiz> {
        @Override
        protected Quiz doInBackground(Quiz... arguments) {
            try {
                Log.d(DEBUG_TAG, "QuizWriter.doInBackground: saving quiz with score: " + arguments[0].getScore());
                quizData.storeQuizzes(arguments[0]);
                return arguments[0];
            } catch (Exception e) {
                Log.d(DEBUG_TAG, "QuizWriter.doInBackground: quiz save failure. ", e);
                return null;
            } //try-catch
        } //doInBackground

        @Override
        protected void onPostExecute(Quiz quiz) {
            if (quiz != null) {
                isQuizSaved = true;
                Log.d(DEBUG_TAG, "QuizWriter.onPostExecute: quiz save success");
            } else {
                Log.d(DEBUG_TAG, "QuizWriter.onPostExecute: quiz save failed");
            } //else
        } //onPostExecute
    } //QuizWriter

    // Stores the selected answer choice inside Question
    @Override
    public void onAnswerSelected(int questionIndex, int selectedIndex) {
        if (questions == null || questions.size() <= questionIndex || questionIndex < 0) {
            Log.d(DEBUG_TAG, "onAnswerSelected: wrong question index: " + questionIndex);
            return;
        } //if
        questions.get(questionIndex).setSelectedIndex(selectedIndex);

    } //onAnswerSelected

    // grade the question after the user swipes left
    private void gradeQuestion(int index) {
        if (questions == null || index >= questions.size() || index < 0) {
            Log.d(DEBUG_TAG, "gradeQuestion: invalid index: " + index);
        } //if

        // if user swipes left and right
        if (isQuestionGraded[index]) {
            Log.d(DEBUG_TAG, "gradeQuestion: " + index + " already been graded");
            return;
        } //if
        isQuestionGraded[index] = true;
        Question question = questions.get(index);

        // update question number that has been answered
        currentQuiz.answered();
        Log.d(DEBUG_TAG, "gradeQuestion: " + currentQuiz.getNumAnswered() + "has been answered");

        if (question.isCorrect()) {
            currentQuiz.correct();
        } //if
        currentQuiz.answered();

        // save quiz once the last question is graded
        if (index == questions.size() - 1 && !isQuizSaved) {
            saveFinishedQuiz();
        } //if

    } //gradeQuestion

    // triggers async save for the whole quiz
    private void saveFinishedQuiz() {
        if (currentQuiz == null) {
            Log.d(DEBUG_TAG, "saveFinishedQuiz: quiz is null");
            return;
        } //if
        new QuizWriter().execute(currentQuiz);
    } //saveFinishedQuiz

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PREV_PAGE, prevPage);

        if (viewPager != null) {
            outState.putInt(CURRENT_PAGE, viewPager.getCurrentItem());
            Log.d(DEBUG_TAG, "onSaveInstanceState: current page = " + viewPager.getCurrentItem());
        } //if
        Log.d(DEBUG_TAG, "onSaveInstanceState: previous page = " + prevPage );

    } // onSaveInstanceState

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // close db connections if they exist
        if (countryData != null) {
            countryData.close();
        } //if
        if (quizData != null) {
            quizData.close();
        }
    }
}

