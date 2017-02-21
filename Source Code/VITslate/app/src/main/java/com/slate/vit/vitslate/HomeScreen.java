package com.slate.vit.vitslate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/*Link to tutorial for building navigation drawer:
http://blog.teamtreehouse.com/add-navigation-drawer-android
 */

public class HomeScreen extends ActionBarActivity {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle = " ";

    String rootDirectory = Environment.getExternalStorageDirectory().toString();
    File captchaDirectory = new File(rootDirectory + "/VITSlate/.TrashBinData");

    String captcha = " ";

    String verCode = " ";
    String userName = " ";
    String passWord = " ";
    String action_bar = " ", button_color = " ", button_colorPressed = " ";
    ProgressDialog progress;
    List<String> cookieParams;
    String sessionId = " ";
    String cookieName = " ";
    Integer contentAsynctask6 = 0;

    SharedPreferences sharedpreferences;

    private ImageView mButtonOne;
    private ImageView mButtonTwo;
    private ImageView mButtonThree;

    private Button mButtonReset;

    private static final String SHOWCASE_ID = "sequence example";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        File captcha = new File(captchaDirectory.toString()+"/captcha.jpg");
        captcha.delete();

        //shared prefs
        final String MyPREFERENCES = "MyPrefs" ;
        sharedpreferences=getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        userName=sharedpreferences.getString("nameKey", "");
        passWord=sharedpreferences.getString("passwordKey", "");
        action_bar=sharedpreferences.getString("action_bar", "#035de7");
        button_color=sharedpreferences.getString("button_color", "#FF4081");
        button_colorPressed=sharedpreferences.getString("button_colorPressed", "#E91E63");

        //showcase view
        mButtonOne = (ImageView) findViewById(R.id.btn_one);
        mButtonTwo = (ImageView) findViewById(R.id.btn_two);
        mButtonThree = (ImageView) findViewById(R.id.btn_three);
        ShowcaseConfig config = new ShowcaseConfig();
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
        sequence.setConfig(config);

        sequence.addSequenceItem(mButtonOne,
                "To start downloading:\nClick on this button to download course material of your choice", "(click here)-> OKAY! Got it!");
        sequence.addSequenceItem(mButtonTwo,
                "After  getting downloaded:\nYour material will appear inside subject's card.","-> OKAY! Got it!");
        sequence.addSequenceItem(mButtonThree,
                "Refresh app to update course material links.","-> Let's get started!");
        sequence.start();


        Intent intent = getIntent();
        String Passcode = intent.getExtras().getString("Passcode");
        if(!Passcode.equals("0")) {
            Toast.makeText(getApplicationContext(), Passcode, Toast.LENGTH_LONG).show();
        }


        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        //Action bar title
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //Set color of action bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(action_bar)));


        List<Slots_DB> items = new Select().from(Slots_DB.class).execute();

        int len = items.size(), endOfName;

        final String[] array = new String[len];

        for (int i = 0; i < len; i++) {
            array[i] = items.get(i).name;
        }

        List orig = Arrays.asList(array);

        Collections.sort(orig, new NaturalOrderComparator());

        System.out.println("Sorted: " + orig);

        ArrayList<Card> cards = new ArrayList<Card>();

        for (int i = 0; i < len; i++) {
            // Create a Card
            if ((array[i].substring(0, 2).contains("A2"))||
                    (array[i].substring(0, 2).contains("B2"))||
                    (array[i].substring(0, 2).contains("C2"))||
                    (array[i].substring(0, 2).contains("D2"))||
                    (array[i].substring(0, 2).contains("E2"))||
                    (array[i].substring(0, 2).contains("F2"))||
                    (array[i].substring(0, 2).contains("G2"))||
                    (array[i].substring(0, 2).contains("A1"))||
                    (array[i].substring(0, 2).contains("B1"))||
                    (array[i].substring(0, 2).contains("C1"))||
                    (array[i].substring(0, 2).contains("D1"))||
                    (array[i].substring(0, 2).contains("E1"))||
                    (array[i].substring(0, 2).contains("F1"))||
                    (array[i].substring(0, 2).contains("G1"))||
                    (array[i].substring(0, 2).contains("H1"))||
                    (array[i].substring(0, 2).contains("H2"))||
                    (array[i].substring(0, 2).contains("H3"))||
                    (array[i].substring(0, 2).contains("H4"))||
                    (array[i].substring(0, 2).contains("H5"))||
                    (array[i].substring(0, 2).contains("K1"))||
                    (array[i].substring(0, 2).contains("K2"))||
                    (array[i].substring(0, 2).contains("K3"))||
                    (array[i].substring(0, 2).contains("K4"))||
                    (array[i].substring(0, 2).contains("K5"))
            )
            {
                Mycard card = new Mycard(this);

                card.card_title = array[i].substring(0, 2);

                endOfName = array[i].length();

                card.subject_title = array[i].substring(2, endOfName);

                final int finalI = i;

                final int finalEndOfName = endOfName;

                final int finalI1 = i;

                card.setOnClickListener(new Card.OnCardClickListener() {
                    @Override
                    public void onClick(Card card, View view) {

                        Intent intent = new Intent(HomeScreen.this, Material_Downloaded.class);
                        intent.putExtra("subject_title", array[finalI1].substring(2, finalEndOfName));
                        startActivity(intent);

                    }
                });

                cards.add(card);
            }
            else if(
                    (array[i].substring(0, 2).contains("TA2"))||
                    (array[i].substring(0, 2).contains("TB2"))||
                    (array[i].substring(0, 2).contains("TC2"))||
                    (array[i].substring(0, 2).contains("TD2"))||
                    (array[i].substring(0, 2).contains("TE2"))||
                    (array[i].substring(0, 2).contains("TF2"))||
                    (array[i].substring(0, 2).contains("TG2"))||
                    (array[i].substring(0, 2).contains("TA1"))||
                    (array[i].substring(0, 2).contains("TB1"))||
                    (array[i].substring(0, 2).contains("TC1"))||
                    (array[i].substring(0, 2).contains("TD1"))||
                    (array[i].substring(0, 2).contains("TE1"))||
                    (array[i].substring(0, 2).contains("TF1"))||
                    (array[i].substring(0, 2).contains("TG1")))
            {
                Mycard card = new Mycard(this);

                card.card_title = array[i].substring(0, 3);

                endOfName = array[i].length();

                card.subject_title = array[i].substring(3, endOfName);

                final int finalI = i;

                final int finalEndOfName = endOfName;

                final int finalI1 = i;

                card.setOnClickListener(new Card.OnCardClickListener() {
                    @Override
                    public void onClick(Card card, View view) {

                        Intent intent = new Intent(HomeScreen.this, Material_Downloaded.class);
                        intent.putExtra("subject_title", array[finalI1].substring(3, finalEndOfName));
                        startActivity(intent);

                    }
                });

                cards.add(card);

            }
            else{System.out.println("False data");}
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(this, cards);

        CardListView listView = (CardListView) this.findViewById(R.id.myList);

        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }

    }

    private static MaterialShowcaseView create(Activity activity, View view, int content, String id, Integer radius)
    {
        MaterialShowcaseView.Builder builder = new MaterialShowcaseView.Builder(activity)
                .setTarget(view)
                        //.setDismissText(button)
                        //.setDismissTextColor(Tools.getThemeColor(activity, R.attr.colorPrimary))
                .setMaskColour(Color.argb(150, 0, 0, 0))
                .setContentText(content)
                .setDismissOnTouch(true)
                .setDelay(0); // optional but starting animations immediately in onCreate can make them choppy

        if (radius != null)
        {
            builder.setUseAutoRadius(false);
            builder.setRadius(radius);
        }
        else
            builder.setUseAutoRadius(true);

        if (id != null)
            builder.singleUse(id); // provide a unique ID used to ensure it is only shown once

        MaterialShowcaseView showcaseView = builder.build();
        return showcaseView;
    }

    private void addDrawerItems() {

        String[] itemArray = {"Refresh", "Themes", "The Team","Share"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        refreshData();
                        break;
                    case 1:
                        setThemeForApp();
                        break;
                    case 2:
                        teamInfo();
                        break;
                    case 3:
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "Hey!\nI'm using new VIT Slate app to download and manage my course material directly through my phone.\nTry it out!\nhttps://play.google.com/store/apps/details?id=com.slate.vit.demo.vitslate";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        break;

                }

            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(Html.fromHtml("VIT<b>Slate</b>"));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(Html.fromHtml("VIT<b>Slate</b>"));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    //Getting cookies
    void refreshData() {

        try {
            //Network operation cant be done on main thread
            class asynctask extends AsyncTask<Void, Void, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //Progress Bar
                    progress = new ProgressDialog(HomeScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    progress.setMessage("Fetching Captcha");
                    progress.show();
                    progress.setCanceledOnTouchOutside(false);
                }

                @Override
                protected String doInBackground(Void... params) {
                    try {
                        //Getting cookiename from the cookies used in the below URL.
                        URLConnection connection = new URL("https://academics.vit.ac.in/student/stud_login.asp").openConnection();
                        List<String> cookies = connection.getHeaderFields().get("Set-Cookie");  //getting cookies in string


                        String cookieList = cookies.get(1);


                        //Go get the cookie name from below
                        cookieName = cookieList.substring(0, 20);


                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                    return cookieName;
                }

                protected void onPostExecute(final String cookieName) {
                    if (cookieName == null) {
                        progress.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
                        toast.show();
                    } else {
                        if (cookieName != null) {
                            //Phase 4: Go for login as cookieName is not null, means internet is there
                            if (!captchaDirectory.exists()) {
                                captchaDirectory.mkdirs();
                            }
                            captcha = captchaDirectory.toString();
                            try {
                                //Network operation cant be done on main thread
                                class asynctask2 extends AsyncTask<Void, Void, String> {
                                    @Override
                                    protected String doInBackground(Void... params) {
                                        try {

                                            //getting login page
                                            Connection.Response res = Jsoup.connect("https://academics.vit.ac.in/student/stud_login.asp").method(Connection.Method.GET).execute();
                                            sessionId = res.cookie(cookieName);

                                            // Reading the captcha
                                            Connection.Response captchaFetch = Jsoup.connect("https://academics.vit.ac.in/student/captcha.asp")
                                                    .cookie(cookieName, sessionId)
                                                    .ignoreContentType(true)
                                                    .method(Connection.Method.GET).timeout(20000).execute();

                                            //Writing out catpcha image
                                            FileOutputStream out = (new FileOutputStream(new java.io.File(captcha + "/captcha.jpg")));
                                            out.write(captchaFetch.bodyAsBytes());
                                            out.close();

                                            //set the cookie parameters
                                            cookieParams = Arrays.asList(cookieName, sessionId);
                                            progress.dismiss();


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            progress.dismiss();
                                            return "false";
                                        }
                                        return "Success";
                                    }

                                    protected void onPostExecute(String value) {
                                        System.out.println("Captcha Fetched");
                                        if(value.equals("false"))
                                        {Toast toast = Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
                                            toast.show();}
                                        //Attempt final login
                                        else{captchaBuilder();}


                                    }
                                }
                                asynctask2 task = new asynctask2();
                                task.execute();
                            }
                            catch (Exception e) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
                                toast.show();
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            asynctask task = new asynctask();
            task.execute();
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
            toast.show();
            e.printStackTrace();
        }
    }

    //Method to show captcha and then called refreshData() from it
    public void captchaBuilder()
    {
        View v;
        //Setting up alert dialog
        AlertDialog.Builder captchaBuilder = new AlertDialog.Builder(HomeScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        LayoutInflater factory = LayoutInflater.from(HomeScreen.this);
        v = factory.inflate(R.layout.custom_dialog, null);

        //using UI elements
        String captcha = captchaDirectory.toString()+"/captcha.jpg";
        Bitmap bmp = BitmapFactory.decodeFile(captcha);
        ImageView image= (ImageView) v.findViewById(R.id.captcha);
        image.setImageBitmap(bmp);

        final EditText takeCaptcha = (EditText) v.findViewById(R.id.takeCaptcha);
        takeCaptcha.setGravity(Gravity.CENTER);
        captchaBuilder.setView(v);

        // Set up the buttons
        captchaBuilder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verCode = takeCaptcha.getText().toString().toUpperCase();
                //Final submission attempt
                try
                {
                    //Network operation cant be done on main thread
                    class asynctask3 extends AsyncTask<Void, Void , String>
                    {
                        String value = " ";
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            //Progress Bar
                            progress = new ProgressDialog(HomeScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                            progress.setMessage("Getting new materials");
                            progress.show();
                        }
                        @Override
                        protected String doInBackground(Void... params) {
                            try
                            {
                                final String cookieName = cookieParams.get(0);
                                final String sessionId = cookieParams.get(1);
                                //Posting data

                                //Submit the information
                                Connection.Response login = Jsoup.connect("https://academics.vit.ac.in/student/stud_login_submit.asp")
                                        .userAgent("Mozilla")
                                        .cookie(cookieName, sessionId)
                                        .data("regno", userName).data("passwd",passWord).data("vrfcd",verCode)
                                        .method(Connection.Method.POST)
                                        .execute();

                                Document docDelete = login.parse(); //Used here to parse response as a document, then finally I will use document as a string
                                String result  = docDelete.toString();

                                //Find word "come" in the response after submission, if you get it then set result as true
                                if(result.contains("come"))
                                {
                                    value =  "true";
                                    //skipping riviera page
                                    try
                                    {
                                        Connection.Response home_page = Jsoup.connect("https://academics.vit.ac.in/student/stud_home.asp")
                                                .cookie(cookieName, sessionId)
                                                .ignoreContentType(true)
                                                .method(Connection.Method.GET).timeout(20000).execute();

                                        Connection.Response riviera_home_open = Jsoup.connect("https://academics.vit.ac.in/student/stud_riviera_home.asp")
                                                .cookie(cookieName, sessionId)
                                                .ignoreContentType(true)
                                                .method(Connection.Method.GET).timeout(20000).execute();


                                        Connection.Response riviera = Jsoup.connect("https://academics.vit.ac.in/student/stud_riviera_home_submit.asp")
                                                .userAgent("Mozilla")
                                                .cookie(cookieName, sessionId)
                                                .data("perdetcmd", "Skip Now")
                                                .method(Connection.Method.POST)
                                                .execute();

                                        System.out.println("After skipping riviera");

                                        Connection.Response home_page_2 = Jsoup.connect("https://academics.vit.ac.in/student/stud_home.asp")
                                                .cookie(cookieName, sessionId)
                                                .ignoreContentType(true)
                                                .method(Connection.Method.GET).timeout(20000).execute();

                                    }
                                    catch (Exception e){e.printStackTrace();}
                                }

                                else{value = "false";}


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return value;
                        }
                        protected void onPostExecute(String value)
                        {
                            progress.dismiss();
                            if(value.contains("true"))
                            {
                                new Delete().from(Course_Page_DB.class).execute();
                                new Delete().from(Course_Page_Downloading.class).execute();
                                new Delete().from(Course_Page_Final_DB.class).execute();
                                new Delete().from(Doc_Save_DB.class).execute();
                                new Delete().from(Doc_Save_Final_DB.class).execute();
                                new Delete().from(Slots_DB.class).execute();
                                new Delete().from(Subject_Code_DB.class).execute();
                                setRefreshedData();
                            }
                            else
                            {
                                Toast toast = Toast.makeText(getApplicationContext(),"Wrong captcha entered", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL,0,150);
                                toast.show();
                            }

                        }
                    }
                    asynctask3 task = new asynctask3();
                    task.execute();

                }
                catch (Exception e)
                {

                    e.printStackTrace();
                }
            }
        });
        captchaBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        captchaBuilder.show();
    }


    void setRefreshedData()
    {
        //getting the SUBJECT CODES, as they are used in URL for opening specific course page
        class asynctask5 extends AsyncTask<Void, Void , String> {

            @Override
            protected String doInBackground(Void... params) {
                String content = "hello";
                try{

                    List<Subject_Code_DB> sub_codes = new Select().from(Subject_Code_DB.class).execute();
                    int lenOfCodes = sub_codes.size();


                    for (int i = 0; i < lenOfCodes; i++) {

                        //Fetched teacher name and the subject code, now I need to fetch the slot names
                        //Fetch course page
                        String getParameter = sub_codes.get(i).number;

                        String course_page_url = "https://academics.vit.ac.in/student/coursepage_view.asp?sem=WS&crs=";
                        Document cp_view = Jsoup.connect(course_page_url + getParameter)
                                .cookie(cookieName, sessionId)
                                .get();
                        Document re_cp_view = Jsoup.connect(course_page_url + getParameter)
                                .cookie(cookieName, sessionId)
                                .get();

                        Doc_Save_DB entry = new Doc_Save_DB();
                        entry.doc = re_cp_view.toString();
                        entry.save();

                    }


                }
                catch(Exception e)
                {

                    e.printStackTrace();
                    Log.d("Debug", "533");
                    Intent i2 = new Intent(HomeScreen.this , HomeScreen.class);
                    i2.putExtra("Passcode", "Connection Lost. Please refresh.");
                    startActivity(i2);

                }
                return content;
            }

            protected void onPostExecute(String content) {

                setDataToTableForAsyncTask5();


            }
        }


        //getting the SUBJECT CODES, as they are used in URL for opening specific course page
        class aynctask4 extends AsyncTask<Void, Void , String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //Progress Bar
                progress = new ProgressDialog(HomeScreen.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                progress.setMessage("Fetching Course Materials (0%)");
                progress.show();
                progress.setCanceledOnTouchOutside(false);

            }

            @Override
            protected String doInBackground(Void... params) {
                String content = "hello";
                try{

                    int i=1;
                    {
                        Document winterSubCodes = Jsoup.connect("https://academics.vit.ac.in/student/timetable_ws.asp")
                                .cookie(cookieName, sessionId)
                                .get();

                        //getting slots again (This moment when my heart alomost stopped breathing)
                        Document reWinterSubCodes = Jsoup.connect("https://academics.vit.ac.in/student/timetable_ws.asp")
                                .cookie(cookieName, sessionId)
                                .get();


                        Elements registration = reWinterSubCodes.select("table[width=1200]");

                        //cellspacing="0" cellpadding="2" border="1" width="1200" style="border-collapse: collapse" bordercolor="#000000"  -- select this only
                        /* System.out.println(registration); */

                        //select the rows among the table for extraction

                        Elements rows = registration.select("tr");

                        for (i = 1; i < rows.size() - 2; i++) {
                        /*
                        first row is the col names so skip it and last two are waste
                        getting the rows(meaning iterating over each tr element)
                        */
                            Element row = rows.get(i);
                        /* getting the columns(means the td values) */
                            Elements cols = row.select("td");

                            if (cols.get(7).text().contains("+L") || cols.get(4).text().contains("Soft Skills"))
                            {
                                System.out.println("Lab encountered or soft skills");

                            } else {
                                if (cols.get(9).text().contains("+")) {
                                    Slots_DB slot = new Slots_DB();
                                    slot.name = (cols.get(9).text()).substring(0,2) + (cols.get(4).text());
                                    slot.save();
                                    //saving the subject codes
                                    Subject_Code_DB sub_code = new Subject_Code_DB();
                                    sub_code.number = cols.get(3).text();
                                    sub_code.save();
                                }
                                else {
                                    //saving the subject name and slot
                                    Slots_DB slot = new Slots_DB();
                                    slot.name = (cols.get(9).text()) + (cols.get(4).text());
                                    slot.save();
                                    //saving the subject codes
                                    Subject_Code_DB sub_code = new Subject_Code_DB();
                                    sub_code.number = cols.get(3).text();
                                    sub_code.save();
                                }
                            }
                        }
                    }


                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Intent i2 = new Intent(HomeScreen.this, HomeScreen.class);
                    i2.putExtra("Passcode", "Connection Lost. Please refresh.");
                    startActivity(i2);

                }
                return content;
            }

            protected void onPostExecute(String content)
            {
                asynctask5 taskCP = new asynctask5();
                taskCP.execute();

            }
        }


        aynctask4 task = new aynctask4();
        task.execute();
    }

    void setDataToTableForAsyncTask5()
    {

        List<Doc_Save_DB> items = new Select().from(Doc_Save_DB.class).execute();
        final int len = items.size();

        for(int j=0; j<len; j++)
        {
            String re_cp_view_string = items.get(j).doc;

            Document re_cp_view = Jsoup.parse(re_cp_view_string);

            //Get the table from it
            Elements cp_viewed = re_cp_view.select("table[width=800]");
            //Select rows
            Elements cp_rows = cp_viewed.select("tr");

            for (int i = 1; i < cp_rows.size(); i++) {
                //first row is the col names so skip it and last two are waste
                //getting the rows(meaning iterating over each tr element)

                Element cp_row = cp_rows.get(i);
                // getting the columns(means the td values)
                Elements cols = cp_row.select("td");
                //saving the data
                if(cols.get(5).text().contains("+L")){System.out.println("Lab");}
                else
                {
                    Course_Page_DB entry = new Course_Page_DB();
                    entry.Course_Code = (cols.get(0).text());
                    entry.Course_Title = (cols.get(1).text());
                    entry.Course_Type = (cols.get(2).text());
                    entry.Faculty = (cols.get(3).text());
                    entry.Class_number = (cols.get(4).text());
                    entry.Slots = (cols.get(5).text());
                    entry.para = cols.select("input[name=crsplancode]").attr("value");
                    entry.save();
                }

            }
        }


        class asynctask6 extends AsyncTask<Void, Integer , Integer> {

            @Override
            protected Integer doInBackground(Void... params) {

                try{

                    List<Course_Page_DB> sub_codes = new Select().from(Course_Page_DB.class).execute();
                    int lenOfCodes = sub_codes.size();
                    contentAsynctask6 = lenOfCodes;



                    for (int i = 0; i <lenOfCodes; i++) {

                        //Fetched teacher name and the subject code, now I need to fetch the slot names
                        //Fetch course page

                        publishProgress(((i * 4900) / (50 * lenOfCodes)));

                        Connection.Response doc = Jsoup.connect("https://academics.vit.ac.in/student/coursepage_view3.asp")
                                .userAgent("Mozilla")
                                .data("sem", "WS").data("crsplancode",sub_codes.get(i).para).data("crpnvwcmd", "View")
                                .method(Connection.Method.POST)
                                .cookie(cookieName, sessionId)
                                .execute();

                        Document re_cp_view = doc.parse();

                        Doc_Save_Final_DB entry = new Doc_Save_Final_DB();
                        entry.doc = re_cp_view.toString();
                        entry.save();


                    }


                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Intent i2 = new Intent(HomeScreen.this , HomeScreen.class);
                    i2.putExtra("Passcode", "Connection Lost. Please refresh.");
                    startActivity(i2);
                }
                return contentAsynctask6;
            }


            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

                progress.setMessage("Fetching Course Materials (" + String.valueOf(values[0])+"%)");


            }
            protected void onPostExecute(Integer content)
            {
                setdata task6 = new setdata();
                task6.execute();

            }
        }

        asynctask6 taskCP = new asynctask6();
        taskCP.execute();

    }

    class setdata extends AsyncTask<Void, Void , String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress.setMessage("Revitalizing the design");

        }


        @Override
        protected String doInBackground(Void... params)
        {
            String content = "hello";
            try{
                Log.d("LoopDebug", "In setDataToTableFor6");

                List<Doc_Save_Final_DB> items = new Select().from(Doc_Save_Final_DB.class).execute();

                Element anchor;
                Element anchorFont;
                Element get0, get1, get5;
                String cp_final_string = " ";
                Document re_cp_view;

                for (int i = 0; i < contentAsynctask6; i++) {
                    //display = 98 + ((i * 2) / (content + 1));
                    //progress.setMessage("Fetching Course Materials (" + String.valueOf(display) + "%)");


                    cp_final_string = items.get(i).doc;
                    if (!(cp_final_string.contains("-- Select --"))) {

                        re_cp_view = Jsoup.parse(cp_final_string);

                        //Get the root table from it
                        Elements tableSelector = re_cp_view.select("table[width=79%]");

                        //get inner tables
                        tableSelector = tableSelector.select("tr");

                        Element innerTableSelector = tableSelector.first();

                        Elements finalSelector = innerTableSelector.select("table");

                        get0 = finalSelector.get(0); //number indicates table order in HTML
                        get1 = finalSelector.get(1);
                        get5 = finalSelector.get(5);



                        //Saving class number in Course_Page_Final_DB
                        Elements cp_rows0 = get0.select("tr");
                        Element cp_row0 = cp_rows0.get(1);
                        Elements cols0 = cp_row0.select("td");

                        //Removed: Getting text/reference material section
                                /*Elements cp_rows1 = get1.select("a");
                                get1len = cp_rows1.size();

                                if (get1len > 0) {
                                    for (int j = 0; j < get1len; j++) {
                                        anchor = cp_rows1.get(j);

                                        anchorFont = anchor.select("font").first();
                                        Course_Page_Final_DB entry = new Course_Page_Final_DB();

                                        entry.Class_number = cols0.get(5).text();
                                        entry.Topic = "Reference Material";
                                        entry.Link = cp_rows1.attr("href");
                                        entry.fileName = anchorFont.text();

                                        entry.save();
                                    }
                                }*/

                        //Fetching main course material
                        Elements cp_rows5 = get5.select("tr");
                        int get5len = cp_rows5.size();
                        String category = "CAT-1";
                        int toggle = 0;
                        String checker = "<font color=\"red\">";

                        System.out.println(String.valueOf(get5len));

                        for (int j = 0; j < (get5len - 1); j++) {

                            Element cp_row4 = cp_rows5.get(j);



                            if (cp_row4.toString().contains(checker)) {
                                if (toggle == 0) {
                                    toggle++;
                                    category = "CAT-2";
                                    Log.d("Debug","Toggle"+String.valueOf(toggle)+"Position"+String.valueOf(j));

                                }
                                else if (toggle == 1) {
                                    toggle++;
                                    category = "TEE";
                                    Log.d("Debug","Toggle"+String.valueOf(toggle)+"Position"+String.valueOf(j));

                                }
                                else if (toggle == 2) {
                                    toggle = 0;
                                    category = "CAT-1";
                                    Log.d("Debug","Toggle"+String.valueOf(toggle)+"Position"+String.valueOf(j));

                                }

                            }
                            else
                            {
                                Elements a4 = cp_row4.select("a");
                                int alen = a4.size();
                                if (alen > 0) {
                                    for (int k = 0; k < alen; k++) {
                                        anchor = a4.get(k);
                                        anchorFont = anchor.select("font").first();
                                        Course_Page_Final_DB entry = new Course_Page_Final_DB();

                                        entry.Class_number = cols0.get(5).text();
                                        entry.Topic = category;
                                        entry.Link = anchor.attr("href");
                                        entry.fileName = anchorFont.text();

                                        Log.d("Saving", anchorFont.text());

                                        entry.save();
                                    }
                                }

                            }
                        }

                    }
                }

                progress.dismiss();
                Log.d("Go to Home Screen", "Going to Home Screen from Home screen");



            }
            catch (Exception e){
                e.printStackTrace();
                Intent i2 = new Intent(HomeScreen.this,HomeScreen.class);
                i2.putExtra("Passcode","Connection Lost. Please refresh.");
                startActivity(i2);
            }

            return content;
        }

        protected void onPostExecute(String content)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
            toast.show();
            Intent i2 = new Intent(HomeScreen.this,HomeScreen.class);
            i2.putExtra("Passcode", "0");
            startActivity(i2);
        }
    }

    void setThemeForApp()
    {
        final SharedPreferences.Editor editor = sharedpreferences.edit();

        final CharSequence[] items = { "RMB", "Suits", "Permanent Roommates", "Iron Man", "Pitchers" };

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
        builder.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        switch(items[item].toString())
                        {
                            case "RMB":
                                editor.putString("action_bar", "#035de7");
                                editor.putString("button_color", "#FF4081");
                                editor.putString("button_colorPressed", "#E91E63");
                                editor.commit();
                                break;
                            case "Suits":
                                editor.putString("action_bar", "#212121");
                                editor.putString("button_color", "#d50000");
                                editor.putString("button_colorPressed", "#b71c1c");
                                editor.commit();
                                break;
                            case "Permanent Roommates":
                                editor.putString("action_bar", "#FF4081");
                                editor.putString("button_color", "#035de7");
                                editor.putString("button_colorPressed", "#023e8e");
                                editor.commit();
                                break;
                            case "Iron Man":
                                editor.putString("action_bar", "#f30101");
                                editor.putString("button_color", "#ffcf1c");
                                editor.putString("button_colorPressed", "#DAA520");
                                editor.commit();
                                break;
                            case "Pitchers":
                                editor.putString("action_bar", "#94b900");
                                editor.putString("button_color", "#3e5b00");
                                editor.putString("button_colorPressed", "#293e00");
                                editor.commit();
                                break;
                            default:
                                editor.putString("action_bar", "#007700");
                                editor.putString("button_color", "#FF4081");
                                editor.putString("button_colorPressed", "#E91E63");
                                editor.commit();
                                break;
                        }
                    }
                });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Intent i2 = new Intent(HomeScreen.this, HomeScreen.class);
                i2.putExtra("Passcode", "0");
                startActivity(i2);

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }



    void teamInfo()
    {

        Intent i2 = new Intent(HomeScreen.this, Team.class);
        startActivity(i2);

    }

    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}


