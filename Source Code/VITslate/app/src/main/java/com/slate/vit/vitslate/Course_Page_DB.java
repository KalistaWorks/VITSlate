package com.slate.vit.vitslate;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Aayush Karwatkar on 15-Sep-15.
 */

@Table(name = "CoursePage")
public class Course_Page_DB extends Model {

    @Column(name = "Course_Code")
    public String Course_Code;

    @Column(name = "Course_Title")
    public String Course_Title;

    @Column(name = "Course_Type")
    public String Course_Type;

    @Column(name = "Faculty")
    public String Faculty;

    @Column(name = "Class_number")
    public String Class_number;

    @Column(name = "Slots")
    public String Slots;

    //Button params for sending post request
    @Column(name = "para")
    public String para;

    public Course_Page_DB() {
        super();
    }

    //Incoming is  for string being passed
    public Course_Page_DB(String course_code_incoming , String Course_Title_incoming ,
                          String Course_Type_incoming, String Faculty_incoming ,
                          String Class_Number_incoming, String Slots_incoming,
                          String para_incoming) {

        super();
        this.Course_Code = course_code_incoming;
        this.Course_Title = Course_Title_incoming;
        this.Course_Type = Course_Type_incoming;
        this.Faculty = Faculty_incoming;
        this.Class_number = Class_Number_incoming;
        this.Slots = Slots_incoming;
        this.para = para_incoming;

    }

    public static Course_Page_DB getRandom() {
        return new Select().from(Course_Page_DB.class).orderBy("RANDOM()").executeSingle();
    }

    public static List<Course_Page_DB> getAll() {

        // This is how you execute a query

        return new Select()
                .from(Course_Page_DB.class)
                .execute();
    }
}