package com.pvpmaster;

import com.pvpmaster.command.PvpCommands;
import com.pvpmaster.entity.BotEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class PvpMaster implements ModInitializer {
    public static final String MOD_ID = "pvpmaster";

    public static final RegistryKey<EntityType<?>> BOT_KEY =
            RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "bot"));

    public static final EntityType<BotEntity> BOT = Registry.register(
            Registries.ENTITY_TYPE, BOT_KEY,
            EntityType.Builder.create(BotEntity::new, SpawnGroup.MISC)
                    .dimensions(0.6f, 1.8f)
                    .maxTrackingRange(8)
                    .build(BOT_KEY));

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(BOT, BotEntity.createBotAttributes());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                PvpCommands.register(dispatcher));
    }
}
