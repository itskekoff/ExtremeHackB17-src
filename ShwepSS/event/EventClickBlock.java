package ShwepSS.event;

import ShwepSS.eventapi.events.Event;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EventClickBlock
implements Event {
    private BlockPos pos;
    private EnumFacing side;

    public EventClickBlock(BlockPos pos, EnumFacing side) {
        this.pos = pos;
        this.side = side;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public EnumFacing getSide() {
        return this.side;
    }
}

