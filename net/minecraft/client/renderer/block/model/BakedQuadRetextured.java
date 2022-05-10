package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BakedQuadRetextured
extends BakedQuad {
    private final TextureAtlasSprite texture;
    private final TextureAtlasSprite spriteOld;

    public BakedQuadRetextured(BakedQuad quad, TextureAtlasSprite textureIn) {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.tintIndex, FaceBakery.getFacingFromVertexData(quad.getVertexData()), textureIn);
        this.texture = textureIn;
        this.format = quad.format;
        this.applyDiffuseLighting = quad.applyDiffuseLighting;
        this.spriteOld = quad.getSprite();
        this.remapQuad();
        this.fixVertexData();
    }

    private void remapQuad() {
        for (int i2 = 0; i2 < 4; ++i2) {
            int j2 = this.format.getIntegerSize() * i2;
            int k2 = this.format.getUvOffsetById(0) / 4;
            this.vertexData[j2 + k2] = Float.floatToRawIntBits(this.texture.getInterpolatedU(this.spriteOld.getUnInterpolatedU(Float.intBitsToFloat(this.vertexData[j2 + k2]))));
            this.vertexData[j2 + k2 + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV(this.spriteOld.getUnInterpolatedV(Float.intBitsToFloat(this.vertexData[j2 + k2 + 1]))));
        }
    }
}

