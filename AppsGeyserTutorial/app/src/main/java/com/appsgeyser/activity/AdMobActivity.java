package com.appsgeyser.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.appsgeyser.Utils.Util;
import com.appsgeyser.tutorial.R;

import im.delight.android.webview.AdvancedWebView;

/**
 * Created by subash.b on 4/7/2017.
 */

public class AdMobActivity extends AppCompatActivity {
    private ProgressDialog mProgress;

    //private Button button;
    private AdvancedWebView webView;
    private static final int PERMISSION_REQUEST_CODE = 1;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_web_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get webview
        webView = (AdvancedWebView) findViewById(R.id.webView1);
        if (Util.isNetworkAvailable(this)) {
         /*   mInterstitialAd = new InterstitialAd(this);

            // set the ad unit ID
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen_createnow));

            AdRequest adRequest = new AdRequest.Builder()
                    .build();

            // Load ads into Interstitial Ads
            mInterstitialAd.loadAd(adRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
*/

            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    startWebView("https://gateway.genpact.com/siteminderagent/forms/login.fcc?TYPE=33554433&REALMOID=06-cf39b48d-3006-1066-8848-84f994900000&GUID=&SMAUTHREASON=0&METHOD=GET&SMAGENTNAME=$SM$S3f1nS9E59Yrdiy2GCZwgZTkD8UMKjKn3RWM%2bIZb7AWHkbeT1GVit73v5Wyvn6LR&TARGET=$SM$HTTPS%3a%2f%2fgateway%2egenpact%2ecom%2faffwebservices%2fpublic%2fsaml2sso%3fSAMLRequest%3dhVLRTttAEPwV6$%2Fps39mlhZzioJQIZAkiWkKhfTuf185J9p3rXeO4X99zIAgqAa$%2BrmZ3ZmZ2f7po6eIAOjbMpiyPBArDaFcZWKbvdnIcnLEBStlC1s5CyEZCdLuaomrqVy5629gf86QEp8IssygKNJ26JWsn5MAzR8DlyXcUTIQQXM$%2B5RE$%2BQTe8J7sTfgMRdHE9wjntGTbMr6zkqn0KC0qgGUpOXN8upSJpGQChE68se8pLTvc9rOkdOuPlB2X4T4kFGa2g8VUWfynkDee9LzAjRvHnV$%2FdXmjt9Co0NgpWA0syFYpM0W4MS6Jx9xlf4$%2FdclZBeB2eXdydwPq7rnYX336N$%2BVqFId2x4OehsGQqLEPsIdtvIz8S8XEovoZitokTmRzJWPxmwcpXZKyiPWuyht5bpQgGNUYV2FZpirRruCrLAXIf4oPRgLzt89poPqWYIDr2WL3cS3bBuesaRe9nNU38ceUe6vsmQyNbHCwo7VUQdvTKhFMNL6GY8xdih6db$%2B33Z6tp5W$%2BOHBuIo$%2Fs9Ab7EFbUoDBQuWde2Gsw58DimjrgfGF4$%2Bir5978Q8$%3D%26RelayState%3did-JB-VhVqr377n7La3cgYztztVnPle2mucXcvEcjfe%26SigAlg%3dhttp$%3A$%2F$%2Fwww%2ew3%2eorg$%2F2000$%2F09$%2Fxmldsig$%23rsa-sha1%26Signature%3dLW9xxROHgCUYoK5qilXZahss$%2ByQjbTiPUA1RZzDB2zu7R6$%2Firhf4eGGaiwxYf3DE8yvtFaI91M22ZJVT8Sak$%2FLL05W51y$%2FYmXrDZCeN2FDsflSAj9IllTsxEyIdatJKWvpKxDH2VSiSj$%2BkJ9IcVlIAZycmMgwwFceYNtcbvMCnQWnjYTv2y$%2BueDfbLfdrfp0oSpEqLrlF3ERNTAT91rLwkdlmK8XksVRPtwUyxKaz1YvRFW8cFlCWWhpB4Zp$%2FPRYhj0QX9$%2BBvfXsEiQgDe1zHDuaFsLUne$%2B5U$%2BdSQ48m$%2FKDfCro9J2pRqtioEr$%2BRv3Ovq$%2FPQzjxdeceRxYFZh$%2BDnIw$%3D$%3D");
                    // Code for above or equal 23 API Oriented Device
                    // Your Permission granted already .Do next code
                } else {
                    requestPermission(); // Code for permission
                }
            } else {
                startWebView("https://gateway.genpact.com/siteminderagent/forms/login.fcc?TYPE=33554433&REALMOID=06-cf39b48d-3006-1066-8848-84f994900000&GUID=&SMAUTHREASON=0&METHOD=GET&SMAGENTNAME=$SM$S3f1nS9E59Yrdiy2GCZwgZTkD8UMKjKn3RWM%2bIZb7AWHkbeT1GVit73v5Wyvn6LR&TARGET=$SM$HTTPS%3a%2f%2fgateway%2egenpact%2ecom%2faffwebservices%2fpublic%2fsaml2sso%3fSAMLRequest%3dhVLRTttAEPwV6$%2Fps39mlhZzioJQIZAkiWkKhfTuf185J9p3rXeO4X99zIAgqAa$%2BrmZ3ZmZ2f7po6eIAOjbMpiyPBArDaFcZWKbvdnIcnLEBStlC1s5CyEZCdLuaomrqVy5629gf86QEp8IssygKNJ26JWsn5MAzR8DlyXcUTIQQXM$%2B5RE$%2BQTe8J7sTfgMRdHE9wjntGTbMr6zkqn0KC0qgGUpOXN8upSJpGQChE68se8pLTvc9rOkdOuPlB2X4T4kFGa2g8VUWfynkDee9LzAjRvHnV$%2FdXmjt9Co0NgpWA0syFYpM0W4MS6Jx9xlf4$%2FdclZBeB2eXdydwPq7rnYX336N$%2BVqFId2x4OehsGQqLEPsIdtvIz8S8XEovoZitokTmRzJWPxmwcpXZKyiPWuyht5bpQgGNUYV2FZpirRruCrLAXIf4oPRgLzt89poPqWYIDr2WL3cS3bBuesaRe9nNU38ceUe6vsmQyNbHCwo7VUQdvTKhFMNL6GY8xdih6db$%2B33Z6tp5W$%2BOHBuIo$%2Fs9Ab7EFbUoDBQuWde2Gsw58DimjrgfGF4$%2Bir5978Q8$%3D%26RelayState%3did-JB-VhVqr377n7La3cgYztztVnPle2mucXcvEcjfe%26SigAlg%3dhttp$%3A$%2F$%2Fwww%2ew3%2eorg$%2F2000$%2F09$%2Fxmldsig$%23rsa-sha1%26Signature%3dLW9xxROHgCUYoK5qilXZahss$%2ByQjbTiPUA1RZzDB2zu7R6$%2Firhf4eGGaiwxYf3DE8yvtFaI91M22ZJVT8Sak$%2FLL05W51y$%2FYmXrDZCeN2FDsflSAj9IllTsxEyIdatJKWvpKxDH2VSiSj$%2BkJ9IcVlIAZycmMgwwFceYNtcbvMCnQWnjYTv2y$%2BueDfbLfdrfp0oSpEqLrlF3ERNTAT91rLwkdlmK8XksVRPtwUyxKaz1YvRFW8cFlCWWhpB4Zp$%2FPRYhj0QX9$%2BBvfXsEiQgDe1zHDuaFsLUne$%2B5U$%2BdSQ48m$%2FKDfCro9J2pRqtioEr$%2BRv3Ovq$%2FPQzjxdeceRxYFZh$%2BDnIw$%3D$%3D");

                // Code for Below 23 API Oriented Device
                // Do next code
            }

        } else {
            Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(AdMobActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(AdMobActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showSystemDialog();



        } else {
            ActivityCompat.requestPermissions(AdMobActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    void showSystemDialog() {

        AlertDialog.Builder alertDialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(AdMobActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            alertDialog = new AlertDialog.Builder(AdMobActivity.this);
        }


        // Setting Dialog Title

        // Setting Dialog Message
        alertDialog.setMessage("You need to enable permissions for storage to download APK ?");

        // Setting Icon to Dialog

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                // Write your code here to invoke YES event
            }
        });


        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event

                dialog.cancel();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link


        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (mProgress == null) {
                    mProgress = new ProgressDialog(AdMobActivity.this);

                    if(!((Activity) AdMobActivity.this).isFinishing())
                    {
                        //show dialog
                        mProgress.show();
                    }

                }
                mProgress.setMessage("Loading " + String.valueOf(progress) + "%");
                if (progress == 100) {
                    mProgress.dismiss();
                    mProgress = null;
                }
            }
        });
        // Other webview options
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(url);
        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "apk ");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });
    }


    // Open previous opened link from history on webview when back button pressed

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
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

  /*  private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }*/
}
