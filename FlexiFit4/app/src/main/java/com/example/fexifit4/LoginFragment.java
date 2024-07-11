package com.example.fexifit4;

import android.content.Intent;
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

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get references to the email and password EditText fields
        EditText emailEditText = view.findViewById(R.id.input_email);
        EditText passwordEditText = view.findViewById(R.id.input_password);

        // Get reference to the login button
        Button loginButton = view.findViewById(R.id.login_button);

        // Get reference to the Sign Up TextView
        TextView signUpTextView = view.findViewById(R.id.Sign_Up);

        // Set onClickListener to the login button
        loginButton.setOnClickListener(v -> {
            // Get entered email and password
            String enteredEmail = emailEditText.getText().toString().trim();
            String enteredPassword = passwordEditText.getText().toString().trim();

            // Check if entered credentials are not empty
            if (!enteredEmail.isEmpty() && !enteredPassword.isEmpty()) {
                // Use Firebase Authentication to sign in
                mAuth.signInWithEmailAndPassword(enteredEmail, enteredPassword)
                        .addOnCompleteListener(getActivity(), task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(getActivity(), "Login successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                                startActivity(intent);
                                getActivity().finish(); // Finish MainActivity to prevent going back when pressing back button
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // If email or password is empty, show error message
                Toast.makeText(getActivity(), "Please enter email and password.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set onClickListener to the Sign Up TextView
        signUpTextView.setOnClickListener(v -> {
            // Navigate to SignupFragment with transition animation
            Fragment signupFragment = new SignupFragment();
            loadFragmentWithAnimation(signupFragment);
        });

        return view;
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
