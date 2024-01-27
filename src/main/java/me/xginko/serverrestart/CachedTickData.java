package me.xginko.serverrestart;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.papermc.paper.threadedregions.ThreadedRegionizer;
import io.papermc.paper.threadedregions.TickRegions;
import net.minecraft.world.level.Level;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public interface CachedTickData {

    double getTPS();
    double getMSPT();

    static @NotNull CachedTickData expireAfter(Duration cacheTime) {
        if (ServerRestart.isFolia) {
            return new Folia(ServerRestart.getInstance(), cacheTime);
        } else {
            return new Default(ServerRestart.getInstance(), cacheTime);
        }
    }

    final class Default implements CachedTickData {

        private final Server server;
        private final Cache<Boolean, Double> tpsCache;

        Default(JavaPlugin plugin, Duration cacheTime) {
            this.server = plugin.getServer();
            this.tpsCache = Caffeine.newBuilder().expireAfterWrite(cacheTime).build();
        }

        @Override
        public double getTPS() {
            Double tps = this.tpsCache.getIfPresent(true);
            if (tps == null) {
                tps = this.server.getTPS()[0];
                this.tpsCache.put(true, tps);
            }
            return tps;
        }

        @Override
        public double getMSPT() {
            Double tickTimeAvg = this.tpsCache.getIfPresent(false);
            if (tickTimeAvg == null) {
                tickTimeAvg = this.server.getAverageTickTime();
                this.tpsCache.put(false, tickTimeAvg);
            }
            // ServerRestart.getLog().info("Average Tick Time: " + tickTimeAvg);
            // ServerRestart.getLog().info("calculated mspt: " + (1000 / getTPS()));
            return tickTimeAvg;
        }
    }

    final class Folia implements CachedTickData {

        private final Server server;
        private final Set<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>> regions;
        private final Cache<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>, Double> tpsCache;
        private final Cache<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>, Double> msptCache;

        Folia(JavaPlugin plugin, Duration cacheTime) {
            this.server = plugin.getServer();
            this.regions = new HashSet<>();
            this.tpsCache = Caffeine.newBuilder().expireAfterWrite(cacheTime).build();
            this.msptCache = Caffeine.newBuilder().expireAfterWrite(cacheTime).build();
        }

        private Set<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>> getAllRegions() {
            for (final World world : server.getWorlds()) {
                ((Level) world).getWorld().getHandle().regioniser.computeForAllRegionsUnsynchronised(regions::add);
            }
            return regions;
        }

        @Override
        public double getTPS() {
            double lowestRegionTPS = 20.00;

            for (final ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData> region : this.getAllRegions()) {
                Double regionTPS = this.tpsCache.getIfPresent(region);
                if (regionTPS == null) {
                    regionTPS = region.getData().getRegionSchedulingHandle().getTickReport5s(System.nanoTime()).tpsData().segmentAll().average();
                    this.tpsCache.put(region, regionTPS);
                }
                if (regionTPS < lowestRegionTPS) {
                    lowestRegionTPS = regionTPS;
                }
            }

            regions.clear(); // clear regions after use
            return lowestRegionTPS;
        }

        @Override
        public double getMSPT() {
            double highestRegionMSPT = 00.00;

            for (final ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData> region : this.getAllRegions()) {
                Double regionMSPT = this.msptCache.getIfPresent(region);
                if (regionMSPT == null) {
                    regionMSPT = region.getData().getRegionSchedulingHandle().getTickReport5s(System.nanoTime()).timePerTickData().segmentAll().average();
                    this.tpsCache.put(region, regionMSPT);
                }
                // ServerRestart.getLog().info("Time per Tick average: " + regionMSPT);
                if (regionMSPT > highestRegionMSPT) {
                    highestRegionMSPT = regionMSPT;
                }
            }

            regions.clear(); // clear regions after use
            return highestRegionMSPT;
        }
    }
}