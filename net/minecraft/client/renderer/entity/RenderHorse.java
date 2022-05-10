package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;

public class RenderHorse
extends RenderLiving<EntityHorse> {
    private static final Map<String, ResourceLocation> LAYERED_LOCATION_CACHE = Maps.newHashMap();

    public RenderHorse(RenderManager p_i47205_1_) {
        super(p_i47205_1_, new ModelHorse(), 0.75f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityHorse entity) {
        String s2 = entity.getHorseTexture();
        ResourceLocation resourcelocation = LAYERED_LOCATION_CACHE.get(s2);
        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s2);
            Minecraft.getMinecraft().getTextureManager().loadTexture(resourcelocation, new LayeredTexture(entity.getVariantTexturePaths()));
            LAYERED_LOCATION_CACHE.put(s2, resourcelocation);
        }
        return resourcelocation;
    }
}

