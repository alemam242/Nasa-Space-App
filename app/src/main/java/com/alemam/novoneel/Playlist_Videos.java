package com.alemam.novoneel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class Playlist_Videos extends AppCompatActivity {

    LinearLayout videoDisplay;
    TextView videoTitle;
    ImageView backButton;
    WebView webView;
    GridView gridView;
    String playlistID,totalVideo;
    HashMap<String,String> hashMap;
    ArrayList<HashMap<String,String>> arrayList;

    String embed,ext,url;
RequestQueue queue;

    public interface PlaylistCallback {
        void onPlaylistsReceived(ArrayList<HashMap<String, String>> playlist);
        void onPlaylistsError(String errorMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_videos);

        backButton = findViewById(R.id.backButton);
        webView = findViewById(R.id.webView);
        gridView = findViewById(R.id.gridView);
        videoDisplay = findViewById(R.id.videoDisplay);
        videoTitle = findViewById(R.id.videoTitle);

        Bundle bundle = getIntent().getExtras();
        playlistID = bundle.getString("playlistID");
        totalVideo = bundle.getString("totalVideo");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getVideos(new PlaylistCallback(){

            @Override
            public void onPlaylistsReceived(ArrayList<HashMap<String, String>> playlist) {
                arrayList = new ArrayList<>();
                arrayList = (ArrayList<HashMap<String, String>>) playlist.clone();

                Log.d("videos", "arraylist size: "+arrayList.size());

                MyAdapter adapter = new MyAdapter();
                gridView.setAdapter(adapter);
            }

            @Override
            public void onPlaylistsError(String errorMessage) {
                Log.e("response", "Failed to fetch playlist: " + errorMessage);
            }
        });
    }


    private void playVideo(String id,String title){
        videoDisplay.setVisibility(View.VISIBLE);

        embed="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/";
        ext = "\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        url = embed+id+ext;

        videoTitle.setText(title);

        webView.loadData(url,"text/html","utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new MyChrome());
    }

    private class MyAdapter extends BaseAdapter{
        private int selectedItem = -1; // Initialize with -1 to indicate no selection initially
        LinearLayout viewItem;
        ImageView thumbnail;
        TextView title,description;
        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            View myView = inflater.inflate(R.layout.recent_news_layout,viewGroup,false);

            viewItem = myView.findViewById(R.id.viewItem);
            thumbnail = myView.findViewById(R.id.thumbnail);
            title = myView.findViewById(R.id.title);
            description = myView.findViewById(R.id.description);


            hashMap = new HashMap<>();
            hashMap = arrayList.get(i);

            String videoID = hashMap.get("videoID");
            String VideoTitle = hashMap.get("title");

            title.setText(hashMap.get("title"));
            description.setText(hashMap.get("desc"));

            Picasso.get()
                    .load(hashMap.get("thumbnail"))
                    .placeholder(R.drawable.placeholder)
                    .into(thumbnail);

            // Check if the current item is selected and update its background color
            if (i == selectedItem) {
//                viewItem.setBackgroundColor(Color.GRAY);
                viewItem.setBackgroundResource(R.drawable.background_img);
            } else {
//                viewItem.setBackgroundColor(ContextCompat.getColor(Playlist_Videos.this, R.color.primary));
                viewItem.setBackgroundResource(R.drawable.background_bkg);
            }


            viewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Update the selected item index
                    selectedItem = i;
                    // Notify the adapter that the data set has changed
                    notifyDataSetChanged();
                    //
                    playVideo(videoID,VideoTitle);
                }
            });



            return myView;
        }
    }

    private void getVideos(PlaylistCallback callback){
        arrayList = new ArrayList<>();

//        Toast.makeText(this, ""+playlistID, Toast.LENGTH_SHORT).show();

        String url="https://youtube.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&maxResults=50" +
                "&playlistId="+playlistID+"&key=AIzaSyBzCuzo9Hrs-IPmJYwq3F76YWueqTslC0o";

        int nm = Integer.parseInt(totalVideo);
        double div = Math.ceil((double)nm/50);
        int loopCount = (int)div-1;

        ShowAlert alert = new ShowAlert();
        alert.showAlert("loading");

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String nextPageToken=null;

                    if(response.has("nextPageToken")){
                        nextPageToken = response.optString("nextPageToken",null);
                    }

                    PageToken token = new PageToken(nextPageToken);

                    JSONArray items = response.getJSONArray("items");

                    for(int i=0;i<items.length();i++){
                        JSONObject subItems = items.getJSONObject(i);

                        JSONObject snippet = subItems.getJSONObject("snippet");
                        String title = snippet.getString("title");
                        String desc = snippet.getString("description");

                        if((snippet.has("thumbnails") && !snippet.isNull("thumbnails")) || !title.contains("Private video")) {

                            JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                            JSONObject thumbnailType;
                            if (thumbnails.has("default")) {
                                thumbnailType = thumbnails.getJSONObject("default");
                            } else if (thumbnails.has("medium")) {
                                thumbnailType = thumbnails.getJSONObject("medium");
                            } else if (thumbnails.has("high")) {
                                thumbnailType = thumbnails.getJSONObject("high");
                            } else if (thumbnails.has("standard")) {
                                thumbnailType = thumbnails.getJSONObject("standard");
                            } else if (thumbnails.has("maxres")) {
                                thumbnailType = thumbnails.getJSONObject("maxres");
                            } else {
                                thumbnailType = null;
                            }

                            String thumbnailURL = thumbnailType.getString("url");

                            JSONObject contentDetails = subItems.getJSONObject("contentDetails");
                            String videoID = contentDetails.getString("videoId");

                            Log.d("list", title);
                            Log.d("list", desc);
                            Log.d("list", thumbnailURL);
                            Log.d("list", videoID);

                            hashMap = new HashMap<>();
                            hashMap.put("title", title);
                            hashMap.put("desc", desc);
                            hashMap.put("thumbnail", thumbnailURL);
                            hashMap.put("videoID", videoID);

                            arrayList.add(hashMap);
                        }
                        else {
                            continue;
                        }
                    } // end of for loop


                    if(loopCount>1){
                        // Others Page Request
                        for(int j=0;j<loopCount;j++)
                        {
                            String newURL = url+"&pageToken="+nextPageToken;
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, newURL, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String nextPageToken=null;

                                        if(response.has("nextPageToken")){
                                            nextPageToken = response.optString("nextPageToken",null);
                                        }
                                        token.setToken(nextPageToken);

                                        Log.d("list",nextPageToken);

                                        JSONArray items = response.getJSONArray("items");


                                        for(int i=0;i<items.length();i++){
                                            JSONObject subItems = items.getJSONObject(i);

                                            JSONObject snippet = subItems.getJSONObject("snippet");
                                            String title = snippet.getString("title");
                                            String desc = snippet.getString("description");


                                            if((snippet.has("thumbnails") && !snippet.isNull("thumbnails")) || !title.contains("Private video")) {

                                                JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                                JSONObject thumbnailType = null;

                                                if (thumbnails.has("default")) {
                                                    thumbnailType = thumbnails.getJSONObject("default");
                                                } else if (thumbnails.has("medium")) {
                                                    thumbnailType = thumbnails.getJSONObject("medium");
                                                } else if (thumbnails.has("high")) {
                                                    thumbnailType = thumbnails.getJSONObject("high");
                                                } else if (thumbnails.has("standard")) {
                                                    thumbnailType = thumbnails.getJSONObject("standard");
                                                } else {
                                                    thumbnailType = thumbnails.getJSONObject("maxres");
                                                }

                                                String thumbnailURL = thumbnailType.getString("url");

                                                JSONObject contentDetails = subItems.getJSONObject("contentDetails");
                                                String videoID = contentDetails.getString("videoId");

                                                Log.d("list", title);
                                                Log.d("list", desc);
                                                Log.d("list", thumbnailURL);
                                                Log.d("list", videoID);

                                                hashMap = new HashMap<>();
                                                hashMap.put("title", title);
                                                hashMap.put("desc", desc);
                                                hashMap.put("thumbnail", thumbnailURL);
                                                hashMap.put("videoID", videoID);

                                                arrayList.add(hashMap);
                                            }
                                            else {
                                                continue;
                                            }
                                        }

                                    }catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    alert.DismissAlert();
                                    callback.onPlaylistsError(error.getMessage());
                                }
                            });

                            nextPageToken = token.getToken();
                            Log.d("list",nextPageToken);
                            RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
                            queue2.add(request);
                        }
                    }


                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                alert.DismissAlert();
                callback.onPlaylistsReceived(arrayList);
//                Log.d("values", "Arraylist size: "+arrayList.size()+"arrayList in response function: " + arrayList);
                Log.d("list", "Arraylist size: "+arrayList.size());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alert.DismissAlert();
                callback.onPlaylistsError(error.getMessage());
            }
        });

        queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(objectRequest);
    }


    public static class PageToken{
        String token;
        public PageToken(String token) {
            this.token = token;
        }

        void setToken(String token){
            this.token = token;
        }
        String getToken(){
            return this.token;
        }
    }

    public class ShowAlert{
        Dialog dialog = new Dialog(Playlist_Videos.this);

        void showAlert(String type){
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            View view = getLayoutInflater().inflate(R.layout.alert_dialog, null);
            dialog.setContentView(view);

            dialog.setCancelable(false);
            LottieAnimationView animationView = dialog.findViewById(R.id.animationView);

            if(type.contains("loading")){
                animationView.setAnimation(R.raw.loading2);
                animationView.playAnimation();
            }
            dialog.show();
        }
        void DismissAlert(){
            if(dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


    private class MyChrome extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }
}
