package com.example.stylematchia.ui.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylematchia.R;
import com.example.stylematchia.adapter.FavoritosAdapter;
import com.example.stylematchia.data.remote.FirebaseFavoritosRepository;
import com.example.stylematchia.data.repository.ProductoRepository;
import com.example.stylematchia.model.Producto;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesActivity extends AppCompatActivity {

    private final List<Producto> allProducts = new ArrayList<>();
    private final Set<String> favoriteIds = new HashSet<>();
    private ProductoRepository productoRepository;
    private FirebaseFavoritosRepository favoritosRepository;
    private ValueEventListener favoritosListener;
    private FavoritosAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        tvEmpty = findViewById(R.id.tvFavoritesEmpty);
        RecyclerView recyclerFavorites = findViewById(R.id.recyclerFavorites);
        recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnInicio = findViewById(R.id.btnNavInicio);
        ImageButton btnIa = findViewById(R.id.btnNavIa);
        ImageButton btnFavoritos = findViewById(R.id.btnNavFavoritos);
        ImageButton btnRecomendaciones = findViewById(R.id.btnNavRecomendaciones);
        ImageButton btnPerfil = findViewById(R.id.btnNavPerfil);

        adapter = new FavoritosAdapter(new FavoritosAdapter.Listener() {
            @Override
            public void onProductClick(Producto producto) {
                Intent intent = new Intent(FavoritesActivity.this, ProductDetailActivity.class);
                intent.putExtra("producto", producto);
                startActivity(intent);
            }

            @Override
            public void onRemoveFavorite(Producto producto) {
                favoritosRepository.toggleFavorito(producto.getId(), false);
            }
        });
        recyclerFavorites.setAdapter(adapter);

        productoRepository = new ProductoRepository(getApplicationContext());
        favoritosRepository = new FirebaseFavoritosRepository();

        productoRepository.observeProducts(productos -> {
            allProducts.clear();
            allProducts.addAll(productos);
            renderFavorites();
        });

        favoritosListener = favoritosRepository.observeFavoritos(ids -> {
            favoriteIds.clear();
            favoriteIds.addAll(ids);
            renderFavorites();
        });

        btnInicio.setOnClickListener(v -> openActivity(HomeActivity.class));
        btnIa.setOnClickListener(v -> openActivity(AiAssistantActivity.class));
        btnFavoritos.setOnClickListener(v -> {});
        btnRecomendaciones.setOnClickListener(v -> openActivity(RecomendacionesActivity.class));
        btnPerfil.setOnClickListener(v -> openActivity(ProfileActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productoRepository != null) {
            productoRepository.stopObserving();
        }
        favoritosRepository.removeObserver(favoritosListener);
    }

    private void renderFavorites() {
        List<Producto> favoritos = new ArrayList<>();
        for (Producto producto : allProducts) {
            if (favoriteIds.contains(producto.getId())) {
                favoritos.add(producto);
            }
        }
        adapter.updateData(favoritos);
        tvEmpty.setVisibility(favoritos.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openActivity(Class<?> target) {
        if (!getClass().equals(target)) {
            startActivity(new Intent(this, target));
        }
    }
}
