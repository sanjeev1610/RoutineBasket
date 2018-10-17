package com.mobiapp4u.pc.routinebasket.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mobiapp4u.pc.routinebasket.R;


public class CommentHolder extends RecyclerView.ViewHolder {

   public TextView userPhone,userComment;
   public RatingBar userRating;

    public CommentHolder(View itemView) {
        super(itemView);
        userComment = (TextView)itemView.findViewById(R.id.comment_text);
        userPhone = (TextView)itemView.findViewById(R.id.user_phone);
        userRating = (RatingBar)itemView.findViewById(R.id.show_comment_ratebar);
    }
}
