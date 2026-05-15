package com.example.stylematchia.data.remote;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class FirebaseFavoritosRepository {

    public interface FavoritosCallback {
        void onFavoritosChanged(Set<String> favoritosIds);
    }

    private DatabaseReference getFavoritosReference() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return null;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("favoritos").child(uid);
        reference.keepSynced(true);
        return reference;
    }

    public ValueEventListener observeFavoritos(FavoritosCallback callback) {
        DatabaseReference reference = getFavoritosReference();
        if (reference == null) {
            callback.onFavoritosChanged(new HashSet<>());
            return null;
        }

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Set<String> ids = new HashSet<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Boolean value = item.getValue(Boolean.class);
                    if (Boolean.TRUE.equals(value) && item.getKey() != null) {
                        ids.add(item.getKey());
                    }
                }
                callback.onFavoritosChanged(ids);
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                callback.onFavoritosChanged(new HashSet<>());
            }
        };

        reference.addValueEventListener(listener);
        return listener;
    }

    public void removeObserver(ValueEventListener listener) {
        DatabaseReference reference = getFavoritosReference();
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }

    public void toggleFavorito(String productId, boolean isFavorite) {
        DatabaseReference reference = getFavoritosReference();
        if (reference == null) {
            return;
        }

        if (isFavorite) {
            reference.child(productId).setValue(true);
        } else {
            reference.child(productId).removeValue();
        }
    }
}
