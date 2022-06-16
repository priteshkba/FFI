package com.ffi.add_address;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ffi.R;

import java.util.ArrayList;

/**
 * Created by admin on 01/05/2017.
 */

public class SpinnerStateAdapter extends BaseAdapter {

    ArrayList<DataX> itemArray;
    Activity mActivity;

    public SpinnerStateAdapter(Activity activity, ArrayList<DataX> array) {
        this.itemArray = array;
        this.mActivity = activity;
    }

    @Override
    public int getCount() {
        return itemArray.size();
    }

    @Override
    public Object getItem(int position) {
        return itemArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder {
        TextView tvItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.custom_spinner_text, null);

            mHolder = new ViewHolder();

            mHolder.tvItem = convertView.findViewById(R.id.tv_item);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.tvItem.setText(itemArray.get(position).getStateName());
        return convertView;
    }
}