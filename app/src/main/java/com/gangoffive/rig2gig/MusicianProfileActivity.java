package com.gangoffive.rig2gig;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MusicianProfileActivity extends AppCompatActivity {

    private String mID;
    private final ArrayList<String> bandArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_profile);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView musicianPhoto = findViewById(R.id.musicianPhoto);
        final TextView musicianName = findViewById(R.id.musicianName);
        final TextView rating = findViewById(R.id.rating);
        final TextView location = findViewById(R.id.location);
        final TextView distance = findViewById(R.id.venue_description_final);
        final TextView bands = findViewById(R.id.bands);

        /*Used to get the id of the musician from the previous activity*/
        mID = getIntent().getStringExtra("EXTRA_MUSICIAN_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        /*Finding the musician by its ID in the "musicians" subfolder*/
        DocumentReference musician = db.collection("musicians").document(mID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        musician.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        musicianName.setText(document.get("name").toString());
                        rating.setText("Rating: " + document.get("rating").toString() + "/5");
                        location.setText(document.get("location").toString());
                        distance.setText("Distance willing to travel: " + document.get("distance").toString() + " miles");
                        bandArray.addAll((ArrayList<String>) document.get("bands"));
                        bands.setText("Bands: " + bandArray.toString().substring(1, bandArray.toString().length()-1));
                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });

        /*Find reference for the photo associated with the listing inside the according subtree*/
        StorageReference musicianPic = storage.getReference().child("/images/musicians/" + mID + ".jpg");

        /*Using Glide to load the picture from the reference directly into the ImageView*/

        GlideApp.with(this)
                .load(musicianPic)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(musicianPhoto);
    }

    /**
     * Overriding the up navigation to call onBackPressed
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listing_menu, menu);

        mID = getIntent().getStringExtra("EXTRA_MUSICIAN_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        /*Finding the listing by its ID in the "performer-listings" subfolder*/
        DocumentReference musician = db.collection("musicians").document(mID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        musician.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        MenuItem star = menu.findItem(R.id.saveButton);
                        star.setVisible(false);

                        if(document.get("user-ref").toString().equals(FirebaseAuth.getInstance().getUid()))
                        {
                            getSupportActionBar().setTitle("My profile");
                        }
                        else
                        {
                            getSupportActionBar().setTitle(document.get("name").toString());
                        }
                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}