package Utils;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
	public static String listToSQLString(List<String> list) {
		// Returns { "Element1", "Element2"} as "'Element1', 'Element2'" for convenient SQL insertion
		return list.stream()
				.map(p -> "'" + p + "'") // wrap each element in single quotes
				.collect(Collectors.joining(", "));
	}
	
	public static String truncate(String text, int limit) {
		return text.length() <= limit ? text : text.substring(0, limit) + "...";
	}
}