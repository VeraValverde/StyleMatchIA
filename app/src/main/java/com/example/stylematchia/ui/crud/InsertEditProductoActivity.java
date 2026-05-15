package com.example.stylematchia.ui.crud;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.stylematchia.R;
import com.example.stylematchia.data.remote.FirebaseUserRepository;
import com.example.stylematchia.data.repository.ProductoRepository;
import com.example.stylematchia.logic.ProductMatcher;
import com.example.stylematchia.model.Producto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InsertEditProductoActivity extends AppCompatActivity {

    private static final String IMGBB_API_KEY = "3924ad7a534973778ca73106c431465d";

    private EditText etNombre;
    private EditText etMarca;
    private EditText etPrecio;
    private EditText etImagenUrl;
    private EditText etEnlaceTienda;
    private EditText etDescripcion;
    private ImageView ivPreviewProducto;
    private TextView tvGeneratedId;
    private Producto productoEditar;
    private ProductoRepository productoRepository;
    private FirebaseUserRepository userRepository;
    private Uri selectedImageUri;
    private boolean isAdmin = false;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    etImagenUrl.setText("");
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .into(ivPreviewProducto);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_edit_producto);

        productoRepository = new ProductoRepository(getApplicationContext());
        userRepository = new FirebaseUserRepository();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        userRepository.checkIsAdmin(currentUser.getUid(), admin -> {
            isAdmin = admin;
            if (!admin) {
                Toast.makeText(this, "Solo los administradores pueden guardar productos.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        TextView tvFormTitle = findViewById(R.id.tvFormTitle);
        tvGeneratedId = findViewById(R.id.tvGeneratedId);
        etNombre = findViewById(R.id.etNombre);
        etMarca = findViewById(R.id.etMarca);
        etPrecio = findViewById(R.id.etPrecio);
        etImagenUrl = findViewById(R.id.etImagenUrl);
        etEnlaceTienda = findViewById(R.id.etEnlaceTienda);
        etDescripcion = findViewById(R.id.etDescripcion);
        ivPreviewProducto = findViewById(R.id.ivPreviewProducto);
        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnVolver = findViewById(R.id.btnVolver);

        if (getIntent().hasExtra("producto")) {
            productoEditar = (Producto) getIntent().getSerializableExtra("producto");
            if (productoEditar != null) {
                tvFormTitle.setText("Editar producto");
                fillForm(productoEditar);
            }
        } else {
            updateGeneratedIdPreview();
        }

        etNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (productoEditar == null) {
                    updateGeneratedIdPreview();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnSeleccionarImagen.setOnClickListener(v -> imagePicker.launch("image/*"));
        btnGuardar.setOnClickListener(v -> saveProduct());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void fillForm(Producto producto) {
        tvGeneratedId.setText(producto.getId());
        etNombre.setText(producto.getNombre());
        etMarca.setText(producto.getMarca());
        etPrecio.setText(String.valueOf(producto.getPrecio()));
        etImagenUrl.setText(producto.getImagenUrl());
        etEnlaceTienda.setText(producto.getEnlaceTienda());
        etDescripcion.setText(producto.getDescripcion());
        Glide.with(this)
                .load(producto.getImagenUrl())
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(ivPreviewProducto);
    }

    private void saveProduct() {
        if (!isAdmin) {
            Toast.makeText(this, "No tienes permisos de administrador.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = etNombre.getText().toString().trim();
        String marca = etMarca.getText().toString().trim();
        String precioTexto = etPrecio.getText().toString().trim();
        String imagenUrl = etImagenUrl.getText().toString().trim();
        String enlaceTienda = etEnlaceTienda.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (nombre.isEmpty() || marca.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Completa nombre, marca y descripcion.", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = productoEditar != null ? productoEditar.getId() : generateProductId(nombre);
        tvGeneratedId.setText(id);

        double precio;
        try {
            precio = Double.parseDouble(precioTexto.isEmpty() ? "0" : precioTexto);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El precio debe ser numerico.", Toast.LENGTH_SHORT).show();
            return;
        }

        Producto producto = productoEditar != null ? productoEditar : new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setMarca(marca);
        producto.setPrecio(precio);
        producto.setEnlaceTienda(enlaceTienda);
        producto.setDescripcion(descripcion);

        if (selectedImageUri != null) {
            uploadImageAndSave(producto);
        } else {
            producto.setImagenUrl(imagenUrl);
            saveProductData(producto);
        }
    }

    private void uploadImageAndSave(Producto producto) {
        Toast.makeText(this, "Subiendo imagen...", Toast.LENGTH_SHORT).show();
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
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

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, java.io.IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(InsertEditProductoActivity.this,
                                    "Error subiendo imagen a ImgBB.", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        String responseData = response.body() != null ? response.body().string() : "";
                        JSONObject json = new JSONObject(responseData);
                        String imageUrl = json.getJSONObject("data").getString("url");

                        runOnUiThread(() -> {
                            producto.setImagenUrl(imageUrl);
                            etImagenUrl.setText(imageUrl);
                            saveProductData(producto);
                        });
                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(InsertEditProductoActivity.this,
                                        "ImgBB no devolvio una URL valida.", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo preparar la imagen.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProductData(Producto producto) {
        productoRepository.saveProduct(producto, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private String generateProductId(String nombre) {
        String base = ProductMatcher.normalize(nombre)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "")
                .toLowerCase(Locale.ROOT);
        if (base.isEmpty()) {
            base = "producto";
        }
        return base + "_" + UUID.randomUUID().toString().substring(0, 6);
    }

    private void updateGeneratedIdPreview() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            tvGeneratedId.setText("Se genera automaticamente al guardar");
            return;
        }
        String previewBase = ProductMatcher.normalize(nombre)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "")
                .toLowerCase(Locale.ROOT);
        if (previewBase.isEmpty()) {
            previewBase = "producto";
        }
        tvGeneratedId.setText(previewBase + "_xxxxxx");
    }
}
