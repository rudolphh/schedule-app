package utils;

// org.junit.jupiter:junit-jupiter:5.4.2
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeChangerTest {

    // get zone ids
    ZoneId utc = ZoneId.of("UTC");
    ZoneId local = ZoneId.systemDefault();// system time zone

    LocalDateTime localDateTime;
    Timestamp currentUtcTimestamp;

    @BeforeEach
    void setUp() {

        // create a LocalDateTime and a Timestamp of that local time expressed in UTC

        localDateTime = LocalDateTime.now();// get the local system time now

        // convert to a utc zoned datetime
        ZonedDateTime localZonedDateTime = localDateTime.atZone(local);// attach local time zone
        ZonedDateTime utcZonedDateTime = localZonedDateTime.withZoneSameInstant(utc);// switch to UTC zone

        // get the timestamp of the utc zoned datetime
        currentUtcTimestamp = Timestamp.valueOf(utcZonedDateTime.toLocalDateTime());
    }

    @Test
    void utcToLocal_UtcTimestampGiven_ShouldReturnSystemLocalDateTime() {

        // test whether the system current local datetime matches that current time as a UTC timestamp
        assertEquals(localDateTime, TimeChanger.utcToLocal(currentUtcTimestamp));
    }

    @Test
    void utcToLocal_UtcTimestampGiven_ShouldReturnIncorrectSystemLocalDateTime(){

        // tests whether the system current local datetime,
        // does NOT match the current time as a UTC timestamp plus 3 hours

        long duration = 3 * 60 * 60 * 1000; // 3 hours * 60 minutes/hr * 60 seconds/min * 1000 ms/second
        Timestamp currentUtcTimestampPlus3Hours =  new Timestamp(currentUtcTimestamp.getTime() + duration);
        assertNotEquals(localDateTime, TimeChanger.utcToLocal(currentUtcTimestampPlus3Hours));
    }

    @Test
    void localToUtc_SystemLocalDateTimeGiven_ShouldReturnUtcTimestamp() {

        // test whether the current time as a UTC timestamp, matches the system current local datetime
        assertEquals(currentUtcTimestamp, TimeChanger.localToUtc(localDateTime));
    }

    @Test
    void localToUtc_SystemLocalDateTimeGiven_ShouldReturnIncorrectUtcTimestamp() {

        // test whether the current time as a utc timestamp,
        // does NOT match the system current local datetime plus 3 hours
        assertNotEquals(currentUtcTimestamp, TimeChanger.localToUtc(localDateTime.plusHours(3)));
    }
}