package edu.uga.cs.countriesquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "MainActivity";
    private CountryData data1;
    private QuizData data2;
    private Button startQuiz;
    private Button prevQuizzes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startQuiz = findViewById(R.id.button2);
        prevQuizzes = findViewById(R.id.button);

        try {
            data2 = new QuizData(this);
            data1 = new CountryData(this);
            data1.open();
            InputStream r = getAssets().open("countries_data.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(r));
            String[] nextRow;


            if (data1.getCountries().isEmpty()) {
                while ((nextRow = reader.readNext()) != null) {
                    if (nextRow.length == 4) {
                        String country = nextRow[0];
                        String capital = nextRow[1];
                        String continent = nextRow[2];
                        String abbreviation = nextRow[3];

                        data1.storeCountries(new Country(country, capital, continent, abbreviation));
                    }
                }
            }

            data1.close();
            reader.close();
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "MainActivity: Exception reading = " + e);
        }

        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Quiz quiz = new Quiz();
                data1.open();
                data2.open();
                quiz = quiz.makeQuiz(data1.getCountries());
                data2.storeQuizzes(quiz);
                data2.close();
                data1.close();

                List<Question> questions = quiz.getQuestions();
                for (Question q : questions) {
                    Log.d(DEBUG_TAG, "What is the capital of " + q.getCountry() + "?\n" +
                            "A) " + q.getWrongCapitals().get(0) + "\n" +
                            "B) " + q.getCorrectCapital() + "\n" +
                            "C) " + q.getWrongCapitals().get(1));
                }
                if (v.getId() == R.id.button2) {
                    Intent intent = new Intent(v.getContext(),
                            QuizActivity.class);
                    startActivity(intent);
                }
            }
        });

        prevQuizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data2.open();
                data2.getQuizzes();
                data2.close();
                if (v.getId() == R.id.button) {
                    Intent intent = new Intent(v.getContext(),
                            PastResultsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}