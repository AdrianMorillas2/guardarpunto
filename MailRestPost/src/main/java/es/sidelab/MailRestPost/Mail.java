package es.sidelab.MailRestPost;

public class Mail {

	private String user;
	private String mail;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	@Override
	public String toString() {
		return "Mail [user=" + user + ", mail=" + mail + "]";
	}
	
	
	
}
