package com.sp.sgnotrash;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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

import java.io.IOException;
import java.util.ArrayList;
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

    private List<Marker> markers = new ArrayList<>(); // List to store markers

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
                        markers.add(marker); // Add marker to the list
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
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
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
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

        bottomSheetDialog.show();
    }

    private void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(findViewById(R.id.nav_view));
        }
    }
}