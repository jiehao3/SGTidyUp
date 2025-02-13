package com.sp.sgnotrash;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import static android.content.Context.MODE_PRIVATE;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText editUsername, editPassword;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize UI elements
        editUsername = findViewById(R.id.EnterUser);
        editPassword = findViewById(R.id.EnterPass);
        Button btnSignUp = findViewById(R.id.SignupButton);
        Button btnbacktoSignIn = findViewById(R.id.LoginButton1);
        ImageView homeButton = findViewById(R.id.homeButtonSignUp);
        queue = Volley.newRequestQueue(this);

        // Return to main menu when home button is clicked.
        homeButton.setOnClickListener(v -> returnToMainMenu());

        btnbacktoSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });


        // Set up sign-up button click handler.
        btnSignUp.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            // Here we default the user's points to 0
            if(username.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // Call our Volley method to insert the new user.
            insertUserVolley(username, password);
        });
    }

    private void insertUserVolley(String username, String password) {
        // Use your sign-up endpoint URL here.
        // If you have a separate URL for sign-ups, replace loginurl accordingly.
        String SIGNUP_URL = ReportVolleyHelper.loginurl;

        // Prepare the parameters for the POST request.
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("point", "0"); // Default points

        JSONObject postData = new JSONObject(params);

        // Create a new JsonObjectRequest.
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                SIGNUP_URL,
                postData,
                response -> handleSignUpSuccess(response),
                error -> handleSignUpError(error)
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Reuse headers from your helper class
                return ReportVolleyHelper.getHeaders();
            }
        };

        // Add the request to the queue.
        queue.add(request);
    }

    private void handleSignUpSuccess(JSONObject response) {
        String username = editUsername.getText().toString().trim();

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("isSignedIn", true)
                .putString("username", username) // Corrected key
                .putInt("points", 0) // Corrected key
                .apply();

        Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignUpActivity.this, MainMenu.class);
        startActivity(intent);
    }

    private void handleSignUpError(VolleyError error) {
        Log.e("SignUpError", error.toString());
        Toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
    }

    private void returnToMainMenu() {
        finish();
    }
}
