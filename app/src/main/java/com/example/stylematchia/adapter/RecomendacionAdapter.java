package com.example.stylematchia.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylematchia.R;
import com.example.stylematchia.model.Recomendacion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecomendacionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Listener {
        void onMarkDone(Recomendacion recomendacion);
        void onMarkPending(Recomendacion recomendacion);
        void onDelete(Recomendacion recomendacion);
    }

    private static final int TYPE_USER = 0;
    private static final int TYPE_ADMIN = 1;

    private final List<Recomendacion> recomendaciones = new ArrayList<>();
    private final boolean adminMode;
    private final Listener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public RecomendacionAdapter(boolean adminMode, Listener listener) {
        this.adminMode = adminMode;
        this.listener = listener;
    }

    public void updateData(List<Recomendacion> nuevasRecomendaciones) {
        recomendaciones.clear();
        recomendaciones.addAll(nuevasRecomendaciones);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return adminMode ? TYPE_ADMIN : TYPE_USER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_ADMIN) {
            View view = inflater.inflate(R.layout.item_recomendacion_admin, parent, false);
            return new AdminViewHolder(view);
        }

        View view = inflater.inflate(R.layout.item_recomendacion_usuario, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Recomendacion recomendacion = recomendaciones.get(position);
        if (holder instanceof AdminViewHolder) {
            bindAdmin((AdminViewHolder) holder, recomendacion);
        } else if (holder instanceof UserViewHolder) {
            bindUser((UserViewHolder) holder, recomendacion);
        }
    }

    @Override
    public int getItemCount() {
        return recomendaciones.size();
    }

    private void bindUser(UserViewHolder holder, Recomendacion recomendacion) {
        holder.tvTitle.setText(buildTitle(recomendacion));
        holder.tvMessage.setText(recomendacion.getMensaje());
        holder.tvStatus.setText("Estado: " + capitalize(recomendacion.getEstado()));
        holder.tvDate.setText(buildDate(recomendacion));
    }

    private void bindAdmin(AdminViewHolder holder, Recomendacion recomendacion) {
        holder.tvUser.setText(recomendacion.getUserName());
        holder.tvEmail.setText(recomendacion.getUserEmail());
        holder.tvTitle.setText(buildTitle(recomendacion));
        holder.tvMessage.setText(recomendacion.getMensaje());
        holder.tvStatus.setText("Estado: " + capitalize(recomendacion.getEstado()));
        holder.tvDate.setText(buildDate(recomendacion));
        holder.btnDone.setOnClickListener(v -> listener.onMarkDone(recomendacion));
        holder.btnPending.setOnClickListener(v -> listener.onMarkPending(recomendacion));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(recomendacion));
    }

    private String buildTitle(Recomendacion recomendacion) {
        String marca = recomendacion.getMarca().trim();
        String estilo = recomendacion.getEstilo().trim();

        if (!marca.isEmpty() && !estilo.isEmpty()) {
            return marca + " · " + estilo;
        }
        if (!marca.isEmpty()) {
            return marca;
        }
        if (!estilo.isEmpty()) {
            return estilo;
        }
        return "Sugerencia de estilo";
    }

    private String buildDate(Recomendacion recomendacion) {
        if (recomendacion.getFecha() <= 0L) {
            return "";
        }
        return dateFormat.format(new Date(recomendacion.getFecha()));
    }

    private String capitalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Pendiente";
        }
        String normalized = value.trim().toLowerCase(Locale.getDefault());
        return normalized.substring(0, 1).toUpperCase(Locale.getDefault()) + normalized.substring(1);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvMessage;
        TextView tvStatus;
        TextView tvDate;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvRecommendationTitle);
            tvMessage = itemView.findViewById(R.id.tvRecommendationMessage);
            tvStatus = itemView.findViewById(R.id.tvRecommendationStatus);
            tvDate = itemView.findViewById(R.id.tvRecommendationDate);
        }
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser;
        TextView tvEmail;
        TextView tvTitle;
        TextView tvMessage;
        TextView tvStatus;
        TextView tvDate;
        Button btnDone;
        Button btnPending;
        Button btnDelete;

        AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvRecommendationUser);
            tvEmail = itemView.findViewById(R.id.tvRecommendationEmail);
            tvTitle = itemView.findViewById(R.id.tvRecommendationTitle);
            tvMessage = itemView.findViewById(R.id.tvRecommendationMessage);
            tvStatus = itemView.findViewById(R.id.tvRecommendationStatus);
            tvDate = itemView.findViewById(R.id.tvRecommendationDate);
            btnDone = itemView.findViewById(R.id.btnRecommendationDone);
            btnPending = itemView.findViewById(R.id.btnRecommendationPending);
            btnDelete = itemView.findViewById(R.id.btnRecommendationDelete);
        }
    }
}
