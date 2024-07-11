package com.example.fexifit4;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView textViewEmail;
    private TextView textViewInitialWeight;
    private TextView textViewCurrentWeight;
    private TextView textViewInitialBmi;
    private TextView textViewCurrentBmi;
    private Button buttonDownload;

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private boolean isPermissionGranted = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize TextViews and Button
        textViewEmail = view.findViewById(R.id.emailTextView);
        textViewInitialWeight = view.findViewById(R.id.initialWeightTextView);
        textViewCurrentWeight = view.findViewById(R.id.currentWeightTextView);
        textViewInitialBmi = view.findViewById(R.id.initialBmiTextView);
        textViewCurrentBmi = view.findViewById(R.id.currentBmiTextView);
        buttonDownload = view.findViewById(R.id.downloadPdfButton);

        // Set onClickListener for download button
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPermissionGranted) {
                    // Request the WRITE_EXTERNAL_STORAGE permission if not granted
                    requestStoragePermission();
                } else {
                    // Permission already granted, proceed with PDF generation and saving
                    generateAndSavePdf();
                }
            }
        });

        // Fetch user profile data
        fetchUserProfile();

        return view;
    }

    private void fetchUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String userEmail = currentUser.getEmail();
            textViewEmail.setText("Email: " + userEmail);

            // Fetch initial weight and BMI from Firestore
            mFirestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String initialWeightStr = documentSnapshot.getString("weight");
                            String initialHeightStr = documentSnapshot.getString("height");
                            double initialHeight = Double.parseDouble(initialHeightStr);
                            double initialWeight = Double.parseDouble(initialWeightStr);

                            // Calculate initial BMI
                            double initialHeightInMeters = initialHeight / 100.0;
                            double initialBmi = initialWeight / (initialHeightInMeters * initialHeightInMeters);

                            // Update TextViews
                            textViewInitialWeight.setText("Initial Weight: " + initialWeightStr);
                            textViewInitialBmi.setText("Initial BMI: " + String.format("%.2f", initialBmi));
                        } else {
                            Toast.makeText(getContext(), "No initial data found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to fetch initial data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });

            // Fetch current weight and BMI from Firestore
            mFirestore.collection("progress").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String currentWeightStr = documentSnapshot.getString("weight");
                            double currentWeight = Double.parseDouble(currentWeightStr);

                            // Fetch initial height from user profile again
                            mFirestore.collection("users").document(userId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot1 -> {
                                        if (documentSnapshot1.exists()) {
                                            String initialHeightStr = documentSnapshot1.getString("height");
                                            double initialHeight = Double.parseDouble(initialHeightStr);

                                            // Calculate current BMI
                                            double initialHeightInMeters = initialHeight / 100.0;
                                            double currentBmi = currentWeight / (initialHeightInMeters * initialHeightInMeters);

                                            // Update TextViews
                                            textViewCurrentWeight.setText("Current Weight: " + currentWeightStr);
                                            textViewCurrentBmi.setText("Current BMI: " + String.format("%.2f", currentBmi));
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Failed to fetch height data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    });
                        } else {
                            Toast.makeText(getContext(), "No progress data found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to fetch progress data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    private void generateAndSavePdf() {
        // Example method to generate a simple PDF and save it to external storage
        Document document = new Document();

        try {
            // Create a file name for the PDF
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Progress_Report_" + timeStamp + ".pdf";

            // Use MediaStore API to save the PDF to Downloads directory
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");

            // For Android 10+ (API 29+), handle scoped storage
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            }

            // Get the content resolver
            ContentResolver contentResolver = requireContext().getContentResolver();

            // Insert the PDF content into MediaStore
            Uri uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

            if (uri != null) {
                OutputStream outputStream = contentResolver.openOutputStream(uri);
                if (outputStream != null) {
                    // Write your PDF content here
                    PdfWriter.getInstance(document, outputStream);
                    document.open();
                    document.add(new Paragraph("Progress Report"));
                    document.add(new Paragraph("Email: " + textViewEmail.getText().toString()));
                    document.add(new Paragraph("Initial Weight: " + textViewInitialWeight.getText().toString()));
                    document.add(new Paragraph("Current Weight: " + textViewCurrentWeight.getText().toString()));
                    document.add(new Paragraph("Initial BMI: " + textViewInitialBmi.getText().toString()));
                    document.add(new Paragraph("Current BMI: " + textViewCurrentBmi.getText().toString()));
                    document.close();
                    Toast.makeText(requireContext(), "PDF saved to Downloads folder", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Failed to create PDF file in Downloads", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, generate and save PDF
                isPermissionGranted = true;
                generateAndSavePdf();
            } else {
                // Permission denied, show a message or handle it gracefully
                Toast.makeText(requireContext(), "Permission denied. Cannot save PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
