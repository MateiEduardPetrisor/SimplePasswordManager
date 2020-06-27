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
	@Column(name = "USER")
	private String userName;
	@Column(name = "PASSWORD")
	private String userPassword;

	public Credential() {
		super();
	}

	public Credential(String websiteName, String userName, String userPassword) {
		super();
		this.websiteName = websiteName;
		this.userName = userName;
		this.userPassword = userPassword;
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

	@Override
	public String toString() {
		return this.id + " " + this.websiteName;
	}
}