import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class TotpGenerator {

    private final byte[] secret;

    public TotpGenerator(byte[] secret) {
        this.secret = secret;
    }

    public boolean validateCode(String token, int validationRange) {
        for (int offset = -validationRange; offset < validationRange + 1; offset++) {
            int counter = (int) Math.floor(Instant.now().toEpochMilli() / 30000.0) + offset;
            if (generateCode(counter, secret).equals(token)) return true;
        }
        return false;
    }

    private String generateCode(int counter, byte[] secret) {
        byte[] buffer = getMessage(counter);
        byte[] hmac = hmacSha1(buffer, secret);
        int truncated = truncate(hmac) & 0x7FFFFFFF;
        return padCode(truncated);
    }

    private byte[] getMessage(int counter) {
        byte[] buffer = ByteBuffer.allocate(8).array();
        for (int i = 0; i < 8; i++) {
            buffer[7 - i] = (byte) (counter & 0xff);
            counter = counter >> 8;
        }
        return buffer;
    }

    private byte[] hmacSha1(byte[] buffer, byte[] key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            return mac.doFinal(buffer);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.err.println("Key error");
            return new byte[0];
        }
    }

    private int truncate(byte[] hmac) {
        int offset = hmac[hmac.length - 1] & 0xf;
        return ((hmac[offset] & 0x7f) << 24) |
                       ((hmac[offset + 1] & 0xff) << 16) |
                       ((hmac[offset + 2] & 0xff) << 8) |
                       (hmac[offset + 3] & 0xff);
    }

    private String padCode(int token) {
        StringBuilder code = new StringBuilder(Integer.toString(token % 1000000));
        while (code.length() < 6) {
            code.insert(0, "0");
        }
        return code.toString();
    }

}
