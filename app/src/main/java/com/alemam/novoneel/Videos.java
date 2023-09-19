package com.alemam.novoneel;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class Videos extends Fragment {
    RequestQueue queue;
    GridView gridView;
    HashMap<String,String> hashMap;
    ArrayList<HashMap<String,String>> arrayList;

    // NASA, ESA, ISRO, SPACEX
    String[] channelList = {"UCLA_DiR1FfKNvjuUpBHmylQ","UCIBaDdAbGlFDeS33shmlD0A","UCw5hEVOTfz_AfzsNFWyNlNg","UCtI0Hodo5o5dUb67FeUjDeA"};

    public interface PlaylistCallback {
        void onPlaylistsReceived(ArrayList<HashMap<String, String>> playlist);
        void onPlaylistsError(String errorMessage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_videos, container, false);

        gridView = myView.findViewById(R.id.gridView);

        getPlaylist(channelList,new PlaylistCallback(){
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



        return myView;
    }

    private class MyAdapter extends BaseAdapter{

        private int selectedItem = -1; // Initialize with -1 to indicate no selection initially
        ImageView playlistImage;
        TextView playlistName,itemCount,Source;
        LinearLayout cardView;

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
            View myView = inflater.inflate(R.layout.video_playlist_item,viewGroup,false);

            cardView = myView.findViewById(R.id.cardView);
            playlistImage = myView.findViewById(R.id.playlistImage);
            playlistName = myView.findViewById(R.id.playlistName);
            itemCount = myView.findViewById(R.id.itemCount);
            Source = myView.findViewById(R.id.Source);

            hashMap = new HashMap<>();
            hashMap = arrayList.get(i);

            String listId = hashMap.get("playlistID");
            String videos = hashMap.get("videos");
            playlistName.setText(hashMap.get("title"));
            Source.append(hashMap.get("source"));
            String items= hashMap.get("videos")+" videos";
            itemCount.setText(items);
            Picasso.get()
                    .load(hashMap.get("thumbnail"))
                    .placeholder(R.drawable.placeholder)
                    .into(playlistImage);

            // Check if the current item is selected and update its background color
            if (i == selectedItem) {
//                viewItem.setBackgroundColor(Color.GRAY);
                cardView.setBackgroundResource(R.drawable.background_img);
            } else {
//                viewItem.setBackgroundColor(ContextCompat.getColor(Playlist_Videos.this, R.color.primary));
                cardView.setBackgroundResource(R.drawable.background_bkg);
            }

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Update the selected item index
                    selectedItem = i;
                    // Notify the adapter that the data set has changed
                    notifyDataSetChanged();

                    Intent myIntent = new Intent(getContext(), Playlist_Videos.class);
                    myIntent.putExtra("playlistID", listId);
                    myIntent.putExtra("totalVideo", videos);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent);
                }
            });

            return myView;
        }
    }


    private void getPlaylist(String[] channelList, PlaylistCallback callback) {
        arrayList = new ArrayList<>();
        for(String s: channelList) {
            String url = "https://youtube.googleapis.com/youtube/v3/playlists?part=snippet%2CcontentDetails" +
                    "&channelId="+s+"&maxResults=50&key=AIzaSyBzCuzo9Hrs-IPmJYwq3F76YWueqTslC0o";

            ShowAlert alert = new ShowAlert();
            alert.showAlert("loading");

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String nextPageToken = response.optString("nextPageToken", null);

                        PageToken token = new PageToken(nextPageToken);

                        JSONObject pageInfo = response.getJSONObject("pageInfo");

                        int totalPlaylist = pageInfo.getInt("totalResults");
                        int perPage = pageInfo.getInt("resultsPerPage");

                        double div = Math.ceil((double) totalPlaylist / perPage);
                        int loopCount = (int) div - 1;

//                        Log.d("values", nextPageToken);
                        Log.d("values", "" + loopCount);

                        JSONArray items = response.getJSONArray("items");

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject subItems = items.getJSONObject(i);

                            String id = subItems.getString("id");

                            JSONObject snippet = subItems.getJSONObject("snippet");
                            String title = snippet.getString("title");
                            String desc = snippet.getString("description");

                            if(!desc.isEmpty()) {
                                String channelTitle = snippet.getString("channelTitle");


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
                                int videos = contentDetails.getInt("itemCount");

                                Log.d("values", id);
                                Log.d("values", title);
                                Log.d("values", channelTitle);
                                Log.d("values", thumbnailURL);
                                Log.d("values", "" + videos);

                                hashMap = new HashMap<>();
                                hashMap.put("playlistID", id);
                                hashMap.put("title", title);
                                hashMap.put("source", channelTitle);
                                hashMap.put("thumbnail", thumbnailURL);
                                hashMap.put("videos", "" + videos);

                                arrayList.add(hashMap);
                            }
                            else{
                                continue;
                            }
                        }

                        // Others Page Request
                        for (int j = 0; j < loopCount; j++) {
                            String newURL = url + "&pageToken=" + nextPageToken;
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, newURL, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String nextPageToken = response.optString("nextPageToken", null);

                                        token.setToken(nextPageToken);

                                        Log.d("values", nextPageToken);

                                        JSONArray items = response.getJSONArray("items");

                                        for (int i = 0; i < items.length(); i++) {
                                            JSONObject subItems = items.getJSONObject(i);

                                            String id = subItems.getString("id");

                                            JSONObject snippet = subItems.getJSONObject("snippet");
                                            String title = snippet.getString("title");String desc = snippet.getString("description");

                                            if(!desc.isEmpty()) {
                                                String channelTitle = snippet.getString("channelTitle");


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
                                                int videos = contentDetails.getInt("itemCount");

                                                Log.d("values", id);
                                                Log.d("values", title);
                                                Log.d("desc", channelTitle);
                                                Log.d("values", thumbnailURL);
                                                Log.d("values", "" + videos);

                                                hashMap = new HashMap<>();
                                                hashMap.put("playlistID", id);
                                                hashMap.put("title", title);
                                                hashMap.put("source", channelTitle);
                                                hashMap.put("thumbnail", thumbnailURL);
                                                hashMap.put("videos", "" + videos);

                                                arrayList.add(hashMap);
                                            }
                                            else {
                                                continue;
                                            }
                                        }
                                    } catch (JSONException e) {
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
                            Log.d("values", nextPageToken);
                            RequestQueue queue2 = Volley.newRequestQueue(getContext());
                            queue2.add(request);
                        }


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    alert.DismissAlert();
                    callback.onPlaylistsReceived(arrayList);
//                Log.d("values", "Arraylist size: "+arrayList.size()+"arrayList in response function: " + arrayList);
                    Log.d("values", "Arraylist size: " + arrayList.size());
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



    /*
    private void getPlaylist(PlaylistCallback callback) {
        arrayList = new ArrayList<>();
        String url="https://youtube.googleapis.com/youtube/v3/playlists?part=snippet%2CcontentDetails" +
                "&channelId=UCLA_DiR1FfKNvjuUpBHmylQ&maxResults=50&key=AIzaSyBzCuzo9Hrs-IPmJYwq3F76YWueqTslC0o";

        ShowAlert alert = new ShowAlert();
        alert.showAlert("loading");

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String nextPageToken = response.optString("nextPageToken", null);

                    PageToken token = new PageToken(nextPageToken);

                    JSONObject pageInfo = response.getJSONObject("pageInfo");

                    int totalPlaylist = pageInfo.getInt("totalResults");
                    int perPage = pageInfo.getInt("resultsPerPage");

                    double div = Math.ceil((double)totalPlaylist/perPage);
                    int loopCount = (int) div-1;

                    Log.d("values",nextPageToken);
                    Log.d("values",""+loopCount);

                    JSONArray items = response.getJSONArray("items");

                    for(int i=0;i<items.length();i++){
                        JSONObject subItems = items.getJSONObject(i);

                        String id = subItems.getString("id");

                        JSONObject snippet = subItems.getJSONObject("snippet");
                        String title = snippet.getString("title");


                        JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                        JSONObject thumbnailType = null;

                        if(thumbnails.has("default")) {
                            thumbnailType = thumbnails.getJSONObject("default");
                        } else if (thumbnails.has("medium")) {
                            thumbnailType = thumbnails.getJSONObject("medium");
                        }else if (thumbnails.has("high")) {
                            thumbnailType = thumbnails.getJSONObject("high");
                        }else if (thumbnails.has("standard")) {
                            thumbnailType = thumbnails.getJSONObject("standard");
                        }
                        else{
                            thumbnailType = thumbnails.getJSONObject("maxres");
                        }

                        String thumbnailURL = thumbnailType.getString("url");

                        JSONObject contentDetails = subItems.getJSONObject("contentDetails");
                        int videos = contentDetails.getInt("itemCount");

                        Log.d("values",id);
                        Log.d("values",title);
//                        Log.d("values",desc);
                        Log.d("values",thumbnailURL);
                        Log.d("values",""+videos);

                        hashMap = new HashMap<>();
                        hashMap.put("playlistID",id);
                        hashMap.put("title",title);
//                        hashMap.put("desc",desc);
                        hashMap.put("thumbnail",thumbnailURL);
                        hashMap.put("videos",""+videos);

                        arrayList.add(hashMap);
                    }

                    // Others Page Request
                    for(int j=0;j<loopCount;j++)
                    {
                        String newURL = url+"&pageToken="+nextPageToken;
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, newURL, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String nextPageToken = response.optString("nextPageToken", null);

                                    token.setToken(nextPageToken);

                                    Log.d("values",nextPageToken);

                                    JSONArray items = response.getJSONArray("items");

                                    for(int i=0;i<items.length();i++){
                                        JSONObject subItems = items.getJSONObject(i);

                                        String id = subItems.getString("id");

                                        JSONObject snippet = subItems.getJSONObject("snippet");
                                        String title = snippet.getString("title");


                                        JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                        JSONObject thumbnailType = null;

                                        if(thumbnails.has("default")) {
                                            thumbnailType = thumbnails.getJSONObject("default");
                                        } else if (thumbnails.has("medium")) {
                                            thumbnailType = thumbnails.getJSONObject("medium");
                                        }else if (thumbnails.has("high")) {
                                            thumbnailType = thumbnails.getJSONObject("high");
                                        }else if (thumbnails.has("standard")) {
                                            thumbnailType = thumbnails.getJSONObject("standard");
                                        }
                                        else{
                                            thumbnailType = thumbnails.getJSONObject("maxres");
                                        }

                                        String thumbnailURL = thumbnailType.getString("url");

                                        JSONObject contentDetails = subItems.getJSONObject("contentDetails");
                                        int videos = contentDetails.getInt("itemCount");

                                        Log.d("values",id);
                                        Log.d("values",title);
//                                        Log.d("desc",desc);
                                        Log.d("values",thumbnailURL);
                                        Log.d("values",""+videos);

                                        hashMap = new HashMap<>();
                                        hashMap.put("playlistID",id);
                                        hashMap.put("title",title);
//                                        hashMap.put("desc",desc);
                                        hashMap.put("thumbnail",thumbnailURL);
                                        hashMap.put("videos",""+videos);

                                        arrayList.add(hashMap);
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
                        Log.d("values",nextPageToken);
                        RequestQueue queue2 = Volley.newRequestQueue(getContext());
                        queue2.add(request);
                    }


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                alert.DismissAlert();
                callback.onPlaylistsReceived(arrayList);
//                Log.d("values", "Arraylist size: "+arrayList.size()+"arrayList in response function: " + arrayList);
                Log.d("values", "Arraylist size: "+arrayList.size());
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