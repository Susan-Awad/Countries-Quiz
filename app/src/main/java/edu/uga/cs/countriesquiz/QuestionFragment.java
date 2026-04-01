package edu.uga.cs.countriesquiz;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class QuestionFragment extends Fragment {
    private static final String DEBUG_TAG = "QuestionFragment";
    private static final String[] numOfQuestions = {
            "1",
            "2",
            "3",
            "4",
            "5",
            "6"
    };

    private int questionNum;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance( int versionNum ) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt( "questionNum", versionNum );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if( getArguments() != null ) {
            questionNum = getArguments().getInt( "questionNum" );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {
        return inflater.inflate(R.layout.questions_fragment, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        TextView header = view.findViewById( R.id.textView3 );
        TextView score = view.findViewById( R.id.textView6 );
        TextView question = view.findViewById( R.id.textView4 );

        RadioButton q1 = view.findViewById( R.id.radioButton );
        RadioButton q2 = view.findViewById( R.id.radioButton2 );
        RadioButton q3 = view.findViewById( R.id.radioButton3 );

        String text = "Question " + numOfQuestions[ questionNum ] + " of 6";
        header.setText( text );
    }

    public static int getNumberOfVersions() {
        return numOfQuestions.length;
    }
}
