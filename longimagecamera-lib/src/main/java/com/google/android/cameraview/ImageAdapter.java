package com.google.android.cameraview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wajahatkarim3.longimagecamera.LongImageCameraActivity;
import com.wajahatkarim3.longimagecamera.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private  List<Bitmap> bitmapsList;
    private  deleteImage deleteImageListener;
    private  Context context;

    public ImageAdapter(List<Bitmap> bitmapsList, Context context, LongImageCameraActivity longImageCameraActivity) {
        this.bitmapsList = bitmapsList;
        this.context = context;
        this.deleteImageListener = longImageCameraActivity;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageAdapter.ViewHolder holder, final int position) {

        holder.imageViewImage.setImageBitmap(bitmapsList.get(position));

        holder.imageViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleteImageListener.onDeleteImage(position,"show");

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.adapter_item_big);

                ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.ivClose);
                ImageView imageViewImage = (ImageView) dialog.findViewById(R.id.ivImage);

                imageViewImage.setImageBitmap(bitmapsList.get(position));

                imageViewClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
        holder.imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImageListener.onDeleteImage(position,"delete");
            }
        });
    }

    @Override
    public int getItemCount() {
        return bitmapsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewImage,imageViewClose;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewClose = (ImageView) itemView.findViewById(R.id.ivClose);
            imageViewImage = (ImageView) itemView.findViewById(R.id.ivImage);

        }
    }
}
