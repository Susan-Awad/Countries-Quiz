package edu.uga.cs.countriesquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
        data1 = new CountryData(this);
        data2 = new QuizData(this);

        Country country = new Country();
        new CountryDBWriter(this).execute(country);

        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), QuizActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        prevQuizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data2.open();
                data2.getQuizzes();
                data2.close();
            }
        });
    }
}