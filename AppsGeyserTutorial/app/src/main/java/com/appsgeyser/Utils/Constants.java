package com.appsgeyser.Utils;

/**
 * Created by subash.b on 27-Feb-17.
 */

public class Constants {
    public static String BASIC_URL = "https://www.googleapis.com/youtube/v3/";
    public static String BASE_URL = BASIC_URL+"search?order=date&part=snippet&";
    public static String CHANNEL_ID = "channelId=UC52gUUd3nmamQINPvCmA7cw";
    public static String PAGE_TOKEN = "&pageToken=";
    public static String PLAYLIST_ID = "PLZm7plTpBVTkzFSxhnHqCmBMHV68LBFkk";
    public static String MAIN_URL = BASE_URL+CHANNEL_ID+"&maxResults=50&key="+ DeveloperKey.DEVELOPER_KEY;

    public static String PLAYLIST_URL = BASIC_URL+"playlists?part=snippet&"+CHANNEL_ID+"&key="+ DeveloperKey.DEVELOPER_KEY+"&maxResults=50";
    public static String PLAYLIST_VIDEO_URL = BASIC_URL+"playlistItems?part=snippet&key="+ DeveloperKey.DEVELOPER_KEY+"&maxResults=50";


    public static  String URL_HEADER = "http://www.magic4walls.com/wp-content/uploads/2014/07/hi-tech-applications-window-phone-ios-android-vector-background.jpg";
    public static  String URL_PROFILE = "https://3.bp.blogspot.com/-VKf6iWjFupo/V4eVcopRGSI/AAAAAAAAICU/_V0jp56Fx78Hz6wBaYE6m_DsHi8Q4VXkgCLcB/s640/emmanuela1.jpg";
    public static  String URL_SHARE = "https://play.google.com/store/apps/details?id=com.appsgeyser.tutorial";
   // https://www.youtube.com/channel/UC-j7LP4at37y3uNTdWLq-vQ

    //public static String ADMOB_CHANNEL_ID = "UClUsCEaF7EkbNFLBOwjCK8w";
    // public static String MONETISATION_CHANNEL_ID = "channelId=UCLHibHBWfeKWJwqQpJfCEuA";

}
