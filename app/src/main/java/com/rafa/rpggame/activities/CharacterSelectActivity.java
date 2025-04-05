package com.rafa.rpggame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.rafa.rpggame.R;
import com.rafa.rpggame.adapters.CharacterAdapter;
import com.rafa.rpggame.managers.UserAccountManager;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;

public class CharacterSelectActivity extends AppCompatActivity {
    private UserAccount userAccount;
    private ListView characterListView;
    private Button createCharacterButton;
    private TextView accountInfoText;
    private CharacterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_select);

        // Obtener la cuenta del usuario
        userAccount = UserAccountManager.getCurrentAccount();

        // Si no hay cuenta, ir a login
        if (userAccount == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Inicializar vistas
        characterListView = findViewById(R.id.character_list_view);
        createCharacterButton = findViewById(R.id.create_character_button);
        accountInfoText = findViewById(R.id.account_info_text);

        // Configurar adaptador
        adapter = new CharacterAdapter(this, userAccount.getCharacters());
        characterListView.setAdapter(adapter);

        // Configurar texto de info
        accountInfoText.setText("Nivel de cuenta: " + userAccount.getAccountLevel());

        // Eventos de clic
        characterListView.setOnItemClickListener((parent, view, position, id) -> {
            Character selectedCharacter = userAccount.getCharacters().get(position);
            userAccount.setSelectedCharacter(selectedCharacter);
            UserAccountManager.updateAccount();

            // Ir a la pantalla principal
            startActivity(new Intent(CharacterSelectActivity.this, MainActivity.class));
        });

    }
    public void refreshCharacterList() {
        // Recargar la lista de personajes
        if (adapter != null && userAccount != null) {
            adapter.updateCharacters(userAccount.getCharacters());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar lista de personajes
        if (adapter != null && userAccount != null) {
            adapter.updateCharacters(userAccount.getCharacters());
        }
    }
}