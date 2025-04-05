package com.rafa.rpggame;

import android.app.Application;
import com.rafa.rpggame.managers.MarketManager;
import com.rafa.rpggame.managers.UserAccountManager;
import com.rafa.rpggame.managers.ZoneManager;

public class RPGApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar gestores
        UserAccountManager.initialize(this);
        MarketManager.initialize(this);
        ZoneManager.initialize();
    }
}