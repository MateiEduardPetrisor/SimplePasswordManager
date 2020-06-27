package app;

import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import controllers.GUILoader;
import encryption.Encryption;
import javafx.application.Application;
import javafx.stage.Stage;

public class SimplePasswordManager extends Application {
	private GUILoader gui = GUILoader.getInstance();

	static {
		Security.addProvider(new BouncyCastleProvider());
		Provider secProv = Security.getProvider(Encryption.SECURITY_PROVIDER);
		if (secProv == null) {
			System.exit(-1);
		}
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Stage mainWindow = this.gui.loadLogin();
		mainWindow.show();
	}
}