package me.xginko.serverrestart.paper;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.xginko.serverrestart.common.CachedTickReport;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

public final class PaperTickReport implements CachedTickReport {

    private final Server server;
    private final Cache<Boolean, Double> tpsCache;

    public PaperTickReport(JavaPlugin plugin, Duration cacheTime) {
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
        Double avgTickTime = this.tpsCache.getIfPresent(false);
        if (avgTickTime == null) {
            avgTickTime = this.server.getAverageTickTime();
            this.tpsCache.put(false, avgTickTime);
        }
        return avgTickTime;
    }
}
