package com.rafa.rpggame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import com.rafa.rpggame.R;
import com.rafa.rpggame.models.character.Character;

public class CharacterAdapter extends ArrayAdapter<Character> {
    private Context context;
    private List<Character> characters;

    public CharacterAdapter(Context context, List<Character> characters) {
        super(context, R.layout.character_list_item, characters);
        this.context = context;
        this.characters = characters;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.character_list_item, parent, false);
        }

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

        return convertView;
    }

    public void updateCharacters(List<Character> newCharacters) {
        this.characters.clear();
        this.characters.addAll(newCharacters);
        notifyDataSetChanged();
    }
}