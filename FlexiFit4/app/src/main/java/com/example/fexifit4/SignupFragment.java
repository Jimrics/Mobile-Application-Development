package com.example.fexifit4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {

    private EditText heightEditText;
    private EditText weightEditText;
    private EditText ageEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView bmiTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get references to the input fields and TextView
        heightEditText = view.findViewById(R.id.heightEditText);
        weightEditText = view.findViewById(R.id.weightEditText);
        ageEditText = view.findViewById(R.id.ageEditText);
        emailEditText = view.findViewById(R.id.EmailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        bmiTextView = view.findViewById(R.id.bmiTextView);

        // Get references to the buttons
        Button calculateBmiButton = view.findViewById(R.id.calculateBmiButton);
        Button signupButton = view.findViewById(R.id.signupButton);

        // Set onClickListener to the Calculate BMI button
        calculateBmiButton.setOnClickListener(v -> calculateAndDisplayBMI());

        // Set onClickListener to the Sign Up button
        signupButton.setOnClickListener(v -> createAccount());

        return view;
    }

    // Method to calculate and display BMI
    private void calculateAndDisplayBMI() {
        String heightStr = heightEditText.getText().toString();
        String weightStr = weightEditText.getText().toString();

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter height and weight", Toast.LENGTH_SHORT).show();
            return;
        }

        double height = Double.parseDouble(heightStr) / 100; // Convert cm to meters
        double weight = Double.parseDouble(weightStr);

        double bmi = weight / (height * height);
        bmiTextView.setText(String.format("Your BMI: %.2f", bmi));
        bmiTextView.setVisibility(View.VISIBLE);
    }

    // Method to create a new account
    private void createAccount() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String heightStr = heightEditText.getText().toString();
        String weightStr = weightEditText.getText().toString();
        String ageStr = ageEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new account using Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, save user details to Firestore
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserDetails(user, heightStr, weightStr, ageStr, email);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to save user details to Firestore
    private void saveUserDetails(FirebaseUser user, String height, String weight, String age, String email) {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("height", height);
        userDetails.put("weight", weight);
        userDetails.put("age", age);
        userDetails.put("email", email);

        db.collection("users").document(user.getUid())
                .set(userDetails)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                    // Navigate to LoginFragment
                    Fragment loginFragment = new LoginFragment();
                    loadFragmentWithAnimation(loginFragment);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error saving user details", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to load a fragment with a custom transition animation
    private void loadFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
