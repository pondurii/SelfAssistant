package com.example.selfassistant;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

/**
 * Created by ponduri on 2/6/17.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private List<MessageObj> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt1, txt2, genre;
        LinearLayout linearLayout1, linearLayout2;

        public MyViewHolder(View view) {
            super(view);
            txt1 = (TextView) view.findViewById(R.id.textView1);
            txt2 = (TextView) view.findViewById(R.id.textView2);
            linearLayout1 = (LinearLayout) view.findViewById(R.id.layout1);
            linearLayout2 = (LinearLayout) view.findViewById(R.id.layout2);
        }
    }


    public RecyclerAdapter(List<MessageObj> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MessageObj messageObj = moviesList.get(position);
        String[] s = messageObj.getMsg().split("#");
        holder.txt1.setText(s[0]);
        holder.txt2.setText(s[1]);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
