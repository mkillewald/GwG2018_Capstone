package com.gameaholix.coinops;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;

public class CoinOpsApp extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // This class is called out in the manifest <application> tag, which makes sure that the
        // below line is run only once and before any other Firebase calls.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        MobileAds.initialize(this, "ca-app-pub-5476169458046094~8586198814");
    }
}
