package Utils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class Time {
	/**
	 * Returns the user-friendly social-media like time stamps in
	 * terms of passed time in seconds, minutes, hours.. up to years
	 */
	public static String timeAgoShort(Timestamp pastTimestamp) {
	    Instant past = pastTimestamp.toInstant();
	    Instant nowInstant = Instant.now();
	    Duration duration = Duration.between(past, nowInstant);
	    long seconds = duration.getSeconds();


	    if (seconds < 60) {
	        return seconds + "s";
	    } else if (seconds < 3600) {
	        long minutes = seconds / 60;
	        return minutes + "m";
	    } else if (seconds < 86400) {
	        long hours = seconds / 3600;
	        return hours + "h";
	    } else if (seconds < 2629746) { 
	        // As high up as a month will go
	    	// TODO: - Make more precise
	        long days = seconds / 86400;
	        return days + "d";
	    }

	    // For months and years, convert to LocalDate (more accurate)
	    LocalDate pastDate = past.atZone(ZoneId.systemDefault()).toLocalDate();
	    LocalDate nowDate = nowInstant.atZone(ZoneId.systemDefault()).toLocalDate();

	    Period period = Period.between(pastDate, nowDate);

	    if (period.getYears() > 0) {
	        return period.getYears() + "y";
	    } else if (period.getMonths() > 0) {
	        return period.getMonths() + "mo";
	    } else {
	        // fallback for slight mismatches
	        long days = seconds / 86400;
	        return days + "d";
	    }
	}

}