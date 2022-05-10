package net.optifine.entity.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.optifine.entity.model.CustomEntityModelParser;
import net.optifine.entity.model.CustomEntityRenderer;
import net.optifine.entity.model.CustomModelRegistry;
import net.optifine.entity.model.CustomModelRenderer;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.entity.model.anim.ModelResolver;
import net.optifine.entity.model.anim.ModelUpdater;
import optifine.Config;

public class CustomEntityModels {
    private static boolean active = false;
    private static Map<Class, Render> originalEntityRenderMap = null;
    private static Map<Class, TileEntitySpecialRenderer> originalTileEntityRenderMap = null;

    public static void update() {
        Map<Class, Render> map = CustomEntityModels.getEntityRenderMap();
        Map<Class, TileEntitySpecialRenderer> map1 = CustomEntityModels.getTileEntityRenderMap();
        if (map == null) {
            Config.warn("Entity render map not found, custom entity models are DISABLED.");
        } else if (map1 == null) {
            Config.warn("Tile entity render map not found, custom entity models are DISABLED.");
        } else {
            active = false;
            map.clear();
            map1.clear();
            map.putAll(originalEntityRenderMap);
            map1.putAll(originalTileEntityRenderMap);
            if (Config.isCustomEntityModels()) {
                ResourceLocation[] aresourcelocation = CustomEntityModels.getModelLocations();
                for (int i2 = 0; i2 < aresourcelocation.length; ++i2) {
                    Class oclass;
                    ResourceLocation resourcelocation = aresourcelocation[i2];
                    Config.dbg("CustomEntityModel: " + resourcelocation.getResourcePath());
                    IEntityRenderer ientityrenderer = CustomEntityModels.parseEntityRender(resourcelocation);
                    if (ientityrenderer == null || (oclass = ientityrenderer.getEntityClass()) == null) continue;
                    if (ientityrenderer instanceof Render) {
                        map.put(oclass, (Render)ientityrenderer);
                    } else if (ientityrenderer instanceof TileEntitySpecialRenderer) {
                        map1.put(oclass, (TileEntitySpecialRenderer)ientityrenderer);
                    } else {
                        Config.warn("Unknown renderer type: " + ientityrenderer.getClass().getName());
                    }
                    active = true;
                }
            }
        }
    }

    private static Map<Class, Render> getEntityRenderMap() {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        Map<Class, Render> map = rendermanager.getEntityRenderMap();
        if (map == null) {
            return null;
        }
        if (originalEntityRenderMap == null) {
            originalEntityRenderMap = new HashMap<Class, Render>(map);
        }
        return map;
    }

    private static Map<Class, TileEntitySpecialRenderer> getTileEntityRenderMap() {
        Map<Class, TileEntitySpecialRenderer> map = TileEntityRendererDispatcher.instance.mapSpecialRenderers;
        if (originalTileEntityRenderMap == null) {
            originalTileEntityRenderMap = new HashMap<Class, TileEntitySpecialRenderer>(map);
        }
        return map;
    }

    private static ResourceLocation[] getModelLocations() {
        String s2 = "optifine/cem/";
        String s1 = ".jem";
        ArrayList<ResourceLocation> list = new ArrayList<ResourceLocation>();
        String[] astring = CustomModelRegistry.getModelNames();
        for (int i2 = 0; i2 < astring.length; ++i2) {
            String s22 = astring[i2];
            String s3 = String.valueOf(s2) + s22 + s1;
            ResourceLocation resourcelocation = new ResourceLocation(s3);
            if (!Config.hasResource(resourcelocation)) continue;
            list.add(resourcelocation);
        }
        ResourceLocation[] aresourcelocation = list.toArray(new ResourceLocation[list.size()]);
        return aresourcelocation;
    }

    private static IEntityRenderer parseEntityRender(ResourceLocation location) {
        try {
            JsonObject jsonobject = CustomEntityModelParser.loadJson(location);
            IEntityRenderer ientityrenderer = CustomEntityModels.parseEntityRender(jsonobject, location.getResourcePath());
            return ientityrenderer;
        }
        catch (IOException ioexception) {
            Config.error(ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
        catch (JsonParseException jsonparseexception) {
            Config.error(jsonparseexception.getClass().getName() + ": " + jsonparseexception.getMessage());
            return null;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static IEntityRenderer parseEntityRender(JsonObject obj, String path) {
        CustomEntityRenderer customentityrenderer = CustomEntityModelParser.parseEntityRender(obj, path);
        String s2 = customentityrenderer.getName();
        ModelAdapter modeladapter = CustomModelRegistry.getModelAdapter(s2);
        CustomEntityModels.checkNull(modeladapter, "Entity not found: " + s2);
        Class oclass = modeladapter.getEntityClass();
        CustomEntityModels.checkNull(oclass, "Entity class not found: " + s2);
        IEntityRenderer ientityrenderer = CustomEntityModels.makeEntityRender(modeladapter, customentityrenderer);
        if (ientityrenderer == null) {
            return null;
        }
        ientityrenderer.setEntityClass(oclass);
        return ientityrenderer;
    }

    private static IEntityRenderer makeEntityRender(ModelAdapter modelAdapter, CustomEntityRenderer cer2) {
        ModelBase modelbase;
        ResourceLocation resourcelocation = cer2.getTextureLocation();
        CustomModelRenderer[] acustommodelrenderer = cer2.getCustomModelRenderers();
        float f2 = cer2.getShadowSize();
        if (f2 < 0.0f) {
            f2 = modelAdapter.getShadowSize();
        }
        if ((modelbase = modelAdapter.makeModel()) == null) {
            return null;
        }
        ModelResolver modelresolver = new ModelResolver(modelAdapter, modelbase, acustommodelrenderer);
        if (!CustomEntityModels.modifyModel(modelAdapter, modelbase, acustommodelrenderer, modelresolver)) {
            return null;
        }
        IEntityRenderer ientityrenderer = modelAdapter.makeEntityRender(modelbase, f2);
        if (ientityrenderer == null) {
            throw new JsonParseException("Entity renderer is null, model: " + modelAdapter.getName() + ", adapter: " + modelAdapter.getClass().getName());
        }
        if (resourcelocation != null) {
            ientityrenderer.setLocationTextureCustom(resourcelocation);
        }
        return ientityrenderer;
    }

    private static boolean modifyModel(ModelAdapter modelAdapter, ModelBase model, CustomModelRenderer[] modelRenderers, ModelResolver mr2) {
        for (int i2 = 0; i2 < modelRenderers.length; ++i2) {
            CustomModelRenderer custommodelrenderer = modelRenderers[i2];
            if (CustomEntityModels.modifyModel(modelAdapter, model, custommodelrenderer, mr2)) continue;
            return false;
        }
        return true;
    }

    private static boolean modifyModel(ModelAdapter modelAdapter, ModelBase model, CustomModelRenderer customModelRenderer, ModelResolver modelResolver) {
        String s2 = customModelRenderer.getModelPart();
        ModelRenderer modelrenderer = modelAdapter.getModelRenderer(model, s2);
        if (modelrenderer == null) {
            Config.warn("Model part not found: " + s2 + ", model: " + model);
            return false;
        }
        if (!customModelRenderer.isAttach()) {
            if (modelrenderer.cubeList != null) {
                modelrenderer.cubeList.clear();
            }
            if (modelrenderer.spriteList != null) {
                modelrenderer.spriteList.clear();
            }
            if (modelrenderer.childModels != null) {
                modelrenderer.childModels.clear();
            }
        }
        modelrenderer.addChild(customModelRenderer.getModelRenderer());
        ModelUpdater modelupdater = customModelRenderer.getModelUpdater();
        if (modelupdater != null) {
            modelResolver.setThisModelRenderer(customModelRenderer.getModelRenderer());
            modelResolver.setPartModelRenderer(modelrenderer);
            if (!modelupdater.initialize(modelResolver)) {
                return false;
            }
            customModelRenderer.getModelRenderer().setModelUpdater(modelupdater);
        }
        return true;
    }

    private static void checkNull(Object obj, String msg) {
        if (obj == null) {
            throw new JsonParseException(msg);
        }
    }

    public static boolean isActive() {
        return active;
    }
}

