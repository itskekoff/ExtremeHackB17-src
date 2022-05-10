package ShwepSS.B17.cg.util;

import java.util.Random;
import net.minecraft.client.gui.ScaledResolution;

public class Particle {
    public float x;
    public float y;
    public float radius;
    public float speed;
    public float ticks;
    public float opacity;

    public Particle(ScaledResolution sr2, float radius, float speed) {
        this.x = new Random().nextFloat() * (float)sr2.getScaledWidth();
        this.y = new Random().nextFloat() * (float)sr2.getScaledHeight();
        this.ticks = new Random().nextFloat() * (float)sr2.getScaledHeight() / 2.0f;
        this.radius = radius;
        this.speed = speed;
    }
}

