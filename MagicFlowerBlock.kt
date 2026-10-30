package tintiksavo.magic.block

import net.minecraft.core.BlockPos
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.FlowerBlock
import net.minecraft.world.level.block.state.BlockState

class MagicFlowerBlock(
    properties: Properties
) : FlowerBlock(MobEffects.MOVEMENT_SPEED, 1, properties) {

    override fun mayPlaceOn(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos
    ): Boolean {
        return state.`is`(ModBlocks.MAGIC_GRASS) ||
                super.mayPlaceOn(state, level, pos)
    }
}