package tintiksavo.magic.worldgen

import net.minecraft.world.level.levelgen.SurfaceRules
import terrablender.api.Regions

import terrablender.api.SurfaceRuleManager
import terrablender.api.TerraBlenderApi
import tintiksavo.magic.Magic
import tintiksavo.magic.block.ModBlocks

class MagicTerraBlender : TerraBlenderApi {

    override fun onTerraBlenderInitialized() {
        // Mantener el registro del bioma que ya funciona.
        Regions.register(MagicOverworldRegion())

        // Regla superficial exclusiva del bosque mágico.
        val magicGrassRule = SurfaceRules.ifTrue(
            SurfaceRules.isBiome(MagicBiomes.BOSQUE_MAGICO),
            SurfaceRules.ifTrue(
                SurfaceRules.ON_FLOOR,
                SurfaceRules.state(
                    ModBlocks.MAGIC_GRASS.defaultBlockState()
                )
            )
        )

        SurfaceRuleManager.addSurfaceRules(
            SurfaceRuleManager.RuleCategory.OVERWORLD,
            Magic.MOD_ID,
            magicGrassRule
        )
    }
}