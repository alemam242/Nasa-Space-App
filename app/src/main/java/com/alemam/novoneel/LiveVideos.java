package com.alemam.novoneel;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;

public class LiveVideos extends Fragment {

    RequestQueue queue;

    GridView gridView;
    HashMap<String,String> hashMap;
    public static ArrayList<HashMap<String,String>> arrayList;

    // NASA, ESA, ISRO, SPACEX
    String[] channelList = {"UCLA_DiR1FfKNvjuUpBHmylQ","UCIBaDdAbGlFDeS33shmlD0A","UCw5hEVOTfz_AfzsNFWyNlNg","UCtI0Hodo5o5dUb67FeUjDeA"};

    public interface PlaylistCallback {
        void onPlaylistsReceived(ArrayList<HashMap<String, String>> playlist);
        void onPlaylistsError(String errorMessage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_live_videos, container, false);;

        gridView = myView.findViewById(R.id.gridView);

        getLiveVideos(channelList,new PlaylistCallback(){

            @Override
            public void onPlaylistsReceived(ArrayList<HashMap<String, String>> playlist) {
                arrayList = new ArrayList<>();
                arrayList = (ArrayList<HashMap<String, String>>) playlist.clone();
                PlayVideos.videos = (ArrayList<HashMap<String, String>>) playlist.clone();

                Log.d("videos", "arraylist size: "+arrayList.size());

                MyAdapter adapter = new MyAdapter();
                gridView.setAdapter(adapter);
            }

            @Override
            public void onPlaylistsError(String errorMessage) {
                Log.e("response", "Failed to fetch playlist: " + errorMessage);
            }
        });


        return myView;
    }


    private class MyAdapter extends BaseAdapter {

        private int selectedItem = -1; // Initialize with -1 to indicate no selection initially
        ImageView thumbnail;
        TextView title,description;
        CardView cardView;

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
            View myView = inflater.inflate(R.layout.live_video_item,viewGroup,false);

            cardView = myView.findViewById(R.id.cardView);
            title = myView.findViewById(R.id.title);
            description = myView.findViewById(R.id.description);
            thumbnail = myView.findViewById(R.id.thumbnail);

            hashMap = new HashMap<>();
            hashMap = arrayList.get(i);

            String videoTitle = hashMap.get("title");
            String videoID = hashMap.get("videoID");

            title.setText(hashMap.get("title"));
            description.setText(hashMap.get("description"));
            Picasso.get()
                    .load(hashMap.get("thumbnail"))
                    .placeholder(R.drawable.placeholder)
                    .into(thumbnail);

            // Check if the current item is selected and update its background color
            if (i == selectedItem) {
//                viewItem.setBackgroundColor(Color.GRAY);
                cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.selectedItem));
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary));
//                cardView.setBackgroundResource(R.drawable.background_bkg);
            }

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Update the selected item index
                    selectedItem = i;
                    // Notify the adapter that the data set has changed
                    notifyDataSetChanged();

                    Intent myIntent = new Intent(getActivity(), PlayVideos.class);
                    myIntent.putExtra("title", videoTitle);
                    myIntent.putExtra("videoID", videoID);
                    myIntent.putExtra("itemNo",i);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent);
                }
            });


            return myView;
        }
    }

    ////////////////////////////
    private void getLiveVideos(String[] channelList, PlaylistCallback callback){
        arrayList = new ArrayList<>();
        String loopExit = channelList[channelList.length-1];
        for (String s : channelList) {
            String url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&channelId=" + s + "&eventType=live" +
                    "&maxResults=50&order=date&q=nasa&type=video&key=AIzaSyBzCuzo9Hrs-IPmJYwq3F76YWueqTslC0o";

            ShowAlert alert = new ShowAlert();
            alert.showAlert("loading");

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONArray items = response.getJSONArray("items");

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject subItems = items.getJSONObject(i);

                            JSONObject info = subItems.getJSONObject("id");
                            String videoID = info.getString("videoId");

                            JSONObject snippet = subItems.getJSONObject("snippet");
                            String channelID = snippet.getString("channelId");
                            String title = snippet.getString("title");
                            String description = snippet.getString("description");

                            JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                            JSONObject thumbnailType = null;

                            if (thumbnails.has("default")) {
                                thumbnailType = thumbnails.getJSONObject("default");
                            } else if (thumbnails.has("medium")) {
                                thumbnailType = thumbnails.getJSONObject("medium");
                            } else {
                                thumbnailType = thumbnails.getJSONObject("high");
                            }

                            String thumbnailURL = thumbnailType.getString("url");


                            Log.d("lives", videoID);
                            Log.d("lives", channelID);
                            Log.d("lives", title);
                            Log.d("lives", description);
                            Log.d("lives", thumbnailURL);

                            hashMap = new HashMap<>();
                            hashMap.put("videoID", videoID);
                            hashMap.put("channelID", channelID);
                            hashMap.put("title", title);
                            hashMap.put("description", description);
                            hashMap.put("thumbnail", thumbnailURL);

                            arrayList.add(hashMap);
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

//                    if(loopExit.equals(s)) {
                        alert.DismissAlert();
                        callback.onPlaylistsReceived(arrayList);
//                    }
//                Log.d("lives", "Arraylist size: "+arrayList.size()+"arrayList in response function: " + arrayList);
                    Log.d("lives", "Arraylist size: " + arrayList.size());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    alert.DismissAlert();
                    callback.onPlaylistsError(error.getMessage());
                }
            });
            queue = Volley.newRequestQueue(getContext());
            queue.add(objectRequest);
        }
    }
    ////////////////////////////

    /*
    private void getLiveVideos(PlaylistCallback callback){
        arrayList = new ArrayList<>();
        String url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&channelId=UCLA_DiR1FfKNvjuUpBHmylQ&eventType=live" +
                "&maxResults=50&order=date&q=nasa&type=video&key=AIzaSyBzCuzo9Hrs-IPmJYwq3F76YWueqTslC0o";

        ShowAlert alert = new ShowAlert();
        alert.showAlert("loading");

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray items = response.getJSONArray("items");

                    for(int i=0;i<items.length();i++){
                        JSONObject subItems = items.getJSONObject(i);

                        JSONObject info = subItems.getJSONObject("id");
                        String videoID = info.getString("videoId");

                        JSONObject snippet = subItems.getJSONObject("snippet");
                        String channelID = snippet.getString("channelId");
                        String title = snippet.getString("title");
                        String description = snippet.getString("description");

                        JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                        JSONObject thumbnailType = null;

                        if(thumbnails.has("default")) {
                            thumbnailType = thumbnails.getJSONObject("default");
                        }
                        else if (thumbnails.has("medium")) {
                            thumbnailType = thumbnails.getJSONObject("medium");
                        }
                        else{
                            thumbnailType = thumbnails.getJSONObject("high");
                        }

                        String thumbnailURL = thumbnailType.getString("url");


                        Log.d("lives",videoID);
                        Log.d("lives",channelID);
                        Log.d("lives",title);
                        Log.d("lives",description);
                        Log.d("lives",thumbnailURL);

                        hashMap = new HashMap<>();
                        hashMap.put("videoID",videoID);
                        hashMap.put("channelID",channelID);
                        hashMap.put("title",title);
                        hashMap.put("description",description);
                        hashMap.put("thumbnail",thumbnailURL);

                        arrayList.add(hashMap);
                    }

                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                alert.DismissAlert();
                callback.onPlaylistsReceived(arrayList);
//                Log.d("lives", "Arraylist size: "+arrayList.size()+"arrayList in response function: " + arrayList);
                Log.d("lives", "Arraylist size: "+arrayList.size());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alert.DismissAlert();
                callback.onPlaylistsError(error.getMessage());
            }
        });
        queue = Volley.newRequestQueue(getContext());
        queue.add(objectRequest);
    }

    */

    public class ShowAlert{
        Dialog dialog = new Dialog(getActivity());

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
}