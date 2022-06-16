package com.ffi.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ffi.R;

import java.util.ArrayList;
import java.util.List;


public class SearchHomeAdapter extends RecyclerView.Adapter<SearchHomeAdapter.ViewHolder> implements Filterable {

    private List<SearchHome> mArrayList;
    private List<SearchHome> mFilteredList;
    private static MyClickListener myClickListener;

    public SearchHomeAdapter(List<SearchHome> arrayList) {
        mArrayList = arrayList;
        mFilteredList = arrayList;

    }

    @Override
    public SearchHomeAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_layout, viewGroup, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(SearchHomeAdapter.ViewHolder viewHolder, int i) {
        viewHolder.search.setText(mArrayList.get(i).getsearchHistory());

    }


    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mArrayList;
                } else {

                    ArrayList<SearchHome> filteredList = new ArrayList<>();

                    for (SearchHome androidVersion : mArrayList) {

                        if (androidVersion.getsearchHistory().toLowerCase().contains(charString)) {
                            filteredList.add(androidVersion);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (List<SearchHome>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView search;
        private ImageButton delete;

        public ViewHolder(View view) {
            super(view);
            search = (TextView) view.findViewById(R.id.search_text);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myClickListener.onItemClick(getAdapterPosition(), v);
                }
            });

        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}

