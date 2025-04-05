package com.rafa.rpggame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.rafa.rpggame.R;
import com.rafa.rpggame.managers.UserAccountManager;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;
import android.widget.Toast;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private UserAccount userAccount;
    private Character selectedCharacter;
    private TextView coinsText;

    private TextView accountLevelText;
    private TextView staminaText;
    private TextView characterNameText;
    private TextView characterLevelText;
    private ImageView characterAvatar;
    private ProgressBar experienceBar;

    private Button exploreButton;
    private Button inventoryButton;
    private Button marketButton;
    private Button profileButton;
    private Button changeCharacterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener datos
        userAccount = UserAccountManager.getCurrentAccount();
        characterNameText = findViewById(R.id.character_name_text);
        characterLevelText = findViewById(R.id.character_level_text);
        characterAvatar = findViewById(R.id.character_avatar);
        experienceBar = findViewById(R.id.experience_bar);

        // Inicializar vistas
        accountLevelText = findViewById(R.id.account_level_text);
        staminaText = findViewById(R.id.stamina_text);
        coinsText = findViewById(R.id.coins_text);

        exploreButton = findViewById(R.id.explore_button);
        inventoryButton = findViewById(R.id.inventory_button);
        marketButton = findViewById(R.id.market_button);
        profileButton = findViewById(R.id.profile_button);
// En MainActivity.java, método onCreate:

// Modificar el listener del botón de exploración
        exploreButton.setOnClickListener(v -> {
            if (userAccount.getSelectedCharacter() == null) {
                Toast.makeText(this, "Primero debes seleccionar un personaje",
                        Toast.LENGTH_SHORT).show();

                // Ir al menú de gestión de personajes
                startActivity(new Intent(MainActivity.this, CharacterManagementActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, ExploreActivity.class));
            }
        });

// Añadir un botón para la gestión de personajes
        Button charactersButton = findViewById(R.id.characters_button);
        charactersButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CharacterManagementActivity.class));
        });
        // Comprobar si tiene personajes
        if (userAccount.getCharacters().isEmpty()) {
            // No tiene personajes, mostrar mensaje
            Toast.makeText(this, "No tienes personajes. Ve a la tienda para comprar uno.", Toast.LENGTH_LONG).show();

            // Deshabilitar botones que requieren personaje
            exploreButton.setEnabled(false);
            inventoryButton.setEnabled(false);
        } else {
            // Si tiene personajes, seleccionar el primero por defecto
            userAccount.setSelectedCharacter(userAccount.getCharacters().get(0));

            // Habilitar todos los botones
            exploreButton.setEnabled(true);
            inventoryButton.setEnabled(true);
        }

        // Configurar eventos de clic
        exploreButton.setOnClickListener(v -> {
            if (userAccount.getSelectedCharacter() == null) {
                Toast.makeText(this, "Primero debes comprar un personaje en la tienda", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(MainActivity.this, ExploreActivity.class));
        });

        inventoryButton.setOnClickListener(v -> {
            if (userAccount.getSelectedCharacter() == null) {
                Toast.makeText(this, "Primero debes comprar un personaje en la tienda", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(MainActivity.this, InventoryActivity.class));
        });

        marketButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MarketActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        updateUI();
    }

    private void updateUI() {
        accountLevelText.setText("Nivel de cuenta: " + userAccount.getAccountLevel());
        staminaText.setText("Stamina: " + userAccount.getStamina() + "/" + userAccount.getMaxStamina());
        coinsText.setText("Monedas: " + userAccount.getCoins());

        // Si hay un personaje seleccionado, mostrar su información
        if (userAccount.getSelectedCharacter() != null) {
            characterNameText.setText(userAccount.getSelectedCharacter().getName());
            characterLevelText.setText("Nivel " + userAccount.getSelectedCharacter().getLevel());
            // Configurar el avatar según la clase
            // (código para mostrar el avatar)
        } else {
            // Ocultar o mostrar un placeholder para la sección de personaje
            characterNameText.setText("Sin personaje");
            characterLevelText.setText("Compra un personaje en la tienda");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar stamina cada vez que se vuelve a la pantalla principal
        userAccount.updateStamina();
        updateUI();
    }

}