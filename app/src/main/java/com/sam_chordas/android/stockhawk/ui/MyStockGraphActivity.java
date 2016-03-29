package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

public class MyStockGraphActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    LineChartView mLineChart;
    String mQuote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_line_graph);
        mLineChart = (LineChartView) findViewById(R.id.linechart);

        Bundle data = getIntent().getExtras();
        if (!data.isEmpty()) {
            mQuote = data.getString("quote");
            setTitle(mQuote);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                QuoteProvider.Quotes.CONTENT_URI.buildUpon().appendPath(mQuote).build(),
                null,
                null,
                null,
                QuoteColumns._ID + " ASC"
        );
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            return;
        }

        LineSet dataset = new LineSet();
        float min = 0f;
        float max = 0f;
        data.moveToFirst();
        do {
            int stockPriceColumn = data.getColumnIndex(QuoteColumns.BIDPRICE);
            float stockPrice = data.getFloat(stockPriceColumn);
            dataset.addPoint(new Point("", stockPrice));

            if (stockPrice > max) {
                max = stockPrice;
            }

            if (min == 0f || stockPrice < min) {
                min = stockPrice;
            }

        } while (data.moveToNext());

        dataset.setColor(Color.parseColor("#ffffff"))
                .setFill(Color.parseColor("#7291cc"))
                .setDotsColor(Color.parseColor("#00ffe9"))
                .setThickness(4)
                .setDashed(new float[]{10f,10f});

        mLineChart.dismiss();
        mLineChart.addData(dataset);

        mLineChart.setLabelsColor(Color.WHITE)
                .setAxisColor(Color.WHITE)
                .setAxisBorderValues((int)min-1, (int)max+1, 1);
        mLineChart.show();
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        // Do nothing...
    }

}
