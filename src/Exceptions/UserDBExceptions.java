package Exceptions;

public class UserDBExceptions {
	
	public static class DuplicateUserException extends RuntimeException {
		public DuplicateUserException(String message) { super(message); }
	}
	
}