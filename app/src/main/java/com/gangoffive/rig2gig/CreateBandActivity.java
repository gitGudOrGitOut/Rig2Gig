package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateBandActivity extends Activity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    EditText cBandName, cBandLocation, cBandDistance, cBandGenres, cBandEmail, cBandPhoneNumber;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_band);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        cBandName = findViewById(R.id.BandName);
        cBandLocation = findViewById(R.id.bandLocation);
        cBandDistance = findViewById(R.id.bandDistance);
        cBandGenres = findViewById(R.id.bandGenres);
        cBandEmail =findViewById(R.id.bandEmail);
        cBandPhoneNumber = findViewById(R.id.bandPhoneNumber);
    }

    /**
     *
     * @param view
     */
    public void createBandBtnOnClick(View view) {
        final String bandUUID = UUID.randomUUID().toString();
        final String bandName = cBandName.getText().toString();
        final String bandLocation = cBandLocation.getText().toString().trim();
        final String bandDistance = cBandDistance.getText().toString().trim();
        final String bandGenres = cBandGenres.getText().toString().trim();
        final String bandEmail = cBandEmail.getText().toString().trim();
        final String bandPhoneNumber = cBandPhoneNumber.getText().toString().trim();
        final String userID = fAuth.getUid();

        if (TextUtils.isEmpty(bandName)){
            cBandName.setError("Band Name Is Required!");
        }
        if (TextUtils.isEmpty(bandLocation)){
            cBandLocation.setError("Band Location Is Required!");
        }
        if (TextUtils.isEmpty(bandDistance)){
            cBandDistance.setError("Distance Is Required!");
        }
        if (TextUtils.isEmpty(bandGenres)){
            cBandGenres.setError("Genres Is Required!");
        }
        if (TextUtils.isEmpty(bandEmail)){
            cBandEmail.setError("Band Email Is Required!");
        }
        if (TextUtils.isEmpty(bandPhoneNumber)){
            cBandPhoneNumber.setError("Band Phone Number Is Required!");
        }

        DocumentReference documentReference = fStore.collection("band").document(bandUUID);
        Map<String, Object> band = new HashMap<>();
        band.put("Band Name", bandName);
        band.put("Band Location", bandLocation);
        band.put("Distance", bandDistance);
        band.put("Genres", bandGenres);
        band.put("Band Email Address", bandEmail);
        band.put("Band Phone Number", bandPhoneNumber);
        band.put("UserID", userID);
        documentReference.set(band).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreateBandActivity.this, "Band has been created", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateBandActivity.this, "Error creating band", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
