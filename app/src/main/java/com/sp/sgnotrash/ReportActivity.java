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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

    private ImageButton refreshButton;
    private RequestQueue requestQueue;

    private int volleyResponseStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        refreshButton = findViewById(R.id.refreshButton);
        ImageButton backButton = findViewById(R.id.backButton);
        Button btnAdd = findViewById(R.id.btnAdd1);
        reportrecyclerView = findViewById(R.id.recyclerView);
        reportrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportAdapter(reportList, report -> {
            Intent intent = new Intent(ReportActivity.this, cleanactivity.class);
            intent.putExtra("image", report.getImage());
            intent.putExtra("name", report.getName());
            intent.putExtra("description", report.getDescription());
            intent.putExtra("ID", report.getReport_id());
            startActivity(intent);
        });
        reportrecyclerView.setAdapter(adapter);
        requestQueue = Volley.newRequestQueue(this);
        backButton.setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(v -> addReport());
        refreshButton.setOnClickListener(v -> getAllVolley());
    }

    private void addReport() {
        startActivity(new Intent(this, ReportForm.class));

    }

    private void clearReports() {

    }


    @Override
    protected void onResume() {
        getAllVolley();
        super.onResume();
    }


    private void getAllVolley() {
        reportList.clear();
        queue = Volley.newRequestQueue(this);
        String url = ReportVolleyHelper.url + "rows";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (volleyResponseStatus == 200) {
                            try {
                                int count = response.getInt("count");
                                reportList.clear();

                                if (count > 0) {
                                    JSONArray data = response.getJSONArray("data");
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject item = data.getJSONObject(i);
                                        Report report = new Report();
                                        int id = item.getInt("id");
                                        if (ReportVolleyHelper.lastID < id) {
                                            ReportVolleyHelper.lastID = id;
                                        }

                                        report.setReport_id(item.getString("id"));
                                        report.setName(item.getString("name"));
                                        report.setDescription(item.getString("description"));
                                        report.setLat(item.getString("lat"));
                                        report.setLon(item.getString("lon"));
                                        report.setImage(item.getString("image"));
                                        reportList.add(report);
                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
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
                        Log.e("OnErrorResponse", error.toString());
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
        queue.add(jsonObjectRequest);
    }
}





