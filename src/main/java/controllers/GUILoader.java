package controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import database.ApplicationError;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import models.Credential;

public class GUILoader {
	private static GUILoader instance;

	public static GUILoader getInstance() {
		if (GUILoader.instance == null) {
			synchronized (GUILoader.class) {
				GUILoader.instance = new GUILoader();
			}
		}
		return GUILoader.instance;
	}

	private GUILoader() {
		super();
	}

	public void displayAlert(AlertType type, String messageText) {
		Alert alert = new Alert(type, messageText);
		alert.showAndWait();
	}

	public ButtonType displayConfrimation(String messageText) {
		Alert alert = new Alert(AlertType.CONFIRMATION, messageText, ButtonType.YES, ButtonType.NO);
		alert.showAndWait();
		return alert.getResult();
	}

	public boolean errorHandler(ApplicationError code) {
		switch (code) {
		case NO_ERROR:
			return true;
		case INVALID_PASSWORD:
			this.displayAlert(AlertType.ERROR, "Password not compliant!");
			return false;
		case NO_MASTER:
			this.displayAlert(AlertType.ERROR, "Master not found!");
			return false;
		case PASSWORDS_MISMATCH:
			this.displayAlert(AlertType.ERROR, "Password mismatch!");
			return false;
		case WRONG_PASSWORD:
			this.displayAlert(AlertType.ERROR, "Wrong Password!");
			return false;
		default:
			this.displayAlert(AlertType.ERROR, "Error " + code);
			return false;
		}
	}

	public void closeWindow(ActionEvent event) {
		Node sourceNode = (Node) event.getSource();
		Stage sourceStage = (Stage) sourceNode.getScene().getWindow();
		sourceStage.close();
	}

	public Stage loadLogin() throws IOException {
		Stage login = null;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
		Parent parent = fxmlLoader.load();
		login = new Stage();
		Scene scene = new Scene(parent);
		login.setResizable(false);
		login.setScene(scene);
		login.initStyle(StageStyle.DECORATED);
		login.setTitle("Simple Password Manager");
		return login;
	}

	public Stage loadConfiguration() throws IOException {
		Stage configuration = null;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Configuration.fxml"));
		Parent parent = fxmlLoader.load();
		configuration = new Stage();
		Scene scene = new Scene(parent);
		configuration.setResizable(false);
		configuration.setScene(scene);
		configuration.setTitle("Setup Master Password");
		configuration.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				try {
					Stage login = loadLogin();
					login.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		return configuration;
	}

	public Stage loadCredentials() throws IOException {
		Stage credentials = null;
		Parent parent = null;
		Scene scene = null;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Credentials.fxml"));
		parent = fxmlLoader.load();
		credentials = new Stage();
		scene = new Scene(parent);
		credentials.setResizable(false);
		credentials.setScene(scene);
		credentials.setTitle("Credentials List");
		credentials.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				try {
					Stage login = loadLogin();
					login.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		return credentials;
	}

	public Stage loadAddEdit(Credential credential) throws IOException {
		Stage addEdit = null;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AddEdit.fxml"));
		fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
			@Override
			public Object call(Class<?> param) {
				if (param == AddEditCtrl.class) {
					AddEditCtrl passwordFormController = new AddEditCtrl();
					passwordFormController.setCredential(credential);
					return passwordFormController;
				} else {
					try {
						param.getDeclaredConstructor().newInstance();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				}
				return param;
			}
		});
		Parent parent = null;
		Scene scene = null;
		parent = fxmlLoader.load();
		addEdit = new Stage();
		scene = new Scene(parent);
		addEdit.setResizable(false);
		addEdit.setScene(scene);
		addEdit.setTitle("Add/Edit Credential");
		addEdit.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				try {
					Stage credentialsList = loadCredentials();
					credentialsList.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		return addEdit;
	}
}