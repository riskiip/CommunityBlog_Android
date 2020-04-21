package com.rizki.blogapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rizki.blogapp.Activities.PostDetailActivity;
import com.rizki.blogapp.Models.Post;
import com.rizki.blogapp.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitle.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.ivPost);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.ivProfile);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        ImageView ivProfile, ivPost;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_post_title);
            ivProfile = itemView.findViewById(R.id.row_post_profile_img);
            ivPost = itemView.findViewById(R.id.row_post_img);

            //TODO: Mengirim ke Detail Activity menggunakan intent
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    i.putExtra("title", mData.get(position).getTitle());
                    i.putExtra("postImage", mData.get(position).getPicture());
                    i.putExtra("description", mData.get(position).getDesc());
                    i.putExtra("postKey", mData.get(position).getPostKey());
                    i.putExtra("userPhoto", mData.get(position).getUserPhoto());

                    long timestamp = (long) mData.get(position).getTimestamp();
                    i.putExtra("postDate", timestamp);
                    mContext.startActivity(i);
                }
            });
        }
    }
}
