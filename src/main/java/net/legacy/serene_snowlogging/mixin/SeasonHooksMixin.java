package net.legacy.serene_snowlogging.mixin;

import net.frozenblock.wilderwild.block.impl.SnowloggingUtils;
import net.frozenblock.wilderwild.config.WWBlockConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.init.ModConfig;
import sereneseasons.season.SeasonHooks;

import static sereneseasons.season.SeasonHooks.warmEnoughToRainSeasonal;

@Mixin(SeasonHooks.class)
public class SeasonHooksMixin {

	@Inject(
			method = "shouldSnowHook",
			at = @At(
					value = "HEAD"
			),
			cancellable = true)
	private static void shouldSnow(Biome biome, LevelReader levelReader, BlockPos pos, int seaLevel, CallbackInfoReturnable<Boolean> cir) {
		if (ModConfig.seasons.generateSnowAndIce && warmEnoughToRainSeasonal(levelReader, pos, seaLevel) || !ModConfig.seasons.generateSnowAndIce && biome.warmEnoughToRain(pos, seaLevel)) {
			cir.setReturnValue(false);
		} else {
			if (levelReader.isInsideBuildHeight(pos.getY()) && levelReader.getBrightness(LightLayer.BLOCK, pos) < 10) {
				BlockState blockstate = levelReader.getBlockState(pos);
				if (blockstate.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(levelReader, pos) || Blocks.SNOW.defaultBlockState().canSurvive(levelReader, pos) && SnowloggingUtils.canSnowlog(levelReader.getBlockState(pos)) && WWBlockConfig.canSnowlogNaturally()) {
					cir.setReturnValue(true);
				}
			}
		}

	}

}

