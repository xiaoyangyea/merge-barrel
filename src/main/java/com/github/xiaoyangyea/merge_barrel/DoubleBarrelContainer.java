package com.github.xiaoyangyea.merge_barrel;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.NonNull;

public class DoubleBarrelContainer implements Container {
	private final BarrelBlockEntity primary;
	private final BarrelBlockEntity secondary;

	public DoubleBarrelContainer(BarrelBlockEntity primary, BarrelBlockEntity secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}

	private static final int DEFAULT_CONTAINER_SIZE = 54;

	@Override
	public int getContainerSize() {
		return DEFAULT_CONTAINER_SIZE;
	}

	@Override
	public boolean isEmpty() {
		return primary.isEmpty() && secondary.isEmpty();
	}

	@Override
	public @NonNull ItemStack getItem(int slot) {
		return slot < 27 ? primary.getItem(slot) : secondary.getItem(slot - 27);
	}

	@Override
	public @NonNull ItemStack removeItem(int slot, int count) {
		return slot < 27 ? primary.removeItem(slot, count) : secondary.removeItem(slot - 27, count);
	}

	@Override
	public @NonNull ItemStack removeItemNoUpdate(int slot) {
		return slot < 27 ? primary.removeItemNoUpdate(slot) : secondary.removeItemNoUpdate(slot - 27);
	}

	@Override
	public void setItem(int slot, @NonNull ItemStack itemStack) {
		if (slot < 27) {
			primary.setItem(slot, itemStack);
		} else {
			secondary.setItem(slot - 27, itemStack);
		}
	}

	@Override
	public void setChanged() {
		primary.setChanged();
		secondary.setChanged();
	}

	@Override
	public boolean stillValid(@NonNull Player player) {
		return primary.stillValid(player) && secondary.stillValid(player);
	}

	@Override
	public void clearContent() {
		primary.clearContent();
		secondary.clearContent();
	}

	@Override
	public void startOpen(@NonNull ContainerUser user) {
		Level level = primary.getLevel();
		if (level != null && !level.isClientSide()) {
			BlockState state = primary.getBlockState();
			// 如果盖子没开，我们手动让它开，并播放一次声音
			if (!state.getValue(BlockStateProperties.OPEN)) {
				level.setBlock(primary.getBlockPos(), state.setValue(BlockStateProperties.OPEN, true), 3);
				level.playSound(null, primary.getBlockPos(), SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.5F, 0.9F + level.getRandom().nextFloat() * 0.1F);
			}
		}
	}

	@Override
	public void stopOpen(@NonNull ContainerUser user) {
		Level level = primary.getLevel();
		if (level != null && !level.isClientSide()) {
			BlockState state = primary.getBlockState();
			// 如果盖子开着，我们手动关掉，并播放一次声音
			if (state.getValue(BlockStateProperties.OPEN)) {
				level.setBlock(primary.getBlockPos(), state.setValue(BlockStateProperties.OPEN, false), 3);
				level.playSound(null, primary.getBlockPos(), SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS, 0.5F, 0.9F + level.getRandom().nextFloat() * 0.1F);
			}
		}
	}
}
