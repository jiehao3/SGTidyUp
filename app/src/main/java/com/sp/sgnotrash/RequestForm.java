package com.sp.sgnotrash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;
import java.util.Locale;



import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

public class RequestForm extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    private ImageView imgPreview;
    private EditText edtName, edtDescription, edtLocation;
    private Bitmap currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_form);
        imgPreview = findViewById(R.id.picture_placeholder1);
        edtName = findViewById(R.id.requestname);
        edtDescription = findViewById(R.id.requestdescription1);
        edtLocation = findViewById(R.id.requestaddress1);

        setupButtons();
    }

    private void setupButtons() {

        findViewById(R.id.photo_button).setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            }
        });

        findViewById(R.id.delete_button).setOnClickListener(v -> {
            currentImage = null;
            imgPreview.setImageResource(android.R.color.transparent);
        });

        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        findViewById(R.id.submit_button).setOnClickListener(v -> validateAndSubmit());
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            currentImage = (Bitmap) data.getExtras().get("data");
            imgPreview.setImageBitmap(currentImage);
        }
    }

    private void validateAndSubmit() {
        String nameStr = edtName.getText().toString().trim();
        String descriptionStr = edtDescription.getText().toString().trim();
        String locationStr = edtLocation.getText().toString().trim();

        edtDescription.setError(null);
        edtLocation.setError(null);

        if (descriptionStr.isEmpty()) {
            edtDescription.setError("Description is required");
            edtDescription.requestFocus();
            return;
        }
        if (locationStr.isEmpty()) {
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
        byte[] imageBytes = stream.toByteArray();

        String subject = "Disposal Request by ";
        String body = "I hope this email finds you well. I would like to request the disposal of an item with the following details:\n\n" +
                            "Name: " + nameStr + "\n" +
                            "Description: " + descriptionStr + "\n" +
                            "Location: " + locationStr + "\n\n" +
                        "I have also attached an image for reference. Please let me know if any further details are required.\n\n" +
                        "Thank you for your assistance.\n\n" +
                    "Best regards,\n" +
                     nameStr;

        sendEmail(subject, body, imageBytes);
    }

    private void sendEmail(String subject, String body, byte[] attachment) {
        new SendEmailTask().execute(subject, body, attachment);
    }

    private class SendEmailTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            String subject = (String) params[0];
            String body = (String) params[1];
            byte[] attachment = (byte[]) params[2];
            MailSender sender = new MailSender("jiehaolek1@gmail.com", "hbun ovvt qhrr jaqs");
            try {
                sender.sendEmailWithAttachment("jiehao.23@ichat.sp.edu.sg", subject, body, attachment, "image.jpg");
                return true;
            } catch (Exception e) {
                Log.e("SendEmailTask", "Error sending email", e);
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(RequestForm.this, "Email sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RequestForm.this, "Failed to send email", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
