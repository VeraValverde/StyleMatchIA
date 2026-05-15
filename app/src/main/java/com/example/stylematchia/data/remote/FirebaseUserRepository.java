package com.example.stylematchia.data.remote;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUserRepository {

    public interface AdminCallback {
        void onResult(boolean isAdmin);
    }

    public interface UserProfileCallback {
        void onLoaded(String nombre, String email, String photoUrl);
    }

    public void saveUser(FirebaseUser user) {
        if (user == null) {
            return;
        }

        String email = user.getEmail() != null ? user.getEmail() : "";
        String fallbackName = email.contains("@") ? email.substring(0, email.indexOf("@")) : "usuario";

        Map<String, Object> values = new HashMap<>();
        values.put("uid", user.getUid());
        values.put("email", email);
        values.put("nombre", user.getDisplayName() != null && !user.getDisplayName().trim().isEmpty()
                ? user.getDisplayName()
                : fallbackName);
        values.put("photoUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");

        FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(user.getUid())
                .updateChildren(values);

        FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(user.getUid())
                .child("isAdmin")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        FirebaseDatabase.getInstance()
                                .getReference("usuarios")
                                .child(user.getUid())
                                .child("isAdmin")
                                .setValue(false);
                    }
                });
    }

    public void loadUserProfile(String uid, UserProfileCallback callback) {
        if (uid == null || uid.trim().isEmpty()) {
            callback.onLoaded("", "", "");
            return;
        }

        FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String photoUrl = snapshot.child("photoUrl").getValue(String.class);
                        callback.onLoaded(
                                nombre != null ? nombre : "",
                                email != null ? email : "",
                                photoUrl != null ? photoUrl : ""
                        );
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onLoaded("", "", "");
                    }
                });
    }

    public void updateUserProfile(String uid, String nombre, String email, String photoUrl) {
        if (uid == null || uid.trim().isEmpty()) {
            return;
        }

        Map<String, Object> values = new HashMap<>();
        values.put("uid", uid);
        values.put("nombre", nombre != null ? nombre : "");
        values.put("email", email != null ? email : "");
        values.put("photoUrl", photoUrl != null ? photoUrl : "");

        FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(uid)
                .updateChildren(values);
    }

    public void checkIsAdmin(String uid, AdminCallback callback) {
        if (uid == null || uid.trim().isEmpty()) {
            callback.onResult(false);
            return;
        }

        FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(uid)
                .child("isAdmin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Boolean value = snapshot.getValue(Boolean.class);
                        callback.onResult(Boolean.TRUE.equals(value));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onResult(false);
                    }
                });
    }
}
