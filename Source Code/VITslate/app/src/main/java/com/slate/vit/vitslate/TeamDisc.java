package com.slate.vit.vitslate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TeamDisc extends ActionBarActivity {

    String action_bar;
    SharedPreferences sharedpreferences;
    String ownerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_disc);

        Intent intent = getIntent();
        String Passcode = intent.getExtras().getString("Passcode");

        final String MyPREFERENCES = "MyPrefs" ;
        sharedpreferences=getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        action_bar=sharedpreferences.getString("action_bar", "#035de7");
        ownerName = sharedpreferences.getString("owner_name", "Owner");
        TextView desc = (TextView) findViewById(R.id.description);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(action_bar)));

        switch (Passcode)
        {
            case "user":
                desc.setText("\nHi "+ownerName+"!\n" +
                        "\n\"I'm VITSlate. \nThanks for having me.\nBy downloading, you have taken ownership of me " +
                        "and have become a part of our team. I hope I'll provide you with a wonderful experience in managing your " +
                        "course material. \n\nPlease take some time to rate and review me on play store. And if you have any suggestion," +
                        " you can always catch hold of our team members anywhere in campus, they will be happy to meet you or " +
                        "you can also send a mail at:\n\nkalista.works@gmail.com \n" +
                        "\nNow let me introduce you to the rest of our team...\"");
                getSupportActionBar().setTitle(ownerName);
                break;
            case "kunal":
                desc.setText("\n" +
                        "\"Kunal Dhodapkar.\nHe is the inspiration behind me and has made me look and feel the way I do. " +
                        "He is the person who crystallised my UI design flow on-paper.\n\n" +
                        "He values ideas, and so, if you are a person who has fresh ideas and a desire to make a dent," +
                        " you can always approach him without hesitation, he'll be more than happy to meet you.\"");
                getSupportActionBar().setTitle("Kunal Dhodapkar");
                break;
            case "aayush":
                desc.setText("\n" +
                        "\"Aayush Karwatkar.\nHe is the person who brought me into a tangible form. A brilliant Android developer, " +
                        "he exercises coding skills in Java, XML, HTML and Javascript.\n\nHe is the main programmer of my " +
                        "frontend and backend.\nAs a person he is convivial, warm, witty, likes challenges and enjoys good food.\"");
                getSupportActionBar().setTitle("Aayush Karwatkar");
                break;
            case "saurabh":
                desc.setText("\n" +
                        "\"Saurabh Thakur.\nHe helped to search for a way to login. " +
                        "\"He has terrific python coding skills and a deep backend knowledge. He likes gymming and solving puzzles." +
                        " He may look like a strong person with great body, but has a tender and honest heart inside. \"");
                getSupportActionBar().setTitle("Saurabh Thakur");
                break;
            case "akshay":
                desc.setText("\n" +
                        "\"Akshay Mahajan.\nHe coded the first UI prototypes of me. An awesome frontend coder in Android," +
                        " he also is good at developing web applications. " +
                        "At first he may look like a shy and reticent guy, " +
                        "but in reality he is a very open-minded person with a good sense of humour.\"");
                getSupportActionBar().setTitle("Akshay Mahajan");
                break;
            case "ghanu":
                desc.setText("\n" +
                        "\"Ghanshyam Gupta.\nHe designed a really friendly icon for me. He has superb command over " +
                        "Photoshop and a good design sense. By nature he is an sociable and gregarious person.\"");
                getSupportActionBar().setTitle("Ghanshyam");
                break;
            default:
                desc.setText("Hi " + ownerName + "!\n" +
                        "\n" +
                        "\"I'm VITSlate. \nThanks for having me.\nBy downloading, you have taken ownership of me " +
                        "and have become a part of our team. I hope I'll provide you with a wonderful experience in managing your " +
                        "course material. \n\nPlease take some time to rate and review me on play store. And if you have any suggestion," +
                        " you can always catch hold of our team members anywhere in campus, they will be happy to meet you or " +
                        "you can also send a mail at:\n\nkalista.works@gmail.com \n" +
                        "\nNow let me introduce you to the rest of our team...\"");
                getSupportActionBar().setTitle(ownerName);
                break;
        }



    }

}
