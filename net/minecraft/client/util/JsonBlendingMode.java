package net.minecraft.client.util;

import com.google.gson.JsonObject;
import java.util.Locale;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.JsonUtils;

public class JsonBlendingMode {
    private static JsonBlendingMode lastApplied;
    private final int srcColorFactor;
    private final int srcAlphaFactor;
    private final int destColorFactor;
    private final int destAlphaFactor;
    private final int blendFunction;
    private final boolean separateBlend;
    private final boolean opaque;

    private JsonBlendingMode(boolean p_i45084_1_, boolean p_i45084_2_, int p_i45084_3_, int p_i45084_4_, int p_i45084_5_, int p_i45084_6_, int p_i45084_7_) {
        this.separateBlend = p_i45084_1_;
        this.srcColorFactor = p_i45084_3_;
        this.destColorFactor = p_i45084_4_;
        this.srcAlphaFactor = p_i45084_5_;
        this.destAlphaFactor = p_i45084_6_;
        this.opaque = p_i45084_2_;
        this.blendFunction = p_i45084_7_;
    }

    public JsonBlendingMode() {
        this(false, true, 1, 0, 1, 0, 32774);
    }

    public JsonBlendingMode(int p_i45085_1_, int p_i45085_2_, int p_i45085_3_) {
        this(false, false, p_i45085_1_, p_i45085_2_, p_i45085_1_, p_i45085_2_, p_i45085_3_);
    }

    public JsonBlendingMode(int p_i45086_1_, int p_i45086_2_, int p_i45086_3_, int p_i45086_4_, int p_i45086_5_) {
        this(true, false, p_i45086_1_, p_i45086_2_, p_i45086_3_, p_i45086_4_, p_i45086_5_);
    }

    public void apply() {
        if (!this.equals(lastApplied)) {
            if (lastApplied == null || this.opaque != lastApplied.isOpaque()) {
                lastApplied = this;
                if (this.opaque) {
                    GlStateManager.disableBlend();
                    return;
                }
                GlStateManager.enableBlend();
            }
            GlStateManager.glBlendEquation(this.blendFunction);
            if (this.separateBlend) {
                GlStateManager.tryBlendFuncSeparate(this.srcColorFactor, this.destColorFactor, this.srcAlphaFactor, this.destAlphaFactor);
            } else {
                GlStateManager.blendFunc(this.srcColorFactor, this.destColorFactor);
            }
        }
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof JsonBlendingMode)) {
            return false;
        }
        JsonBlendingMode jsonblendingmode = (JsonBlendingMode)p_equals_1_;
        if (this.blendFunction != jsonblendingmode.blendFunction) {
            return false;
        }
        if (this.destAlphaFactor != jsonblendingmode.destAlphaFactor) {
            return false;
        }
        if (this.destColorFactor != jsonblendingmode.destColorFactor) {
            return false;
        }
        if (this.opaque != jsonblendingmode.opaque) {
            return false;
        }
        if (this.separateBlend != jsonblendingmode.separateBlend) {
            return false;
        }
        if (this.srcAlphaFactor != jsonblendingmode.srcAlphaFactor) {
            return false;
        }
        return this.srcColorFactor == jsonblendingmode.srcColorFactor;
    }

    public int hashCode() {
        int i2 = this.srcColorFactor;
        i2 = 31 * i2 + this.srcAlphaFactor;
        i2 = 31 * i2 + this.destColorFactor;
        i2 = 31 * i2 + this.destAlphaFactor;
        i2 = 31 * i2 + this.blendFunction;
        i2 = 31 * i2 + (this.separateBlend ? 1 : 0);
        i2 = 31 * i2 + (this.opaque ? 1 : 0);
        return i2;
    }

    public boolean isOpaque() {
        return this.opaque;
    }

    public static JsonBlendingMode parseBlendNode(JsonObject json) {
        if (json == null) {
            return new JsonBlendingMode();
        }
        int i2 = 32774;
        int j2 = 1;
        int k2 = 0;
        int l2 = 1;
        int i1 = 0;
        boolean flag = true;
        boolean flag1 = false;
        if (JsonUtils.isString(json, "func") && (i2 = JsonBlendingMode.stringToBlendFunction(json.get("func").getAsString())) != 32774) {
            flag = false;
        }
        if (JsonUtils.isString(json, "srcrgb") && (j2 = JsonBlendingMode.stringToBlendFactor(json.get("srcrgb").getAsString())) != 1) {
            flag = false;
        }
        if (JsonUtils.isString(json, "dstrgb") && (k2 = JsonBlendingMode.stringToBlendFactor(json.get("dstrgb").getAsString())) != 0) {
            flag = false;
        }
        if (JsonUtils.isString(json, "srcalpha")) {
            l2 = JsonBlendingMode.stringToBlendFactor(json.get("srcalpha").getAsString());
            if (l2 != 1) {
                flag = false;
            }
            flag1 = true;
        }
        if (JsonUtils.isString(json, "dstalpha")) {
            i1 = JsonBlendingMode.stringToBlendFactor(json.get("dstalpha").getAsString());
            if (i1 != 0) {
                flag = false;
            }
            flag1 = true;
        }
        if (flag) {
            return new JsonBlendingMode();
        }
        return flag1 ? new JsonBlendingMode(j2, k2, l2, i1, i2) : new JsonBlendingMode(j2, k2, i2);
    }

    private static int stringToBlendFunction(String p_148108_0_) {
        String s2 = p_148108_0_.trim().toLowerCase(Locale.ROOT);
        if ("add".equals(s2)) {
            return 32774;
        }
        if ("subtract".equals(s2)) {
            return 32778;
        }
        if ("reversesubtract".equals(s2)) {
            return 32779;
        }
        if ("reverse_subtract".equals(s2)) {
            return 32779;
        }
        if ("min".equals(s2)) {
            return 32775;
        }
        return "max".equals(s2) ? 32776 : 32774;
    }

    private static int stringToBlendFactor(String p_148107_0_) {
        String s2 = p_148107_0_.trim().toLowerCase(Locale.ROOT);
        s2 = s2.replaceAll("_", "");
        s2 = s2.replaceAll("one", "1");
        s2 = s2.replaceAll("zero", "0");
        if ("0".equals(s2 = s2.replaceAll("minus", "-"))) {
            return 0;
        }
        if ("1".equals(s2)) {
            return 1;
        }
        if ("srccolor".equals(s2)) {
            return 768;
        }
        if ("1-srccolor".equals(s2)) {
            return 769;
        }
        if ("dstcolor".equals(s2)) {
            return 774;
        }
        if ("1-dstcolor".equals(s2)) {
            return 775;
        }
        if ("srcalpha".equals(s2)) {
            return 770;
        }
        if ("1-srcalpha".equals(s2)) {
            return 771;
        }
        if ("dstalpha".equals(s2)) {
            return 772;
        }
        return "1-dstalpha".equals(s2) ? 773 : -1;
    }
}

