package me.xginko.serverrestart.folia;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.papermc.paper.threadedregions.ThreadedRegionizer;
import io.papermc.paper.threadedregions.TickRegions;
import me.xginko.serverrestart.common.CachedTickReport;
import net.minecraft.world.level.Level;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public final class FoliaTickReport implements CachedTickReport {

    private final Server server;
    private final Set<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>> regions;
    private final Cache<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>, Double> tpsCache;
    private final Cache<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>, Double> msptCache;

    public FoliaTickReport(JavaPlugin plugin, Duration cacheTime) {
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
            if (regionMSPT > highestRegionMSPT) {
                highestRegionMSPT = regionMSPT;
            }
        }

        regions.clear(); // clear regions after use
        return highestRegionMSPT;
    }
}
