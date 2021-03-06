package net.survival.graphics

import net.survival.block.Column
import net.survival.graphics.opengl.GLMatrixStack
import net.survival.graphics.opengl.GLRenderContext
import net.survival.graphics.opengl.GLState
import net.survival.graphics.particle.ClientParticleSpace
import net.survival.render.ModelType
import org.joml.Matrix4f
import java.util.*
import java.util.stream.Collectors

private const val ASSET_ROOT_PATH = "./assets/"
private const val TEXTURE_PATH = ASSET_ROOT_PATH
private const val FONT_PATH = "textures/fonts/default"
private const val PIXELS_PER_EM = 24

class CompositeDisplay(
    viewportWidth: Int,
    viewportHeight: Int,
) {
    private val camera = PerspectiveCamera()

    private val blockDisplay: BlockDisplay
    private val modelDisplay: ModelDisplay
    private val particleDisplay: ParticleDisplay
    private val skyboxDisplay = SkyboxDisplay()
    private val cloudDisplay = CloudDisplay()
    private val textRenderer: TextRenderer

    private val clientParticleSpace: ClientParticleSpace = ClientParticleSpace()

    private val drawTextCommandQueue: Queue<DrawTextCommand>

    private val viewportWidth: Int
    private val viewportHeight: Int

    private var visibilityFlags = VisibilityFlags.DEFAULT

    private val cameraViewMatrix = Matrix4f()
    private val cameraProjectionMatrix = Matrix4f()
    private val textProjectionMatrix = Matrix4f()

    private var columnsToInvalidate: MutableList<InvalidateColumn> = ArrayList()

    init {
        blockDisplay = BlockDisplay(camera)
        modelDisplay = ModelDisplay(camera)
        particleDisplay = ParticleDisplay(clientParticleSpace, camera)
        textRenderer = TextRenderer(FONT_PATH)

        drawTextCommandQueue = LinkedList()

        this.viewportWidth = viewportWidth
        this.viewportHeight = viewportHeight

        val mipmappedTextureAtlas = TextureAtlas(TEXTURE_PATH, true)
        val textureAtlas = TextureAtlas(TEXTURE_PATH, false)
        Assets.setup(mipmappedTextureAtlas, textureAtlas)
    }

    fun close() {
        blockDisplay.close()
        Assets.tearDown()
    }

    private fun isVisible(flag: Int): Boolean = visibilityFlags and flag != 0

    fun toggleVisibilityFlags(flags: Int) {
        visibilityFlags = visibilityFlags xor flags
    }

    fun tick(elapsedTime: Double) {
        cloudDisplay.tick(elapsedTime)
    }

    fun display() {
        cameraViewMatrix.identity()
        camera.getViewMatrix(cameraViewMatrix)
        cameraProjectionMatrix.identity()
        camera.getProjectionMatrix(cameraProjectionMatrix)

        Assets.getMipmappedTextureAtlas().updateTextures()
        Assets.getTextureAtlas().updateTextures()

        // Clears color and depth buffers.
        GLRenderContext.clearColorBuffer(0.0f, 0.0f, 0.0f, 0.0f)
        GLRenderContext.clearDepthBuffer(1.0f)

        // Display skybox.
        if (isVisible(VisibilityFlags.SKYBOX)) {
            skyboxDisplay.display(cameraViewMatrix, cameraProjectionMatrix)
        }

        // Display blocks, entities, and clouds.
        GLState.pushFogEnabled(true)
        GLState.pushLinearFog(16.0f, 128.0f,
            skyboxDisplay.middleR,
            skyboxDisplay.middleG,
            skyboxDisplay.middleB,
            1.0f
        )

        run {
            run {
                var i = 0
                while (i < 6 && columnsToInvalidate.isNotEmpty()) {
                    val lastIndex = columnsToInvalidate.size - 1
                    val message = columnsToInvalidate.removeAt(lastIndex)
                    val columnPos = message.columnPos
                    val column = message.column
                    blockDisplay.redrawColumn(columnPos, column)
                    ++i
                }
            }

            for (i in columnsToInvalidate.indices.reversed()) {
                val message = columnsToInvalidate[i]
                if (message.invalidationPriority == ColumnInvalidationPriority.NOW) {
                    val columnPos = message.columnPos
                    val column = message.column
                    blockDisplay.redrawColumn(columnPos, column)
                }
            }

            if (isVisible(VisibilityFlags.BLOCKS)) {
                blockDisplay.display()
            }

            if (isVisible(VisibilityFlags.ENTITIES)) {
                modelDisplay.display()
            }

            if (isVisible(VisibilityFlags.PARTICLES)) {
                particleDisplay.display()
            }

            if (isVisible(VisibilityFlags.CLOUDS)) {
                cloudDisplay.display(
                    cameraViewMatrix,
                    cameraProjectionMatrix,
                    camera.x,
                    camera.z
                )
            }
        }

        GLState.popFogParams()
        GLState.popFogEnabled()

        // Draw Text
        if (isVisible(VisibilityFlags.HUD)) {
            textProjectionMatrix.identity()
            textProjectionMatrix.ortho2D(
                0.0f,
                viewportWidth.toFloat() / PIXELS_PER_EM,
                viewportHeight.toFloat() / PIXELS_PER_EM,
                0.0f
            )

            GLMatrixStack.setProjectionMatrix(textProjectionMatrix)
            GLMatrixStack.push()
            GLMatrixStack.loadIdentity()

            while (!drawTextCommandQueue.isEmpty()) {
                val command = drawTextCommandQueue.remove()
                val text = command.text
                val x = command.x
                val y = command.y
                val z = command.z
                textRenderer.drawText(text, TextStyle.DEFAULT, x, y, z)
            }

            GLMatrixStack.pop()
        }
    }

    fun drawModel(
        x: Float, y: Float, z: Float,
        yaw: Float, pitch: Float, roll: Float,
        scaleX: Float, scaleY: Float, scaleZ: Float,
        modelType: ModelType,
    ) {
        modelDisplay.drawModel(DrawModelCommand(
            x, y, z,
            yaw, pitch, roll,
            scaleX, scaleY, scaleZ,
            modelType
        ))
    }

    fun setColumn(
        columnPos: Long,
        column: Column?,
        invalidationPriority: ColumnInvalidationPriority,
    ) {
        columnsToInvalidate = columnsToInvalidate.stream()
            .filter { e: InvalidateColumn -> e.columnPos != columnPos }
            .collect(Collectors.toList())
        columnsToInvalidate.add(InvalidateColumn(columnPos, column, invalidationPriority))
    }

    fun moveCamera(x: Float, y: Float, z: Float) {
        camera.x = x
        camera.y = y
        camera.z = z
    }

    fun orientCamera(yaw: Float, pitch: Float) {
        camera.yaw = yaw
        camera.pitch = pitch
        // TODO: Missing Camera::roll.
    }

    fun setCameraParams(fov: Float, width: Float, height: Float, nearClipPlane: Float, farClipPlane: Float) {
        camera.fov = fov
        camera.width = width
        camera.height = height
        camera.nearClipPlane = nearClipPlane
        camera.farClipPlane = farClipPlane
    }

    fun setCloudParams(
        seed: Long,
        density: Float,
        elevation: Float,
        speedX: Float,
        speedZ: Float,
        alpha: Float,
    ) {
        cloudDisplay.seed = seed
        cloudDisplay.setDensity(density)
        cloudDisplay.elevation = elevation
        cloudDisplay.speedX = speedX
        cloudDisplay.speedZ = speedZ
        cloudDisplay.alpha = alpha
    }

    fun setSkyColor(
        br: Float, bg: Float, bb: Float,
        mr: Float, mg: Float, mb: Float,
        tr: Float, tg: Float, tb: Float,
    ) {
        skyboxDisplay.setColor(br, bg, bb, mr, mg, mb, tr, tg, tb)
    }

    fun drawText(text: String, fontSize: Float, left: Float, top: Float) {
        drawTextCommandQueue.add(DrawTextCommand(text, left, top, 0.0f))
    }
}

object VisibilityFlags {
    const val BLOCKS = 0x1
    const val ENTITIES = 0x2
    const val PARTICLES = 0x4
    const val SKYBOX = 0x8
    const val CLOUDS = 0x10
    const val HUD = 0x20
    const val DEBUG_GEOMETRY = 0x40
    const val DEFAULT = BLOCKS or ENTITIES or PARTICLES or SKYBOX or CLOUDS or HUD or DEBUG_GEOMETRY
}

enum class ColumnInvalidationPriority {
    LOW, NOW
}

private class InvalidateColumn(
    val columnPos: Long,
    val column: Column?,
    val invalidationPriority: ColumnInvalidationPriority
)

internal sealed class DrawCommand

internal class DrawTextCommand(val text: String, val x: Float, val y: Float, val z: Float): DrawCommand()

internal class DrawModelCommand(
    val x: Float, val y: Float, val z: Float,
    val yaw: Float, val pitch: Float, val roll: Float,
    val scaleX: Float, val scaleY: Float, val scaleZ: Float,
    val modelType: ModelType,
): DrawCommand()
