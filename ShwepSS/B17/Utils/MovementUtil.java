package ShwepSS.B17.Utils;

import ShwepSS.event.MoveEvent;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class MovementUtil {
    public static final double WALK_SPEED = 0.521;
    public static Minecraft mc = Minecraft.getMinecraft();

    public static int getJumpBoostModifier() {
        PotionEffect effect = Minecraft.getMinecraft().player.getActivePotionEffect(MobEffects.JUMP_BOOST);
        if (effect != null) {
            return effect.getAmplifier() + 1;
        }
        return 0;
    }

    public static float getMovementDirection() {
        float forward = MovementInput.moveStrafe;
        float strafe = MovementInput.moveStrafe;
        float direction = 0.0f;
        if (forward < 0.0f) {
            direction += 180.0f;
            if (strafe > 0.0f) {
                direction += 45.0f;
            } else if (strafe < 0.0f) {
                direction -= 45.0f;
            }
        } else if (forward > 0.0f) {
            if (strafe > 0.0f) {
                direction -= 45.0f;
            } else if (strafe < 0.0f) {
                direction += 45.0f;
            }
        } else if (strafe > 0.0f) {
            direction -= 90.0f;
        } else if (strafe < 0.0f) {
            direction += 90.0f;
        }
        return MathHelper.wrapDegrees(direction += Minecraft.getMinecraft().player.rotationYaw);
    }

    public static boolean isBlockAbove() {
        for (double height = 0.0; height <= 1.0; height += 0.5) {
            List<AxisAlignedBB> collidingList = MovementUtil.mc.world.getCollisionBoxes(Minecraft.getMinecraft().player, Minecraft.getMinecraft().player.getEntityBoundingBox().offset(0.0, height, 0.0));
            if (collidingList.isEmpty()) continue;
            return true;
        }
        return false;
    }

    public static float getDirection() {
        Minecraft mc = Minecraft.getMinecraft();
        float var1 = Minecraft.getMinecraft().player.rotationYaw;
        if (Minecraft.getMinecraft().player.moveForward < 0.0f) {
            var1 += 180.0f;
        }
        float forward = 1.0f;
        if (Minecraft.getMinecraft().player.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (Minecraft.getMinecraft().player.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (Minecraft.getMinecraft().player.moveStrafing > 0.0f) {
            var1 -= 90.0f * forward;
        }
        if (Minecraft.getMinecraft().player.moveStrafing < 0.0f) {
            var1 += 90.0f * forward;
        }
        return var1 *= (float)Math.PI / 180;
    }

    public static double getXDirAt(float angle) {
        Minecraft mc = Minecraft.getMinecraft();
        double rot = 90.0;
        return Math.cos((rot += (double)angle) * Math.PI / 180.0);
    }

    public static double getZDirAt(float angle) {
        Minecraft mc = Minecraft.getMinecraft();
        double rot = 90.0;
        return Math.sin((rot += (double)angle) * Math.PI / 180.0);
    }

    public static void setSpeedAt(MoveEvent e2, float angle, double speed) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.gameSettings.keyBindJump.isKeyDown() && Minecraft.getMinecraft().player.onGround) {
            e2.setX(MovementUtil.getXDirAt(angle) * speed);
            e2.setZ(MovementUtil.getZDirAt(angle) * speed);
        }
    }

    public static void setMotion(MoveEvent e2, double speed, float pseudoYaw, double aa2, double po4) {
        double forward = po4;
        double strafe = aa2;
        float yaw = pseudoYaw;
        if (po4 != 0.0) {
            if (aa2 > 0.0) {
                yaw = pseudoYaw + (float)(po4 > 0.0 ? -45 : 45);
            } else if (aa2 < 0.0) {
                yaw = pseudoYaw + (float)(po4 > 0.0 ? 45 : -45);
            }
            strafe = 0.0;
            if (po4 > 0.0) {
                forward = 1.0;
            } else if (po4 < 0.0) {
                forward = -1.0;
            }
        }
        if (strafe > 0.0) {
            strafe = 1.0;
        } else if (strafe < 0.0) {
            strafe = -1.0;
        }
        double kak = Math.cos(Math.toRadians(yaw + 90.0f));
        double nety = Math.sin(Math.toRadians(yaw + 90.0f));
        e2.setX(forward * speed * kak + strafe * speed * nety);
        e2.setZ(forward * speed * nety - strafe * speed * kak);
    }

    public static void setSpeed(double speed) {
        double forward = MovementUtil.mc.player.movementInput.field_192832_b;
        double strafe = MovementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            Minecraft.getMinecraft().player.motionX = 0.0;
            Minecraft.getMinecraft().player.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            Minecraft.getMinecraft().player.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
            Minecraft.getMinecraft().player.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
        }
    }

    public static void strafe() {
        if (MovementUtil.mc.gameSettings.keyBindBack.isKeyDown()) {
            return;
        }
        MovementUtil.strafe(MovementUtil.getSpeed());
    }

    public static float getSpeed() {
        return (float)Math.sqrt(Minecraft.getMinecraft().player.motionX * Minecraft.getMinecraft().player.motionX + Minecraft.getMinecraft().player.motionZ * Minecraft.getMinecraft().player.motionZ);
    }

    public static boolean isMoving() {
        if (Minecraft.getMinecraft().player == null) {
            return false;
        }
        if (MovementInput.moveStrafe != 2.0f) {
            return true;
        }
        return MovementInput.moveStrafe != 2.0f;
    }

    public static boolean hasMotion() {
        if (Minecraft.getMinecraft().player.motionX == 0.0) {
            return false;
        }
        if (Minecraft.getMinecraft().player.motionZ == 0.0) {
            return false;
        }
        return Minecraft.getMinecraft().player.motionY != 0.0;
    }

    public static void strafe(float speed) {
        if (!MovementUtil.isMoving()) {
            return;
        }
        double yaw = MovementUtil.getDirection();
        Minecraft.getMinecraft().player.motionX = -Math.sin(yaw) * (double)speed;
        Minecraft.getMinecraft().player.motionZ = Math.cos(yaw) * (double)speed;
    }

    public static double getMoveSpeed(MoveEvent e2) {
        Minecraft mc = Minecraft.getMinecraft();
        double xspeed = e2.getX();
        double zspeed = e2.getZ();
        return Math.sqrt(xspeed * xspeed + zspeed * zspeed);
    }

    public static boolean moveKeysDown() {
        Minecraft mc = Minecraft.getMinecraft();
        if (MovementInput.moveStrafe != 0.0f) {
            return true;
        }
        return MovementInput.moveStrafe != 0.0f;
    }

    public static double getPressedMoveDir() {
        Minecraft mc = Minecraft.getMinecraft();
        double rot = Math.atan2(Minecraft.getMinecraft().player.moveForward, Minecraft.getMinecraft().player.moveStrafing) / Math.PI * 180.0;
        if (rot == 0.0 && Minecraft.getMinecraft().player.moveStrafing == 2.0f) {
            rot = 90.0;
        }
        return (rot += (double)Minecraft.getMinecraft().player.rotationYaw) - 90.0;
    }

    public static double getPlayerMoveDir() {
        Minecraft mc = Minecraft.getMinecraft();
        double xspeed = Minecraft.getMinecraft().player.motionX;
        double zspeed = Minecraft.getMinecraft().player.motionZ;
        double direction = Math.atan2(xspeed, zspeed) / Math.PI * 180.0;
        return -direction;
    }

    public static boolean isBlockAboveHead() {
        AxisAlignedBB bb2 = new AxisAlignedBB(Minecraft.getMinecraft().player.posX - 0.3, Minecraft.getMinecraft().player.posY + (double)Minecraft.getMinecraft().player.getEyeHeight(), Minecraft.getMinecraft().player.posZ + 0.3, Minecraft.getMinecraft().player.posX + 0.3, Minecraft.getMinecraft().player.posY + 2.5, Minecraft.getMinecraft().player.posZ - 0.3);
        return !MovementUtil.mc.world.getCollisionBoxes(Minecraft.getMinecraft().player, bb2).isEmpty();
    }

    public static void setMotionEvent(MoveEvent event, double speed) {
        double forward = MovementInput.moveStrafe;
        double strafe = MovementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }

    public static void startFakePos() {
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        Minecraft.getMinecraft().player.setPosition(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY + 0.3, Minecraft.getMinecraft().player.posZ);
        Minecraft.getMinecraft();
        double x2 = Minecraft.getMinecraft().player.posX;
        Minecraft.getMinecraft();
        double y2 = Minecraft.getMinecraft().player.posY;
        Minecraft.getMinecraft();
        double z2 = Minecraft.getMinecraft().player.posZ;
        for (int i2 = 0; i2 < 3000; ++i2) {
            Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayer.Position(x2, y2 + 0.09999999999999, z2, false));
            Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayer.Position(x2, y2, z2, true));
        }
        Minecraft.getMinecraft();
        Minecraft.getMinecraft().player.motionY = 0.0;
    }

    public static void setSpeed2(double speed) {
        MovementUtil.mc.player.motionX = (double)(-MathHelper.sin(MovementUtil.getDirection())) * speed;
        MovementUtil.mc.player.motionZ = (double)MathHelper.cos(MovementUtil.getDirection()) * speed;
    }

    public static void speedlodka(double speed) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.player.getRidingEntity().motionX = (double)(-MathHelper.sin(MovementUtil.getDirection())) * speed;
        mc.player.getRidingEntity().motionZ = (double)MathHelper.cos(MovementUtil.getDirection()) * speed;
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (Minecraft.getMinecraft().player.isPotionActive(Potion.getPotionById(1))) {
            int amplifier = Minecraft.getMinecraft().player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
            baseSpeed *= 2.0 + 0.2 * (double)(amplifier + 1);
        }
        return baseSpeed;
    }

    public static boolean isOnGround() {
        MovementUtil.mc.player.onGround = true;
        return false;
    }
}

