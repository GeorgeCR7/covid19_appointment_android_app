package com.example.projectcovid_19.models;

import android.os.Parcel;
import android.os.Parcelable;

public class VaccineCenter implements Parcelable {

    private String title;
    private String description;
    private String location;
    private String longitude, latitude;

    public VaccineCenter() {
    }

    public VaccineCenter(String title, String description, String location, String longitude, String latitude) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    protected VaccineCenter(Parcel in) {
        title = in.readString();
        description = in.readString();
        location = in.readString();
        longitude = in.readString();
        latitude = in.readString();
    }

    public static final Creator<VaccineCenter> CREATOR = new Creator<VaccineCenter>() {
        @Override
        public VaccineCenter createFromParcel(Parcel in) {
            return new VaccineCenter(in);
        }

        @Override
        public VaccineCenter[] newArray(int size) {
            return new VaccineCenter[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeString(longitude);
        dest.writeString(latitude);
    }
}
