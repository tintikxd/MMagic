package tintiksavo.magic.worldgen

import terrablender.api.Regions
import terrablender.api.TerraBlenderApi

class MagicTerraBlender : TerraBlenderApi {
    override fun onTerraBlenderInitialized() {
        Regions.register(MagicOverworldRegion())
    }
}
