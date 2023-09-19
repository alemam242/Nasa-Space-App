package com.alemam.novoneel;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayVideos extends AppCompatActivity {

    WebView webView;
    GridView gridView;
    ImageView backButton;
    TextView videoTitle;
    String videoID="",title="";
    int itemNo=-1;
    String previousID="";
    String embed,url,ext;
    HashMap<String,String> hashMap;
    public static ArrayList<HashMap<String,String>> videos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_videos);

         Bundle bundle = getIntent().getExtras();
         videoID = bundle.getString("videoID");
         title = bundle.getString("title");
         itemNo = bundle.getInt("itemNo");

        webView = findViewById(R.id.webView);
        gridView = findViewById(R.id.gridView);
        videoTitle = findViewById(R.id.videoTitle);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        String url = "<iframe src=\"https://www.youtube.com/embed/"+videoID+"\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
         playVideo(videoID);
//        webView.loadUrl(url);
        videoTitle.setText(title);


        MyAdapter adapter = new MyAdapter();
        gridView.setAdapter(adapter);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void playVideo(String id){
        embed="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/";
        ext = "\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        url = embed+id+ext;

        webView.loadData(url,"text/html","utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new MyChrome());
    }
    private class MyAdapter extends BaseAdapter{

        private int selectedItem = itemNo; // Initialize with -1 to indicate no selection initially
        LinearLayout viewItem;
        ImageView thumbnail;
        TextView title,description;

        @Override
        public int getCount() {
            return videos.size();
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
            hashMap = videos.get(i);

            String videoId = hashMap.get("videoID");
            String VideoTitle = hashMap.get("title");

            title.setText(hashMap.get("title"));
            description.setText(hashMap.get("description"));

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

                    videoTitle.setText(VideoTitle);
                    playVideo(videoId);
                }
            });



            return myView;
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
