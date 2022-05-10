package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhaseManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final EntityDragon dragon;
    private final IPhase[] phases = new IPhase[PhaseList.getTotalPhases()];
    private IPhase phase;

    public PhaseManager(EntityDragon dragonIn) {
        this.dragon = dragonIn;
        this.setPhase(PhaseList.HOVER);
    }

    public void setPhase(PhaseList<?> phaseIn) {
        if (this.phase == null || phaseIn != this.phase.getPhaseList()) {
            if (this.phase != null) {
                this.phase.removeAreaEffect();
            }
            this.phase = this.getPhase(phaseIn);
            if (!this.dragon.world.isRemote) {
                this.dragon.getDataManager().set(EntityDragon.PHASE, phaseIn.getId());
            }
            LOGGER.debug("Dragon is now in phase {} on the {}", (Object)phaseIn, (Object)(this.dragon.world.isRemote ? "client" : "server"));
            this.phase.initPhase();
        }
    }

    public IPhase getCurrentPhase() {
        return this.phase;
    }

    public <T extends IPhase> T getPhase(PhaseList<T> phaseIn) {
        int i2 = phaseIn.getId();
        if (this.phases[i2] == null) {
            this.phases[i2] = phaseIn.createPhase(this.dragon);
        }
        return (T)this.phases[i2];
    }
}

