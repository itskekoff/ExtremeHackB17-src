package optifine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;
import optifine.Config;

public class ModelUtils {
    public static void dbgModel(IBakedModel p_dbgModel_0_) {
        if (p_dbgModel_0_ != null) {
            Config.dbg("Model: " + p_dbgModel_0_ + ", ao: " + p_dbgModel_0_.isAmbientOcclusion() + ", gui3d: " + p_dbgModel_0_.isGui3d() + ", builtIn: " + p_dbgModel_0_.isBuiltInRenderer() + ", particle: " + p_dbgModel_0_.getParticleTexture());
            EnumFacing[] aenumfacing = EnumFacing.VALUES;
            for (int i2 = 0; i2 < aenumfacing.length; ++i2) {
                EnumFacing enumfacing = aenumfacing[i2];
                List<BakedQuad> list = p_dbgModel_0_.getQuads(null, enumfacing, 0L);
                ModelUtils.dbgQuads(enumfacing.getName(), list, "  ");
            }
            List<BakedQuad> list1 = p_dbgModel_0_.getQuads(null, null, 0L);
            ModelUtils.dbgQuads("General", list1, "  ");
        }
    }

    private static void dbgQuads(String p_dbgQuads_0_, List p_dbgQuads_1_, String p_dbgQuads_2_) {
        for (Object bakedquad : p_dbgQuads_1_) {
            ModelUtils.dbgQuad(p_dbgQuads_0_, (BakedQuad)bakedquad, p_dbgQuads_2_);
        }
    }

    public static void dbgQuad(String p_dbgQuad_0_, BakedQuad p_dbgQuad_1_, String p_dbgQuad_2_) {
        Config.dbg(String.valueOf(p_dbgQuad_2_) + "Quad: " + p_dbgQuad_1_.getClass().getName() + ", type: " + p_dbgQuad_0_ + ", face: " + p_dbgQuad_1_.getFace() + ", tint: " + p_dbgQuad_1_.getTintIndex() + ", sprite: " + p_dbgQuad_1_.getSprite());
        ModelUtils.dbgVertexData(p_dbgQuad_1_.getVertexData(), "  " + p_dbgQuad_2_);
    }

    public static void dbgVertexData(int[] p_dbgVertexData_0_, String p_dbgVertexData_1_) {
        int i2 = p_dbgVertexData_0_.length / 4;
        Config.dbg(String.valueOf(p_dbgVertexData_1_) + "Length: " + p_dbgVertexData_0_.length + ", step: " + i2);
        for (int j2 = 0; j2 < 4; ++j2) {
            int k2 = j2 * i2;
            float f2 = Float.intBitsToFloat(p_dbgVertexData_0_[k2 + 0]);
            float f1 = Float.intBitsToFloat(p_dbgVertexData_0_[k2 + 1]);
            float f22 = Float.intBitsToFloat(p_dbgVertexData_0_[k2 + 2]);
            int l2 = p_dbgVertexData_0_[k2 + 3];
            float f3 = Float.intBitsToFloat(p_dbgVertexData_0_[k2 + 4]);
            float f4 = Float.intBitsToFloat(p_dbgVertexData_0_[k2 + 5]);
            Config.dbg(String.valueOf(p_dbgVertexData_1_) + j2 + " xyz: " + f2 + "," + f1 + "," + f22 + " col: " + l2 + " u,v: " + f3 + "," + f4);
        }
    }

    public static IBakedModel duplicateModel(IBakedModel p_duplicateModel_0_) {
        List list = ModelUtils.duplicateQuadList(p_duplicateModel_0_.getQuads(null, null, 0L));
        EnumFacing[] aenumfacing = EnumFacing.VALUES;
        HashMap<EnumFacing, List<BakedQuad>> map = new HashMap<EnumFacing, List<BakedQuad>>();
        for (int i2 = 0; i2 < aenumfacing.length; ++i2) {
            EnumFacing enumfacing = aenumfacing[i2];
            List<BakedQuad> list1 = p_duplicateModel_0_.getQuads(null, enumfacing, 0L);
            List list2 = ModelUtils.duplicateQuadList(list1);
            map.put(enumfacing, list2);
        }
        SimpleBakedModel simplebakedmodel = new SimpleBakedModel(list, map, p_duplicateModel_0_.isAmbientOcclusion(), p_duplicateModel_0_.isGui3d(), p_duplicateModel_0_.getParticleTexture(), p_duplicateModel_0_.getItemCameraTransforms(), p_duplicateModel_0_.getOverrides());
        return simplebakedmodel;
    }

    public static List duplicateQuadList(List p_duplicateQuadList_0_) {
        ArrayList<BakedQuad> list = new ArrayList<BakedQuad>();
        for (Object bakedquad : p_duplicateQuadList_0_) {
            BakedQuad bakedquad1 = ModelUtils.duplicateQuad((BakedQuad)bakedquad);
            list.add(bakedquad1);
        }
        return list;
    }

    public static BakedQuad duplicateQuad(BakedQuad p_duplicateQuad_0_) {
        BakedQuad bakedquad = new BakedQuad((int[])p_duplicateQuad_0_.getVertexData().clone(), p_duplicateQuad_0_.getTintIndex(), p_duplicateQuad_0_.getFace(), p_duplicateQuad_0_.getSprite());
        return bakedquad;
    }
}

