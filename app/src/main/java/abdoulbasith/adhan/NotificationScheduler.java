package abdoulbasith.adhan;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationScheduler {

    //TODO : Code unique for each reminder

    public static void setReminder(Context context, Class<?> alarmReceiver, Calendar calendar, String type, int jobId) {

        // cancel already scheduled reminders
        cancelReminder(context, alarmReceiver, jobId);


        // Enable a receiver

        ComponentName receiver = new ComponentName(context, alarmReceiver);
        PackageManager packageManager = context.getPackageManager();

        packageManager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        Intent alarmIntent = new Intent(context, alarmReceiver);
        alarmIntent.putExtra("type", type);
        alarmIntent.putExtra("requestCode", jobId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, jobId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Log.d(AdhanJobService.class.getName(), "setReminder for "+type);
    }

    private static void cancelReminder(Context context, Class<?> alarmReceiver, int jobId) {
        Log.d(AdhanJobService.class.getName(), "cancel previous Reminder");

        // Disable a receiver

        ComponentName receiver = new ComponentName(context, alarmReceiver);
        PackageManager packageManager = context.getPackageManager();

        packageManager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent alarmIntent = new Intent(context, alarmReceiver);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, jobId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void showNotification(Context context, Class<?> mainActivity, String prayerType, int jobId) {

        Log.d(AdhanJobService.class.getName(), "showNotification");

        String channelId = context.getString(R.string.channel_id);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(context.getString(R.string.notification_big_title) + prayerType);
        bigTextStyle.bigText(Utils.getHijriDate(context));

        Intent notificationIntent = new Intent(context, mainActivity);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(mainActivity);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(jobId, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(context.getString(R.string.channel_id))
                .setContentIntent(pendingIntent)
                .setStyle(bigTextStyle);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Utils.NOTIFICATION_ID, notificationBuilder.build());
    }
}
