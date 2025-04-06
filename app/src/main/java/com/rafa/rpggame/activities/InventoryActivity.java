package com.rafa.rpggame.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.rafa.rpggame.R;
import com.rafa.rpggame.adapters.EquipmentAdapter;
import com.rafa.rpggame.adapters.ItemAdapter;
import com.rafa.rpggame.dialogs.SellItemDialog;
import com.rafa.rpggame.managers.GameDataManager;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.character.Stat;
import com.rafa.rpggame.models.items.ConsumableItem;
import com.rafa.rpggame.models.items.EquipableItem;
import com.rafa.rpggame.models.items.EquipmentSlot;
import com.rafa.rpggame.models.items.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity {
    private UserAccount userAccount;
    private Character selectedCharacter;

    private TabHost tabHost;
    private TextView characterInfoText;
    private GridView inventoryGrid;
    private GridView equipmentGrid;
    private TextView statsText;
    private Button useButton;
    private Button sellButton;

    private ItemAdapter inventoryAdapter;
    private EquipmentAdapter equipmentAdapter;
    private Item selectedItem;
    private EquipmentSlot selectedSlot;

    public void refreshInventory() {
        // Actualizar UI
        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Obtener datos actualizados del GameDataManager
        userAccount = GameDataManager.getCurrentAccount();
        selectedCharacter = userAccount.getSelectedCharacter();

        // Verificar si hay personaje seleccionado
        if (selectedCharacter == null) {
            // Intentar seleccionar un personaje automáticamente
            if (userAccount.getCharacters() != null && !userAccount.getCharacters().isEmpty()) {
                selectedCharacter = userAccount.getCharacters().get(0);
                userAccount.setSelectedCharacter(selectedCharacter);
                GameDataManager.updateAccount();
                Toast.makeText(this, "Se ha seleccionado automáticamente: " + selectedCharacter.getName(),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No tienes personajes. Ve a la tienda para comprar uno.",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        // Inicializar vistas
        tabHost = findViewById(R.id.tab_host);
        characterInfoText = findViewById(R.id.character_info_text);
        inventoryGrid = findViewById(R.id.inventory_grid);
        equipmentGrid = findViewById(R.id.equipment_grid);
        statsText = findViewById(R.id.stats_text);
        useButton = findViewById(R.id.use_button);
        sellButton = findViewById(R.id.sell_button);

        // Configurar tabs
        tabHost.setup();

        TabHost.TabSpec inventoryTab = tabHost.newTabSpec("inventory");
        inventoryTab.setIndicator("Inventario");
        inventoryTab.setContent(R.id.inventory_tab);
        tabHost.addTab(inventoryTab);

        TabHost.TabSpec equipmentTab = tabHost.newTabSpec("equipment");
        equipmentTab.setIndicator("Equipo");
        equipmentTab.setContent(R.id.equipment_tab);
        tabHost.addTab(equipmentTab);

        TabHost.TabSpec statsTab = tabHost.newTabSpec("stats");
        statsTab.setIndicator("Estadísticas");
        statsTab.setContent(R.id.stats_tab);
        tabHost.addTab(statsTab);

        // Configurar adaptadores
        inventoryAdapter = new ItemAdapter(this, selectedCharacter.getInventory());
        inventoryGrid.setAdapter(inventoryAdapter);

        equipmentAdapter = new EquipmentAdapter(this, selectedCharacter.getEquipment());
        equipmentGrid.setAdapter(equipmentAdapter);

        // Eventos
        inventoryGrid.setOnItemClickListener((parent, view, position, id) -> {
            selectedItem = selectedCharacter.getInventory().get(position);
            selectedSlot = null;
            updateActionButtons();
        });

        equipmentGrid.setOnItemClickListener((parent, view, position, id) -> {
            selectedItem = null;
            selectedSlot = EquipmentSlot.values()[position];
            updateActionButtons();
        });

        useButton.setOnClickListener(v -> {
            if (selectedItem != null) {
                if (selectedItem instanceof EquipableItem) {
                    // Equipar
                    selectedCharacter.equip((EquipableItem) selectedItem);
                } else if (selectedItem instanceof ConsumableItem) {
                    // Usar
                    selectedItem.use(selectedCharacter);
                    selectedCharacter.getInventory().remove(selectedItem);
                }

                updateUI();
                GameDataManager.updateAccount();
            } else if (selectedSlot != null) {
                // Desequipar
                selectedCharacter.unequip(selectedSlot);
                updateUI();
                GameDataManager.updateAccount();
            }
        });

        sellButton.setOnClickListener(v -> {
            if (selectedItem != null) {
                // Mostrar diálogo de venta
                SellItemDialog dialog = new SellItemDialog(selectedItem);
                dialog.show(getSupportFragmentManager(), "sell_item");
            }
        });

        updateUI();
    }

    private void updateUI() {
        // Asegurarse de tener datos actualizados
        userAccount = GameDataManager.getCurrentAccount();
        selectedCharacter = userAccount.getSelectedCharacter();

        if (selectedCharacter == null) {
            Toast.makeText(this, "Error: No hay personaje seleccionado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        characterInfoText.setText(selectedCharacter.getName() + " - Nivel " + selectedCharacter.getLevel());

        // Actualizar adaptadores
        inventoryAdapter.updateItems(selectedCharacter.getInventory());
        equipmentAdapter.updateEquipment(selectedCharacter.getEquipment());

        // Actualizar estadísticas
        StringBuilder statsBuilder = new StringBuilder();
        HashMap<Stat, Integer> stats = selectedCharacter.getStats();

        for (Map.Entry<Stat, Integer> entry : stats.entrySet()) {
            statsBuilder.append(entry.getKey().toString())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }

        statsText.setText(statsBuilder.toString());

        // Restablecer selección
        selectedItem = null;
        selectedSlot = null;
        updateActionButtons();
    }

    private void updateActionButtons() {
        if (selectedItem != null) {
            if (selectedItem instanceof EquipableItem) {
                useButton.setText("Equipar");
            } else {
                useButton.setText("Usar");
            }
            useButton.setEnabled(true);
            sellButton.setEnabled(true);
        } else if (selectedSlot != null && selectedCharacter.getEquipment().getItemInSlot(selectedSlot) != null) {
            useButton.setText("Desequipar");
            useButton.setEnabled(true);
            sellButton.setEnabled(false);
        } else {
            useButton.setEnabled(false);
            sellButton.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Recargar datos actualizados
        userAccount = GameDataManager.getCurrentAccount();
        selectedCharacter = userAccount.getSelectedCharacter();

        // Verificar si todavía hay un personaje seleccionado
        if (selectedCharacter == null) {
            if (userAccount.getCharacters() != null && !userAccount.getCharacters().isEmpty()) {
                selectedCharacter = userAccount.getCharacters().get(0);
                userAccount.setSelectedCharacter(selectedCharacter);
                GameDataManager.updateAccount();
            } else {
                Toast.makeText(this, "No tienes personajes", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        // Actualizar UI con datos frescos
        updateUI();
    }
}