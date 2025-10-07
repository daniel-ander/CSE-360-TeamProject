package Exceptions;

public class QuestionDBExceptions {
	
	public static class DuplicateQuestionIDException extends RuntimeException {
		public DuplicateQuestionIDException(String message) { super(message); }
	}
	
}