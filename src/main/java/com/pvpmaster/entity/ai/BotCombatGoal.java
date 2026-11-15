package com.pvpmaster.entity.ai;

import com.pvpmaster.entity.BotEntity;
import com.pvpmaster.entity.BotLevel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;

import java.util.EnumSet;

/** Melee combat: approach, keep spacing, strafe, jump, retreat, attack with delay/miss chance. */
public class BotCombatGoal extends Goal {
    private final BotEntity bot;
    private boolean clockwise = true;
    private boolean inRange = false;
    private int nextStrafeSwitch, nextPath, nextAttack, reactionEnd, nextHeal;

    public BotCombatGoal(BotEntity bot) {
        this.bot = bot;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override public boolean canStart() {
        LivingEntity t = bot.getTarget();
        return t != null && t.isAlive();
    }

    @Override public boolean shouldContinue() { return canStart(); }

    @Override public void stop() {
        bot.getNavigation().stop();
        bot.setSprinting(false);
        inRange = false;
    }

    @Override public void tick() {
        LivingEntity t = bot.getTarget();
        if (t == null) return;
        BotLevel lv = bot.getBotLevel();
        int now = bot.age;

        bot.lookAtEntity(t, 30f, 30f);
        bot.getLookControl().lookAt(t, 30f, 30f);
        double dist = bot.distanceTo(t);

        if (now >= nextStrafeSwitch) {
            clockwise = bot.getRandom().nextBoolean();
            nextStrafeSwitch = now + 10 + bot.getRandom().nextInt(20);
        }

        boolean lowHealth = bot.getHealth() < bot.getMaxHealth() * 0.3f;

        if (dist > 4.0) {
            bot.setSprinting(true);
            if (now >= nextPath) {
                bot.getNavigation().startMovingTo(t, 1.15 * lv.speed);
                nextPath = now + 4;
            }
        } else {
            bot.getNavigation().stop();
            bot.setSprinting(false);
            float fwd = dist < 2.2 ? -0.5f : (dist > 3.0 ? 0.6f : 0f);
            if (lv.retreat && lowHealth) fwd = -0.7f;
            float side = lv.strafe ? (clockwise ? 0.6f : -0.6f) : 0f;
            bot.getMoveControl().strafeTo(fwd, side);
            if (lv.jump && bot.isOnGround() && bot.getRandom().nextInt(25) == 0) {
                bot.getJumpControl().setActive();
            }
        }

        // Simulated golden apple (regeneration + absorption), cooldown 30s.
        if (lv.useItems && lowHealth && now >= nextHeal) {
            bot.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
            bot.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0));
            nextHeal = now + 600;
        }

        // Attacking
        if (dist <= 3.0 && bot.canSee(t)) {
            if (!inRange) {
                inRange = true;
                reactionEnd = now + lv.reactionTicks;
            }
            if (now >= reactionEnd && now >= nextAttack) {
                boolean axe = bot.getMainHandStack().isIn(ItemTags.AXES);
                bot.swingHand(Hand.MAIN_HAND);
                if (bot.getRandom().nextFloat() < lv.accuracy) {
                    bot.tryAttack(bot.serverWorld(), t);
                }
                nextAttack = now + Math.max(lv.attackInterval, axe ? 20 : 12) + bot.getRandom().nextInt(4);
            }
        } else {
            inRange = false;
        }
    }
}
