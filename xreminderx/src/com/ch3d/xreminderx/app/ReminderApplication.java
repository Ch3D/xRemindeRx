
package com.ch3d.xreminderx.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Application;

import com.ch3d.xreminderx.module.ActivityModule;
import com.ch3d.xreminderx.module.AndroidModule;
import com.ch3d.xreminderx.module.ParseCloudModule;
import com.ch3d.xreminderx.module.ServiceModule;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import dagger.ObjectGraph;

public class ReminderApplication extends Application {
    private ObjectGraph graph;
    private final List<Object> injectList = new ArrayList<Object>();

    private GoogleApiClient mPlusClient;

    public GoogleApiClient getGoogleApi() {
        return mPlusClient;
    }

    protected List<Object> getModules() {
        return Arrays.asList(new AndroidModule(this), new ActivityModule(), new ServiceModule(),
                new ParseCloudModule());
    }

    public void inject(final Object object) {
        if (graph == null) {
            injectList.add(object);
        } else {
            graph.inject(object);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Parse.initialize(this, "64LrLB3jNDJQq8sSApLLHUWbjv2wmiACyemSLfN3",
        // "u1rLwcDJzwZuNYa06M0ODtsMqmNbYU9MPKwnjG3E");

        mPlusClient = new GoogleApiClient.Builder(this).addApi(Plus.API, null)
                .addScope(Plus.SCOPE_PLUS_LOGIN).addScope(Plus.SCOPE_PLUS_PROFILE).build();
        mPlusClient.connect();

        graph = ObjectGraph.create(getModules().toArray());
        for (final Object obj : injectList) {
            graph.inject(obj);
        }
        injectList.clear();
    }
}
