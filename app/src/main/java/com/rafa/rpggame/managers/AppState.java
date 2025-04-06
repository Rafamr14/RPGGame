package com.rafa.rpggame.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;

import java.util.List;

/**
 * Singleton para mantener el estado global de la aplicación.
 * Esta clase garantiza que todas las actividades accedan a la misma instancia de datos
 * y evita problemas de sincronización.
 */
public class AppState {
    private static final String TAG = "AppState";
    private static final String PREF_NAME = "game_state";
    private static final String KEY_USER_DATA = "user_data";

    // Instancia única del singleton
    private static AppState instance;

    // Datos de la aplicación
    private UserAccount userAccount;
    private Context appContext;
    private Gson gson;

    /**
     * Constructor privado para prevenir instanciación externa
     */
    private AppState() {
        gson = new GsonBuilder().serializeNulls().create();
    }

    /**
     * Obtener la instancia única del singleton
     */
    public static synchronized AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    /**
     * Inicializar el estado con un contexto de aplicación
     */
    public void initialize(Context context) {
        Log.d(TAG, "Inicializando AppState");
        this.appContext = context.getApplicationContext();
        loadData();
    }

    /**
     * Cargar los datos desde SharedPreferences
     */
    private void loadData() {
        try {
            if (appContext == null) {
                Log.e(TAG, "No se puede cargar datos: contexto nulo");
                return;
            }

            SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String userData = prefs.getString(KEY_USER_DATA, null);

            if (userData != null) {
                userAccount = gson.fromJson(userData, UserAccount.class);
                Log.d(TAG, "Datos de usuario cargados: " + userAccount.getUsername());

                // Verificar la integridad de los datos cargados
                if (userAccount.getCharacters() == null) {
                    userAccount.setCharacters(new java.util.ArrayList<>());
                }

                // Registrar información de los personajes para depuración
                Log.d(TAG, "Personajes cargados: " + userAccount.getCharacters().size());
                for (Character c : userAccount.getCharacters()) {
                    Log.d(TAG, "Personaje cargado: " + c.getName() + " (ID: " + c.getId() + ")");
                }

                // Verificar el personaje seleccionado
                if (userAccount.getSelectedCharacter() != null) {
                    Log.d(TAG, "Personaje seleccionado: " + userAccount.getSelectedCharacter().getName() +
                            " (ID: " + userAccount.getSelectedCharacter().getId() + ")");
                } else if (!userAccount.getCharacters().isEmpty()) {
                    userAccount.setSelectedCharacter(userAccount.getCharacters().get(0));
                    Log.d(TAG, "Auto-seleccionado personaje: " + userAccount.getSelectedCharacter().getName());
                    saveData(); // Guardar la selección automática
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar datos", e);
        }
    }

    /**
     * Guardar los datos en SharedPreferences
     */
    public void saveData() {
        try {
            if (appContext == null) {
                Log.e(TAG, "No se puede guardar datos: contexto nulo");
                return;
            }

            if (userAccount == null) {
                Log.e(TAG, "No se puede guardar datos: cuenta de usuario nula");
                return;
            }

            SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            String userData = gson.toJson(userAccount);
            editor.putString(KEY_USER_DATA, userData);

            // Guardar cambios de forma síncrona para garantizar que se escriban inmediatamente
            editor.commit();

            Log.d(TAG, "Datos guardados correctamente");

            // Registrar información de personajes para depuración
            if (userAccount.getCharacters() != null) {
                Log.d(TAG, "Personajes guardados: " + userAccount.getCharacters().size());
                for (Character c : userAccount.getCharacters()) {
                    Log.d(TAG, "Personaje guardado: " + c.getName() + " (ID: " + c.getId() + ")");
                }

                if (userAccount.getSelectedCharacter() != null) {
                    Log.d(TAG, "Personaje seleccionado guardado: " + userAccount.getSelectedCharacter().getName() +
                            " (ID: " + userAccount.getSelectedCharacter().getId() + ")");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar datos", e);
        }
    }

    /**
     * Crear una nueva cuenta de usuario
     */
    public UserAccount createAccount(String username) {
        userAccount = new UserAccount(username);
        userAccount.setId(System.currentTimeMillis());
        saveData();
        return userAccount;
    }

    /**
     * Intentar iniciar sesión con un nombre de usuario
     */
    public boolean login(String username) {
        if (userAccount != null && userAccount.getUsername().equals(username)) {
            return true;
        }
        return false;
    }

    /**
     * Obtener la cuenta de usuario actual
     */
    public UserAccount getUserAccount() {
        return userAccount;
    }

    /**
     * Cerrar sesión y limpiar datos
     */
    public void logout() {
        // Guardar los datos actuales antes de cerrar sesión
        saveData();

        // Limpiar la cuenta
        userAccount = null;

        // También podríamos borrar los datos guardados, pero mejor preservarlos
        // para facilitar el inicio de sesión posterior
    }

    /**
     * Limpiar completamente todos los datos guardados
     */
    public void clearAllData() {
        if (appContext != null) {
            SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().clear().commit();
            userAccount = null;
            Log.d(TAG, "Todos los datos borrados");
        }
    }

    /**
     * Comprobar si hay una sesión activa
     */
    public boolean isLoggedIn() {
        return userAccount != null;
    }

    /**
     * Comprobar si hay un personaje seleccionado
     */
    public boolean hasSelectedCharacter() {
        return userAccount != null && userAccount.getSelectedCharacter() != null;
    }

    /**
     * Actualizar el objeto de cuenta de usuario
     * (por ejemplo, después de modificaciones en otras partes de la app)
     */
    public void updateUserAccount(UserAccount account) {
        this.userAccount = account;
        saveData();
    }

    /**
     * Añadir un personaje a la cuenta actual
     */
    public boolean addCharacter(Character character) {
        if (userAccount == null) {
            Log.e(TAG, "No se puede añadir personaje: cuenta de usuario nula");
            return false;
        }

        // Verificar que el personaje no exista
        boolean exists = false;
        for (Character c : userAccount.getCharacters()) {
            if (c.getId() == character.getId()) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            userAccount.addCharacter(character);

            // Si no hay personaje seleccionado, seleccionar este
            if (userAccount.getSelectedCharacter() == null) {
                userAccount.setSelectedCharacter(character);
            }

            // Guardar los cambios
            saveData();

            Log.d(TAG, "Personaje añadido: " + character.getName() + " (ID: " + character.getId() + ")");
            Log.d(TAG, "Total de personajes: " + userAccount.getCharacters().size());

            return true;
        } else {
            Log.w(TAG, "El personaje ya existe: " + character.getName());
            return false;
        }
    }

    /**
     * Establecer un personaje como el seleccionado
     */
    public boolean selectCharacter(Character character) {
        if (userAccount == null) {
            Log.e(TAG, "No se puede seleccionar personaje: cuenta de usuario nula");
            return false;
        }

        // Verificar que el personaje exista en la lista
        boolean exists = false;
        for (Character c : userAccount.getCharacters()) {
            if (c.getId() == character.getId()) {
                exists = true;
                break;
            }
        }

        if (exists) {
            userAccount.setSelectedCharacter(character);
            saveData();

            Log.d(TAG, "Personaje seleccionado: " + character.getName() + " (ID: " + character.getId() + ")");
            return true;
        } else {
            Log.w(TAG, "No se puede seleccionar un personaje que no existe en la cuenta");
            return false;
        }
    }

    /**
     * Obtener el personaje seleccionado
     */
    public Character getSelectedCharacter() {
        if (userAccount == null) {
            return null;
        }
        return userAccount.getSelectedCharacter();
    }

    /**
     * Obtener la lista de personajes
     */
    public List<Character> getCharacters() {
        if (userAccount == null) {
            return new java.util.ArrayList<>();
        }
        return userAccount.getCharacters();
    }
}