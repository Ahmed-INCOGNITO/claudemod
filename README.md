# PvP Master (v0.1, UNTESTED source)

Fabric mod for Minecraft Java 1.21.11. Needs Java 21, Fabric Loader >= 0.18, Fabric API.

## Build
Local:  install Gradle 9.2.1, then `gradle build` -> build/libs/pvpmaster-0.1.0.jar
Cloud:  push to GitHub; the Actions workflow builds the jar (Artifacts > pvpmaster-jar).
Check https://fabricmc.net/develop for the current yarn_mappings / fabric_version and edit gradle.properties if needed.
Put the jar (not the -sources jar) in .minecraft/mods together with Fabric API.

## Use (survival mode; creative players aren't damaged)
/pvpmenu                                   opens the menu
/pvp spawn <sword|axe> <easy|normal|hard|expert> [1-5]
/pvp clear

## Implemented
Custom bot entity, goal-based melee AI (approach, spacing, strafing, jumping, retreat, reaction delay, miss chance,
axe/sword cooldowns), real vanilla damage/knockback via the held weapon, diamond armor, simulated golden apple
healing, menu GUI, commands.
## Not implemented
Other modes (mace, crystal, UHC, pot, SMP, custom), shield use, arenas, HUD, statistics, armor rendering on bots.

## If compile errors
BotEntity.serverWorld(): if getEntityWorld() is missing, use getWorld().
