package edu.uga.cs.countriesquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ResultsFragment extends Fragment {

    private static final String DEBUG_TAG = "ResultsFragment";
   private TextView scoreText;

    // required constructor 
   public ResultsFragment() {
       
   }

    public static ResultsFragment newInstance() {
        return new ResultsFragment();
    } //newInstance

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "onCreateView: inflating results_fragment layout");
        return inflater.inflate(R.layout.results_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get all views
        scoreText = view.findViewById(R.id.textView8);
        Button newQuizButton = view.findViewById(R.id.button3);
        Button pastResultsButton = view.findViewById(R.id.button4);

        updateScore();
        QuizActivity activity = (QuizActivity) getActivity();
        if (activity != null) {
            activity.checkQuizSaved();
            Log.d(DEBUG_TAG, "onResume: quiz saved");
        }

        // start new quiz
        newQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizActivity.class);
            startActivity(intent);

            // close current activity
            if (getActivity() != null) {
                getActivity().finish();
            } //if
        });

        // open past results activity
        pastResultsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PastResultsActivity.class);
            startActivity(intent);
        });

    } //onViewCreated

    @Override
    public void onResume() {
        super.onResume();
        updateScore();
        QuizActivity activity = (QuizActivity) getActivity();
        if (activity != null) {
            activity.checkQuizSaved();
            Log.d(DEBUG_TAG, "onResume: quiz saved");
        }
   }
    public void updateScore() {
        QuizActivity activity = (QuizActivity) getActivity();

        if (activity != null && activity.getCurrentQuiz() != null) {
            int score = activity.getCurrentQuiz().getScore();
            int totalQuestions = activity.getTotalQuestions();
            Log.d(DEBUG_TAG, "onViewCreated: final score = " + score);
            Log.d(DEBUG_TAG, "onViewCreated: total questions = " + totalQuestions);

            // set score text
            scoreText.setText("Score: " + score + " / " + totalQuestions);
        } //if
    }
}
