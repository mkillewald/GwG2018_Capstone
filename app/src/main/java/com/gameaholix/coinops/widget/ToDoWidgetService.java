package com.gameaholix.coinops.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ToDoWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ToDoListProvider(this.getApplicationContext(), intent));
    }
}
