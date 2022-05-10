package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.MathHelper;
import optifine.MathUtils;

public class Stitcher {
    private final int mipmapLevelStitcher;
    private final Set<Holder> setStitchHolders = Sets.newHashSetWithExpectedSize(256);
    private final List<Slot> stitchSlots = Lists.newArrayListWithCapacity(256);
    private int currentWidth;
    private int currentHeight;
    private final int maxWidth;
    private final int maxHeight;
    private final int maxTileDimension;

    public Stitcher(int maxWidthIn, int maxHeightIn, int maxTileDimensionIn, int mipmapLevelStitcherIn) {
        this.mipmapLevelStitcher = mipmapLevelStitcherIn;
        this.maxWidth = maxWidthIn;
        this.maxHeight = maxHeightIn;
        this.maxTileDimension = maxTileDimensionIn;
    }

    public int getCurrentWidth() {
        return this.currentWidth;
    }

    public int getCurrentHeight() {
        return this.currentHeight;
    }

    public void addSprite(TextureAtlasSprite textureAtlas) {
        Holder stitcher$holder = new Holder(textureAtlas, this.mipmapLevelStitcher);
        if (this.maxTileDimension > 0) {
            stitcher$holder.setNewDimension(this.maxTileDimension);
        }
        this.setStitchHolders.add(stitcher$holder);
    }

    public void doStitch() {
        Object[] astitcher$holder = this.setStitchHolders.toArray(new Holder[this.setStitchHolders.size()]);
        Arrays.sort(astitcher$holder);
        Object[] arrobject = astitcher$holder;
        int n2 = astitcher$holder.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            Object stitcher$holder = arrobject[i2];
            if (this.allocateSlot((Holder)stitcher$holder)) continue;
            String s2 = String.format("Unable to fit: %s, size: %dx%d, atlas: %dx%d, atlasMax: %dx%d - Maybe try a lower resolution resourcepack?", ((Holder)stitcher$holder).getAtlasSprite().getIconName(), ((Holder)stitcher$holder).getAtlasSprite().getIconWidth(), ((Holder)stitcher$holder).getAtlasSprite().getIconHeight(), this.currentWidth, this.currentHeight, this.maxWidth, this.maxHeight);
            throw new StitcherException((Holder)stitcher$holder, s2);
        }
        this.currentWidth = MathHelper.smallestEncompassingPowerOfTwo(this.currentWidth);
        this.currentHeight = MathHelper.smallestEncompassingPowerOfTwo(this.currentHeight);
    }

    public List<TextureAtlasSprite> getStichSlots() {
        ArrayList<Slot> list = Lists.newArrayList();
        for (Slot stitcher$slot : this.stitchSlots) {
            stitcher$slot.getAllStitchSlots(list);
        }
        ArrayList<TextureAtlasSprite> list1 = Lists.newArrayList();
        for (Slot stitcher$slot1 : list) {
            Holder stitcher$holder = stitcher$slot1.getStitchHolder();
            TextureAtlasSprite textureatlassprite = stitcher$holder.getAtlasSprite();
            textureatlassprite.initSprite(this.currentWidth, this.currentHeight, stitcher$slot1.getOriginX(), stitcher$slot1.getOriginY(), stitcher$holder.isRotated());
            list1.add(textureatlassprite);
        }
        return list1;
    }

    private static int getMipmapDimension(int p_147969_0_, int p_147969_1_) {
        return (p_147969_0_ >> p_147969_1_) + ((p_147969_0_ & (1 << p_147969_1_) - 1) == 0 ? 0 : 1) << p_147969_1_;
    }

    private boolean allocateSlot(Holder p_94310_1_) {
        TextureAtlasSprite textureatlassprite = p_94310_1_.getAtlasSprite();
        boolean flag = textureatlassprite.getIconWidth() != textureatlassprite.getIconHeight();
        for (int i2 = 0; i2 < this.stitchSlots.size(); ++i2) {
            if (this.stitchSlots.get(i2).addSlot(p_94310_1_)) {
                return true;
            }
            if (!flag) continue;
            p_94310_1_.rotate();
            if (this.stitchSlots.get(i2).addSlot(p_94310_1_)) {
                return true;
            }
            p_94310_1_.rotate();
        }
        return this.expandAndAllocateSlot(p_94310_1_);
    }

    private boolean expandAndAllocateSlot(Holder p_94311_1_) {
        Slot stitcher$slot;
        boolean flag2;
        boolean flag1;
        int i2 = Math.min(p_94311_1_.getWidth(), p_94311_1_.getHeight());
        int j2 = Math.max(p_94311_1_.getWidth(), p_94311_1_.getHeight());
        int k2 = MathHelper.smallestEncompassingPowerOfTwo(this.currentWidth);
        int l2 = MathHelper.smallestEncompassingPowerOfTwo(this.currentHeight);
        int i1 = MathHelper.smallestEncompassingPowerOfTwo(this.currentWidth + i2);
        int j1 = MathHelper.smallestEncompassingPowerOfTwo(this.currentHeight + i2);
        boolean flag = i1 <= this.maxWidth;
        boolean bl2 = flag1 = j1 <= this.maxHeight;
        if (!flag && !flag1) {
            return false;
        }
        int k1 = MathUtils.roundDownToPowerOfTwo(this.currentHeight);
        boolean bl3 = flag2 = flag && i1 <= 2 * k1;
        if (this.currentWidth == 0 && this.currentHeight == 0) {
            flag2 = true;
        }
        if (flag2) {
            if (p_94311_1_.getWidth() > p_94311_1_.getHeight()) {
                p_94311_1_.rotate();
            }
            if (this.currentHeight == 0) {
                this.currentHeight = p_94311_1_.getHeight();
            }
            stitcher$slot = new Slot(this.currentWidth, 0, p_94311_1_.getWidth(), this.currentHeight);
            this.currentWidth += p_94311_1_.getWidth();
        } else {
            stitcher$slot = new Slot(0, this.currentHeight, this.currentWidth, p_94311_1_.getHeight());
            this.currentHeight += p_94311_1_.getHeight();
        }
        stitcher$slot.addSlot(p_94311_1_);
        this.stitchSlots.add(stitcher$slot);
        return true;
    }

    public static class Holder
    implements Comparable<Holder> {
        private final TextureAtlasSprite theTexture;
        private final int width;
        private final int height;
        private final int mipmapLevelHolder;
        private boolean rotated;
        private float scaleFactor = 1.0f;

        public Holder(TextureAtlasSprite theTextureIn, int mipmapLevelHolderIn) {
            this.theTexture = theTextureIn;
            this.width = theTextureIn.getIconWidth();
            this.height = theTextureIn.getIconHeight();
            this.mipmapLevelHolder = mipmapLevelHolderIn;
            this.rotated = Stitcher.getMipmapDimension(this.height, mipmapLevelHolderIn) > Stitcher.getMipmapDimension(this.width, mipmapLevelHolderIn);
        }

        public TextureAtlasSprite getAtlasSprite() {
            return this.theTexture;
        }

        public int getWidth() {
            int i2 = this.rotated ? this.height : this.width;
            return Stitcher.getMipmapDimension((int)((float)i2 * this.scaleFactor), this.mipmapLevelHolder);
        }

        public int getHeight() {
            int i2 = this.rotated ? this.width : this.height;
            return Stitcher.getMipmapDimension((int)((float)i2 * this.scaleFactor), this.mipmapLevelHolder);
        }

        public void rotate() {
            this.rotated = !this.rotated;
        }

        public boolean isRotated() {
            return this.rotated;
        }

        public void setNewDimension(int p_94196_1_) {
            if (this.width > p_94196_1_ && this.height > p_94196_1_) {
                this.scaleFactor = (float)p_94196_1_ / (float)Math.min(this.width, this.height);
            }
        }

        public String toString() {
            return "Holder{width=" + this.width + ", height=" + this.height + ", name=" + this.theTexture.getIconName() + '}';
        }

        @Override
        public int compareTo(Holder p_compareTo_1_) {
            int i2;
            if (this.getHeight() == p_compareTo_1_.getHeight()) {
                if (this.getWidth() == p_compareTo_1_.getWidth()) {
                    if (this.theTexture.getIconName() == null) {
                        return p_compareTo_1_.theTexture.getIconName() == null ? 0 : -1;
                    }
                    return this.theTexture.getIconName().compareTo(p_compareTo_1_.theTexture.getIconName());
                }
                i2 = this.getWidth() < p_compareTo_1_.getWidth() ? 1 : -1;
            } else {
                i2 = this.getHeight() < p_compareTo_1_.getHeight() ? 1 : -1;
            }
            return i2;
        }
    }

    public static class Slot {
        private final int originX;
        private final int originY;
        private final int width;
        private final int height;
        private List<Slot> subSlots;
        private Holder holder;

        public Slot(int originXIn, int originYIn, int widthIn, int heightIn) {
            this.originX = originXIn;
            this.originY = originYIn;
            this.width = widthIn;
            this.height = heightIn;
        }

        public Holder getStitchHolder() {
            return this.holder;
        }

        public int getOriginX() {
            return this.originX;
        }

        public int getOriginY() {
            return this.originY;
        }

        public boolean addSlot(Holder holderIn) {
            if (this.holder != null) {
                return false;
            }
            int i2 = holderIn.getWidth();
            int j2 = holderIn.getHeight();
            if (i2 <= this.width && j2 <= this.height) {
                if (i2 == this.width && j2 == this.height) {
                    this.holder = holderIn;
                    return true;
                }
                if (this.subSlots == null) {
                    this.subSlots = Lists.newArrayListWithCapacity(1);
                    this.subSlots.add(new Slot(this.originX, this.originY, i2, j2));
                    int k2 = this.width - i2;
                    int l2 = this.height - j2;
                    if (l2 > 0 && k2 > 0) {
                        int j1;
                        int i1 = Math.max(this.height, k2);
                        if (i1 >= (j1 = Math.max(this.width, l2))) {
                            this.subSlots.add(new Slot(this.originX, this.originY + j2, i2, l2));
                            this.subSlots.add(new Slot(this.originX + i2, this.originY, k2, this.height));
                        } else {
                            this.subSlots.add(new Slot(this.originX + i2, this.originY, k2, j2));
                            this.subSlots.add(new Slot(this.originX, this.originY + j2, this.width, l2));
                        }
                    } else if (k2 == 0) {
                        this.subSlots.add(new Slot(this.originX, this.originY + j2, i2, l2));
                    } else if (l2 == 0) {
                        this.subSlots.add(new Slot(this.originX + i2, this.originY, k2, j2));
                    }
                }
                for (Slot stitcher$slot : this.subSlots) {
                    if (!stitcher$slot.addSlot(holderIn)) continue;
                    return true;
                }
                return false;
            }
            return false;
        }

        public void getAllStitchSlots(List<Slot> p_94184_1_) {
            if (this.holder != null) {
                p_94184_1_.add(this);
            } else if (this.subSlots != null) {
                for (Slot stitcher$slot : this.subSlots) {
                    stitcher$slot.getAllStitchSlots(p_94184_1_);
                }
            }
        }

        public String toString() {
            return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
        }
    }
}

