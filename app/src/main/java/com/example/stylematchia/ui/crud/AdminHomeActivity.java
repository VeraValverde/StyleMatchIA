package com.example.stylematchia.ui.crud;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stylematchia.MainActivity;
import com.example.stylematchia.R;
import com.example.stylematchia.data.remote.FirebaseUserRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminHomeActivity extends AppCompatActivity {

    private FirebaseUserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        Button btnGestionProductos = findViewById(R.id.btnAdminGestionProductos);
        Button btnGestionRecomendaciones = findViewById(R.id.btnAdminGestionRecomendaciones);
        Button btnCerrarSesion = findViewById(R.id.btnAdminLogout);

        userRepository = new FirebaseUserRepository();
        userRepository.checkIsAdmin(currentUser.getUid(), isAdmin -> {
            if (!isAdmin) {
                Toast.makeText(this, "Solo los administradores pueden entrar aqui.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnGestionProductos.setOnClickListener(v ->
                startActivity(new Intent(this, CrudProductosActivity.class))
        );
        btnGestionRecomendaciones.setOnClickListener(v ->
                startActivity(new Intent(this, GestionRecomendacionesActivity.class))
        );
        btnCerrarSesion.setOnClickListener(v -> logout());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(
                this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        );
        googleSignInClient.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }
}
