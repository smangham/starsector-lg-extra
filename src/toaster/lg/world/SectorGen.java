package toaster.lg.world;

import com.fs.starfarer.api.campaign.SectorAPI;
import toaster.lg.world.systems.LGDecommissioned;

/**
 * Generates the mod content.
 * <p>
 * Pretty simple.
 */
public class SectorGen {
    /**
     * Generates the mod content.
     *
     * @param sector The active sector.
     */
    public void generate(SectorAPI sector) {
        new LGDecommissioned().generate(sector);
    }
}
