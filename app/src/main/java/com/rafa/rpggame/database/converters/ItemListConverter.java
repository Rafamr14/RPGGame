package com.rafa.rpggame.database.converters;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rafa.rpggame.models.items.Item;
import java.lang.reflect.Type;
import java.util.List;

public class ItemListConverter {
    @TypeConverter
    public static String fromItemList(List<Item> items) {
        if (items == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Item>>() {}.getType();
        return gson.toJson(items, type);
    }

    @TypeConverter
    public static List<Item> toItemList(String itemsString) {
        if (itemsString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Item>>() {}.getType();
        return gson.fromJson(itemsString, type);
    }
}