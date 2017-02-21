package com.slate.vit.vitslate;

        import com.activeandroid.Model;
        import com.activeandroid.annotation.Column;
        import com.activeandroid.annotation.Table;
        import com.activeandroid.query.Select;

        import java.util.List;

/**
 * Created by Aayush Karwatkar on 15-Sep-15.
 */

@Table(name = "CoursePageFinal")
public class Course_Page_Final_DB extends Model {

    @Column(name = "Class_number")
    public String Class_number;

    @Column(name = "Topic")
    public String Topic;
    //As in assignment or reference material or cat1 or cat2 or tee

    @Column(name = "fileName")
    public String fileName;

    @Column(name = "Link")
    public String Link;

    public Course_Page_Final_DB() {
        super();
    }

    //Incoming is  for string being passed
    public Course_Page_Final_DB(String Class_Number_incoming, String topic_incoming,
                                String fileName_incoming , String link_incoming) {

        super();
        this.Class_number = Class_Number_incoming;
        this.Topic = topic_incoming;//Topic : Reference material or the "lecture topic"
        this.fileName = fileName_incoming;
        this.Link = link_incoming;

    }

    public static Course_Page_Final_DB getRandom() {
        return new Select().from(Course_Page_Final_DB.class).orderBy("RANDOM()").executeSingle();
    }

    public static List<Course_Page_Final_DB> getAll() {

        // This is how you execute a query

        return new Select()
                .from(Course_Page_Final_DB.class)
                .execute();
    }
}