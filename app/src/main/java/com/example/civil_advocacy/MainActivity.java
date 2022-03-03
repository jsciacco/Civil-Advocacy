package com.example.civil_advocacy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


// J.C. Sciaccotta

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private final List<Official> officialList = new ArrayList<>();

    private RecyclerView recyclerView;

    private Official official;

    private OfficialAdapter mAdapter;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private FusedLocationProviderClient mFusedLocationClient;

    private static final int LOCATION_REQUEST = 111;

    private static String locationString = "Unspecified Location";

    private EditText et;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.location);

        mAdapter = new OfficialAdapter(officialList, this);

        recyclerView = findViewById(R.id.recycler);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        boolean trueFalse = doNetCheck();

        if (trueFalse) {
            mFusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this);
            determineLocation();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without an internet connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            textView.setText("No Data For Location");
            mAdapter.notifyDataSetChanged();
        }

        activityResultLauncher = registerForActivityResult(
              new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);
    }

    public void doLocationName(View v) {
        new Thread(this::handleGetFromLocationName).start();
    }

    public void handleGetFromLocationName() {

        officialList.clear();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses;

            String loc = et.getText().toString();
            addresses = geocoder.getFromLocationName(loc, 10);
            runOnUiThread(() -> displayAddresses(addresses));
        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
            e.printStackTrace();
        }
    }

    private void displayAddresses(List<Address> addresses) {
        StringBuilder sb = new StringBuilder();
        if (addresses.size() == 0) {
            ((TextView) findViewById(R.id.location)).setText(R.string.nothing_found);
            return;
        }

        for (Address ad : addresses) {

            String a = String.format("%s, %s %s",
                    (ad.getLocality() == null ? "" : ad.getLocality()),
                    (ad.getAdminArea() == null ? "" : ad.getAdminArea()),
                    (ad.getPostalCode() == null ? "" : ad.getPostalCode()));

            if (!a.trim().isEmpty())
                sb.append("* ").append(a.trim());

            sb.append("\n");
            String city = ad.getLocality();
            String state = ad.getAdminArea();
            String zip = ad.getPostalCode();
            if (zip != null){
                doDownload(zip);
            }
            else if (city != null){
                doDownload(city);
            }
            else{
                doDownload(state);
            }
        }
        ((TextView) findViewById(R.id.location)).setText(sb.toString());
    }
    private void determineLocation() {
        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some situations this can be null.
                        if (location != null) {
                            locationString = getPlace(location);
                            ((TextView) findViewById(R.id.location)).setText(locationString);
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    private String getPlace(Location loc) {

        StringBuilder sb = new StringBuilder();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            sb.append(String.format(
                    Locale.getDefault(),
                    "%s, %s%n%n",
                    city, state));
            doDownload(city);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void doDownload(String s){
        OfficialDownloaderRunnable loaderTaskRunnable = new OfficialDownloaderRunnable(this, s);
        new Thread(loaderTaskRunnable).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    textView.setText(R.string.deniedText);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks
        Toast.makeText(MainActivity.this, "You clicked!", Toast.LENGTH_SHORT).show();
        int pos = recyclerView.getChildLayoutPosition(v);
        official = officialList.get(pos);
        openNewActivityOfficial(null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menuA) {
            Toast.makeText(this, "You want to do A", Toast.LENGTH_SHORT).show();
            openNewActivityAbout(null);
            return true;
        } else if (item.getItemId() == R.id.menuB) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            et = new EditText(this);
            et.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(et);
            builder.setTitle("Enter a City, State or a Zip Code:");
            builder.setPositiveButton("OK", (dialog, id) -> {
                Toast.makeText(MainActivity.this, "Finding location!", Toast.LENGTH_SHORT).show();
                boolean trueFalse = doNetCheck();
                if (trueFalse) {
                    doLocationName(null);
                }
                else{
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                    builder2.setTitle("No Network Connection");
                    builder2.setMessage("Data cannot be accessed/loaded without an internet connection");
                    AlertDialog dialog2 = builder2.create();
                    dialog2.show();
                    textView.setText("No Data For Location");
                    officialList.clear();
                    mAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, id) -> {
                Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void openNewActivityOfficial(View v) {
        Intent intent = new Intent(MainActivity.this, OfficialActivity.class);
        intent.putExtra("Official", official);

        activityResultLauncher.launch(intent);
    }

    public void openNewActivityAbout(View v) {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        activityResultLauncher.launch(intent);
    }

    public void updateData(Official official) {

        if (official == null) {
            Toast.makeText(this, "Please Enter a Valid City, State, or Zip Code", Toast.LENGTH_SHORT).show();
            return;
        }

        String locCity = official.getLocCity();
        String locState = official.getLocState();
        String locZip = official.getLocZip();
        String officialPosition = official.getOfficialPosition();
        String officialName = official.getOfficialName();
        String officialParty = official.getOfficialParty();
        String address = official.getAddress();
        String addressCity = official.getAddressCity();
        String addressState = official.getAddressState();
        String addressZip = official.getAddressZip();
        String phoneNumber = official.getPhoneNumber();
        String website = official.getWebsite();
        String email = official.getEmail();
        String photoUrl = official.getPhotoUrl();
        String fb = official.getFB();
        String fbID = official.getFBID();
        String twitter = official.getTwitter();
        String twitterID = official.getTwitterID();
        String youtube = official.getYoutube();
        String youtubeID = official.getYoutubeID();

        officialList.add(
                new Official(locCity,locState,locZip,officialPosition,officialName,officialParty,
                        address,addressCity,addressState,addressZip,
                        phoneNumber,website,email,photoUrl,fb,fbID,
                        twitter, twitterID, youtube, youtubeID));

        StringBuilder sb = new StringBuilder();

        String a = String.format("%s %s %s",
                    locCity, locState, locZip);

        if (!a.trim().isEmpty())
            sb.append(a.trim());

        textView.setText(sb.toString());

        mAdapter.notifyDataSetChanged();


    }

    public void handleResult(ActivityResult result) {
        Toast.makeText(this, "Back to MainActivity!", Toast.LENGTH_SHORT).show();
        return;
    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            Toast.makeText(MainActivity.this, getString(R.string.connected), Toast.LENGTH_SHORT).show();
            return true;

        } else {
            Toast.makeText(MainActivity.this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
