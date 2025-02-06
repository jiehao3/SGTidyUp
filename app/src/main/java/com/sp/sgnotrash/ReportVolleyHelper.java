package com.sp.sgnotrash;


import android.util.Log;

import java.util.HashMap;

public class ReportVolleyHelper {
    static String region = " https://6ff33eaf-c5b8-41c6-a53d-d5423fce3769-asia-south1.apps.astra.datastax.com/api/rest";
    static String url = region + "/v2/keyspaces/reportlist/reports/";

    static String loginurl = region + "/v2/keyspaces/reportlist/login";
    static String Cassandra_Token = "AstraCS:YDHylxxZAxAURiEGaKwOeSIq:25a6ad1b03f26e00afeabaa3430ee2b064a6d7cdce565f7ee5b61409d5c7d700";
    static int lastID = 0;
    static HashMap getHeaders(){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Cassandra-Token", Cassandra_Token);
        Log.d("RequestHeaders", headers.toString());
        headers.put("Accept","application/json");
        return headers;
    }
}