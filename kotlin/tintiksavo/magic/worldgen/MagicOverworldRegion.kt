package tintiksavo.magic.worldgen

import com.mojang.datafixers.util.Pair
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Climate
import terrablender.api.ParameterUtils.Continentalness
import terrablender.api.ParameterUtils.Depth
import terrablender.api.ParameterUtils.Erosion
import terrablender.api.ParameterUtils.Humidity
import terrablender.api.ParameterUtils.Temperature
import terrablender.api.ParameterUtils.Weirdness
import terrablender.api.Region
import terrablender.api.RegionType
import terrablender.api.VanillaParameterOverlayBuilder
import java.util.function.Consumer

class MagicOverworldRegion : Region(ResourceLocation("magic", "magic_overworld"), RegionType.OVERWORLD, 2) {
    override fun addBiomes(
        registry: Registry<Biome>,
        mapper: Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>>
    ) {
        val builder = VanillaParameterOverlayBuilder()
        terrablender.api.ParameterUtils.ParameterPointListBuilder()
            .temperature(Temperature.span(Temperature.COOL, Temperature.WARM))
            .humidity(Humidity.span(Humidity.DRY, Humidity.WET))
            .continentalness(Continentalness.INLAND)
            .erosion(Erosion.EROSION_0, Erosion.EROSION_1, Erosion.EROSION_2)
            .depth(Depth.SURFACE, Depth.FLOOR)
            .weirdness(Weirdness.MID_SLICE_NORMAL_ASCENDING, Weirdness.MID_SLICE_NORMAL_DESCENDING)
            .build()
            .forEach { builder.add(it, MagicBiomes.BOSQUE_MAGICO) }
        builder.build().forEach { mapper.accept(it) }
    }
}
