// Crear CharacterManagementActivity.java
package com.rafa.rpggame.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.rafa.rpggame.R;
import com.rafa.rpggame.adapters.CharacterAdapter;
import com.rafa.rpggame.managers.UserAccountManager;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.rafa.rpggame.R;
import com.rafa.rpggame.adapters.CharacterAdapter;
import com.rafa.rpggame.managers.UserAccountManager;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.character.Stat;
import java.util.Map;

public class CharacterManagementActivity extends AppCompatActivity {
    private UserAccount userAccount;
    private ListView characterListView;
    private TextView characterInfoText;
    private Button selectButton;
    private Button levelUpButton;
    private CharacterAdapter adapter;
    private Character selectedCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_management);

        // Obtener cuenta
        userAccount = UserAccountManager.getCurrentAccount();

        // Inicializar vistas
        characterListView = findViewById(R.id.character_list_view);
        characterInfoText = findViewById(R.id.character_info_text);
        selectButton = findViewById(R.id.select_button);
        levelUpButton = findViewById(R.id.level_up_button);

        // Configurar adaptador
        adapter = new CharacterAdapter(this, userAccount.getCharacters());
        characterListView.setAdapter(adapter);

        // Selección de personaje
        characterListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedCharacter = userAccount.getCharacters().get(position);
            updateCharacterInfo();

            // Habilitar botones
            boolean isSelected = (userAccount.getSelectedCharacter() == selectedCharacter);
            selectButton.setEnabled(!isSelected);
            levelUpButton.setEnabled(true);
        });

        // Botón seleccionar
        selectButton.setOnClickListener(v -> {
            if (selectedCharacter != null) {
                userAccount.setSelectedCharacter(selectedCharacter);
                UserAccountManager.updateAccount();
                Toast.makeText(this, "Personaje seleccionado: " + selectedCharacter.getName(),
                        Toast.LENGTH_SHORT).show();
                selectButton.setEnabled(false);

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
                    UserAccountManager.updateAccount();

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
}