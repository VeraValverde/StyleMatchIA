package com.example.stylematchia.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stylematchia.R;
import com.example.stylematchia.model.Producto;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    public interface OnProductoActionListener {
        void onEdit(Producto producto);
        void onDelete(Producto producto);
        void onOpen(Producto producto);
    }

    private final List<Producto> productos;
    private final boolean showCrudActions;
    private final OnProductoActionListener listener;

    public ProductoAdapter(List<Producto> productos, boolean showCrudActions, OnProductoActionListener listener) {
        this.productos = new ArrayList<>(productos);
        this.showCrudActions = showCrudActions;
        this.listener = listener;
    }

    public void updateProductos(List<Producto> nuevosProductos) {
        productos.clear();
        productos.addAll(nuevosProductos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.tvNombre.setText(producto.getNombre());
        holder.tvMarcaCategoria.setText("ID: " + producto.getId());
        holder.tvPrecio.setText(String.format("%.2f €", producto.getPrecio()));
        holder.tvDescripcion.setText(producto.getDescripcion());

        Glide.with(holder.ivProducto.getContext())
                .load(producto.getImagenUrl())
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(holder.ivProducto);

        holder.itemView.setOnClickListener(v -> listener.onOpen(producto));
        holder.btnEditar.setVisibility(showCrudActions ? View.VISIBLE : View.GONE);
        holder.btnEliminar.setVisibility(showCrudActions ? View.VISIBLE : View.GONE);
        holder.btnEditar.setOnClickListener(v -> listener.onEdit(producto));
        holder.btnEliminar.setOnClickListener(v -> listener.onDelete(producto));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProducto;
        TextView tvNombre;
        TextView tvMarcaCategoria;
        TextView tvPrecio;
        TextView tvDescripcion;
        Button btnEditar;
        Button btnEliminar;

        ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProducto = itemView.findViewById(R.id.ivProducto);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvMarcaCategoria = itemView.findViewById(R.id.tvMarcaCategoria);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
