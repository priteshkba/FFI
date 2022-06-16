package com.ffi.my_orderdetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ffi.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class customSpinnerAdapter extends BaseAdapter {

    @Nullable
    private  Context context;
    LayoutInflater inflter;
    @NotNull
    private ArrayList<ProductReturnReasonResponse.DataEntity> returnReturn;

    public customSpinnerAdapter(@Nullable Context context, @NotNull ArrayList<ProductReturnReasonResponse.DataEntity> returnReturn) {
        this.context = context;
        this.returnReturn = returnReturn;
        inflter = (LayoutInflater.from(context));

    }

    @Override
    public int getCount() {
        return returnReturn.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflter.inflate(R.layout.user, null);
        TextView names = view.findViewById(R.id.tvReason);
        names.setText(returnReturn.get(position).getReasontext());
        return view;
    }
}
