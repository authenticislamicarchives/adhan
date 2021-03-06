package abdoulbasith.adhan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.azan.types.PrayersType;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdhanJobService extends JobService {

    private static final String TAG = AdhanJobService.class.getSimpleName();
    boolean isWorking = false;
    boolean jobCancelled = false;
    private HashMap<PrayersType, Date> salahMap = new HashMap<>();

    //Appelé quand c'est l'heure de lancer le Job
    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Log.d(TAG, "onStartJob");

        isWorking = true;

        startWorkOnNewThread(jobParameters);

        return isWorking;
    }

    //Appelé si le job est annulé avant d'avoir fini le travail
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob");

        jobCancelled = true;
        boolean needReschedule = isWorking;

        jobFinished(jobParameters, needReschedule);

        return needReschedule;
    }

    private void startWorkOnNewThread(final JobParameters jobParameters){
        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork(jobParameters);
            }
        }).start();
    }

    private void doWork(JobParameters jobParameters){

        Log.d(TAG, "doWork");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.shared_preference), MODE_PRIVATE);

        salahMap = Utils.getSalahTimes(sharedPreferences.getInt(getString(R.string.preference_city), 0));

        createNotificationChannel();


        for(Map.Entry<PrayersType, Date> entry : salahMap.entrySet()) {

            Log.d(AdhanJobService.TAG, entry.getKey().name());
            Log.d(AdhanJobService.TAG, entry.getValue().toString());

            if(entry.getKey() != PrayersType.SUNRISE) {

                // If salah time is not before now (after current date)
                if(!entry.getValue().before(new Date())) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(entry.getValue());
                    calendar.set(Calendar.SECOND, 0); // On met 0 secondes

                    NotificationScheduler.setReminder(this, AlarmReceiver.class, calendar, entry.getKey().name(), entry.getKey().getIndex());

                    Log.d(AdhanJobService.TAG, "calendar : " + calendar.getTime());
                }
            }
        }


        Log.d(TAG, "Job finished!");
        isWorking = false;
        boolean needsReschedule = false;
        jobFinished(jobParameters, needsReschedule);

    }



    public void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Créer le canal pour montrer les notifications.
            String channelId  = getString(R.string.channel_id);
            String channelName = getString(R.string.channel_name);

            Uri notificationSoundUri = Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/raw/cricket");

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();


            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(notificationSoundUri, audioAttributes);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);


            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
