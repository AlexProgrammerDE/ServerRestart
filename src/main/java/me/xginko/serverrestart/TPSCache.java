package me.xginko.serverrestart;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.papermc.paper.threadedregions.*;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface TPSCache {

    double getGlobalTPS();
    double getLowestTPS();
    double getTPS(Event event);
    double getTPS(Location location);
    double getTPS(World world, int chunkX, int chunkZ);

    static @NotNull TPSCache create(long checkDelayMillis) {
        if (ServerRestart.isFolia()) {
            return new Folia(ServerRestart.getInstance(), checkDelayMillis);
        } else {
            return new Default(ServerRestart.getInstance(), checkDelayMillis);
        }
    }

    final class Default implements TPSCache {

        private final Server server;
        private final Cache<Object, Double> cached_tps;
        private static final Object TPS_KEY = new Object(); // Dummy value to associate with tps in the backing Cache

        Default(JavaPlugin plugin, long checkDelayMillis) {
            this.server = plugin.getServer();
            this.cached_tps = Caffeine.newBuilder().expireAfterWrite(Duration.ofMillis(checkDelayMillis)).build();
            this.getGlobalTPS();
        }

        @Override
        public double getGlobalTPS() {
            Double tps = this.cached_tps.getIfPresent(TPS_KEY);
            if (tps == null) {
                tps = this.server.getTPS()[0];
                this.cached_tps.put(TPS_KEY, tps);
            }
            return tps;
        }

        @Override
        public double getLowestTPS() {
            return getGlobalTPS();
        }

        @Override
        public double getTPS(Event event) {
            return getGlobalTPS();
        }

        @Override
        public double getTPS(World world, int chunkX, int chunkZ) {
            return getGlobalTPS();
        }

        @Override
        public double getTPS(Location location) {
            return getGlobalTPS();
        }
    }

    final class Folia implements TPSCache {

        private final JavaPlugin plugin;
        private final Server server;
        private final Cache<TickRegionScheduler.RegionScheduleHandle, Double> cached_tps;

        Folia(JavaPlugin plugin, long checkDelayMillis) {
            this.plugin = plugin;
            this.server = plugin.getServer();
            this.cached_tps = Caffeine.newBuilder().expireAfterWrite(Duration.ofMillis(checkDelayMillis)).build();
        }

        @Override
        public double getGlobalTPS() {
            // Get region handle and check if there is already a cached tps for it
            final TickRegionScheduler.RegionScheduleHandle regionHandle = RegionizedServer.getGlobalTickData();
            Double tps = this.cached_tps.getIfPresent(regionHandle);
            if (tps == null) {
                // If nothing is cached yet, get tps and add to cache
                tps = regionHandle.getTickReport5s(System.nanoTime()).tpsData().segmentAll().average();
                this.cached_tps.put(regionHandle, tps);
            }
            return tps;
        }

        @Override
        public double getLowestTPS() {
            final List<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>> regions =
                    new ArrayList<>();

            for (final World world : server.getWorlds()) {
                ((Level) world).getWorld().getHandle().regioniser.computeForAllRegions(regions::add);
            }

            double lowestTPS = 20.0;

            for (ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData> region : regions) {
                final TickRegionScheduler.RegionScheduleHandle regionHandle = region.getData().getRegionSchedulingHandle();
                Double tps = this.cached_tps.getIfPresent(regionHandle);
                if (tps == null) {
                    // If nothing is cached yet, get tps and add to cache
                    tps = regionHandle.getTickReport5s(System.nanoTime()).tpsData().segmentAll().average();
                    this.cached_tps.put(regionHandle, tps);
                }
                if (tps < lowestTPS) {
                    lowestTPS = tps;
                }
            }

            return lowestTPS;
        }

        /**
         *   USE THIS METHOD INSIDE EVENTS
         */
        @Override
        public double getTPS(Event event) {
            // Get the potential separate region that owns the location
            final ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>
                    currentRegion = TickRegionScheduler.getCurrentRegion();
            // If not happening on a separate region, it must mean we're on the main region
            if (currentRegion == null) {
                return getGlobalTPS();
            }
            // Get region handle and check if there is already a cached tps for it
            final TickRegionScheduler.RegionScheduleHandle regionHandle = currentRegion.getData().getRegionSchedulingHandle();
            Double tps = this.cached_tps.getIfPresent(regionHandle);
            if (tps == null) {
                // If nothing is cached yet, get tps and add to cache
                tps = regionHandle.getTickReport5s(System.nanoTime()).tpsData().segmentAll().average();
                this.cached_tps.put(regionHandle, tps);
            }
            return tps;
        }

        /**
         *   DO NOT USE THIS METHOD INSIDE EVENTS, THIS IS ONLY MEANT FOR SCHEDULED CHECKS!
         *   Uses region scheduler to get a TPS at a certain location, waiting for a result if necessary
         */
        @Override
        public double getTPS(Location location) {
            if (location == null) return getGlobalTPS();
            return getTPS(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
        }

        /**
         *   DO NOT USE THIS METHOD INSIDE EVENTS, THIS IS ONLY MEANT FOR SCHEDULED CHECKS!
         *   Uses region scheduler to get a TPS at a certain location, waiting for a result if necessary
         */
        @Override
        public double getTPS(World world, int chunkX, int chunkZ) {
            if (world == null) return getGlobalTPS();
            CompletableFuture<Double> result = new CompletableFuture<>();
            this.server.getRegionScheduler().execute(this.plugin, world, chunkX, chunkZ, () -> {
                // Get the potential separate region that owns the location
                final ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>
                        currentRegion = TickRegionScheduler.getCurrentRegion();
                // If not happening on a separate region, it must mean we're on the main region
                if (currentRegion == null) {
                    result.complete(getGlobalTPS());
                    return;
                }
                // Get region handle and check if there is already a cached tps for it
                final TickRegionScheduler.RegionScheduleHandle regionHandle = currentRegion.getData().getRegionSchedulingHandle();
                Double tps = this.cached_tps.getIfPresent(regionHandle);
                if (tps == null) {
                    // If nothing is cached yet, get tps and add to cache
                    tps = regionHandle.getTickReport5s(System.nanoTime()).tpsData().segmentAll().average();
                    this.cached_tps.put(regionHandle, tps);
                }
                result.complete(tps);
            });

            try {
                return result.get();
            } catch (InterruptedException | ExecutionException e) {
                return getGlobalTPS();
            }
        }
    }
}