package com.example.stylematchia;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stylematchia.data.remote.FirebaseUserRepository;
import com.example.stylematchia.ui.crud.AdminHomeActivity;
import com.example.stylematchia.ui.screen.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {

    private enum AuthMode {
        LOGIN,
        REGISTER
    }

    private FirebaseAuth auth;
    private FirebaseUserRepository userRepository;
    private GoogleSignInClient googleSignInClient;
    private LinearLayout layoutConfirmPassword;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private ImageButton btnTogglePassword;
    private ImageButton btnToggleConfirmPassword;
    private TextView tvAuthTitle;
    private TextView tvAuthSubtitle;
    private TextView tvStatus;
    private Button btnLogin;
    private Button btnRegister;
    private AuthMode authMode = AuthMode.LOGIN;
    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    private final ActivityResultLauncher<Intent> googleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData() == null) {
                    return;
                }

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    Toast.makeText(this, getString(R.string.google_sign_in_error), Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        userRepository = new FirebaseUserRepository();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);
        tvAuthTitle = findViewById(R.id.tvAuthTitle);
        tvAuthSubtitle = findViewById(R.id.tvAuthSubtitle);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        Button btnGoogle = findViewById(R.id.btnGoogle);
        tvStatus = findViewById(R.id.tvStatus);

        showLogin();

        btnLogin.setOnClickListener(v -> {
            if (authMode == AuthMode.LOGIN) {
                loginUser();
            } else {
                registerUser();
            }
        });
        btnRegister.setOnClickListener(v -> {
            if (authMode == AuthMode.LOGIN) {
                showRegister();
            } else {
                showLogin();
            }
        });
        btnGoogle.setOnClickListener(v -> googleLauncher.launch(googleSignInClient.getSignInIntent()));
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility(false));
        btnToggleConfirmPassword.setOnClickListener(v -> togglePasswordVisibility(true));
    }

    private void showLogin() {
        authMode = AuthMode.LOGIN;
        clearStatus();
        etName.setVisibility(View.GONE);
        layoutConfirmPassword.setVisibility(View.GONE);
        tvAuthTitle.setText("Iniciar sesion");
        tvAuthSubtitle.setText("Tu estilo. Nuestro match.");
        btnLogin.setText("Iniciar sesion");
        btnRegister.setText("Crear cuenta");
    }

    private void showRegister() {
        authMode = AuthMode.REGISTER;
        clearStatus();
        etName.setVisibility(View.VISIBLE);
        layoutConfirmPassword.setVisibility(View.VISIBLE);
        tvAuthTitle.setText("Crear cuenta");
        tvAuthSubtitle.setText("Crea tu perfil para guardar favoritos.");
        btnLogin.setText("Registrarse");
        btnRegister.setText("Ya tengo cuenta");
    }

    private void clearStatus() {
        tvStatus.setText("");
        etName.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
    }

    private void togglePasswordVisibility(boolean confirmField) {
        EditText target = confirmField ? etConfirmPassword : etPassword;
        ImageButton button = confirmField ? btnToggleConfirmPassword : btnTogglePassword;
        boolean visible = confirmField ? !confirmPasswordVisible : !passwordVisible;

        target.setInputType(visible
                ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        target.setSelection(target.getText().length());
        button.setImageResource(visible ? R.drawable.ic_visibility_off : R.drawable.ic_visibility);

        if (confirmField) {
            confirmPasswordVisible = visible;
        } else {
            passwordVisible = visible;
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateLoginForm(email, password)) {
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveCurrentUser();
                        openHome();
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        tvStatus.setText("Debes registrarte antes de iniciar sesion.");
                    } else {
                        tvStatus.setText("Correo o contrasena incorrectos. Si no tienes cuenta, debes registrarte.");
                    }
                });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateRegisterForm(name, email, password, confirmPassword)) {
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(request).addOnCompleteListener(updateTask -> {
                                saveCurrentUser();
                                openHome();
                            });
                        } else {
                            openHome();
                        }
                    } else {
                        tvStatus.setText(getRegisterErrorMessage(task.getException()));
                    }
                });
    }

    private String getRegisterErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthUserCollisionException) {
            return "Ese correo ya esta registrado. Inicia sesion.";
        }
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return "La contrasena es demasiado debil. Usa minimo 6 caracteres.";
        }
        if (exception instanceof FirebaseAuthInvalidCredentialsException
                || exception instanceof FirebaseAuthEmailException) {
            return "Debes poner un correo valido.";
        }
        if (exception != null && exception.getMessage() != null) {
            return "No se pudo registrar: " + exception.getMessage();
        }
        return "No se pudo registrar. Revisa Firebase Authentication.";
    }

    private boolean validateLoginForm(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Debes escribir un correo electronico.");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Debes poner un correo valido.");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Debes escribir una contrasena.");
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("La contrasena debe tener minimo 6 caracteres.");
            return false;
        }

        return true;
    }

    private boolean validateRegisterForm(String name, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            etName.setError("Debes escribir tu nombre.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contrasenas no coinciden.");
            return false;
        }
        return validateLoginForm(email, password);
    }

    private void firebaseAuthWithGoogle(@NonNull GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveCurrentUser();
                        openHome();
                    } else {
                        Toast.makeText(this, getString(R.string.google_sign_in_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveCurrentUser() {
        userRepository.saveUser(auth.getCurrentUser());
    }

    private void openHome() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return;
        }

        userRepository.checkIsAdmin(user.getUid(), isAdmin -> {
            Class<?> target = isAdmin ? AdminHomeActivity.class : HomeActivity.class;
            startActivity(new Intent(this, target));
            finish();
        });
    }
}
