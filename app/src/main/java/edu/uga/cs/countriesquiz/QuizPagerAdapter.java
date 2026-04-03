package edu.uga.cs.countriesquiz;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import java.util.List;


public class QuizPagerAdapter extends FragmentStateAdapter {

    private static final String DEBUG_TAG = "QuizPagerAdapter";
    private List<Question> questions;
    private Quiz currentQuiz;

    public QuizPagerAdapter (FragmentManager fragmentManager, Lifecycle lifecycle, List<Question> questions, Quiz currentQuiz) {
        super(fragmentManager, lifecycle);
        this.questions = questions;
        this.currentQuiz = currentQuiz;
        Log.d(DEBUG_TAG, "QuizPagerAdapter created with " + questions.size() + " questions");
    }

    @Override
    public Fragment createFragment(int position) {
        Log.d(DEBUG_TAG, "createFragment position = " + position);
        if (position < questions.size()) {
            return QuestionFragment.newInstance(position, questions.get(position));
        } //if

        // last results page
        return ResultsFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        Log.d(DEBUG_TAG, "getItemCount = " + (questions.size()));
        return questions.size() + 1;
    }
}
