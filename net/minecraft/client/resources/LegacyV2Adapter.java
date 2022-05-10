package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class LegacyV2Adapter
implements IResourcePack {
    private final IResourcePack field_191383_a;

    public LegacyV2Adapter(IResourcePack p_i47182_1_) {
        this.field_191383_a = p_i47182_1_;
    }

    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException {
        return this.field_191383_a.getInputStream(this.func_191382_c(location));
    }

    private ResourceLocation func_191382_c(ResourceLocation p_191382_1_) {
        int i2;
        String s2 = p_191382_1_.getResourcePath();
        if (!"lang/swg_de.lang".equals(s2) && s2.startsWith("lang/") && s2.endsWith(".lang") && (i2 = s2.indexOf(95)) != -1) {
            final String s1 = String.valueOf(s2.substring(0, i2 + 1)) + s2.substring(i2 + 1, s2.indexOf(46, i2)).toUpperCase() + ".lang";
            return new ResourceLocation(p_191382_1_.getResourceDomain(), ""){

                @Override
                public String getResourcePath() {
                    return s1;
                }
            };
        }
        return p_191382_1_;
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        return this.field_191383_a.resourceExists(this.func_191382_c(location));
    }

    @Override
    public Set<String> getResourceDomains() {
        return this.field_191383_a.getResourceDomains();
    }

    @Override
    @Nullable
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
        return this.field_191383_a.getPackMetadata(metadataSerializer, metadataSectionName);
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return this.field_191383_a.getPackImage();
    }

    @Override
    public String getPackName() {
        return this.field_191383_a.getPackName();
    }
}

