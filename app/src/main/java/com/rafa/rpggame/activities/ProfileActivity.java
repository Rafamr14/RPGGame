package com.rafa.rpggame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.rafa.rpggame.R;
import com.rafa.rpggame.managers.GameDataManager;
import com.rafa.rpggame.models.UserAccount;

public class ProfileActivity extends AppCompatActivity {
    private UserAccount userAccount;

    private TextView usernameText;
    private TextView accountLevelText;
    private TextView coinsText;
    private TextView gemsText;
    private TextView staminaText;
    private TextView charactersText;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userAccount = GameDataManager.getCurrentAccount();

        // Inicializar vistas
        usernameText = findViewById(R.id.username_text);
        accountLevelText = findViewById(R.id.account_level_text);
        coinsText = findViewById(R.id.coins_text);
        gemsText = findViewById(R.id.gems_text);
        staminaText = findViewById(R.id.stamina_text);
        charactersText = findViewById(R.id.characters_text);
        logoutButton = findViewById(R.id.logout_button);

        // Configurar UI
        updateUI();

        // Evento de logout
        logoutButton.setOnClickListener(v -> {
            // Guardar datos actuales antes de cerrar sesión
            GameDataManager.saveData();

            // Ahora cerrar sesión
            GameDataManager.logout();

            // Ir a la pantalla de login
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            // Limpiar historial de actividades para evitar volver atrás
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Cerrar esta actividad
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Recargar datos frescos
        userAccount = GameDataManager.getCurrentAccount();
        updateUI();
    }

    private void updateUI() {
        usernameText.setText(userAccount.getUsername());
        accountLevelText.setText("Nivel de cuenta: " + userAccount.getAccountLevel());
        coinsText.setText("Monedas: " + userAccount.getCoins());
        gemsText.setText("Gemas: " + userAccount.getGems());
        staminaText.setText("Stamina: " + userAccount.getStamina() + "/" + userAccount.getMaxStamina());
        charactersText.setText("Personajes: " + userAccount.getCharacters().size() + "/" + userAccount.getMaxCharacters());
    }
}