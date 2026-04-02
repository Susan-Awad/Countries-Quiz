package edu.uga.cs.countriesquiz;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Question implements Serializable {

    private static final String DEBUG_TAG = "Question";
    private long id;
    private String country;
    private String correctCapital;
    private List<String> wrongCapitals;
    private int selectedIndex;
    private int correctIndex;
    private List<String> answerChoices;

    // default constructor
    public Question() {
        this.id = -1;
        this.country = null;
        this.correctCapital = null;
        this.wrongCapitals = null;
        this.selectedIndex = -1;
        this.answerChoices = new ArrayList<>();
        this.correctIndex = -1;

    }

    public Question(String country, String correctCapital, List<String> wrongCapitals, int selectedIndex) {
        this.id = -1;
        this.country = country;
        this.correctCapital = correctCapital;
        this.wrongCapitals = wrongCapitals;
        this.selectedIndex = selectedIndex;
        this.answerChoices = new ArrayList<>();
        this.answerChoices.add(correctCapital);
        if (wrongCapitals != null) {
            this.answerChoices.addAll(wrongCapitals);
        }
        Collections.shuffle(answerChoices);
        this.correctIndex = this.answerChoices.indexOf(correctCapital);
    }

    // Method to create 6 different questions with 2 incorrect answers
    public List<Question> makeQuestions(List<Country> countries) {
        List<Question> questions = new ArrayList<>();
        List<Country> selected_Countries = new ArrayList<>();

        // selects 6 random countries
        for (int i = 0; i < 6; i++) {
            int index = ThreadLocalRandom.current().nextInt(197);
            selected_Countries.add(countries.get(index));
        }

        // assigns the attributes of capital, country, and wrong capitals
        for (int i = 0; i < 6; i++) {
            String country = selected_Countries.get(i).getCountry();
            String capital = selected_Countries.get(i).getCapital();
            List<String> incorrect = new ArrayList<>();

            // ensures the wrong answers are random and do not duplicate
            int index1 = ThreadLocalRandom.current().nextInt(197);
            int index2 = ThreadLocalRandom.current().nextInt(197);
            while ((countries.get(index1) == selected_Countries.get(i)) || (countries.get(index2) == selected_Countries.get(i))) {
                index1 = ThreadLocalRandom.current().nextInt(197);
                index2 = ThreadLocalRandom.current().nextInt(197);
            }

            // adds incorrect answers to a list
            incorrect.add(countries.get(index1).getCapital());
            incorrect.add(countries.get(index2).getCapital());

            // create the question and add it to the list
            Question question = new Question(country, capital, incorrect, -1);
            question.setId(i);
            questions.add(question);

            Log.d(DEBUG_TAG, "Question: Question made = " + question.toString());
        }

        return questions;
    }

    public long getId() {
        return id;
    }

    public long setId(long id) {
        return this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public String getCorrectCapital() {
        return correctCapital;
    }

    public List<String> getWrongCapitals() {
        return wrongCapitals;
    }


    public String toString() {
        return id + ": " + country + " " + correctCapital + " " + wrongCapitals.get(0) + " " + wrongCapitals.get(1);
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
    public int getSelectedIndex() {
        return selectedIndex;
    }
    public int getCorrectIndex() {
        return correctIndex;
    }
    public List<String> getAnswerChoices() {
        return answerChoices;
    }
    public boolean isCorrect() {
        return selectedIndex == correctIndex;
    }
}
