package com.slate.vit.vitslate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class Team extends ActionBarActivity {

    String action_bar;
    SharedPreferences sharedpreferences;
    String rootDirectory = Environment.getExternalStorageDirectory().toString();
    File captchaDirectory = new File(rootDirectory + "/VITSlate/.TrashBinData");
    String ownerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        final String profile = captchaDirectory.toString()+"/.ProfilePic.jpg";
        Bitmap bmp = BitmapFactory.decodeFile(profile);

        ImageView userImage = (ImageView) findViewById(R.id.user);
        //bmp= Bitmap.createScaledBitmap(bmp,150 ,199,false);
        userImage.setImageBitmap(bmp);

        final String MyPREFERENCES = "MyPrefs" ;
        sharedpreferences=getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        action_bar=sharedpreferences.getString("action_bar", "#035de7");
        ownerName = sharedpreferences.getString("owner_name", "Owner");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(action_bar)));
        getSupportActionBar().setTitle("The team");


        Button kunal = (Button) findViewById(R.id.kunal);
        Button aayush = (Button) findViewById(R.id.aayush);
        Button saurabh = (Button) findViewById(R.id.saurabh);
        Button akshay = (Button) findViewById(R.id.akshay);
        Button ghanu = (Button) findViewById(R.id.ghanu);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(Team.this, TeamDisc.class);
                i2.putExtra("Passcode", "user");
                startActivity(i2);
            }
        });

        kunal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(Team.this, TeamDisc.class);
                i2.putExtra("Passcode", "kunal");
                startActivity(i2);
            }
        });

        aayush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(Team.this, TeamDisc.class);
                i2.putExtra("Passcode", "aayush");
                startActivity(i2);
            }
        });

        saurabh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(Team.this, TeamDisc.class);
                i2.putExtra("Passcode", "saurabh");
                startActivity(i2);
            }
        });

        akshay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(Team.this, TeamDisc.class);
                i2.putExtra("Passcode", "akshay");
                startActivity(i2);
            }
        });

        ghanu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(Team.this, TeamDisc.class);
                i2.putExtra("Passcode", "ghanu");
                startActivity(i2);
            }
        });

    }

}
