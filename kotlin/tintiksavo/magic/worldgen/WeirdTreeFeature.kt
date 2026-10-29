package tintiksavo.magic.worldgen

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration
import kotlin.math.abs

class WeirdTreeFeature : Feature<NoneFeatureConfiguration>(NoneFeatureConfiguration.CODEC) {
    override fun place(context: FeaturePlaceContext<NoneFeatureConfiguration>): Boolean {
        val level = context.level()
        val origin = context.origin()
        val random = context.random()
        val ground = level.getBlockState(origin.below()).block
        if (ground != Blocks.GRASS_BLOCK && ground != Blocks.DIRT &&
            ground != Blocks.COARSE_DIRT && ground != Blocks.ROOTED_DIRT &&
            ground != Blocks.MOSS_BLOCK) return false

        val height = 13 + random.nextInt(9)
        val leanX = random.nextInt(3) - 1
        val leanZ = random.nextInt(3) - 1
        val trunk = Blocks.DARK_OAK_LOG.defaultBlockState()
        val pinkLeaves = persistentLeaves(Blocks.CHERRY_LEAVES.defaultBlockState())
        val blueLeaves = persistentLeaves(Blocks.AZALEA_LEAVES.defaultBlockState())
        val floweringLeaves = persistentLeaves(Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState())

        for (y in 0..height) {
            val p = origin.offset(leanX * y / 4, y, leanZ * y / 4)
            val state = level.getBlockState(p)
            val block = state.block
            if (!state.isAir && block != Blocks.SHORT_GRASS && block != Blocks.TALL_GRASS &&
                block != Blocks.FERN && block != Blocks.LARGE_FERN) return false
        }

        for (y in 0 until height) {
            val p = origin.offset(leanX * y / 4, y, leanZ * y / 4)
            level.setBlock(p, trunk, 3)
        }

        val crownBase = origin.above(height - 5).offset(leanX * height / 4, 0, leanZ * height / 4)
        for (tier in 0..3) {
            val radius = when (tier) { 0 -> 3; 1 -> 5; 2 -> 4; else -> 2 }
            val y = tier * 2 - 1
            for (dx in -radius..radius) for (dz in -radius..radius) {
                val dist = dx * dx + dz * dz
                if (dist > radius * radius + random.nextInt(5) || random.nextInt(5) == 0) continue
                val p = crownBase.offset(dx, y + random.nextInt(2), dz)
                val block = level.getBlockState(p).block
                if (block == Blocks.AIR || block == Blocks.SHORT_GRASS || block == Blocks.TALL_GRASS) {
                    val state = when (random.nextInt(5)) {
                        0 -> blueLeaves
                        1, 2 -> floweringLeaves
                        else -> pinkLeaves
                    }
                    level.setBlock(p, state, 3)
                }
            }
        }

        repeat(4) {
            val dirX = random.nextInt(3) - 1
            val dirZ = if (dirX == 0) (if (random.nextBoolean()) 1 else -1) else random.nextInt(3) - 1
            val branchY = height - 4 - random.nextInt(4)
            val length = 3 + random.nextInt(4)
            for (step in 1..length) {
                val p = origin.offset(leanX * branchY / 4 + dirX * step, branchY + step / 3, leanZ * branchY / 4 + dirZ * step)
                if (level.getBlockState(p).isAir) level.setBlock(p, trunk, 3)
                for (lx in -1..1) for (lz in -1..1) {
                    if (abs(lx) + abs(lz) > 2) continue
                    val leafPos = p.offset(lx, 1, lz)
                    if (level.getBlockState(leafPos).isAir) {
                        level.setBlock(leafPos, if (random.nextBoolean()) pinkLeaves else floweringLeaves, 3)
                    }
                }
            }
        }
        return true
    }

    private fun persistentLeaves(state: BlockState): BlockState =
        state.setValue(LeavesBlock.PERSISTENT, true).setValue(LeavesBlock.DISTANCE, 1)
}
