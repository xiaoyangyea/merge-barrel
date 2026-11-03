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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DoubleBarrelContainer implements Container {
	private final BarrelBlockEntity primary;
	private final BarrelBlockEntity secondary;

	public DoubleBarrelContainer(BarrelBlockEntity primary, BarrelBlockEntity secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}

	private static final int DEFAULT_CONTAINER_SIZE = 54;

	private static final Map<Long, Integer> OPEN_COUNTERS = new ConcurrentHashMap<>();

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
		if (user.getLivingEntity() instanceof Player player && player.isSpectator()) return;

		Level level = primary.getLevel();
		if (level == null || level.isClientSide()) return;

		long posKey = primary.getBlockPos().asLong();
		// 增加计数
		int count = OPEN_COUNTERS.merge(posKey, 1, Integer::sum);

		// 只有第一个打开的人，才触发声音和开盖
		if (count == 1) {
			BlockState state = primary.getBlockState();
			if (!state.getValue(BlockStateProperties.OPEN)) {
				// 标志位改为 2 (UPDATE_CLIENTS)，减少无意义的邻居更新
				level.setBlock(primary.getBlockPos(), state.setValue(BlockStateProperties.OPEN, true), 2);
				level.playSound(null, primary.getBlockPos(), SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.5F, 0.9F + level.getRandom().nextFloat() * 0.1F);
			}

			// 副木桶开盖
			Level secondaryLevel = secondary.getLevel();
			if (secondaryLevel != null && !secondaryLevel.isClientSide()) {
				BlockState secState = secondary.getBlockState();
				if (!secState.getValue(BlockStateProperties.OPEN)) {
					secondaryLevel.setBlock(secondary.getBlockPos(), secState.setValue(BlockStateProperties.OPEN, true), 2);
				}
			}
		}
	}

	@Override
	public void stopOpen(@NonNull ContainerUser user) {
		if (user.getLivingEntity() instanceof Player player && player.isSpectator()) return;

		Level level = primary.getLevel();
		if (level == null || level.isClientSide()) return;

		long posKey = primary.getBlockPos().asLong();
		// 减少计数
		int count = OPEN_COUNTERS.merge(posKey, -1, Integer::sum);

		// 如果数量小于等于 0，说明所有人都关掉了，才触发关盖和声音
		if (count <= 0) {
			OPEN_COUNTERS.remove(posKey); // 清理内存，防止泄漏

			BlockState state = primary.getBlockState();
			if (state.getValue(BlockStateProperties.OPEN)) {
				level.setBlock(primary.getBlockPos(), state.setValue(BlockStateProperties.OPEN, false), 2);
				level.playSound(null, primary.getBlockPos(), SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS, 0.5F, 0.9F + level.getRandom().nextFloat() * 0.1F);
			}

			Level secondaryLevel = secondary.getLevel();
			if (secondaryLevel != null && !secondaryLevel.isClientSide()) {
				BlockState secState = secondary.getBlockState();
				if (secState.getValue(BlockStateProperties.OPEN)) {
					secondaryLevel.setBlock(secondary.getBlockPos(), secState.setValue(BlockStateProperties.OPEN, false), 2);
				}
			}
		}
	}
}
