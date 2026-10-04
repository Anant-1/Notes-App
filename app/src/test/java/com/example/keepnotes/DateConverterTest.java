package com.example.keepnotes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.keepnotes.data.database.DateConverter;

import org.junit.Test;

import java.util.Date;

public class DateConverterTest {

    @Test
    public void testDateToTimestampAndBack() {
        long nowMillis = 1600000000000L;
        Date date = new Date(nowMillis);

        Long timestamp = DateConverter.toTimeStamp(date);
        assertEquals(Long.valueOf(nowMillis), timestamp);

        Date convertedDate = DateConverter.toDate(timestamp);
        assertEquals(date, convertedDate);
    }

    @Test
    public void testNullHandling() {
        assertNull(DateConverter.toDate(null));
        assertNull(DateConverter.toTimeStamp(null));
    }
}
