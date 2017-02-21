package com.slate.vit.vitslate;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Aayush Karwatkar on 15-Sep-15.
 */
@Table(name = "SubCode")
public class Subject_Code_DB extends Model {

    //For opening the course page
    @Column(name = "Number")
    public String number;

    public Subject_Code_DB() {
        super();
    }

    public Subject_Code_DB(String code_name) {
        super();
        this.number = code_name;

    }

    public static Slots_DB getRandom() {
        return new Select().from(Subject_Code_DB.class).orderBy("RANDOM()").executeSingle();
    }

    public static List<Subject_Code_DB> getAll() {

        // This is how you execute a query

        return new Select()
                .from(Subject_Code_DB.class)
                .execute();
    }
}
