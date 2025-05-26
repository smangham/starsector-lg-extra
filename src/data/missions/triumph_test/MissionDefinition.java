package data.missions.triumph_test;

import java.util.List;

import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.impl.combat.EscapeRevealPlugin;


public class MissionDefinition implements MissionDefinitionPlugin {

	public void defineMission(MissionDefinitionAPI api) {
		// Set up the fleets so we can add ships and fighter wings to them.
		// In this scenario, the fleets are attacking each other, but
		// in other scenarios, a fleet may be defending or trying to escape
		api.initFleet(FleetSide.PLAYER, "LGS", FleetGoal.ESCAPE, false, 5);
		api.initFleet(FleetSide.ENEMY, "ISS", FleetGoal.ATTACK, true);

		// Set a small blurb for each fleet that shows up on the mission detail and
		// mission results screens to identify each side.
		api.setFleetTagline(FleetSide.PLAYER, "Sindran Diktat Patrol");
		api.setFleetTagline(FleetSide.ENEMY, "Mercenary Saboteurs");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("The LGS Lion's Roar must escape");
		api.addBriefingItem("Remember you can use the Refit screen to modify the loadout of your ships.");

		// Set up the player's fleet.  Variant names come from the
		// files in data/variants and data/variants/fighters
		//api.addToFleet(FleetSide.PLAYER, "harbinger_Strike", FleetMemberType.SHIP, "TTS Invisible Hand", true);
		api.addToFleet(FleetSide.PLAYER, "toaster_conquest2_lg_Standard", FleetMemberType.SHIP, "LGS Lion's Roar", true);

		api.addToFleet(FleetSide.PLAYER, "toaster_champion_lg_Standard", FleetMemberType.SHIP, "LGS Pride of Volturn", false);

		api.addToFleet(FleetSide.PLAYER, "hammerhead_LG_Balanced", FleetMemberType.SHIP, "LGS Untarnished Glory", false);
		api.addToFleet(FleetSide.PLAYER, "sunder_LG_CS", FleetMemberType.SHIP, "LGS Promise of Triumph", false);

		api.addToFleet(FleetSide.PLAYER, "centurion_LG_Assault", FleetMemberType.SHIP, "LGS Patriots of Askonia", false);
		api.addToFleet(FleetSide.PLAYER, "brawler_LG_Elite", FleetMemberType.SHIP, "LGS Inevitable Conquest", false);

		api.defeatOnShipLoss("LGS Lion's Roar");
		
		// Set up the enemy fleet.
		api.addToFleet(FleetSide.ENEMY, "gryphon_Standard", FleetMemberType.SHIP,  true);
		api.addToFleet(FleetSide.ENEMY, "falcon_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "heron_Attack1", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "mora_Torpedo", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.ENEMY, "medusa_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hammerhead_Elite", FleetMemberType.SHIP,  false);

		api.addToFleet(FleetSide.ENEMY, "vigilance_Support1", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "vigilance_Support1", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "brawler_Assault", FleetMemberType.SHIP, false);
		
		// Set up the map.
		float width = 24000f;
		float height = 18000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;

		
		// Add an asteroid field
		api.addAsteroidField(
				0, 0, 0, width, 20f, 70f, 250
		);

		api.addPlanet(
				0, 0, 250f, "gas_giant", 500f, true
		);
		api.addPlanet(
				220, 100, 50f, "water", 100f, true
		);

		BattleCreationContext context = new BattleCreationContext(
				null, null, null, null
		);
		context.setInitialEscapeRange(height/2);
		context.setEscapeDeploymentBurnDuration(2f);
		api.addPlugin(new EscapeRevealPlugin(context));
	}
}




