package com.example.civil_advocacy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private Picasso picasso;

    private ImageView imageView1;
    private ImageView imageView2;
    private TextView locOfficial;
    private TextView oPosition;
    private TextView oName;

    private Official o;

    private ConstraintLayout cL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        locOfficial = findViewById(R.id.locOfficial);
        imageView1 = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        oPosition = findViewById(R.id.office);
        oName = findViewById(R.id.offName);

        picasso = Picasso.get();

        picasso.setLoggingEnabled(true);

        cL = (ConstraintLayout) findViewById(R.id.activity_photo);

        Intent intent = getIntent();
        if (intent.hasExtra("Official")) {
            o = (Official) intent.getSerializableExtra("Official");
        }

        StringBuilder sb = new StringBuilder();

        String a = String.format("%s %s %s",
                o.getLocCity(), o.getLocState(), o.getLocZip());

        if (!a.trim().isEmpty())
            sb.append(a.trim());

        locOfficial.setText(sb.toString());

        if (o.getOfficialPosition() != "") {
            oPosition.setText(o.getOfficialPosition());
        }
        if (o.getOfficialName() != "") {
            oName.setText(o.getOfficialName());
        }
        if (o.getOfficialParty().contains("Democrat")) {
            imageView2.setImageResource(R.drawable.dem_logo);
            cL.setBackgroundColor(Color.parseColor("#0000FF"));
        } else if (o.getOfficialParty().contains("Republican")) {
            imageView2.setImageResource(R.drawable.rep_logo);
            cL.setBackgroundColor(Color.parseColor("#FF0000"));
        } else {
            cL.setBackgroundColor(Color.parseColor("#FF000000"));
        }

        String url = o.getPhotoUrl();
        if (url != "") {
            loadRemoteImage(url);
        }
        else{
            imageView1.setImageResource(R.drawable.missing);
        }

    }
    private void loadRemoteImage(String imageURL) {
        // Needs gradle  implementation 'com.squareup.picasso:picasso:2.71828'

        boolean trueFalse = doNetCheck();
        if (trueFalse) {
            long millisS = System.currentTimeMillis();

            picasso.load(imageURL)
                    .error(R.drawable.missing)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView1, new Callback() {
                        @Override
                        public void onSuccess() {
                            long millisE = System.currentTimeMillis();
                            Log.d(TAG, "loadRemoteImage: Duration: " +
                                    (millisE - millisS) + " ms");
                        }

                        @Override
                        public void onError(Exception e) {
                            long millisE = System.currentTimeMillis();
                            Log.d(TAG, "loadRemoteImage: Duration: " +
                                    (millisE - millisS) + " ms");
                        }
                    });
        }
        else{
            imageView1.setImageResource(R.drawable.brokenimage);
        }
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
            Toast.makeText(PhotoActivity.this, getString(R.string.connected), Toast.LENGTH_SHORT).show();
            return true;

        } else {
            Toast.makeText(PhotoActivity.this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
