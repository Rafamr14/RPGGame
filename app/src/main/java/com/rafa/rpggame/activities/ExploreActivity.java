package com.rafa.rpggame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.rafa.rpggame.R;
import com.rafa.rpggame.adapters.CharacterAdapter;
import com.rafa.rpggame.adapters.ZoneAdapter;
import com.rafa.rpggame.dialogs.ExplorationResultDialog;
import com.rafa.rpggame.managers.GameDataManager;
import com.rafa.rpggame.managers.ZoneManager;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.zones.Exploration;
import com.rafa.rpggame.models.zones.Zone;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {
    private static final String TAG = "ExploreActivity";

    private UserAccount userAccount;
    private Character selectedCharacter;

    private TextView characterInfoText;
    private TextView staminaText;
    private Spinner zoneSpinner;
    private ListView characterListView;
    private Button exploreButton;
    private Button refreshButton; // Nuevo botón para forzar refresco
    private CharacterAdapter characterAdapter;
    private ZoneAdapter zoneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        Log.d(TAG, "Iniciando ExploreActivity");

        // Obtener datos
        userAccount = GameDataManager.getCurrentAccount();
        selectedCharacter = userAccount.getSelectedCharacter();

        // Inicializar vistas
        characterInfoText = findViewById(R.id.character_info_text);
        staminaText = findViewById(R.id.stamina_text);
        zoneSpinner = findViewById(R.id.zone_spinner);
        characterListView = findViewById(R.id.character_list_view);
        exploreButton = findViewById(R.id.explore_button);

        // Añadir botón de refresco
        refreshButton = new Button(this);
        refreshButton.setText("Refrescar Lista");
        refreshButton.setOnClickListener(v -> forceRefreshCharacterList());

        // Añadir el botón a la vista (puedes modificar esto para que coincida con tu layout)
        ViewGroup layout = findViewById(R.id.explore_layout);
        if (layout != null) {
            layout.addView(refreshButton);
        }

        // Verificar si hay un personaje seleccionado
        if (selectedCharacter == null) {
            showMessage("Primero debes seleccionar un personaje");

            // Si hay personajes disponibles pero ninguno seleccionado, seleccionar el primero
            if (userAccount.getCharacters() != null && !userAccount.getCharacters().isEmpty()) {
                selectedCharacter = userAccount.getCharacters().get(0);
                userAccount.setSelectedCharacter(selectedCharacter);
                GameDataManager.updateAccount();
                showMessage("Se ha seleccionado automáticamente: " + selectedCharacter.getName());
            } else {
                // Si no hay personajes, volver a la pantalla principal
                showMessage("No tienes personajes. Ve a la tienda para comprar uno.");
                finish();
                return;
            }
        }

        // Configurar adaptadores
        Log.d(TAG, "Configurando adaptadores");
        Log.d(TAG, "Personajes disponibles: " + (userAccount.getCharacters() != null ? userAccount.getCharacters().size() : 0));

        if (userAccount.getCharacters() != null) {
            for (Character c : userAccount.getCharacters()) {
                Log.d(TAG, "Personaje disponible: " + c.getName() + " (ID: " + c.getId() + ")");
            }
        }

        characterAdapter = new CharacterAdapter(this, userAccount.getCharacters());
        characterListView.setAdapter(characterAdapter);

        // Ahora que estamos seguros de que selectedCharacter no es null
        List<Zone> availableZones = ZoneManager.getAvailableZones(selectedCharacter.getLevel());
        zoneAdapter = new ZoneAdapter(this, availableZones);
        zoneSpinner.setAdapter(zoneAdapter);

        // Seleccionar personaje actual en la lista
        int selectedPosition = userAccount.getCharacters().indexOf(selectedCharacter);
        if (selectedPosition >= 0) {
            characterListView.setItemChecked(selectedPosition, true);
            Log.d(TAG, "Personaje seleccionado en la posición " + selectedPosition);
        } else {
            Log.w(TAG, "No se pudo encontrar el personaje seleccionado en la lista");
        }

        // Eventos
        characterListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedCharacter = userAccount.getCharacters().get(position);
            userAccount.setSelectedCharacter(selectedCharacter);
            GameDataManager.updateAccount();
            updateUI();

            // Actualizar lista de zonas disponibles según nivel del personaje
            List<Zone> zones = ZoneManager.getAvailableZones(selectedCharacter.getLevel());
            zoneAdapter = new ZoneAdapter(ExploreActivity.this, zones);
            zoneSpinner.setAdapter(zoneAdapter);

            Log.d(TAG, "Personaje seleccionado de la lista: " + selectedCharacter.getName());
        });

        exploreButton.setOnClickListener(v -> {
            // Verificar nuevamente que selectedCharacter no sea null
            if (selectedCharacter == null) {
                showMessage("Primero debes seleccionar un personaje");
                return;
            }

            Zone selectedZone = (Zone) zoneSpinner.getSelectedItem();

            if (selectedZone == null) {
                showMessage("Selecciona una zona para explorar");
                return;
            }

            if (selectedCharacter.getLevel() < selectedZone.getMinLevel()) {
                showMessage("Nivel insuficiente para explorar esta zona");
                return;
            }

            if (userAccount.getStamina() < selectedZone.getStaminaCost()) {
                showMessage("No tienes suficiente stamina");
                return;
            }

            // Iniciar exploración
            startExploration(selectedZone);
        });

        updateUI();

        // Forzar refresco inicial de la lista
        forceRefreshCharacterList();
    }

    private void updateUI() {
        if (selectedCharacter != null) {
            characterInfoText.setText(selectedCharacter.getName() + " - Nivel " + selectedCharacter.getLevel());
        } else {
            characterInfoText.setText("No hay personaje seleccionado");
        }
        staminaText.setText("Stamina: " + userAccount.getStamina() + "/" + userAccount.getMaxStamina());
    }

    private void startExploration(Zone zone) {
        // Guardar el estado actual antes de iniciar exploración
        GameDataManager.updateAccount();

        Exploration exploration = zone.startExploration(selectedCharacter, userAccount);

        if (exploration != null) {
            // Mostrar diálogo de resultados
            ExplorationResultDialog dialog = new ExplorationResultDialog(exploration);
            dialog.show(getSupportFragmentManager(), "exploration_result");

            // Actualizar UI
            updateUI();

            // Guardar cambios en la cuenta
            GameDataManager.updateAccount();
        } else {
            showMessage("No se pudo iniciar la exploración");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Actualizar los datos del personaje seleccionado
        userAccount = GameDataManager.getCurrentAccount();
        selectedCharacter = userAccount.getSelectedCharacter();

        // Verificar si todavía tenemos un personaje seleccionado
        if (selectedCharacter == null && userAccount.getCharacters() != null && !userAccount.getCharacters().isEmpty()) {
            selectedCharacter = userAccount.getCharacters().get(0);
            userAccount.setSelectedCharacter(selectedCharacter);
            GameDataManager.updateAccount();
        }

        // Si no hay personaje seleccionado, volver a la actividad principal
        if (selectedCharacter == null) {
            showMessage("No tienes personajes. Ve a la tienda para comprar uno.");
            finish();
            return;
        }

        // Actualizar la interfaz
        updateUI();

        // Forzar refresco de la lista de personajes
        forceRefreshCharacterList();
    }

    /**
     * Método para forzar la actualización de la lista de personajes
     */
    private void forceRefreshCharacterList() {
        try {
            Log.d(TAG, "Forzando refresco de lista de personajes");

            // Recargar datos actualizados
            userAccount = GameDataManager.getCurrentAccount();

            // Recargar la lista desde GameDataManager
            List<Character> characters = userAccount.getCharacters();

            // Registrar la lista actual
            Log.d(TAG, "Refrescando lista de personajes. Total: " +
                    (characters != null ? characters.size() : 0));

            if (characters != null) {
                for (Character c : characters) {
                    Log.d(TAG, "Personaje en lista: " + c.getName() + " (ID: " + c.getId() + ")");
                }
            }

            // Forzar la actualización del adaptador
            if (characterAdapter != null) {
                characterAdapter.updateCharacters(characters);
                Log.d(TAG, "Adaptador actualizado con nuevos datos");
            } else {
                // Si el adaptador no existe, crearlo
                characterAdapter = new CharacterAdapter(this, characters);
                characterListView.setAdapter(characterAdapter);
                Log.d(TAG, "Creado nuevo adaptador para los personajes");
            }

            // Marcar el personaje seleccionado en la lista
            selectedCharacter = userAccount.getSelectedCharacter();
            if (selectedCharacter != null && characters != null) {
                int position = characters.indexOf(selectedCharacter);
                if (position >= 0) {
                    characterListView.setItemChecked(position, true);
                    Log.d(TAG, "Marcado personaje seleccionado en posición " + position);
                } else {
                    Log.w(TAG, "No se pudo encontrar el personaje seleccionado en la lista");
                }
            }

            // Mostrar mensaje informativo
            showMessage("Lista de personajes actualizada");
        } catch (Exception e) {
            Log.e(TAG, "Error al refrescar lista de personajes", e);
            showMessage("Error al actualizar la lista de personajes");
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}