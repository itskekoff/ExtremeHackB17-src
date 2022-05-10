package optifine;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import optifine.Config;

public class EntityUtils {
    private static final Map<Class, Integer> mapIdByClass = new HashMap<Class, Integer>();
    private static final Map<String, Integer> mapIdByLocation = new HashMap<String, Integer>();
    private static final Map<String, Integer> mapIdByName = new HashMap<String, Integer>();

    static {
        for (int i2 = 0; i2 < 1000; ++i2) {
            ResourceLocation resourcelocation;
            Class<? extends Entity> oclass = EntityList.getClassFromID(i2);
            if (oclass == null || (resourcelocation = EntityList.func_191306_a(oclass)) == null) continue;
            String s2 = resourcelocation.toString();
            String s1 = EntityList.func_191302_a(resourcelocation);
            if (s1 == null) continue;
            if (mapIdByClass.containsKey(oclass)) {
                Config.warn("Duplicate entity class: " + oclass + ", id1: " + mapIdByClass.get(oclass) + ", id2: " + i2);
            }
            if (mapIdByLocation.containsKey(s2)) {
                Config.warn("Duplicate entity location: " + s2 + ", id1: " + mapIdByLocation.get(s2) + ", id2: " + i2);
            }
            if (mapIdByName.containsKey(s2)) {
                Config.warn("Duplicate entity name: " + s1 + ", id1: " + mapIdByName.get(s1) + ", id2: " + i2);
            }
            mapIdByClass.put(oclass, i2);
            mapIdByLocation.put(s2, i2);
            mapIdByName.put(s1, i2);
        }
    }

    public static int getEntityIdByClass(Entity p_getEntityIdByClass_0_) {
        return p_getEntityIdByClass_0_ == null ? -1 : EntityUtils.getEntityIdByClass(p_getEntityIdByClass_0_.getClass());
    }

    public static int getEntityIdByClass(Class p_getEntityIdByClass_0_) {
        Integer integer = mapIdByClass.get(p_getEntityIdByClass_0_);
        return integer == null ? -1 : integer;
    }

    public static int getEntityIdByLocation(String p_getEntityIdByLocation_0_) {
        Integer integer = mapIdByLocation.get(p_getEntityIdByLocation_0_);
        return integer == null ? -1 : integer;
    }

    public static int getEntityIdByName(String p_getEntityIdByName_0_) {
        Integer integer = mapIdByName.get(p_getEntityIdByName_0_);
        return integer == null ? -1 : integer;
    }
}

