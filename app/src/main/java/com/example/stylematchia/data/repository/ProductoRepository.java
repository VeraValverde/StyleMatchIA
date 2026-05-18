package com.example.stylematchia.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.stylematchia.data.local.AppDatabase;
import com.example.stylematchia.data.local.ProductoDao;
import com.example.stylematchia.data.remote.FirebaseProductoRepository;
import com.example.stylematchia.model.Producto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductoRepository {

    public interface ProductosCallback {
        void onProductosLoaded(List<Producto> productos);
    }

    public interface ActionCallback {
        void onComplete(String message);
    }

    private final ProductoDao productoDao;
    private final FirebaseProductoRepository firebaseRepository;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private ValueEventListener productosListener;

    public ProductoRepository(Context context) {
        productoDao = AppDatabase.getInstance(context).productoDao();
        firebaseRepository = new FirebaseProductoRepository();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void observeProducts(ProductosCallback callback) {
        loadLocalProducts(callback);

        stopObserving();
        productosListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Producto> remoteProducts = new ArrayList<>();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Producto producto = item.getValue(Producto.class);
                    if (producto != null) {
                        if (producto.getId().trim().isEmpty()) {
                            producto.setId(item.getKey() != null ? item.getKey() : "");
                        }
                        remoteProducts.add(producto);
                    }
                }

                executorService.execute(() -> {
                    try {
                        productoDao.replaceAll(remoteProducts);
                        List<Producto> currentProducts = productoDao.getAll();
                        mainHandler.post(() -> callback.onProductosLoaded(currentProducts));
                    } catch (Exception e) {
                        loadLocalProducts(callback);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadLocalProducts(callback);
            }
        };

        firebaseRepository.addProductsListener(productosListener);
    }

    public void saveProduct(Producto producto, ActionCallback callback) {
        executorService.execute(() -> {
            productoDao.insert(producto);
            firebaseRepository.saveProduct(producto);
            mainHandler.post(() -> callback.onComplete("Producto guardado en Room y Firebase."));
        });
    }

    public void deleteProduct(Producto producto, ActionCallback callback) {
        executorService.execute(() -> {
            productoDao.deleteById(producto.getId());
            firebaseRepository.deleteProduct(producto.getId());
            mainHandler.post(() -> callback.onComplete("Producto eliminado en Room y Firebase."));
        });
    }

    public void stopObserving() {
        if (productosListener != null) {
            firebaseRepository.removeProductsListener(productosListener);
            productosListener = null;
        }
    }

    private void loadLocalProducts(ProductosCallback callback) {
        executorService.execute(() -> {
            List<Producto> localProducts = productoDao.getAll();
            mainHandler.post(() -> callback.onProductosLoaded(localProducts));
        });
    }
}
