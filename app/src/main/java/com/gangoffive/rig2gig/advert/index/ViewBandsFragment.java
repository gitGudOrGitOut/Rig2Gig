package com.gangoffive.rig2gig.advert.index;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.details.BandListingDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

public class ViewBandsFragment extends Fragment
{
    private static final String TAG = "ViewBandsFragment";

    private String currentMusicianRef;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentSnapshot lastVisible;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;
    private BandAdapter adapter;

    private ArrayList<BandListing> bandListings;
    private final ArrayList<String> bands = new ArrayList();
    private boolean callingFirebase = false;

    /**
     * Creates an fragment for an index of band adverts.
     * @param inflater The inflater is used to read the passed xml file.
     * @param container The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View v = inflater.inflate(R.layout.fragment_view_bands, container, false);

        final Button noInternet = v.findViewById(R.id.noInternet);

        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        noInternet.setVisibility(isConnected ? View.GONE : View.VISIBLE);

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.viewSwipeContainer);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "get successful with data123213213");
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }

                lastVisible = null;

                ft.detach(ViewBandsFragment.this).attach(ViewBandsFragment.this).commit();
                swipeLayout.setRefreshing(false);
            }
        });
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));

        /*currentMusicianRef = this.getArguments().getString("CURRENT_MUSICIAN_ID"); //this is not passed in the intent
        DocumentReference bandRef = db.collection("musicians").document(currentMusicianRef);
        bandRef.get(source)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Band Query Get successful with data");
                                ArrayList<String> members = (ArrayList<String>) document.get("bands");
                                for(String band : bands) {
                                    bands.add(band);
                                }
                            } else {
                                Log.d(TAG, "Band Query Get successful without data");
                            }
                        } else {
                            Log.d(TAG, "Band Query Get unsuccessful");
                        }
                    }
                });
        while(bands.isEmpty()) {

        }*/

        recyclerView = (RecyclerView) v.findViewById(R.id.viewRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        bandListings = new ArrayList<>();
        adapter = new BandAdapter(bandListings, getContext());
        adapter.setOnItemClickListener(new BandAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent openListingIntent = new Intent(v.getContext(), BandListingDetailsActivity.class);
                String listingRef = bandListings.get(position).getListingRef();
                openListingIntent.putExtra("EXTRA_BAND_LISTING_ID", listingRef);
                startActivityForResult(openListingIntent, 1);
            }
        });
        recyclerView.setAdapter(adapter);
        firebaseCall(source);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(callingFirebase == false) {
                    super.onScrollStateChanged(recyclerView, newState);

                    int offset = recyclerView.computeVerticalScrollOffset();
                    int extent = recyclerView.computeVerticalScrollExtent();
                    int range = recyclerView.computeVerticalScrollRange();

                    float percentage = (100.0f * offset / (float)(range - extent));

                    if(percentage > 75) {
                        firebaseCall(source);
                    }
                }
            }
        });

        return v;
    }

    /**
     * Calls the Firebase database for more adverts, has built in pagenation.
     * @param source
     */
    private void firebaseCall(Source source) {

        callingFirebase = true;

        Query next;
        Timestamp currentDate = Timestamp.now();

        if(lastVisible == null) {
            next = db.collection("band-listings")
                    .whereGreaterThanOrEqualTo("expiry-date",  currentDate)
                    .limit(10);
        } else {
            next = db.collection("band-listings")
                    .whereGreaterThanOrEqualTo("expiry-date",  currentDate)
                    .startAfter(lastVisible)
                    .limit(10);
        }


        next.get(source)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            if(!documentSnapshots.isEmpty())
                            {
                                Log.d(TAG, "get successful with data");

                                for(DocumentSnapshot documentSnapshot : documentSnapshots){

                                    BandListing bandListing = new BandListing(
                                            documentSnapshot.getId(),
                                            documentSnapshot.get("band-ref").toString());

                                    if (!bands.contains(documentSnapshot.get("band-ref").toString())) {
                                        bandListings.add(bandListing);
                                    }

                                    lastVisible = documentSnapshot;
                                }

                                adapter.notifyItemInserted(bandListings.size() - 1);

                                callingFirebase = false;
                            } else {
                                Log.d(TAG, "get successful without data");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    /**
     * After this fragment opens the associated advert activity, when it returns it forces the activity
     * to restart to allow the favourited adverts to updated.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ((BandAdvertIndexActivity)getActivity()).refreshActivity();
    }
}
