package com.rafa.rpggame.managers;

import android.content.Context;
import android.util.Log;
import com.rafa.rpggame.database.AppDatabase;
import com.rafa.rpggame.database.entities.UserEntity;
import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;
import java.util.ArrayList;
import java.util.List;

public class UserAccountManager {
    private static final String TAG = "UserAccountManager";
    private static UserAccount currentAccount;
    private static List<UserAccount> allAccounts;
    private static Context appContext;
    private static AppDatabase database;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
        database = AppDatabase.getDatabase(appContext);
        loadAccounts();
    }

    private static void loadAccounts() {
        allAccounts = new ArrayList<>();

        try {
            // Cargar todas las cuentas desde la base de datos
            List<UserEntity> userEntities = database.userDao().getAllUsers();
            Log.d(TAG, "Loaded " + userEntities.size() + " accounts from database");

            for (UserEntity entity : userEntities) {
                UserAccount account = convertEntityToAccount(entity);
                allAccounts.add(account);
                Log.d(TAG, "Loaded account: " + account.getUsername() + ", characters: " +
                        (account.getCharacters() != null ? account.getCharacters().size() : 0));
            }

            // Si no hay cuenta actual seleccionada, intentar obtenerla
            if (currentAccount == null && !allAccounts.isEmpty()) {
                currentAccount = allAccounts.get(0);
                Log.d(TAG, "Selected first account as current: " + currentAccount.getUsername());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading accounts from database", e);
        }
    }

    private static UserAccount convertEntityToAccount(UserEntity entity) {
        try {
            UserAccount account = new UserAccount(entity.getUsername());
            account.setId(entity.getId());
            account.setAccountLevel(entity.getAccountLevel());
            account.setStamina(entity.getStamina());
            account.setMaxStamina(entity.getMaxStamina());
            account.setCoins(entity.getCoins());
            account.setGems(entity.getGems());

            // Limpiar y establecer los personajes
            account.setCharacters(new ArrayList<>());
            List<Character> characters = entity.getCharacters();
            if (characters != null && !characters.isEmpty()) {
                for (Character character : characters) {
                    account.addCharacter(character);
                    Log.d(TAG, "Added character: " + character.getName() + ", ID: " + character.getId());

                    // Seleccionar el personaje correspondiente
                    if (character.getId() == entity.getSelectedCharacterId()) {
                        account.setSelectedCharacter(character);
                        Log.d(TAG, "Selected character: " + character.getName());
                    }
                }
            }

            // Si no se encontró el personaje seleccionado pero hay personajes, seleccionar el primero
            if (account.getSelectedCharacter() == null && !account.getCharacters().isEmpty()) {
                account.setSelectedCharacter(account.getCharacters().get(0));
                Log.d(TAG, "Auto-selected first character: " + account.getSelectedCharacter().getName());
            }

            // Establecer el buzón
            if (entity.getMailbox() != null) {
                account.setMailbox(entity.getMailbox());
            }

            // Establecer el tiempo de regeneración de stamina
            account.setLastStaminaRegenTime(entity.getLastStaminaRegenTime());

            return account;
        } catch (Exception e) {
            Log.e(TAG, "Error converting entity to account", e);
            throw e;
        }
    }

    private static UserEntity convertAccountToEntity(UserAccount account) {
        try {
            long selectedCharacterId = 0;
            if (account.getSelectedCharacter() != null) {
                selectedCharacterId = account.getSelectedCharacter().getId();
                Log.d(TAG, "Selected character ID for account " + account.getUsername() + ": " + selectedCharacterId);
            } else {
                Log.w(TAG, "No selected character for account " + account.getUsername());
            }

            UserEntity entity = new UserEntity(
                    account.getId(),
                    account.getUsername(),
                    account.getAccountLevel(),
                    account.getExperience(),
                    account.getStamina(),
                    account.getMaxStamina(),
                    account.getCoins(),
                    account.getGems(),
                    account.getCharacters(),
                    selectedCharacterId,
                    account.getMailbox(),
                    account.getLastStaminaRegenTime()
            );

            return entity;
        } catch (Exception e) {
            Log.e(TAG, "Error converting account to entity", e);
            throw e;
        }
    }

    public static UserAccount createAccount(String username) {
        try {
            // Verificar si ya existe
            UserEntity existingUser = database.userDao().getUserByUsername(username);
            if (existingUser != null) {
                Log.d(TAG, "Account already exists: " + username);
                return null;
            }

            // Crear nueva cuenta
            UserAccount newAccount = new UserAccount(username);
            newAccount.setId(System.currentTimeMillis()); // ID único basado en tiempo
            allAccounts.add(newAccount);
            currentAccount = newAccount;

            // Guardar en la base de datos
            UserEntity entity = convertAccountToEntity(newAccount);
            database.userDao().insertUser(entity);
            Log.d(TAG, "Created and saved new account: " + username);

            return newAccount;
        } catch (Exception e) {
            Log.e(TAG, "Error creating account", e);
            throw e;
        }
    }

    public static boolean login(String username) {
        try {
            UserEntity userEntity = database.userDao().getUserByUsername(username);
            if (userEntity != null) {
                currentAccount = convertEntityToAccount(userEntity);
                Log.d(TAG, "Logged in as: " + username);

                // Verificar si tiene personajes y personaje seleccionado
                if (currentAccount.getCharacters() != null && !currentAccount.getCharacters().isEmpty()) {
                    Log.d(TAG, "Account has " + currentAccount.getCharacters().size() + " characters");
                    if (currentAccount.getSelectedCharacter() != null) {
                        Log.d(TAG, "Selected character: " + currentAccount.getSelectedCharacter().getName());
                    } else {
                        Log.w(TAG, "No selected character after login!");
                        currentAccount.setSelectedCharacter(currentAccount.getCharacters().get(0));
                        Log.d(TAG, "Auto-selected first character: " + currentAccount.getSelectedCharacter().getName());
                        updateAccount();
                    }
                } else {
                    Log.d(TAG, "Account has no characters");
                }

                return true;
            }
            Log.d(TAG, "Login failed: " + username);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error during login", e);
            throw e;
        }
    }

    public static void logout() {
        try {
            // Guardar el estado actual antes de cerrar sesión
            if (currentAccount != null) {
                database.userDao().updateUser(convertAccountToEntity(currentAccount));
                Log.d(TAG, "Logged out and saved account state: " + currentAccount.getUsername());
            }
            currentAccount = null;
        } catch (Exception e) {
            Log.e(TAG, "Error during logout", e);
        }
    }

    public static UserAccount getCurrentAccount() {
        return currentAccount;
    }

    public static List<UserAccount> getAllAccounts() {
        return allAccounts;
    }

    public static void updateAccount() {
        try {
            if (currentAccount != null) {
                // Log del estado de la cuenta antes de guardar
                Log.d(TAG, "Updating account: " + currentAccount.getUsername());
                Log.d(TAG, "Characters: " + (currentAccount.getCharacters() != null ? currentAccount.getCharacters().size() : 0));

                if (currentAccount.getSelectedCharacter() != null) {
                    Log.d(TAG, "Selected character: " + currentAccount.getSelectedCharacter().getName() +
                            ", ID: " + currentAccount.getSelectedCharacter().getId());
                } else {
                    Log.w(TAG, "No selected character!");

                    // Intentar seleccionar un personaje automáticamente
                    if (currentAccount.getCharacters() != null && !currentAccount.getCharacters().isEmpty()) {
                        currentAccount.setSelectedCharacter(currentAccount.getCharacters().get(0));
                        Log.d(TAG, "Auto-selected first character: " + currentAccount.getSelectedCharacter().getName());
                    }
                }

                // Actualizar en la base de datos
                UserEntity entity = convertAccountToEntity(currentAccount);
                database.userDao().updateUser(entity);
                Log.d(TAG, "Account updated in database");

                // Verificar que se guardó correctamente
                UserEntity savedEntity = database.userDao().getUserByUsername(currentAccount.getUsername());
                if (savedEntity != null) {
                    Log.d(TAG, "Verification - Selected character ID in DB: " + savedEntity.getSelectedCharacterId());
                }
            } else {
                Log.w(TAG, "Cannot update: no current account");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating account", e);
            throw e;
        }
    }
}