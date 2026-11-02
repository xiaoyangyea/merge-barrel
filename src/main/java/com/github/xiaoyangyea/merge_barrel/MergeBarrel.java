package com.github.xiaoyangyea.merge_barrel;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MergeBarrel implements ModInitializer {
	public static final String MOD_ID = "merge-barrel";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Merge Barrel Mod Initializing...");

		UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
			if(hand != InteractionHand.MAIN_HAND) {
				return InteractionResult.PASS;
			}

			if (player.isSecondaryUseActive() && player.getItemInHand(hand).getItem() instanceof BlockItem) return InteractionResult.PASS;

			BlockPos pos = hitResult.getBlockPos();
			BlockState state = level.getBlockState(pos);

			if(state.is(Blocks.BARREL)) {
				DoubleBarrelContainer container = LargeBarrelHelper.getLargeBarrelContainer(level, pos, state);

				if(container != null) {
					if(level.isClientSide()) {
						return InteractionResult.SUCCESS;
					}

					player.openMenu(new SimpleMenuProvider(
							(syncId, playerInventory, p) -> ChestMenu.sixRows(syncId, playerInventory, container), Component.translatable("container.barrel")
					));
					return InteractionResult.CONSUME;
				}
			}

			return InteractionResult.PASS;

		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
