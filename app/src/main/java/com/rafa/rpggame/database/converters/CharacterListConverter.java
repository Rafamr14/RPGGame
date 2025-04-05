package com.rafa.rpggame.database.converters;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rafa.rpggame.models.character.Character;
import java.lang.reflect.Type;
import java.util.List;

public class CharacterListConverter {
    @TypeConverter
    public static String fromCharacterList(List<Character> characters) {
        if (characters == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Character>>() {}.getType();
        return gson.toJson(characters, type);
    }

    @TypeConverter
    public static List<Character> toCharacterList(String charactersString) {
        if (charactersString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Character>>() {}.getType();
        return gson.fromJson(charactersString, type);
    }
}
