package com.sp.sgnotrash;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {
    private EditText editusername, editTextPassword;
    private Button buttonSignUp;
    private Button buttonSignIn;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editusername = findViewById(R.id.etUsername);
        editTextPassword = findViewById(R.id.etPassword);
        buttonSignUp = findViewById(R.id.btnSignUp);
        buttonSignIn = findViewById(R.id.btnLogin);

        queue = Volley.newRequestQueue(this);

        buttonSignIn.setOnClickListener(v -> {
            String email = editusername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(SignInActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            signIn(email, password);
        });

        // Link to the sign-up page if the user does not have an account.
        buttonSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void signIn(String email, String password) {
        // Create JSON body for the sign in request.
        JSONObject signInData = new JSONObject();
        try {
            signInData.put("email", email);
            signInData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Replace with your actual Astra DB endpoint for sign in.
        String signInUrl = "https://astra.db.example.com/signin";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, signInUrl, signInData,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            // Retrieve additional user info from the response.
                            String userName = response.getString("userName");
                            // Save the sign in state.
                            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("isSignedIn", true);
                            editor.putString("userName", userName);
                            editor.apply();
                            Toast.makeText(SignInActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                            finish(); // Return to MainMenu
                        } else {
                            Toast.makeText(SignInActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(SignInActivity.this, "Error signing in", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(request);
    }
}
