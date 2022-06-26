package database;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import encryption.Encryption;
import models.Credential;

public class Database implements AutoCloseable {
	private static Database instance;
	private Encryption api;
	private Configuration hibernateConfiguration;
	private SessionFactory hibernateSessionFactory;

	public static Database getInstance() {
		if (Database.instance == null) {
			synchronized (Database.class) {
				Database.instance = new Database();
			}
		}
		return Database.instance;
	}

	private Database() {
		super();
		this.hibernateConfiguration = new Configuration();
		this.hibernateConfiguration.addAnnotatedClass(Credential.class);
		this.hibernateConfiguration.configure();
		this.hibernateSessionFactory = this.hibernateConfiguration.buildSessionFactory();
		this.api = Encryption.getInstance();
	}

	public ApplicationError setup(String password, String confirmPassword)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		if (this.checkPasswordCompliance(password) || this.checkPasswordCompliance(confirmPassword)) {
			if (password.equals(confirmPassword)) {
				Session hibernateSession = this.hibernateSessionFactory.openSession();
				Transaction transaction = hibernateSession.beginTransaction();
				byte[] Iv = this.api.generateInitializationVector();
				String encIv = this.api.encode(new String(Iv));
				String filePwd = this.api.hashPassword(password);
				Credential master = new Credential(this.api.encode(Encryption.FILE_SIG),
						this.api.encode(Encryption.MASTER), this.api.encode(filePwd), encIv);
				hibernateSession.save(master);
				transaction.commit();
				hibernateSession.close();
				return ApplicationError.NO_ERROR;
			} else {
				return ApplicationError.PASSWORDS_MISMATCH;
			}
		} else {
			return ApplicationError.INVALID_PASSWORD;
		}
	}

	public ApplicationError authenticateUser(String password) throws NoSuchAlgorithmException, NoSuchProviderException {
		Credential master = this.readMaster();
		if (master != null) {
			if (this.checkPasswordCompliance(password)) {
				String filePwd = this.api.decode(master.getUserPassword());
				String inputPwd = this.api.hashPassword(password);
				if (filePwd.equals(inputPwd)) {
					this.api.setIv(this.api.decode(master.getUserName()));
					this.api.setPwd(password);
					return ApplicationError.NO_ERROR;
				} else {
					return ApplicationError.WRONG_PASSWORD;
				}
			} else {
				return ApplicationError.INVALID_PASSWORD;
			}
		} else {
			return ApplicationError.NO_MASTER;
		}
	}

	public Credential readMaster() {
		Session hibernateSession = this.hibernateSessionFactory.openSession();
		String masterHQL = "From Credential C where C.websiteName =: pWebsite";
		Query<?> query = hibernateSession.createQuery(masterHQL);
		query.setParameter("pWebsite", this.api.encode(Encryption.FILE_SIG));
		List<?> result = query.list();
		Credential master = null;
		if (result.size() >= 1) {
			master = (Credential) result.get(0);
		}
		hibernateSession.close();
		return master;
	}

	private boolean checkPasswordCompliance(String password) {
		if (password != null && !password.trim().isEmpty()
				&& (password.length() == 16 || password.length() == 24 || password.length() == 32)) {
			return true;
		} else {
			return false;
		}
	}

	private List<?> decryptCredentials()
			throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		List<?> cred = (List<?>) this.readCredentials();
		for (Object element : cred) {
			if (element instanceof Credential) {
				String iv = this.api.decode(((Credential) element).getIv());
				this.api.setIv(iv);
				String website = this.api.decrypt(((Credential) element).getWebsiteName());
				String username = this.api.decrypt(((Credential) element).getUserName());
				String password = this.api.decrypt(((Credential) element).getUserPassword());
				((Credential) element).setWebsiteName(website);
				((Credential) element).setUserName(username);
				((Credential) element).setUserPassword(password);
			}
		}
		return cred;
	}

	private void encryptCredentials(List<?> decryptedCredential)
			throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		for (Object element : decryptedCredential) {
			if (element instanceof Credential) {
				byte[] iv = this.api.generateInitializationVector();
				this.api.setIv(new String(iv));
				String encIv = this.api.encode(new String(iv));
				String website = this.api.encrypt(((Credential) element).getWebsiteName());
				String username = this.api.encrypt(((Credential) element).getUserName());
				String password = this.api.encrypt(((Credential) element).getUserPassword());
				((Credential) element).setWebsiteName(website);
				((Credential) element).setUserName(username);
				((Credential) element).setUserPassword(password);
				((Credential) element).setIv(encIv);
				this.updateCredential(((Credential) element).getId(), (Credential) element);
			}
		}
	}

	public ApplicationError migrate(String oldPassword, String confirmOldPassword, String newPassword,
			String confirmNewPassword)
			throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		if (this.checkPasswordCompliance(oldPassword) && this.checkPasswordCompliance(confirmOldPassword)
				&& this.checkPasswordCompliance(newPassword) && this.checkPasswordCompliance(confirmNewPassword)) {
			if (oldPassword.equals(confirmOldPassword)) {
				Credential oldMaster = this.readMaster();
				if (oldMaster != null) {
					String oldPwd = this.api.hashPassword(oldPassword);
					String filePwd = this.api.decode(oldMaster.getUserPassword());
					this.api.setPwd(oldPassword);
					this.api.setIv(this.api.decode(oldMaster.getUserName()));
					if (oldPwd.equals(filePwd)) {
						if (newPassword.equals(confirmNewPassword)) {
							List<?> credentials = this.decryptCredentials();
							byte[] newIv = this.api.generateInitializationVector();
							this.api.setIv(new String(newIv));
							String encNewIv = this.api.encode(new String(newIv));
							String newFilePwd = this.api.hashPassword(newPassword);
							Credential newMaster = new Credential(this.api.encode(Encryption.FILE_SIG),
									this.api.encode(Encryption.MASTER), this.api.encode(newFilePwd), encNewIv);
							this.api.setPwd(newPassword);
							this.api.setIv(new String(newIv));
							this.updateCredential(oldMaster.getId(), newMaster);
							this.encryptCredentials(credentials);
							return ApplicationError.NO_ERROR;
						} else {
							return ApplicationError.PASSWORDS_MISMATCH;
						}
					} else {
						return ApplicationError.WRONG_PASSWORD;
					}
				} else {
					return ApplicationError.NO_MASTER;
				}
			} else {
				return ApplicationError.PASSWORDS_MISMATCH;
			}
		} else {
			return ApplicationError.INVALID_PASSWORD;
		}
	}

	public void insertCredential(Credential credential) throws SecurityException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, SystemException {
		Session hibernateSession = this.hibernateSessionFactory.openSession();
		Transaction transaction = hibernateSession.beginTransaction();
		hibernateSession.save(credential);
		transaction.commit();
		hibernateSession.close();
	}

	public List<?> readCredentials() {
		Session hibernateSession = this.hibernateSessionFactory.openSession();
		String readHQL = "From Credential C where C.websiteName !=: pWebsite";
		Query<?> query = hibernateSession.createQuery(readHQL);
		query.setParameter("pWebsite", this.api.encode(Encryption.FILE_SIG));
		List<?> results = query.list();
		hibernateSession.close();
		return results;
	}

	public void updateCredential(int id, Credential credential) {
		Session hibernateSession = this.hibernateSessionFactory.openSession();
		Transaction transaction = hibernateSession.beginTransaction();
		String updateHQL = "UPDATE Credential set websiteName =: pWebsite, userName =: pUserName, userPassword =: pUserPassword, iv =: pIv WHERE id =: pId";
		Query<?> query = hibernateSession.createQuery(updateHQL);
		query.setParameter("pId", id);
		query.setParameter("pWebsite", credential.getWebsiteName());
		query.setParameter("pUserName", credential.getUserName());
		query.setParameter("pUserPassword", credential.getUserPassword());
		query.setParameter("pIv", credential.getIv());
		query.executeUpdate();
		transaction.commit();
		hibernateSession.close();
	}

	public void deleteCredential(int id) {
		Session hibernateSession = this.hibernateSessionFactory.openSession();
		Transaction transaction = hibernateSession.beginTransaction();
		String deleteHQL = "DELETE FROM Credential WHERE id = :pId";
		Query<?> query = hibernateSession.createQuery(deleteHQL);
		query.setParameter("pId", id);
		query.executeUpdate();
		transaction.commit();
		hibernateSession.close();
	}

	@Override
	public void close() throws Exception {
		if (this.hibernateSessionFactory != null && this.hibernateSessionFactory.isOpen()) {
			this.hibernateSessionFactory.close();
		}
	}
}