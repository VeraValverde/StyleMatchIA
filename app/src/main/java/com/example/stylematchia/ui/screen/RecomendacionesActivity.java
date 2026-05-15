package com.example.stylematchia.ui.screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylematchia.MainActivity;
import com.example.stylematchia.R;
import com.example.stylematchia.adapter.RecomendacionAdapter;
import com.example.stylematchia.data.remote.FirebaseRecomendacionRepository;
import com.example.stylematchia.model.Recomendacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecomendacionesActivity extends AppCompatActivity {

    private EditText etMarca;
    private EditText etEstilo;
    private EditText etMensaje;
    private TextView tvEmpty;
    private RecomendacionAdapter adapter;
    private FirebaseRecomendacionRepository recomendacionRepository;
    private ValueEventListener recomendacionesListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendaciones);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        etMarca = findViewById(R.id.etRecommendationBrand);
        etEstilo = findViewById(R.id.etRecommendationStyle);
        etMensaje = findViewById(R.id.etRecommendationMessage);
        tvEmpty = findViewById(R.id.tvRecommendationsEmpty);
        Button btnEnviar = findViewById(R.id.btnSendRecommendation);

        ImageButton btnNavInicio = findViewById(R.id.btnNavInicio);
        ImageButton btnNavIa = findViewById(R.id.btnNavIa);
        ImageButton btnNavFavoritos = findViewById(R.id.btnNavFavoritos);
        ImageButton btnNavRecomendaciones = findViewById(R.id.btnNavRecomendaciones);
        ImageButton btnNavPerfil = findViewById(R.id.btnNavPerfil);

        RecyclerView recyclerView = findViewById(R.id.recyclerRecommendations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecomendacionAdapter(false, new RecomendacionAdapter.Listener() {
            @Override
            public void onMarkDone(Recomendacion recomendacion) {
            }

            @Override
            public void onMarkPending(Recomendacion recomendacion) {
            }

            @Override
            public void onDelete(Recomendacion recomendacion) {
            }
        });
        recyclerView.setAdapter(adapter);

        recomendacionRepository = new FirebaseRecomendacionRepository();
        recomendacionesListener = recomendacionRepository.observeRecommendations(this::showCurrentUserRecommendations);

        btnEnviar.setOnClickListener(v -> sendRecommendation());
        btnNavInicio.setOnClickListener(v -> openActivity(HomeActivity.class));
        btnNavIa.setOnClickListener(v -> openActivity(AiAssistantActivity.class));
        btnNavFavoritos.setOnClickListener(v -> openActivity(FavoritesActivity.class));
        btnNavRecomendaciones.setOnClickListener(v -> {});
        btnNavPerfil.setOnClickListener(v -> openActivity(ProfileActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recomendacionRepository != null) {
            recomendacionRepository.removeObserver(recomendacionesListener);
        }
    }

    private void sendRecommendation() {
        String marca = etMarca.getText().toString().trim();
        String estilo = etEstilo.getText().toString().trim();
        String mensaje = etMensaje.getText().toString().trim();

        if (TextUtils.isEmpty(marca) && TextUtils.isEmpty(estilo)) {
            Toast.makeText(this, "Escribe una marca o un estilo.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mensaje)) {
            etMensaje.setError("Debes escribir tu recomendacion.");
            return;
        }

        String email = currentUser.getEmail() != null ? currentUser.getEmail() : "";
        String userName = currentUser.getDisplayName();
        if (userName == null || userName.trim().isEmpty()) {
            userName = email.contains("@") ? email.substring(0, email.indexOf("@")) : "Usuario";
        }

        Recomendacion recomendacion = new Recomendacion(
                "",
                currentUser.getUid(),
                userName,
                email,
                marca,
                estilo,
                mensaje,
                "pendiente",
                System.currentTimeMillis()
        );

        recomendacionRepository.saveRecommendation(recomendacion, (success, message) -> runOnUiThread(() -> {
            if (success) {
                etMarca.setText("");
                etEstilo.setText("");
                etMensaje.setText("");
                showThankYouDialog();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void showThankYouDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Gracias por su recomendacion")
                .setMessage("La tendremos muy en cuenta.")
                .setCancelable(true)
                .create();
        dialog.show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }, 2200);
    }

    private void showCurrentUserRecommendations(List<Recomendacion> allRecommendations) {
        List<Recomendacion> ownRecommendations = new ArrayList<>();

        for (Recomendacion recomendacion : allRecommendations) {
            if (currentUser.getUid().equals(recomendacion.getUserId())) {
                ownRecommendations.add(recomendacion);
            }
        }

        Collections.sort(ownRecommendations, (first, second) -> Long.compare(second.getFecha(), first.getFecha()));
        adapter.updateData(ownRecommendations);
        tvEmpty.setVisibility(ownRecommendations.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void openActivity(Class<?> target) {
        startActivity(new Intent(this, target));
    }
}
