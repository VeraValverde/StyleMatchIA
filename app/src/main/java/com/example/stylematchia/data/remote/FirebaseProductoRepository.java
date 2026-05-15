package com.example.stylematchia.data.remote;

import com.example.stylematchia.model.Producto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseProductoRepository {

    private final DatabaseReference productosReference;

    public FirebaseProductoRepository() {
        productosReference = FirebaseDatabase.getInstance().getReference("productos");
        productosReference.keepSynced(true);
    }

    public void addProductsListener(ValueEventListener listener) {
        productosReference.addValueEventListener(listener);
    }

    public void removeProductsListener(ValueEventListener listener) {
        productosReference.removeEventListener(listener);
    }

    public void saveProduct(Producto producto) {
        productosReference.child(producto.getId()).setValue(producto);
    }

    public void deleteProduct(String productId) {
        productosReference.child(productId).removeValue();
    }
}
