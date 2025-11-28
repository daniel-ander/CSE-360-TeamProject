package Utils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

public class Time {
	 public static String timeAgoShort(Timestamp pastTimestamp) {
	        Instant past = pastTimestamp.toInstant();  // convert Timestamp to Instant
	        Instant now = Instant.now();
	        Duration duration = Duration.between(past, now);
	        long seconds = duration.getSeconds();

	        if (seconds < 60) {
	            return seconds + "s";
	        } else if (seconds < 3600) {
	            long minutes = seconds / 60;
	            return minutes + "m";
	        } else if (seconds < 86400) {
	            long hours = seconds / 3600;
	            return hours + "h";
	        } else {
	            long days = seconds / 86400;
	            return days + "d"; 
	        }
	    }
}