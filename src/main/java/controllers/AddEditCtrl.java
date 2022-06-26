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
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import database.Database;
import encryption.Encryption;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Credential;

public class AddEditCtrl implements Initializable {
	private final GUILoader gui = GUILoader.getInstance();
	private final Encryption api = Encryption.getInstance();
	private final Database db = Database.getInstance();
	private Credential credential;

	public void setCredential(Credential credential) {
		this.credential = credential;
	}

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private AnchorPane idAnchorPane;

	@FXML
	private Label idLabelWebsite;

	@FXML
	private Label idLabelUsername;

	@FXML
	private Label idLavbelPassword;

	@FXML
	private TextField idTextFieldWebsite;

	@FXML
	private TextField idTextFieldUsername;

	@FXML
	private TextField idTextFieldPassword;

	@FXML
	private Label idLabelConfirmPassword;

	@FXML
	private Button idButtonAddPassword;

	@FXML
	private Button idButtonCancel;

	@FXML
	void addButtonClick(ActionEvent event)
			throws IOException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
			SecurityException, RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
		if (this.credential != null) {
			this.editCredential(event);
		} else {
			this.addCredential(event);
		}
	}

	private void editCredential(ActionEvent event) throws InvalidKeyException, InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException, SecurityException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException {
		String website = this.idTextFieldWebsite.getText();
		String username = this.idTextFieldUsername.getText();
		String password = this.idTextFieldPassword.getText();
		if (this.checkInputCompliance(password) && this.checkInputCompliance(username)
				&& this.checkInputCompliance(website)) {
			if (this.gui.displayConfrimation("Save changes ?") == ButtonType.YES) {
				byte[] iv = this.api.generateInitializationVector();
				this.api.setIv(new String(iv));
				String encIv = this.api.encode(new String(iv));
				String encWebsite = this.api.encrypt(website);
				String encUsername = this.api.encrypt(username);
				String encPassword = this.api.encrypt(password);
				this.credential.setWebsiteName(encWebsite);
				this.credential.setUserName(encUsername);
				this.credential.setUserPassword(encPassword);
				this.credential.setIv(encIv);
				this.db.updateCredential(this.credential.getId(), this.credential);
				this.gui.closeWindow(event);
				Stage passwordList = this.gui.loadCredentials();
				passwordList.show();
			} else {
				this.gui.closeWindow(event);
				Stage credentialsList = this.gui.loadCredentials();
				credentialsList.show();
			}
		} else {
			this.gui.displayAlert(AlertType.ERROR, "Blank fields not accepted!");
		}
	}

	private boolean checkInputCompliance(String text) {
		if (text != null && !text.trim().isEmpty()) {
			return true;
		}
		return false;
	}

	private void addCredential(ActionEvent event)
			throws IOException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
			SecurityException, RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
		String website = this.idTextFieldWebsite.getText();
		String username = this.idTextFieldUsername.getText();
		String password = this.idTextFieldPassword.getText();
		if (this.checkInputCompliance(password) && this.checkInputCompliance(username)
				&& this.checkInputCompliance(website)) {
			byte[] iv = this.api.generateInitializationVector();
			this.api.setIv(new String(iv));
			String encIv = this.api.encode(new String(iv));
			String encWebsite = this.api.encrypt(website);
			String encUsername = this.api.encrypt(username);
			String encPassword = this.api.encrypt(password);
			Credential encCredential = new Credential(encWebsite, encUsername, encPassword, encIv);
			this.db.insertCredential(encCredential);
			this.gui.closeWindow(event);
			Stage credentialsList = this.gui.loadCredentials();
			credentialsList.show();
		} else {
			this.gui.displayAlert(AlertType.ERROR, "Blank fields not accepted!");
		}
	}

	@FXML
	void cancelButtonClick(ActionEvent event) throws IOException {
		this.gui.closeWindow(event);
		Stage credentialList = this.gui.loadCredentials();
		credentialList.show();
	}

	@FXML
	void initialize() {
		assert idAnchorPane != null
				: "fx:id=\"idAnchorPane\" was not injected: check your FXML file 'PasswordForm.fxml'.";
		assert idLabelWebsite != null
				: "fx:id=\"idLabelWebsite\" was not injected: check your FXML file 'PasswordForm.fxml'.";
		assert idLabelUsername != null
				: "fx:id=\"idLabelUsername\" was not injected: check your FXML file 'PasswordForm.fxml'.";
		assert idLavbelPassword != null
				: "fx:id=\"idLavbelPassword\" was not injected: check your FXML file 'PasswordForm.fxml'.";
		assert idTextFieldWebsite != null
				: "fx:id=\"idTextFieldWebsite\" was not injected: check your FXML file 'PasswordForm.fxml'.";
		assert idTextFieldUsername != null
				: "fx:id=\"idTextFieldUsername\" was not injected: check your FXML file 'PasswordForm.fxml'.";
		assert idTextFieldPassword != null
				: "fx:id=\"idPasswordFieldPassword\" was not injected: check your FXML file 'PasswordForm.fxml'.";
		assert idLabelConfirmPassword != null
				: "fx:id=\"idLabelConfirmPassword\" was not injected: check your FXML file 'PasswordForm.fxml'.";
		assert idButtonAddPassword != null
				: "fx:id=\"idButtonAddPassword\" was not injected: check your FXML file 'PasswordForm.fxml'.";
		assert idButtonCancel != null
				: "fx:id=\"idButtonCancel\" was not injected: check your FXML file 'PasswordForm.fxml'.";
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (this.credential != null) {
			try {
				this.idButtonAddPassword.setText("OK");
				this.api.setIv(this.api.decode(credential.getIv()));
				this.idTextFieldUsername.setText(api.decrypt(this.credential.getUserName()));
				this.idTextFieldWebsite.setText(api.decrypt(this.credential.getWebsiteName()));
				this.idTextFieldPassword.setText(api.decrypt(this.credential.getUserPassword()));
			} catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException
					| NoSuchProviderException | NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException e) {
				e.printStackTrace();
			}
		}
	}
}