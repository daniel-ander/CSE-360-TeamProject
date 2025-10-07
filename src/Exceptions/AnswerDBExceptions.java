package Exceptions;

public class AnswerDBExceptions {
	
	public static class DuplicateAnswerIDException extends RuntimeException {
		public DuplicateAnswerIDException(String message) { super(message); }
	}
	
}