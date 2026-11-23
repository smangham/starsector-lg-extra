package toaster.lg;
import com.fs.starfarer.api.Global;
import toaster.lg.world.SectorGen;

import com.fs.starfarer.api.BaseModPlugin;

public class ToasterLGModPlugin extends BaseModPlugin {
    /**
     * Does the setup.
     */
    public static void setUp() {
        new SectorGen().generate(Global.getSector());
    }

    /**
     * Run after the economy has been run on a new game.
     * <p>
     * Doing this, rather than `onNewGame`, to ensure that Agreus e.t.c. exists.
     */
    @Override
    public void onNewGameAfterEconomyLoad() {
        super.onNewGameAfterEconomyLoad();
        setUp();
    }

    /**
     * Do sector gen and hyperspace plugin registration on load.
     *
     * @param newGame Is this the 'load' of  starting a new game, true or false.
     */
    @Override
    public void onGameLoad(boolean newGame) {
        super.onGameLoad(newGame);
        setUp();
    }
}
