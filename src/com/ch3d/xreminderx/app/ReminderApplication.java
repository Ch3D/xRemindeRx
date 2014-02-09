
package com.ch3d.xreminderx.app;

import java.util.Arrays;
import java.util.List;

import android.app.Application;

import com.ch3d.xreminderx.module.AndroidModule;
import com.ch3d.xreminderx.module.ActivityModule;

import dagger.ObjectGraph;

public class ReminderApplication extends Application {
    private ObjectGraph graph;

    protected List<Object> getModules() {
        return Arrays.asList(new AndroidModule(this), new ActivityModule());
    }

    public void inject(Object object) {
        graph.inject(object);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        graph = ObjectGraph.create(getModules().toArray());
    }
}
