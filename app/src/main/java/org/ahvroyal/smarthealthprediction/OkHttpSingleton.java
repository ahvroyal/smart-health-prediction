package org.ahvroyal.smarthealthprediction;

import okhttp3.OkHttpClient;

public class OkHttpSingleton extends OkHttpClient {

    private static OkHttpSingleton oInstance;

    private OkHttpSingleton() {

    }

    public static synchronized OkHttpSingleton getInstance() {
        if (oInstance == null) {
            oInstance = new OkHttpSingleton();
        }

        return oInstance;
    }

}
