package com.slate.vit.vitslate;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Aayush Karwatkar on 16-Jul-15.
 * Just a java file to save the slots in database.
 * Has two functions, select a random name from database and another to store values.
 */
@Table(name = "Slots")
public class Slots_DB extends Model{

    @Column(name = "Code")
    public String name;

    public Slots_DB() {
        super();
    }

    public Slots_DB(String name) {
        super();
        this.name = name;

    }

    public static Slots_DB getRandom() {
        return new Select().from(Slots_DB.class).orderBy("RANDOM()").executeSingle();
    }

    public static List<Slots_DB> getAll() {

        // This is how you execute a query

        return new Select()

                .from(Slots_DB.class)
                .execute();
    }
}

/*
Reference purpose
* @Table(name = "Categories")
public class Category extends Model {
    @Column(name = "Name")
    public String name;

    public Category() {
        super();
    }

    public Category(String name) {
        super();
        this.name = name;
    }

    public List<Item> items() {
        return getMany(Item.class, "Category");
    }
}
*/