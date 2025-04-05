package com.rafa.rpggame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.rafa.rpggame.R;
import com.rafa.rpggame.managers.UserAccountManager;
import com.rafa.rpggame.models.UserAccount;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private Button loginButton;
    private Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar vistas
        usernameInput = findViewById(R.id.username_input);
        loginButton = findViewById(R.id.login_button);
        createAccountButton = findViewById(R.id.create_account_button);

        // Comprobar si ya hay sesión iniciada
        UserAccount currentAccount = UserAccountManager.getCurrentAccount();
        if (currentAccount != null) {
            // Ir directamente a la selección de personaje
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

            boolean success = UserAccountManager.login(username);

            if (success) {
                goToMain();
            } else {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            }
        });

        createAccountButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "Ingresa un nombre de usuario", Toast.LENGTH_SHORT).show();
                return;
            }

            UserAccount newAccount = UserAccountManager.createAccount(username);

            if (newAccount != null) {
                Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                goToMain();
            } else {
                Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}