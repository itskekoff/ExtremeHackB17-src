package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemBow
extends Item {
    public ItemBow() {
        this.maxStackSize = 1;
        this.setMaxDamage(384);
        this.setCreativeTab(CreativeTabs.COMBAT);
        this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter(){

            @Override
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (entityIn == null) {
                    return 0.0f;
                }
                return entityIn.getActiveItemStack().getItem() != Items.BOW ? 0.0f : (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0f;
            }
        });
        this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter(){

            @Override
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0f : 0.0f;
            }
        });
    }

    private ItemStack findAmmo(EntityPlayer player) {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        for (int i2 = 0; i2 < player.inventory.getSizeInventory(); ++i2) {
            ItemStack itemstack = player.inventory.getStackInSlot(i2);
            if (!this.isArrow(itemstack)) continue;
            return itemstack;
        }
        return ItemStack.field_190927_a;
    }

    protected boolean isArrow(ItemStack stack) {
        return stack.getItem() instanceof ItemArrow;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
            boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemstack = this.findAmmo(entityplayer);
            if (!itemstack.func_190926_b() || flag) {
                int i2;
                float f2;
                if (itemstack.func_190926_b()) {
                    itemstack = new ItemStack(Items.ARROW);
                }
                if ((double)(f2 = ItemBow.getArrowVelocity(i2 = this.getMaxItemUseDuration(stack) - timeLeft)) >= 0.1) {
                    boolean flag1;
                    boolean bl2 = flag1 = flag && itemstack.getItem() == Items.ARROW;
                    if (!worldIn.isRemote) {
                        int k2;
                        int j2;
                        ItemArrow itemarrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
                        EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
                        entityarrow.setAim(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0f, f2 * 3.0f, 1.0f);
                        if (f2 == 1.0f) {
                            entityarrow.setIsCritical(true);
                        }
                        if ((j2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack)) > 0) {
                            entityarrow.setDamage(entityarrow.getDamage() + (double)j2 * 0.5 + 0.5);
                        }
                        if ((k2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack)) > 0) {
                            entityarrow.setKnockbackStrength(k2);
                        }
                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                            entityarrow.setFire(100);
                        }
                        stack.damageItem(1, entityplayer);
                        if (flag1 || entityplayer.capabilities.isCreativeMode && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                            entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        }
                        worldIn.spawnEntityInWorld(entityarrow);
                    }
                    worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + f2 * 0.5f);
                    if (!flag1 && !entityplayer.capabilities.isCreativeMode) {
                        itemstack.func_190918_g(1);
                        if (itemstack.func_190926_b()) {
                            entityplayer.inventory.deleteStack(itemstack);
                        }
                    }
                    entityplayer.addStat(StatList.getObjectUseStats(this));
                }
            }
        }
    }

    public static float getArrowVelocity(int charge) {
        float f2 = (float)charge / 20.0f;
        if ((f2 = (f2 * f2 + f2 * 2.0f) / 3.0f) > 1.0f) {
            f2 = 1.0f;
        }
        return f2;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World itemStackIn, EntityPlayer worldIn, EnumHand playerIn) {
        boolean flag;
        ItemStack itemstack = worldIn.getHeldItem(playerIn);
        boolean bl2 = flag = !this.findAmmo(worldIn).func_190926_b();
        if (!worldIn.capabilities.isCreativeMode && !flag) {
            return flag ? new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack) : new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
        }
        worldIn.setActiveHand(playerIn);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }
}

