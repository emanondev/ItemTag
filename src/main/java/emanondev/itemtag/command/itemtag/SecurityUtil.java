package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.YMLConfig;
import emanondev.itemtag.ItemTag;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class SecurityUtil {

    private static UUID uuid;

    private static @NotNull UUID getUUID() {
        if (uuid == null) {
            YMLConfig config = ItemTag.get().getConfig("security.yml");
            try {
                String txt = config.getString("server_uuid", null);
                if (txt != null) {
                    uuid = UUID.fromString(txt);
                } else {
                    uuid = UUID.randomUUID();
                    config.set("server_uuid", uuid.toString());
                    config.save();
                }
            } catch (Exception e) {
                e.printStackTrace();
                uuid = UUID.randomUUID();
                config.set("server_uuid", uuid.toString());
                config.save();
            }
        }
        return uuid;
    }

    public static String generateControlKey(@NotNull String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(
                    (getUUID() + text).getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Platform doesn't support SHA-256");
        }
    }

    public static boolean verifyControlKey(@NotNull String text, @NotNull String controlKey) {
        return controlKey.equals(generateControlKey(text));
    }
}
