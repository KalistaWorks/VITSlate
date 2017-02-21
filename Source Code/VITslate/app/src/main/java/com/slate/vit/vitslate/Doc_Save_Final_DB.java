package com.slate.vit.vitslate;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Aayush Karwatkar on 17-Sep-15.
 */
@Table(name = "DocFinal")
public class Doc_Save_Final_DB extends Model {

    @Column(name = "doc")
    public String doc;

    public Doc_Save_Final_DB() {
        super();
    }

    //Incoming is  for string being passed
    public Doc_Save_Final_DB(String doc_incoming) {

        super();
        this.doc = doc_incoming;
    }

    public static Doc_Save_Final_DB getRandom() {
        return new Select().from(Slots_DB.class).orderBy("RANDOM()").executeSingle();
    }

    public static List<Doc_Save_Final_DB> getAll() {

        // This is how you execute a query

        return new Select()
                .from(Doc_Save_Final_DB.class)
                .execute();
    }
}