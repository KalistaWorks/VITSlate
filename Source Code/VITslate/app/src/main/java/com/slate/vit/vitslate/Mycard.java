package com.slate.vit.vitslate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Aayush Karwatkar on 02-Sep-15.
 *
 * A class which gives the basic layout of a card in the cardlist.
 *
 * It first gets the layout friom row_card.xml which is xml layout of how each card will look like.
 * Then the two textviews are being dynamically assigned the values which we pass from the HomeScreen.java class,
 * while declaring a card.
 *
 * Then the card listener is being set up here for the imButton.
 */
public class Mycard extends Card {

    public String card_title;
    public String subject_title;
    String action_bar, button_color, button_colorPressed;
    SharedPreferences sharedpreferences;

    public Mycard(Context context){
        super(context, R.layout.row_card);
    }
    @Override

    public void setupInnerViewElements(ViewGroup parent, View view) {

        Context context = getContext();
        final String MyPREFERENCES = "MyPrefs" ;
        sharedpreferences=context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        action_bar=sharedpreferences.getString("action_bar", "#035de7");
        button_color=sharedpreferences.getString("button_color", "#FF4081");
        button_colorPressed=sharedpreferences.getString("button_colorPressed", "#E91E63");

        TextView tx= (TextView)view.findViewById(R.id.card_title);
        tx.setText(card_title);
        tx.setTextColor(Color.parseColor(action_bar));

        TextView tx2= (TextView)view.findViewById(R.id.subject_title);
        tx2.setText(subject_title);

        ImageButton imButton = (ImageButton) view.findViewById(R.id.imButton);
        switch (action_bar)
        {
            case "#035de7":
                imButton.setImageResource(R.mipmap.ic_course_viewer_final);
                break;
            case "#212121":
                imButton.setImageResource(R.mipmap.ic_course_viewer_final_suits);
                break;
            case "#FF4081":
                imButton.setImageResource(R.mipmap.ic_course_viewer_final_permanent_roommates);
                break;
            case "#f30101":
                imButton.setImageResource(R.mipmap.ic_course_viewer_final_game_of_thrones);
                break;
            case "#94b900":
                imButton.setImageResource(R.mipmap.ic_course_viewer_final_pitchers);
                break;
            default:
                imButton.setImageResource(R.mipmap.ic_course_viewer_final);
                break;

        }


        imButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(), Teacher.class);
                i.putExtra("subject_title", subject_title);
                getContext().startActivity(i);
            }
        });

    }



}
