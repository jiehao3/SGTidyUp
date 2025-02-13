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

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SignInActivity extends AppCompatActivity {
    private EditText editusername, editTextPassword;
    private Button buttonSignUp;
    private Button buttonSignIn;
    private RequestQueue queue;

    int volleyResponseStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize UI elements
        editusername = findViewById(R.id.etUsername);
        editTextPassword = findViewById(R.id.etPassword);
        buttonSignUp = findViewById(R.id.btnSignUp);
        buttonSignIn = findViewById(R.id.btnLogin);
        queue = Volley.newRequestQueue(this);

        // Home button: return to MainMenu
        ImageView homeButton = findViewById(R.id.homeButtonSignIn);
        homeButton.setOnClickListener(v -> {
            finish();
        });

        // Handle sign in click
        buttonSignIn.setOnClickListener(v -> {
            String username = editusername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()){
                Toast.makeText(SignInActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            signIn(username, password);
        });

        // Navigate to the sign-up page if user does not have an account.
        buttonSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void signIn(String username, String password) {
        // Build query parameters similar to your reference code.
        String params = "?where={\"username\": {\"$eq\":[\"" + username + "\"]}, " +
                "\"password\": {\"$eq\":[\"" + password + "\"]}}";
        // Append the parameters to your login URL.
        String signInUrl = ReportVolleyHelper.loginurl + params;

        // Create a new Volley request queue.
        RequestQueue queue = Volley.newRequestQueue(this);


        // Create a GET JsonObjectRequest.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, signInUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Check that the network response status was 200.
                        if (volleyResponseStatus == 200) {
                            Log.d("Response", response.toString());
                            try {
                                // Get the number of records returned.
                                int count = response.getInt("count");
                                if (count > 0) {
                                    // Optional: retrieve additional user details from the response.
                                    // (Assuming your response includes a "documents" array with user info.)
                                    JSONArray documents = response.getJSONArray("data");
                                    JSONObject userObject = documents.getJSONObject(0);
                                    String usernameResponse = userObject.getString("username");
                                    int points = userObject.getInt("point");
                                    if (userObject.has("point")) {
                                        points = userObject.getInt("point");
                                    }

                                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("isSignedIn", true);
                                    editor.putString("username", usernameResponse); // Corrected key
                                    editor.putInt("points", points); // Corrected key
                                    editor.apply();

                                    Toast.makeText(SignInActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent = new Intent(SignInActivity.this, MainMenu.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(SignInActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(SignInActivity.this, "Error processing response", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("OnErrorResponse", error.toString());
                        Toast.makeText(SignInActivity.this, "Error signing in", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return ReportVolleyHelper.getHeaders();
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                volleyResponseStatus = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };

        // Add the request to the queue.
        queue.add(request);
    }

}
