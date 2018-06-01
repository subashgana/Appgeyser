package com.appsgeyser.fragment;

        import android.animation.Animator;
        import android.animation.AnimatorListenerAdapter;
        import android.annotation.TargetApi;
        import android.app.Activity;
        import android.app.ProgressDialog;
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
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.LayoutInflater;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.ViewPropertyAnimator;
        import android.webkit.WebResourceError;
        import android.webkit.WebResourceRequest;
        import android.webkit.WebView;
        import android.webkit.WebViewClient;
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


public class PlaylistVideosFragment extends Fragment implements  AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentActivity myContext;
    private static final int ANIMATION_DURATION_MILLIS = 300;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


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
    private MainVideosFragmnet.OnFragmentInteractionListener mListener;
    ArrayList<String> videomyJsonString;

    private boolean isViewShown = false;

    DetailsVo detailsvo;
    View videoBox;
    View closeButton;


    public PlaylistVideosFragment() {
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


        View view = inflater.inflate(R.layout.fragment_notifications,
                container, false);
        // setHasOptionsMenu(true);
        //String strtext = getArguments().getString("fragment");
        //  Toast.makeText(getActivity(), strtext, Toast.LENGTH_SHORT).show();
//
        closeButton = view.findViewById(R.id.close_button);
        gridView = (GridView) view.findViewById(R.id.not_gridview);
        gridView.setOnItemClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        imageLoader = MyApplication.getInstance().getImageLoader();
        movieList = new ArrayList<>();
        adapter = new SwipeGridAdapter(getActivity(), movieList, imageLoader);
        gridView.setTextFilterEnabled(true);


        videoBox = view.findViewById(R.id.video_box);
        closeButton = view.findViewById(R.id.close_button);
        videoBox.setVisibility(View.INVISIBLE);

        DetailsVo detailsvo = new DetailsVo();
        detailsvo.setId("FlCBL4TmWME");
        detailsvo.setTitle("Ad mob");

        movieList.add(detailsvo);

        gridView.setAdapter(adapter);


        return view;
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

    /**
     * Fetching movies json by making http call
     */

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
        transaction.replace(R.id.myyoutube_fragment, youTubePlayerFragment, "photos");
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
                    .remove(getChildFragmentManager().findFragmentById(R.id.myyoutube_fragment))
                    .commit();

        }


    }
}