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
    Timestamp utcTimestamp;

    @BeforeEach
    void setUp() {

        localDateTime = LocalDateTime.now();// get the local system time now

        // convert to a utc zoned datetime
        ZonedDateTime localZonedDateTime = localDateTime.atZone(local);// attach local time zone
        ZonedDateTime utcZonedDateTime = localZonedDateTime.withZoneSameInstant(utc);// switch to UTC zone

        // get the timestamp of the utc zoned datetime
        utcTimestamp = Timestamp.valueOf(utcZonedDateTime.toLocalDateTime());
    }

    @Test
    void utcToLocal_UtcTimestampGiven_ShouldReturnSystemLocalDateTime() {

        // test whether the system local datetime, matches the utc timestamp
        assertEquals(localDateTime, TimeChanger.utcToLocal(utcTimestamp));
    }

    @Test
    void utcToLocal_UtcTimestampGiven_ShouldReturnIncorrectSystemLocalDateTime(){
        assertNotEquals(localDateTime.plusHours(8), TimeChanger.utcToLocal(utcTimestamp));
    }

    @Test
    void localToUtc_SystemLocalDateTimeGiven_ShouldReturnUtcTimestamp() {

        // test whether the utc timestamp, matches the system local datetime
        assertEquals(utcTimestamp, TimeChanger.localToUtc(localDateTime));
    }

    @Test
    void localToUtc_SystemLocalDateTimeGiven_ShouldReturnIncorrectUtcTimestamp() {

        // test whether the utc timestamp, matches the system local datetime
        assertNotEquals(utcTimestamp, TimeChanger.localToUtc(localDateTime.plusHours(8)));
    }
}