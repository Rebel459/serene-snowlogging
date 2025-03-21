package net.legacy.serene_snowlogging.mixin;

import net.frozenblock.wilderwild.block.impl.SnowloggingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sereneseasons.init.ModTags;
import sereneseasons.season.RandomUpdateHandler;
import sereneseasons.season.SeasonHooks;

import static sereneseasons.season.SeasonHooks.warmEnoughToRainSeasonal;

@Mixin(RandomUpdateHandler.class)
public class RandomUpdateHandlerMixin {

	@Inject(
			method = "meltInChunk",
			at = @At(
					value = "HEAD"
			)
	)
	private static void shouldSnow(ChunkMap chunkMap, LevelChunk chunkIn, float meltChance, CallbackInfo ci) {
		ServerLevel world = chunkMap.level;
		ChunkPos chunkpos = chunkIn.getPos();
		int i = chunkpos.getMinBlockX();
		int j = chunkpos.getMinBlockZ();
		if (meltChance > 0.0F && world.random.nextFloat() < meltChance) {
			BlockPos topAirPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, world.getBlockRandomPos(i, 0, j, 15));
			BlockPos topGroundPos = topAirPos.below();
			BlockState aboveGroundState = world.getBlockState(topAirPos);
			Holder<Biome> biome = world.getBiome(topAirPos);
			if (!biome.is(ModTags.Biomes.BLACKLISTED_BIOMES) && SeasonHooks.getBiomeTemperature(world, biome, topGroundPos, world.getSeaLevel()) >= 0.15F && SnowloggingUtils.canSnowlog(aboveGroundState)) {
				world.setBlockAndUpdate(topAirPos, SnowloggingUtils.getStateWithoutSnow(aboveGroundState));
			}
		}

	}

}

