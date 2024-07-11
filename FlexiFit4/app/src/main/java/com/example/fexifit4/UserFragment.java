package com.example.fexifit4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserFragment extends Fragment {

    private EditText editTextWeight;
    private EditText editTextRunningSpeed;
    private EditText editTextFeedback;
    private Button buttonSave;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public UserFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Initialize views
        editTextWeight = view.findViewById(R.id.weightEditText);
        editTextRunningSpeed = view.findViewById(R.id.speedEditText);
        editTextFeedback = view.findViewById(R.id.feedbackEditText);
        buttonSave = view.findViewById(R.id.saveButton);

        // Button click listener to save data
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProgress();
            }
        });

        return view;
    }

    private void saveProgress() {
        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Get input values
            String weight = editTextWeight.getText().toString().trim();
            String runningSpeed = editTextRunningSpeed.getText().toString().trim();
            String feedback = editTextFeedback.getText().toString().trim();

            // Validate inputs
            if (weight.isEmpty()) {
                editTextWeight.setError("Weight is required");
                editTextWeight.requestFocus();
                return;
            }

            if (runningSpeed.isEmpty()) {
                editTextRunningSpeed.setError("Running speed is required");
                editTextRunningSpeed.requestFocus();
                return;
            }

            // Create Progress object
            Map<String, Object> progress = new HashMap<>();
            progress.put("weight", weight);
            progress.put("runningSpeed", runningSpeed);
            progress.put("feedback", feedback);
            progress.put("email", currentUser.getEmail());

            // Save to Firebase Firestore under "progress" collection
            mFirestore.collection("progress").document(userId).set(progress)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Progress saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to save progress", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });
        }
    }
}
