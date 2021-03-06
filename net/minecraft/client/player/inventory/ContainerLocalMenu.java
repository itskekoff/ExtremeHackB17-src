package net.minecraft.client.player.inventory;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public class ContainerLocalMenu
extends InventoryBasic
implements ILockableContainer {
    private final String guiID;
    private final Map<Integer, Integer> dataValues = Maps.newHashMap();

    public ContainerLocalMenu(String id2, ITextComponent title, int slotCount) {
        super(title, slotCount);
        this.guiID = id2;
    }

    @Override
    public int getField(int id2) {
        return this.dataValues.containsKey(id2) ? this.dataValues.get(id2) : 0;
    }

    @Override
    public void setField(int id2, int value) {
        this.dataValues.put(id2, value);
    }

    @Override
    public int getFieldCount() {
        return this.dataValues.size();
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public void setLockCode(LockCode code) {
    }

    @Override
    public LockCode getLockCode() {
        return LockCode.EMPTY_CODE;
    }

    @Override
    public String getGuiID() {
        return this.guiID;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        throw new UnsupportedOperationException();
    }
}

