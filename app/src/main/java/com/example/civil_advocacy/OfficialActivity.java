package com.example.civil_advocacy;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private Picasso picasso;

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageViewFB;
    private ImageView imageViewTwit;
    private ImageView imageViewYT;

    private TextView locOfficial;
    private TextView oAddress;
    private TextView oAddressData;
    private TextView oPhone;
    private TextView oPhoneData;
    private TextView oEmail;
    private TextView oEmailData;
    private TextView oWebsite;
    private TextView oWebsiteData;
    private TextView oPosition;
    private TextView oName;
    private TextView oParty;

    private Official o;

    private ConstraintLayout cL;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        locOfficial = findViewById(R.id.locOfficial);
        imageView1 = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        imageViewFB = findViewById(R.id.imageViewFB);
        imageViewTwit = findViewById(R.id.imageViewTwit);
        imageViewYT = findViewById(R.id.imageViewYT);

        oPosition = findViewById(R.id.office);
        oName = findViewById(R.id.offName);
        oParty = findViewById(R.id.offParty);
        oAddress = findViewById(R.id.oAddress);
        oAddressData = findViewById(R.id.oAddressData);
        oPhone = findViewById(R.id.oPhone);
        oPhoneData = findViewById(R.id.oPhoneData);
        oEmail = findViewById(R.id.oEmail);
        oEmailData = findViewById(R.id.oEmailData);
        oWebsite = findViewById(R.id.oWebsite);
        oWebsiteData = findViewById(R.id.oWebsiteData);

        Linkify.addLinks(oWebsiteData, Linkify.ALL);
        Linkify.addLinks(oPhoneData, Linkify.ALL);
        Linkify.addLinks(oAddressData, Linkify.ALL);
        Linkify.addLinks(oEmailData, Linkify.ALL);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);

        picasso = Picasso.get();

        picasso.setLoggingEnabled(true);

        cL = (ConstraintLayout) findViewById(R.id.activity_official);

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
        if (o.getOfficialParty() != "") {
            oParty.setText("("+o.getOfficialParty()+")");
        }
        if (o.getAddress() != "") {
            String address = "Address: ";
            String newGetAddress = o.getAddress();
            newGetAddress = newGetAddress.replaceAll(",","");
            oAddress.setText(address);
            oAddressData.setText(String.format("%s%n %s, %s %s", newGetAddress,
                    o.getAddressCity(), o.getAddressState(), o.getAddressZip()));
        }
        if (o.getPhoneNumber() != "") {
            String phone = "Phone: ";
            oPhone.setText(phone);
            oPhoneData.setText(o.getPhoneNumber());
        }
        if (o.getEmail() != "") {
            String email = "Email: ";
            oEmail.setText(email);
            oEmailData.setText(o.getEmail());
        }
        if (o.getWebsite() != "") {
            String website = "Website: ";
            oWebsite.setText(website);
            oWebsiteData.setText(o.getWebsite());
        }
        if (o.getFBID() != "") {
            imageViewFB.setImageResource(R.drawable.facebook);
        }
        if (o.getTwitterID() != "") {
            imageViewTwit.setImageResource(R.drawable.twitter);
        }
        if (o.getYoutubeID() != "") {
            imageViewYT.setImageResource(R.drawable.youtube);
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

        Toast.makeText(OfficialActivity.this, "You clicked!", Toast.LENGTH_SHORT).show();
    }

    public void openNewActivityPhoto(View v) {

        if(o.getPhotoUrl() == ""){
            return;
        }
        else {
            Intent intent = new Intent(OfficialActivity.this, PhotoActivity.class);
            intent.putExtra("Official", o);

            activityResultLauncher.launch(intent);
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

    public void clickParty(View v) {
        String dem = "https://democrats.org";
        String rep = "https://www.gop.com";

        Intent intent;

        if(o.getOfficialParty().contains("Democrat")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dem));
        }
        else{
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rep));
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (twitter/https) intents");
        }
    }

    public void clickMap(View v) {
        String newGetAddress = o.getAddress();
        newGetAddress = newGetAddress.replaceAll(",","");
        String address = (String.format("%s, %s, %s %s", newGetAddress,
                o.getAddressCity(), o.getAddressState(), o.getAddressZip()));

        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));

        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);

        // Check if there is an app that can handle geo intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (geo) intents");
        }
    }

    public void clickCall(View v) {
        String number = o.getPhoneNumber();

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_DIAL (tel) intents");
        }
    }

    public void clickEmail(View v) {
        String[] addresses = new String[]{"christopher.hield@gmail.com", o.getEmail()};

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));

        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, "This comes from EXTRA_SUBJECT");
        intent.putExtra(Intent.EXTRA_TEXT, "Email text body from EXTRA_TEXT...");

        // Check if there is an app that can handle mailto intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles SENDTO (mailto) intents");
        }
    }

    public void clickWebsite(View v) {
        String web = o.getWebsite();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(web));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (twitter/https) intents");
        }
    }

    public void facebookClicked(View v) {
        // You need the FB user's id for the url
        String FACEBOOK_URL = "https://www.facebook.com/" + o.getFBID();

        Intent intent;

        // Check if FB is installed, if not we'll use the browser
        if (isPackageInstalled("com.facebook.katana")) {
            String urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }

        // Check if there is an app that can handle fb or https intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (fb/https) intents");
        }

    }

    public void twitterClicked(View v) {
        String user = o.getTwitterID();
        String twitterAppUrl = "twitter://user?screen_name=" + user;
        String twitterWebUrl = "https://twitter.com/" + user;

        Intent intent;
        // Check if Twitter is installed, if not we'll use the browser
        if (isPackageInstalled("com.twitter.android")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (twitter/https) intents");
        }
    }

    public void youTubeClicked(View v) {
        String name = o.getYoutubeID();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public boolean isPackageInstalled(String packageName) {
        try {
            return getPackageManager().getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void makeErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg);
        builder.setTitle("No App Found");

        AlertDialog dialog = builder.create();
        dialog.show();
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
            Toast.makeText(OfficialActivity.this, getString(R.string.connected), Toast.LENGTH_SHORT).show();
            return true;

        } else {
            Toast.makeText(OfficialActivity.this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    public void handleResult(ActivityResult result) {
        Toast.makeText(this, "Back to OfficialActivity!", Toast.LENGTH_SHORT).show();
        return;
    }
}
