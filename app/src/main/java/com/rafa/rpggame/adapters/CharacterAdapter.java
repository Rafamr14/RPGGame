package com.rafa.rpggame.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import com.rafa.rpggame.R;
import com.rafa.rpggame.models.character.Character;

public class CharacterAdapter extends ArrayAdapter<Character> {
    private static final String TAG = "CharacterAdapter";

    private Context context;
    private List<Character> characters;

    public CharacterAdapter(Context context, List<Character> characters) {
        super(context, R.layout.character_list_item, characters);
        this.context = context;

        // Asegurarse de que la lista no sea nula
        this.characters = characters != null ? characters : new ArrayList<>();

        // Registrar para depuración
        Log.d(TAG, "Creando adaptador con " + this.characters.size() + " personajes");
        for(Character c : this.characters) {
            Log.d(TAG, "Personaje cargado: " + c.getName() + " (ID: " + c.getId() + ")");
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.character_list_item, parent, false);
        }

        try {
            Character character = characters.get(position);

            ImageView avatarView = convertView.findViewById(R.id.character_avatar);
            TextView nameText = convertView.findViewById(R.id.character_name);
            TextView classText = convertView.findViewById(R.id.character_class);
            TextView levelText = convertView.findViewById(R.id.character_level);

            // Configurar avatar según clase
            switch (character.getCharacterClass()) {
                case WARRIOR:
                    avatarView.setImageResource(R.drawable.avatar_warrior);
                    break;
                case MAGE:
                    avatarView.setImageResource(R.drawable.avatar_mage);
                    break;
                case ROGUE:
                    avatarView.setImageResource(R.drawable.avatar_rogue);
                    break;
                case CLERIC:
                    avatarView.setImageResource(R.drawable.avatar_cleric);
                    break;
                default:
                    avatarView.setImageResource(R.drawable.avatar_default);
                    break;
            }

            nameText.setText(character.getName());
            classText.setText(character.getCharacterClass().toString() + " - " + character.getRole().toString());
            levelText.setText("Nivel " + character.getLevel());
        } catch (Exception e) {
            Log.e(TAG, "Error al renderizar personaje en posición " + position, e);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return characters.size();
    }

    @Override
    public Character getItem(int position) {
        if (position >= 0 && position < characters.size()) {
            return characters.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (position >= 0 && position < characters.size()) {
            return characters.get(position).getId();
        }
        return -1;
    }

    public void updateCharacters(List<Character> newCharacters) {
        // Limpiar y actualizar la lista actual
        this.characters.clear();

        // Verificar que la lista nueva no sea nula
        if (newCharacters != null) {
            // Añadir todos los personajes nuevos
            this.characters.addAll(newCharacters);

            // Registrar para depuración
            Log.d(TAG, "Actualizando adaptador con " + newCharacters.size() + " personajes");
            for (Character c : newCharacters) {
                Log.d(TAG, "Personaje actualizado: " + c.getName() + " (ID: " + c.getId() + ")");
            }
        } else {
            Log.w(TAG, "Se intentó actualizar con una lista nula");
        }

        // Notificar que los datos han cambiado
        notifyDataSetChanged();
    }
}