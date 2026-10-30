package tintiksavo.magic.worldgen

import net.minecraft.core.BlockPos
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration
import tintiksavo.magic.block.ModBlocks
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class WeirdTreeFeature :
    Feature<NoneFeatureConfiguration>(NoneFeatureConfiguration.CODEC) {

    override fun place(
        context: FeaturePlaceContext<NoneFeatureConfiguration>
    ): Boolean {
        val level = context.level()
        val origin = context.origin()
        val random = context.random()

        // El árbol solamente crece sobre MAGIC_DIRT.
        if (level.getBlockState(origin.below()).block != ModBlocks.MAGIC_DIRT) {
            return false
        }

        val trunk = ModBlocks.MAGIC_LOG.defaultBlockState()
        val leaves = persistentLeaves(
            ModBlocks.MAGIC_LEAVES.defaultBlockState()
        )

        val height = 18 + random.nextInt(11)

        // Comprobar el espacio del tronco antes de generar el árbol.
        for (y in 0 until height) {
            val width = trunkWidth(y, height)
            val offset = (4 - width) / 2

            for (dx in 0 until width) {
                for (dz in 0 until width) {
                    val pos = origin.offset(
                        dx + offset,
                        y,
                        dz + offset
                    )

                    if (!isReplaceable(level.getBlockState(pos))) {
                        return false
                    }
                }
            }
        }

        // TRONCO: base 4x4, transición a 3x3 y copa 2x2.
        for (y in 0 until height) {
            val width = trunkWidth(y, height)
            val offset = (4 - width) / 2

            for (dx in 0 until width) {
                for (dz in 0 until width) {
                    val pos = origin.offset(
                        dx + offset,
                        y,
                        dz + offset
                    )

                    level.setBlock(pos, trunk, 3)
                }
            }
        }

        // RAÍCES: ocho raíces que se extienden desde la base.
        for (i in 0 until 8) {
            val angle = Math.PI * 2.0 * i / 8.0 +
                    random.nextDouble() * 0.25

            val dx = cos(angle)
            val dz = sin(angle)
            val length = 3 + random.nextInt(5)

            for (step in 1..length) {
                val x = (1.5 + dx * step).toInt()
                val z = (1.5 + dz * step).toInt()

                val rootPos = origin.offset(x, 0, z)
                val width = if (step <= 2) 2 else 1

                for (ox in 0 until width) {
                    for (oz in 0 until width) {
                        val pos = rootPos.offset(ox, 0, oz)

                        if (isReplaceable(level.getBlockState(pos))) {
                            level.setBlock(pos, trunk, 3)
                        }
                    }
                }

                // Algunas raíces se introducen en la tierra.
                if (step % 2 == 0) {
                    val below = rootPos.below()
                    val block = level.getBlockState(below).block

                    if (
                        block == ModBlocks.MAGIC_DIRT ||
                        block == Blocks.DIRT ||
                        block == Blocks.GRASS_BLOCK
                    ) {
                        level.setBlock(below, trunk, 3)
                    }
                }
            }
        }

        // RAMAS PRINCIPALES: gruesas junto al tronco y finas
        // en los extremos.
        val branchCount = 6

        for (i in 0 until branchCount) {
            val angle = Math.PI * 2.0 * i / branchCount +
                    random.nextDouble() * 0.35

            val startY = (
                    height * (0.48 + random.nextDouble() * 0.25)
                    ).toInt()

            val length = 5 + random.nextInt(5)
            val startX = 1.5
            val startZ = 1.5

            val endX = startX + cos(angle) * length
            val endZ = startZ + sin(angle) * length
            val endY = startY + 2 + random.nextInt(4)

            for (step in 0..length) {
                val t = step.toDouble() / length

                val x = startX + (endX - startX) * t
                val z = startZ + (endZ - startZ) * t
                val y = startY + (endY - startY) * t

                val width = when {
                    t < 0.30 -> 2
                    t < 0.68 -> 1
                    else -> 0
                }

                placeBranchSegment(
                    level = level,
                    origin = origin,
                    x = x,
                    y = y,
                    z = z,
                    width = width,
                    trunk = trunk
                )

                // Ramas secundarias que nacen de las principales.
                if (
                    step > 1 &&
                    step < length - 1 &&
                    step % 2 == 0 &&
                    random.nextFloat() < 0.45f
                ) {
                    val side = if (random.nextBoolean()) 1.0 else -1.0
                    val secondaryAngle =
                        angle + side * (0.45 + random.nextDouble() * 0.8)

                    val secondaryLength = 2 + random.nextInt(4)

                    val secondaryEndX =
                        x + cos(secondaryAngle) * secondaryLength
                    val secondaryEndZ =
                        z + sin(secondaryAngle) * secondaryLength
                    val secondaryEndY = y + 1 + random.nextInt(3)

                    for (secondaryStep in 0..secondaryLength) {
                        val st = secondaryStep.toDouble() / secondaryLength

                        val sx = x + (secondaryEndX - x) * st
                        val sz = z + (secondaryEndZ - z) * st
                        val sy = y + (secondaryEndY - y) * st

                        placeBranchSegment(
                            level = level,
                            origin = origin,
                            x = sx,
                            y = sy,
                            z = sz,
                            width = if (st < 0.35) 1 else 0,
                            trunk = trunk
                        )
                    }

                    createLeafCluster(
                        level = level,
                        center = origin.offset(
                            secondaryEndX.toInt(),
                            secondaryEndY.toInt(),
                            secondaryEndZ.toInt()
                        ),
                        radius = 2 + random.nextInt(2),
                        leaves = leaves
                    )
                }
            }

            // Una masa de hojas en el extremo de cada rama.
            createLeafCluster(
                level = level,
                center = origin.offset(
                    endX.toInt(),
                    endY,
                    endZ.toInt()
                ),
                radius = 3 + random.nextInt(2),
                leaves = leaves
            )
        }

        // COPA CENTRAL: varios grupos superpuestos para dar volumen.
        val crownY = height - 3

        createLeafCluster(
            level,
            origin.offset(1, crownY, 1),
            5,
            leaves
        )

        createLeafCluster(
            level,
            origin.offset(-1, crownY + 2, 1),
            4,
            leaves
        )

        createLeafCluster(
            level,
            origin.offset(2, crownY + 4, -1),
            3,
            leaves
        )

        createLeafCluster(
            level,
            origin.offset(0, crownY - 2, -1),
            4,
            leaves
        )

        return true
    }

    private fun trunkWidth(y: Int, height: Int): Int {
        return when {
            y < 5 -> 4
            y < height * 0.55 -> 3
            else -> 2
        }
    }

    private fun placeBranchSegment(
        level: WorldGenLevel,
        origin: BlockPos,
        x: Double,
        y: Double,
        z: Double,
        width: Int,
        trunk: BlockState
    ) {
        val base = origin.offset(
            x.toInt(),
            y.toInt(),
            z.toInt()
        )

        for (dx in 0..width) {
            for (dz in 0..width) {
                val pos = base.offset(dx, 0, dz)
                val state = level.getBlockState(pos)

                // No sobrescribir bloques sólidos del terreno.
                if (
                    isReplaceable(state) ||
                    state.block == ModBlocks.MAGIC_LEAVES
                ) {
                    level.setBlock(pos, trunk, 3)
                }
            }
        }
    }

    private fun createLeafCluster(
        level: WorldGenLevel,
        center: BlockPos,
        radius: Int,
        leaves: BlockState
    ) {
        val radiusSquared = radius * radius

        for (dx in -radius..radius) {
            for (dy in -radius..radius) {
                for (dz in -radius..radius) {
                    val distanceSquared =
                        dx * dx + dy * dy + dz * dz

                    if (distanceSquared > radiusSquared) continue

                    // Bordes irregulares para evitar copas perfectamente esféricas.
                    if (
                        distanceSquared > (radius - 1) * (radius - 1) &&
                        (dx * 31 + dy * 17 + dz * 13) % 4 == 0
                    ) {
                        continue
                    }

                    val pos = center.offset(dx, dy, dz)
                    val state = level.getBlockState(pos)

                    // Las hojas rellenan aire y vegetación pequeña,
                    // pero nunca reemplazan el tronco.
                    if (isReplaceable(state)) {
                        level.setBlock(pos, leaves, 3)
                    }
                }
            }
        }
    }

    private fun isReplaceable(state: BlockState): Boolean {
        val block = state.block

        return state.isAir ||
                block == Blocks.GRASS ||
                block == Blocks.TALL_GRASS ||
                block == Blocks.FERN ||
                block == Blocks.LARGE_FERN
    }

    private fun persistentLeaves(state: BlockState): BlockState {
        return state
            .setValue(LeavesBlock.PERSISTENT, true)
            .setValue(LeavesBlock.DISTANCE, 1)
    }
}