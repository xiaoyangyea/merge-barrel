package com.github.xiaoyangyea.merge_barrel;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.NonNull;

public class ModProperties {
	public enum MergeState implements StringRepresentable {
		SINGLE("single"),
		FIRST("first"),
		SECOND("second")
		;
		private final String name;
    	private MergeState(String name) {
			this.name = name;
		}
		@Override
		public @NonNull String getSerializedName() {
			return this.name;
		}
	}
	public static final EnumProperty<MergeState> MERGE_STATE = EnumProperty.create("merge_state", MergeState.class);
}
