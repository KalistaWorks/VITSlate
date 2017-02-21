package com.slate.vit.vitslate;

        import com.activeandroid.Model;
        import com.activeandroid.annotation.Column;
        import com.activeandroid.annotation.Table;
        import com.activeandroid.query.Select;

        import java.util.List;

/**
 * Created by Aayush Karwatkar on 15-Sep-15.
 */

@Table(name = "CoursePageDownloads")
public class Course_Page_Downloading extends Model {


    @Column(name = "Link")
    public String Link;

    public Course_Page_Downloading() {
        super();
    }

    //Incoming is  for string being passed
    public Course_Page_Downloading(String link_incoming) {

        super();
        this.Link = link_incoming;

    }

    public static Course_Page_Downloading getRandom() {
        return new Select().from(Course_Page_Downloading.class).orderBy("RANDOM()").executeSingle();
    }

    public static List<Course_Page_Downloading> getAll() {

        // This is how you execute a query

        return new Select()
                .from(Course_Page_Downloading.class)
                .execute();
    }
}