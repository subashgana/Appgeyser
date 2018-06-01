package com.appsgeyser.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.GridView;
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
import com.appsgeyser.Utils.Util;
import com.appsgeyser.activity.MainActivity;
import com.appsgeyser.app.MyApplication;
import com.appsgeyser.helper.SwipeGridAdapter;
import com.appsgeyser.tutorial.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainVideosFragmnet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainVideosFragmnet extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentActivity myContext;
    private static final int ANIMATION_DURATION_MILLIS = 300;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String videoId = null;
    YouTubePlayerSupportFragment youTubePlayerFragment;
    YouTubePlayer YPlayer;


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
    String videoid;
    private OnFragmentInteractionListener mListener;
    ArrayList<String> videomyJsonString;

    private boolean isViewShown = false;

    DetailsVo detailsvo;
    View videoBox;
    View closeButton;


    public MainVideosFragmnet() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {

        if (activity instanceof FragmentActivity) {
            myContext = (FragmentActivity) activity;
        }

        super.onAttach(activity);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainVideosFragmnet.
     */
    // TODO: Rename and change types and number of parameters
    public static MainVideosFragmnet newInstance(String param1, String param2) {
        MainVideosFragmnet fragment = new MainVideosFragmnet();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_photos,
                container, false);
        // setHasOptionsMenu(true);
        //String strtext = getArguments().getString("fragment");
        //  Toast.makeText(getActivity(), strtext, Toast.LENGTH_SHORT).show();
//
        closeButton = view.findViewById(R.id.close_button);
        gridView = (GridView) view.findViewById(R.id.mygridview);
        gridView.setOnItemClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        imageLoader = MyApplication.getInstance().getImageLoader();
        movieList = new ArrayList<>();
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter = new SwipeGridAdapter(getActivity(), movieList, imageLoader);
        gridView.setTextFilterEnabled(true);
        gridView.setAdapter(adapter);

        videoBox = view.findViewById(R.id.video_box);
        closeButton = view.findViewById(R.id.close_button);
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


                                        if (Util.isNetworkAvailable(getActivity())) {
                                            fetchMovies();
                                        } else {
                                            Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_LONG).show();
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
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        videoBox.setMinimumHeight(height/3);
        videoBox.setMinimumWidth(width/3);

        return view;
    }


   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_search_fragment, menu);
        try {
            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String newText) {
                    // do your search
                    //adapter.
                    String text = newText;
                    Toast.makeText(getActivity(),text,Toast.LENGTH_SHORT).show();
                    adapter.filter(text);
                    gridView.setAdapter(adapter);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // do your search on change or save the last string or...
                    String text = newText;
                    Toast.makeText(getActivity(),text,Toast.LENGTH_SHORT).show();
                    adapter.filter(text);
                    return false;

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

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

        if (Util.isNetworkAvailable(getActivity())) {
            fetchMovies();
        } else {
            Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_LONG).show();
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
                                        if (Util.isNetworkAvailable(getActivity())) {
                                            fetchMovies();
                                        } else {
                                            Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_LONG).show();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        videoBox = getActivity().findViewById(R.id.video_box);
        gridView.setChoiceMode(gridView.CHOICE_MODE_SINGLE);
        gridView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        videoid = movieList.get(pos).getId();
        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.youtube_fragment, youTubePlayerFragment, "photos");
        transaction.addToBackStack(null);
        transaction.commit();


        if (Util.isNetworkAvailable(getActivity())) {
            setUserVisibleHint(true);
        } else {
            Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_LONG).show();
        }


        if (videoBox.getVisibility() != View.VISIBLE)

        {
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
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

    @Override
    public void onPause() {
        super.onPause();
        //YPlayer.release();


        ViewPropertyAnimator animator = videoBox.animate()
                .translationYBy(videoBox.getHeight())
                .setDuration(ANIMATION_DURATION_MILLIS);
        runOnAnimationEnd(animator, new Runnable() {
            @Override
            public void run() {
                videoBox.setVisibility(View.INVISIBLE);
            }
        });
        closePlayer();
        if (youTubePlayerFragment != null) {
            getFragmentManager().beginTransaction()
                    .remove(getChildFragmentManager().findFragmentById(R.id.youtube_fragment))
                    .commit();

        }


    }
}
