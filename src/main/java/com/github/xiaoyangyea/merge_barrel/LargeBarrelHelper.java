package com.github.xiaoyangyea.merge_barrel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class LargeBarrelHelper {
	@Nullable
	public static DoubleBarrelContainer getLargeBarrelContainer(Level level, BlockPos pos, BlockState state) {
		if (!state.hasProperty(ModProperties.MERGE_STATE)) return null;
		ModProperties.MergeState mergeState = state.getValue(ModProperties.MERGE_STATE);
		if (mergeState == ModProperties.MergeState.SINGLE) return null;

		Direction facing = state.getValue(BlockStateProperties.FACING);

		Direction partnerDir = (mergeState == ModProperties.MergeState.FIRST) ? facing : facing.getOpposite();
		BlockPos partnerPos = pos.relative(partnerDir);

		BlockEntity selfEntity = level.getBlockEntity(pos);
		BlockEntity partnerEntity = level.getBlockEntity(partnerPos);

		if ((selfEntity instanceof BarrelBlockEntity primary) && (partnerEntity instanceof BarrelBlockEntity secondary)) {
			if (mergeState == ModProperties.MergeState.FIRST) {
				return new DoubleBarrelContainer(primary, secondary);
			} else {
				return new DoubleBarrelContainer(secondary, primary);
			}
		}
		return null;
	}
}
