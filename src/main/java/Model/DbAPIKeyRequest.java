package Model;

import org.jetbrains.annotations.Nullable;

import java.sql.Date;

public class DbAPIKeyRequest {
    private final String emailAddress;
    private String APIKey;
    private final DbAPIKeyCalls call;
    private Date validTo;

    public DbAPIKeyRequest(DbAPIKeyCalls call, String emailAddress) {
        this.emailAddress = emailAddress;
        this.call = call;
    }

    public DbAPIKeyRequest(DbAPIKeyCalls call, String emailAddress, String APIKey) {
        this.emailAddress = emailAddress;
        this.APIKey = APIKey;
        this.call = call;
    }

    public DbAPIKeyRequest(DbAPIKeyCalls call, String emailAddress, String APIKey, @Nullable Date validTo) {
        this.emailAddress = emailAddress;
        this.APIKey = APIKey;
        this.call = call;
        this.validTo = validTo;
    }

    public DbAPIKeyCalls getCall() {
        return call;
    }

    public Date getValidTo() {
        return validTo;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getAPIKey() {
        return APIKey;
    }
}
