package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;

public class BannerTextures {
    public static final Cache BANNER_DESIGNS = new Cache("B", new ResourceLocation("textures/entity/banner_base.png"), "textures/entity/banner/");
    public static final Cache SHIELD_DESIGNS = new Cache("S", new ResourceLocation("textures/entity/shield_base.png"), "textures/entity/shield/");
    public static final ResourceLocation SHIELD_BASE_TEXTURE = new ResourceLocation("textures/entity/shield_base_nopattern.png");
    public static final ResourceLocation BANNER_BASE_TEXTURE = new ResourceLocation("textures/entity/banner/base.png");

    public static class Cache {
        private final Map<String, CacheEntry> cacheMap = Maps.newLinkedHashMap();
        private final ResourceLocation cacheResourceLocation;
        private final String cacheResourceBase;
        private final String cacheId;

        public Cache(String id2, ResourceLocation baseResource, String resourcePath) {
            this.cacheId = id2;
            this.cacheResourceLocation = baseResource;
            this.cacheResourceBase = resourcePath;
        }

        @Nullable
        public ResourceLocation getResourceLocation(String id2, List<BannerPattern> patternList, List<EnumDyeColor> colorList) {
            if (id2.isEmpty()) {
                return null;
            }
            id2 = String.valueOf(this.cacheId) + id2;
            CacheEntry bannertextures$cacheentry = this.cacheMap.get(id2);
            if (bannertextures$cacheentry == null) {
                if (this.cacheMap.size() >= 256 && !this.freeCacheSlot()) {
                    return BANNER_BASE_TEXTURE;
                }
                ArrayList<String> list = Lists.newArrayList();
                for (BannerPattern bannerpattern : patternList) {
                    list.add(String.valueOf(this.cacheResourceBase) + bannerpattern.func_190997_a() + ".png");
                }
                bannertextures$cacheentry = new CacheEntry();
                bannertextures$cacheentry.textureLocation = new ResourceLocation(id2);
                Minecraft.getMinecraft().getTextureManager().loadTexture(bannertextures$cacheentry.textureLocation, new LayeredColorMaskTexture(this.cacheResourceLocation, list, colorList));
                this.cacheMap.put(id2, bannertextures$cacheentry);
            }
            bannertextures$cacheentry.lastUseMillis = System.currentTimeMillis();
            return bannertextures$cacheentry.textureLocation;
        }

        private boolean freeCacheSlot() {
            long i2 = System.currentTimeMillis();
            Iterator<String> iterator = this.cacheMap.keySet().iterator();
            while (iterator.hasNext()) {
                String s2 = iterator.next();
                CacheEntry bannertextures$cacheentry = this.cacheMap.get(s2);
                if (i2 - bannertextures$cacheentry.lastUseMillis <= 5000L) continue;
                Minecraft.getMinecraft().getTextureManager().deleteTexture(bannertextures$cacheentry.textureLocation);
                iterator.remove();
                return true;
            }
            return this.cacheMap.size() < 256;
        }
    }

    static class CacheEntry {
        public long lastUseMillis;
        public ResourceLocation textureLocation;

        private CacheEntry() {
        }
    }
}

