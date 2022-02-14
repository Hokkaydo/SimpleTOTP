import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.codec.binary.Base32;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SecretManager {

    private final Map<UUID, byte[]> SECRETS = new HashMap<>();

    public BitMatrix generateQrCode(UUID uniqueId, String name) {
        byte[] bytes = getOrGenerateAndRegister(uniqueId);
        String secret = new String(new Base32().encode(bytes)).replace("=", "");
        try {
            return new QRCodeWriter().encode(String.format("otpauth://totp/%s?secret=%s", name, secret), BarcodeFormat.QR_CODE, 128, 128);
        } catch (WriterException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] getOrGenerateAndRegister(UUID uniqueId) {
        byte[] output = SECRETS.get(uniqueId);
        if (output == null) {
            output = generateAndRegisterSecret(uniqueId);
        }
        return output;
    }

    private byte[] generateAndRegisterSecret(UUID uniqueId) {
        byte[] bytes = new byte[20];
        new SecureRandom().nextBytes(bytes);
        SECRETS.put(uniqueId, bytes);
        return bytes;
    }

    public void remove(UUID uuid) {
        SECRETS.remove(uuid);
    }

}
