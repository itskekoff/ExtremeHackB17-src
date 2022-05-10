package net.minecraft.client.shader;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class Framebuffer {
    public int framebufferTextureWidth;
    public int framebufferTextureHeight;
    public int framebufferWidth;
    public int framebufferHeight;
    public boolean useDepth;
    public int framebufferObject;
    public int framebufferTexture;
    public int depthBuffer;
    public float[] framebufferColor;
    public int framebufferFilter;

    public Framebuffer(int width, int height, boolean useDepthIn) {
        this.useDepth = useDepthIn;
        this.framebufferObject = -1;
        this.framebufferTexture = -1;
        this.depthBuffer = -1;
        this.framebufferColor = new float[4];
        this.framebufferColor[0] = 1.0f;
        this.framebufferColor[1] = 1.0f;
        this.framebufferColor[2] = 1.0f;
        this.framebufferColor[3] = 0.0f;
        this.createBindFramebuffer(width, height);
    }

    public void createBindFramebuffer(int width, int height) {
        if (!OpenGlHelper.isFramebufferEnabled()) {
            this.framebufferWidth = width;
            this.framebufferHeight = height;
        } else {
            GlStateManager.enableDepth();
            if (this.framebufferObject >= 0) {
                this.deleteFramebuffer();
            }
            this.createFramebuffer(width, height);
            this.checkFramebufferComplete();
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
        }
    }

    public void deleteFramebuffer() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            this.unbindFramebufferTexture();
            this.unbindFramebuffer();
            if (this.depthBuffer > -1) {
                OpenGlHelper.glDeleteRenderbuffers(this.depthBuffer);
                this.depthBuffer = -1;
            }
            if (this.framebufferTexture > -1) {
                TextureUtil.deleteTexture(this.framebufferTexture);
                this.framebufferTexture = -1;
            }
            if (this.framebufferObject > -1) {
                OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
                OpenGlHelper.glDeleteFramebuffers(this.framebufferObject);
                this.framebufferObject = -1;
            }
        }
    }

    public void createFramebuffer(int width, int height) {
        this.framebufferWidth = width;
        this.framebufferHeight = height;
        this.framebufferTextureWidth = width;
        this.framebufferTextureHeight = height;
        if (!OpenGlHelper.isFramebufferEnabled()) {
            this.framebufferClear();
        } else {
            this.framebufferObject = OpenGlHelper.glGenFramebuffers();
            this.framebufferTexture = TextureUtil.glGenTextures();
            if (this.useDepth) {
                this.depthBuffer = OpenGlHelper.glGenRenderbuffers();
            }
            this.setFramebufferFilter(9728);
            GlStateManager.bindTexture(this.framebufferTexture);
            GlStateManager.glTexImage2D(3553, 0, 32856, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, 6408, 5121, null);
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject);
            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, this.framebufferTexture, 0);
            if (this.useDepth) {
                OpenGlHelper.glBindRenderbuffer(OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
                OpenGlHelper.glRenderbufferStorage(OpenGlHelper.GL_RENDERBUFFER, 33190, this.framebufferTextureWidth, this.framebufferTextureHeight);
                OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
            }
            this.framebufferClear();
            this.unbindFramebufferTexture();
        }
    }

    public void setFramebufferFilter(int framebufferFilterIn) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            this.framebufferFilter = framebufferFilterIn;
            GlStateManager.bindTexture(this.framebufferTexture);
            GlStateManager.glTexParameteri(3553, 10241, framebufferFilterIn);
            GlStateManager.glTexParameteri(3553, 10240, framebufferFilterIn);
            GlStateManager.glTexParameteri(3553, 10242, 10496);
            GlStateManager.glTexParameteri(3553, 10243, 10496);
            GlStateManager.bindTexture(0);
        }
    }

    public void checkFramebufferComplete() {
        int i2 = OpenGlHelper.glCheckFramebufferStatus(OpenGlHelper.GL_FRAMEBUFFER);
        if (i2 != OpenGlHelper.GL_FRAMEBUFFER_COMPLETE) {
            if (i2 == OpenGlHelper.GL_FB_INCOMPLETE_ATTACHMENT) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
            }
            if (i2 == OpenGlHelper.GL_FB_INCOMPLETE_MISS_ATTACH) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
            }
            if (i2 == OpenGlHelper.GL_FB_INCOMPLETE_DRAW_BUFFER) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
            }
            if (i2 == OpenGlHelper.GL_FB_INCOMPLETE_READ_BUFFER) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
            }
            throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i2);
        }
    }

    public void bindFramebufferTexture() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(this.framebufferTexture);
        }
    }

    public void unbindFramebufferTexture() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(0);
        }
    }

    public void bindFramebuffer(boolean p_147610_1_) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject);
            if (p_147610_1_) {
                GlStateManager.viewport(0, 0, this.framebufferWidth, this.framebufferHeight);
            }
        }
    }

    public void unbindFramebuffer() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
        }
    }

    public void setFramebufferColor(float red, float green, float blue, float alpha) {
        this.framebufferColor[0] = red;
        this.framebufferColor[1] = green;
        this.framebufferColor[2] = blue;
        this.framebufferColor[3] = alpha;
    }

    public void framebufferRender(int width, int height) {
        this.framebufferRenderExt(width, height, true);
    }

    public void framebufferRenderExt(int width, int height, boolean p_178038_3_) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.colorMask(true, true, true, false);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, width, height, 0.0, 1000.0, 3000.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0f, 0.0f, -2000.0f);
            GlStateManager.viewport(0, 0, width, height);
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableAlpha();
            if (p_178038_3_) {
                GlStateManager.disableBlend();
                GlStateManager.enableColorMaterial();
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.bindFramebufferTexture();
            float f2 = width;
            float f1 = height;
            float f22 = (float)this.framebufferWidth / (float)this.framebufferTextureWidth;
            float f3 = (float)this.framebufferHeight / (float)this.framebufferTextureHeight;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(0.0, f1, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(f2, f1, 0.0).tex(f22, 0.0).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(f2, 0.0, 0.0).tex(f22, f3).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, f3).color(255, 255, 255, 255).endVertex();
            tessellator.draw();
            this.unbindFramebufferTexture();
            GlStateManager.depthMask(true);
            GlStateManager.colorMask(true, true, true, true);
        }
    }

    public void framebufferClear() {
        this.bindFramebuffer(true);
        GlStateManager.clearColor(this.framebufferColor[0], this.framebufferColor[1], this.framebufferColor[2], this.framebufferColor[3]);
        int i2 = 16384;
        if (this.useDepth) {
            GlStateManager.clearDepth(1.0);
            i2 |= 0x100;
        }
        GlStateManager.clear(i2);
        this.unbindFramebuffer();
    }
}

