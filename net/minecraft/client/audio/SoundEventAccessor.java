package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class SoundEventAccessor
implements ISoundEventAccessor<Sound> {
    private final List<ISoundEventAccessor<Sound>> accessorList = Lists.newArrayList();
    private final Random rnd = new Random();
    private final ResourceLocation location;
    private final ITextComponent subtitle;

    public SoundEventAccessor(ResourceLocation locationIn, @Nullable String subtitleIn) {
        this.location = locationIn;
        this.subtitle = subtitleIn == null ? null : new TextComponentTranslation(subtitleIn, new Object[0]);
    }

    @Override
    public int getWeight() {
        int i2 = 0;
        for (ISoundEventAccessor<Sound> isoundeventaccessor : this.accessorList) {
            i2 += isoundeventaccessor.getWeight();
        }
        return i2;
    }

    @Override
    public Sound cloneEntry() {
        int i2 = this.getWeight();
        if (!this.accessorList.isEmpty() && i2 != 0) {
            int j2 = this.rnd.nextInt(i2);
            for (ISoundEventAccessor<Sound> isoundeventaccessor : this.accessorList) {
                if ((j2 -= isoundeventaccessor.getWeight()) >= 0) continue;
                return isoundeventaccessor.cloneEntry();
            }
            return SoundHandler.MISSING_SOUND;
        }
        return SoundHandler.MISSING_SOUND;
    }

    public void addSound(ISoundEventAccessor<Sound> p_188715_1_) {
        this.accessorList.add(p_188715_1_);
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    @Nullable
    public ITextComponent getSubtitle() {
        return this.subtitle;
    }
}

