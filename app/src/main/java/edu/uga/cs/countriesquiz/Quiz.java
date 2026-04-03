package edu.uga.cs.countriesquiz;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Quiz implements Serializable {
    private static final String DEBUG_TAG = "Quiz";
    private long id;
    private LocalDate date;
    private int score;
    private int num_answered;
    private List<Question> questions;

    // default constructor
    public Quiz() {
        this.id = -1;
        this.date = null;
        this.score = 0;
        this.num_answered = 0;
        this.questions = null;
    }

    @SuppressLint("NewApi")
    public Quiz(Date date, int score, int num_answered) {
        this.id = -1;
        this.date = LocalDate.now();
        this.score = score;
        this.num_answered = num_answered;
    }

    // method to create the quiz and assign the quiz questions with the current date
    @SuppressLint("NewApi")
    public Quiz makeQuiz(List<Country> countries) {
        Quiz quiz = new Quiz();
        Question q = new Question();

        // calls makeQuestions() to assign 6 random questions to the quiz
        List<Question> questions = q.makeQuestions(countries);

        // sets the questions to the quiz and assigns the current date to the quiz
        quiz.setQuestions(questions);
        quiz.setDate(LocalDate.now());
        Log.d(DEBUG_TAG, "Quiz: Quiz " + quiz.toString() + " with questions: " + questions.toString());

        return quiz;
    }

    public void correct() {
        this.score += 1;
    }

    public void answered() {
        this.num_answered += 1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public int getScore() {
        return score;
    }

    public int getNumAnswered() {
        return num_answered;
    }

    public List<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String toString() {
        return "Quiz " + id + "\n" +
                "Taken on: " + date + "\n" +
                "Result: " + score + " out of 6\n";
    }
}
