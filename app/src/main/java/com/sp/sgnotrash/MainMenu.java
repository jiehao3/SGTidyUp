package com.sp.sgnotrash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainMenu extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap myMap;

    private DrawerLayout drawerLayout;
    private ImageView menubutton;

    private ImageView statuscheck;

    private SearchView searchview;
    private GPSTracker gpsTracker;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private TextView areaStatusText;
    private Handler handler;
    private Runnable refreshRunnable;

    private ConstraintLayout mainlayout;

    private boolean show = false;

    private List<Marker> markers = new ArrayList<>();
    private List<Marker> recyclingmarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        new Handler().postDelayed(() -> {
            setContentView(R.layout.main);
            startMain();
        }, 2000);
    }

    private void startMain() {
        searchview = findViewById(R.id.searchbar);
        menubutton = findViewById(R.id.menubutton);
        drawerLayout = findViewById(R.id.drawer_layout);
        areaStatusText = findViewById(R.id.AreaStatus);
        mainlayout = findViewById(R.id.mainlayout);
        statuscheck = findViewById(R.id.statuscheck);

        menubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                // Handle item clicks
                if (id == R.id.contactsupport) {
                    showSupportOptions();
                } else if (id == R.id.webpage) {
                    Intent intent = new Intent(MainMenu.this, WebPageActivity.class);
                    startActivity(intent);
                } else if (id == R.id.settings) {
                    Intent intent = new Intent(MainMenu.this, SettingsActivity.class);
                    startActivity(intent);
                }

                // Close the drawer after handling the click
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });




        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchview.getQuery().toString();
                List<Address> addresslist = null;
                if (location != null) {
                    Geocoder geocoder = new Geocoder(MainMenu.this);
                    try {
                        addresslist = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresslist != null && !addresslist.isEmpty()) {
                        Address address = addresslist.get(0);
                        LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
                        Marker marker = myMap.addMarker(new MarkerOptions().position(latlng).title(location));
                        markers.add(marker);
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));
                    } else {
                        Toast.makeText(MainMenu.this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // Request location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            initMap();
        }

        // Floating Action Button to trigger BottomSheet
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map3);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        // Enable My Location layer if permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myMap.setMyLocationEnabled(true);

            // Get the current location using GPSTracker
            gpsTracker = new GPSTracker(MainMenu.this);
            if (gpsTracker.canGetlocation()) {
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                if (latitude != 0.0 && longitude != 0.0) {
                    LatLng myLocation = new LatLng(latitude, longitude);
                    Marker marker = myMap.addMarker(new MarkerOptions().position(myLocation).title("Your Current Location"));
                    markers.add(marker); // Add marker to the list
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
                } else {
                    Toast.makeText(this, "Unable to get location, try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enable GPS to get location.", Toast.LENGTH_SHORT).show();
            }
        }

        // Start the handler to refresh the page every 5 seconds
        startRefreshing();
    }

    private void startRefreshing() {
        handler = new Handler();
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                if (myMap != null) {
                    LatLngBounds visibleBounds = myMap.getProjection().getVisibleRegion().latLngBounds;
                    int markerCount = 0;
                    for (Marker marker : markers) {
                        if (visibleBounds.contains(marker.getPosition())) {
                            markerCount++;
                        }
                    }

                    // Update the UI based on the number of markers
                    if (markerCount >= 0 && markerCount <= 1) {
                        areaStatusText.setText("Area Status: Clean");
                        mainlayout.setBackgroundColor(Color.parseColor("#DDE4D9"));
                        statuscheck.setImageResource(R.drawable.img_4);
                    } else if (markerCount >= 2 && markerCount <= 5) {
                        areaStatusText.setText("Area Status: Slightly Dirty");
                        mainlayout.setBackgroundColor(Color.parseColor("#EDF795"));
                        statuscheck.setImageResource(R.drawable.img_5);
                    } else {
                        areaStatusText.setText("Area Status: Very Dirty");
                        mainlayout.setBackgroundColor(Color.parseColor("#9E4242"));
                        statuscheck.setImageResource(R.drawable.img_3);
                    }
                }
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(refreshRunnable);


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMap();
            } else {
                Toast.makeText(this, "Permission denied. Cannot access location.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openBottomSheet() {

        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottomsheetlayout, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetView.findViewById(R.id.cancel_button).setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetView.findViewById(R.id.Report).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            startActivity(new Intent(this, ReportActivity.class));
            Toast.makeText(this, "Report clicked", Toast.LENGTH_SHORT).show();
        });

        bottomSheetView.findViewById(R.id.Request).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            startActivity(new Intent(this, RequestActivity.class));
            Toast.makeText(this, "Request clicked", Toast.LENGTH_SHORT).show();
        });

        bottomSheetView.findViewById(R.id.ShowAlllocations).setOnClickListener(v -> {
            if (show == false) {
                showAllRecyclingBins();
            }
            else{
                removeRecycleBinsMarkers();
            }
            bottomSheetDialog.dismiss();
            Toast.makeText(this, "Bins clicked", Toast.LENGTH_SHORT).show();
        });

        bottomSheetDialog.show();
    }

    private void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(findViewById(R.id.nav_view));
        }
    }
    private void showSupportOptions() {
        // List of support options (names and phone numbers)
        String[] supportOptions = {"Local Call", "Overseas Call"};
        String[] phoneNumbers = {"+65 6225 5632", "+65 6225 5632"};

        // Create an AlertDialog to display the options
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("To report suspected scams or verify the identity of the NEA officer");

        // Set the list of options
        builder.setItems(supportOptions, (dialog, which) -> {
            // Get the selected phone number
            String selectedPhoneNumber = phoneNumbers[which];

            // Launch the phone app with the selected number
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + selectedPhoneNumber));
            startActivity(intent);
        });

        // Show the dialog
        builder.show();
    }
    private static class GeoJSON {
        List<Feature> features;
    }

    private static class Feature {
        Geometry geometry;
        Properties properties;
    }

    private static class Geometry {
        String type;
        List<Double> coordinates;
    }

    private static class Properties {
        String NAME;
        String DESCRIPTION;
        String Address;
        String TYPE_LEVEL_;
        String TYPE_LEVEL1;
        String LAYER;
    }

    private void showAllRecyclingBins() {
        List<Feature> recyclingFeatures = loadRecyclingLocationsFromGeoJSON();
        if (recyclingFeatures.isEmpty()) {
            Toast.makeText(this, "No recycling bins found", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Feature feature : recyclingFeatures) {
            if (feature.geometry != null && feature.geometry.coordinates != null && feature.geometry.coordinates.size() >= 2) {
                LatLng location = new LatLng(feature.geometry.coordinates.get(1), feature.geometry.coordinates.get(0));


                Marker marker = myMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title("Recycling Bin")
                        .snippet("Deposit papers, plastics, glass and metals")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_9)));

                recyclingmarkers.add(marker);
            }
        }
        show = true;
    }
    private void removeRecycleBinsMarkers(){
        for (Marker marker : recyclingmarkers) {
            marker.remove();
        }
        recyclingmarkers.clear();
        show = false;
    }
    private List<Feature> loadRecyclingLocationsFromGeoJSON() {
        List<Feature> features = new ArrayList<>();
        try {
            String json = readAssetsFile("recyclingbins.geojson");
            Gson gson = new Gson();
            GeoJSON geoJSON = gson.fromJson(json, GeoJSON.class);
            features = geoJSON.features;
        } catch (Exception e) {
            Log.e("GeoJSON", "Error loading locations", e);
        }
        return features;
    }

    private String readAssetsFile(String filename) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open(filename);
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }

}