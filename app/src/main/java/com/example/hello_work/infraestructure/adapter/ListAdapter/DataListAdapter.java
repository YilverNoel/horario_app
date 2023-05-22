package com.example.hello_work.infraestructure.adapter.ListAdapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hello_work.R;
import com.example.hello_work.infraestructure.adapter.ClassData;
import com.example.hello_work.infraestructure.adapter.listener.Listener;

import java.util.List;

public class DataListAdapter extends RecyclerView.Adapter<DataListAdapter.MyViewHolder> {
    private List<ClassData> itemList; // Lista de elementos
    private Listener context;

    public DataListAdapter(List<ClassData> itemList, Listener context) {

        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       ClassData item = itemList.get(position);
        holder.name.setText(item.getNameTeacher());
        holder.schedule.setText(item.getSchedule());
        holder.subject.setText(item.getNameSubject());
        holder.buttonYes.setOnClickListener(x->
            context.callback(item, true)
        );
        holder.buttonNot.setOnClickListener(x->
            context.callback(item, false)
        );
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView subject;
        public TextView schedule;
        public Button buttonYes;
        public Button buttonNot;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvNameTeacher);
            schedule = itemView.findViewById(R.id.tvCode);
            subject = itemView.findViewById(R.id.tvNameSubject);
            buttonYes = itemView.findViewById(R.id.btnYes);
            buttonNot = itemView.findViewById(R.id.btnNo);
        }
    }
    
}

