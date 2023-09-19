package com.alemam.novoneel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class viewImages extends AppCompatActivity {

    ImageView backButton;
    GridView gridView;
    TextView astronomy,empty;
    HashMap<String,String> hashMap;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    ArrayList<HashMap<String,String>> imageData = new ArrayList<>();
    ArrayList<HashMap<String,String>> tmp;

    RequestQueue queue;
    String key=null;
    int page=1;



    public interface ImageCallBack {
        void onImageReceived(ArrayList<HashMap<String, String>> images);
        void onImageError(String errorMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        Bundle bundle = getIntent().getExtras();
        key = bundle.getString("key");

        backButton = findViewById(R.id.backButton);
        gridView = findViewById(R.id.gridView);
        astronomy = findViewById(R.id.astronomy);
        empty = findViewById(R.id.empty);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(key.equals("astronomy")) {
            getImages(new ImageCallBack() {
                @Override
                public void onImageReceived(ArrayList<HashMap<String, String>> images) {
                    arrayList = new ArrayList<>();
                    arrayList = (ArrayList<HashMap<String, String>>) images.clone();

                    Log.d("imageData", "arraylist size: " + arrayList.size());

                    if (arrayList.size() == 0) {
                        gridView.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                    } else {
                        empty.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);

                        MyAdapter adapter = new MyAdapter();
                        gridView.setAdapter(adapter);
                    }
                }

                @Override
                public void onImageError(String errorMessage) {
                    Log.e("imageData", "Failed to fetch data: " + errorMessage);
                }
            });
        }else{
            ShowAlert alert = new ShowAlert();
            alert.showAlert("loading");
            getMarsImages(page, new ImageCallBack() {
                @Override
                public void onImageReceived(ArrayList<HashMap<String, String>> images) {
                    if(images.size() == 0){
                        alert.DismissAlert();
                        if (imageData.size() == 0) {
                            gridView.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
                        } else {
                            empty.setVisibility(View.GONE);
                            gridView.setVisibility(View.VISIBLE);

                            arrayList = new ArrayList<>();
                            arrayList = (ArrayList<HashMap<String, String>>) imageData.clone();

                            Log.d("imageData", "arraylist["+arrayList.size()+"]: " + arrayList);
                            MyAdapter adapter = new MyAdapter();
                            gridView.setAdapter(adapter);
                        }
                    }

                    imageData = (ArrayList<HashMap<String, String>>) images.clone();
//                        images = new ArrayList<>();



                    Log.d("imageData", "images array size: " + images.size());
                    Log.d("imageData", "arraylist size: " + arrayList.size());
                    Log.d("imageData", "arraylist size: " + imageData.size());
                }

                @Override
                public void onImageError(String errorMessage) {
                    Log.e("imageData", "Failed to fetch data: " + errorMessage);
                }
            });
        }
    }

    private void getMarsImages(int inpage,ImageCallBack callback) {
//        arrayList = new ArrayList<>();
        tmp = new ArrayList<>();
        //String url="https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&page="+inpage+"&api_key=guIBXkOxNrViVIS555zS2618JZy8752p7hwjk3dc";

        String url = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/latest_photos?page="+inpage+"&api_key=guIBXkOxNrViVIS555zS2618JZy8752p7hwjk3dc";
        Log.d("imageData","URL: "+url);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("latest_photos");
                    if(array.length() > 0){
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String imgUrl = object.getString("img_src");

                            hashMap = new HashMap<>();
                            hashMap.put("image",imgUrl);
                            arrayList.add(hashMap);
                        }

                        callback.onImageReceived(arrayList);

                        page++;
                        getMarsImages(page, callback);
                    }else{
                        callback.onImageReceived(tmp);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onImageError(error.getMessage());
            }
        });
        queue = Volley.newRequestQueue(viewImages.this);
        queue.add(objectRequest);

    }

    private void getImages(ImageCallBack callback) {
        arrayList = new ArrayList<>();

        LocalDate date = LocalDate.now();
        LocalDate newDate = date.minusDays(30);

        String url = "https://api.nasa.gov/planetary/apod?start_date="+newDate+"&end_date="+date+"&thumbs=True&api_key=guIBXkOxNrViVIS555zS2618JZy8752p7hwjk3dc";
        ShowAlert alert = new ShowAlert();
        alert.showAlert("loading");

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("imageData","Data: "+response.length());
                try{
                for (int i = 0; i < response.length(); i++) {
                    JSONObject object = response.getJSONObject(i);
                    String title = object.getString("title");
                    String type = object.getString("media_type");
                    String imgUrl=null;

                    if(type.contains("image")){
                        imgUrl = object.getString("url");
                    }else if(type.contains("video")){
                        imgUrl = object.getString("thumbnail_url");
                    }


                    Log.d("imageData","title: "+title);
                    Log.d("imageData","image: "+imgUrl);

                    hashMap = new HashMap<>();
                    hashMap.put("title",title);
                    hashMap.put("image",imgUrl);

                    arrayList.add(hashMap);
                }
            }catch (JSONException e) {
                    Log.d("imageData","Exception: "+e);
                    throw new RuntimeException(e);
                }
                alert.DismissAlert();
                Log.d("imageData", "Arraylist size: "+arrayList.size());
                callback.onImageReceived(arrayList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alert.DismissAlert();
                callback.onImageError(error.getMessage());
            }
        });
        queue = Volley.newRequestQueue(viewImages.this);
        queue.add(arrayRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class MyAdapter extends BaseAdapter{

        TextView title;
        ImageView image;
        View bar;
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
            View myView = inflater.inflate(R.layout.image_item,viewGroup,false);

            title = myView.findViewById(R.id.title);
            image = myView.findViewById(R.id.image);


            hashMap = new HashMap<>();
            hashMap = arrayList.get(i);

            if(key.contains("astronomy")) {
                title.setVisibility(View.VISIBLE);
                title.setText(hashMap.get("title"));
            }else{
                title.setVisibility(View.GONE);
            }

//            String img = hashMap.get("image");
//            String[] arrImg = img.split("://", 2);

//            Log.d("imageData", "image: " + arrImg[0]+" -- "+arrImg[1]);
            Picasso.get()
                    .load(hashMap.get("image"))
                    .placeholder(R.drawable.placeholder)
                    .into(image);

            return myView;
        }
    }


    public class ShowAlert{
        Dialog dialog = new Dialog(viewImages.this);

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