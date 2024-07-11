package com.example.fexifit4;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Locale;

public class CyclingFragment extends Fragment {

    private Chronometer chronometer;
    private Button startButton;
    private Button stopButton;
    private TextView caloriesBurnedText;
    private boolean running;
    private long pauseOffset;

    public CyclingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cycling, container, false);

        chronometer = view.findViewById(R.id.chronometer);
        startButton = view.findViewById(R.id.start_button);
        stopButton = view.findViewById(R.id.stop_button);
        caloriesBurnedText = view.findViewById(R.id.calories_burned_text);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        return view;
    }

    public void startTimer() {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
    }

    public void stopTimer() {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
            startButton.setEnabled(true);
            stopButton.setEnabled(false);

            // Calculate calories burned based on cycling time (example calculation)
            long cyclingTimeInMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            double caloriesBurned = calculateCaloriesBurned(cyclingTimeInMillis);
            caloriesBurnedText.setText(String.format(Locale.getDefault(), "Calories Burned: %.2f kcal", caloriesBurned));
        }
    }

    private double calculateCaloriesBurned(long cyclingTimeInMillis) {
        // Example calculation: 75 calories burned per 10 minutes of cycling
        double caloriesPerMinute = 7.5; // Adjust this value based on your actual calorie calculation formula
        double minutes = cyclingTimeInMillis / 60000.0;
        return minutes * caloriesPerMinute;
    }
}
