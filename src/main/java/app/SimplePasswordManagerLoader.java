package app;

public class SimplePasswordManagerLoader {
	public static void main(String[] args) {
		try {
			SimplePasswordManager.run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
} // passwordpassword