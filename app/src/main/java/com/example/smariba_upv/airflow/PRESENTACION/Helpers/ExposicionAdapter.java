package com.example.smariba_upv.airflow.PRESENTACION.Helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smariba_upv.airflow.POJO.ExposicionItem;
import com.example.smariba_upv.airflow.R;

import java.util.List;

public class ExposicionAdapter extends RecyclerView.Adapter<ExposicionAdapter.ExposicionViewHolder> {

    private List<ExposicionItem> exposicionItems;

    public ExposicionAdapter(List<ExposicionItem> exposicionItems) {
        this.exposicionItems = exposicionItems;
    }

    @NonNull
    @Override
    public ExposicionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exposicion, parent, false);
        return new ExposicionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExposicionViewHolder holder, int position) {
        ExposicionItem item = exposicionItems.get(position);
        holder.tvTitulo.setText(item.getTitulo());
        holder.tvNivel.setText(item.getNivel());

        // Cambiar backgroundTint y íconos según el nivel
        switch (item.getNivel()) {
            case "excelente":
                holder.cardView.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.RosaExcelente));
                holder.ivEstado.setImageResource(R.drawable.landing_excelente);
                break;
            case "buena":
                holder.cardView.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.VerdeBueno));
                holder.ivEstado.setImageResource(R.drawable.landing_bueno);
                break;
            case "media":
                holder.cardView.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.AmarilloMedio));
                holder.ivEstado.setImageResource(R.drawable.landing_media);
                holder.tvNivel.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.Negro));
                holder.tvTitulo.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.Negro));
                break;
            case "mala":
                holder.cardView.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.NaranjaMalo));
                holder.ivEstado.setImageResource(R.drawable.landing_malo);
                break;
            case "Peligroso":
                holder.cardView.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.RojoPeligroso));
                holder.ivEstado.setImageResource(R.drawable.landing_peligroso);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return exposicionItems.size();
    }

    static class ExposicionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvNivel;
        ImageView ivEstado;
        CardView cardView;

        public ExposicionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvNivel = itemView.findViewById(R.id.tvNivel);
            ivEstado = itemView.findViewById(R.id.ivEstado);
            cardView = (CardView) itemView;
        }
    }
}
