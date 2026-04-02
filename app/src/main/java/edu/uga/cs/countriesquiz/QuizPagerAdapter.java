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

    public QuizPagerAdapter (FragmentManager fragmentManager, Lifecycle lifecycle,List<Question> questions) {
        super(fragmentManager, lifecycle);
        this.questions = questions;
        Log.d(DEBUG_TAG, "QuizPagerAdapter created with " + questions.size() + " questions");
    }

    @Override
    public Fragment createFragment(int position) {
        return QuestionFragment.newInstance(position, questions.get(position));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
