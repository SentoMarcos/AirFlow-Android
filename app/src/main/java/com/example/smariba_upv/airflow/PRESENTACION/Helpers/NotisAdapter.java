package com.example.smariba_upv.airflow.PRESENTACION.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smariba_upv.airflow.POJO.ItemNotisSalud;
import com.example.smariba_upv.airflow.R;

import java.util.List;

public class NotisAdapter extends RecyclerView.Adapter<NotisAdapter.NotisViewHolder> {

    private List<ItemNotisSalud> notificationList; // Store the notification data
    private Context context; // Context for accessing resources
    private RecyclerView recyclerView;

    public NotisAdapter(Context context, List<ItemNotisSalud> notificationList, RecyclerView recyclerView) {
        this.context = context;
        this.notificationList = notificationList;
        this.recyclerView = recyclerView;
        this.recyclerView.setLayoutManager(new GridLayoutManager(context, 2)); // Configurar dos columnas
    }

    @NonNull
    @Override
    public NotisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single notification item
        View view = LayoutInflater.from(context).inflate(R.layout.item_notis_salud, parent, false);
        return new NotisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotisViewHolder holder, int position) {
        ItemNotisSalud notification = notificationList.get(position);

        // Cambiar para mostrar tiempo relativo
        holder.timeTextView.setText(notification.getRelativeTime());
        holder.messageTextView.setText(notification.getMsg());
        holder.stateTextView.setText(notification.getState().toUpperCase());

        // Cambiar el color del CardView basado en el estado
        switch (notification.getState()) {
            case "excelente":
                holder.cardView.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.RosaExcelente));
                break;
            case "buena":
                holder.cardView.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.VerdeBueno));
                break;
            case "media":
                holder.cardView.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.AmarilloMedio));
                break;
            case "mala":
                holder.cardView.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.NaranjaMalo));
                break;
            case "Peligroso":
                holder.cardView.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.RojoPeligroso));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotisViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView timeTextView;
        TextView messageTextView;
        TextView stateTextView;

        public NotisViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.tvTime);
            messageTextView = itemView.findViewById(R.id.tvMsg);
            stateTextView = itemView.findViewById(R.id.tvState);
            cardView = (CardView) itemView;
        }
    }

    // Método para actualizar los datos del adaptador
    public void updateData(List<ItemNotisSalud> newData) {
        this.notificationList.clear(); // Limpia la lista existente
        this.notificationList.addAll(newData); // Añade los nuevos datos
        notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
    }
}
