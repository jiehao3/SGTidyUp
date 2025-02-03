package com.sp.sgnotrash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class ReportForm extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    private static final int LOCATION_PERMISSION_REQUEST = 101;
    private ImageView imgPreview;
    private EditText edtName, edtDescription, edtLocation;
    private Bitmap currentImage;
    //private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form);

        // Initialize views
        imgPreview = findViewById(R.id.picture_placeholder);
        edtName = findViewById(R.id.enter_name);
        edtDescription = findViewById(R.id.enter_description);
        edtLocation = findViewById(R.id.enter_location);
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Button handlers
        setupButtons();
    }

    private void setupButtons() {
        // Get Location Button
        //findViewById(R.id.location_button).setOnClickListener(v -> getCurrentLocation());

        // Take Photo Button
        findViewById(R.id.photo_button).setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            }
        });

        // Delete Photo Button
        findViewById(R.id.delete_button).setOnClickListener(v -> {
            currentImage = null;
            imgPreview.setImageResource(android.R.color.transparent);
        });

        // Back Button
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        // Submit Button
        findViewById(R.id.submit_button).setOnClickListener(v -> validateAndSubmit());
    }

    /*private void getCurrentLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            edtLocation.setText(String.format("%s, %s",
                                    location.getLatitude(),
                                    location.getLongitude()));
                        }
                    });
        }
    }

     */

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void validateAndSubmit() {
        String description = edtDescription.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();

        if (description.isEmpty()) {
            edtDescription.setError("Description is required");
            return;
        }

        if (location.isEmpty()) {
            edtLocation.setError("Location is required");
            return;
        }

        if (currentImage == null) {
            Toast.makeText(this, "Photo is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Handle form submission
        Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Permission handling
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return false;
        }
        return true;
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            currentImage = (Bitmap) data.getExtras().get("data");
            imgPreview.setImageBitmap(currentImage);
        }
    }


}