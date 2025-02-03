package com.sp.sgnotrash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

public class RequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ArrayList<String> reportList;

    private android.widget.ImageButton ImageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        // Initialize UI elements
        ImageButton backButton = findViewById(R.id.backButton);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnRemove = findViewById(R.id.btnRemove);
        recyclerView = findViewById(R.id.recyclerView);



        // Back button functionality
        backButton.setOnClickListener(v -> finish()); // Goes back to the previous activity

        // Add button functionality
        btnAdd.setOnClickListener(v -> addRequest());

        // Clean button functionality
        btnRemove.setOnClickListener(v -> removeReports());
    }

    private void addRequest() {
        startActivity(new Intent(this, RequestForm.class));

    }

    private void removeReports() {

    }
}