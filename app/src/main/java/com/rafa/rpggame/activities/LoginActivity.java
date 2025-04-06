package com.rafa.rpggame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.rafa.rpggame.R;
import com.rafa.rpggame.managers.AppState;
import com.rafa.rpggame.models.UserAccount;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText usernameInput;
    private Button loginButton;
    private Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "Iniciando LoginActivity");

        // Inicializar vistas
        usernameInput = findViewById(R.id.username_input);
        loginButton = findViewById(R.id.login_button);
        createAccountButton = findViewById(R.id.create_account_button);

        // Comprobar si ya hay sesión iniciada
        if (AppState.getInstance().isLoggedIn()) {
            Log.d(TAG, "Sesión activa detectada, redirigiendo a MainActivity");
            goToMain();
            return;
        }

        // Configurar eventos
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "Ingresa un nombre de usuario", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = AppState.getInstance().login(username);

            if (success) {
                Log.d(TAG, "Inicio de sesión exitoso para: " + username);
                goToMain();
            } else {
                Log.d(TAG, "Inicio de sesión fallido para: " + username);
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            }
        });

        createAccountButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "Ingresa un nombre de usuario", Toast.LENGTH_SHORT).show();
                return;
            }

            UserAccount newAccount = AppState.getInstance().createAccount(username);

            if (newAccount != null) {
                Log.d(TAG, "Nueva cuenta creada: " + username);
                Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                goToMain();
            } else {
                Log.d(TAG, "Error al crear cuenta: " + username);
                Toast.makeText(this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show();
            }
        });

        Log.d(TAG, "LoginActivity inicializada");
    }

    private void goToMain() {
        Log.d(TAG, "Redirigiendo a MainActivity");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Cerrar esta actividad para no poder volver atrás
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume LoginActivity");

        // Verificar si hay una sesión activa por si se vuelve a esta actividad
        if (AppState.getInstance().isLoggedIn()) {
            Log.d(TAG, "Sesión activa detectada en onResume, redirigiendo a MainActivity");
            goToMain();
        }
    }
}