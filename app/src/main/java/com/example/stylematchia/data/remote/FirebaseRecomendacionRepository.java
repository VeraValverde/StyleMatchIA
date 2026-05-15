package com.example.stylematchia.data.remote;

import com.example.stylematchia.model.Recomendacion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRecomendacionRepository {

    public interface RecommendationsCallback {
        void onLoaded(List<Recomendacion> recomendaciones);
    }

    public interface ActionCallback {
        void onComplete(boolean success, String message);
    }

    private final DatabaseReference recomendacionesReference;

    public FirebaseRecomendacionRepository() {
        recomendacionesReference = FirebaseDatabase.getInstance().getReference("recomendaciones");
        recomendacionesReference.keepSynced(true);
    }

    public ValueEventListener observeRecommendations(RecommendationsCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Recomendacion> recomendaciones = new ArrayList<>();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Recomendacion recomendacion = item.getValue(Recomendacion.class);
                    if (recomendacion != null) {
                        if (recomendacion.getId().trim().isEmpty()) {
                            recomendacion.setId(item.getKey() != null ? item.getKey() : "");
                        }
                        recomendaciones.add(recomendacion);
                    }
                }

                callback.onLoaded(recomendaciones);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onLoaded(new ArrayList<>());
            }
        };

        recomendacionesReference.addValueEventListener(listener);
        return listener;
    }

    public void removeObserver(ValueEventListener listener) {
        if (listener != null) {
            recomendacionesReference.removeEventListener(listener);
        }
    }

    public void saveRecommendation(Recomendacion recomendacion, ActionCallback callback) {
        String id = recomendacion.getId().trim().isEmpty()
                ? recomendacionesReference.push().getKey()
                : recomendacion.getId();

        if (id == null || id.trim().isEmpty()) {
            callback.onComplete(false, "No se pudo crear la recomendacion.");
            return;
        }

        recomendacion.setId(id);
        recomendacionesReference.child(id)
                .setValue(recomendacion)
                .addOnSuccessListener(unused ->
                        callback.onComplete(true, "Recomendacion enviada correctamente."))
                .addOnFailureListener(error ->
                        callback.onComplete(false, "No se pudo guardar la recomendacion."));
    }

    public void updateStatus(String recommendationId, String estado, ActionCallback callback) {
        if (recommendationId == null || recommendationId.trim().isEmpty()) {
            callback.onComplete(false, "No se pudo actualizar el estado.");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("estado", estado);

        recomendacionesReference.child(recommendationId)
                .updateChildren(updates)
                .addOnSuccessListener(unused ->
                        callback.onComplete(true, "Estado actualizado."))
                .addOnFailureListener(error ->
                        callback.onComplete(false, "No se pudo actualizar el estado."));
    }

    public void deleteRecommendation(String recommendationId, ActionCallback callback) {
        if (recommendationId == null || recommendationId.trim().isEmpty()) {
            callback.onComplete(false, "No se pudo borrar la recomendacion.");
            return;
        }

        recomendacionesReference.child(recommendationId)
                .removeValue()
                .addOnSuccessListener(unused ->
                        callback.onComplete(true, "Recomendacion eliminada."))
                .addOnFailureListener(error ->
                        callback.onComplete(false, "No se pudo eliminar la recomendacion."));
    }
}
