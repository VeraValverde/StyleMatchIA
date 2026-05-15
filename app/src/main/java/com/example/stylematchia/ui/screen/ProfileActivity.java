package com.example.stylematchia.ui.screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.stylematchia.MainActivity;
import com.example.stylematchia.R;
import com.example.stylematchia.data.remote.FirebaseUserRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONObject;

import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String IMGBB_API_KEY = "3924ad7a534973778ca73106c431465d";

    private FirebaseUserRepository userRepository;
    private FirebaseUser currentUser;
    private ImageView ivProfilePhoto;
    private TextView tvNombre;
    private TextView tvGmail;
    private String currentPhotoUrl = "";

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    uploadProfilePhoto(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        userRepository = new FirebaseUserRepository();
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        tvNombre = findViewById(R.id.tvProfileName);
        tvGmail = findViewById(R.id.tvProfileEmail);
        Button btnCambiarFoto = findViewById(R.id.btnProfilePhoto);
        Button btnCerrarSesion = findViewById(R.id.btnProfileLogout);

        ImageButton btnNavInicio = findViewById(R.id.btnNavInicio);
        ImageButton btnNavIa = findViewById(R.id.btnNavIa);
        ImageButton btnNavFavoritos = findViewById(R.id.btnNavFavoritos);
        ImageButton btnNavRecomendaciones = findViewById(R.id.btnNavRecomendaciones);
        ImageButton btnNavPerfil = findViewById(R.id.btnNavPerfil);

        loadProfileData();

        btnCambiarFoto.setOnClickListener(v -> imagePicker.launch("image/*"));
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(
                    this,
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
            );
            googleSignInClient.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        });

        btnNavInicio.setOnClickListener(v -> openActivity(HomeActivity.class));
        btnNavIa.setOnClickListener(v -> openActivity(AiAssistantActivity.class));
        btnNavFavoritos.setOnClickListener(v -> openActivity(FavoritesActivity.class));
        btnNavRecomendaciones.setOnClickListener(v -> openActivity(RecomendacionesActivity.class));
        btnNavPerfil.setOnClickListener(v -> {});
    }

    private void loadProfileData() {
        String fallbackName = currentUser.getDisplayName();
        if (fallbackName == null || fallbackName.trim().isEmpty()) {
            String email = currentUser.getEmail();
            fallbackName = email != null && email.contains("@")
                    ? email.substring(0, email.indexOf("@"))
                    : "Usuario StyleMatch";
        }

        tvNombre.setText(fallbackName);
        tvGmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        loadPhoto(currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "");

        userRepository.loadUserProfile(currentUser.getUid(), (nombre, email, photoUrl) -> runOnUiThread(() -> {
            if (!nombre.trim().isEmpty()) {
                tvNombre.setText(nombre);
            }
            if (!email.trim().isEmpty()) {
                tvGmail.setText(email);
            }
            currentPhotoUrl = photoUrl;
            loadPhoto(photoUrl);
        }));
    }

    private void loadPhoto(String photoUrl) {
        Glide.with(this)
                .load(photoUrl == null || photoUrl.trim().isEmpty() ? R.drawable.ic_profile : photoUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(ivProfilePhoto);
    }

    private void uploadProfilePhoto(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Toast.makeText(this, "No se pudo leer la imagen.", Toast.LENGTH_SHORT).show();
                return;
            }

            byte[] bytes = new byte[inputStream.available()];
            int readBytes = inputStream.read(bytes);
            inputStream.close();

            if (readBytes <= 0) {
                Toast.makeText(this, "La imagen seleccionada no es valida.", Toast.LENGTH_SHORT).show();
                return;
            }

            String base64Image = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("image", base64Image)
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.imgbb.com/1/upload?key=" + IMGBB_API_KEY)
                    .post(body)
                    .build();

            Toast.makeText(this, "Subiendo foto de perfil...", Toast.LENGTH_SHORT).show();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, java.io.IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(ProfileActivity.this, "No se pudo subir la foto.", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        String responseData = response.body() != null ? response.body().string() : "";
                        JSONObject json = new JSONObject(responseData);
                        String imageUrl = json.getJSONObject("data").getString("url");

                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(imageUrl))
                                .build();

                        currentUser.updateProfile(request).addOnCompleteListener(task -> {
                            userRepository.updateUserProfile(
                                    currentUser.getUid(),
                                    tvNombre.getText().toString(),
                                    tvGmail.getText().toString(),
                                    imageUrl
                            );
                            runOnUiThread(() -> {
                                currentPhotoUrl = imageUrl;
                                loadPhoto(imageUrl);
                                Toast.makeText(ProfileActivity.this, "Foto de perfil actualizada.", Toast.LENGTH_SHORT).show();
                            });
                        });
                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(ProfileActivity.this, "No se pudo leer la respuesta de ImgBB.", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo preparar la foto.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openActivity(Class<?> target) {
        startActivity(new Intent(this, target));
    }
}
