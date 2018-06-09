package com.example.hp_lap.gorupchat;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by hp-lap on 2/24/2018.
 */

public class MessageAdapter extends ArrayAdapter<MessageModel> {

    public MessageAdapter(Context context, int resource, List<MessageModel> object){
        super(context,resource,object);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item,
                    parent,false);
        }

        ImageView mImageView = (ImageView)convertView.findViewById(R.id.iv_photo);
        TextView mMessageTextView = (TextView) convertView.findViewById(R.id.tv_message);
        TextView mAutherTextView = (TextView)convertView.findViewById(R.id.tv_name);

        MessageModel messageModel = getItem(position);

        boolean isImage = messageModel.getPhotoUrl() != null;

        if(isImage){
            mMessageTextView.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
            Glide.with(mImageView.getContext())
                    .load(messageModel.getPhotoUrl())
                    .into(mImageView);
        }
        else{
            mMessageTextView.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.GONE);
            mMessageTextView.setText(messageModel.getText());
        }
        mAutherTextView.setText(messageModel.getName());

        return convertView;
    }
}
