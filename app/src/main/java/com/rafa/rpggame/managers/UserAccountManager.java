package com.rafa.rpggame.managers;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rafa.rpggame.models.UserAccount;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserAccountManager {
    private static final String PREF_NAME = "user_account_prefs";
    private static final String KEY_CURRENT_ACCOUNT = "current_account";
    private static final String KEY_ALL_ACCOUNTS = "all_accounts";

    private static UserAccount currentAccount;
    private static List<UserAccount> allAccounts;
    private static Context appContext;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
        loadAccounts();
    }

    private static void loadAccounts() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // Cargar todas las cuentas
        String accountsJson = prefs.getString(KEY_ALL_ACCOUNTS, null);
        if (accountsJson != null) {
            Type accountListType = new TypeToken<ArrayList<UserAccount>>(){}.getType();
            allAccounts = gson.fromJson(accountsJson, accountListType);
        } else {
            allAccounts = new ArrayList<>();
        }

        // Cargar cuenta actual
        String currentAccountJson = prefs.getString(KEY_CURRENT_ACCOUNT, null);
        if (currentAccountJson != null) {
            currentAccount = gson.fromJson(currentAccountJson, UserAccount.class);
        } else {
            currentAccount = null;
        }
    }

    private static void saveAccounts() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        // Guardar todas las cuentas
        String accountsJson = gson.toJson(allAccounts);
        editor.putString(KEY_ALL_ACCOUNTS, accountsJson);

        // Guardar cuenta actual
        if (currentAccount != null) {
            String currentAccountJson = gson.toJson(currentAccount);
            editor.putString(KEY_CURRENT_ACCOUNT, currentAccountJson);
        }

        editor.apply();
    }

    public static UserAccount createAccount(String username) {
        // Verificar si ya existe el nombre de usuario
        for (UserAccount account : allAccounts) {
            if (account.getUsername().equals(username)) {
                return null; // Nombre de usuario ya existe
            }
        }

        UserAccount newAccount = new UserAccount(username);
        allAccounts.add(newAccount);
        currentAccount = newAccount;
        saveAccounts();

        return newAccount;
    }

    public static boolean login(String username) {
        for (UserAccount account : allAccounts) {
            if (account.getUsername().equals(username)) {
                currentAccount = account;
                saveAccounts();
                return true;
            }
        }

        return false;
    }

    public static void logout() {
        currentAccount = null;
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_CURRENT_ACCOUNT);
        editor.apply();
    }

    public static UserAccount getCurrentAccount() {
        return currentAccount;
    }

    public static List<UserAccount> getAllAccounts() {
        return allAccounts;
    }

    public static void updateAccount() {
        saveAccounts();
    }
}