package com.example.civil_advocacy;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class MyViewHolder extends RecyclerView.ViewHolder {

    TextView office;
    TextView name;
    TextView party;

    MyViewHolder(View view) {
        super(view);
        office = view.findViewById(R.id.Office);
        name = view.findViewById(R.id.Name);
        party = view.findViewById(R.id.Party);
    }

}
