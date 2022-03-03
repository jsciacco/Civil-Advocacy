package com.example.civil_advocacy;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class Official implements Serializable {

    private final String locCity;
    private final String locState;
    private final String locZip;
    private final String officialPosition;
    private final String officialName;
    private final String officialParty;
    private final String address;
    private final String addressCity;
    private final String addressState;
    private final String addressZip;
    private final String phoneNumber;
    private final String website;
    private final String email;
    private final String photoUrl;
    private final String fb;
    private final String fbID;
    private final String twitter;
    private final String twitterID;
    private final String youtube;
    private final String youtubeID;



    public Official(String locCity, String locState, String locZip,
                    String officialPosition, String officialName, String officialParty,
                    String address, String addressCity, String addressState,
                    String addressZip, String phoneNumber, String website,  String email, String photoUrl,
                    String twitter, String twitterID, String fb, String fbID, String youtube, String youtubeID) {
        this.locCity = locCity;
        this.locState = locState;
        this.locZip = locZip;
        this.officialPosition = officialPosition;
        this.officialName = officialName;
        this.officialParty = officialParty;
        this.address = address;
        this.addressCity = addressCity;
        this.addressState = addressState;
        this.addressZip = addressZip;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.email = email;
        this.photoUrl = photoUrl;
        this.fb = fb;
        this.fbID = fbID;
        this.twitter = twitter;
        this.twitterID = twitterID;
        this.youtube = youtube;
        this.youtubeID = youtubeID;
    }

    public String getLocCity() {
        return locCity;
    }
    public String getLocState() {
        return locState;
    }
    public String getLocZip(){
        return locZip;
    }
    public String getOfficialPosition() {
        return officialPosition;
    }
    public String getOfficialName() {
        return officialName;
    }
    public String getOfficialParty(){
        return officialParty;
    }
    public String getAddress(){ return address;}
    public String getAddressCity(){
        return addressCity;
    }
    public String getAddressState() {
        return addressState;
    }
    public String getAddressZip() {
        return addressZip;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }
    public String getWebsite() {
        return website;
    }
    public String getEmail() {
        return email;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public String getFB(){
        return fb;
    }
    public String getFBID(){
        return fbID;
    }
    public String getTwitter(){
        return twitter;
    }
    public String getTwitterID(){
        return twitterID;
    }
    public String getYoutube(){
        return youtube;
    }
    public String getYoutubeID(){
        return youtubeID;
    }

    @NonNull
    @Override
    public String toString() {

        try {
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(sw);
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.name("officialPosition").value(getOfficialPosition());
            jsonWriter.name("officialName").value(getOfficialName());
            jsonWriter.name("officialParty").value(getOfficialParty());
            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}