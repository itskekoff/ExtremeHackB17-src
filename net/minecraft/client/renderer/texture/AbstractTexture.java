package net.minecraft.client.renderer.texture;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import shadersmod.client.MultiTexID;
import shadersmod.client.ShadersTex;

public abstract class AbstractTexture
implements ITextureObject {
    protected int glTextureId = -1;
    protected boolean blur;
    protected boolean mipmap;
    protected boolean blurLast;
    protected boolean mipmapLast;
    public MultiTexID multiTex;

    public void setBlurMipmapDirect(boolean blurIn, boolean mipmapIn) {
        int j2;
        int i2;
        this.blur = blurIn;
        this.mipmap = mipmapIn;
        if (blurIn) {
            i2 = mipmapIn ? 9987 : 9729;
            j2 = 9729;
        } else {
            i2 = mipmapIn ? 9986 : 9728;
            j2 = 9728;
        }
        GlStateManager.bindTexture(this.getGlTextureId());
        GlStateManager.glTexParameteri(3553, 10241, i2);
        GlStateManager.glTexParameteri(3553, 10240, j2);
    }

    @Override
    public void setBlurMipmap(boolean blurIn, boolean mipmapIn) {
        this.blurLast = this.blur;
        this.mipmapLast = this.mipmap;
        this.setBlurMipmapDirect(blurIn, mipmapIn);
    }

    @Override
    public void restoreLastBlurMipmap() {
        this.setBlurMipmapDirect(this.blurLast, this.mipmapLast);
    }

    @Override
    public int getGlTextureId() {
        if (this.glTextureId == -1) {
            this.glTextureId = TextureUtil.glGenTextures();
        }
        return this.glTextureId;
    }

    public void deleteGlTexture() {
        ShadersTex.deleteTextures(this, this.glTextureId);
        if (this.glTextureId != -1) {
            TextureUtil.deleteTexture(this.glTextureId);
            this.glTextureId = -1;
        }
    }

    @Override
    public MultiTexID getMultiTexID() {
        return ShadersTex.getMultiTexID(this);
    }
}

