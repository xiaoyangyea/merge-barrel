package com.github.xiaoyangyea.merge_barrel.mixin;

import com.github.xiaoyangyea.merge_barrel.DoubleBarrelContainer;
import com.github.xiaoyangyea.merge_barrel.LargeBarrelHelper;
import com.github.xiaoyangyea.merge_barrel.ModProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BarrelBlock.class)
public class BarrelBlockMixin extends Block {

	public BarrelBlockMixin(Properties properties) {
		super(properties);
	}

	@Inject(
			method = "createBlockStateDefinition",
			at = @At("TAIL")
	)
	protected void addMergeProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(ModProperties.MERGE_STATE);
	}

	@Inject(
			method = "getStateForPlacement",
			at = @At("RETURN"),
			cancellable = true
	)
	private void onPlaceMerge(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
		BlockState newState = cir.getReturnValue();
		if(newState == null) return;	//放置失败时返回

		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction facing = newState.getValue(BlockStateProperties.FACING);

		Direction[] checkDirs = {facing, facing.getOpposite()};

		for (Direction checkDir : checkDirs) {
			BlockPos neighbourPos = pos.relative(checkDir);
			BlockState neighbourState = level.getBlockState(neighbourPos);

			if(neighbourState.is(Blocks.BARREL)
				&& neighbourState.getValue(BlockStateProperties.FACING) == facing
				&& neighbourState.getValue(ModProperties.MERGE_STATE) == ModProperties.MergeState.SINGLE) {

				BlockState newMergedBlockState;
				BlockState neighbourMergedBlockState;

				if(checkDir == facing) {
					newMergedBlockState = newState.setValue(ModProperties.MERGE_STATE, ModProperties.MergeState.FIRST);
					neighbourMergedBlockState = neighbourState.setValue(ModProperties.MERGE_STATE, ModProperties.MergeState.SECOND);
				} else {
					newMergedBlockState = newState.setValue(ModProperties.MERGE_STATE, ModProperties.MergeState.SECOND);
					neighbourMergedBlockState = neighbourState.setValue(ModProperties.MERGE_STATE, ModProperties.MergeState.FIRST);
				}

				cir.setReturnValue(newMergedBlockState);

				level.setBlockAndUpdate(neighbourPos, neighbourMergedBlockState);

				break;
			}
		}
	}

	@Inject(method = "getAnalogOutputSignal", at = @At("HEAD"), cancellable = true)
	private void getLargeBarrelComparatorOut(BlockState state, Level level, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir
	) {
		DoubleBarrelContainer container = LargeBarrelHelper.getLargeBarrelContainer(level, pos, state);

		if(container != null) {
			cir.setReturnValue(AbstractContainerMenu.getRedstoneSignalFromContainer(container));
		}
	}
}
