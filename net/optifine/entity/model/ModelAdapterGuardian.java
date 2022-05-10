package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderGuardian;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGuardian;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import optifine.Config;
import optifine.Reflector;

public class ModelAdapterGuardian
extends ModelAdapter {
    public ModelAdapterGuardian() {
        super(EntityGuardian.class, "guardian", 0.5f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelGuardian();
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelGuardian)) {
            return null;
        }
        ModelGuardian modelguardian = (ModelGuardian)model;
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.getFieldValue(modelguardian, Reflector.ModelGuardian_body);
        }
        if (modelPart.equals("eye")) {
            return (ModelRenderer)Reflector.getFieldValue(modelguardian, Reflector.ModelGuardian_eye);
        }
        String s2 = "spine";
        if (modelPart.startsWith(s2)) {
            ModelRenderer[] amodelrenderer1 = (ModelRenderer[])Reflector.getFieldValue(modelguardian, Reflector.ModelGuardian_spines);
            if (amodelrenderer1 == null) {
                return null;
            }
            String s3 = modelPart.substring(s2.length());
            int j2 = Config.parseInt(s3, -1);
            return --j2 >= 0 && j2 < amodelrenderer1.length ? amodelrenderer1[j2] : null;
        }
        String s1 = "tail";
        if (modelPart.startsWith(s1)) {
            ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(modelguardian, Reflector.ModelGuardian_tail);
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
        RenderGuardian renderguardian = new RenderGuardian(rendermanager);
        renderguardian.mainModel = modelBase;
        renderguardian.shadowSize = shadowSize;
        return renderguardian;
    }
}

