package com.rafa.rpggame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.rafa.rpggame.R;
import com.rafa.rpggame.managers.AppState;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Referencias a los elementos de la UI
    private TextView accountLevelText;
    private TextView staminaText;
    private TextView coinsText;
    private TextView characterNameText;
    private TextView characterLevelText;
    private ImageView characterAvatar;
    private ProgressBar experienceBar;

    private Button exploreButton;
    private Button inventoryButton;
    private Button marketButton;
    private Button profileButton;
    private Button charactersButton;

    // Datos
    private UserAccount userAccount;
    private Character selectedCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Iniciando MainActivity");

        try {
            // Inicializar elementos de la UI
            initializeViews();

            // Cargar datos del usuario
            loadUserData();

            // Configurar listeners de botones
            setupButtonListeners();

            // Actualizar la UI
            updateUI();

            Log.d(TAG, "MainActivity inicializada correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar MainActivity", e);
            Toast.makeText(this, "Error al iniciar la aplicación", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        Log.d(TAG, "Inicializando vistas");

        // Textos de información
        accountLevelText = findViewById(R.id.account_level_text);
        staminaText = findViewById(R.id.stamina_text);
        coinsText = findViewById(R.id.coins_text);

        // Información del personaje
        characterNameText = findViewById(R.id.character_name_text);
        characterLevelText = findViewById(R.id.character_level_text);
        characterAvatar = findViewById(R.id.character_avatar);
        experienceBar = findViewById(R.id.experience_bar);

        // Botones
        exploreButton = findViewById(R.id.explore_button);
        inventoryButton = findViewById(R.id.inventory_button);
        marketButton = findViewById(R.id.market_button);
        profileButton = findViewById(R.id.profile_button);
        charactersButton = findViewById(R.id.characters_button);

        // Verificar que todos los elementos se hayan encontrado
        if (accountLevelText == null || staminaText == null || coinsText == null ||
                characterNameText == null || characterLevelText == null || characterAvatar == null ||
                exploreButton == null || inventoryButton == null || marketButton == null ||
                profileButton == null || charactersButton == null) {

            Log.e(TAG, "No se pudieron encontrar todos los elementos de la UI");
            throw new IllegalStateException("Error al inicializar vistas");
        }

        Log.d(TAG, "Vistas inicializadas correctamente");
    }

    private void loadUserData() {
        Log.d(TAG, "Cargando datos del usuario");

        // Obtener la cuenta del usuario directamente de AppState
        userAccount = AppState.getInstance().getUserAccount();

        if (userAccount == null) {
            Log.e(TAG, "No se encontró cuenta de usuario");
            // Redirigir a login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Obtener el personaje seleccionado
        selectedCharacter = AppState.getInstance().getSelectedCharacter();

        // Registrar información para depuración
        Log.d(TAG, "Cuenta cargada: " + userAccount.getUsername());
        Log.d(TAG, "Número de personajes: " + userAccount.getCharacters().size());

        if (selectedCharacter != null) {
            Log.d(TAG, "Personaje seleccionado: " + selectedCharacter.getName() +
                    " (ID: " + selectedCharacter.getId() + ")");
        } else {
            Log.d(TAG, "No hay personaje seleccionado");
        }
    }

    private void setupButtonListeners() {
        Log.d(TAG, "Configurando listeners de botones");

        // Botón de exploración
        exploreButton.setOnClickListener(v -> {
            if (selectedCharacter == null) {
                Toast.makeText(this, "Primero debes seleccionar un personaje", Toast.LENGTH_SHORT).show();
                // Ir a la gestión de personajes
                startActivity(new Intent(MainActivity.this, CharacterManagementActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, ExploreActivity.class));
            }
        });

        // Botón de personajes
        charactersButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CharacterManagementActivity.class));
        });

        // Botón de inventario
        inventoryButton.setOnClickListener(v -> {
            if (selectedCharacter == null) {
                Toast.makeText(this, "Primero debes comprar un personaje en la tienda", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(MainActivity.this, InventoryActivity.class));
        });

        // Botón de mercado
        marketButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MarketActivity.class));
        });

        // Botón de perfil
        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        Log.d(TAG, "Listeners configurados correctamente");
    }

    private void updateUI() {
        Log.d(TAG, "Actualizando UI");

        try {
            // Actualizar información de la cuenta
            accountLevelText.setText("Nivel de cuenta: " + userAccount.getAccountLevel());
            staminaText.setText("Stamina: " + userAccount.getStamina() + "/" + userAccount.getMaxStamina());
            coinsText.setText("Monedas: " + userAccount.getCoins());

            // Comprobar si hay personajes
            boolean hasCharacters = userAccount.getCharacters() != null && !userAccount.getCharacters().isEmpty();

            // Establecer estado de los botones según disponibilidad de personajes
            exploreButton.setEnabled(hasCharacters && selectedCharacter != null);
            inventoryButton.setEnabled(hasCharacters && selectedCharacter != null);

            // Actualizar información del personaje seleccionado
            if (selectedCharacter != null) {
                characterNameText.setText(selectedCharacter.getName());
                characterLevelText.setText("Nivel " + selectedCharacter.getLevel());

                // Configurar avatar según clase
                switch (selectedCharacter.getCharacterClass()) {
                    case WARRIOR:
                        characterAvatar.setImageResource(R.drawable.avatar_warrior);
                        break;
                    case MAGE:
                        characterAvatar.setImageResource(R.drawable.avatar_mage);
                        break;
                    case ROGUE:
                        characterAvatar.setImageResource(R.drawable.avatar_rogue);
                        break;
                    case CLERIC:
                        characterAvatar.setImageResource(R.drawable.avatar_cleric);
                        break;
                    default:
                        characterAvatar.setImageResource(R.drawable.avatar_default);
                        break;
                }
            } else {
                // No hay personaje seleccionado
                characterNameText.setText("Sin personaje");
                characterLevelText.setText("Compra un personaje en la tienda");
                characterAvatar.setImageResource(R.drawable.avatar_default);
            }

            Log.d(TAG, "UI actualizada correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar UI", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume MainActivity");

        try {
            // Recargar datos actualizados desde AppState
            userAccount = AppState.getInstance().getUserAccount();
            selectedCharacter = AppState.getInstance().getSelectedCharacter();

            // Actualizar stamina
            if (userAccount != null) {
                userAccount.updateStamina();
                AppState.getInstance().saveData();
            }

            // Actualizar UI
            updateUI();

            // Registrar información para depuración
            Log.d(TAG, "Datos recargados en onResume");

            if (userAccount != null) {
                Log.d(TAG, "Personajes en la cuenta: " + userAccount.getCharacters().size());
                for (Character c : userAccount.getCharacters()) {
                    Log.d(TAG, "Personaje: " + c.getName() + " (ID: " + c.getId() + ")");
                }
                if (selectedCharacter != null) {
                    Log.d(TAG, "Personaje seleccionado: " + selectedCharacter.getName() +
                            " (ID: " + selectedCharacter.getId() + ")");
                } else {
                    Log.d(TAG, "No hay personaje seleccionado");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en onResume", e);
        }
    }
}
