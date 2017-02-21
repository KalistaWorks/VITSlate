package com.slate.vit.vitslate;

/**
 * Created by Aayush Karwatkar on 24-Sep-15.
 */
public class Model {

    private String name;
    public boolean selected;

    public Model(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
