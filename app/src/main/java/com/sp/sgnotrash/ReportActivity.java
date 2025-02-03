package com.sp.sgnotrash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {


    private RecyclerView recyclerView;

    private ArrayList<String> reportList;

    private ImageButton ImageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize UI elements
        ImageButton backButton = findViewById(R.id.backButton);
        Button btnAdd = findViewById(R.id.btnAdd1);
        Button btnClean = findViewById(R.id.btnClean);
        recyclerView = findViewById(R.id.recyclerView);



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
