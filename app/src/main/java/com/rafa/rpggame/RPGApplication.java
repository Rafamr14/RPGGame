package com.rafa.rpggame;

import android.app.Application;
import android.util.Log;
import com.rafa.rpggame.managers.AppState;
import com.rafa.rpggame.managers.MarketManager;
import com.rafa.rpggame.managers.ZoneManager;

public class RPGApplication extends Application {
    private static final String TAG = "RPGApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Iniciando aplicación RPG");

        try {
            // Inicializar nuestro singleton de estado global
            AppState.getInstance().initialize(this);
            Log.d(TAG, "AppState inicializado");

            // Inicializar otros gestores
            MarketManager.initialize(this);
            ZoneManager.initialize();

            Log.d(TAG, "Todos los gestores inicializados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar la aplicación", e);
        }
    }
}