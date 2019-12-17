package abdoulbasith.adhan;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.icu.util.IslamicCalendar;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.azan.types.PrayersType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView txt_fajr, txt_lever, txt_dhuhr, txt_asr, txt_maghrib, txt_isha, txt_date_hijri, txt_date, txt_lieu_methode, txt_version;
    private Button btn_app, btn_blog, btn_mail;
    private HashMap<PrayersType, Date> salahMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_date_hijri = findViewById(R.id.txt_hijri_date_du_jour);
        txt_date = findViewById(R.id.txt_date_du_jour);
        txt_fajr = findViewById(R.id.txt_fajr_heure);
        txt_lever = findViewById(R.id.txt_lever_heure);
        txt_dhuhr = findViewById(R.id.txt_dhuhr_heure);
        txt_asr = findViewById(R.id.txt_asr_heure);
        txt_maghrib = findViewById(R.id.txt_maghrib_heure);
        txt_isha = findViewById(R.id.txt_isha_heure);
        txt_lieu_methode = findViewById(R.id.txt_lieu_methode);
        txt_version = findViewById(R.id.txt_version);

        btn_app = findViewById(R.id.btn_android_app);
        btn_blog = findViewById(R.id.btn_blog);
        btn_mail = findViewById(R.id.btn_mail);

        txt_version.append(BuildConfig.VERSION_NAME);


        getHijriDate();

        getSalahTimes();

        scheduleJob();

        initListeners();
    }


    public void getHijriDate(){


        /*
        // Alternative : not in use, to remove
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            HijrahDate hijrahDate = HijrahChronology.INSTANCE.date(
                    LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE)));

            Log.d("MainActivity", hijrahDate.getChronology().toString());
            Log.d("MainActivity", hijrahDate.getEra().toString());
            Log.d("MainActivity", hijrahDate.toString());
        }*/


        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            Calendar calendar = Calendar.getInstance();
            IslamicCalendar islamicCalendar = new IslamicCalendar(calendar.getTime());

            islamicCalendar.setCalculationType(IslamicCalendar.CalculationType.ISLAMIC_UMALQURA);

            Log.d("MainActivity", islamicCalendar.getCalculationType().name());
            Log.d("MainActivity", "YEAR "+islamicCalendar.get(android.icu.util.Calendar.YEAR));
            Log.d("MainActivity", "MONTH "+ islamicCalendar.get(android.icu.util.Calendar.MONTH)); // à Incrementer
            Log.d("MainActivity", "DAY "+islamicCalendar.get(android.icu.util.Calendar.DAY_OF_MONTH));

            int month = islamicCalendar.get(android.icu.util.Calendar.MONTH);
            String monthHijri = "";

            switch (month){
                case IslamicCalendar.MUHARRAM:
                    monthHijri = getString(R.string.txt_muharram);
                    break;
                case IslamicCalendar.SAFAR:
                    monthHijri = getString(R.string.txt_safar);
                    break;
                case IslamicCalendar.RABI_1:
                    monthHijri = getString(R.string.txt_rabi_1);
                    break;
                case IslamicCalendar.RABI_2:
                    monthHijri = getString(R.string.txt_rabi_2);
                    break;
                case IslamicCalendar.JUMADA_1:
                    monthHijri = getString(R.string.txt_jumada_1);
                    break;
                case IslamicCalendar.JUMADA_2:
                    monthHijri = getString(R.string.txt_jumada_2);
                    break;
                case IslamicCalendar.RAJAB:
                    monthHijri = getString(R.string.txt_rajab);
                    break;
                case IslamicCalendar.SHABAN:
                    monthHijri = getString(R.string.txt_shaban);
                    break;
                case IslamicCalendar.RAMADAN:
                    monthHijri = getString(R.string.txt_ramadan);
                    break;
                case IslamicCalendar.SHAWWAL:
                    monthHijri = getString(R.string.txt_shawwal);
                    break;
                case IslamicCalendar.DHU_AL_QIDAH:
                    monthHijri = getString(R.string.txt_dhul_qadah);
                    break;
                case IslamicCalendar.DHU_AL_HIJJAH:
                    monthHijri = getString(R.string.txt_dhul_hijjah);
                    break;
            }

            String hijriDate = islamicCalendar.get(android.icu.util.Calendar.DAY_OF_MONTH) + " " + monthHijri + " " +islamicCalendar.get(android.icu.util.Calendar.YEAR);

            txt_date_hijri.setText(hijriDate);

        } else
            txt_date_hijri.setVisibility(View.GONE);*/

        txt_date_hijri.setText(Utils.getHijriDate(this));

    }

    public void getSalahTimes() {

        txt_date.setText(getFormattedDate(new Date()));

        txt_lieu_methode.setText(getString(R.string.txt_ville_methode));

        salahMap = Utils.getSalahTimes();

        txt_fajr.setText(getFormattedTime(salahMap.get(PrayersType.FAJR)));
        txt_lever.setText(getFormattedTime(salahMap.get(PrayersType.SUNRISE)));
        txt_dhuhr.setText(getFormattedTime(salahMap.get(PrayersType.ZUHR)));
        txt_asr.setText(getFormattedTime(salahMap.get(PrayersType.ASR)));
        txt_maghrib.setText(getFormattedTime(salahMap.get(PrayersType.MAGHRIB)));
        txt_isha.append(getFormattedTime(salahMap.get(PrayersType.ISHA)));

    }


    public void scheduleJob(){

        ComponentName componentName = new ComponentName(this, AdhanJobService.class);

        JobInfo jobInfo = new JobInfo.Builder(Utils.JOB_ID, componentName)
                .setPeriodic(Utils.JOB_PERIOD)
                //.setPeriodic(900000) // Minimum : 15 min
                .setPersisted(true)
                //.setExtras(bundle)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo);

        if (resultCode == JobScheduler.RESULT_SUCCESS)
            Log.d(AdhanJobService.class.getName(), "Job scheduled !");
        else
            Log.d(AdhanJobService.class.getName(), "Job not scheduled : " + resultCode);


        //TEST
        //MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.cricket);
        //mediaPlayer.start();

    }

    public void initListeners(){

        btn_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=abdulbasith.authenticislam")));
            }
        });

        btn_blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent blogIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://authenticislam.org"));
                startActivity(blogIntent);
            }
        });

        btn_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:authenticislamicarchives@gmail.com"));
                startActivity(emailIntent);
            }
        });
    }

    private String getFormattedDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.FRANCE);
        return dateFormat.format(date);
    }

    private String getFormattedTime(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.FRANCE);
        return dateFormat.format(date);
    }

}
