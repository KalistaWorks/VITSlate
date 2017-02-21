package com.slate.vit.vitslate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.io.File;
import java.util.List;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class Login extends ActionBarActivity {

    //Initializing variables here
    private EditText username,password;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String name = "nameKey"; //dummy initialization
    public static final String pass = "passwordKey"; ////dummy initialization
    SharedPreferences sharedpreferences;

    ProgressDialog progress;
    String value="false";

    List<String> cookieParams;
    String sessionId = " ";
    String cookieName = " ";

    String rootDirectory = Environment.getExternalStorageDirectory().toString();
    File appDirectory = new File(rootDirectory + "/VITSlate");
    File captchaDirectory = new File(rootDirectory + "/VITSlate/.TrashBinData");
    String captcha;

    String verCode="XXXXXX";
    String userName="12XXX0XXX";
    String passWord="XXXXXXXXX"; //Dummy values
    String ownerName= " ";

    Integer contentAsynctask6 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //checking whether shared preferences are set or not
        sharedpreferences=getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(name))
        {
            if(sharedpreferences.contains(pass)){
                Intent i = new Intent(this,HomeScreen.class);
                i.putExtra("Passcode","0");
                startActivity(i);
            }
        }

        //Wake lock for screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Disabling certificates
        disableCertificates();

        //screen layout
        setContentView(R.layout.activity_login);

        //Initialize Active Android library so that we would save all the data database using it
        ActiveAndroid.initialize(this);


        //All relevant declarations and initializations
        username = (EditText)findViewById(R.id.uname_take); //username editText
        password = (EditText)findViewById(R.id.pass_take); //password editText
        Button button= (Button) findViewById(R.id.login_button); //Button for logging in
        final String pattern = "(\\d{2})([A-Z]{3})(\\d{4})"; //Pattern for matching username


        //if we don't have shared preferences set then we call the login function
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                //Phase1: check for empty strings
                userName = username.getText().toString().toUpperCase();
                passWord = password.getText().toString();

                if ((!userName.matches("")) && (!passWord.matches(""))) {

                    //Phase2: Check username field pattern using java regular expressions
                    if (userName.matches(pattern)) {

                        //phase3: Check for internet then proceed

                        try {
                            //Network operation cant be done on main thread
                            class asynctask extends AsyncTask<Void, Void, String> {
                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                    //Progress Bar
                                    progress = new ProgressDialog(Login.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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
                                                            Response res = Jsoup.connect("https://academics.vit.ac.in/student/stud_login.asp").method(Method.GET).execute();
                                                            sessionId = res.cookie(cookieName);


                                                            //Reading the captcha
                                                            Response captchaFetch = Jsoup.connect("https://academics.vit.ac.in/student/captcha.asp")
                                                                    .cookie(cookieName, sessionId)
                                                                    .ignoreContentType(true)
                                                                    .method(Method.GET).timeout(20000).execute();

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
                                                        else{captchaBuilder(v);}


                                                    }
                                                }
                                                asynctask2 task = new asynctask2();
                                                task.execute();
                                            } catch (Exception e) {
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


                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Check your registration number", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
                        toast.show();
                    }

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter both credentials", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 150);
                    toast.show();

                }

            }
        });

        //Saving all the items in database

    }



    public void captchaBuilder(View v)
    {
        //Setting up alert dialog
        AlertDialog.Builder captchaBuilder = new AlertDialog.Builder(Login.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        LayoutInflater factory = LayoutInflater.from(Login.this);
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

                        @Override
                        protected void onPreExecute()
                        {
                            super.onPreExecute();
                            //Progress Bar
                            progress = new ProgressDialog(Login.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                            progress.setMessage("Encrypting your credentials");
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
                                Response login = Jsoup.connect("https://academics.vit.ac.in/student/stud_login_submit.asp")
                                        .userAgent("Mozilla")
                                        .cookie(cookieName, sessionId)
                                        .data("regno", userName).data("passwd",passWord).data("vrfcd",verCode)
                                        .method(Method.POST)
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
                                        Response home_page = Jsoup.connect("https://academics.vit.ac.in/student/stud_home.asp")
                                                .cookie(cookieName, sessionId)
                                                .ignoreContentType(true)
                                                .method(Method.GET).timeout(20000).execute();

                                        Response riviera_home_open = Jsoup.connect("https://academics.vit.ac.in/student/stud_riviera_home.asp")
                                                .cookie(cookieName, sessionId)
                                                .ignoreContentType(true)
                                                .method(Method.GET).timeout(20000).execute();


                                        Response riviera = Jsoup.connect("https://academics.vit.ac.in/student/stud_riviera_home_submit.asp")
                                                .userAgent("Mozilla")
                                                .cookie(cookieName, sessionId)
                                                .data("perdetcmd", "Skip Now")
                                                .method(Method.POST)
                                                .execute();

                                        System.out.println("After skipping riviera");

                                        Response home_page_2 = Jsoup.connect("https://academics.vit.ac.in/student/stud_home.asp")
                                                .cookie(cookieName, sessionId)
                                                .ignoreContentType(true)
                                                .method(Method.GET).timeout(20000).execute();

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
                                Toast toast = Toast.makeText(getApplicationContext(),"Welcome", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL,0,150);
                                toast.show();
                                saveData();
                            }
                            else
                            {
                                Toast toast = Toast.makeText(getApplicationContext(),"Please check your credentials and try again.", Toast.LENGTH_LONG);
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


    public void saveData()
    {

        //Putting encrypted values into the prefernces, once after validation is done
        String saveUsername=userName;
        String savepassWord=passWord;

        CryptLib cryptObj = null;


        SharedPreferences.Editor editor = sharedpreferences.edit();
        /*try
        {
            saveUsername = cryptObj.encrypt(userName, "ninaksukhamaano" , "aiyada");
            savepassWord = cryptObj.encrypt(passWord, "ninaksukhamaano" , "aiyada");
        }
        catch(Exception e){e.printStackTrace();}
        */
        editor.putString(name, saveUsername);
        editor.putString(pass, savepassWord);
        editor.putString("action_bar", "#035de7");
        editor.putString("button_color", "#FF4081");
        editor.putString("button_colorPressed", "#E91E63");
        editor.commit();




        //getting the SUBJECT CODES, as they are used in URL for opening specific course page
        class asynctask5 extends AsyncTask<Void, Void , String> {

            @Override
            protected String doInBackground(Void... params) {
                String content = "hello";
                try{

                    // Reading the profile pic
                    Response profilePicFetch = Jsoup.connect("https://academics.vit.ac.in/student/view_photo.asp")
                            .cookie(cookieName, sessionId)
                            .ignoreContentType(true)
                            .method(Method.GET).timeout(20000).execute();

                    //Writing out profile pic image
                    FileOutputStream out = (new FileOutputStream(new java.io.File(captcha + "/.ProfilePic.jpg")));
                    out.write(profilePicFetch.bodyAsBytes());
                    out.close();

                    // Reading the profile name then saving to shared prefs
                    Response profileNameFetch = Jsoup.connect("https://academics.vit.ac.in/student/view_photo.asp")
                            .cookie(cookieName, sessionId)
                            .ignoreContentType(true)
                            .method(Method.GET).timeout(20000).execute();
                    Document proNameFetch = Jsoup.connect("https://academics.vit.ac.in/student/profile_personal_view.asp")
                            .cookie(cookieName, sessionId)
                            .get();

                    Elements proName = proNameFetch.select("table[style=border-collapse: collapse]");
                    Elements rows = proName.select("tr");
                    Element row = rows.get(1);
                    rows = row.select("td");
                    row = rows.get(1);

                    ownerName = row.text();
                    Log.d("owner" , ownerName);
                    //Fetched owner Name



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
                    Intent i2 = new Intent(Login.this , HomeScreen.class);
                    i2.putExtra("Passcode","Connection Lost. Please refresh.");
                    startActivity(i2);

                    e.printStackTrace();
                }
                return content;
            }

            protected void onPostExecute(String content) {

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("owner_name", ownerName);
                editor.commit();

                setDataToTableForAsyncTask5();


            }
        }


        //getting the SUBJECT CODES, as they are used in URL for opening specific course page
        class aynctask4 extends AsyncTask<Void, Void , String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //Progress Bar
                progress = new ProgressDialog(Login.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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

                    Intent i2 = new Intent(Login.this, HomeScreen.class);
                    i2.putExtra("Passcode","Connection Lost. Please refresh.");
                    startActivity(i2);

                    e.printStackTrace();
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

                        Response doc = Jsoup.connect("https://academics.vit.ac.in/student/coursepage_view3.asp")
                                .userAgent("Mozilla")
                                .data("sem", "WS").data("crsplancode",sub_codes.get(i).para).data("crpnvwcmd", "View")
                                .method(Method.POST)
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
                    Intent i2 = new Intent(Login.this , HomeScreen.class);
                    i2.putExtra("Passcode","Connection Lost. Please refresh.");
                    startActivity(i2);

                    e.printStackTrace();
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

            progress.setMessage("Finalizing the design");

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

                                 Elements finalSelector = innerTableSelector.select("table");//[width=95%]

                                 get0 = finalSelector.get(0); //number indicates table order in HTML
                                 get1 = finalSelector.get(1);
                                 get5 = finalSelector.get(5);//4



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
                         Log.d("Go to Home Screen", "Going to Home Screen from Login Screen");
                         Intent iHome = new Intent(Login.this, HomeScreen.class);
                         iHome.putExtra("Passcode", "0");
                         startActivity(iHome);
                     }
                     catch (Exception e){
                         Intent i2 = new Intent(Login.this,HomeScreen.class);
                         i2.putExtra("Passcode","Connection Lost. Please refresh.");
                         startActivity(i2);
                     }

            return content;
        }

        protected void onPostExecute(String content)
        {

        }
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

    //For activity resume functionality
    @Override
    protected void onResume() {
        sharedpreferences=getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(name))
        {
            if(sharedpreferences.contains(pass)){
                Intent i = new Intent(this,HomeScreen.class);
                i.putExtra("Passcode","0");
                startActivity(i);
            }
        }
        super.onResume();
    }

}
