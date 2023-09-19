package com.alemam.novoneel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class View_All_News extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView;
    GridView gridView;
    HashMap<String,String> hashMap;
    public static ArrayList<HashMap<String,String>> allnews=new ArrayList<>();

    RequestQueue queue;


    public interface NewsCallBack {
        void onNewsReceived(ArrayList<HashMap<String, String>> news);
        void onNewsError(String errorMessage);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_news);

        gridView = findViewById(R.id.gridView);



        if(allnews.isEmpty()){

            getAllNews(new Dashboard.NewsCallBack(){

                @Override
                public void onNewsReceived(ArrayList<HashMap<String, String>> news) {
                    allnews = new ArrayList<>();
                    allnews = (ArrayList<HashMap<String, String>>) news.clone();

                    Log.d("videos", "arraylist size: "+allnews.size());

                    MyAdapter adapter = new MyAdapter();
                    gridView.setAdapter(adapter);
                }

                @Override
                public void onNewsError(String errorMessage) {
                    Log.e("response", "Failed to fetch playlist: " + errorMessage);
                }
            });
        }
        else{
            MyAdapter adapter = new MyAdapter();
            gridView.setAdapter(adapter);
        }

    }



    private class MyAdapter extends BaseAdapter {

        private int selectedItem = -1; // Initialize with -1 to indicate no selection initially
        LinearLayout viewItem;
        ImageView thumbnail;
        TextView title,description;

        @Override
        public int getCount() {
            return allnews.size();
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
            hashMap = allnews.get(i);

            String Title = hashMap.get("title");
            String Desc = hashMap.get("desc");
            String Content = hashMap.get("content");
            String IMGURL = hashMap.get("imageURL");

            title.setText(hashMap.get("title"));
            description.setText(hashMap.get("desc"));

            Picasso.get()
                    .load(hashMap.get("imageURL"))
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

                    Intent myIntent = new Intent(View_All_News.this, ReadNews.class);
                    myIntent.putExtra("title", Title);
                    myIntent.putExtra("desc", Desc);
                    myIntent.putExtra("content", Content);
                    myIntent.putExtra("imageURL", IMGURL);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent);
                }
            });



            return myView;
        }
    }


    private void getAllNews(Dashboard.NewsCallBack callback) {
        allnews = new ArrayList<>();

        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Initializing the calendar Object
        Calendar c = Calendar.getInstance();
        // Using format() method for conversion
        String curr_date = DateFormat.format(c.getTime());
        String[] date_array = curr_date.split("-");

        int year = Integer.parseInt(date_array[0]); //2023
        int month = Integer.parseInt(date_array[1]); //08
        int day = Integer.parseInt(date_array[2]); //02

        if(day < 3){
            month = month-1;
            if(month==1 || month == 3 || month==5 || month==7 || month==8 || month==10 || month==12){
                if(day==2){
                    day = 31;
                }
                else{
                    day = 30;
                }
            }
            else{
                if(day==2){
                    day = 30;
                }
                else{
                    day = 29;
                }
            }
        }
        else
        {
            day = day-2;
        }

        if(month <= 1){
            year = year-1;
        }

        String from_date = String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day);

        Log.d("date","from: "+from_date+" <> to: "+curr_date);

//        String url = "https://newsdata.io/api/1/news?apikey=pub_278947901ddd27de32c04283bdbac4a966497" +
//                "&q=NASA&language=bn,en&category=science,technology,top,world&full_content=1&image=1&size=10";

        String url="https://appservicebysuvo.000webhostapp.com/MyAPI/getNews.php";
        ShowAlert alert = new ShowAlert();
        alert.showAlert("loading");
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                alert.DismissAlert();
                Toast.makeText(View_All_News.this, "Request Success", Toast.LENGTH_SHORT).show();

                try {
                    if(response.getString("status").equals("success")) {

                        int total = response.getInt("totalResults");
                        double div = Math.ceil((double)total/10);
                        total = (int) div-1;

                        String nextPageToken=null;
                        if(response.has("nextPage")){
                            nextPageToken = response.optString("nextPage",null);
                        }

                        PageToken token = new PageToken(nextPageToken);

                        JSONArray array = response.getJSONArray("results");
                        Log.d("news",""+array);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject items = array.getJSONObject(i);

                            String title = items.getString("title");
                            String desc = items.getString("description");
                            String content = items.getString("content");
                            String imageURL = items.getString("image_url");
                            Log.d("news",title);
                            Log.d("news",desc);
                            Log.d("news",content);
                            Log.d("news",imageURL);
                            hashMap = new HashMap<>();
                            hashMap.put("title", title);
                            hashMap.put("desc", desc);
                            hashMap.put("content", content);
                            hashMap.put("imageURL", imageURL);

                            allnews.add(hashMap);
                        }


                        for(int j=0;j<total;j++){
                            String newUrl = url+"&page="+nextPageToken;

                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, newUrl, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {

                                        if(response.getString("status").equals("success")) {
                                            String nextPageToken=null;
                                            if(response.has("nextPage")){
                                                nextPageToken = response.optString("nextPage",null);
                                            }

                                            token.setToken(nextPageToken);

                                            JSONArray array = response.getJSONArray("results");
                                            Log.d("news",""+array);
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject items = array.getJSONObject(i);

                                                String title = items.getString("title");
                                                String desc = items.getString("description");
                                                String content = items.getString("content");
                                                String imageURL = items.getString("image_url");
                                                Log.d("news",title);
                                                Log.d("news",desc);
                                                Log.d("news",content);
                                                Log.d("news",imageURL);
                                                hashMap = new HashMap<>();
                                                hashMap.put("title", title);
                                                hashMap.put("desc", desc);
                                                hashMap.put("content", content);
                                                hashMap.put("imageURL", imageURL);

                                                allnews.add(hashMap);
                                            }

                                        }
                                    }
                                    catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });

                            nextPageToken = token.getToken();
                            Log.d("values",nextPageToken);

                            RequestQueue queue1 = Volley.newRequestQueue(View_All_News.this);
                            queue1.add(request);

                        }

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                alert.DismissAlert();
                callback.onNewsReceived(allnews);
//                Log.d("values", "Arraylist size: "+arrayList.size()+"arrayList in response function: " + arrayList);
                Log.d("arraySize", "Arraylist size: "+allnews.size());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alert.DismissAlert();
                Toast.makeText(View_All_News.this, "Request Failed", Toast.LENGTH_SHORT).show();
                callback.onNewsError(error.getMessage());
            }
        });

        queue = Volley.newRequestQueue(View_All_News.this);
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
        Dialog dialog = new Dialog(View_All_News.this);

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