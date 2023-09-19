package com.alemam.novoneel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    DrawerLayout drawerLayout;
    MaterialToolbar toolbar;
    FrameLayout frameLayout;
    NavigationView navigationView;
    View myView;
    BottomNavigationView bottomNavigationView;
    RequestQueue queue;
    String navItem="";
    HashMap<String,String> hashMap;
    public static ArrayList<HashMap<String,String>> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        frameLayout = findViewById(R.id.frameLayout);

        navigationView = findViewById(R.id.navigationView);
        myView = navigationView.getHeaderView(0);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        frameLayout.removeAllViews();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout, new Dashboard());
        fragmentTransaction.commit();

        // for nav drawer toogle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this,drawerLayout,toolbar,R.string.drawer_close,R.string.drawer_open
        ){
            @Override
            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
                // Add your work
            }
        };
        drawerLayout.addDrawerListener(toggle);
        /////////////////////////

        //=============== For Navigation View =================
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.home)
                {
                    navItem="home";

                    bottomNavigationView.setSelectedItemId(R.id.Home);
                    drawerLayout.closeDrawer(GravityCompat.START);

                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.add(R.id.frameLayout, new Dashboard());
                    fragmentTransaction.commit();
                }
                if(item.getItemId() == R.id.news)
                {
                    navItem="news";

                    bottomNavigationView.setSelectedItemId(R.id.News);
                    drawerLayout.closeDrawer(GravityCompat.START);

                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.add(R.id.frameLayout, new All_News());
                    fragmentTransaction.commit();
                }
                if(item.getItemId() == R.id.video)
                {
                    navItem="video";

                    bottomNavigationView.setSelectedItemId(R.id.Videos);
                    drawerLayout.closeDrawer(GravityCompat.START);

                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.add(R.id.frameLayout, new Videos());
                    fragmentTransaction.commit();
                }
                if(item.getItemId() == R.id.live)
                {
                    navItem="live";

                    bottomNavigationView.setSelectedItemId(R.id.Live);
                    drawerLayout.closeDrawer(GravityCompat.START);

                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.add(R.id.frameLayout, new LiveVideos());
                    fragmentTransaction.commit();
                }
                if(item.getItemId() == R.id.astronomy)
                {
                    navItem="astronomy";
                    drawerLayout.closeDrawer(GravityCompat.START);

                    Intent myIntent = new Intent(MainActivity.this,viewImages.class);
                    myIntent.putExtra("key", "astronomy");
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent);
                }
                if(item.getItemId() == R.id.mars)
                {
                    navItem="mars";
                    drawerLayout.closeDrawer(GravityCompat.START);

                    Intent myIntent = new Intent(MainActivity.this,viewImages.class);
                    myIntent.putExtra("key", "mars");
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent);
                }
                if(item.getItemId() == R.id.share)
                {
                    navItem="share";
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "https://drive.google.com/drive/folders/1nQlTE_Szgqf3Cj29D6Y2zsUxLu73lRFc?usp=sharing";
                    String shareSub = "Share our app";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "using"));
//                    Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if(item.getItemId() == R.id.about)
                {
                    navItem="about";
                    ImageView Github;
                    AlertDialog.Builder alertadd = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                    final View view = factory.inflate(R.layout.about_me, null);
                    Github = view.findViewById(R.id.Github);
                    alertadd.setView(view);
                    alertadd.setCancelable(true);
                    alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertadd.show();

                    Github.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(),Social_Link.class);
                            intent.putExtra("key","github");
                            startActivity(intent);
                        }
                    });
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                return true;
            }
        });



        //=========== For Bottom Navigation View ==============
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.Home) {
                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.frameLayout, new Dashboard());
                    fragmentTransaction.commit();
//                    Toast.makeText(MainActivity.this, ""+Notify, Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.News) {
                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.frameLayout, new All_News());
                    fragmentTransaction.commit();
//                    Toast.makeText(MainActivity.this, ""+Notify, Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.Videos) {
                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.frameLayout, new Videos());
                    fragmentTransaction.commit();
//                    Toast.makeText(MainActivity.this, ""+Notify, Toast.LENGTH_SHORT).show();
                    return true;
                }
                else{
                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.add(R.id.frameLayout, new LiveVideos());
                    fragmentTransaction.commit();
//                    Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                    return true;
                }

            };
        });




    } // End of onCreate


    // Back Pressed
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.warning)
                .setTitle("Quit")
                .setMessage("Are you sure want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showRatingBox();
//                        finishAndRemoveTask();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    } // End of On Back Pressed

    private void showRatingBox()
    {
        ImageView star1,star2,star3,star4,star5;
        AlertDialog.Builder alertadd = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View view = factory.inflate(R.layout.rating, null);
        star1 = view.findViewById(R.id.star1);
        star2 = view.findViewById(R.id.star2);
        star3 = view.findViewById(R.id.star3);
        star4 = view.findViewById(R.id.star4);
        star5 = view.findViewById(R.id.star5);
        alertadd.setView(view);
        alertadd.setCancelable(true);
        alertadd.setTitle("Rate This App");
        alertadd.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finishAndRemoveTask();
            }
        });
        alertadd.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finishAndRemoveTask();
            }
        });
        alertadd.show();

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.star_fill);
                star2.setImageResource(R.drawable.star);
                star3.setImageResource(R.drawable.star);
                star4.setImageResource(R.drawable.star);
                star5.setImageResource(R.drawable.star);
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.star_fill);
                star2.setImageResource(R.drawable.star_fill);
                star3.setImageResource(R.drawable.star);
                star4.setImageResource(R.drawable.star);
                star5.setImageResource(R.drawable.star);
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.star_fill);
                star2.setImageResource(R.drawable.star_fill);
                star3.setImageResource(R.drawable.star_fill);
                star4.setImageResource(R.drawable.star);
                star5.setImageResource(R.drawable.star);
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.star_fill);
                star2.setImageResource(R.drawable.star_fill);
                star3.setImageResource(R.drawable.star_fill);
                star4.setImageResource(R.drawable.star_fill);
                star5.setImageResource(R.drawable.star);
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.star_fill);
                star2.setImageResource(R.drawable.star_fill);
                star3.setImageResource(R.drawable.star_fill);
                star4.setImageResource(R.drawable.star_fill);
                star5.setImageResource(R.drawable.star_fill);
            }
        });



    }// End of Rating Box
    //==================================================


    public class ShowAlert{
        Dialog dialog = new Dialog(getApplicationContext());

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


} // End of Main Class