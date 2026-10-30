package tintiksavo.magic.worldgen

import com.mojang.datafixers.util.Pair
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Climate
import org.slf4j.LoggerFactory
import terrablender.api.ParameterUtils
import terrablender.api.ParameterUtils.Continentalness
import terrablender.api.ParameterUtils.Depth
import terrablender.api.ParameterUtils.Erosion
import terrablender.api.ParameterUtils.Humidity
import terrablender.api.ParameterUtils.Temperature
import terrablender.api.ParameterUtils.Weirdness
import terrablender.api.Region
import terrablender.api.RegionType
import java.util.function.Consumer

class MagicOverworldRegion : Region(
    ResourceLocation("magic", "magic_overworld"),
    RegionType.OVERWORLD,
    2
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger("Tintiksavo Magic / TerraBlender")
    }

    override fun addBiomes(
        registry: Registry<Biome>,
        mapper: Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>>
    ) {
        LOGGER.info("[TerraBlender] addBiomes() llamado. Registrando puntos para magic:bosque_magico.")

        val points = ParameterUtils.ParameterPointListBuilder()
            .temperature(Temperature.span(Temperature.COOL, Temperature.WARM))
            .humidity(Humidity.span(Humidity.DRY, Humidity.WET))
            .continentalness(Continentalness.INLAND)
            .erosion(
                Erosion.EROSION_0,
                Erosion.EROSION_1,
                Erosion.EROSION_2
            )
            .depth(Depth.SURFACE, Depth.FLOOR)
            .weirdness(
                Weirdness.MID_SLICE_NORMAL_ASCENDING,
                Weirdness.MID_SLICE_NORMAL_DESCENDING
            )
            .build()

        LOGGER.info(
            "[TerraBlender] Se generaron {} puntos de parámetros para magic:bosque_magico.",
            points.size
        )

        points.forEach { point ->
            mapper.accept(Pair.of(point, MagicBiomes.BOSQUE_MAGICO))
        }

        LOGGER.info(
            "[TerraBlender] Se enviaron {} puntos al mapper para magic:bosque_magico.",
            points.size
        )

        val biomeKey = MagicBiomes.BOSQUE_MAGICO
        LOGGER.info(
            "[TerraBlender] Clave del bioma: {}. ¿Está presente en el registro?: {}",
            biomeKey.location(),
            registry.containsKey(biomeKey)
        )
    }
}
