package edu.uga.cs.countriesquiz;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements QuestionFragment.AnswerListener{

    public static final String DEBUG_TAG = "QuizActivity";
    private static final String CURRENT_PAGE = "currentPage";
    private static final String PREV_PAGE = "previousPage";
    private static final String CURRENT_QUIZ = "currentQuiz";
    private static final String QUESTION_SCORES = "questionsScores";
    private static final String IS_QUESTION_ANSWERED = "isQuestionAnswered";
    private static final String IS_QUIZ_SAVED = "isQuizSaved";

    private CountryData countryData;
    private QuizData quizData;
    private Quiz currentQuiz;
    private ViewPager2 viewPager;
    private QuizPagerAdapter quizPagerAdapter;
    private List<Question> questions;
    private boolean isQuizSaved;
    private int[] questionScores;
    private boolean[] isQuestionAnswered;
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

        // save if there is a disruption
        if (savedInstanceState != null) {
            currentQuiz = (Quiz) savedInstanceState.getSerializable(CURRENT_QUIZ);
            prevPage = savedInstanceState.getInt(PREV_PAGE, 0);
            questionScores = savedInstanceState.getIntArray(QUESTION_SCORES);
            isQuestionAnswered = savedInstanceState.getBooleanArray(IS_QUESTION_ANSWERED);
            isQuizSaved = savedInstanceState.getBoolean(IS_QUIZ_SAVED, false);

            if (currentQuiz != null) {
                questions = currentQuiz.getQuestions();
            } //if

            if (questions == null) {
                questions = new ArrayList<>();
            }//if

            if (questionScores == null || questionScores.length != questions.size()) {
                questionScores = new int[questions.size()];
            } //if

            if (isQuestionAnswered == null || isQuestionAnswered.length != questions.size()) {
                isQuestionAnswered = new boolean[questions.size()];
            }//if

            setPager();
            int restoredPage = savedInstanceState.getInt(CURRENT_PAGE, 0);
            viewPager.setCurrentItem(restoredPage,false);
            prevPage = restoredPage;

            if (restoredPage == questions.size() && !isQuizSaved) {
                Log.d(DEBUG_TAG, "onCreate: saving quiz now");
                saveFinishedQuiz();
            } //if
            } else {
                // load quiz
                new QuizLoader().execute();
                Log.d(DEBUG_TAG, "setPager: restored prevPage = " + prevPage);
            } //else
    } //onCreate

   // set up pager and adapter
    private void setPager() {
        if (questions == null) {
            return;
        }
        quizPagerAdapter = new QuizPagerAdapter(getSupportFragmentManager(), getLifecycle(), questions, currentQuiz);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(quizPagerAdapter);
        viewPager.setVisibility(View.VISIBLE);
        Log.d(DEBUG_TAG, "setPager: ViewPager2 hasm been setup");
    } //setPager

    // This is an AsyncTask class (it extends AsyncTask) to perform DB writing, asynchronously.
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
        // in a background process has finished.
        // That object will be passed as argument to onPostExecute.
        // onPostExecute is like the notify method in an asynchronous method call discussed in class.
        @Override
        protected void onPostExecute(Quiz quiz) {
           if (quiz == null) {
               finish();
               return;
            } //if

            // save quiz and question list
            currentQuiz = quiz;
            questions = currentQuiz.getQuestions();
            Log.d(DEBUG_TAG, "QuizLoader.onPostExecute: questions generated = " + questions.size());

            isQuizSaved = false;
            questionScores = new int[6];
            isQuestionAnswered = new boolean[questions.size()];

            // pager and adapter set up
            quizPagerAdapter = new QuizPagerAdapter(getSupportFragmentManager(), getLifecycle(), questions, currentQuiz);
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
                    if (position > prevPage && prevPage < questions.size()) {
                        gradeQuestion(prevPage);
                    } //if

                    // disables swiping at results page
                    if (position == questions.size()) {
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + position);

                        if (fragment instanceof ResultsFragment) {
                            ((ResultsFragment) fragment).updateScore();
                        }//if

                        Log.d(DEBUG_TAG, "QuizLoader.onPostExecute.onPageSelected: results page, swiping OFF");
                        viewPager.setUserInputEnabled(false);
                    } else {
                        viewPager.setUserInputEnabled(true);
                    }
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
                currentQuiz = quiz;
                questions = currentQuiz.getQuestions();
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

        // save selected answer choice index
        Question question = questions.get(questionIndex);
        question.setSelectedIndex(selectedIndex);

        if (question.getSelectedIndex() == -1 ) {
            return;
        } //if

        // update question number that has been answered
        if(!isQuestionAnswered[questionIndex]) {
            isQuestionAnswered[questionIndex] = true;
            currentQuiz.answered();
        }//if

        if (question.isCorrect()) {
            questionScores[questionIndex] = 1;
        } else {
            questionScores[questionIndex] = 0;
        }

        Log.d(DEBUG_TAG, "onAnswerSelected: questionScore[" + questionIndex + "] = " + questionScores[questionIndex]);

        int currentScore = 0;

        for (int score : questionScores) {
            currentScore += score;
        }
        currentQuiz.setScore(currentScore);
        Log.d(DEBUG_TAG, "onAnswerSelected: current score = " + currentScore);
    } //onAnswerSelected

    // grade the question after the user swipes left
    private void gradeQuestion(int index) {
        if (questions == null || index >= questions.size() || index < 0) {
            Log.d(DEBUG_TAG, "gradeQuestion: invalid index: " + index);
            return;
        } //if

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

        if (!isQuizSaved) {
            new QuizWriter().execute(currentQuiz);
        } //if
    } //saveFinishedQuiz

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PREV_PAGE, prevPage);

        if (viewPager != null) {
            outState.putSerializable(CURRENT_QUIZ, currentQuiz);
            outState.putInt(CURRENT_PAGE, viewPager.getCurrentItem());
            outState.putInt(PREV_PAGE, prevPage);
            outState.putIntArray(QUESTION_SCORES, questionScores);
            outState.putBooleanArray(IS_QUESTION_ANSWERED, isQuestionAnswered);
            outState.putBoolean(IS_QUIZ_SAVED, isQuizSaved);

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
    } //onDestroy

    // retrieves the current quiz
    public Quiz getCurrentQuiz (){
        return currentQuiz;
    } //getCurrentQuiz

    // gets total number of questions
    public int getTotalQuestions() {
        return questions.size();
    } //getTotalQuestions

    // double check to see if quiz is saved or not
    public void checkQuizSaved() {
        Log.d(DEBUG_TAG, "ensureQuizSaved: called, isQuizSaved = " + isQuizSaved);

        if (!isQuizSaved && currentQuiz != null) {
            Log.d(DEBUG_TAG, "ensureQuizSaved: currentQuiz score = " + currentQuiz.getScore());
            saveFinishedQuiz();
        }
    }
}

