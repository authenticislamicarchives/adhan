package abdoulbasith.adhan;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.IslamicCalendar;
import android.util.Log;

import com.azan.PrayerTimes;
import com.azan.TimeCalculator;
import com.azan.types.AngleCalculationType;
import com.azan.types.PrayersType;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static double latitudeVilliers = 48.827340;
    public static double longitudeVillers = 2.542390;

    public static double latitudeMakkah = 21.422990;
    public static double longitudeMakkah = 39.825736;

    public static double latitudeMadinah = 24.467462;
    public static double longitudeMadinah = 39.611070;

    public static double latitudeKaraikal = 10.918813;
    public static double longitudeKaraikal = 79.827070;


    public static int JOB_ID = 5;
    public static int NOTIFICATION_ID = 5;
    public static int JOB_PERIOD = 2 * 60 * 60 * 1000;  // 3 hours

    private SharedPreferences sharedPreferences;

    public static HashMap<PrayersType, Date> getSalahTimes(int selectedCity) {

        double latitude = 0, longitude = 0;
        //double timeZone = 0;
        TimeZone timeZone = null;


        switch (selectedCity){
            case 0:
                latitude = latitudeVilliers;
                longitude = longitudeVillers;
                timeZone = TimeZone.getTimeZone("GMT+00:00");
                break;
            case 1:
                latitude = latitudeMakkah;
                longitude = longitudeMakkah;

                timeZone = TimeZone.getTimeZone("GMT+2:00");
                break;
            case 2:
                latitude = latitudeMadinah;
                longitude = longitudeMadinah;
                timeZone = TimeZone.getTimeZone("GMT+2:00");
                break;
            case 3:
                latitude = latitudeKaraikal;
                longitude = longitudeKaraikal;

                timeZone = TimeZone.getTimeZone("GMT+5:30");
            default:
                break;
        }


        GregorianCalendar date = new GregorianCalendar(timeZone);

        Log.d("MainActivity", "TimeZone name :"+timeZone.getDisplayName());
        Log.d("MainActivity", "TimeZone offset :"+TimeUnit.MILLISECONDS.toHours(timeZone.getRawOffset()));


        PrayerTimes prayerTimes = new TimeCalculator().date(date).location(latitude, longitude,
                0, TimeUnit.MILLISECONDS.toHours(timeZone.getRawOffset())).timeCalculationMethod(AngleCalculationType.UMM_AL_QURA).umElQuraRamadanAdjustment(false).calculateTimes();
        prayerTimes.setUseSecond(true);


        HashMap<PrayersType, Date> salahMap = new HashMap<>();

        salahMap.put(PrayersType.FAJR, prayerTimes.getPrayTime(PrayersType.FAJR));
        salahMap.put(PrayersType.SUNRISE, prayerTimes.getPrayTime(PrayersType.SUNRISE));
        salahMap.put(PrayersType.ZUHR, prayerTimes.getPrayTime(PrayersType.ZUHR));
        salahMap.put(PrayersType.ASR, prayerTimes.getPrayTime(PrayersType.ASR));
        salahMap.put(PrayersType.MAGHRIB, prayerTimes.getPrayTime(PrayersType.MAGHRIB));
        salahMap.put(PrayersType.ISHA, prayerTimes.getPrayTime(PrayersType.ISHA));

        Log.d("MainActivity", "salahMap : "+salahMap.toString());

        return salahMap;
    }

    public static String getHijriDate(Context context) {

        String hijriDate = "";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            Calendar calendar = Calendar.getInstance();
            IslamicCalendar islamicCalendar = new IslamicCalendar(calendar.getTime());

            islamicCalendar.setCalculationType(IslamicCalendar.CalculationType.ISLAMIC_UMALQURA);

            Log.d("MainActivity", islamicCalendar.getCalculationType().name());
            Log.d("MainActivity", "YEAR " + islamicCalendar.get(android.icu.util.Calendar.YEAR));
            Log.d("MainActivity", "MONTH " + islamicCalendar.get(android.icu.util.Calendar.MONTH)); // à Incrementer
            Log.d("MainActivity", "DAY " + islamicCalendar.get(android.icu.util.Calendar.DAY_OF_MONTH));

            int month = islamicCalendar.get(android.icu.util.Calendar.MONTH);
            String monthHijri = "";

            switch (month) {
                case IslamicCalendar.MUHARRAM:
                    monthHijri = context.getString(R.string.txt_muharram);
                    break;
                case IslamicCalendar.SAFAR:
                    monthHijri = context.getString(R.string.txt_safar);
                    break;
                case IslamicCalendar.RABI_1:
                    monthHijri = context.getString(R.string.txt_rabi_1);
                    break;
                case IslamicCalendar.RABI_2:
                    monthHijri = context.getString(R.string.txt_rabi_2);
                    break;
                case IslamicCalendar.JUMADA_1:
                    monthHijri = context.getString(R.string.txt_jumada_1);
                    break;
                case IslamicCalendar.JUMADA_2:
                    monthHijri = context.getString(R.string.txt_jumada_2);
                    break;
                case IslamicCalendar.RAJAB:
                    monthHijri = context.getString(R.string.txt_rajab);
                    break;
                case IslamicCalendar.SHABAN:
                    monthHijri = context.getString(R.string.txt_shaban);
                    break;
                case IslamicCalendar.RAMADAN:
                    monthHijri = context.getString(R.string.txt_ramadan);
                    break;
                case IslamicCalendar.SHAWWAL:
                    monthHijri = context.getString(R.string.txt_shawwal);
                    break;
                case IslamicCalendar.DHU_AL_QIDAH:
                    monthHijri = context.getString(R.string.txt_dhul_qadah);
                    break;
                case IslamicCalendar.DHU_AL_HIJJAH:
                    monthHijri = context.getString(R.string.txt_dhul_hijjah);
                    break;
            }

            hijriDate = islamicCalendar.get(android.icu.util.Calendar.DAY_OF_MONTH) + " " + monthHijri + " " + islamicCalendar.get(android.icu.util.Calendar.YEAR);

        }

        return hijriDate;
    }
}
