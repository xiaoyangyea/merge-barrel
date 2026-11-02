package com.github.xiaoyangyea.merge_barrel.mixin;

import com.github.xiaoyangyea.merge_barrel.DoubleBarrelContainer;
import com.github.xiaoyangyea.merge_barrel.LargeBarrelHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
	@Inject(method = "getBlockContainer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;", shift = At.Shift.AFTER), cancellable = true)
	private static void interceptHopperContainer(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Container> cir) {
		if (state.getBlock() instanceof BarrelBlock) {
			DoubleBarrelContainer largeContainer = LargeBarrelHelper.getLargeBarrelContainer(level, pos, state);

			if (largeContainer != null) {
				cir.setReturnValue(largeContainer);
			}
		}
	}
}
