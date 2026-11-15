package com.pvpmaster.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exception.CommandSyntaxException;
import com.pvpmaster.PvpMaster;
import com.pvpmaster.entity.BotEntity;
import com.pvpmaster.entity.BotLevel;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;

import java.util.List;

public class PvpCommands {
    private static final String[] MODES = {"sword", "axe"};

    public static void register(CommandDispatcher<ServerCommandSource> d) {
        d.register(CommandManager.literal("pvp")
                .then(CommandManager.literal("spawn")
                        .then(CommandManager.argument("mode", StringArgumentType.word())
                                .suggests((c, b) -> CommandSource.suggestMatching(MODES, b))
                                .then(CommandManager.argument("level", StringArgumentType.word())
                                        .suggests((c, b) -> CommandSource.suggestMatching(
                                                new String[]{"easy", "normal", "hard", "expert"}, b))
                                        .executes(c -> spawn(c, 1))
                                        .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 5))
                                                .executes(c -> spawn(c, IntegerArgumentType.getInteger(c, "count")))))))
                .then(CommandManager.literal("clear").executes(PvpCommands::clear)));
    }

    private static int spawn(CommandContext<ServerCommandSource> ctx, int count) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();
        ServerPlayerEntity p = src.getPlayerOrThrow();
        ServerWorld w = src.getWorld();
        String mode = StringArgumentType.getString(ctx, "mode");
        BotLevel lv = BotLevel.parse(StringArgumentType.getString(ctx, "level"));
        if (lv == null) {
            src.sendError(Text.literal("Level must be easy, normal, hard or expert."));
            return 0;
        }
        double yaw = Math.toRadians(p.getYaw());
        for (int i = 0; i < count; i++) {
            BotEntity bot = PvpMaster.BOT.create(w, SpawnReason.COMMAND);
            if (bot == null) continue;
            double dist = 5.0 + i * 1.5;
            bot.setPosition(p.getX() - Math.sin(yaw) * dist, p.getY(), p.getZ() + Math.cos(yaw) * dist);
            bot.setYaw(p.getYaw() + 180f);
            bot.configure(mode, lv, "Bot " + lv.name().charAt(0) + lv.name().substring(1).toLowerCase(), p);
            w.spawnEntity(bot);
        }
        src.sendFeedback(() -> Text.literal("Spawned " + count + " " + lv.name().toLowerCase() + " bot(s). Use /pvp clear to remove."), false);
        return count;
    }

    private static int clear(CommandContext<ServerCommandSource> ctx) {
        ServerWorld w = ctx.getSource().getWorld();
        List<? extends BotEntity> bots = w.getEntitiesByType(TypeFilter.instanceOf(BotEntity.class), e -> true);
        bots.forEach(BotEntity::discard);
        int n = bots.size();
        ctx.getSource().sendFeedback(() -> Text.literal("Removed " + n + " bot(s)."), false);
        return n;
    }
}
