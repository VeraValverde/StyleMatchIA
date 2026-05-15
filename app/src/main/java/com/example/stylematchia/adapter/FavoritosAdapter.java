package com.example.stylematchia.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stylematchia.R;
import com.example.stylematchia.model.Producto;

import java.util.ArrayList;
import java.util.List;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.FavoritoViewHolder> {

    public interface Listener {
        void onProductClick(Producto producto);
        void onRemoveFavorite(Producto producto);
    }

    private final List<Producto> productos = new ArrayList<>();
    private final Listener listener;

    public FavoritosAdapter(Listener listener) {
        this.listener = listener;
    }

    public void updateData(List<Producto> nuevosProductos) {
        productos.clear();
        productos.addAll(nuevosProductos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorito_producto, parent, false);
        return new FavoritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.tvNombre.setText(producto.getNombre());
        holder.tvMarca.setText(producto.getMarca());
        holder.tvPrecio.setText(String.format("%.2f €", producto.getPrecio()));

        Glide.with(holder.ivProducto.getContext())
                .load(producto.getImagenUrl())
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(holder.ivProducto);

        holder.itemView.setOnClickListener(v -> listener.onProductClick(producto));
        holder.btnFavorite.setOnClickListener(v -> listener.onRemoveFavorite(producto));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class FavoritoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProducto;
        TextView tvNombre;
        TextView tvMarca;
        TextView tvPrecio;
        ImageButton btnFavorite;

        FavoritoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProducto = itemView.findViewById(R.id.ivFavoritoProducto);
            tvNombre = itemView.findViewById(R.id.tvFavoritoNombre);
            tvMarca = itemView.findViewById(R.id.tvFavoritoMarca);
            tvPrecio = itemView.findViewById(R.id.tvFavoritoPrecio);
            btnFavorite = itemView.findViewById(R.id.btnFavoritoHeart);
        }
    }
}
