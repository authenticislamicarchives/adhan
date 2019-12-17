package abdoulbasith.adhan;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.Context.JOB_SCHEDULER_SERVICE;


public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

                //TODO
                // On reboot, reschedule the job without launching app

                ComponentName componentName = new ComponentName(context, AdhanJobService.class);

                JobInfo jobInfo = new JobInfo.Builder(Utils.JOB_ID, componentName)
                        .setPeriodic(Utils.JOB_PERIOD)
                        //.setPeriodic(900000) // Minimum : 15 min
                        .setPersisted(true)
                        //.setExtras(bundle)
                        .build();

                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
                int resultCode = jobScheduler.schedule(jobInfo);

                if (resultCode == JobScheduler.RESULT_SUCCESS)
                    Log.d(AdhanJobService.class.getName(), "Job scheduled !");
                else
                    Log.d(AdhanJobService.class.getName(), "Job not scheduled : " + resultCode);

            }

        }


        Log.d(AdhanJobService.class.getName(), "AlarmReceiver");

        String prayerType = intent.getStringExtra("type");
        int requestCode = intent.getIntExtra("requestCode",0);

        Log.d(AdhanJobService.class.getName(), "extras type : "+prayerType);


        NotificationScheduler.showNotification(context, MainActivity.class, prayerType, requestCode);

    }


}
