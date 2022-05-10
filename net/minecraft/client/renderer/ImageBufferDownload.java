package net.minecraft.client.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.IImageBuffer;

public class ImageBufferDownload
implements IImageBuffer {
    private int[] imageData;
    private int imageWidth;
    private int imageHeight;

    @Override
    @Nullable
    public BufferedImage parseUserSkin(BufferedImage image) {
        boolean flag;
        if (image == null) {
            return null;
        }
        this.imageWidth = 64;
        this.imageHeight = 64;
        int i2 = image.getWidth();
        int j2 = image.getHeight();
        int k2 = 1;
        while (this.imageWidth < i2 || this.imageHeight < j2) {
            this.imageWidth *= 2;
            this.imageHeight *= 2;
            k2 *= 2;
        }
        BufferedImage bufferedimage = new BufferedImage(this.imageWidth, this.imageHeight, 2);
        Graphics graphics = bufferedimage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        boolean bl2 = flag = image.getHeight() == 32 * k2;
        if (flag) {
            graphics.setColor(new Color(0, 0, 0, 0));
            graphics.fillRect(0 * k2, 32 * k2, 64 * k2, 32 * k2);
            graphics.drawImage(bufferedimage, 24 * k2, 48 * k2, 20 * k2, 52 * k2, 4 * k2, 16 * k2, 8 * k2, 20 * k2, null);
            graphics.drawImage(bufferedimage, 28 * k2, 48 * k2, 24 * k2, 52 * k2, 8 * k2, 16 * k2, 12 * k2, 20 * k2, null);
            graphics.drawImage(bufferedimage, 20 * k2, 52 * k2, 16 * k2, 64 * k2, 8 * k2, 20 * k2, 12 * k2, 32 * k2, null);
            graphics.drawImage(bufferedimage, 24 * k2, 52 * k2, 20 * k2, 64 * k2, 4 * k2, 20 * k2, 8 * k2, 32 * k2, null);
            graphics.drawImage(bufferedimage, 28 * k2, 52 * k2, 24 * k2, 64 * k2, 0 * k2, 20 * k2, 4 * k2, 32 * k2, null);
            graphics.drawImage(bufferedimage, 32 * k2, 52 * k2, 28 * k2, 64 * k2, 12 * k2, 20 * k2, 16 * k2, 32 * k2, null);
            graphics.drawImage(bufferedimage, 40 * k2, 48 * k2, 36 * k2, 52 * k2, 44 * k2, 16 * k2, 48 * k2, 20 * k2, null);
            graphics.drawImage(bufferedimage, 44 * k2, 48 * k2, 40 * k2, 52 * k2, 48 * k2, 16 * k2, 52 * k2, 20 * k2, null);
            graphics.drawImage(bufferedimage, 36 * k2, 52 * k2, 32 * k2, 64 * k2, 48 * k2, 20 * k2, 52 * k2, 32 * k2, null);
            graphics.drawImage(bufferedimage, 40 * k2, 52 * k2, 36 * k2, 64 * k2, 44 * k2, 20 * k2, 48 * k2, 32 * k2, null);
            graphics.drawImage(bufferedimage, 44 * k2, 52 * k2, 40 * k2, 64 * k2, 40 * k2, 20 * k2, 44 * k2, 32 * k2, null);
            graphics.drawImage(bufferedimage, 48 * k2, 52 * k2, 44 * k2, 64 * k2, 52 * k2, 20 * k2, 56 * k2, 32 * k2, null);
        }
        graphics.dispose();
        this.imageData = ((DataBufferInt)bufferedimage.getRaster().getDataBuffer()).getData();
        this.setAreaOpaque(0 * k2, 0 * k2, 32 * k2, 16 * k2);
        if (flag) {
            this.doTransparencyHack(32 * k2, 0 * k2, 64 * k2, 32 * k2);
        }
        this.setAreaOpaque(0 * k2, 16 * k2, 64 * k2, 32 * k2);
        this.setAreaOpaque(16 * k2, 48 * k2, 48 * k2, 64 * k2);
        return bufferedimage;
    }

    @Override
    public void skinAvailable() {
    }

    private void doTransparencyHack(int p_189559_1_, int p_189559_2_, int p_189559_3_, int p_189559_4_) {
        for (int i2 = p_189559_1_; i2 < p_189559_3_; ++i2) {
            for (int j2 = p_189559_2_; j2 < p_189559_4_; ++j2) {
                int k2 = this.imageData[i2 + j2 * this.imageWidth];
                if ((k2 >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (int l2 = p_189559_1_; l2 < p_189559_3_; ++l2) {
            for (int i1 = p_189559_2_; i1 < p_189559_4_; ++i1) {
                int n2 = l2 + i1 * this.imageWidth;
                this.imageData[n2] = this.imageData[n2] & 0xFFFFFF;
            }
        }
    }

    private void setAreaOpaque(int x2, int y2, int width, int height) {
        for (int i2 = x2; i2 < width; ++i2) {
            for (int j2 = y2; j2 < height; ++j2) {
                int n2 = i2 + j2 * this.imageWidth;
                this.imageData[n2] = this.imageData[n2] | 0xFF000000;
            }
        }
    }
}

