package com.meetingscheduler.Utils;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class TimeFunctions {

    public static Timestamp timeUtc() {
        return Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC")));
    }

    public static Timestamp utcTimestampToLocalTimestamp(Timestamp timestamp) {
        ZonedDateTime zonedDateTimeUtc = timestamp.toLocalDateTime().atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedDateTimeLocalTime = zonedDateTimeUtc.withZoneSameInstant(ZoneId.systemDefault());
        return Timestamp.valueOf(zonedDateTimeLocalTime.toLocalDateTime());
    }

    public static Timestamp localTimestamptoUtcTimestamp(Timestamp timestamp) {
        ZonedDateTime zonedDateTimeLocalTime = timestamp.toLocalDateTime().atZone(ZoneId.systemDefault());
        ZonedDateTime zonedDateTimeUtc = zonedDateTimeLocalTime.withZoneSameInstant(ZoneId.of("UTC"));
        return Timestamp.valueOf(zonedDateTimeUtc.toLocalDateTime());
    }

    public static Timestamp stringToTimestamp(String localDate, String hour, String minute) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm");
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.parse((localDate + " " + hour + ":" + minute), dateTimeFormatter));
        return timestamp;
    }
}
