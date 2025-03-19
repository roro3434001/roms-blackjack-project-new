package com.example.romsproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LeaderBoard_RecyclerViewAdapter extends RecyclerView.Adapter<LeaderBoard_RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<LeaderBoardModel> LeaderBoardModels;



    public LeaderBoard_RecyclerViewAdapter(Context context, ArrayList<LeaderBoardModel>LeaderBoardModels){
        this.context= context;
        this.LeaderBoardModels =LeaderBoardModels;
    }

    @NonNull
    @Override
    public LeaderBoard_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row,parent,false);
        return new LeaderBoard_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderBoard_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.tvName.setText(LeaderBoardModels.get(position).getPlayerName());
        holder.tvCoinBalance.setText(LeaderBoardModels.get(position).getPlayerCoinBalance());
        holder.imageView.setImageResource(LeaderBoardModels.get(position).getImage());

    }

    @Override
    public int getItemCount() {
        return LeaderBoardModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView tvName,tvCoinBalance;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView= itemView.findViewById(R.id.imageView);
            tvName=itemView.findViewById(R.id.textView);
            tvCoinBalance=itemView.findViewById(R.id.textCoinBalance);
        }
    }
}
