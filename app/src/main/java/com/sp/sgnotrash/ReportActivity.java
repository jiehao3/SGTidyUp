package com.sp.sgnotrash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {


    private RecyclerView reportrecyclerView;

    private List<Report> reportList = new ArrayList<>();
    private ReportAdapter adapter;
    private RequestQueue queue;

    private ImageButton ImageButton;
    private RequestQueue requestQueue;

    private int volleyResponseStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize UI elements
        ImageButton backButton = findViewById(R.id.backButton);
        Button btnAdd = findViewById(R.id.btnAdd1);
        Button btnClean = findViewById(R.id.btnClean);
        reportrecyclerView = findViewById(R.id.recyclerView);
        reportrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportAdapter(reportList);
        reportrecyclerView.setAdapter(adapter);
        requestQueue = Volley.newRequestQueue(this);

        // Fetch reports from Astra DB


        // Back button functionality
        backButton.setOnClickListener(v -> finish()); // Goes back to the previous activity

        // Add button functionality
        btnAdd.setOnClickListener(v -> addReport());

        // Clean button functionality
        btnClean.setOnClickListener(v -> clearReports());
    }

    private void addReport() {
        startActivity(new Intent(this, ReportForm.class));

    }

    private void clearReports() {

    }
}
    /*
    @Override
    protected void onResume() {
        getAllVolley();
        super.onResume();
    }
    /*
    private void getAllVolley() {
        queue = Volley.newRequestQueue(this);
        String url = ReportVolleyHelper.url + "rows"; //Query all records
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (volleyResponseStatus == 200) { // Read successfully from database
                            try {
                                int count = response.getInt("count"); //Number of records from database
                                adapter.clear(); //reset adapter
                                if (count > 0) { //Has more than 1 record
                                    empty.setVisibility(View.INVISIBLE);
                                    JSONArray data = response.getJSONArray("data");//Get all the records as JSON array
                                    for (int i = 0; i <= count; i++) { // Loop through all records
                                        Report r = new Report();
                                        // Store the lastest id in lastID
                                        if (Report.lastID < data.getJSONObject(i).getInt("id")) {
                                            Report.lastID = data.getJSONObject(i).getInt("id");
                                        }
                                        // For each json record
                                        r.setId(data.getJSONObject(i).getString("id")); //read the id
                                        r.setName(data.getJSONObject(i).getString("restaurantname")); //extract the restaurantname
                                        r.setAddress(data.getJSONObject(i).getString("restaurantaddress")); //extract the restaurantaddress
                                        r.setTelephone(data.getJSONObject(i).getString("restauranttel")); //extract the restauranttel
                                        r.setRestaurantType(data.getJSONObject(i).getString("restauranttype")); //extract the restauranttype
                                        r.setLat(data.getJSONObject(i).getString("lat")); //extract the lat
                                        r.setLon(data.getJSONObject(i).getString("lon")); //extract the lon
                                        adapter.add(r); // add the record to the adapter
                                    }
                                } else {
                                    empty.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
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
                return ReportVolleyHelper.getHeader();
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                volleyResponseStatus = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        // add JsonObjectRequest to the RequestQueue
        queue.add(jsonObjectRequest);
    }
}


//"mongodb+srv://admin:9Xy1NSBQhDMDexDg@cluster0.mrzw2.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0&ssl=true&dnsClient.disableJndi=true";

     */


