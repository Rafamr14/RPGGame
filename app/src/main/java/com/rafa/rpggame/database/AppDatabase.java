package com.rafa.rpggame.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.rafa.rpggame.database.converters.CharacterListConverter;
import com.rafa.rpggame.database.converters.ItemListConverter;
import com.rafa.rpggame.database.daos.UserDao;
import com.rafa.rpggame.database.entities.UserEntity;

@Database(entities = {UserEntity.class}, version = 1, exportSchema = false)
@TypeConverters({CharacterListConverter.class, ItemListConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class, "rpggame_database")
                            .allowMainThreadQueries() // No recomendado para producción
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}