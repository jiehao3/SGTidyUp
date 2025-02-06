package com.sp.sgnotrash;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Response;
import com.android.volley.AuthFailureError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AstraDbHelper {

    // Your Astra DB credentials and constants
    private static final String ASTRA_DB_ID = "0d6a6613-07dd-45f3-bfce-cbe992364d22";
    private static final String REGION = "us-east1";
    private static final String NAMESPACE = "reportlist";
    private static final String COLLECTION = "reports";
    private static final String TOKEN = "AstraCS:rLCpaKOMUmhHLzUhDrIuKzDI:6c36d9bbe13aea1118a18abd88c24c52d4e6f84dd7ad397ac1db33921bdf1c7f";
    // Construct the base URL according to Astra DB REST API format.
    private static final String BASE_URL= "https://0d6a6613-07dd-45f3-bfce-cbe992364d22-us-east1.apps.astra.datastax.com/api/rest/v2/namespaces/reportlist/collections/reports";


    private static AstraDbHelper instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private AstraDbHelper(Context context) {
        ctx = context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(ctx);
    }

    // Get the singleton instance
    public static synchronized AstraDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AstraDbHelper(context);
        }
        return instance;
    }

    // Add a request to the queue
    public <T> void addToRequestQueue(Request<T> req) {
        requestQueue.add(req);
    }
    public void fetchReports(Response.Listener<JSONArray> listener,
                             Response.ErrorListener errorListener) {
        // For a GET request, you typically use the base URL.
        String url = BASE_URL;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                listener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add your Astra DB token in the headers
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Cassandra-Token", TOKEN);
                return headers;
            }
        };
        addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Add a report to the collection.
     *
     * @param report        A JSONObject representing your report data.
     * @param listener      A Volley listener to handle the JSONObject response.
     * @param errorListener A Volley error listener.
     */
    public void addReport(JSONObject report,
                          Response.Listener<JSONObject> listener,
                          Response.ErrorListener errorListener) {
        // For a POST request, use the same base URL.
        String url = BASE_URL;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                report,
                listener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add the required headers (token and content-type)
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Cassandra-Token", TOKEN);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        addToRequestQueue(jsonObjectRequest);
    }
}
