package com.example.hello_work.infraestructure.adapter.ListAdapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.nombre.setText(item.getNameTeacher());
        holder.codigo.setText(item.getSchedule());
        holder.materia.setText(item.getNameSubject());
        holder.itemView.setOnClickListener(v -> {
            context.callback(item.getNameTeacher());
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // Implementa los métodos necesarios: onCreateViewHolder, onBindViewHolder, getItemCount, etc.

    // Aquí se define la clase ViewHolder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre;
        public TextView materia;
        public TextView codigo;

        public MyViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombreProfe);
            codigo = itemView.findViewById(R.id.tvCodigo);
            materia = itemView.findViewById(R.id.tvNombreMateria);
        }
    }
}

