package com.example.stylematchia.ui.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylematchia.MainActivity;
import com.example.stylematchia.R;
import com.example.stylematchia.adapter.CatalogoAdapter;
import com.example.stylematchia.data.remote.FirebaseFavoritosRepository;
import com.example.stylematchia.data.remote.FirebaseUserRepository;
import com.example.stylematchia.data.repository.ProductoRepository;
import com.example.stylematchia.logic.ProductMatcher;
import com.example.stylematchia.model.Producto;
import com.example.stylematchia.ui.crud.CrudProductosActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ProductoRepository productoRepository;
    private FirebaseFavoritosRepository favoritosRepository;
    private FirebaseUserRepository userRepository;
    private ValueEventListener favoritosListener;
    private CatalogoAdapter adapter;
    private final List<Producto> productos = new ArrayList<>();
    private final Set<String> favoriteIds = new HashSet<>();
    private EditText etBuscar;
    private TextView tvEmptyHome;
    private String currentCategory = "Todo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            openLogin();
            return;
        }

        etBuscar = findViewById(R.id.etBuscar);
        tvEmptyHome = findViewById(R.id.tvEmptyHome);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnAbrirCrud = findViewById(R.id.btnAbrirCrud);
        Button btnRecomendaciones = findViewById(R.id.btnRecomendaciones);
        Button btnTodo = findViewById(R.id.btnChipTodo);
        Button btnCamisetas = findViewById(R.id.btnChipCamisetas);
        Button btnPantalones = findViewById(R.id.btnChipPantalones);
        Button btnZapatillas = findViewById(R.id.btnChipZapatillas);
        Button btnSudaderas = findViewById(R.id.btnChipSudaderas);
        Button btnChaquetas = findViewById(R.id.btnChipChaquetas);

        ImageButton btnNavInicio = findViewById(R.id.btnNavInicio);
        ImageButton btnNavIa = findViewById(R.id.btnNavIa);
        ImageButton btnNavFavoritos = findViewById(R.id.btnNavFavoritos);
        ImageButton btnNavRecomendaciones = findViewById(R.id.btnNavRecomendaciones);
        ImageButton btnNavPerfil = findViewById(R.id.btnNavPerfil);

        RecyclerView recyclerHome = findViewById(R.id.recyclerHome);
        recyclerHome.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new CatalogoAdapter(new CatalogoAdapter.Listener() {
            @Override
            public void onProductClick(Producto producto) {
                Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
                intent.putExtra("producto", producto);
                startActivity(intent);
            }

            @Override
            public void onFavoriteToggle(Producto producto, boolean makeFavorite) {
                favoritosRepository.toggleFavorito(producto.getId(), makeFavorite);
            }
        });
        recyclerHome.setAdapter(adapter);

        productoRepository = new ProductoRepository(getApplicationContext());
        favoritosRepository = new FirebaseFavoritosRepository();
        userRepository = new FirebaseUserRepository();

        productoRepository.observeProducts(items -> {
            productos.clear();
            productos.addAll(items);
            applyFilters();
        });
        favoritosListener = favoritosRepository.observeFavoritos(ids -> {
            favoriteIds.clear();
            favoriteIds.addAll(ids);
            applyFilters();
        });

        btnSearch.setOnClickListener(v -> applyFilters());
        btnRecomendaciones.setOnClickListener(v -> openActivity(RecomendacionesActivity.class));
        btnAbrirCrud.setVisibility(View.GONE);
        userRepository.checkIsAdmin(currentUser.getUid(), isAdmin -> {
            btnAbrirCrud.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            btnAbrirCrud.setOnClickListener(v -> {
                if (isAdmin) {
                    startActivity(new Intent(this, CrudProductosActivity.class));
                }
            });
        });

        btnTodo.setOnClickListener(v -> selectCategory("Todo"));
        btnCamisetas.setOnClickListener(v -> selectCategory("Camisetas"));
        btnPantalones.setOnClickListener(v -> selectCategory("Pantalones"));
        btnZapatillas.setOnClickListener(v -> selectCategory("Zapatillas"));
        btnSudaderas.setOnClickListener(v -> selectCategory("Sudaderas"));
        btnChaquetas.setOnClickListener(v -> selectCategory("Chaquetas"));

        btnNavInicio.setOnClickListener(v -> {});
        btnNavIa.setOnClickListener(v -> openActivity(AiAssistantActivity.class));
        btnNavFavoritos.setOnClickListener(v -> openActivity(FavoritesActivity.class));
        btnNavRecomendaciones.setOnClickListener(v -> openActivity(RecomendacionesActivity.class));
        btnNavPerfil.setOnClickListener(v -> openActivity(ProfileActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productoRepository != null) {
            productoRepository.stopObserving();
        }
        favoritosRepository.removeObserver(favoritosListener);
    }

    private void selectCategory(String category) {
        currentCategory = category;
        applyFilters();
    }

    private void applyFilters() {
        String query = etBuscar.getText().toString().trim().toLowerCase();
        List<Producto> filtrados = new ArrayList<>();

        for (Producto producto : productos) {
            boolean categoryOk = ProductMatcher.matchesCategory(producto, currentCategory);
            boolean searchOk = ProductMatcher.matchesSearch(producto, query);

            if (categoryOk && searchOk) {
                filtrados.add(producto);
            }
        }

        adapter.updateData(filtrados, favoriteIds);
        tvEmptyHome.setVisibility(filtrados.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openActivity(Class<?> target) {
        startActivity(new Intent(this, target));
    }

    private void openLogin() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
