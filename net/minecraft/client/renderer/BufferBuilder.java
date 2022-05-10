package net.minecraft.client.renderer;

import com.google.common.primitives.Floats;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import optifine.Config;
import optifine.RenderEnv;
import optifine.TextureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import shadersmod.client.SVertexBuilder;

public class BufferBuilder {
    private static final Logger LOGGER = LogManager.getLogger();
    private ByteBuffer byteBuffer;
    public IntBuffer rawIntBuffer;
    private ShortBuffer rawShortBuffer;
    public FloatBuffer rawFloatBuffer;
    public int vertexCount;
    private VertexFormatElement vertexFormatElement;
    private int vertexFormatIndex;
    private boolean noColor;
    public int drawMode;
    private double xOffset;
    private double yOffset;
    private double zOffset;
    private VertexFormat vertexFormat;
    private boolean isDrawing;
    private BlockRenderLayer blockLayer = null;
    private boolean[] drawnIcons = new boolean[256];
    private TextureAtlasSprite[] quadSprites = null;
    private TextureAtlasSprite[] quadSpritesPrev = null;
    private TextureAtlasSprite quadSprite = null;
    public SVertexBuilder sVertexBuilder;
    public RenderEnv renderEnv = null;

    public BufferBuilder(int bufferSizeIn) {
        if (Config.isShaders()) {
            bufferSizeIn *= 2;
        }
        this.byteBuffer = GLAllocation.createDirectByteBuffer(bufferSizeIn * 4);
        this.rawIntBuffer = this.byteBuffer.asIntBuffer();
        this.rawShortBuffer = this.byteBuffer.asShortBuffer();
        this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
        SVertexBuilder.initVertexBuilder(this);
    }

    private void growBuffer(int p_181670_1_) {
        if (Config.isShaders()) {
            p_181670_1_ *= 2;
        }
        if (MathHelper.roundUp(p_181670_1_, 4) / 4 > this.rawIntBuffer.remaining() || this.vertexCount * this.vertexFormat.getNextOffset() + p_181670_1_ > this.byteBuffer.capacity()) {
            int i2 = this.byteBuffer.capacity();
            int j2 = i2 + MathHelper.roundUp(p_181670_1_, 0x200000);
            LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", (Object)i2, (Object)j2);
            int k2 = this.rawIntBuffer.position();
            ByteBuffer bytebuffer = GLAllocation.createDirectByteBuffer(j2);
            this.byteBuffer.position(0);
            bytebuffer.put(this.byteBuffer);
            bytebuffer.rewind();
            this.byteBuffer = bytebuffer;
            this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
            this.rawIntBuffer = this.byteBuffer.asIntBuffer();
            this.rawIntBuffer.position(k2);
            this.rawShortBuffer = this.byteBuffer.asShortBuffer();
            this.rawShortBuffer.position(k2 << 1);
            if (this.quadSprites != null) {
                TextureAtlasSprite[] atextureatlassprite = this.quadSprites;
                int l2 = this.getBufferQuadSize();
                this.quadSprites = new TextureAtlasSprite[l2];
                System.arraycopy(atextureatlassprite, 0, this.quadSprites, 0, Math.min(atextureatlassprite.length, this.quadSprites.length));
                this.quadSpritesPrev = null;
            }
        }
    }

    public void sortVertexData(float p_181674_1_, float p_181674_2_, float p_181674_3_) {
        int i2 = this.vertexCount / 4;
        final float[] afloat = new float[i2];
        for (int j2 = 0; j2 < i2; ++j2) {
            afloat[j2] = BufferBuilder.getDistanceSq(this.rawFloatBuffer, (float)((double)p_181674_1_ + this.xOffset), (float)((double)p_181674_2_ + this.yOffset), (float)((double)p_181674_3_ + this.zOffset), this.vertexFormat.getIntegerSize(), j2 * this.vertexFormat.getNextOffset());
        }
        Integer[] ainteger = new Integer[i2];
        for (int k2 = 0; k2 < ainteger.length; ++k2) {
            ainteger[k2] = k2;
        }
        Arrays.sort(ainteger, new Comparator<Integer>(){

            @Override
            public int compare(Integer p_compare_1_, Integer p_compare_2_) {
                return Floats.compare(afloat[p_compare_2_], afloat[p_compare_1_]);
            }
        });
        BitSet bitset = new BitSet();
        int l2 = this.vertexFormat.getNextOffset();
        int[] aint = new int[l2];
        int i1 = bitset.nextClearBit(0);
        while (i1 < ainteger.length) {
            int j1 = ainteger[i1];
            if (j1 != i1) {
                this.rawIntBuffer.limit(j1 * l2 + l2);
                this.rawIntBuffer.position(j1 * l2);
                this.rawIntBuffer.get(aint);
                int k1 = j1;
                int l1 = ainteger[j1];
                while (k1 != i1) {
                    this.rawIntBuffer.limit(l1 * l2 + l2);
                    this.rawIntBuffer.position(l1 * l2);
                    IntBuffer intbuffer = this.rawIntBuffer.slice();
                    this.rawIntBuffer.limit(k1 * l2 + l2);
                    this.rawIntBuffer.position(k1 * l2);
                    this.rawIntBuffer.put(intbuffer);
                    bitset.set(k1);
                    k1 = l1;
                    l1 = ainteger[l1];
                }
                this.rawIntBuffer.limit(i1 * l2 + l2);
                this.rawIntBuffer.position(i1 * l2);
                this.rawIntBuffer.put(aint);
            }
            bitset.set(i1);
            i1 = bitset.nextClearBit(i1 + 1);
        }
        this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
        this.rawIntBuffer.position(this.getBufferSize());
        if (this.quadSprites != null) {
            TextureAtlasSprite[] atextureatlassprite = new TextureAtlasSprite[this.vertexCount / 4];
            int i22 = this.vertexFormat.getNextOffset() / 4 * 4;
            for (int j2 = 0; j2 < ainteger.length; ++j2) {
                int k2 = ainteger[j2];
                atextureatlassprite[j2] = this.quadSprites[k2];
            }
            System.arraycopy(atextureatlassprite, 0, this.quadSprites, 0, atextureatlassprite.length);
        }
    }

    public State getVertexState() {
        this.rawIntBuffer.rewind();
        int i2 = this.getBufferSize();
        this.rawIntBuffer.limit(i2);
        int[] aint = new int[i2];
        this.rawIntBuffer.get(aint);
        this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
        this.rawIntBuffer.position(i2);
        TextureAtlasSprite[] atextureatlassprite = null;
        if (this.quadSprites != null) {
            int j2 = this.vertexCount / 4;
            atextureatlassprite = new TextureAtlasSprite[j2];
            System.arraycopy(this.quadSprites, 0, atextureatlassprite, 0, j2);
        }
        return new State(aint, new VertexFormat(this.vertexFormat), atextureatlassprite);
    }

    public int getBufferSize() {
        return this.vertexCount * this.vertexFormat.getIntegerSize();
    }

    private static float getDistanceSq(FloatBuffer p_181665_0_, float p_181665_1_, float p_181665_2_, float p_181665_3_, int p_181665_4_, int p_181665_5_) {
        float f2 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 0);
        float f1 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 1);
        float f22 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 2);
        float f3 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 0);
        float f4 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 1);
        float f5 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 2);
        float f6 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 0);
        float f7 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 1);
        float f8 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 2);
        float f9 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 0);
        float f10 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 1);
        float f11 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 2);
        float f12 = (f2 + f3 + f6 + f9) * 0.25f - p_181665_1_;
        float f13 = (f1 + f4 + f7 + f10) * 0.25f - p_181665_2_;
        float f14 = (f22 + f5 + f8 + f11) * 0.25f - p_181665_3_;
        return f12 * f12 + f13 * f13 + f14 * f14;
    }

    public void setVertexState(State state) {
        this.rawIntBuffer.clear();
        this.growBuffer(state.getRawBuffer().length * 4);
        this.rawIntBuffer.put(state.getRawBuffer());
        this.vertexCount = state.getVertexCount();
        this.vertexFormat = new VertexFormat(state.getVertexFormat());
        if (state.stateQuadSprites != null) {
            if (this.quadSprites == null) {
                this.quadSprites = this.quadSpritesPrev;
            }
            if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
                this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
            }
            TextureAtlasSprite[] atextureatlassprite = state.stateQuadSprites;
            System.arraycopy(atextureatlassprite, 0, this.quadSprites, 0, atextureatlassprite.length);
        } else {
            if (this.quadSprites != null) {
                this.quadSpritesPrev = this.quadSprites;
            }
            this.quadSprites = null;
        }
    }

    public void reset() {
        this.vertexCount = 0;
        this.vertexFormatElement = null;
        this.vertexFormatIndex = 0;
        this.quadSprite = null;
    }

    public void begin(int glMode, VertexFormat format) {
        if (this.isDrawing) {
            throw new IllegalStateException("Already building!");
        }
        this.isDrawing = true;
        this.reset();
        this.drawMode = glMode;
        this.vertexFormat = format;
        this.vertexFormatElement = format.getElement(this.vertexFormatIndex);
        this.noColor = false;
        this.byteBuffer.limit(this.byteBuffer.capacity());
        if (Config.isShaders()) {
            SVertexBuilder.endSetVertexFormat(this);
        }
        if (Config.isMultiTexture()) {
            if (this.blockLayer != null) {
                if (this.quadSprites == null) {
                    this.quadSprites = this.quadSpritesPrev;
                }
                if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
                    this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
                }
            }
        } else {
            if (this.quadSprites != null) {
                this.quadSpritesPrev = this.quadSprites;
            }
            this.quadSprites = null;
        }
    }

    public BufferBuilder tex(double u2, double v2) {
        if (this.quadSprite != null && this.quadSprites != null) {
            u2 = this.quadSprite.toSingleU((float)u2);
            v2 = this.quadSprite.toSingleV((float)v2);
            this.quadSprites[this.vertexCount / 4] = this.quadSprite;
        }
        int i2 = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT: {
                this.byteBuffer.putFloat(i2, (float)u2);
                this.byteBuffer.putFloat(i2 + 4, (float)v2);
                break;
            }
            case UINT: 
            case INT: {
                this.byteBuffer.putInt(i2, (int)u2);
                this.byteBuffer.putInt(i2 + 4, (int)v2);
                break;
            }
            case USHORT: 
            case SHORT: {
                this.byteBuffer.putShort(i2, (short)v2);
                this.byteBuffer.putShort(i2 + 2, (short)u2);
                break;
            }
            case UBYTE: 
            case BYTE: {
                this.byteBuffer.put(i2, (byte)v2);
                this.byteBuffer.put(i2 + 1, (byte)u2);
            }
        }
        this.nextVertexFormatIndex();
        return this;
    }

    public BufferBuilder lightmap(int p_187314_1_, int p_187314_2_) {
        int i2 = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT: {
                this.byteBuffer.putFloat(i2, p_187314_1_);
                this.byteBuffer.putFloat(i2 + 4, p_187314_2_);
                break;
            }
            case UINT: 
            case INT: {
                this.byteBuffer.putInt(i2, p_187314_1_);
                this.byteBuffer.putInt(i2 + 4, p_187314_2_);
                break;
            }
            case USHORT: 
            case SHORT: {
                this.byteBuffer.putShort(i2, (short)p_187314_2_);
                this.byteBuffer.putShort(i2 + 2, (short)p_187314_1_);
                break;
            }
            case UBYTE: 
            case BYTE: {
                this.byteBuffer.put(i2, (byte)p_187314_2_);
                this.byteBuffer.put(i2 + 1, (byte)p_187314_1_);
            }
        }
        this.nextVertexFormatIndex();
        return this;
    }

    public void putBrightness4(int p_178962_1_, int p_178962_2_, int p_178962_3_, int p_178962_4_) {
        int i2 = (this.vertexCount - 4) * this.vertexFormat.getIntegerSize() + this.vertexFormat.getUvOffsetById(1) / 4;
        int j2 = this.vertexFormat.getNextOffset() >> 2;
        this.rawIntBuffer.put(i2, p_178962_1_);
        this.rawIntBuffer.put(i2 + j2, p_178962_2_);
        this.rawIntBuffer.put(i2 + j2 * 2, p_178962_3_);
        this.rawIntBuffer.put(i2 + j2 * 3, p_178962_4_);
    }

    public void putPosition(double x2, double y2, double z2) {
        int i2 = this.vertexFormat.getIntegerSize();
        int j2 = (this.vertexCount - 4) * i2;
        for (int k2 = 0; k2 < 4; ++k2) {
            int l2 = j2 + k2 * i2;
            int i1 = l2 + 1;
            int j1 = i1 + 1;
            this.rawIntBuffer.put(l2, Float.floatToRawIntBits((float)(x2 + this.xOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(l2))));
            this.rawIntBuffer.put(i1, Float.floatToRawIntBits((float)(y2 + this.yOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(i1))));
            this.rawIntBuffer.put(j1, Float.floatToRawIntBits((float)(z2 + this.zOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(j1))));
        }
    }

    public int getColorIndex(int vertexIndex) {
        return ((this.vertexCount - vertexIndex) * this.vertexFormat.getNextOffset() + this.vertexFormat.getColorOffset()) / 4;
    }

    public void putColorMultiplier(float red, float green, float blue, int vertexIndex) {
        int i2 = this.getColorIndex(vertexIndex);
        int j2 = -1;
        if (!this.noColor) {
            j2 = this.rawIntBuffer.get(i2);
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                int k2 = (int)((float)(j2 & 0xFF) * red);
                int l2 = (int)((float)(j2 >> 8 & 0xFF) * green);
                int i1 = (int)((float)(j2 >> 16 & 0xFF) * blue);
                j2 &= 0xFF000000;
                j2 = j2 | i1 << 16 | l2 << 8 | k2;
            } else {
                int j1 = (int)((float)(j2 >> 24 & 0xFF) * red);
                int k1 = (int)((float)(j2 >> 16 & 0xFF) * green);
                int l1 = (int)((float)(j2 >> 8 & 0xFF) * blue);
                j2 &= 0xFF;
                j2 = j2 | j1 << 24 | k1 << 16 | l1 << 8;
            }
        }
        this.rawIntBuffer.put(i2, j2);
    }

    private void func_192836_a(int p_192836_1_, int p_192836_2_) {
        int i2 = this.getColorIndex(p_192836_2_);
        int j2 = p_192836_1_ >> 16 & 0xFF;
        int k2 = p_192836_1_ >> 8 & 0xFF;
        int l2 = p_192836_1_ & 0xFF;
        this.putColorRGBA(i2, j2, k2, l2);
    }

    public void putColorRGB_F(float red, float green, float blue, int vertexIndex) {
        int i2 = this.getColorIndex(vertexIndex);
        int j2 = MathHelper.clamp((int)(red * 255.0f), 0, 255);
        int k2 = MathHelper.clamp((int)(green * 255.0f), 0, 255);
        int l2 = MathHelper.clamp((int)(blue * 255.0f), 0, 255);
        this.putColorRGBA(i2, j2, k2, l2);
    }

    public void putColorRGBA(int index, int red, int green, int blue) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.rawIntBuffer.put(index, 0xFF000000 | blue << 16 | green << 8 | red);
        } else {
            this.rawIntBuffer.put(index, red << 24 | green << 16 | blue << 8 | 0xFF);
        }
    }

    public void noColor() {
        this.noColor = true;
    }

    public BufferBuilder color(float red, float green, float blue, float alpha) {
        return this.color((int)(red * 255.0f), (int)(green * 255.0f), (int)(blue * 255.0f), (int)(alpha * 255.0f));
    }

    public BufferBuilder color(int red, int green, int blue, int alpha) {
        if (this.noColor) {
            return this;
        }
        int i2 = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT: {
                this.byteBuffer.putFloat(i2, (float)red / 255.0f);
                this.byteBuffer.putFloat(i2 + 4, (float)green / 255.0f);
                this.byteBuffer.putFloat(i2 + 8, (float)blue / 255.0f);
                this.byteBuffer.putFloat(i2 + 12, (float)alpha / 255.0f);
                break;
            }
            case UINT: 
            case INT: {
                this.byteBuffer.putFloat(i2, red);
                this.byteBuffer.putFloat(i2 + 4, green);
                this.byteBuffer.putFloat(i2 + 8, blue);
                this.byteBuffer.putFloat(i2 + 12, alpha);
                break;
            }
            case USHORT: 
            case SHORT: {
                this.byteBuffer.putShort(i2, (short)red);
                this.byteBuffer.putShort(i2 + 2, (short)green);
                this.byteBuffer.putShort(i2 + 4, (short)blue);
                this.byteBuffer.putShort(i2 + 6, (short)alpha);
                break;
            }
            case UBYTE: 
            case BYTE: {
                if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                    this.byteBuffer.put(i2, (byte)red);
                    this.byteBuffer.put(i2 + 1, (byte)green);
                    this.byteBuffer.put(i2 + 2, (byte)blue);
                    this.byteBuffer.put(i2 + 3, (byte)alpha);
                    break;
                }
                this.byteBuffer.put(i2, (byte)alpha);
                this.byteBuffer.put(i2 + 1, (byte)blue);
                this.byteBuffer.put(i2 + 2, (byte)green);
                this.byteBuffer.put(i2 + 3, (byte)red);
            }
        }
        this.nextVertexFormatIndex();
        return this;
    }

    public void addVertexData(int[] vertexData) {
        if (Config.isShaders()) {
            SVertexBuilder.beginAddVertexData(this, vertexData);
        }
        this.growBuffer(vertexData.length * 4);
        this.rawIntBuffer.position(this.getBufferSize());
        this.rawIntBuffer.put(vertexData);
        this.vertexCount += vertexData.length / this.vertexFormat.getIntegerSize();
        if (Config.isShaders()) {
            SVertexBuilder.endAddVertexData(this);
        }
    }

    public void endVertex() {
        ++this.vertexCount;
        this.growBuffer(this.vertexFormat.getNextOffset());
        this.vertexFormatIndex = 0;
        this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
        if (Config.isShaders()) {
            SVertexBuilder.endAddVertex(this);
        }
    }

    public BufferBuilder pos(double x2, double y2, double z2) {
        if (Config.isShaders()) {
            SVertexBuilder.beginAddVertex(this);
        }
        int i2 = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT: {
                this.byteBuffer.putFloat(i2, (float)(x2 + this.xOffset));
                this.byteBuffer.putFloat(i2 + 4, (float)(y2 + this.yOffset));
                this.byteBuffer.putFloat(i2 + 8, (float)(z2 + this.zOffset));
                break;
            }
            case UINT: 
            case INT: {
                this.byteBuffer.putInt(i2, Float.floatToRawIntBits((float)(x2 + this.xOffset)));
                this.byteBuffer.putInt(i2 + 4, Float.floatToRawIntBits((float)(y2 + this.yOffset)));
                this.byteBuffer.putInt(i2 + 8, Float.floatToRawIntBits((float)(z2 + this.zOffset)));
                break;
            }
            case USHORT: 
            case SHORT: {
                this.byteBuffer.putShort(i2, (short)(x2 + this.xOffset));
                this.byteBuffer.putShort(i2 + 2, (short)(y2 + this.yOffset));
                this.byteBuffer.putShort(i2 + 4, (short)(z2 + this.zOffset));
                break;
            }
            case UBYTE: 
            case BYTE: {
                this.byteBuffer.put(i2, (byte)(x2 + this.xOffset));
                this.byteBuffer.put(i2 + 1, (byte)(y2 + this.yOffset));
                this.byteBuffer.put(i2 + 2, (byte)(z2 + this.zOffset));
            }
        }
        this.nextVertexFormatIndex();
        return this;
    }

    public void putNormal(float x2, float y2, float z2) {
        int i2 = (byte)(x2 * 127.0f) & 0xFF;
        int j2 = (byte)(y2 * 127.0f) & 0xFF;
        int k2 = (byte)(z2 * 127.0f) & 0xFF;
        int l2 = i2 | j2 << 8 | k2 << 16;
        int i1 = this.vertexFormat.getNextOffset() >> 2;
        int j1 = (this.vertexCount - 4) * i1 + this.vertexFormat.getNormalOffset() / 4;
        this.rawIntBuffer.put(j1, l2);
        this.rawIntBuffer.put(j1 + i1, l2);
        this.rawIntBuffer.put(j1 + i1 * 2, l2);
        this.rawIntBuffer.put(j1 + i1 * 3, l2);
    }

    private void nextVertexFormatIndex() {
        ++this.vertexFormatIndex;
        this.vertexFormatIndex %= this.vertexFormat.getElementCount();
        this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
        if (this.vertexFormatElement.getUsage() == VertexFormatElement.EnumUsage.PADDING) {
            this.nextVertexFormatIndex();
        }
    }

    public BufferBuilder normal(float x2, float y2, float z2) {
        int i2 = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT: {
                this.byteBuffer.putFloat(i2, x2);
                this.byteBuffer.putFloat(i2 + 4, y2);
                this.byteBuffer.putFloat(i2 + 8, z2);
                break;
            }
            case UINT: 
            case INT: {
                this.byteBuffer.putInt(i2, (int)x2);
                this.byteBuffer.putInt(i2 + 4, (int)y2);
                this.byteBuffer.putInt(i2 + 8, (int)z2);
                break;
            }
            case USHORT: 
            case SHORT: {
                this.byteBuffer.putShort(i2, (short)((int)(x2 * 32767.0f) & 0xFFFF));
                this.byteBuffer.putShort(i2 + 2, (short)((int)(y2 * 32767.0f) & 0xFFFF));
                this.byteBuffer.putShort(i2 + 4, (short)((int)(z2 * 32767.0f) & 0xFFFF));
                break;
            }
            case UBYTE: 
            case BYTE: {
                this.byteBuffer.put(i2, (byte)((int)(x2 * 127.0f) & 0xFF));
                this.byteBuffer.put(i2 + 1, (byte)((int)(y2 * 127.0f) & 0xFF));
                this.byteBuffer.put(i2 + 2, (byte)((int)(z2 * 127.0f) & 0xFF));
            }
        }
        this.nextVertexFormatIndex();
        return this;
    }

    public void setTranslation(double x2, double y2, double z2) {
        this.xOffset = x2;
        this.yOffset = y2;
        this.zOffset = z2;
    }

    public void finishDrawing() {
        if (!this.isDrawing) {
            throw new IllegalStateException("Not building!");
        }
        this.isDrawing = false;
        this.byteBuffer.position(0);
        this.byteBuffer.limit(this.getBufferSize() * 4);
    }

    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public int getDrawMode() {
        return this.drawMode;
    }

    public void putColor4(int argb) {
        for (int i2 = 0; i2 < 4; ++i2) {
            this.func_192836_a(argb, i2 + 1);
        }
    }

    public void putColorRGB_F4(float red, float green, float blue) {
        for (int i2 = 0; i2 < 4; ++i2) {
            this.putColorRGB_F(red, green, blue, i2 + 1);
        }
    }

    public void putSprite(TextureAtlasSprite p_putSprite_1_) {
        if (this.quadSprites != null) {
            int i2 = this.vertexCount / 4;
            this.quadSprites[i2 - 1] = p_putSprite_1_;
        }
    }

    public void setSprite(TextureAtlasSprite p_setSprite_1_) {
        if (this.quadSprites != null) {
            this.quadSprite = p_setSprite_1_;
        }
    }

    public boolean isMultiTexture() {
        return this.quadSprites != null;
    }

    public void drawMultiTexture() {
        if (this.quadSprites != null) {
            int i2 = Config.getMinecraft().getTextureMapBlocks().getCountRegisteredSprites();
            if (this.drawnIcons.length <= i2) {
                this.drawnIcons = new boolean[i2 + 1];
            }
            Arrays.fill(this.drawnIcons, false);
            int j2 = 0;
            int k2 = -1;
            int l2 = this.vertexCount / 4;
            for (int i1 = 0; i1 < l2; ++i1) {
                int j1;
                TextureAtlasSprite textureatlassprite = this.quadSprites[i1];
                if (textureatlassprite == null || this.drawnIcons[j1 = textureatlassprite.getIndexInMap()]) continue;
                if (textureatlassprite == TextureUtils.iconGrassSideOverlay) {
                    if (k2 >= 0) continue;
                    k2 = i1;
                    continue;
                }
                i1 = this.drawForIcon(textureatlassprite, i1) - 1;
                ++j2;
                if (this.blockLayer == BlockRenderLayer.TRANSLUCENT) continue;
                this.drawnIcons[j1] = true;
            }
            if (k2 >= 0) {
                this.drawForIcon(TextureUtils.iconGrassSideOverlay, k2);
                ++j2;
            }
            if (j2 > 0) {
                // empty if block
            }
        }
    }

    private int drawForIcon(TextureAtlasSprite p_drawForIcon_1_, int p_drawForIcon_2_) {
        GL11.glBindTexture(3553, p_drawForIcon_1_.glSpriteTextureId);
        int i2 = -1;
        int j2 = -1;
        int k2 = this.vertexCount / 4;
        for (int l2 = p_drawForIcon_2_; l2 < k2; ++l2) {
            TextureAtlasSprite textureatlassprite = this.quadSprites[l2];
            if (textureatlassprite == p_drawForIcon_1_) {
                if (j2 >= 0) continue;
                j2 = l2;
                continue;
            }
            if (j2 < 0) continue;
            this.draw(j2, l2);
            if (this.blockLayer == BlockRenderLayer.TRANSLUCENT) {
                return l2;
            }
            j2 = -1;
            if (i2 >= 0) continue;
            i2 = l2;
        }
        if (j2 >= 0) {
            this.draw(j2, k2);
        }
        if (i2 < 0) {
            i2 = k2;
        }
        return i2;
    }

    private void draw(int p_draw_1_, int p_draw_2_) {
        int i2 = p_draw_2_ - p_draw_1_;
        if (i2 > 0) {
            int j2 = p_draw_1_ * 4;
            int k2 = i2 * 4;
            GL11.glDrawArrays(this.drawMode, j2, k2);
        }
    }

    public void setBlockLayer(BlockRenderLayer p_setBlockLayer_1_) {
        this.blockLayer = p_setBlockLayer_1_;
        if (p_setBlockLayer_1_ == null) {
            if (this.quadSprites != null) {
                this.quadSpritesPrev = this.quadSprites;
            }
            this.quadSprites = null;
            this.quadSprite = null;
        }
    }

    private int getBufferQuadSize() {
        int i2 = this.rawIntBuffer.capacity() * 4 / (this.vertexFormat.getIntegerSize() * 4);
        return i2;
    }

    public RenderEnv getRenderEnv(IBlockAccess p_getRenderEnv_1_, IBlockState p_getRenderEnv_2_, BlockPos p_getRenderEnv_3_) {
        if (this.renderEnv == null) {
            this.renderEnv = new RenderEnv(p_getRenderEnv_1_, p_getRenderEnv_2_, p_getRenderEnv_3_);
            return this.renderEnv;
        }
        this.renderEnv.reset(p_getRenderEnv_1_, p_getRenderEnv_2_, p_getRenderEnv_3_);
        return this.renderEnv;
    }

    public boolean isDrawing() {
        return this.isDrawing;
    }

    public double getXOffset() {
        return this.xOffset;
    }

    public double getYOffset() {
        return this.yOffset;
    }

    public double getZOffset() {
        return this.zOffset;
    }

    public void putColorRGBA(int p_putColorRGBA_1_, int p_putColorRGBA_2_, int p_putColorRGBA_3_, int p_putColorRGBA_4_, int p_putColorRGBA_5_) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.rawIntBuffer.put(p_putColorRGBA_1_, p_putColorRGBA_5_ << 24 | p_putColorRGBA_4_ << 16 | p_putColorRGBA_3_ << 8 | p_putColorRGBA_2_);
        } else {
            this.rawIntBuffer.put(p_putColorRGBA_1_, p_putColorRGBA_2_ << 24 | p_putColorRGBA_3_ << 16 | p_putColorRGBA_4_ << 8 | p_putColorRGBA_5_);
        }
    }

    public boolean isColorDisabled() {
        return this.noColor;
    }

    public class State {
        private final int[] stateRawBuffer;
        private final VertexFormat stateVertexFormat;
        private TextureAtlasSprite[] stateQuadSprites;

        public State(int[] p_i5_2_, VertexFormat p_i5_3_, TextureAtlasSprite[] p_i5_4_) {
            this.stateRawBuffer = p_i5_2_;
            this.stateVertexFormat = p_i5_3_;
            this.stateQuadSprites = p_i5_4_;
        }

        public State(int[] buffer, VertexFormat format) {
            this.stateRawBuffer = buffer;
            this.stateVertexFormat = format;
        }

        public int[] getRawBuffer() {
            return this.stateRawBuffer;
        }

        public int getVertexCount() {
            return this.stateRawBuffer.length / this.stateVertexFormat.getIntegerSize();
        }

        public VertexFormat getVertexFormat() {
            return this.stateVertexFormat;
        }
    }
}

