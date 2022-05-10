package net.minecraft.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;

public class ModelBox {
    private final PositionTextureVertex[] vertexPositions;
    private final TexturedQuad[] quadList;
    public final float posX1;
    public final float posY1;
    public final float posZ1;
    public final float posX2;
    public final float posY2;
    public final float posZ2;
    public String boxName;

    public ModelBox(ModelRenderer renderer, int texU, int texV, float x2, float y2, float z2, int dx2, int dy2, int dz2, float delta) {
        this(renderer, texU, texV, x2, y2, z2, dx2, dy2, dz2, delta, renderer.mirror);
    }

    public ModelBox(ModelRenderer p_i0_1_, int[][] p_i0_2_, float p_i0_3_, float p_i0_4_, float p_i0_5_, float p_i0_6_, float p_i0_7_, float p_i0_8_, float p_i0_9_, boolean p_i0_10_) {
        this.posX1 = p_i0_3_;
        this.posY1 = p_i0_4_;
        this.posZ1 = p_i0_5_;
        this.posX2 = p_i0_3_ + p_i0_6_;
        this.posY2 = p_i0_4_ + p_i0_7_;
        this.posZ2 = p_i0_5_ + p_i0_8_;
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new TexturedQuad[6];
        float f2 = p_i0_3_ + p_i0_6_;
        float f1 = p_i0_4_ + p_i0_7_;
        float f22 = p_i0_5_ + p_i0_8_;
        p_i0_3_ -= p_i0_9_;
        p_i0_4_ -= p_i0_9_;
        p_i0_5_ -= p_i0_9_;
        f2 += p_i0_9_;
        f1 += p_i0_9_;
        f22 += p_i0_9_;
        if (p_i0_10_) {
            float f3 = f2;
            f2 = p_i0_3_;
            p_i0_3_ = f3;
        }
        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(p_i0_3_, p_i0_4_, p_i0_5_, 0.0f, 0.0f);
        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f2, p_i0_4_, p_i0_5_, 0.0f, 8.0f);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f2, f1, p_i0_5_, 8.0f, 8.0f);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(p_i0_3_, f1, p_i0_5_, 8.0f, 0.0f);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(p_i0_3_, p_i0_4_, f22, 0.0f, 0.0f);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f2, p_i0_4_, f22, 0.0f, 8.0f);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f2, f1, f22, 8.0f, 8.0f);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(p_i0_3_, f1, f22, 8.0f, 0.0f);
        this.vertexPositions[0] = positiontexturevertex7;
        this.vertexPositions[1] = positiontexturevertex;
        this.vertexPositions[2] = positiontexturevertex1;
        this.vertexPositions[3] = positiontexturevertex2;
        this.vertexPositions[4] = positiontexturevertex3;
        this.vertexPositions[5] = positiontexturevertex4;
        this.vertexPositions[6] = positiontexturevertex5;
        this.vertexPositions[7] = positiontexturevertex6;
        this.quadList[0] = this.makeTexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5}, p_i0_2_[4], false, p_i0_1_.textureWidth, p_i0_1_.textureHeight);
        this.quadList[1] = this.makeTexturedQuad(new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, p_i0_2_[5], false, p_i0_1_.textureWidth, p_i0_1_.textureHeight);
        this.quadList[2] = this.makeTexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, p_i0_2_[1], true, p_i0_1_.textureWidth, p_i0_1_.textureHeight);
        this.quadList[3] = this.makeTexturedQuad(new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, p_i0_2_[0], true, p_i0_1_.textureWidth, p_i0_1_.textureHeight);
        this.quadList[4] = this.makeTexturedQuad(new PositionTextureVertex[]{positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, p_i0_2_[2], false, p_i0_1_.textureWidth, p_i0_1_.textureHeight);
        this.quadList[5] = this.makeTexturedQuad(new PositionTextureVertex[]{positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6}, p_i0_2_[3], false, p_i0_1_.textureWidth, p_i0_1_.textureHeight);
        if (p_i0_10_) {
            TexturedQuad[] arrtexturedQuad = this.quadList;
            int n2 = this.quadList.length;
            for (int i2 = 0; i2 < n2; ++i2) {
                TexturedQuad texturedquad = arrtexturedQuad[i2];
                texturedquad.flipFace();
            }
        }
    }

    private TexturedQuad makeTexturedQuad(PositionTextureVertex[] p_makeTexturedQuad_1_, int[] p_makeTexturedQuad_2_, boolean p_makeTexturedQuad_3_, float p_makeTexturedQuad_4_, float p_makeTexturedQuad_5_) {
        if (p_makeTexturedQuad_2_ == null) {
            return null;
        }
        return p_makeTexturedQuad_3_ ? new TexturedQuad(p_makeTexturedQuad_1_, p_makeTexturedQuad_2_[2], p_makeTexturedQuad_2_[3], p_makeTexturedQuad_2_[0], p_makeTexturedQuad_2_[1], p_makeTexturedQuad_4_, p_makeTexturedQuad_5_) : new TexturedQuad(p_makeTexturedQuad_1_, p_makeTexturedQuad_2_[0], p_makeTexturedQuad_2_[1], p_makeTexturedQuad_2_[2], p_makeTexturedQuad_2_[3], p_makeTexturedQuad_4_, p_makeTexturedQuad_5_);
    }

    public ModelBox(ModelRenderer renderer, int texU, int texV, float x2, float y2, float z2, int dx2, int dy2, int dz2, float delta, boolean mirror) {
        this.posX1 = x2;
        this.posY1 = y2;
        this.posZ1 = z2;
        this.posX2 = x2 + (float)dx2;
        this.posY2 = y2 + (float)dy2;
        this.posZ2 = z2 + (float)dz2;
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new TexturedQuad[6];
        float f2 = x2 + (float)dx2;
        float f1 = y2 + (float)dy2;
        float f22 = z2 + (float)dz2;
        x2 -= delta;
        y2 -= delta;
        z2 -= delta;
        f2 += delta;
        f1 += delta;
        f22 += delta;
        if (mirror) {
            float f3 = f2;
            f2 = x2;
            x2 = f3;
        }
        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(x2, y2, z2, 0.0f, 0.0f);
        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f2, y2, z2, 0.0f, 8.0f);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f2, f1, z2, 8.0f, 8.0f);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(x2, f1, z2, 8.0f, 0.0f);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(x2, y2, f22, 0.0f, 0.0f);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f2, y2, f22, 0.0f, 8.0f);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f2, f1, f22, 8.0f, 8.0f);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(x2, f1, f22, 8.0f, 0.0f);
        this.vertexPositions[0] = positiontexturevertex7;
        this.vertexPositions[1] = positiontexturevertex;
        this.vertexPositions[2] = positiontexturevertex1;
        this.vertexPositions[3] = positiontexturevertex2;
        this.vertexPositions[4] = positiontexturevertex3;
        this.vertexPositions[5] = positiontexturevertex4;
        this.vertexPositions[6] = positiontexturevertex5;
        this.vertexPositions[7] = positiontexturevertex6;
        this.quadList[0] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5}, texU + dz2 + dx2, texV + dz2, texU + dz2 + dx2 + dz2, texV + dz2 + dy2, renderer.textureWidth, renderer.textureHeight);
        this.quadList[1] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, texU, texV + dz2, texU + dz2, texV + dz2 + dy2, renderer.textureWidth, renderer.textureHeight);
        this.quadList[2] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, texU + dz2, texV, texU + dz2 + dx2, texV + dz2, renderer.textureWidth, renderer.textureHeight);
        this.quadList[3] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, texU + dz2 + dx2, texV + dz2, texU + dz2 + dx2 + dx2, texV, renderer.textureWidth, renderer.textureHeight);
        this.quadList[4] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, texU + dz2, texV + dz2, texU + dz2 + dx2, texV + dz2 + dy2, renderer.textureWidth, renderer.textureHeight);
        this.quadList[5] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6}, texU + dz2 + dx2 + dz2, texV + dz2, texU + dz2 + dx2 + dz2 + dx2, texV + dz2 + dy2, renderer.textureWidth, renderer.textureHeight);
        if (mirror) {
            TexturedQuad[] arrtexturedQuad = this.quadList;
            int n2 = this.quadList.length;
            for (int i2 = 0; i2 < n2; ++i2) {
                TexturedQuad texturedquad = arrtexturedQuad[i2];
                texturedquad.flipFace();
            }
        }
    }

    public void render(BufferBuilder renderer, float scale) {
        TexturedQuad[] arrtexturedQuad = this.quadList;
        int n2 = this.quadList.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            TexturedQuad texturedquad = arrtexturedQuad[i2];
            if (texturedquad == null) continue;
            texturedquad.draw(renderer, scale);
        }
    }

    public ModelBox setBoxName(String name) {
        this.boxName = name;
        return this;
    }
}

