package com.alemam.novoneel;

import static android.speech.tts.TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE;
import static android.speech.tts.TextToSpeech.QUEUE_ADD;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class ReadNews extends AppCompatActivity {
    private static final int CHUNK_SIZE = 4000;
    ImageView backButton,thumbnail;
    TextView newsTitle, newsDesc,newsContent;
    String title,desc,content,imgURL;
    ImageView readButton;
//    TextToSpeech tts;


    boolean isSpeaking = false;
    TextToSpeech textToSpeech;

    int startIndex = 0, endIndex=0, textLength=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news);

        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        desc = bundle.getString("desc");
        content = bundle.getString("content");
        imgURL = bundle.getString("imageURL");

        backButton = findViewById(R.id.backButton);
        thumbnail = findViewById(R.id.thumbnail);
        newsTitle = findViewById(R.id.newsTitle);
        newsDesc = findViewById(R.id.newsDesc);
        newsContent = findViewById(R.id.newsContent);
        readButton = findViewById(R.id.readButton);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(0.8f);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Handle language initialization error
                    }
                }
            }
        });



        // Set up the UtteranceProgressListener
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                if (utteranceId.equals("mySpeaker")) {
                    isSpeaking = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            speechButton.setBackgroundResource(R.drawable.button_image_stop);
                        }
                    });
                }
            }

            @Override
            public void onDone(String utteranceId) {
                if (utteranceId.equals("mySpeaker")) {
                    // Check if there are more chunks to process
                    // If not, update UI and reset flags
                    if (startIndex >= textLength) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isSpeaking = false;
                                readButton.setImageResource(R.drawable.voice);
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(String utteranceId) {
                // Speech error
            }
        });






        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        newsTitle.setText(title);
        newsDesc.setText(desc);
        newsContent.setText(content);

        Picasso.get()
                .load(imgURL)
                .placeholder(R.drawable.placeholder)
                .into(thumbnail);



        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSpeaking) {
                    isSpeaking = true;
                    readButton.setImageResource(R.drawable.stopreading);
                    String allText = title+". "+desc+". "+content;
                    startSpeech(allText);
                } else {
                    isSpeaking = false;
                    readButton.setImageResource(R.drawable.voice);
                    textToSpeech.stop();
                }
            }
        });

    }





    private void startSpeech(String text) {
        isSpeaking = true;
//        speechButton.setBackgroundResource(R.drawable.button_image_stop);

        textLength = text.length();
        startIndex = 0;

        while (startIndex < textLength) {
            endIndex = Math.min(startIndex + CHUNK_SIZE, textLength);
            String chunk = text.substring(startIndex, endIndex);

            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "mySpeaker");
            textToSpeech.speak(chunk, TextToSpeech.QUEUE_ADD, params, "mySpeaker");

            startIndex = endIndex;
        }
    }


    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}