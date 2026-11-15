package com.pvpmaster.entity;

import com.pvpmaster.entity.ai.BotCombatGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class BotEntity extends PathAwareEntity {
    private BotLevel level = BotLevel.NORMAL;

    public BotEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createBotAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.29)
                .add(EntityAttributes.ATTACK_DAMAGE, 1.0)
                .add(EntityAttributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new BotCombatGoal(this));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public BotLevel getBotLevel() { return level; }

    /** Single place that needs the world; if Yarn names differ, fix only here. */
    public ServerWorld serverWorld() { return (ServerWorld) this.getEntityWorld(); }

    public void configure(String mode, BotLevel level, String name, PlayerEntity target) {
        this.level = level;
        this.setPersistent();
        this.setCustomName(Text.literal(name));
        this.setCustomNameVisible(true);
        this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
        this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
        this.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
        this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
        this.equipStack(EquipmentSlot.MAINHAND,
                new ItemStack("axe".equalsIgnoreCase(mode) ? Items.DIAMOND_AXE : Items.DIAMOND_SWORD));
        if (target != null) this.setTarget(target);
    }

    @Override
    public void onDeath(DamageSource source) {
        if (source.getAttacker() instanceof ServerPlayerEntity p) {
            p.sendMessage(Text.literal("Victory! You defeated " + this.getName().getString()), true);
        }
        // Training bots never drop gear.
        for (EquipmentSlot s : EquipmentSlot.VALUES) this.equipStack(s, ItemStack.EMPTY);
        super.onDeath(source);
    }
}
