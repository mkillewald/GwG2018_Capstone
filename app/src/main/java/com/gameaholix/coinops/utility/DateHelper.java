package com.gameaholix.coinops.utility;

import android.content.Context;
import android.util.Log;

import com.gameaholix.coinops.R;

import java.text.DateFormat;
import java.util.Date;

public class DateHelper {
    private static final String TAG = DateHelper.class.getSimpleName();

    public static String getDateTime(Context context, long timeStamp){
        try{
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(timeStamp));
        }
        catch(Exception e){
            Log.e(TAG, "Exception: ", e);
            return context.getString(R.string.not_available);
        }
    }

    public static String getDate(Context context, long timeStamp){
        try{
            return DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(timeStamp));
        }
        catch(Exception e){
            Log.e(TAG, "Exception: ", e);
            return context.getString(R.string.not_available);
        }
    }

    public static String getTime(Context context, long timeStamp){
        try{
            return DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(timeStamp));
        }
        catch(Exception e){
            Log.e(TAG, "Exception: ", e);
            return context.getString(R.string.not_available);
        }
    }
}
