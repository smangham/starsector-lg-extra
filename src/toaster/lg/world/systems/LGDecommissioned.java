package toaster.lg.world.systems;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import toaster.lg.campaign.ids.Submarkets;

import java.util.Objects;


/**
 * Initialises mod content in the Arcadia system.
 * <p>
 * Pretty basic thing.
 */
public class LGDecommissioned {
    /**
     * Generates the mod submarkets e.t.c. in the system.
     * <p>
     * This is a little awkward because the market key "agreus" isn't consistent so we have to find directly.
     *
     * @param sector The active sector.
     */
    public void generate(SectorAPI sector) {
        MarketAPI market = null;

        for (PlanetAPI planet : sector.getStarSystem("Arcadia").getPlanets()) {
            if (Objects.equals(planet.getName(), "Agreus")) {
                market = planet.getMarket();
            }
        }

        if (market == null) {
            // log.warn("tstHP: No market initialised for Agreus, somehow?");
        } else {
            // Find the submarket, or create it.
            if (market.getSubmarket(Submarkets.LG_DECOMMISSIONED) == null) {
                market.addSubmarket(Submarkets.LG_DECOMMISSIONED);
            }
        }
    }
}
