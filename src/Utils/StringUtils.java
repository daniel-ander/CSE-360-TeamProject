package Utils;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
	public static String listToSQLString(List<String> list) {
		return list.stream()
				.map(p -> "'" + p + "'") // wrap each element in single quotes
				.collect(Collectors.joining(", "));
	}
}