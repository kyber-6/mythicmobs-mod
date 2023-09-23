package net.pixeldream.mythicmobs;

import eu.midnightdust.lib.config.MidnightConfig;
import mod.azure.azurelib.AzureLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.pixeldream.mythicmobs.config.MythicMobsConfigs;
import net.pixeldream.mythicmobs.event.EntityEvents;
import net.pixeldream.mythicmobs.registry.*;
import net.pixeldream.mythicmobs.world.gen.WorldGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.pixeldream.mythicmobs.registry.ItemRegistry.AUTOMATON_HEAD;

public class MythicMobs implements ModInitializer {
	public static final String MOD_ID = "mythicmobs";
	public static final String MOD_NAME = "Mythic Mobs";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "item_group"), () -> new ItemStack(AUTOMATON_HEAD));

	@Override
	public void onInitialize() {
		LOGGER.info("Hello from " + MOD_NAME + "!");
		LOGGER.info("Initializing GeckoLib for " + MOD_NAME);
		AzureLib.initialize();
		LOGGER.info("Initializing MidnightConfig for " + MOD_NAME);
		MidnightConfig.init(MOD_ID, MythicMobsConfigs.class);
		LOGGER.info("Registering entities for " + MOD_NAME);
		new EntityRegistry();
		LOGGER.info("Registering particles for " + MOD_NAME);
		new ParticleRegistry();
		LOGGER.info("Registering items for " + MOD_NAME);
		new ItemRegistry();
		LOGGER.info("Registering blocks for " + MOD_NAME);
		new BlockRegistry();
		LOGGER.info("Registering block entities for " + MOD_NAME);
		new BlockEntityRegistry();
		LOGGER.info("Registering sounds for " + MOD_NAME);
		new SoundRegistry();
		LOGGER.info("Replacing Iron Golems with Automata from " + MOD_NAME);
		EntityEvents.replaceNaturallySpawningIronGolemsWithAutomata();
		EntityEvents.checkForUnSpawnedGolem();
		LOGGER.info("Generating world-gen for " + MOD_NAME);
		WorldGen.generateWorldGen();
	}
}