package com.rafa.rpggame.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.rafa.rpggame.R;
import com.rafa.rpggame.adapters.CharacterAdapter;
import com.rafa.rpggame.managers.GameDataManager;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.character.Stat;
import java.util.Map;

public class CharacterManagementActivity extends AppCompatActivity {
    private static final String TAG = "CharacterManagement";

    private UserAccount userAccount;
    private ListView characterListView;
    private TextView characterInfoText;
    private Button selectButton;
    private Button levelUpButton;
    private Button refreshButton; // Nuevo botón para refrescar
    private CharacterAdapter adapter;
    private Character selectedCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_management);

        Log.d(TAG, "Iniciando CharacterManagementActivity");

        // Obtener cuenta
        userAccount = GameDataManager.getCurrentAccount();
        Log.d(TAG, "Cuenta cargada: " + userAccount.getUsername());
        Log.d(TAG, "Número de personajes: " +
                (userAccount.getCharacters() != null ? userAccount.getCharacters().size() : 0));

        if (userAccount.getCharacters() != null) {
            for (Character c : userAccount.getCharacters()) {
                Log.d(TAG, "Personaje encontrado: " + c.getName() + " (ID: " + c.getId() + ")");
            }
        }

        // Inicializar vistas
        characterListView = findViewById(R.id.character_list_view);
        characterInfoText = findViewById(R.id.character_info_text);
        selectButton = findViewById(R.id.select_button);
        levelUpButton = findViewById(R.id.level_up_button);

        // Añadir botón de refresco
        refreshButton = new Button(this);
        refreshButton.setText("Refrescar Personajes");
        refreshButton.setOnClickListener(v -> forceRefreshCharacterList());

        // Añadir el botón a la vista (puedes modificar esto para que coincida con tu layout)
        ViewGroup layout = findViewById(R.id.character_management_layout);
        if (layout != null) {
            layout.addView(refreshButton);
        }

        // Configurar adaptador
        adapter = new CharacterAdapter(this, userAccount.getCharacters());
        characterListView.setAdapter(adapter);

        // Selección de personaje
        characterListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedCharacter = userAccount.getCharacters().get(position);
            updateCharacterInfo();
            Log.d(TAG, "Personaje seleccionado: " + selectedCharacter.getName());

            // Habilitar botones
            boolean isSelected = (userAccount.getSelectedCharacter() == selectedCharacter);
            selectButton.setEnabled(!isSelected);
            levelUpButton.setEnabled(true);
        });

        // Botón seleccionar
        selectButton.setOnClickListener(v -> {
            if (selectedCharacter != null) {
                userAccount.setSelectedCharacter(selectedCharacter);
                GameDataManager.updateAccount();
                Toast.makeText(this, "Personaje seleccionado: " + selectedCharacter.getName(),
                        Toast.LENGTH_SHORT).show();
                selectButton.setEnabled(false);
                Log.d(TAG, "Personaje seleccionado como actual: " + selectedCharacter.getName());

                // Actualizar UI
                adapter.notifyDataSetChanged();
            }
        });

        // Botón subir nivel
        levelUpButton.setOnClickListener(v -> {
            if (selectedCharacter != null) {
                // Costo para subir de nivel: 100 monedas × nivel actual
                int cost = selectedCharacter.getLevel() * 100;

                if (userAccount.getCoins() >= cost) {
                    userAccount.reduceCoins(cost);
                    selectedCharacter.levelUp();
                    GameDataManager.updateAccount();
                    Log.d(TAG, "Personaje subido a nivel " + selectedCharacter.getLevel());

                    // Actualizar UI
                    updateCharacterInfo();
                    Toast.makeText(this, "¡Personaje subido a nivel " +
                            selectedCharacter.getLevel() + "!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No tienes suficientes monedas. Necesitas: " +
                            cost, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Desactivar botones inicialmente
        selectButton.setEnabled(false);
        levelUpButton.setEnabled(false);

        // Forzar refresco inicial
        forceRefreshCharacterList();
    }

    private void updateCharacterInfo() {
        if (selectedCharacter != null) {
            StringBuilder info = new StringBuilder();
            info.append("Nombre: ").append(selectedCharacter.getName()).append("\n");
            info.append("Clase: ").append(selectedCharacter.getCharacterClass()).append("\n");
            info.append("Rol: ").append(selectedCharacter.getRole()).append("\n");
            info.append("Nivel: ").append(selectedCharacter.getLevel()).append("\n\n");

            // Añadir estadísticas
            info.append("Estadísticas:\n");
            for (Map.Entry<Stat, Integer> entry : selectedCharacter.getStats().entrySet()) {
                info.append("• ").append(entry.getKey()).append(": ")
                        .append(entry.getValue()).append("\n");
            }

            info.append("\nCosto para subir de nivel: ").append(selectedCharacter.getLevel() * 100)
                    .append(" monedas");

            characterInfoText.setText(info.toString());
        } else {
            characterInfoText.setText("Selecciona un personaje");
        }
    }

    /**
     * Método para forzar la actualización de la lista de personajes
     */
    private void forceRefreshCharacterList() {
        try {
            Log.d(TAG, "Forzando refresco de lista de personajes");

            // Recargar datos actualizados
            userAccount = GameDataManager.getCurrentAccount();

            // Obtener la lista actualizada de personajes
            Log.d(TAG, "Personajes en cuenta: " +
                    (userAccount.getCharacters() != null ? userAccount.getCharacters().size() : 0));

            if (userAccount.getCharacters() != null) {
                for (Character c : userAccount.getCharacters()) {
                    Log.d(TAG, "Personaje disponible: " + c.getName() + " (ID: " + c.getId() + ")");
                }
            }

            // Actualizar el adaptador con la nueva lista
            if (adapter != null) {
                adapter.updateCharacters(userAccount.getCharacters());
                Log.d(TAG, "Adaptador actualizado con nuevos datos");
            } else {
                adapter = new CharacterAdapter(this, userAccount.getCharacters());
                characterListView.setAdapter(adapter);
                Log.d(TAG, "Nuevo adaptador creado");
            }

            // Actualizar selección en la lista
            selectedCharacter = userAccount.getSelectedCharacter();
            if (selectedCharacter != null && userAccount.getCharacters() != null) {
                int position = userAccount.getCharacters().indexOf(selectedCharacter);
                if (position >= 0) {
                    characterListView.setItemChecked(position, true);
                    updateCharacterInfo();
                    Log.d(TAG, "Seleccionado personaje en posición " + position);
                }
            }

            Toast.makeText(this, "Lista de personajes actualizada", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error al refrescar lista de personajes", e);
            Toast.makeText(this, "Error al actualizar lista", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume CharacterManagementActivity");

        // Recargar datos
        userAccount = GameDataManager.getCurrentAccount();

        // Actualizar la lista de personajes
        forceRefreshCharacterList();
    }
}