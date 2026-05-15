package com.example.stylematchia.ui.screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.stylematchia.R;
import com.example.stylematchia.data.remote.FirebaseFavoritosRepository;
import com.example.stylematchia.model.Producto;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

public class ProductDetailActivity extends AppCompatActivity {

    private FirebaseFavoritosRepository favoritosRepository;
    private ValueEventListener favoritosListener;
    private Producto producto;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        producto = (Producto) getIntent().getSerializableExtra("producto");
        if (producto == null) {
            finish();
            return;
        }

        favoritosRepository = new FirebaseFavoritosRepository();

        ImageView ivProducto = findViewById(R.id.ivDetalleProducto);
        TextView tvNombre = findViewById(R.id.tvDetalleNombre);
        TextView tvMarca = findViewById(R.id.tvDetalleMarca);
        TextView tvPrecio = findViewById(R.id.tvDetallePrecio);
        TextView tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        TextView tvMeta = findViewById(R.id.tvDetalleMeta);
        Button btnVolver = findViewById(R.id.btnDetalleVolver);
        Button btnTienda = findViewById(R.id.btnVerTienda);
        ImageButton btnFavorite = findViewById(R.id.btnDetalleFavorite);

        ImageButton btnNavInicio = findViewById(R.id.btnNavInicio);
        ImageButton btnNavIa = findViewById(R.id.btnNavIa);
        ImageButton btnNavFavoritos = findViewById(R.id.btnNavFavoritos);
        ImageButton btnNavPerfil = findViewById(R.id.btnNavPerfil);

        Glide.with(this)
                .load(producto.getImagenUrl())
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(ivProducto);

        tvNombre.setText(producto.getNombre());
        tvMarca.setText(producto.getMarca());
        tvPrecio.setText(String.format("%.2f €", producto.getPrecio()));
        tvDescripcion.setText(producto.getDescripcion());
        tvMeta.setText(producto.getMarca());

        favoritosListener = favoritosRepository.observeFavoritos(ids -> updateFavoriteState(ids, btnFavorite));

        btnVolver.setOnClickListener(v -> finish());
        btnFavorite.setOnClickListener(v -> favoritosRepository.toggleFavorito(producto.getId(), !isFavorite));
        btnTienda.setOnClickListener(v -> {
            if (!producto.getEnlaceTienda().isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(producto.getEnlaceTienda())));
            }
        });

        btnNavInicio.setOnClickListener(v -> openActivity(HomeActivity.class));
        btnNavIa.setOnClickListener(v -> openActivity(AiAssistantActivity.class));
        btnNavFavoritos.setOnClickListener(v -> openActivity(FavoritesActivity.class));
        btnNavPerfil.setOnClickListener(v -> openActivity(ProfileActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favoritosRepository.removeObserver(favoritosListener);
    }

    private void updateFavoriteState(Set<String> ids, ImageButton button) {
        isFavorite = ids.contains(producto.getId());
        button.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_outline);
    }

    private void openActivity(Class<?> target) {
        startActivity(new Intent(this, target));
    }
}
