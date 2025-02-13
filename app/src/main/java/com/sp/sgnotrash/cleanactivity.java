package com.sp.sgnotrash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class cleanactivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 100;
    private ImageView reportImageView;
    private TextView reportnameTextView;
    private TextView reportdescTextView;
    private ImageView cleanImageView;
    private EditText commentEditText;
    private Button photoButton;
    private Button deletePhotoButton;
    private Button submitButton;
    private ImageButton backButton;
    private Bitmap currentImage;
    private String reportId; // Moved to class-level variable

    private int volleyResponseStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleanactivity);

        Intent intent = getIntent();
        reportId = intent.getStringExtra("ID"); // Initialize class-level variable
        String imageBase64 = intent.getStringExtra("image");
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");

        initializeViews();
        populateData(imageBase64, name, description);
        setupButtonListeners();
    }

    private void initializeViews() {
        reportImageView = findViewById(R.id.ReportImage);
        reportnameTextView = findViewById(R.id.name);
        reportdescTextView = findViewById(R.id.description);
        cleanImageView = findViewById(R.id.cleanpicture);
        commentEditText = findViewById(R.id.comments);
        photoButton = findViewById(R.id.photo_button1);
        deletePhotoButton = findViewById(R.id.delete_button2);
        submitButton = findViewById(R.id.submit_button1);
        backButton = findViewById(R.id.backButton1);
    }

    private void populateData(String imageBase64, String name, String description) {
        reportnameTextView.setText("Report Made By: " + name);
        reportdescTextView.setText("Report Description: " + description);

        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            reportImageView.setImageBitmap(bitmap);
        }
    }

    private void setupButtonListeners() {
        photoButton.setOnClickListener(v -> handleCameraPermission());
        deletePhotoButton.setOnClickListener(v -> clearCurrentImage());
        backButton.setOnClickListener(v -> finish());
        submitButton.setOnClickListener(v -> validateAndSubmit());
    }

    private void handleCameraPermission() {
        if (checkCameraPermission()) {
            openCamera();
        }
    }

    private void clearCurrentImage() {
        currentImage = null;
        cleanImageView.setImageResource(android.R.color.transparent);
    }

    private void validateAndSubmit() {
        String comment = commentEditText.getText().toString().trim();

        if (currentImage == null) {
            Toast.makeText(this, "Cleaning photo is required", Toast.LENGTH_SHORT).show();
            return;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        currentImage.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        String imageBase64 = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

        Toast.makeText(this, "Cleaned: " + comment, Toast.LENGTH_SHORT).show();

        if (reportId != null && !reportId.isEmpty()) {
            deleteVolley(reportId);
            awardPointsToUser();
        } else {
            Log.e("CleanActivity", "Report ID missing");
        }
        //Intent intent = new Intent (this, ReportActivity.class);
        //startActivity(intent);
        finish();

    }

    private void deleteVolley(String id) {
        // Rest api link
        String url = ReportVolleyHelper.url + id; // Delete by id
        RequestQueue queue = Volley.newRequestQueue(this);
        // Use DELETE REST api call
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Response status : " + volleyResponseStatus, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(cleanactivity.this, ReportActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e("OnErrorResponse", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return ReportVolleyHelper.getHeaders();
            }
        };
        // add JsonObjectRequest to the RequestQueue
        queue.add(jsonObjectRequest);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST);
            return false;
        }
        return true;
    }
    private void awardPointsToUser() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "Hello");
        String password = prefs.getString("password", "123");

        // Step 1: Fetch the user's document ID using their username
        String queryUrl = ReportVolleyHelper.loginurl +"?where={\"username\": {\"$eq\":[\"" + username + "\"]}, " +
                "\"password\": {\"$eq\":[\"" + password + "\"]}}";
        JsonObjectRequest queryRequest = new JsonObjectRequest(
                Request.Method.GET, queryUrl, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        if (data.length() > 0) {
                            JSONObject user = data.getJSONObject(0);
                            int currentPoints = user.getInt("point");
                            int newPoints = currentPoints + 1;

                            // Step 2: Update points using the document ID
                            String updateUrl = ReportVolleyHelper.loginurl + "/" + user;
                            JSONObject body = new JSONObject();
                            body.put("point", newPoints);

                            JsonObjectRequest updateRequest = new JsonObjectRequest(
                                    Request.Method.PATCH, updateUrl, body,
                                    updateResponse -> {
                                        // Update local preferences and UI
                                        prefs.edit().putInt("points", newPoints).apply();
                                        Toast.makeText(this, "+1 Point!", Toast.LENGTH_SHORT).show();
                                    },
                                    error -> Log.e("UpdateError", error.toString())
                            ) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    return ReportVolleyHelper.getHeaders();
                                }
                            };

                            Volley.newRequestQueue(this).add(updateRequest);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("QueryError", error.toString())
        );

        Volley.newRequestQueue(this).add(queryRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            currentImage = (Bitmap) data.getExtras().get("data");
            cleanImageView.setImageBitmap(currentImage);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}