package Exceptions;

public class DBExceptions {
	
	public static class DataAccessException extends RuntimeException {
		public DataAccessException(String message) { super(message); }
	}
	
	public static class DuplicateRecordIDException extends RuntimeException {
		public DuplicateRecordIDException(String message) { super(message); }
	}
	
}