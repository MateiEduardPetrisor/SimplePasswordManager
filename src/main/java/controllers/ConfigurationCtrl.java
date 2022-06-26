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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ConfigurationCtrl implements Initializable {
	private final GUILoader gui = GUILoader.getInstance();
	private final Database db = Database.getInstance();
	private boolean isMaster;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private AnchorPane idAnchorPane;

	@FXML
	private Button idButtonSetPassword;

	@FXML
	private Button idButtonCancel;

	@FXML
	private Label idLabelOldPwd;

	@FXML
	private PasswordField idPassFieldOldPwd;

	@FXML
	private Label idLabelConfirmOldPwd;

	@FXML
	private PasswordField idPassFieldConfirmOldPwd;

	@FXML
	private Label idLabelNewPwd;

	@FXML
	private PasswordField idPassFieldNewPwd;

	@FXML
	private Label idLabelConfirmNewPwd;

	@FXML
	private PasswordField idPassFieldConfirmNewPed;

	@FXML
	void cancelClick(ActionEvent event) throws IOException {
		this.gui.closeWindow(event);
		Stage login = this.gui.loadLogin();
		login.show();
	}

	@FXML
	void setPasswordClick(ActionEvent event)
			throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeyException,
			InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String oldPwd = null;
		String confirmOldPwd = null;
		String newPwd = null;
		String confirmNewPwd = null;
		if (this.isMaster) {
			oldPwd = this.idPassFieldOldPwd.getText();
			confirmOldPwd = this.idPassFieldConfirmOldPwd.getText();
			newPwd = this.idPassFieldNewPwd.getText();
			confirmNewPwd = this.idPassFieldConfirmNewPed.getText();
			ApplicationError code = this.db.migrate(oldPwd, confirmOldPwd, newPwd, confirmNewPwd);
			if (this.gui.errorHandler(code)) {
				this.gui.closeWindow(event);
				Stage login = this.gui.loadLogin();
				login.show();
			}
		} else {
			this.hideGuiFileds();
			newPwd = this.idPassFieldNewPwd.getText();
			confirmNewPwd = this.idPassFieldConfirmNewPed.getText();
			ApplicationError code = this.db.setup(newPwd, confirmNewPwd);
			if (this.gui.errorHandler(code)) {
				this.gui.closeWindow(event);
				Stage login = this.gui.loadLogin();
				login.show();
			}
		}
	}

	@FXML
	void initialize() {
		assert idAnchorPane != null
				: "fx:id=\"idAnchorPane\" was not injected: check your FXML file 'ConfigurationForm.fxml'.";
		assert idButtonSetPassword != null
				: "fx:id=\"idButtonSetPassword\" was not injected: check your FXML file 'ConfigurationForm.fxml'.";
		assert idButtonCancel != null
				: "fx:id=\"idButtonCancel\" was not injected: check your FXML file 'ConfigurationForm.fxml'.";
		assert idLabelOldPwd != null
				: "fx:id=\"idLabelOldPwd\" was not injected: check your FXML file 'ConfigurationForm.fxml'.";
		assert idPassFieldOldPwd != null
				: "fx:id=\"idPassFieldOldPwd\" was not injected: check your FXML file 'ConfigurationForm.fxml'.";
		assert idLabelConfirmOldPwd != null
				: "fx:id=\"idLabelConfirmOldPwd\" was not injected: check your FXML file 'ConfigurationForm.fxml'.";
		assert idPassFieldConfirmOldPwd != null
				: "fx:id=\"idPassFieldConfirmOldPwd\" was not injected: check your FXML file 'ConfigurationForm.fxml'.";
		assert idLabelNewPwd != null
				: "fx:id=\"idLabelNewPwd\" was not injected: check your FXML file 'ConfigurationForm.fxml'.";
		assert idPassFieldNewPwd != null
				: "fx:id=\"idPassFieldNewPwd\" was not injected: check your FXML file 'ConfigurationForm.fxml'.";
		assert idLabelConfirmNewPwd != null
				: "fx:id=\"idLabelConfirmNewPwd\" was not injected: check your FXML file 'ConfigurationForm.fxml'.";
		assert idPassFieldConfirmNewPed != null
				: "fx:id=\"idPassFieldConfirmNewPed\" was not injected: check your FXML file 'ConfigurationForm.fxml'.";
	}

	private void hideGuiFileds() {
		this.idPassFieldOldPwd.setVisible(false);
		this.idLabelOldPwd.setVisible(false);
		this.idPassFieldConfirmOldPwd.setVisible(false);
		this.idLabelConfirmOldPwd.setVisible(false);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (this.db.readMaster() == null) {
			this.hideGuiFileds();
			this.isMaster = false;
		} else {
			this.isMaster = true;
		}
	}
}