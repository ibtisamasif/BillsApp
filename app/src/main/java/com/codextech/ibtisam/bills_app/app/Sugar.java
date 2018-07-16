package com.codextech.ibtisam.bills_app.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.orm.SugarContext;

public class Sugar extends Application {
    private static final String TAG = "SugarApplicationClass";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: Sugar Class");
        super.onCreate();
        SugarContext.init(getApplicationContext());

//        // create table if not exists
//        SchemaGenerator schemaGenerator = new SchemaGenerator(this);
//        schemaGenerator.createDatabase(new SugarDb(this).getDB());

//        // SQUARE memory leakage library
//        Log.d(TAG, "onCreate: SquareLeakLibrary");
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
//        // Normal app init code...

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, "onLowMemory: ");
//        Toast.makeText(this, "Memory Low", Toast.LENGTH_SHORT).show();
        super.onLowMemory();
    }
}
