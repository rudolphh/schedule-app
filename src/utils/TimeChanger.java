package utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TimeChanger {

    private static final ZoneId utc = ZoneId.of("UTC");
    private static final ZoneId local = ZoneId.systemDefault();

    public static LocalDateTime fromUTC(Timestamp timestamp){
        // convert to local date time without zone information
        LocalDateTime localDateTime = timestamp.toLocalDateTime();

        // attach UTC time zone information
        ZonedDateTime utcZoned = localDateTime.atZone(utc);

        // change zone to local to get local time offset from UTC
        ZonedDateTime localZoned = utcZoned.withZoneSameInstant(local);

        return localZoned.toLocalDateTime();// return the local time
    }

    public static Timestamp toUTC(LocalDateTime localDateTime){

        ZonedDateTime localZoned = localDateTime.atZone(local);// attach local time zone information
        ZonedDateTime utcZoned = localZoned.withZoneSameInstant(utc);// switch to UTC zone

        return Timestamp.valueOf(utcZoned.toLocalDateTime());// return timestamp in UTC
    }
}
