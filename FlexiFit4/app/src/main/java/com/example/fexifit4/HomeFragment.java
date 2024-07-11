package com.example.fexifit4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize your Dashboard UI components here
        CardView cyclingCardView = view.findViewById(R.id.cycling2);
        cyclingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCyclingFragment();
            }
        });

        CardView runningCardView = view.findViewById(R.id.running);
        runningCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRunningFragment();
            }
        });

        CardView strengthTrainingCardView = view.findViewById(R.id.StrengthTraining);
        strengthTrainingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStrengthTrainingFragment();
            }
        });

        // Open UserFragment when User CardView is clicked
        CardView userCardView = view.findViewById(R.id.user);
        userCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserFragment();
            }
        });

        // Update to open GymFragment when Gym CardView is clicked
        CardView gymCardView = view.findViewById(R.id.gym);
        gymCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGymFragment();
            }
        });

        return view;
    }

    private void openCyclingFragment() {
        Fragment cyclingFragment = new CyclingFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, cyclingFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openRunningFragment() {
        Fragment runningFragment = new RunningFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, runningFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openStrengthTrainingFragment() {
        Fragment strengthFragment = new StrengthFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, strengthFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openUserFragment() {
        Fragment userFragment = new UserFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, userFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Method to open GymFragment
    private void openGymFragment() {
        Fragment gymFragment = new GymFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, gymFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
