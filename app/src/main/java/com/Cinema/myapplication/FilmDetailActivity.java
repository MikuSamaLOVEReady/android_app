package com.Cinema.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Cinema.myapplication.ui.home.HomeFragment;


public class FilmDetailActivity extends AppCompatActivity {


    private String FilmID;
    public int PosterID;
    private ImageView Poster;
    private TextView FilmName;
    private TextView blurb;
    private TextView Director;
    private TextView Actors;
    private TextView Detail;


    private  Button schedule_button;
    private  Button back;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_film);


        //拿到这个ID
        Intent intent=getIntent();
        FilmID=intent.getStringExtra("FilmID");
        //接受海报ID
        PosterID =intent.getIntExtra("Position",-1);
        Poster= findViewById(R.id.Film_Poster);
        Poster.setImageBitmap((Bitmap) HomeFragment.Info_list.get(PosterID).get("image"));

        LinearLayout temp=(LinearLayout)findViewById(R.id.Detail_background);
        temp.setBackgroundResource(R.drawable.dark_color);

        FilmName = findViewById(R.id.filmname);
        //FilmName.append(HomeFragment.Info_list.get(PosterID).get("FilmName").toString());
        FilmName.setText(HomeFragment.Info_list.get(PosterID).get("FilmName").toString());

        blurb    = findViewById(R.id.blurb);
        blurb.append(HomeFragment.Info_list.get(PosterID).get("Blurb").toString());
        //blurb.setText();

        Director = findViewById(R.id.Director);
        Director.append(HomeFragment.Info_list.get(PosterID).get("Director").toString());
        //Director.setText(HomeFragment.Info_list.get(PosterID).get("Director").toString());

        Actors = findViewById(R.id.Actors);
        Actors.append(HomeFragment.Info_list.get(PosterID).get("LeadActors").toString());
        //Actors.setText(HomeFragment.Info_list.get(PosterID).get("LeadActors").toString());

        Detail = findViewById(R.id.Detail);
        Detail.setText(HomeFragment.Info_list.get(PosterID).get("Detial").toString());


        schedule_button= findViewById(R.id.schedule_show);
        schedule_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FilmDetailActivity.this, ScheduleActivity.class);
                //用getActivity()获得当前的fragmentactivit
                intent.putExtra("FilmID",FilmID);
                startActivity(intent);
            }
        });

        back=findViewById(R.id.Back_homeFragment);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FilmDetailActivity.this, NavActivity.class);
                //用getActivity()获得当前的fragmentactivit
                startActivity(intent);
            }
        });




    }

}
