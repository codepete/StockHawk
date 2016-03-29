package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by peter on 3/29/16.
 */
public class StockHawkWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockHawkWidgetListProvider(this.getApplicationContext(), intent);
    }
}
