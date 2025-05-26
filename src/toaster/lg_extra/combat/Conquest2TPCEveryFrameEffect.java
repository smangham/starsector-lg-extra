package toaster.lg_extra.combat;
import java.awt.Color;
import java.util.List;

import com.fs.starfarer.api.combat.*;
import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.EmpArcEntityAPI.EmpArcParams;
import com.fs.starfarer.api.util.Misc;

import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;

/**
 */

public class Conquest2TPCEveryFrameEffect implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {
    private static final Logger log = Logger.getLogger(Conquest2TPCEveryFrameEffect.class);
    public static float ARC_THICKNESS = 12f;
    public static float ARC_CORE_WIDTH_MULT = 0.33f;

    public static int RATIO_ARC_UPPER = 3;

    public static float ARC_POSITION_X_TOP = 16f;
    public static float ARC_RANGE_Y_TOP_MIN = 141f;
    public static float ARC_RANGE_Y_TOP_MAX = 166f;

    public static float ARC_POSITION_X_BOTTOM = 14.5f;
    public static float ARC_RANGE_Y_BOTTOM_MIN = 56f;
    public static float ARC_RANGE_Y_BOTTOM_MAX = 100f;

    public static float ARC_RANGE_SEARCH = 40f;

    // Standard limits for the timers
    public static float TIMER_FX_MIN = 0.05f;
    public static float TIMER_FX_MAX = 0.07f;
    public static float TIMER_ACTIVE_FX = 0.5f;

    // Save local copies of the ship, colour e.t.c.
    protected ShipAPI ship = null;
    protected Color colour = null;
    protected EmpArcParams empArcParams;

    // Timers for the FX instances, and activity entirely
    protected float timerFxActive = 0f;
    protected float timerFx = 0f;

    /**
     * Steps forward each frame, playing the visual FX
     *
     * @param amount: The amount of time that has progressed since last frame.
     * @param engine: The game engine.
     * @param weapon: The weapon that this code belongs to.
     */
    public void advance(
            float amount,
            CombatEngineAPI engine,
            WeaponAPI weapon
    ) {
        if (this.timerFxActive <= 0) {
            // The TPC FX aren't active, it hasn't fired recently
            return;

        } else {
            // Advance the cooldown for turning the FX off
//            log.info(
//                "Conquest2TPC.advance: Active timer " + amount + " / " + this.timer_fx_active
//            );
            this.timerFxActive -= amount;
        }

        if (this.timerFx > 0) {
            // The TPC FX hasn't triggered yet
//            log.info(
//                "Conquest2TPC.advance: Effects timer " + amount + " / " + this.timer_fx
//            );
            this.timerFx -= amount;
            return;

        } else {
            // Trigger the TPC FX
            this.timerFx += Misc.random.nextFloat(
                    TIMER_FX_MIN, TIMER_FX_MAX
            );
//            log.info(
//                    "Conquest2TPC.advance: FX triggered, resetting timer to " + this.timer_fx
//            );
        }

        // Randomly decide where to start the FX - 3/4 bottom, 1/4 top
        float position_x, position_y;
        if (Misc.random.nextInt(0, RATIO_ARC_UPPER) > 0) {
            position_x = ARC_POSITION_X_BOTTOM * (Misc.random.nextBoolean() ? 1 : -1);
            position_y = Misc.random.nextFloat(
                    ARC_RANGE_Y_BOTTOM_MIN, ARC_RANGE_Y_BOTTOM_MAX
            );
        } else {
            position_x = ARC_POSITION_X_TOP * (Misc.random.nextBoolean() ? 1 : -1);
            position_y = Misc.random.nextFloat(
                    ARC_RANGE_Y_TOP_MIN,
                    ARC_RANGE_Y_TOP_MAX
            );
        }

        // Get the starting position of the arc
        Vector2f vector_source = getVectorAbsolute(position_x, position_y);
        List<DamagingProjectileAPI> projectile_list = CombatUtils.getProjectilesWithinRange(
                vector_source,
                ARC_RANGE_SEARCH
        );
//        log.info(
//                "Conquest2TPC.advance: Found " + projectile_list.size() + " projectiles"
//        );

        // Now, time to look for a projectile to pin the arcs to...
        DamagingProjectileAPI projectile = null;
        if (!projectile_list.isEmpty()) {
            // Look through the projectiles for one that's matching the weapon spec
            for (DamagingProjectileAPI projectile_item : projectile_list) {
                // Was this projectile fired by this weapon?
//                log.info("Projectile: "+projectile_item+", weapon it's from: "+projectile_item.getWeapon()+", this weapon: "+weapon);
                if (projectile_item.getWeapon() == weapon) {
                    projectile = projectile_item;
                    break;
                }
            }
        }

        if (projectile != null) {
            // If we've found a projectile, create an arc to it
            createDecorativeArcToProjectile(
                    vector_source,
                    projectile,
                    engine
            );

        } else {
            // If we haven't, create an arc across the forks
            createDecorativeArcAcross(
                    vector_source,
                    getVectorAbsolute(
                            -position_x,
                            position_y
                    ),
                    engine
            );
        }
    }

    /**
     * Starts the timers when the weapon is fired
     *
     * @param projectile: The projectile that was just fired, if any.
     * @param weapon: The weapon that this code belongs to.
     * @param engine: The game engine.
     */
    public void onFire(
            DamagingProjectileAPI projectile,
            WeaponAPI weapon,
            CombatEngineAPI engine
    ) {
//        log.info(
//                "Conquest2TPC.onFire: Fired"
//        );

        if (projectile != null) {
            // The weapon is actively firing
            this.timerFxActive = TIMER_ACTIVE_FX;
        }

        if (this.ship == null) {
            // This is the first time it's fired, so set the variables
            this.ship = weapon.getShip();
            this.colour = weapon.getSpec().getGlowColor();

            this.empArcParams = new EmpArcParams();
            // Set arcs to fade out when they're slightly too far away
            this.empArcParams.fadeOutDist = ARC_RANGE_SEARCH;
            // Allow the arcs to be more wiggly
            this.empArcParams.segmentLengthMult = 0.25f;
            // Reduce the size of the start/end glow
            this.empArcParams.glowSizeMult = 0.5f;
            this.empArcParams.glowAlphaMult = 0.5f;
        }

//        params.zigZagReductionFactor = 0.25f;
        //params.maxZigZagMult = 0f;
        //params.flickerRateMult = 0.75f;
//        empArcParams.flickerRateMult = 1f;
//        empArcParams.fadeOutDist = 32f;
//        empArcParams.minFadeOutMult = 1f;
//		params.fadeOutDist = 200f;
//		params.minFadeOutMult = 2f;
//        params.glowSizeMult = 0.5f;
//        params.glowAlphaMult = 0.5f;
//        params.brightSpotFadeFraction = 0.5f;
//        params.brightSpotFullFraction = 0.5f;
//
//          log.info("Conquest2TPC.onFire: Fired over");
    }

    /**
     * Re-adjusts a vector from relative to the ship graphic, to relative to its heading.
     *
     * @param x: The x-position (relative to the ship sprite).
     * @param y: The y-position (relative to the ship sprite).
     * @return vector_result: A 2-d vector of those two relative to the ship location and heading in the combat.
     */
    public Vector2f getVectorAbsolute(
            float x,
            float y
    ) {
        Vector2f vector_result = new Vector2f(x, y);
        Vector2f.add(
                this.ship.getLocation(),
                vector_result,
                vector_result
        );

        return VectorUtils.rotateAroundPivot(
                vector_result,
                ship.getLocation(),
                ship.getFacing() - 90.0f
        );
    }

    /**
     * Creates an arc that goes to a projectile.
     *
     * @param vector_source: The absolute location at which the arc starts.
     * @param projectile: The projectile the arc goes to.
     * @param engine: The game engine.
     */
    public void createDecorativeArcToProjectile(
            Vector2f vector_source,
            DamagingProjectileAPI projectile,
            CombatEngineAPI engine
    ) {
//        log.info(
//                "Conquest2TPC.createDecorativeArcToProjectile: Creating targeted arc"
//        );
        EmpArcEntityAPI arc = engine.spawnEmpArcVisual(
                vector_source,
                this.ship,
                projectile.getLocation(),
                projectile,
                ARC_THICKNESS,
                this.colour,
                Color.white,
                this.empArcParams
        );
        arc.setCoreWidthOverride(
                ARC_THICKNESS * ARC_CORE_WIDTH_MULT
        );
        arc.setUpdateFromOffsetEveryFrame(true);
        arc.setSingleFlickerMode(true);
    }

    /**
     * Creates an arc that goes to the opposite fork.
     *
     * @param vector_source: The absolute location at which the arc starts.
     * @param vector_target: The absolute location at which the arc ends.
     * @param engine: The game engine.
     */
    public void createDecorativeArcAcross(
            Vector2f vector_source,
            Vector2f vector_target,
            CombatEngineAPI engine
    ) {
//        log.info(
//                "Conquest2TPC.createDecorativeArcAcross: Creating across arc"
//        );
        EmpArcEntityAPI arc = engine.spawnEmpArcVisual(
                vector_source,
                this.ship,
                vector_target,
                this.ship,
                ARC_THICKNESS,
                this.colour,
                Color.white,
                this.empArcParams
        );
//        arc.setUpdateFromOffsetEveryFrame(true);
        arc.setCoreWidthOverride(
                ARC_THICKNESS * ARC_CORE_WIDTH_MULT
        );
    }
}
