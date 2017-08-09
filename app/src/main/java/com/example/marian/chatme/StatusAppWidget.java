package com.example.marian.chatme;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.example.marian.chatme.activities.MainActivity;

public class StatusAppWidget extends AppWidgetProvider {
    int tickets;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.status_widget);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_header, pendingIntent);

            Intent ticketPurchaseIntent = new Intent(context, MainActivity.class);
            PendingIntent ticketPurchasePendingIntent = PendingIntent.getActivity(context, 0, ticketPurchaseIntent, 0);
            views.setOnClickPendingIntent(R.id.buyTicket_button, ticketPurchasePendingIntent);


            SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getResources().getString(R.string.ticket_pref_key), Context.MODE_PRIVATE);
            String tickets = sharedPref.getString(context.getString(R.string.tickets_number), "hi");
            views.setTextViewText(R.id.tickets_number_textview, String.valueOf(tickets));


            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }


            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {

    }

    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {

    }
}
