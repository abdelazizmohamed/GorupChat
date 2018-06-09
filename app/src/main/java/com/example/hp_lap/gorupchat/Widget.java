package com.example.hp_lap.gorupchat;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by hp-lap on 2/26/2018.
 */
public class Widget extends AppWidgetProvider {

    RemoteViews views;
    RemoteViews imageView;
    private static final String ACTION_BROADCASTWIIDGET = "ACTION_BROADCASTWIIDGET";

    private String getInformation(){
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.orderByChild("email").equalTo("Abdel-aziz.m@outlook.com")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            MessageModel message = dataSnapshot.getChildren().iterator().next().getValue(MessageModel.class);
                            views.setTextViewText(R.id.admin,message.getName()+'\n'+message.getName());
                        }catch (Exception e){}
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
        return "";
    }


    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        views = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);



        //imageView = new RemoteViews(context.getPackageName(), R.layout.widget);
       // new LoadBitmap(imageView).execute("http://findicons.com/files/icons/2101/ciceronian/59/photos.png");
        //imageView.setOnClickPendingIntent(R.id.widget_view,pendingIntent);

        new LoadBitmap(views).execute("Group Chat");
        views.setOnClickPendingIntent(R.id.widget_title,pendingIntent);

        Intent secondIntent = new Intent(context,Widget.class);
        secondIntent.setAction(ACTION_BROADCASTWIIDGET);

        context.sendBroadcast(secondIntent);
        appWidgetManager.updateAppWidget(appWidgetId,views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {super.onEnabled(context);}

    @Override
    public void onDisabled(Context context) {super.onDisabled(context);}

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_BROADCASTWIIDGET.equals(intent.getAction())) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setTextViewText(R.id.information, getInformation());
            ComponentName componentName = new ComponentName(context, Widget.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(componentName, views);
        }
    }
    public class LoadBitmap extends AsyncTask<String,Void,Bitmap> {
        private RemoteViews views;
        private String url = "http://findicons.com/files/icons/2101/ciceronian/59/photos.png";

        LoadBitmap(RemoteViews views){
            this.views = views;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                InputStream inputStream = new java.net.URL(url).openStream();
                Bitmap bitmap=BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


    }

}

