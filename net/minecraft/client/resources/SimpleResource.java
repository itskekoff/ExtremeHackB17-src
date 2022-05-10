package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class SimpleResource
implements IResource {
    private final Map<String, IMetadataSection> mapMetadataSections = Maps.newHashMap();
    private final String resourcePackName;
    private final ResourceLocation srResourceLocation;
    private final InputStream resourceInputStream;
    private final InputStream mcmetaInputStream;
    private final MetadataSerializer srMetadataSerializer;
    private boolean mcmetaJsonChecked;
    private JsonObject mcmetaJson;

    public SimpleResource(String resourcePackNameIn, ResourceLocation srResourceLocationIn, InputStream resourceInputStreamIn, InputStream mcmetaInputStreamIn, MetadataSerializer srMetadataSerializerIn) {
        this.resourcePackName = resourcePackNameIn;
        this.srResourceLocation = srResourceLocationIn;
        this.resourceInputStream = resourceInputStreamIn;
        this.mcmetaInputStream = mcmetaInputStreamIn;
        this.srMetadataSerializer = srMetadataSerializerIn;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return this.srResourceLocation;
    }

    @Override
    public InputStream getInputStream() {
        return this.resourceInputStream;
    }

    @Override
    public boolean hasMetadata() {
        return this.mcmetaInputStream != null;
    }

    @Override
    @Nullable
    public <T extends IMetadataSection> T getMetadata(String sectionName) {
        IMetadataSection t2;
        if (!this.hasMetadata()) {
            return null;
        }
        if (this.mcmetaJson == null && !this.mcmetaJsonChecked) {
            this.mcmetaJsonChecked = true;
            BufferedReader bufferedreader = null;
            try {
                bufferedreader = new BufferedReader(new InputStreamReader(this.mcmetaInputStream, StandardCharsets.UTF_8));
                this.mcmetaJson = new JsonParser().parse(bufferedreader).getAsJsonObject();
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(bufferedreader);
                throw throwable;
            }
            IOUtils.closeQuietly(bufferedreader);
        }
        if ((t2 = this.mapMetadataSections.get(sectionName)) == null) {
            t2 = this.srMetadataSerializer.parseMetadataSection(sectionName, this.mcmetaJson);
        }
        return (T)t2;
    }

    @Override
    public String getResourcePackName() {
        return this.resourcePackName;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof SimpleResource)) {
            return false;
        }
        SimpleResource simpleresource = (SimpleResource)p_equals_1_;
        if (this.srResourceLocation != null ? !this.srResourceLocation.equals(simpleresource.srResourceLocation) : simpleresource.srResourceLocation != null) {
            return false;
        }
        return !(this.resourcePackName != null ? !this.resourcePackName.equals(simpleresource.resourcePackName) : simpleresource.resourcePackName != null);
    }

    public int hashCode() {
        int i2 = this.resourcePackName != null ? this.resourcePackName.hashCode() : 0;
        i2 = 31 * i2 + (this.srResourceLocation != null ? this.srResourceLocation.hashCode() : 0);
        return i2;
    }

    @Override
    public void close() throws IOException {
        this.resourceInputStream.close();
        if (this.mcmetaInputStream != null) {
            this.mcmetaInputStream.close();
        }
    }
}

