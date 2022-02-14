import java.util.UUID;

public class DefaultDoubleFAUser implements DoubleFAUser {


    private final TotpGenerator totpGenerator;
    private final UUID uuid;

    public DefaultDoubleFAUser(UUID uuid, byte[] secret) {
        this.uuid = uuid;
        this.totpGenerator = new TotpGenerator(secret);
    }

    @Override
    public boolean is2FARequired() {
        return true;
    }

    @Override
    public boolean is2FAEnabled() {
        return true;
    }

    @Override
    public void set2FAEnabled() {
        //empty
    }

    @Override
    public boolean validate2FACode(String code) {
        return totpGenerator.validateCode(code, 1);
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

}
