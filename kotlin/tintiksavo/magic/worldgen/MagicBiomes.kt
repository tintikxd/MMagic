package tintiksavo.magic.worldgen

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome
import tintiksavo.magic.Magic

object MagicBiomes {
    val BOSQUE_MAGICO: ResourceKey<Biome> =
        ResourceKey.create(Registries.BIOME, Magic.id("bosque_magico"))
}
