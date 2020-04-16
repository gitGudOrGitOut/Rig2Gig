package com.gangoffive.rig2gig.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.gangoffive.rig2gig.R;

public class TermsOfServiceFragment extends Fragment {

    WebView mWebView;

    public TermsOfServiceFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.termsandconditions, container, false);

        mWebView = v.findViewById(R.id.tandcs);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl("file:///android_asset/termsandconditions.html");
        return v;
    }
}
