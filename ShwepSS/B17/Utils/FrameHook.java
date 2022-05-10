package ShwepSS.B17.Utils;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class FrameHook {
    private static JFrame frame;

    public static void createFrame(DefaultResourcePack mcDefaultResourcePack, Logger logger) throws LWJGLException {
        block5: {
            frame = new JFrame("Minecraft 1.12.2");
            Canvas canvas = new Canvas();
            canvas.setBackground(new Color(16, 16, 16));
            Display.setParent(canvas);
            Minecraft mc = Minecraft.getMinecraft();
            canvas.setSize(mc.displayWidth, mc.displayHeight);
            frame.add(canvas);
            frame.setDefaultCloseOperation(3);
            frame.pack();
            frame.setLocationRelativeTo(null);
            InputStream icon16 = null;
            InputStream icon17 = null;
            try {
                try {
                    icon16 = mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
                    icon17 = mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
                    ArrayList<BufferedImage> icons = new ArrayList<BufferedImage>();
                    icons.add(ImageIO.read(icon16));
                    icons.add(ImageIO.read(icon17));
                    frame.setIconImages(icons);
                }
                catch (Exception e2) {
                    logger.error("Couldn't set icon", (Throwable)e2);
                    IOUtils.closeQuietly(icon16);
                    IOUtils.closeQuietly(icon17);
                    break block5;
                }
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(icon16);
                IOUtils.closeQuietly(icon17);
                throw throwable;
            }
            IOUtils.closeQuietly(icon16);
            IOUtils.closeQuietly(icon17);
            IOUtils.closeQuietly(icon16);
            IOUtils.closeQuietly(icon17);
        }
    }

    private static boolean isAutoMaximize() {
        File autoMaximizeFile = new File(Minecraft.getMinecraft().mcDataDir + "/wurst/automaximize.json");
        boolean autoMaximizeEnabled = false;
        if (!autoMaximizeFile.exists()) {
            FrameHook.createAutoMaximizeFile(autoMaximizeFile);
        }
        try {
            BufferedReader load = new BufferedReader(new FileReader(autoMaximizeFile));
            String line = load.readLine();
            load.close();
            Minecraft.getMinecraft();
            line.equals("true");
            autoMaximizeEnabled = false;
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        return autoMaximizeEnabled;
    }

    private static void createAutoMaximizeFile(File autoMaximizeFile) {
        try {
            PrintWriter printWriter;
            if (!autoMaximizeFile.getParentFile().exists()) {
                autoMaximizeFile.getParentFile().mkdirs();
            }
            PrintWriter save = printWriter = new PrintWriter(new FileWriter(autoMaximizeFile));
            printWriter.println(Boolean.toString(false));
            save.close();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static void maximize() {
        if (frame != null) {
            frame.setExtendedState(6);
        }
    }

    public static JFrame getFrame() {
        return frame;
    }
}

