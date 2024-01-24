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

public interface TPSCache {

    double getTPS();

    static @NotNull TPSCache create(Duration cacheTime) {
        if (ServerRestart.isFolia()) {
            return new Folia(ServerRestart.getInstance(), cacheTime);
        } else {
            return new Default(ServerRestart.getInstance(), cacheTime);
        }
    }

    final class Default implements TPSCache {

        private final Server server;
        private final Cache<Boolean, Double> tpsCache;

        Default(JavaPlugin plugin, Duration cacheTime) {
            this.server = plugin.getServer();
            this.tpsCache = Caffeine.newBuilder().expireAfterWrite(cacheTime).build();
            this.getTPS(); // Run once to fill cache
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
    }

    final class Folia implements TPSCache {

        private final Server server;
        private final Set<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>> regions;
        private final Cache<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>, Double> tpsCache;

        Folia(JavaPlugin plugin, Duration cacheTime) {
            this.server = plugin.getServer();
            this.regions = new HashSet<>();
            this.tpsCache = Caffeine.newBuilder().expireAfterWrite(cacheTime).build();
            this.getTPS(); // Run once to fill cache
        }

        @Override
        public double getTPS() {
            // Get all current regions
            for (final World world : server.getWorlds()) {
                ((Level) world).getWorld().getHandle().regioniser.computeForAllRegions(regions::add);
            }

            // Since we only need to check for critically low TPS, this is totally enough to get what we need
            double lowestRegionTPS = 20.0;

            for (final ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData> region : regions) {
                Double regionTPS = this.tpsCache.getIfPresent(region);
                if (regionTPS == null) {
                    regionTPS = region.getData().getRegionSchedulingHandle().getTickReport5s(System.nanoTime()).tpsData().segmentAll().average();
                    this.tpsCache.put(region, regionTPS);
                }

                if (regionTPS < lowestRegionTPS) {
                    lowestRegionTPS = regionTPS;
                }
            }

            regions.clear(); // cleanup regions after use
            return lowestRegionTPS;
        }
    }
}