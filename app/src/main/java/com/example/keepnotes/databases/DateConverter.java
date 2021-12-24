package com.example.keepnotes.databases;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    //Used by room for querying the date
    @TypeConverter
    public static final Date toDate(Long timestamp){
        return timestamp == null ? null : new Date(timestamp);
    }

    //Used by room for writing the date into the database
    @TypeConverter
    public static final Long toTimeStamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
