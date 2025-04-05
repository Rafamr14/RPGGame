package com.rafa.rpggame.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;

/**
 * Un gestor de datos simplificado que guarda toda la información del juego
 * directamente en SharedPreferences usando Gson para serializar/deserializar.
 */
public class GameDataManager {
    private static final String TAG = "GameDataManager";
    private static final String PREF_NAME = "game_data";
    private static final String KEY_USER_ACCOUNT = "user_account";
    private static final String KEY_CURRENT_CHARACTER_ID = "current_character_id";

    private static Context appContext;
    private static UserAccount userAccount;
    private static Gson gson;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
        gson = new GsonBuilder().serializeNulls().create();
        loadData();
    }

    /**
     * Carga los datos del juego desde SharedPreferences
     */
    private static void loadData() {
        try {
            SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String userAccountJson = prefs.getString(KEY_USER_ACCOUNT, null);
            long selectedCharacterId = prefs.getLong(KEY_CURRENT_CHARACTER_ID, -1);

            if (userAccountJson != null) {
                userAccount = gson.fromJson(userAccountJson, UserAccount.class);
                Log.d(TAG, "Loaded account: " + userAccount.getUsername());
                Log.d(TAG, "Characters: " + (userAccount.getCharacters() != null ? userAccount.getCharacters().size() : 0));

                // Restaurar el personaje seleccionado
                if (selectedCharacterId != -1 && userAccount.getCharacters() != null) {
                    for (Character character : userAccount.getCharacters()) {
                        if (character.getId() == selectedCharacterId) {
                            userAccount.setSelectedCharacter(character);
                            Log.d(TAG, "Restored selected character: " + character.getName());
                            break;
                        }
                    }
                }

                // Si no hay personaje seleccionado pero hay personajes, seleccionar el primero
                if (userAccount.getSelectedCharacter() == null &&
                        userAccount.getCharacters() != null &&
                        !userAccount.getCharacters().isEmpty()) {
                    userAccount.setSelectedCharacter(userAccount.getCharacters().get(0));
                    Log.d(TAG, "Auto-selected first character: " + userAccount.getSelectedCharacter().getName());
                    saveData();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading game data", e);
            userAccount = null;
        }
    }

    /**
     * Guarda los datos del juego en SharedPreferences
     */
    public static void saveData() {
        try {
            if (userAccount == null) {
                Log.w(TAG, "Cannot save: no account data");
                return;
            }

            SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            // Guardar la cuenta completa
            String userAccountJson = gson.toJson(userAccount);
            editor.putString(KEY_USER_ACCOUNT, userAccountJson);

            // Guardar el ID del personaje seleccionado por separado
            if (userAccount.getSelectedCharacter() != null) {
                editor.putLong(KEY_CURRENT_CHARACTER_ID, userAccount.getSelectedCharacter().getId());
                Log.d(TAG, "Saved selected character ID: " + userAccount.getSelectedCharacter().getId());
            } else {
                editor.remove(KEY_CURRENT_CHARACTER_ID);
                Log.d(TAG, "No selected character to save");
            }

            editor.apply();
            Log.d(TAG, "Game data saved successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error saving game data", e);
        }
    }

    /**
     * Crea una nueva cuenta
     */
    public static UserAccount createAccount(String username) {
        userAccount = new UserAccount(username);
        userAccount.setId(System.currentTimeMillis());
        saveData();
        return userAccount;
    }

    /**
     * Inicia sesión con un nombre de usuario
     */
    public static boolean login(String username) {
        if (userAccount != null && userAccount.getUsername().equals(username)) {
            Log.d(TAG, "Logged in as: " + username);

            // Verificar el personaje seleccionado
            if (userAccount.getSelectedCharacter() != null) {
                Log.d(TAG, "Selected character: " + userAccount.getSelectedCharacter().getName());
            } else if (userAccount.getCharacters() != null && !userAccount.getCharacters().isEmpty()) {
                userAccount.setSelectedCharacter(userAccount.getCharacters().get(0));
                Log.d(TAG, "Auto-selected first character: " + userAccount.getSelectedCharacter().getName());
                saveData();
            }

            return true;
        }
        Log.d(TAG, "Login failed: " + username);
        return false;
    }

    /**
     * Cierra la sesión actual
     */
    public static void logout() {
        saveData();
        userAccount = null;
    }

    /**
     * Obtiene la cuenta actual
     */
    public static UserAccount getCurrentAccount() {
        return userAccount;
    }

    /**
     * Actualiza los datos de la cuenta
     */
    public static void updateAccount() {
        saveData();
    }

    /**
     * Elimina todos los datos guardados
     */
    public static void clearAllData() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        userAccount = null;
        Log.d(TAG, "All game data cleared");
    }
}