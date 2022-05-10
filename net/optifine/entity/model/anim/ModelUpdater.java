package net.optifine.entity.model.anim;

import net.optifine.entity.model.anim.IModelResolver;
import net.optifine.entity.model.anim.ModelVariableUpdater;

public class ModelUpdater {
    private ModelVariableUpdater[] modelVariableUpdaters;

    public ModelUpdater(ModelVariableUpdater[] modelVariableUpdaters) {
        this.modelVariableUpdaters = modelVariableUpdaters;
    }

    public void update() {
        for (int i2 = 0; i2 < this.modelVariableUpdaters.length; ++i2) {
            ModelVariableUpdater modelvariableupdater = this.modelVariableUpdaters[i2];
            modelvariableupdater.update();
        }
    }

    public boolean initialize(IModelResolver mr2) {
        for (int i2 = 0; i2 < this.modelVariableUpdaters.length; ++i2) {
            ModelVariableUpdater modelvariableupdater = this.modelVariableUpdaters[i2];
            if (modelvariableupdater.initialize(mr2)) continue;
            return false;
        }
        return true;
    }
}

