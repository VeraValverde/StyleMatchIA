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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CatalogoAdapter extends RecyclerView.Adapter<CatalogoAdapter.CatalogoViewHolder> {

    public interface Listener {
        void onProductClick(Producto producto);
        void onFavoriteToggle(Producto producto, boolean makeFavorite);
    }

    private final List<Producto> productos = new ArrayList<>();
    private final Set<String> favoritosIds = new HashSet<>();
    private final Listener listener;

    public CatalogoAdapter(Listener listener) {
        this.listener = listener;
    }

    public void updateData(List<Producto> productos, Set<String> favoritosIds) {
        this.productos.clear();
        this.productos.addAll(productos);
        this.favoritosIds.clear();
        this.favoritosIds.addAll(favoritosIds);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CatalogoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalogo_producto, parent, false);
        return new CatalogoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        boolean isFavorite = favoritosIds.contains(producto.getId());

        holder.tvNombre.setText(producto.getNombre());
        holder.tvMarca.setText(producto.getMarca());
        holder.tvPrecio.setText(String.format("%.2f €", producto.getPrecio()));
        holder.btnFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_outline);

        Glide.with(holder.ivProducto.getContext())
                .load(producto.getImagenUrl())
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(holder.ivProducto);

        holder.itemView.setOnClickListener(v -> listener.onProductClick(producto));
        holder.btnFavorite.setOnClickListener(v -> listener.onFavoriteToggle(producto, !isFavorite));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class CatalogoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProducto;
        TextView tvNombre;
        TextView tvMarca;
        TextView tvPrecio;
        ImageButton btnFavorite;

        CatalogoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProducto = itemView.findViewById(R.id.ivCatalogoProducto);
            tvNombre = itemView.findViewById(R.id.tvCatalogoNombre);
            tvMarca = itemView.findViewById(R.id.tvCatalogoMarca);
            tvPrecio = itemView.findViewById(R.id.tvCatalogoPrecio);
            btnFavorite = itemView.findViewById(R.id.btnCatalogoFavorite);
        }
    }
}
