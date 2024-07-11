package com.example.fexifit4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProgressFragment extends Fragment {

    private TextView textViewInitialWeight;
    private TextView textViewCurrentWeight;
    private TextView textViewInitialHeight;
    private TextView textViewInitialBmi;
    private TextView textViewCurrentBmi;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        // Initialize TextViews
        textViewInitialWeight = view.findViewById(R.id.initialWeightTextView);
        textViewCurrentWeight = view.findViewById(R.id.currentWeightTextView);
        textViewInitialHeight = view.findViewById(R.id.initialHeightTextView);
        textViewInitialBmi = view.findViewById(R.id.initialBmiTextView);
        textViewCurrentBmi = view.findViewById(R.id.currentBmiTextView);

        // Fetch user progress data
        fetchUserProgress();

        return view;
    }

    private void fetchUserProgress() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch initial height and weight from user profile
            mFirestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                // Get initial height and weight
                                String initialHeightStr = documentSnapshot.getString("height");
                                String initialWeightStr = documentSnapshot.getString("weight");

                                if (initialHeightStr != null && !initialHeightStr.isEmpty() && initialWeightStr != null && !initialWeightStr.isEmpty()) {
                                    double initialHeight = Double.parseDouble(initialHeightStr);
                                    double initialWeight = Double.parseDouble(initialWeightStr);

                                    // Display initial height and weight
                                    textViewInitialHeight.setText("Initial Height: " + initialHeight + " cm");
                                    textViewInitialWeight.setText("Initial Weight: " + initialWeight + " kg");

                                    // Calculate and display initial BMI
                                    double initialBmi = calculateBmi(initialWeight, initialHeight);
                                    textViewInitialBmi.setText("Initial BMI: " + initialBmi);

                                    // Fetch current weight and calculate BMI
                                    fetchCurrentWeightAndCalculateBMI(userId, initialHeight);
                                } else {
                                    Toast.makeText(getContext(), "Initial height or weight not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "User profile not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to fetch user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void fetchCurrentWeightAndCalculateBMI(String userId, double initialHeight) {
        mFirestore.collection("progress").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String currentWeightStr = documentSnapshot.getString("weight");

                            if (currentWeightStr != null && !currentWeightStr.isEmpty()) {
                                double currentWeight = Double.parseDouble(currentWeightStr);

                                // Display current weight
                                textViewCurrentWeight.setText("Current Weight: " + currentWeight + " kg");

                                // Calculate and display current BMI
                                double currentBmi = calculateBmi(currentWeight, initialHeight);
                                textViewCurrentBmi.setText("Current BMI: " + currentBmi);
                            } else {
                                Toast.makeText(getContext(), "Current weight not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "No progress data found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to fetch progress data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
    }

    private double calculateBmi(double weight, double height) {
        return weight / ((height / 100) * (height / 100));
    }
}
