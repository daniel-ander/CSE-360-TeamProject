package Utils;

public class GeneratePostId {
	private static int currentId = 0;

	public static synchronized int getNextId() {
		return currentId++;
	}
}
