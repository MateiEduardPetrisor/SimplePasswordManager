package controllers;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import database.ApplicationError;
import database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginCtrl {
	private final GUILoader gui = GUILoader.getInstance();
	private final Database db = Database.getInstance();

	@FXML
	private AnchorPane idAnchorPane;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private PasswordField idPasswordField;

	@FXML
	private Label idLabelPassword;

	@FXML
	private Button idButtonLogin;

	@FXML
	private Button idButtonExit;

	@FXML
	private Button idButtonSetup;

	@FXML
	void exitButtonClick(ActionEvent event) {
		this.gui.closeWindow(event);
	}

	@FXML
	void loginButtonClick(ActionEvent event)
			throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeyException,
			InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String password = this.idPasswordField.getText();
		ApplicationError code = this.db.authenticateUser(password);
		if (this.gui.errorHandler(code)) {
			this.gui.closeWindow(event);
			Stage credentialsList = this.gui.loadCredentials();
			credentialsList.show();
		}
	}

	@FXML
	void setupClick(ActionEvent event) throws IOException {
		this.gui.closeWindow(event);
		Stage configuration = this.gui.loadConfiguration();
		configuration.show();
	}

	@FXML
	void initialize() {
		assert idAnchorPane != null : "fx:id=\"idScene\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert idPasswordField != null
				: "fx:id=\"idPasswordField\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert idLabelPassword != null
				: "fx:id=\"idLabelPassword\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert idButtonLogin != null
				: "fx:id=\"idButtonLogin\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert idButtonExit != null : "fx:id=\"idButtonExit\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert idButtonSetup != null
				: "fx:id=\"idButtonSetup\" was not injected: check your FXML file 'MainForm.fxml'.";
	}
}