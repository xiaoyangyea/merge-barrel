package com.github.xiaoyangyea.merge_barrel.mixin;

import com.github.xiaoyangyea.merge_barrel.ModProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class BlockMixin {
	@Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
	private void onUpdateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random, CallbackInfoReturnable<BlockState> cir) {
		if (state.getBlock() instanceof BarrelBlock && state.hasProperty(ModProperties.MERGE_STATE)) {
			ModProperties.MergeState mergeState = state.getValue(ModProperties.MERGE_STATE);

			if (mergeState == ModProperties.MergeState.SINGLE) return;

			Direction facing = state.getValue(BlockStateProperties.FACING);

			Direction partnerDir = (mergeState == ModProperties.MergeState.FIRST) ? facing : facing.getOpposite();

			if (directionToNeighbour == partnerDir) {
				if (!neighbourState.is(Blocks.BARREL) || neighbourState.getValue(BlockStateProperties.FACING) != facing) {
					cir.setReturnValue(state.setValue(ModProperties.MERGE_STATE, ModProperties.MergeState.SINGLE));
				}
			}
		}
	}
}
