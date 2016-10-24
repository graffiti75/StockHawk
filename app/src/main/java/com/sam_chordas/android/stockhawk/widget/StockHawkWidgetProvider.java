package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.StocksActivity;

/**
 * StockHawkWidgetProvider.java.
 *
 * @author Rodrigo Cericatto
 * @since Oct 24, 2016
 */
public class StockHawkWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int total = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider.
        for (int i = 0; i < total; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch SplashScreenActivity.
            Intent intent = new Intent(context, StocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an click listener to the button.
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stock_hawk);
            views.setOnClickPendingIntent(R.id.id_widget_button, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget.
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}