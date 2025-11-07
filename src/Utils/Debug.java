package Utils;

public class Debug {
	public static boolean isEnabled = true;
	//TODO: this needs to also turn off the buttons for easy login and stuff, also maybe a verbose mode 
	
	public static void write(String text) {
		if(isEnabled) {
			System.out.println(text);
		}
	}
}