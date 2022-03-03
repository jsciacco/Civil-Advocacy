package com.example.civil_advocacy;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class OfficialDownloaderRunnable implements Runnable {

    private static final String TAG = "OfficialDownloaderRunnable";

    private final MainActivity mainActivity;
    private final String cityName;

    private static final String officialURLpart1 = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    private static final String officialURLpart2 = "&address=";

    private static final String yourAPIKey = "AIzaSyB_-sIAb1pK_YXooQsHmREo43eyqZw8XoQ";

    private final List<Official> officialList = new ArrayList<>();

    OfficialDownloaderRunnable(MainActivity mainActivity, String cityName) {
        this.mainActivity = mainActivity;
        this.cityName = cityName;
    }


    @Override
    public void run() {

        String finalURL = officialURLpart1+yourAPIKey+officialURLpart2+cityName;

        Log.d(TAG, "doInBackground: " + finalURL);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(finalURL);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    public void handleResults(final String jsonString) {

        parseJSON(jsonString);
        for (Official o: officialList) {
            mainActivity.runOnUiThread(() -> mainActivity.updateData(o));
        }
    }

    private Official parseJSON(String s) {

        try {

            JSONObject jObjMain = new JSONObject(s);
            JSONArray jOffices = jObjMain.getJSONArray("offices");
            JSONArray jOfficials = jObjMain.getJSONArray("officials");

            String locCity = jObjMain.getJSONObject("normalizedInput").getString("city");
            String locState = jObjMain.getJSONObject("normalizedInput").getString("state");
            String locZip = jObjMain.getJSONObject("normalizedInput").getString("zip");
            for (int i = 0; i <jOffices.length(); i++){
                JSONObject objArray = jOffices.getJSONObject(i);
                String officialPosition = objArray.getString("name");
                JSONArray officialIndices = objArray.getJSONArray("officialIndices");
                for (int j = 0; j <officialIndices.length(); j++){
                    int index = officialIndices.getInt(j);
                    JSONObject iOfficial = jOfficials.getJSONObject(index);
                    String officialName = iOfficial.getString("name");
                    String officialParty = iOfficial.getString("party");
                    String address = "";
                    String addressCity = "";
                    String addressState = "";
                    String addressZip = "";
                    if (iOfficial.has("address")) {
                        String addressLine1 = "";
                        if (iOfficial.getJSONArray("address").getJSONObject(0).has("line1")) {
                            addressLine1 = iOfficial.getJSONArray("address").getJSONObject(0).getString("line1");
                        }
                        String addressLine2 = "";
                        if (iOfficial.getJSONArray("address").getJSONObject(0).has("line2")) {
                            addressLine2 = iOfficial.getJSONArray("address").getJSONObject(0).getString("line2");
                        }
                        String addressLine3 = "";
                        if (iOfficial.getJSONArray("address").getJSONObject(0).has("line3")) {
                            addressLine3 = iOfficial.getJSONArray("address").getJSONObject(0).getString("line3");
                        }
                        address = addressLine1 + "," + addressLine2 + "," + addressLine3;

                        if (iOfficial.getJSONArray("address").getJSONObject(0).has("city")) {
                            addressCity = iOfficial.getJSONArray("address").getJSONObject(0).getString("city");
                        }

                        if (iOfficial.getJSONArray("address").getJSONObject(0).has("state")) {
                            addressState = iOfficial.getJSONArray("address").getJSONObject(0).getString("state");
                        }

                        if (iOfficial.getJSONArray("address").getJSONObject(0).has("zip")) {
                            addressZip = iOfficial.getJSONArray("address").getJSONObject(0).getString("zip");
                        }
                    }
                    String phoneNumber = "";
                    if(iOfficial.has("phones")) {
                        if (!iOfficial.getJSONArray("phones").getString(0).isEmpty()) {
                            phoneNumber = iOfficial.getJSONArray("phones").getString(0);
                        }
                    }
                    String website = "";
                    if(iOfficial.has("urls")) {
                        if (!iOfficial.getJSONArray("urls").getString(0).isEmpty()) {
                            website = iOfficial.getJSONArray("urls").getString(0);
                        }
                    }
                    String email = "";
                    if (iOfficial.has("emails")){
                       email = iOfficial.getJSONArray("emails").getString(0);
                    }
                    String photoUrl = "";
                    if (iOfficial.has("photoUrl")) {
                        photoUrl = iOfficial.getString("photoUrl");
                    }
                    String fb = "";
                    String fbID = "";
                    String twitter = "";
                    String twitterID = "";
                    String youtube = "";
                    String youtubeID = "";
                        /*if (iOfficial.getJSONArray("channels").getJSONObject(0).has("type")) {
                            if (iOfficial.getJSONArray("channels").getJSONObject(0).getString("type").contains("Twitter")) {
                                twitter = iOfficial.getJSONArray("channels").getJSONObject(0).getString("type");
                                twitterID = iOfficial.getJSONArray("channels").getJSONObject(0).getString("id");
                            } else if (iOfficial.getJSONArray("channels").getJSONObject(0).getString("type").contains("Facebook")) {
                                fb = iOfficial.getJSONArray("channels").getJSONObject(0).getString("type");
                                fbID = iOfficial.getJSONArray("channels").getJSONObject(0).getString("id");
                            } else {
                                youtube = iOfficial.getJSONArray("channels").getJSONObject(0).getString("type");
                                youtubeID = iOfficial.getJSONArray("channels").getJSONObject(0).getString("id");
                            }
                        }*/
                    if (iOfficial.has("channels")) {
                        if (iOfficial.getJSONArray("channels").length() == 1) {
                            if (iOfficial.getJSONArray("channels").getJSONObject(0).has("type")) {
                                if (iOfficial.getJSONArray("channels").getJSONObject(0).getString("type").contains("Twitter")) {
                                    twitter = iOfficial.getJSONArray("channels").getJSONObject(0).getString("type");
                                    twitterID = iOfficial.getJSONArray("channels").getJSONObject(0).getString("id");
                                } else if (iOfficial.getJSONArray("channels").getJSONObject(0).getString("type").contains("Facebook")) {
                                    fb = iOfficial.getJSONArray("channels").getJSONObject(0).getString("type");
                                    fbID = iOfficial.getJSONArray("channels").getJSONObject(0).getString("id");
                                } else {
                                    youtube = iOfficial.getJSONArray("channels").getJSONObject(0).getString("type");
                                    youtubeID = iOfficial.getJSONArray("channels").getJSONObject(0).getString("id");
                                    ;
                                }
                            }
                        }
                        if (iOfficial.getJSONArray("channels").length() == 2) {
                            if (iOfficial.getJSONArray("channels").getJSONObject(0).has("type")) {
                                if (iOfficial.getJSONArray("channels").getJSONObject(0).getString("type").contains("Twitter")) {
                                    twitter = iOfficial.getJSONArray("channels").getJSONObject(0).getString("type");
                                    twitterID = iOfficial.getJSONArray("channels").getJSONObject(0).getString("id");
                                } else if (iOfficial.getJSONArray("channels").getJSONObject(0).getString("type").contains("Facebook")) {
                                    fb = iOfficial.getJSONArray("channels").getJSONObject(0).getString("type");
                                    fbID = iOfficial.getJSONArray("channels").getJSONObject(0).getString("id");
                                } else {
                                    youtube = iOfficial.getJSONArray("channels").getJSONObject(0).getString("type");
                                    youtubeID = iOfficial.getJSONArray("channels").getJSONObject(0).getString("id");
                                    ;
                                }
                            }
                            if (iOfficial.getJSONArray("channels").getJSONObject(1).has("type")) {
                                if (iOfficial.getJSONArray("channels").getJSONObject(1).getString("type").contains("Twitter")) {
                                    twitter = iOfficial.getJSONArray("channels").getJSONObject(1).getString("type");
                                    twitterID = iOfficial.getJSONArray("channels").getJSONObject(1).getString("id");
                                } else if (iOfficial.getJSONArray("channels").getJSONObject(1).getString("type").contains("Facebook")) {
                                    fb = iOfficial.getJSONArray("channels").getJSONObject(1).getString("type");
                                    fbID = iOfficial.getJSONArray("channels").getJSONObject(1).getString("id");
                                } else {
                                    youtube = iOfficial.getJSONArray("channels").getJSONObject(1).getString("type");
                                    youtubeID = iOfficial.getJSONArray("channels").getJSONObject(1).getString("id");
                                }
                            }
                        }
                        if (iOfficial.getJSONArray("channels").length() == 3) {
                            if (iOfficial.getJSONArray("channels").getJSONObject(0).has("type")) {
                                if (iOfficial.getJSONArray("channels").getJSONObject(0).getString("type").contains("Twitter")) {
                                    twitter = iOfficial.getJSONArray("channels").getJSONObject(0).getString("type");
                                    twitterID = iOfficial.getJSONArray("channels").getJSONObject(0).getString("id");
                                } else if (iOfficial.getJSONArray("channels").getJSONObject(0).getString("type").contains("Facebook")) {
                                    fb = iOfficial.getJSONArray("channels").getJSONObject(0).getString("type");
                                    fbID = iOfficial.getJSONArray("channels").getJSONObject(0).getString("id");
                                } else {
                                    youtube = iOfficial.getJSONArray("channels").getJSONObject(0).getString("type");
                                    youtubeID = iOfficial.getJSONArray("channels").getJSONObject(0).getString("id");
                                    ;
                                }
                            }
                            if (iOfficial.getJSONArray("channels").getJSONObject(1).has("type")) {
                                if (iOfficial.getJSONArray("channels").getJSONObject(1).getString("type").contains("Twitter")) {
                                    twitter = iOfficial.getJSONArray("channels").getJSONObject(1).getString("type");
                                    twitterID = iOfficial.getJSONArray("channels").getJSONObject(1).getString("id");
                                } else if (iOfficial.getJSONArray("channels").getJSONObject(1).getString("type").contains("Facebook")) {
                                    fb = iOfficial.getJSONArray("channels").getJSONObject(1).getString("type");
                                    fbID = iOfficial.getJSONArray("channels").getJSONObject(1).getString("id");
                                } else {
                                    youtube = iOfficial.getJSONArray("channels").getJSONObject(1).getString("type");
                                    youtubeID = iOfficial.getJSONArray("channels").getJSONObject(1).getString("id");
                                }
                            }
                            if (iOfficial.getJSONArray("channels").getJSONObject(2).has("type")) {
                                if (iOfficial.getJSONArray("channels").getJSONObject(2).getString("type").contains("Twitter")) {
                                    twitter = iOfficial.getJSONArray("channels").getJSONObject(2).getString("type");
                                    twitterID = iOfficial.getJSONArray("channels").getJSONObject(2).getString("id");
                                } else if (iOfficial.getJSONArray("channels").getJSONObject(2).getString("type").contains("Facebook")) {
                                    fb = iOfficial.getJSONArray("channels").getJSONObject(2).getString("type");
                                    fbID = iOfficial.getJSONArray("channels").getJSONObject(2).getString("id");
                                } else {
                                    youtube = iOfficial.getJSONArray("channels").getJSONObject(2).getString("type");
                                    youtubeID = iOfficial.getJSONArray("channels").getJSONObject(2).getString("id");
                                }
                            }
                        }
                    }
                    officialList.add(new Official(locCity, locState, locZip, officialPosition, officialName, officialParty,
                            address, addressCity, addressState, addressZip,
                            phoneNumber, website, email, photoUrl, twitter, twitterID, fb, fbID, youtube, youtubeID));
                }
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
