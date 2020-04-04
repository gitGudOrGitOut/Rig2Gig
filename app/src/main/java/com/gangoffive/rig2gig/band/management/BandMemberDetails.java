package com.gangoffive.rig2gig.band.management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gangoffive.rig2gig.advert.management.CreateAdvertisement;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.R;

import java.util.Map;

public class BandMemberDetails extends AppCompatActivity implements CreateAdvertisement {

    private int height, width;
    private Button ok;
    private TextView name, userName, location, phone, email, rating;
    private String musicianRef, userRef;
    private ListingManager userManager, musicianManager;
    private Map<String, Object> musician, user;
    private boolean musicianDownloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_member_details);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = (metrics.heightPixels) /100 * 80;
        width = (metrics.widthPixels) /100 * 80;
        getWindow().setLayout(width,height);
        Intent intent = getIntent();
        musicianRef = intent.getStringExtra("EXTRA_MUSICIAN_REF");
        musicianDownloaded = false;
        musicianManager = new ListingManager(musicianRef,"Musician","profileEdit");
        musicianManager.getUserInfo(this);
        ok = findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onSuccessFromDatabase(Map<String, Object> data) {
        if (data != null)
        {
            if (!musicianDownloaded)
            {
                musicianDownloaded = true;
                musician = data;
                userRef = musician.get("user-ref").toString();
                userManager = new ListingManager(userRef,"User","profileEdit");
                userManager.getUserInfo(this);
            }
            else
            {
                user = data;
                setViewReferences();
            }
        }
    }

    @Override
    public void setViewReferences()
    {
        name = findViewById(R.id.name);
        userName = findViewById(R.id.userName);
        location = findViewById(R.id.location);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        rating = findViewById(R.id.rating);
        populateInitialFields();
    }

    @Override
    public void populateInitialFields() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                name.setText(musician.get("name").toString());
                userName.setText(user.get("username").toString());
                location.setText(musician.get("location").toString());
                phone.setText(user.get("phone-number").toString());
                email.setText(user.get("email-address").toString());
                rating.setText(musician.get("rating").toString());
            }
        });
    }

    @Override
    public void createAdvertisement() {

    }

    @Override
    public void cancelAdvertisement() {

    }

    @Override
    public void listingDataMap() {

    }

    @Override
    public boolean validateDataMap() {
        return false;
    }



    @Override
    public void onSuccessFromDatabase(Map<String, Object> data, Map<String, Object> listingData) {

    }

    @Override
    public ImageView getImageView() {
        return null;
    }

    @Override
    public void handleDatabaseResponse(Enum creationResult) {

    }

    @Override
    public void onSuccessfulImageDownload() {

    }
}