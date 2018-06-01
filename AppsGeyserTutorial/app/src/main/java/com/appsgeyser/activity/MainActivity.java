package com.appsgeyser.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appsgeyser.Utils.Constants;
import com.appsgeyser.Utils.DetailsVo;
import com.appsgeyser.Utils.DeveloperKey;
import com.appsgeyser.Utils.NotificationUtils;
import com.appsgeyser.Utils.Util;
import com.appsgeyser.app.Config;
import com.appsgeyser.app.MyApplication;
import com.appsgeyser.fragment.HomeFragment;
import com.appsgeyser.fragment.MainVideosFragmnet;
import com.appsgeyser.helper.SwipeGridAdapter;
import com.appsgeyser.other.CircleTransform;
import com.appsgeyser.tutorial.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg;
    //private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    //private FloatingActionButton fab;
    private static final int ANIMATION_DURATION_MILLIS = 300;
    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = Constants.URL_HEADER;
    private static final String urlProfileImg = Constants.URL_PROFILE;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

 /*   private AdView mAdView;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest;*/


    String videoId = null;
    YouTubePlayerSupportFragment youTubePlayerFragment;
    YouTubePlayer YPlayer;
    String videoid = null;

    private String TAG = MainActivity.class.getSimpleName();

    private String URL_TOP_250 = Constants.MAIN_URL;

    private SwipeRefreshLayout swipeRefreshLayout;
    private GridView gridView;
    SwipeGridAdapter adapter;
    private List<DetailsVo> movieList;
    private String nextpageToken = " ";
    ImageLoader imageLoader;
    // initially offset will be 0, later will be updated while parsing the json
    private String myPageToken = null;
    int scrollSize = 0;
    YouTubePlayerView youTubeView;
    private MainVideosFragmnet.OnFragmentInteractionListener mListener;
    ArrayList<String> videomyJsonString;

    private boolean isViewShown = false;

    DetailsVo detailsvo;
    View videoBox;
    View closeButton;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


      //  mAdView = (AdView) findViewById(R.id.adView);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    //txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();
     /*   mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen_mainactivity));

        adRequest = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });
        mAdView.loadAd(adRequest);*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        // fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        // txtName = (TextView) navHeader.findViewById(R.id.name);
        //txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        //imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);


      /*  fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, Constants.URL_SHARE);
                startActivity(Intent.createChooser(shareIntent, "share Action"));
              *//*  Snackbar.make(view, "Pull down the screen for more videos", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*//*
            }
        });*/

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        closeButton = findViewById(R.id.close_button);
        gridView = (GridView) findViewById(R.id.mygridview);
        gridView.setOnItemClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        imageLoader = MyApplication.getInstance().getImageLoader();
        movieList = new ArrayList<>();
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter = new SwipeGridAdapter(this, movieList, imageLoader);
        gridView.setTextFilterEnabled(true);
        gridView.setAdapter(adapter);

        videoBox = findViewById(R.id.video_box);
        closeButton = findViewById(R.id.close_button);
        videoBox.setVisibility(View.INVISIBLE);
        // youTubeView = (YouTubePlayerView) view.findViewById(R.id.youtube_view);
        //videoFragment = (VideoFragment) getActivity().getFragmentManager().findFragmentById(R.id.video_fragment_container);


        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);


                                        if (Util.isNetworkAvailable(MainActivity.this)) {
                                            fetchMovies();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
        );

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePlayer();

            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        videoBox.setMinimumHeight(height / 3);
        videoBox.setMinimumWidth(width / 3);


        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
        } else {

            // Code for Below 23 API Oriented Device
            // Do next code
        }


     /*   if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }*/


    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showSystemDialog();

            // Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store APK. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {

                    showSystemDialog();
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
/*

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
*/


    void showSystemDialog() {

        AlertDialog.Builder alertDialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            alertDialog = new AlertDialog.Builder(MainActivity.this);
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

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        /*txtName.setText("Village Food Factory");
        txtWebsite.setText("My Dady's Kitchen");*/

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
     /*   Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);
*/
        // showing dot next to notifications label
        // navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            //toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app

        // show or hide the fab button
        // toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        Bundle bundle = new Bundle();
        switch (navItemIndex) {
            case 0:
                // home
                if (Util.isNetworkAvailable(getApplicationContext())) {
                    HomeFragment homeFragment = new HomeFragment();

                    bundle.putString("fragment", "HomeFragment");
                    //set Fragmentclass Arguments
                    homeFragment.setArguments(bundle);
                    return homeFragment;

                } else {
                    Toast.makeText(getApplicationContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
                }

            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                 /*   case R.id.nav_photos:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PHOTOS;
                        break;
                    case R.id.nav_movies:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;*/
                   /* case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;*/
                    case R.id.nav_create:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, CreateNow.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.navi_signin_dev:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, GoogleDevloperSignin.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.navi_admob:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AdMobActivity.class));
                        drawer.closeDrawers();
                        return true;

                 /*   case R.id.nav_admobtutorial:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AdmobTutorial.class));
                        drawer.closeDrawers();
                        return true;*/
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                       /* startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();*/

                        if (Util.isNetworkAvailable(getApplicationContext())) {
                            Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_SHARE));
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
                        }
                        return true;
/*
                    case R.id.nav_blog:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, TabBlogApp.class));
                        drawer.closeDrawers();
                        return true;*/

                    case R.id.nav_fb:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, TabFacebookApp.class));
                        drawer.closeDrawers();
                        return true;
                   /* case R.id.nav_powered_by:
                        // launch new intent instead of loading fragment
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"subashdefy@gmail.com","cartoonnetwork.hindusthani@gmail.com"});
                        email.putExtra(Intent.EXTRA_SUBJECT, "Developing a Android app");
                        email.putExtra(Intent.EXTRA_TEXT, "Hi, I need to develop a android app and make money using Admob");
                        email.setData(Uri.parse("subashdefy@gmail.com"));
                        email.setType("message/rfc822");
                        startActivity(email);
                        return true;*/
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setVisible(true);
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    /* @Override
     public void onBackPressed() {
         // This code loads home fragment when back key is pressed
         // when user is in other fragment than home
         if (shouldLoadHomeFragOnBackPress) {
             // checking if user is on other navigation menu
             // rather than home
             if (navItemIndex != 0) {
                 navItemIndex = 0;
                 CURRENT_TAG = TAG_HOME;
                 loadHomeFragment();
                 return;
             }
         }

         super.onBackPressed();
     }
 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code

            AlertDialog.Builder alertDialog;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                alertDialog = new AlertDialog.Builder(MainActivity.this);
            }


            // Setting Dialog Title
            alertDialog.setTitle("Exit...");

            // Setting Dialog Message
            alertDialog.setMessage("Are you sure you want Exit this?");

            // Setting Icon to Dialog

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    System.exit(0);
                    finish();
                    // Write your code here to invoke YES event
                }
            });


            alertDialog.setNeutralButton("Rate app", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    if (Util.isNetworkAvailable(getApplicationContext())) {
                        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_SHARE));
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
                    }
                    dialog.cancel();
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
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            // Toast.makeText(getApplicationContext(), "share to", Toast.LENGTH_LONG).show();

            if (Util.isNetworkAvailable(getApplicationContext())) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, Constants.URL_SHARE);
                startActivity(Intent.createChooser(shareIntent, "share Action"));
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.action_rate) {
            // Toast.makeText(getApplicationContext(), "share to", Toast.LENGTH_LONG).show();

            if (Util.isNetworkAvailable(getApplicationContext())) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_SHARE));
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
            }

        }
        // user is in notifications fragment
        // and selected 'Mark all as Read'

        return super.onOptionsItemSelected(item);
    }

    private void closePlayer() {
        gridView.clearChoices();
        gridView.requestLayout();
        try {
            if (YPlayer.isPlaying()) {
                YPlayer.release();
            }
        } catch (Exception e) {

        }
        ViewPropertyAnimator animator = videoBox.animate()
                .translationYBy(videoBox.getHeight())
                .setDuration(ANIMATION_DURATION_MILLIS);
        runOnAnimationEnd(animator, new Runnable() {
            @Override
            public void run() {
                videoBox.setVisibility(View.INVISIBLE);
            }
        });
    }


    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {

        if (Util.isNetworkAvailable(MainActivity.this)) {
            fetchMovies();
        } else {
            Toast.makeText(getApplicationContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Fetching movies json by making http call
     */
    private void fetchMovies() {
        scrollSize = scrollSize + 49;
        Log.d(TAG, myPageToken + "");
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        String url;
        if (myPageToken != null) {
            url = URL_TOP_250 + Constants.PAGE_TOKEN + myPageToken;
        } else {

            url = URL_TOP_250;
        }
        Log.d(TAG, url);
        // Volley's json array request object
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                swipeRefreshLayout.setRefreshing(true);
                ArrayList<String> myJsonString = new ArrayList<String>();
                myJsonString.add(response.toString());

                videomyJsonString = new ArrayList<String>();

                for (String jsonString : myJsonString) {
                    try {
                        if (nextpageToken != null) {
                            JSONObject obj = new JSONObject(jsonString);
                            if (obj.has("nextPageToken")) {
                                nextpageToken = obj.optString("nextPageToken");

                            } else {

                                nextpageToken = null;
                            }

                            if (nextpageToken != myPageToken) {
                                myPageToken = nextpageToken;
                            }
                            JSONArray jsonObj = obj.getJSONArray("items");
                            final int numberOfItemsInResp = jsonObj.length();
                            detailsvo = null;
                            // videoId = null;
                            //playlistid  = null;
                            for (int j = 0; j < numberOfItemsInResp; j++) {
                                detailsvo = new DetailsVo();
                                String str_etag = jsonObj.getJSONObject(j).optString("etag");
                                String kind = jsonObj.getJSONObject(j).optJSONObject("id").optString("kind");
                                if (kind.equals("youtube#video")) {
                                    videoId = jsonObj.getJSONObject(j).optJSONObject("id").optString("videoId");
                                } else {
                                    videoId = null;
                                }
                                if (videoId != null) {
                                    String titleStr = jsonObj.getJSONObject(j).optJSONObject("snippet").optString("title");
                                    JSONObject thumbnailobj = jsonObj.getJSONObject(j).optJSONObject("snippet").getJSONObject("thumbnails");
                                    JSONObject imageobj = thumbnailobj.optJSONObject("medium");
                                    String url = imageobj.optString("url");
                                    if (str_etag != null) {
                                        if (titleStr != null && url != null) {
                                            detailsvo.setId(videoId);
                                            detailsvo.setTitle(titleStr);
                                            detailsvo.setmUrl(url);
                                            movieList.add(detailsvo);
                                            ;
                                        }
                                    }
                                }
                              /*  String urlvideo = "https://www.googleapis.com/youtube/v3/videos?id=" + videoId + "&part=contentDetails&key=" + DeveloperKey.DEVELOPER_KEY;
                                JsonObjectRequest jsonObjReqnew = new JsonObjectRequest(Request.Method.GET,
                                        urlvideo, null, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        videomyJsonString.add(response.toString());
                                        for (String jsonString : videomyJsonString) {

                                            try {

                                                JSONObject obj = new JSONObject(jsonString);

                                                JSONArray jsonObj = obj.getJSONArray("items");

                                                for (int k = 0; k < jsonObj.length(); k++) {

                                                    JSONObject thumbnailobj = jsonObj.getJSONObject(k).optJSONObject("contentDetails");
*//*
                                                    Duration dur = DatatypeFactory.newInstance().newDuration(thumbnailobj.optString("duration"));
                                                    int min = dur.getMinutes(); // Should return 5
                                                    detailsvo.setDuration(String.valueOf(min));*//*
                                                    // Duration.parse("P98DT01H23M45S").toMillis();

                                                    Log.d("duration", thumbnailobj.optString("duration"));

                                                }

                                            } catch (Exception e) {


                                            }
                                        }


                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                                        Toast.makeText(getActivity(),
                                                error.getMessage(), Toast.LENGTH_SHORT).show();
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                });
*/
                                //Collections.reverse(movieList);

                                // MyApplication.getInstance().addToRequestQueue(jsonObjReqnew);
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();

                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
     /*   if (movieList.size() > scrollSize) {
            gridView.setSelection(scrollSize);
        }*/

        try {
            gridView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (movieList.size() > 0) {

                            if (gridView.getLastVisiblePosition() == gridView.getAdapter().getCount() - 1 &&
                                    gridView.getChildAt(gridView.getChildCount() - 1).getBottom() <= gridView.getHeight()) {

                                swipeRefreshLayout.setRefreshing(true);

                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Util.isNetworkAvailable(getApplicationContext())) {
                                            fetchMovies();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }, 1000);

                            }
                        }
                    }
                    return false;
                }
            });


        } catch (Exception e) {

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @TargetApi(16)
    private void runOnAnimationEnd(ViewPropertyAnimator animator, final Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            animator.withEndAction(runnable);
        } else {
            animator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    runnable.run();
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        videoid = movieList.get(pos).getId();
        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.youtube_fragment, youTubePlayerFragment, "photos");
        transaction.addToBackStack(null);
        transaction.commit();
        if (Util.isNetworkAvailable(getApplicationContext())) {
            setUserVisibleHint(true);
        } else {
            Toast.makeText(getApplicationContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
        }


        if (videoBox.getVisibility() != View.VISIBLE) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Initially translate off the screen so that it can be animated in from below.
                videoBox.setTranslationY(videoBox.getHeight());
            }
            videoBox.setVisibility(View.VISIBLE);
        }

        // If the fragment is off the screen, we animate it in.
        if (videoBox.getTranslationY() > 0)

        {
            videoBox.animate().translationY(0).setDuration(ANIMATION_DURATION_MILLIS);
        }

    }


    public void setUserVisibleHint(boolean isVisibleToUser) {
        youTubePlayerFragment.setUserVisibleHint(true);
        if (!isVisibleToUser && YPlayer != null) {
            YPlayer.release();
        }
        if (isVisibleToUser && youTubePlayerFragment != null) {
            youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {


                    if (!wasRestored) {
                        YPlayer = youTubePlayer;
                        // YPlayer.setFullscreen(true);
                        YPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                        //YPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                        YPlayer.setShowFullscreenButton(false);
                        YPlayer.loadVideo(videoid);
                        YPlayer.play();
                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
        }
    }


    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

      /*  if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}
