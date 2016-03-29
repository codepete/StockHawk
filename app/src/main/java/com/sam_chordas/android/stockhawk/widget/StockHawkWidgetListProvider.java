package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by peter on 3/29/16.
 */
public class StockHawkWidgetListProvider implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;
    private int mAppWidgetId;

    public StockHawkWidgetListProvider(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(
                QuoteProvider.Quotes.CONTENT_URI,
                null,
                QuoteColumns.ISCURRENT + " = ?",
                new String[] {"1"},
                null
        );

    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }

    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() < position) {
            return null;
        }

        mCursor.moveToPosition(position);
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        int symbolIndex = mCursor.getColumnIndex(QuoteColumns.SYMBOL);
        int priceIndex = mCursor.getColumnIndex(QuoteColumns.BIDPRICE);
        int isUpIndex = mCursor.getColumnIndex(QuoteColumns.ISUP);
        remoteView.setTextViewText(R.id.stock_symbol, mCursor.getString(symbolIndex));
        remoteView.setTextViewText(R.id.stock_price, String.valueOf(mCursor.getFloat(priceIndex)));

        boolean isUp = mCursor.getInt(isUpIndex) == 1;
        if (isUp) {
            remoteView.setInt(R.id.stock_price, "setBackgroundColor", Color.RED);
        } else {
            remoteView.setInt(R.id.stock_price, "setBackgroundColor", Color.GREEN);
        }

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (mCursor == null || mCursor.getCount() == 0) {
            return -1;
        }

        mCursor.moveToPosition(position);

        return mCursor.getLong(0);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
