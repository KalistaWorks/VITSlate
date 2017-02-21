package com.slate.vit.vitslate;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Aayush Karwatkar on 25-Sep-15.
 */
public class MyAdapter extends ArrayAdapter<Model> {
    private final List<Model> list;
    private final Activity context;
    boolean checkAll_flag = false;
    boolean checkItem_flag = false;

    public MyAdapter(Activity context, List<Model> list) {
        super(context, R.layout.teacher_material_row, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
        protected GifImageView gif;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            convertView = inflator.inflate(R.layout.teacher_material_row, null);
            viewHolder = new ViewHolder();

            viewHolder.text = (TextView) convertView.findViewById(R.id.text2);

            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.check);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            viewHolder.gif = (GifImageView) convertView.findViewById(R.id.gif);

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.text2, viewHolder.text);
            convertView.setTag(R.id.check, viewHolder.checkbox);
            convertView.setTag(R.id.gif, viewHolder.gif);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try
        {
            viewHolder.checkbox.setTag(position); // This line is important.

            int linkLength = list.get(position).getName().length();

            viewHolder.text.setText(list.get(position).getName().substring(82, linkLength));

            viewHolder.checkbox.setChecked(list.get(position).isSelected());

            viewHolder.gif.setVisibility(View.GONE);

        }
        catch (Exception e){e.printStackTrace();}

        return convertView;
    }



}
