package com.example.stylematchia.ui.crud;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.stylematchia.data.remote.FirebaseUserRepository;
import com.example.stylematchia.model.Recomendacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestionRecomendacionesActivity extends AppCompatActivity {

    private TextView tvEmpty;
    private RecomendacionAdapter adapter;
    private FirebaseRecomendacionRepository recomendacionRepository;
    private FirebaseUserRepository userRepository;
    private ValueEventListener recomendacionesListener;
    private final List<Recomendacion> recomendaciones = new ArrayList<>();
    private String currentFilter = "todas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_recomendaciones);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        Button btnVolver = findViewById(R.id.btnRecommendationsBack);
        Button btnTodas = findViewById(R.id.btnFilterAll);
        Button btnPendientes = findViewById(R.id.btnFilterPending);
        Button btnHechas = findViewById(R.id.btnFilterDone);
        Button btnLogout = findViewById(R.id.btnLogoutRecommendations);
        tvEmpty = findViewById(R.id.tvRecommendationsAdminEmpty);

        RecyclerView recyclerView = findViewById(R.id.recyclerRecommendationsAdmin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecomendacionAdapter(true, new RecomendacionAdapter.Listener() {
            @Override
            public void onMarkDone(Recomendacion recomendacion) {
                updateStatus(recomendacion, "hecha");
            }

            @Override
            public void onMarkPending(Recomendacion recomendacion) {
                updateStatus(recomendacion, "pendiente");
            }

            @Override
            public void onDelete(Recomendacion recomendacion) {
                confirmDelete(recomendacion);
            }
        });
        recyclerView.setAdapter(adapter);

        recomendacionRepository = new FirebaseRecomendacionRepository();
        userRepository = new FirebaseUserRepository();

        recomendacionesListener = recomendacionRepository.observeRecommendations(items -> {
            recomendaciones.clear();
            recomendaciones.addAll(items);
            applyFilter();
        });

        btnVolver.setOnClickListener(v -> finish());
        btnTodas.setOnClickListener(v -> {
            currentFilter = "todas";
            applyFilter();
        });
        btnPendientes.setOnClickListener(v -> {
            currentFilter = "pendiente";
            applyFilter();
        });
        btnHechas.setOnClickListener(v -> {
            currentFilter = "hecha";
            applyFilter();
        });
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        });

        userRepository.checkIsAdmin(currentUser.getUid(), isAdmin -> {
            if (!isAdmin) {
                Toast.makeText(this, "Solo los administradores pueden gestionar recomendaciones.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recomendacionRepository != null) {
            recomendacionRepository.removeObserver(recomendacionesListener);
        }
    }

    private void applyFilter() {
        List<Recomendacion> filtered = new ArrayList<>();

        for (Recomendacion recomendacion : recomendaciones) {
            if ("todas".equals(currentFilter) || currentFilter.equalsIgnoreCase(recomendacion.getEstado())) {
                filtered.add(recomendacion);
            }
        }

        Collections.sort(filtered, (first, second) -> Long.compare(second.getFecha(), first.getFecha()));
        adapter.updateData(filtered);
        tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void updateStatus(Recomendacion recomendacion, String status) {
        recomendacionRepository.updateStatus(recomendacion.getId(), status, (success, message) ->
                runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show())
        );
    }

    private void confirmDelete(Recomendacion recomendacion) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar recomendacion")
                .setMessage("Se borrara definitivamente de Firebase.")
                .setPositiveButton("Eliminar", (dialog, which) ->
                        recomendacionRepository.deleteRecommendation(recomendacion.getId(), (success, message) ->
                                runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show())
                        )
                )
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
