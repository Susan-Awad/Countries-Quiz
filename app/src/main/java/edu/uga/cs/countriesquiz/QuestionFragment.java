package edu.uga.cs.countriesquiz;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.List;

public class QuestionFragment extends Fragment {

    private static final String DEBUG_TAG = "QuestionFragment";
    private static final String ARG_QUESTION = "question";
    private static final String ARG_POSITION = "position";


    private int questionIndex;
    private AnswerListener answerListener;
    private Question question;

    public interface AnswerListener {
        void onAnswerSelected(int questionIndex, int selectedIndex);
    } //AnswerListener

    // required empty constructor
    public QuestionFragment() {

    }

    public static QuestionFragment newInstance(int position, Question question) {
        QuestionFragment questionFragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putSerializable(ARG_QUESTION, (Serializable) question);
        questionFragment.setArguments(args);
        return questionFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AnswerListener) {
            answerListener = (AnswerListener) context;
            Log.d(DEBUG_TAG, "onAttach: AnswerListener attach success");
        } else {
            throw new RuntimeException(context.toString());
        } // else
    } //onAttach

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "onCreateView: inflating questions_fragment layout");
        return inflater.inflate(R.layout.questions_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            questionIndex = getArguments().getInt(ARG_POSITION);
            question = (Question) getArguments().getSerializable(ARG_QUESTION);
            Log.d(DEBUG_TAG, "Index " + questionIndex + " for country " + question.getCountry() + " loaded");
        } //if

        // get all views from layout
        TextView questionNumberText = view.findViewById(R.id.textView3);
        TextView questionText = view.findViewById(R.id.textView4);
        TextView scoreText = view.findViewById(R.id.textView6);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        RadioButton answer1Button = view.findViewById(R.id.radioButton);
        RadioButton answer2Button = view.findViewById(R.id.radioButton2);
        RadioButton answer3Button = view.findViewById(R.id.radioButton3);

        // set texts for views
        questionNumberText.setText("Question " + (questionIndex + 1) + " of 6");
        questionText.setText("Name the capital city of " + question.getCountry());

        // get answer choices
        List<String> answerChoices = question.getAnswerChoices();

        // put answer choices into the radio buttons
        answer1Button.setText(answerChoices.get(0));
        answer2Button.setText(answerChoices.get(1));
        answer3Button.setText(answerChoices.get(2));

        Log.d(DEBUG_TAG, "Answer choices load: " + answerChoices);

        // if an answer is selected before, restore the answer choice
        if (question.getSelectedIndex() != -1) {
            if (question.getSelectedIndex() == 0) {
                answer1Button.setChecked(true);
            } else if (question.getSelectedIndex() == 1) {
                answer2Button.setChecked(true);
            } else if (question.getSelectedIndex() == 2) {
                answer3Button.setChecked(true);
            } //else-if
            Log.d(DEBUG_TAG, "onViewCreated: Restored prev selected index: " + question.getSelectedIndex());
        } //if

        // check listener for when user selects a radio button
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedIndex = -1;

            // convert checked radio button id to answer index
            if (checkedId == R.id.radioButton) {
                selectedIndex = 0;
            } else if (checkedId == R.id.radioButton2) {
                selectedIndex = 1;
            } else if(checkedId == R.id.radioButton3) {
                selectedIndex = 2;
            } //else-if

            // save selected answer choice in Question object
            question.setSelectedIndex(selectedIndex);
            Log.d(DEBUG_TAG, "onViewCreated: answer index " + selectedIndex + " for question " + questionIndex + " was selected");

            if (answerListener != null) {
                answerListener.onAnswerSelected(questionIndex, selectedIndex);
                Log.d(DEBUG_TAG, "onViewCreated: AnswerListner notified for question " + questionIndex);
            } //if
        });
    } //onViewCreated

    @Override
    public void onDetach() {
        super.onDetach();
        answerListener = null;
    }
}
