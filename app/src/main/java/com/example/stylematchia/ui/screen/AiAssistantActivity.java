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

import com.example.stylematchia.R;
import com.example.stylematchia.adapter.CatalogoAdapter;
import com.example.stylematchia.data.remote.FirebaseFavoritosRepository;
import com.example.stylematchia.data.remote.OpenRouterRecommendationService;
import com.example.stylematchia.data.repository.ProductoRepository;
import com.example.stylematchia.logic.AiRecommender;
import com.example.stylematchia.logic.ProductMatcher;
import com.example.stylematchia.model.Producto;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AiAssistantActivity extends AppCompatActivity {

    private final List<Producto> allProducts = new ArrayList<>();
    private final List<Producto> currentResults = new ArrayList<>();
    private final Set<String> favoriteIds = new HashSet<>();
    private ProductoRepository productoRepository;
    private FirebaseFavoritosRepository favoritosRepository;
    private OpenRouterRecommendationService openRouterService;
    private ValueEventListener favoritosListener;
    private CatalogoAdapter adapter;
    private EditText etPrompt;
    private TextView tvResult;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        etPrompt = findViewById(R.id.etAiPrompt);
        tvResult = findViewById(R.id.tvAiResult);
        tvEmpty = findViewById(R.id.tvAiEmpty);
        Button btnBuscar = findViewById(R.id.btnAiBuscar);
        ImageButton btnInicio = findViewById(R.id.btnNavInicio);
        ImageButton btnIa = findViewById(R.id.btnNavIa);
        ImageButton btnFavoritos = findViewById(R.id.btnNavFavoritos);
        ImageButton btnRecomendaciones = findViewById(R.id.btnNavRecomendaciones);
        ImageButton btnPerfil = findViewById(R.id.btnNavPerfil);
        RecyclerView recyclerAi = findViewById(R.id.recyclerAiResults);
        recyclerAi.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new CatalogoAdapter(new CatalogoAdapter.Listener() {
            @Override
            public void onProductClick(Producto producto) {
                Intent intent = new Intent(AiAssistantActivity.this, ProductDetailActivity.class);
                intent.putExtra("producto", producto);
                startActivity(intent);
            }

            @Override
            public void onFavoriteToggle(Producto producto, boolean makeFavorite) {
                favoritosRepository.toggleFavorito(producto.getId(), makeFavorite);
            }
        });
        recyclerAi.setAdapter(adapter);

        productoRepository = new ProductoRepository(getApplicationContext());
        favoritosRepository = new FirebaseFavoritosRepository();
        openRouterService = new OpenRouterRecommendationService();

        productoRepository.observeProducts(productos -> {
            allProducts.clear();
            allProducts.addAll(productos);
        });

        favoritosListener = favoritosRepository.observeFavoritos(ids -> {
            favoriteIds.clear();
            favoriteIds.addAll(ids);
            adapter.updateData(currentResults, favoriteIds);
        });

        btnBuscar.setOnClickListener(v -> searchRecommendations());
        btnInicio.setOnClickListener(v -> openActivity(HomeActivity.class));
        btnIa.setOnClickListener(v -> {});
        btnFavoritos.setOnClickListener(v -> openActivity(FavoritesActivity.class));
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

    private void searchRecommendations() {
        String prompt = etPrompt.getText().toString().trim();
        if (prompt.isEmpty()) {
            tvResult.setText("Escribe lo que buscas para poder recomendarte prendas.");
            tvEmpty.setVisibility(View.VISIBLE);
            currentResults.clear();
            adapter.updateData(currentResults, favoriteIds);
            return;
        }

        tvResult.setText(openRouterService.isConfigured()
                ? "Analizando tu busqueda con OpenRouter..."
                : "OpenRouter no esta configurado. Usando recomendacion local...");

        if (openRouterService.isConfigured()) {
            openRouterService.recommend(prompt, allProducts, new OpenRouterRecommendationService.CallbackResult() {
                @Override
                public void onSuccess(OpenRouterRecommendationService.RecommendationResult result) {
                    runOnUiThread(() -> showOpenRouterResults(result));
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        tvResult.setText(message + " Usando recomendacion local.");
                        showLocalResults(prompt);
                    });
                }
            });
        } else {
            showLocalResults(prompt);
        }
    }

    private void showOpenRouterResults(OpenRouterRecommendationService.RecommendationResult result) {
        String prompt = etPrompt.getText().toString().trim();
        List<Producto> selectedProducts = new ArrayList<>();
        for (String id : result.productIds) {
            for (Producto producto : allProducts) {
                if (producto.getId().equalsIgnoreCase(id)
                        && ProductMatcher.matchesRequestedAttributes(producto, prompt)) {
                    selectedProducts.add(producto);
                    break;
                }
            }
        }

        if (selectedProducts.isEmpty()) {
            tvResult.setText("OpenRouter no encontro productos claros. Usando recomendacion local.");
            showLocalResults(etPrompt.getText().toString().trim());
            return;
        }

        currentResults.clear();
        currentResults.addAll(selectedProducts);
        tvResult.setText(result.summary);
        tvEmpty.setVisibility(selectedProducts.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.updateData(currentResults, favoriteIds);
    }

    private void showLocalResults(String prompt) {
        AiRecommender.Result result = AiRecommender.recommend(prompt, allProducts);
        currentResults.clear();
        currentResults.addAll(result.productos);
        tvResult.setText(result.message);
        tvEmpty.setVisibility(result.productos.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.updateData(currentResults, favoriteIds);
    }

    private void openActivity(Class<?> target) {
        if (!getClass().equals(target)) {
            startActivity(new Intent(this, target));
        }
    }
}
