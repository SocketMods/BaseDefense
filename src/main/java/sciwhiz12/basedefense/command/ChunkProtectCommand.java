package sciwhiz12.basedefense.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.UsernameCache;
import sciwhiz12.basedefense.world.ProtectedChunksSavedData;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.arguments.EntityArgument.getPlayer;
import static net.minecraft.command.arguments.EntityArgument.player;
import static net.minecraft.command.arguments.GameProfileArgument.gameProfile;
import static net.minecraft.command.arguments.GameProfileArgument.getGameProfiles;
import static net.minecraft.util.text.TextFormatting.*;

public class ChunkProtectCommand {
    public static LiteralArgumentBuilder<CommandSource> nodeBuilder() {
        // @formatter:off
        return literal("chunk")
            .then(literal("claim").executes(ctx -> claimChunk(ctx.getSource(), false)))
            .then(
                literal("unclaim")
                    .then(literal("all")
                            .executes(ctx -> unclaimAllChunks(ctx.getSource(), Collections.singleton(ctx.getSource().asPlayer().getGameProfile()), false)))
                    .executes(ctx -> unclaimChunk(ctx.getSource(), false))
            ).then(
                literal("info")
                    .then(argument("target", player())
                            .executes(ctx -> displayAllChunksInfo(ctx.getSource(), getPlayer(ctx, "target"))))
                    .executes(ctx -> displayCurrentChunkInfo(ctx.getSource()))
            ).then(
                literal("admin").requires(ChunkProtectCommand::hasAdminPermission)
                    .then(literal("claim")
                            .executes(ctx -> claimChunk(ctx.getSource(), true)))
                    .then(
                        literal("unclaim")
                            .then(argument("target", gameProfile())
                                    .executes(ctx -> unclaimAllChunks(ctx.getSource(), getGameProfiles(ctx, "target"), true)))
                            .executes(ctx -> unclaimChunk(ctx.getSource(), true))
                    )
            );
        // @formatter:on
    }

    private static final Dynamic2CommandExceptionType CHUNK_ALREADY_CLAIMED = new Dynamic2CommandExceptionType(
            (first, second) -> adminWrap(
                    new TranslationTextComponent("command.basedefense.chunk.claim.error.already_claimed", second),
                    Boolean.parseBoolean(first.toString())));
    private static final Dynamic2CommandExceptionType CHUNK_NOT_CLAIMED = new Dynamic2CommandExceptionType(
            (first, second) -> adminWrap(
                    new TranslationTextComponent("command.basedefense.chunk.unclaim.error.not_claimed", second),
                    Boolean.parseBoolean(first.toString())));
    private static final DynamicCommandExceptionType NOT_CHUNK_OWNER = new DynamicCommandExceptionType(
            first -> new TranslationTextComponent("command.basedefense.chunk.unclaim.error.not_owner", first));
    private static final DynamicCommandExceptionType UNCLAIM_ALL_FAILED = new DynamicCommandExceptionType(
            first -> adminWrap(new TranslationTextComponent("command.basedefense.chunk.unclaim_all.error.failed"),
                    Boolean.parseBoolean(first.toString())));

    private static int claimChunk(CommandSource executor, boolean admin) throws CommandSyntaxException {
        ServerPlayerEntity player = executor.asPlayer();
        ProtectedChunksSavedData savedData = ProtectedChunksSavedData.getFromWorld(player.getServerWorld());
        UUID executorID = admin ? Util.DUMMY_UUID : player.getGameProfile().getId();
        ChunkPos chunkPos = player.getServerWorld().getChunk(player.getPosition()).getPos();
        long chunkID = chunkPos.asLong();
        if (savedData.isChunkOwned(chunkID)) {
            throw CHUNK_NOT_CLAIMED.create(admin, pos(chunkPos));
        } else {
            savedData.addOwnedChunk(executorID, chunkID);
            executor.sendFeedback(adminWrap(
                    new TranslationTextComponent("command.basedefense.chunk.claim.success", pos(chunkPos)).mergeStyle(GREEN),
                    admin), true);
            return 1;
        }
    }

    private static int unclaimChunk(CommandSource executor, boolean admin) throws CommandSyntaxException {
        ServerPlayerEntity player = executor.asPlayer();
        ProtectedChunksSavedData savedData = ProtectedChunksSavedData.getFromWorld(player.getServerWorld());
        UUID executorID = admin ? Util.DUMMY_UUID : player.getGameProfile().getId();
        ChunkPos chunkPos = player.getServerWorld().getChunk(player.getPosition()).getPos();
        long chunkID = chunkPos.asLong();
        if (!savedData.isChunkOwned(chunkID)) {
            throw CHUNK_NOT_CLAIMED.create(admin, pos(chunkPos));
        } else if (!admin && !savedData.getOwnedChunks(executorID).contains(chunkID)) {
            throw NOT_CHUNK_OWNER.create(pos(chunkPos));
        } else {
            savedData.removeChunkOwner(chunkID);
            executor.sendFeedback(adminWrap(
                    new TranslationTextComponent("command.basedefense.chunk.unclaim.success", pos(chunkPos))
                            .mergeStyle(GREEN), admin), true);
            return 1;
        }
    }

    private static int unclaimAllChunks(CommandSource executor, Collection<GameProfile> targets, boolean admin)
            throws CommandSyntaxException {
        ProtectedChunksSavedData savedData = ProtectedChunksSavedData.getFromWorld(executor.getWorld());
        int result = 0;
        for (GameProfile profile : targets) {
            if (profile.getId() != null) {
                if (!savedData.getOwnedChunks(profile.getId()).isEmpty()) {
                    result += savedData.getOwnedChunks(profile.getId()).size();
                    savedData.clearOwnedChunks(profile.getId());
                    executor.sendFeedback(adminWrap(
                            new TranslationTextComponent("command.basedefense.chunk.unclaim_all.success",
                                    TextComponentUtils.getDisplayName(profile)), admin), true);
                }
            }
        }
        if (result < 0) {
            throw UNCLAIM_ALL_FAILED.create(admin);
        } else {
            return result;
        }
    }

    private static int displayCurrentChunkInfo(CommandSource executor) {
        ProtectedChunksSavedData savedData = ProtectedChunksSavedData.getFromWorld(executor.getWorld());
        ChunkPos chunkPos = executor.getWorld().getChunk(new BlockPos(executor.getPos())).getPos();
        long chunkID = chunkPos.asLong();
        UUID ownerUID = savedData.getChunkOwner(chunkID);
        IFormattableTextComponent ownerText = namedUID(ownerUID, executor.getServer());
        executor.sendFeedback(
                new TranslationTextComponent("command.basedefense.chunk.info", pos(chunkPos), ownerText).mergeStyle(WHITE),
                true);
        return 1;
    }

    private static int displayAllChunksInfo(CommandSource executor, ServerPlayerEntity targetPlayer) {
        return 1;
    }

    private static boolean hasAdminPermission(CommandSource source) {
        return source.hasPermissionLevel(3);
    }

    private static IFormattableTextComponent namedUID(@Nullable UUID uuid, @Nullable MinecraftServer server) {
        if (uuid == null) { // not owned
            return new TranslationTextComponent("command.basedefense.chunk.info.owner.none").mergeStyle(RED);
        } else if (uuid.equals(Util.DUMMY_UUID)) { // server
            return new TranslationTextComponent("command.basedefense.chunk.info.owner.server").mergeStyle(LIGHT_PURPLE);
        } else { // UUID _may_ be associated with player
            IFormattableTextComponent username = null;
            if (server != null) {
                ServerPlayerEntity player = server.getPlayerList().getPlayerByUUID(uuid);
                if (player != null) {
                    username = player.getDisplayName().deepCopy();
                    if (username.getStyle().getColor() == null) {
                        username.mergeStyle(WHITE);
                    }
                }
            }
            if (username == null) {
                String lastKnownName = UsernameCache.getLastKnownUsername(uuid);
                if (lastKnownName != null) {
                    username = new StringTextComponent(lastKnownName).modifyStyle((style) -> style.setHoverEvent(
                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(uuid.toString())))
                            .setInsertion(lastKnownName).applyFormatting(GRAY));
                }
            }

            if (username != null) {
                return new TranslationTextComponent("command.basedefense.chunk.info.owner.named", username).mergeStyle(BLUE);
            } else {
                return new TranslationTextComponent("command.basedefense.chunk.info.owner.unknown").modifyStyle(
                        style -> style.applyFormatting(YELLOW).setHoverEvent(
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(uuid.toString()))));
            }
        }
    }

    private static IFormattableTextComponent pos(ChunkPos pos) {
        return new TranslationTextComponent("command.basedefense.chunk.position", pos.x, pos.z).mergeStyle(GOLD);
    }

    private static IFormattableTextComponent adminWrap(IFormattableTextComponent text, boolean admin) {
        if (admin) {
            return adminWrap(text);
        }
        return text;
    }

    private static IFormattableTextComponent adminWrap(IFormattableTextComponent text) {
        return new StringTextComponent("")
                .append(new TranslationTextComponent("command.basedefense.admin").mergeStyle(LIGHT_PURPLE)).append(text);
    }
}
