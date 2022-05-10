package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWither;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWither;
import net.minecraft.entity.boss.EntityWither;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import optifine.Config;
import optifine.Reflector;

public class ModelAdapterWither
extends ModelAdapter {
    public ModelAdapterWither() {
        super(EntityWither.class, "wither", 0.5f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelWither(0.0f);
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelWither)) {
            return null;
        }
        ModelWither modelwither = (ModelWither)model;
        String s2 = "body";
        if (modelPart.startsWith(s2)) {
            ModelRenderer[] amodelrenderer1 = (ModelRenderer[])Reflector.getFieldValue(modelwither, Reflector.ModelWither_bodyParts);
            if (amodelrenderer1 == null) {
                return null;
            }
            String s3 = modelPart.substring(s2.length());
            int j2 = Config.parseInt(s3, -1);
            return --j2 >= 0 && j2 < amodelrenderer1.length ? amodelrenderer1[j2] : null;
        }
        String s1 = "head";
        if (modelPart.startsWith(s1)) {
            ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(modelwither, Reflector.ModelWither_heads);
            if (amodelrenderer == null) {
                return null;
            }
            String s22 = modelPart.substring(s1.length());
            int i2 = Config.parseInt(s22, -1);
            return --i2 >= 0 && i2 < amodelrenderer.length ? amodelrenderer[i2] : null;
        }
        return null;
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderWither renderwither = new RenderWither(rendermanager);
        renderwither.mainModel = modelBase;
        renderwither.shadowSize = shadowSize;
        return renderwither;
    }
}

