package com.slate.vit.vitslate;

import android.app.AlertDialog;
import android.app.NotificationManager;
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
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.rey.material.widget.Button;
import com.software.shell.fab.ActionButton;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Teacher_Material extends ActionBarActivity implements AdapterView.OnItemClickListener {

    String subject_title = " ", class_number = " ", faculty = " ";
    ArrayAdapter<Model> adapter;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle = " ";
    List<Model> list = new ArrayList<Model>();
    List<Course_Page_Final_DB> items;
    List<Course_Page_Downloading> itemsDownload;
    String[][] array_code = new String[1000][];
    int toogleSelect = 0;
    List<String> downloadThis = new ArrayList<String>();
    File appDirectory;
    String value = "";
    int counterForToast;

    String action_bar = " ", button_color = " ", button_colorPressed = " ";
    SharedPreferences sharedpreferences;
    ProgressDialog progress;
    String rootDirectory = Environment.getExternalStorageDirectory().toString();
    File captchaDirectory = new File(rootDirectory + "/VITSlate/.TrashBinData");
    String captcha = " ";
    List<String> cookieParams;
    String sessionId = " ";
    String cookieName = " ";
    Integer contentAsynctask6 = 0;
    String verCode = " ";
    String userName = " ";
    String passWord = " ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher__material);

        File captcha = new File(captchaDirectory.toString()+"/captcha.jpg");
        captcha.delete();

        //Disabling certificates
        disableCertificates();

        //Intent Data
        subject_title = getIntent().getStringExtra("subject_title");
        class_number = getIntent().getStringExtra("class_number");
        faculty = getIntent().getStringExtra("faculty");

        appDirectory = new File(rootDirectory + "/VITSlate/WinSem2015/"+subject_title+"/"+faculty);

        if (!appDirectory.exists()) {

            appDirectory.mkdirs();

        }

        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        getSupportActionBar().setTitle(faculty);

        final String MyPREFERENCES = "MyPrefs" ;

        sharedpreferences=getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        userName=sharedpreferences.getString("nameKey", "");
        passWord=sharedpreferences.getString("passwordKey", "");
        action_bar=sharedpreferences.getString("action_bar", "#035de7");
        button_color=sharedpreferences.getString("button_color", "#FF4082");
        button_colorPressed=sharedpreferences.getString("button_colorPressed", "#E91E63");

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Set color of action bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(action_bar)));

        ActionButton actionButton = (ActionButton) findViewById(R.id.action_button);
        // To set the ActionButton size
        actionButton.setSize(70.0f); // in density-independent pixels
        actionButton.setShadowYOffset(5.0f);

        // To check whether image is present:
        boolean hasImage = actionButton.hasImage();

        // To set an image (either bitmap, drawable or resource id):

        actionButton.setImageResource(R.drawable.icon_home_white);
        actionButton.setButtonColor(Color.parseColor(button_color));
        actionButton.setButtonColorPressed(Color.parseColor(button_colorPressed));

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent iHome = new Intent(getApplicationContext(), HomeScreen.class);
                iHome.putExtra("Passcode", "0");
                startActivity(iHome);


            }
        });

        // Get ListView object from xml
        final ListView listView = (ListView) findViewById(R.id.listMaterial);
        adapter = new MyAdapter(this, getModel());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);


        //Asynctask to download  items
        class asynctask extends AsyncTask<List<String>, Void, String>
        {
            int fetched_files_completely = 0;
            String[] stockArr = new String[0];

            @Override
            protected String doInBackground(List<String>... creds) {

                stockArr = new String[creds[0].size()];
                stockArr = creds[0].toArray(stockArr);
                int counterForDownloaded = 0;

                for(String s : stockArr)
                {
                    try {
                        counterForDownloaded++;

                        int linklength = s.length();

                        String encodedString = URLEncoder.encode(s.substring(82,linklength), "UTF-8");

                        encodedString = s.substring(0,82) + encodedString;

                        Connection.Response res = Jsoup.connect(encodedString).method(Connection.Method.GET)
                                .ignoreContentType(true)
                                .maxBodySize(1024 * 1024 * 10 * 10) //100 MB cap
                                .timeout(30000)
                                .execute();
                        int length = s.length();

                        FileOutputStream out = (new FileOutputStream(new java.io.File(appDirectory.toString() + "/"+s.substring(82,length))));
                        out.write(res.bodyAsBytes());
                        out.close();
                        fetched_files_completely++; //counter to get the files downloaded or not.
                        new Delete().from(Course_Page_Downloading.class).where("Link = ?", s).execute();

                        //Updating notification value
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Teacher_Material.this);
                        mBuilder.setSmallIcon(R.drawable.ic_notifi);
                        mBuilder.setContentTitle("Downloading Files (" + counterForDownloaded + "/" + counterForToast + ")");
                        mBuilder.setContentText("Ninja Algorithm in action!");
                        mBuilder.setOngoing(true);
                        // notificationID allows you to update the notification later on.
                        mNotificationManager.notify(9999, mBuilder.build());


                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        new Delete().from(Course_Page_Downloading.class).where("Link = ?", s).execute();
                    }
                }
                counterForDownloaded = 0;

                return String.valueOf(fetched_files_completely);
            }

            protected void onPostExecute(String value)
            {

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(9999);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Teacher_Material.this);
                mBuilder.setSmallIcon(R.drawable.ic_notifi);
                if(value.equals("0"))
                {
                    mBuilder.setContentTitle("Select the materials."); //change
                    mBuilder.setContentText("Click on the checkboxes.");
                    // notificationID allows you to update the notification later on.
                    mNotificationManager.notify(10000, mBuilder.build());
                    mNotificationManager.notify(10000, mBuilder.build());
                }
                else if(!value.equals("0"))
                {
                    mBuilder.setContentTitle(value + " downloaded out of " + String.valueOf(stockArr.length));
                    mBuilder.setContentText("Files available in subject card");
                    // notificationID allows you to update the notification later on.
                    mNotificationManager.notify(10000, mBuilder.build());
                    mNotificationManager.notify(10000, mBuilder.build());
                }

            }

        }




        //Asynctask to Check for internet
        class asynctask2 extends AsyncTask<List<String>, Void, String>
        {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //Notification bar
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Teacher_Material.this);
                mBuilder.setSmallIcon(R.drawable.ic_notifi);
                mBuilder.setContentTitle("Downloading Files...");
                mBuilder.setContentText("Ninja Algorithm in action!");
                mBuilder.setOngoing(true);
                // notificationID allows you to update the notification later on.
                mNotificationManager.notify(9999, mBuilder.build());


            }

            @Override
            protected String doInBackground(List<String>... creds) {
                try
                {
                    //Getting cookiename from the cookies used in the below URL.
                    URLConnection connection = new URL("https://academics.vit.ac.in/student/stud_login.asp").openConnection();
                    List<String> cookies = connection.getHeaderFields().get("Set-Cookie");  //getting cookies in string

                    String cookieList = cookies.get(1);

                    //Go get the cookie name from below
                    value = cookieList.substring(0, 20);


                }
                catch (Exception e)
                {

                    e.printStackTrace();
                }
                return value;
            }

            protected void onPostExecute(String value)
            {

                if(value.equals(""))
                {
                    for(String s : downloadThis)
                    {
                        new Delete().from(Course_Page_Downloading.class).where("Link = ?", s).execute();
                    }

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(9999);
                    Toast toast = Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
                    toast.show();
                    downloadThis = new ArrayList();

                    //showing toast to again open the teacher

                    Toast toastException = Toast.makeText(getApplicationContext(), "Re-open this teacher.", Toast.LENGTH_LONG);
                    toastException.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
                    toastException.show();

                    //Notification for re-open teacher
                    NotificationManager mNotificationManagerException = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Teacher_Material.this);
                    mBuilder.setSmallIcon(R.drawable.ic_notifi);
                    mBuilder.setContentTitle("Please re-open this teacher.");
                    mBuilder.setContentText("Then select your materials again");
                    // notificationID allows you to update the notification later on.
                    mNotificationManager.notify(9998, mBuilder.build());

                    //to remove all items from listview in caseof exception
                    listView.clearChoices();
                    adapter.notifyDataSetChanged();
                    listView.invalidateViews();

                }
                else if(!value.equals(""))
                {
                    adapter.notifyDataSetChanged();
                    //make changes in adapter and load asynctask
                    Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(counterForToast) + " item(s) queued for downloading.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
                    toast.show();
                    asynctask task = new asynctask();
                    //here
                    task.execute(downloadThis);
                    downloadThis = new ArrayList();

                }

            }

        }


        /*
        View headerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer_layout1, null , false);
        listViewCat1.addHeaderView(headerView);



        // Get ListView object from xml
        ListView listViewCat2 = (ListView) findViewById(R.id.listCat2);
        adapter = new MyAdapter(this, getModelCat2());
        listViewCat2.setAdapter(adapter);

        listViewCat2.setOnItemClickListener(this);

        // Get ListView object from xml
        final ListView listViewTee = (ListView) findViewById(R.id.listTee);
        adapter = new MyAdapter(this, getModelTee());
        listViewTee.setAdapter(adapter);

        listViewTee.setOnItemClickListener(this);
        */


        final Button buttonDownload = (Button) findViewById(R.id.buttonDownload);
        buttonDownload.setTextColor(Color.parseColor(action_bar));
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterForToast = 0;
                //Removing selected items
                for (Model integer : new ArrayList<>(list)) {
                    if (integer.isSelected()) {
                        list.remove(integer);
                        Log.d("Debug", "Removed");
                        Course_Page_Downloading entry = new Course_Page_Downloading();
                        entry.Link = integer.getName();
                        entry.save();

                        //Saving in a list to be passed to asynctask
                        downloadThis.add(integer.getName());

                        //increment counter
                        counterForToast++;
                    }

                }


                    asynctask2 task2 = new asynctask2();
                    task2.execute();
                    Toast toast = Toast.makeText(getApplicationContext(), "Connecting to server. Please wait...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
                    toast.show();


            }
        });

        final Button buttonSelectAll = (Button) findViewById(R.id.buttonSelectAll);
        buttonSelectAll.setTextColor(Color.parseColor(action_bar));
        buttonSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                if(toogleSelect == 0)
                {
                    for (int i = 0; i < list.size(); i++) {

                        list.get(i).setSelected(true);

                   }
                    buttonSelectAll.setText("De-select all");
                    toogleSelect=1;
                }
                else if(toogleSelect == 1) {
                    for (int i = 0; i < list.size(); i++) {

                        list.get(i).setSelected(false);

                    }
                    buttonSelectAll.setText("Select all");
                    toogleSelect=0;
                }
                adapter.notifyDataSetChanged();

            }
        });

    }




    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        TextView label = (TextView) v.getTag(R.id.text2);
        CheckBox checkbox = (CheckBox) v.getTag(R.id.check);


    }


    private String isCheckedOrNot(CheckBox checkbox) {
        if(checkbox.isChecked())
            return "is checked";
        else
            return "is not checked";
    }


    private List<Model> getModel() {


        //Populate list text elements
        items = new Select()
                .from(Course_Page_Final_DB.class)
                .where("Class_number = ?", class_number)
                .execute();

        itemsDownload = new Select()
                .from(Course_Page_Downloading.class)
                .execute();

        int len = items.size();

        array_code = new String[len][3];

        //Check if file exists or not in folder
        List<String> names = new ArrayList<String>();

        File appDirectory = new File(rootDirectory + "/VITSlate/WinSem2015/"+subject_title+"/"+faculty);

        for (File f : appDirectory.listFiles()) {
            if (f.isFile()){
                String name = f.getName();
                names.add(name);
                Log.d("Debug",name);
            }
        }

        //add downloading items elements into list "names"
        int len2 = itemsDownload.size();
        int i=0;
        for(i=0; i<len2 ;i++ )
        {
            try
            {
                int linkLength = itemsDownload.get(i).Link.length();
                String topic_full_name = itemsDownload.get(i).Link.substring(82, linkLength);
                names.add(topic_full_name);
            }
            catch (Exception e){e.printStackTrace();}
        }

        for (i = 0; i < len; i++)
        {

            array_code[i][0] = items.get(i).Link;
            array_code[i][1] = items.get(i).Topic;
            array_code[i][2] = items.get(i).Link;
            int linkLength = array_code[i][0].length();

            try
            {
                String topic_full_name = array_code[i][0].substring(82, linkLength);

                if(!(names.contains(topic_full_name))){

                    list.add(new Model(array_code[i][0]));

                }
            }
            catch (Exception e){e.printStackTrace();}

        }
        if(list.size() == 0)
        {
            Toast toast = Toast.makeText(getApplication() , "Sorry, nothing here." , Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
            toast.show();
        }
        //remove element in list which are in download list
        return list;
    }


    private void addDrawerItems() {

        String[] itemArray = {"Refresh", "Themes", "The Team"};
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
                getSupportActionBar().setTitle(faculty);
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

    public void disableCertificates() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {



        }
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
                    progress = new ProgressDialog(Teacher_Material.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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
        AlertDialog.Builder captchaBuilder = new AlertDialog.Builder(Teacher_Material.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        LayoutInflater factory = LayoutInflater.from(Teacher_Material.this);
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
                            progress = new ProgressDialog(Teacher_Material.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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
                    Intent iHome = new Intent(getApplicationContext(),Teacher_Material.class);
                    iHome.putExtra("subject_title", subject_title);
                    iHome.putExtra("class_number", class_number);
                    iHome.putExtra("faculty", faculty);
                    startActivity(iHome);

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
                progress = new ProgressDialog(Teacher_Material.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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
                    Intent iHome = new Intent(getApplicationContext(),Teacher_Material.class);
                    iHome.putExtra("subject_title", subject_title);
                    iHome.putExtra("class_number", class_number);
                    iHome.putExtra("faculty", faculty);
                    startActivity(iHome);

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
                    Intent iHome = new Intent(getApplicationContext(),Teacher_Material.class);
                    iHome.putExtra("subject_title", subject_title);
                    iHome.putExtra("class_number", class_number);
                    iHome.putExtra("faculty" , faculty);
                    startActivity(iHome);
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
                String cp_final_string;
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
                Intent iHome = new Intent(getApplicationContext(),Teacher_Material.class);
                iHome.putExtra("subject_title", subject_title);
                iHome.putExtra("class_number", class_number);
                iHome.putExtra("faculty" , faculty);
                startActivity(iHome);
            }

            return content;
        }

        protected void onPostExecute(String content)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
            toast.show();
            Intent iHome = new Intent(getApplicationContext(),Teacher_Material.class);
            iHome.putExtra("subject_title", subject_title);
            iHome.putExtra("class_number", class_number);
            iHome.putExtra("faculty" , faculty);
            startActivity(iHome);
        }
    }

    void setThemeForApp()
    {
        final SharedPreferences.Editor editor = sharedpreferences.edit();

        final CharSequence[] items = { "RMB", "Suits", "Permanent Roommates", "Iron Man", "Pitchers" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Teacher_Material.this);
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
                                editor.putString("action_bar", "#FF4082");
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
                                editor.putString("button_color", "#FF4082");
                                editor.putString("button_colorPressed", "#E91E63");
                                editor.commit();
                                break;
                        }
                    }
                });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Intent iHome = new Intent(getApplicationContext(),Teacher_Material.class);
                iHome.putExtra("subject_title", subject_title);
                iHome.putExtra("class_number", class_number);
                iHome.putExtra("faculty" , faculty);
                startActivity(iHome);

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

        Intent i2 = new Intent(Teacher_Material.this, Team.class);
        startActivity(i2);

    }

    public void onBackPressed()
    {

        Intent i2 = new Intent(Teacher_Material.this,Teacher.class);
        i2.putExtra("subject_title", subject_title);
        startActivity(i2);

    }
}

