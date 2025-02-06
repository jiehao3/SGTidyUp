package com.sp.sgnotrash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.datastax.astra.client.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;




public class ReportForm extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    private static final int LOCATION_PERMISSION_REQUEST = 101;
    private ImageView imgPreview;
    private EditText edtName, edtDescription, edtLocation;
    private Bitmap currentImage;
    private RequestQueue requestQueue;
    private String id = "";
    private GPSTracker gpsTracker;
    private double latitude = 0.0d;
    private double longitude = 0.0d;
    private double myLatitude = 0.0d;
    private double myLongitude = 0.0d;

    private Button getlocation;
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
        requestQueue = Volley.newRequestQueue(this);
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        id = getIntent().getStringExtra("ID");
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
        findViewById(R.id.location_button).setOnClickListener(v -> getlocation());
        // Submit Button
        findViewById(R.id.submit_button).setOnClickListener(v -> validateAndSubmit());
    }

    private void getlocation() {
        myLatitude = GPSTracker.getLatitude();
        myLongitude = GPSTracker.getLongitude();
        edtLocation.setText(String.format(Locale.getDefault(), "%.2f, %.2f", myLatitude, myLongitude));


    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void validateAndSubmit() {
        String nameStr = edtName.getText().toString().trim();
        String descriptionStr = edtDescription.getText().toString().trim();
        String lat = String.valueOf(myLatitude);
        String lon = String.valueOf(myLongitude);
        String location = edtLocation.getText().toString().trim();

        edtDescription.setError(null);
        edtLocation.setError(null);

        if (descriptionStr.isEmpty()) {
            edtDescription.setError("Description is required");
            edtDescription.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            edtLocation.setError("Location is required");
            edtLocation.requestFocus();
            return;
        }

        if (currentImage == null) {
            Toast.makeText(this, "Photo is required", Toast.LENGTH_SHORT).show();
            return;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        currentImage.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        String imageBase64 = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

        Map<String, String> params = new HashMap<>();
        params.put("name", edtName.getText().toString());
        params.put("description", edtDescription.getText().toString());
        params.put("location", edtLocation.getText().toString());
        params.put("image", imageBase64);
        params.put("lat", String.valueOf(lat));
        params.put("lon", String.valueOf(lon));

        if (id == null) {
            insertVolley(String.valueOf(ReportVolleyHelper.lastID + 1), nameStr, descriptionStr, lat, lon, imageBase64);
        } else {
            //updateVolley(restaurantID, nameStr, addrStr, telStr, restType, String.valueOf(latitude), String.valueOf(longitude));
        }
        finish();
    }

    private void insertVolley(String id, String nameStr, String descriptionStr, String lat, String lon, String imageBase64) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("name", nameStr);
        params.put("description", descriptionStr);
        params.put("lat", lat);
        params.put("lon", lon);
        params.put("image", imageBase64);
        JSONObject postdata = new JSONObject(params); // Data as JSON object to be insert into the database
        RequestQueue queue = Volley.newRequestQueue(this);
        // Rest api link
        String url = ReportVolleyHelper.url;
        // Use POST REST api call
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postdata,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

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



