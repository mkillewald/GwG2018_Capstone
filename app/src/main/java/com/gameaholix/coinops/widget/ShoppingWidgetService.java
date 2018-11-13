package com.gameaholix.coinops.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ShoppingWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ShoppingListProvider(this.getApplicationContext(), intent));
    }
}
