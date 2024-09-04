package Model;

public class ContextBody {
    private String emailAddress;

    private String APIKey;

    private String username;
    private String password;
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAPIKey() {
        return APIKey;
    }

    public void setAPIKey(String APIKey) {
        this.APIKey = APIKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
