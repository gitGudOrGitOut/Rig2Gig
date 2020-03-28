package com.gangoffive.rig2gig;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BandProfileActivity extends AppCompatActivity {

    private String mID;
    private final ArrayList<String> memberArray = new ArrayList<>();
    private Button rateMeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_profile);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView bandPhoto = findViewById(R.id.bandPhoto);
        final TextView bandName = findViewById(R.id.bandName);
        final TextView rating = findViewById(R.id.rating);
        final TextView location = findViewById(R.id.location);
        final TextView description = findViewById(R.id.description);
        final TextView members = findViewById(R.id.members);

        /*Used to get the id of the band from the previous activity*/
        mID = getIntent().getStringExtra("EXTRA_BAND_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        /*Finding the band by its ID in the "bands" subfolder*/
        DocumentReference band = db.collection("bands").document(mID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        band.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        bandName.setText(document.get("name").toString());
                        rating.setText("Rating: " + document.get("rating").toString() + "/5");
                        location.setText(document.get("location").toString());
                        description.setText(document.get("description").toString());
                        memberArray.addAll((ArrayList<String>) document.get("members"));
                        members.setText("Members: ");

                        for(String member : memberArray)
                        {
                            db.collection("musicians").document(member)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists())
                                        {
                                            members.setText(members.getText().equals("Members: ") ?
                                                    "Members: " + document.get("name").toString() :
                                                    members.getText() + ", " + document.get("name").toString());
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });

        /*Find reference for the photo associated with the listing inside the according subtree*/
        StorageReference bandPic = storage.getReference().child("/images/bands/" + mID + ".jpg");

        /*Using Glide to load the picture from the reference directly into the ImageView*/

        GlideApp.with(this)
                .load(bandPic)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(bandPhoto);

        rateMeButton = findViewById(R.id.ratingBtn);

        //setupRatingDialog();
        //checkAlreadyRated();
    }

    /**
     * This method is used to set up the rating dialog for users if they have not rated a Musician yet.
     */
    private void setupRatingDialog()
    {
        rateMeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
    }

    /**
     * This method is used to check whether or not the user viewing the Musician has already submitted a rating.
     */
    private void checkAlreadyRated()
    {
        //Use mID global variable to get the correct Musician Document from the Musician Collection in Firebase
        //Then create check the "Already Rated" String[] to see if the logged in user has already submitted a rating.
        //If they have, do not call setupRatingDialog() and replace the rating button on layout with appropriate text.
        //Else if they haven't, call setupRatingDialog() to create the necessary steps for the user to rate this Musician.
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

        AtomicBoolean currentUserInBand = new AtomicBoolean(false);

        mID = getIntent().getStringExtra("EXTRA_BAND_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        /*Finding the listing by its ID in the "performer-listings" subfolder*/
        DocumentReference band = db.collection("bands").document(mID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        band.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                        MenuItem star = menu.findItem(R.id.saveButton);
                        star.setVisible(false);
                        getSupportActionBar().setTitle(document.get("name").toString());
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
