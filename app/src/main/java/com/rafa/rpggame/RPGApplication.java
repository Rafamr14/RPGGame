package com.rafa.rpggame;

import android.app.Application;
import com.rafa.rpggame.managers.GameDataManager;
import com.rafa.rpggame.managers.MarketManager;
import com.rafa.rpggame.managers.ZoneManager;

public class RPGApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar gestores
        GameDataManager.initialize(this);  // Usa el nuevo gestor en lugar de UserAccountManager
        MarketManager.initialize(this);
        ZoneManager.initialize();
    }
}