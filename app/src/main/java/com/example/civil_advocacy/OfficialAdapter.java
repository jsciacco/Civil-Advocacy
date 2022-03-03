package com.example.civil_advocacy;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "OfficialAdapter";
    private final List<Official> officialList;
    private final MainActivity mainAct;

    OfficialAdapter(List<Official> empList, MainActivity ma) {
        this.officialList = empList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.politican_layout_row, parent, false);

        itemView.setOnClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Official official = officialList.get(position);
        holder.office.setText(official.getOfficialPosition());
        holder.name.setText(official.getOfficialName());
        holder.party.setText("("+official.getOfficialParty()+")");
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }

}