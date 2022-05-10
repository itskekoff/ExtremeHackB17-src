package ShwepSS.B17.Utils;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.client.Minecraft;

public final class WurstFolders {
    public static final Path MAIN = Minecraft.getMinecraft().mcDataDir.toPath().resolve("ExtremeHack");
    public static final Path SERVERLISTS = MAIN.resolve("serverlists");

    public static void initialize() {
        if (System.getProperty("user.home") == null) {
            throw new RuntimeException("user.home property is missing!");
        }
        try {
            for (Field field : WurstFolders.class.getFields()) {
                Path path = (Path)field.get(null);
                if (Files.exists(path, new LinkOption[0])) continue;
                Files.createDirectory(path, new FileAttribute[0]);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

