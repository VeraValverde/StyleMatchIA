package com.example.stylematchia.ui.crud;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylematchia.R;
import com.example.stylematchia.adapter.ProductoAdapter;
import com.example.stylematchia.data.remote.FirebaseUserRepository;
import com.example.stylematchia.data.repository.ProductoRepository;
import com.example.stylematchia.logic.ProductMatcher;
import com.example.stylematchia.model.Producto;
import com.example.stylematchia.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CrudProductosActivity extends AppCompatActivity {

    private EditText etBuscar;
    private TextView tvSinDatos;
    private ProductoAdapter adapter;
    private ProductoRepository productoRepository;
    private FirebaseUserRepository userRepository;
    private boolean isAdmin = false;
    private final List<Producto> listaCompleta = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_productos);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        Button btnVolver = findViewById(R.id.btnCrudVolver);
        etBuscar = findViewById(R.id.etBuscar);
        Button btnBuscar = findViewById(R.id.btnBuscar);
        Button btnInsertar = findViewById(R.id.btnInsertar);
        Button btnLogout = findViewById(R.id.btnLogout);
        tvSinDatos = findViewById(R.id.tvSinDatos);
        RecyclerView recyclerProductos = findViewById(R.id.recyclerProductos);

        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductoAdapter(new ArrayList<>(), true, new ProductoAdapter.OnProductoActionListener() {
            @Override
            public void onEdit(Producto producto) {
                Intent intent = new Intent(CrudProductosActivity.this, InsertEditProductoActivity.class);
                intent.putExtra("producto", producto);
                startActivity(intent);
            }

            @Override
            public void onDelete(Producto producto) {
                confirmDelete(producto);
            }

            @Override
            public void onOpen(Producto producto) {
                Intent intent = new Intent(CrudProductosActivity.this, InsertEditProductoActivity.class);
                intent.putExtra("producto", producto);
                startActivity(intent);
            }
        });
        recyclerProductos.setAdapter(adapter);

        productoRepository = new ProductoRepository(getApplicationContext());
        productoRepository.observeProducts(productos -> {
            listaCompleta.clear();
            listaCompleta.addAll(productos);
            applyFilter();
        });

        btnVolver.setOnClickListener(v -> finish());
        btnBuscar.setOnClickListener(v -> applyFilter());
        btnInsertar.setOnClickListener(v ->
                startActivity(new Intent(this, InsertEditProductoActivity.class))
        );
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(
                    this,
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
            );
            googleSignInClient.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        });

        btnInsertar.setEnabled(false);

        userRepository = new FirebaseUserRepository();
        userRepository.checkIsAdmin(currentUser.getUid(), admin -> {
            isAdmin = admin;
            btnInsertar.setEnabled(admin);
            if (!admin) {
                Toast.makeText(this, "Solo los administradores pueden gestionar productos.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productoRepository != null) {
            productoRepository.stopObserving();
        }
    }

    private void applyFilter() {
        String query = etBuscar.getText().toString().trim().toLowerCase();
        List<Producto> filtered = new ArrayList<>();

        if (query.isEmpty()) {
            filtered.addAll(listaCompleta);
        } else {
            for (Producto producto : listaCompleta) {
                if (producto.getNombre().toLowerCase().contains(query)
                        || producto.getMarca().toLowerCase().contains(query)
                        || ProductMatcher.searchableText(producto).contains(query)) {
                    filtered.add(producto);
                }
            }
        }

        adapter.updateProductos(filtered);
        tvSinDatos.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void confirmDelete(Producto producto) {
        if (!isAdmin) {
            Toast.makeText(this, "No tienes permisos de administrador.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("Se borrara del catalogo local y de Firebase.")
                .setPositiveButton("Eliminar", (dialog, which) ->
                        productoRepository.deleteProduct(producto, message ->
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        )
                )
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
