package com.gameaholix.coinops.utility;

import android.content.Context;

import com.gameaholix.coinops.R;

import java.text.DateFormat;
import java.util.Date;

public class DateHelper {

    public static String getDateTime(Context context, long timeStamp){
        try{
            return DateFormat.getDateTimeInstance().format(new Date(timeStamp));
        }
        catch(Exception e){
            return context.getString(R.string.not_available);
        }
    }

    public static String getDate(Context context, long timeStamp){
        try{
            return DateFormat.getDateInstance().format(new Date(timeStamp));
        }
        catch(Exception e){
            return context.getString(R.string.not_available);
        }
    }

    public static String getTime(Context context, long timeStamp){
        try{
            return DateFormat.getTimeInstance().format(new Date(timeStamp));
        }
        catch(Exception e){
            return context.getString(R.string.not_available);
        }
    }
}
