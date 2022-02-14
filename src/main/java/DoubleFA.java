import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class DoubleFA {

    private static final SecretManager secretManager = new SecretManager();
    private static final Map<Integer, DoubleFAUser> users = new HashMap<>();

    public static void main(String[] args) throws IOException {
        System.out.println("--- Usage ---");
        System.out.println("cu: Create user in database and create an associated QRCode for 2FA");
        System.out.println("auth <id> <code>: Test authentication system for <id> user and check if given <code> is correct");
        System.out.println("ru <id>: Remove user from database");
        System.out.println("Enter q to abort");
        int counter = 0;
        Scanner scanner = new Scanner(System.in);
        String entry;
        while (!(entry = scanner.nextLine()).equals("q")) {
            if (entry.equals("cu")) {
                UUID uuid = UUID.randomUUID();
                BitMatrix bitMatrix = secretManager.generateQrCode(uuid, String.valueOf(counter));
                //MatrixToImageWriter.writeToPath(bitMatrix, "png", Path.of("./QRCode-"+counter+".png"));
                BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
                File file = new File("./QRCode-" + counter + ".png");
                file.createNewFile();
                ImageIO.write(image, "png", file);
                System.out.println("Created user : " + counter);
                DoubleFAUser dfaUser = new DefaultDoubleFAUser(uuid, secretManager.getOrGenerateAndRegister(uuid));
                users.put(counter++, dfaUser);
                continue;
            }
            if (entry.startsWith("auth")) {
                String[] split = entry.split(" ");
                if (split.length < 3) {
                    System.out.println("Syntax: auth <id> <code>");
                    continue;
                }
                String id = split[1];
                if (!isInteger(id) || Integer.parseInt(id) > users.size() - 1 || Integer.parseInt(id) < -1) {
                    System.out.println("Id must be a positive integer between 0 and " + (users.size() - 1));
                    continue;
                }
                String token = split[2];
                if (token.length() != 6 || !isInteger(token)) {
                    System.out.println("Please specify totp token in 6 integers");
                    continue;
                }
                TotpGenerator totpGenerator = new TotpGenerator(secretManager.getOrGenerateAndRegister(users.get(Integer.parseInt(id)).getUniqueId()));
                boolean valid = totpGenerator.validateCode(token, 1);
                System.out.println(valid ? "Valid token" : "Invalid token");
            }

            if (entry.startsWith("ru")) {
                String[] split = entry.split(" ");
                if (split.length < 2) {
                    System.out.println("Syntax: ru <id>");
                    continue;
                }
                String id = split[1];
                if (!isInteger(id) || Integer.parseInt(id) > users.size() - 1 || Integer.parseInt(id) < -1) {
                    System.out.println("Id must be a positive integer between 0 and " + (users.size() - 1));
                    continue;
                }
                secretManager.remove(users.get(Integer.parseInt(id)).getUniqueId());
                users.remove(Integer.parseInt(id));
                File file = new File("./QRCode-" + id + ".png");
                file.delete();
            }
        }
    }

    private static boolean isInteger(String integer) {
        try {
            Integer.parseInt(integer);
            return true;
        } catch (NumberFormatException ignored) {
        }
        return false;
    }

}
