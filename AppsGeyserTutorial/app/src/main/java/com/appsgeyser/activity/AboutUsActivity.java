package com.appsgeyser.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appsgeyser.Utils.Util;
import com.appsgeyser.tutorial.R;


public class AboutUsActivity extends AppCompatActivity {
    TextView tv_powerd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);


        if (Util.isNetworkAvailable(this)) {
/*
            mInterstitialAd = new InterstitialAd(this);

            // set the ad unit ID
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen_aboutus));

            AdRequest adRequest = new AdRequest.Builder()
                    .build();

            // Load ads into Interstitial Ads
            mInterstitialAd.loadAd(adRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });*/
        } else {
            Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_LONG).show();
        }
        tv_powerd = (TextView) findViewById(R.id.tv_powerdby);
        tv_powerd.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tv_powerd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"subashmoto@gmail.com", "cartoonnetwork.hindusthani@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Developing a Android app");
                email.putExtra(Intent.EXTRA_TEXT, "Hi, I need to develop a android app and make money using Admob");
                email.setData(Uri.parse("subashdefy@gmail.com"));
                email.setType("message/rfc822");
                startActivity(email);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

   /* private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }*/
}
