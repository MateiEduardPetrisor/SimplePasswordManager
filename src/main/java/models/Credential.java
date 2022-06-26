package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;

@Entity
@Table(name = "CREDENTIALS")
@Transactional
public class Credential {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private int id;
	@Column(name = "WEBSITE")
	private String websiteName;
	@Column(name = "USERNAME")
	private String userName;
	@Column(name = "PASSWORD")
	private String userPassword;
	@Column(name = "IV")
	private String iv;

	public Credential() {
		super();
	}

	public Credential(String websiteName, String userName, String userPassword, String Iv) {
		super();
		this.websiteName = websiteName;
		this.userName = userName;
		this.userPassword = userPassword;
		this.iv = Iv;
	}

	public int getId() {
		return this.id;
	}

	public String getWebsiteName() {
		return this.websiteName;
	}

	public void setWebsiteName(String websiteName) {
		this.websiteName = websiteName;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return this.userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	@Override
	public String toString() {
		return this.websiteName;
	}
}