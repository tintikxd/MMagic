package tintiksavo.magic.worldgen

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration
import tintiksavo.magic.Magic

object ModWorldgen {
    val WEIRD_TREE: Feature<NoneFeatureConfiguration> = Registry.register(
        BuiltInRegistries.FEATURE, Magic.id("weird_tree"), WeirdTreeFeature()
    )

    fun register() {
        WEIRD_TREE
    }
}
