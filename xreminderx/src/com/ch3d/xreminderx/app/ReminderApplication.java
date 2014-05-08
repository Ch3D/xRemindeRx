
package com.ch3d.xreminderx.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Application;

import com.ch3d.xreminderx.module.ActivityModule;
import com.ch3d.xreminderx.module.AndroidModule;
import com.ch3d.xreminderx.module.ParseCloudModule;
import com.ch3d.xreminderx.module.ServiceModule;
import com.parse.Parse;

import dagger.ObjectGraph;

public class ReminderApplication extends Application {
    private ObjectGraph  graph;
    private List<Object> injectList = new ArrayList<>();

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
        Parse.initialize(this, "64LrLB3jNDJQq8sSApLLHUWbjv2wmiACyemSLfN3",
                "u1rLwcDJzwZuNYa06M0ODtsMqmNbYU9MPKwnjG3E");
        graph = ObjectGraph.create(getModules().toArray());
        for (Object obj : injectList) {
            graph.inject(obj);
        }
        injectList.clear();
    }
}
