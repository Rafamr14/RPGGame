package com.rafa.rpggame.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;

import java.util.ArrayList;

/**
 * Gestor de datos simplificado para evitar problemas de persistencia
 */
public class GameDataManager {
    private static final String TAG = "GameDataManager";
    private static final String PREF_NAME = "game_data";
    private static final String KEY_USER_ACCOUNT = "user_account";
    private static final String KEY_SELECTED_CHARACTER_ID = "selected_character_id";

    private static Context appContext;
    private static UserAccount userAccount;
    private static Gson gson;

    /**
     * Inicializa el gestor de datos con un contexto de aplicación
     */
    public static void initialize(Context context) {
        if (context == null) {
            Log.e(TAG, "Context nulo al inicializar GameDataManager");
            return;
        }

        appContext = context.getApplicationContext();
        gson = new GsonBuilder()
                .serializeNulls()
                .create();

        // Intenta cargar datos existentes
        loadData();
    }

    /**
     * Carga los datos del juego desde SharedPreferences
     */
    private static void loadData() {
        try {
            if (appContext == null) {
                Log.e(TAG, "Context nulo al cargar datos");
                return;
            }

            SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            // Cargar datos de la cuenta
            String userAccountJson = prefs.getString(KEY_USER_ACCOUNT, null);
            if (userAccountJson != null) {
                try {
                    // Intentar deserializar la cuenta
                    userAccount = gson.fromJson(userAccountJson, UserAccount.class);

                    // Verificar que la cuenta sea válida
                    if (userAccount != null) {
                        Log.d(TAG, "Cuenta cargada: " + userAccount.getUsername());

                        // Asegurar que la lista de personajes no sea nula
                        if (userAccount.getCharacters() == null) {
                            userAccount.setCharacters(new ArrayList<>());
                        }

                        // Cargar el ID del personaje seleccionado
                        long selectedCharacterId = prefs.getLong(KEY_SELECTED_CHARACTER_ID, -1);
                        if (selectedCharacterId != -1) {
                            // Buscar el personaje por ID
                            Character selectedCharacter = null;
                            for (Character character : userAccount.getCharacters()) {
                                if (character.getId() == selectedCharacterId) {
                                    selectedCharacter = character;
                                    break;
                                }
                            }

                            // Establecer el personaje seleccionado
                            if (selectedCharacter != null) {
                                userAccount.setSelectedCharacter(selectedCharacter);
                                Log.d(TAG, "Personaje seleccionado restaurado: " + selectedCharacter.getName());
                            } else if (!userAccount.getCharacters().isEmpty()) {
                                // Si no se encuentra el personaje guardado pero hay personajes, seleccionar el primero
                                userAccount.setSelectedCharacter(userAccount.getCharacters().get(0));
                                Log.d(TAG, "Personaje seleccionado no encontrado, se seleccionó automáticamente: " +
                                        userAccount.getSelectedCharacter().getName());
                            }
                        } else if (!userAccount.getCharacters().isEmpty()) {
                            // Si no hay personaje seleccionado guardado pero hay personajes, seleccionar el primero
                            userAccount.setSelectedCharacter(userAccount.getCharacters().get(0));
                            Log.d(TAG, "Seleccionado automáticamente el primer personaje: " +
                                    userAccount.getSelectedCharacter().getName());
                        }
                    } else {
                        Log.e(TAG, "La cuenta deserializada es nula");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error al deserializar la cuenta", e);
                    userAccount = null;
                }
            } else {
                Log.d(TAG, "No se encontró una cuenta guardada");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar datos", e);
            userAccount = null;
        }
    }

    /**
     * Guarda los datos del juego en SharedPreferences
     */
    public static void saveData() {
        try {
            if (appContext == null) {
                Log.e(TAG, "Context nulo al guardar datos");
                return;
            }

            if (userAccount == null) {
                Log.w(TAG, "No hay cuenta para guardar");
                return;
            }

            SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            // Guardar datos de la cuenta
            String userAccountJson = gson.toJson(userAccount);
            editor.putString(KEY_USER_ACCOUNT, userAccountJson);

            // Guardar ID del personaje seleccionado
            if (userAccount.getSelectedCharacter() != null) {
                editor.putLong(KEY_SELECTED_CHARACTER_ID, userAccount.getSelectedCharacter().getId());
                Log.d(TAG, "Guardado ID del personaje seleccionado: " + userAccount.getSelectedCharacter().getId());
            } else {
                editor.remove(KEY_SELECTED_CHARACTER_ID);
                Log.d(TAG, "No hay personaje seleccionado para guardar");
            }

            // Aplicar cambios
            editor.apply();
            Log.d(TAG, "Datos guardados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar datos", e);
        }
    }

    /**
     * Crea una nueva cuenta de usuario
     */
    public static UserAccount createAccount(String username) {
        try {
            // Verificar que el nombre de usuario sea válido
            if (username == null || username.trim().isEmpty()) {
                Log.e(TAG, "Nombre de usuario inválido");
                return null;
            }

            // Crear nueva cuenta
            userAccount = new UserAccount(username);
            userAccount.setId(System.currentTimeMillis());

            // Guardar la cuenta
            saveData();

            return userAccount;
        } catch (Exception e) {
            Log.e(TAG, "Error al crear cuenta", e);
            return null;
        }
    }

    /**
     * Inicia sesión con un nombre de usuario
     */
    public static boolean login(String username) {
        try {
            // Verificar que el nombre de usuario sea válido
            if (username == null || username.trim().isEmpty()) {
                Log.e(TAG, "Nombre de usuario inválido");
                return false;
            }

            // Verificar si la cuenta actual coincide
            if (userAccount != null && userAccount.getUsername().equals(username)) {
                Log.d(TAG, "Sesión iniciada como: " + username);
                return true;
            }

            Log.d(TAG, "Inicio de sesión fallido: " + username);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error al iniciar sesión", e);
            return false;
        }
    }

    /**
     * Cierra la sesión actual
     */
    public static void logout() {
        try {
            // Guardar datos antes de cerrar sesión
            saveData();

            // Limpiar la referencia a la cuenta
            userAccount = null;

            Log.d(TAG, "Sesión cerrada correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al cerrar sesión", e);
        }
    }

    /**
     * Obtiene la cuenta actual
     */
    public static UserAccount getCurrentAccount() {
        if (userAccount == null) {
            // Intentar cargar datos si no hay cuenta
            loadData();
        }
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
        try {
            if (appContext != null) {
                SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                prefs.edit().clear().apply();
            }

            userAccount = null;
            Log.d(TAG, "Todos los datos han sido eliminados");
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar datos", e);
        }
    }

    /**
     * Verifica si existe una sesión activa
     */
    public static boolean hasActiveSession() {
        return userAccount != null;
    }
}