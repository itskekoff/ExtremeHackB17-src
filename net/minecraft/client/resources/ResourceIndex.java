package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceIndex {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, File> resourceMap;

    protected ResourceIndex() {
        this.resourceMap = Maps.newHashMap();
    }

    public ResourceIndex(File assetsFolder, String indexName) {
        block8: {
            this.resourceMap = Maps.newHashMap();
            File file1 = new File(assetsFolder, "objects");
            File file2 = new File(assetsFolder, "indexes/" + indexName + ".json");
            BufferedReader bufferedreader = null;
            try {
                bufferedreader = Files.newReader(file2, StandardCharsets.UTF_8);
                JsonObject jsonobject = new JsonParser().parse(bufferedreader).getAsJsonObject();
                JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "objects", null);
                if (jsonobject1 != null) {
                    for (Map.Entry<String, JsonElement> entry : jsonobject1.entrySet()) {
                        JsonObject jsonobject2 = (JsonObject)entry.getValue();
                        String s2 = entry.getKey();
                        String[] astring = s2.split("/", 2);
                        String s1 = astring.length == 1 ? astring[0] : String.valueOf(astring[0]) + ":" + astring[1];
                        String s22 = JsonUtils.getString(jsonobject2, "hash");
                        File file3 = new File(file1, String.valueOf(s22.substring(0, 2)) + "/" + s22);
                        this.resourceMap.put(s1, file3);
                    }
                }
            }
            catch (JsonParseException var20) {
                LOGGER.error("Unable to parse resource index file: {}", (Object)file2);
                IOUtils.closeQuietly(bufferedreader);
                break block8;
            }
            catch (FileNotFoundException var21) {
                try {
                    LOGGER.error("Can't find the resource index file: {}", (Object)file2);
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(bufferedreader);
                    throw throwable;
                }
                IOUtils.closeQuietly(bufferedreader);
                break block8;
            }
            IOUtils.closeQuietly(bufferedreader);
        }
    }

    @Nullable
    public File getFile(ResourceLocation location) {
        String s2 = location.toString();
        return this.resourceMap.get(s2);
    }

    public boolean isFileExisting(ResourceLocation location) {
        File file1 = this.getFile(location);
        return file1 != null && file1.isFile();
    }

    public File getPackMcmeta() {
        return this.resourceMap.get("pack.mcmeta");
    }
}

