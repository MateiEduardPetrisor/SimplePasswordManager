package controllers;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import database.Database;
import encryption.Encryption;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Credential;

public class CredentialsCtrl implements Initializable {
	private final GUILoader gui = GUILoader.getInstance();
	private final Encryption api = Encryption.getInstance();
	private final Database db = Database.getInstance();

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private AnchorPane idAnchorPane;

	@FXML
	private ListView<Credential> idListViewPasswords;

	@FXML
	private Button idButtonAddCredential;

	@FXML
	private Button idButtonDeleteCredential;

	@FXML
	private Button idButtonEditCredential;

	@FXML
	private Button idButtonLock;

	@FXML
	void addCredentialClick(ActionEvent event) throws IOException {
		this.gui.closeWindow(event);
		Stage addCredential = this.gui.loadAddEdit(null);
		addCredential.show();
	}

	@FXML
	void deleteCredentialClick(ActionEvent event) throws InvalidKeyException, InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		Credential selected = this.idListViewPasswords.getSelectionModel().getSelectedItem();
		if (selected != null) {
			if (this.gui.displayConfrimation("Confirm delete ?") == ButtonType.YES) {
				this.idListViewPasswords.getItems().remove(selected);
				this.db.deleteCredential(selected.getId());
			}
		} else {
			this.gui.displayAlert(AlertType.WARNING, "Select a credential to delete!");
		}
	}

	@FXML
	void editCredentialClick(ActionEvent event)
			throws IOException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Credential selected = this.idListViewPasswords.getSelectionModel().getSelectedItem();
		if (selected != null) {
			this.api.setIv(this.api.decode(selected.getIv()));
			selected.setWebsiteName(this.api.encrypt(selected.getWebsiteName()));
			this.idListViewPasswords.getItems().remove(selected);
			this.gui.closeWindow(event);
			Stage editCredential = this.gui.loadAddEdit(selected);
			editCredential.show();
		} else {
			this.gui.displayAlert(AlertType.WARNING, "Select a credential to edit!");
		}
	}

	@FXML
	void lockClick(ActionEvent event) throws IOException {
		this.gui.closeWindow(event);
		Stage login = this.gui.loadLogin();
		login.show();
	}

	@FXML
	void initialize() {
		assert idAnchorPane != null
				: "fx:id=\"idAnchorPane\" was not injected: check your FXML file 'PasswordsListForm.fxml'.";
		assert idListViewPasswords != null
				: "fx:id=\"idListViewPasswords\" was not injected: check your FXML file 'PasswordsListForm.fxml'.";
		assert idButtonAddCredential != null
				: "fx:id=\"idButtonAddCredential\" was not injected: check your FXML file 'PasswordsListForm.fxml'.";
		assert idButtonDeleteCredential != null
				: "fx:id=\"idButtonDeleteCredential\" was not injected: check your FXML file 'PasswordsListForm.fxml'.";
		assert idButtonEditCredential != null
				: "fx:id=\"idButtonEditCredential\" was not injected: check your FXML file 'PasswordsListForm.fxml'.";
		assert idButtonLock != null
				: "fx:id=\"idButtonLock\" was not injected: check your FXML file 'PasswordsListForm.fxml'.";
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			List<?> cred = (List<?>) this.db.readCredentials();
			for (Object element : cred) {
				if (element instanceof Credential) {
					this.api.setIv(this.api.decode(((Credential) element).getIv()));
					((Credential) element).setWebsiteName(this.api.decrypt(((Credential) element).getWebsiteName()));
					this.idListViewPasswords.getItems().add((Credential) element);
				}
			}
			ObservableList<Credential> unsortedList = this.idListViewPasswords.getItems();
			SortedList<Credential> sortedList = this.idListViewPasswords.getItems().sorted();
			FXCollections.copy(unsortedList, sortedList);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException
				| NoSuchProviderException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}
	}
}