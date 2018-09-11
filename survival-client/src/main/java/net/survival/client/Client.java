package net.survival.client;

import java.util.Iterator;

import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.survival.block.BlockType;
import net.survival.client.graphics.ClientDisplay;
import net.survival.client.graphics.GraphicsSettings;
import net.survival.client.graphics.gui.GuiDisplay;
import net.survival.client.graphics.opengl.GLDisplay;
import net.survival.client.graphics.opengl.GLRenderContext;
import net.survival.client.gui.Control;
import net.survival.client.input.GlfwKeyboardAdapter;
import net.survival.client.input.GlfwMouseAdapter;
import net.survival.client.input.Key;
import net.survival.client.input.Keyboard;
import net.survival.client.input.Mouse;
import net.survival.concurrent.TaskScheduler;
import net.survival.entity.CharacterModel;
import net.survival.entity.Npc;
import net.survival.entity.NpcMovementStyle;
import net.survival.entity.Player;
import net.survival.util.HitBox;
import net.survival.world.EntitySystem;
import net.survival.world.World;
import net.survival.world.chunk.Chunk;
import net.survival.world.chunk.ChunkPos;
import net.survival.world.chunk.ChunkSystem;
import net.survival.world.gen.InfiniteChunkGenerator;
import net.survival.world.gen.decoration.WorldDecorator;
import net.survival.world.chunk.CircularChunkLoader;
import net.survival.world.chunk.DefaultChunkDatabase;

public class Client implements AutoCloseable
{
    private static final String WINDOW_TITLE = "Survival";

    private static final double TICKS_PER_SECOND = 60.0;
    private static final double SECONDS_PER_TICK = 1.0 / TICKS_PER_SECOND;

    private final World world;

    private final CircularChunkLoader chunkLoader;
    private final DefaultChunkDatabase chunkDatabase;
    private final InfiniteChunkGenerator chunkGenerator;
    private final WorldDecorator worldDecorator;
    private final ChunkSystem chunkSystem;
    
    private final EntitySystem entitySystem;

    private final Control control;

    private final ClientDisplay clientDisplay;
    private final GuiDisplay guiDisplay;

    private final FpsCamera fpsCamera;

    private Player player;

    private Client() {
        world = new World();
        
        chunkLoader = new CircularChunkLoader(10);
        chunkDatabase = new DefaultChunkDatabase();
        chunkGenerator = new InfiniteChunkGenerator(22L);
        worldDecorator = WorldDecorator.createDefault();
        chunkSystem = new ChunkSystem(chunkDatabase, chunkGenerator, worldDecorator);
        
        entitySystem = new EntitySystem();

        control = new Control();
        control.getClientRectangle().setRight(0.1);
        control.getClientRectangle().setBottom(0.025);
        control.setText("");

        clientDisplay = new ClientDisplay(world, GraphicsSettings.WINDOW_WIDTH,
                GraphicsSettings.WINDOW_HEIGHT);
        guiDisplay = new GuiDisplay(control);

        fpsCamera = new FpsCamera(new Vector3d(60.0, 72.0, 20.0), 0.0f, -1.0f);
    }

    @Override
    public void close() throws RuntimeException {
        clientDisplay.close();
    }

    public void tick(double elapsedTime) {
        double cursorDX = Mouse.getDeltaX();
        double cursorDY = Mouse.getDeltaY();
        fpsCamera.rotate(-cursorDX / 128.0, -cursorDY / 128.0);

        if (Keyboard.isKeyPressed(Key.R)) {
            Player newPlayer = new Player();
            newPlayer.x = player != null ? player.x : fpsCamera.position.x;
            newPlayer.y = player != null ? player.y + 3.0 : fpsCamera.position.y;
            newPlayer.z = player != null ? player.z : fpsCamera.position.z;
            newPlayer.hitBox = new HitBox(0.4375, 0.9, 0.4375);
            newPlayer.visible = false;
            player = newPlayer;
            world.addCharacter(newPlayer);
        }

        double joystickX = 0.0;
        double joystickZ = 0.0;
        double joystickY = 0.0;

        if (Keyboard.isKeyDown(Key.W)) {
            joystickX += Math.sin(fpsCamera.yaw);
            joystickZ -= Math.cos(fpsCamera.yaw);
        }
        if (Keyboard.isKeyDown(Key.S)) {
            joystickX += Math.sin(fpsCamera.yaw + Math.PI);
            joystickZ -= Math.cos(fpsCamera.yaw + Math.PI);
        }
        if (Keyboard.isKeyDown(Key.A)) {
            joystickX += Math.sin(fpsCamera.yaw - Math.PI / 2.0);
            joystickZ -= Math.cos(fpsCamera.yaw - Math.PI / 2.0);
        }
        if (Keyboard.isKeyDown(Key.D)) {
            joystickX += Math.sin(fpsCamera.yaw + Math.PI / 2.0);
            joystickZ -= Math.cos(fpsCamera.yaw + Math.PI / 2.0);
        }

        if (Keyboard.isKeyDown(Key.SPACE)) {
            joystickY = 1.0;
        }
        if (Keyboard.isKeyDown(Key.LEFT_SHIFT)) {
            joystickY = -1.0;
        }

        if (player != null) {
            player.setMoveDirectionControlValues(joystickX, joystickZ);

            if (Keyboard.isKeyPressed(Key.SPACE))
                player.setJumpControlValue();
        }
        else {
            final double CAMERA_SPEED = 80.0;
            fpsCamera.position.x += joystickX * CAMERA_SPEED * elapsedTime;
            fpsCamera.position.z += joystickZ * CAMERA_SPEED * elapsedTime;
            fpsCamera.position.y += joystickY * 20.0 * elapsedTime;
        }

        int cx = ChunkPos.toChunkX((int) Math.floor(fpsCamera.position.x));
        int cz = ChunkPos.toChunkZ((int) Math.floor(fpsCamera.position.z));
        chunkLoader.setCenter(cx, cz);

        chunkSystem.update(world, chunkLoader);
        entitySystem.update(world, elapsedTime);

        if (player != null) {
            fpsCamera.position.x = player.x;
            fpsCamera.position.y = player.y + 0.8;
            fpsCamera.position.z = player.z;
        }

        if (Mouse.isLmbPressed() || Mouse.isRmbPressed()) {
            double px = fpsCamera.position.x;
            double py = fpsCamera.position.y;
            double pz = fpsCamera.position.z;
            final double DELTA = 0.0078125;
            for (double zz = 0.0; zz < 7.0; zz += DELTA) {
                px += DELTA * Math.sin(fpsCamera.yaw) * Math.cos(fpsCamera.pitch);
                py += DELTA * Math.sin(fpsCamera.pitch);
                pz -= DELTA * Math.cos(fpsCamera.yaw) * Math.cos(fpsCamera.pitch);
                int pxi = (int) Math.floor(px);
                int pyi = (int) Math.floor(py);
                int pzi = (int) Math.floor(pz);
                if (world.getBlock(pxi, pyi, pzi) != BlockType.EMPTY.id) {
                    if (Mouse.isLmbPressed())
                        world.setBlock(pxi, pyi, pzi, BlockType.EMPTY.id);
                    else if (Mouse.isRmbPressed())
                        world.setBlock(pxi, pyi, pzi, BlockType.OAK_FENCE.id);
                    break;
                }
            }
        }

        if (Keyboard.isKeyPressed(Key.T)) {
            Npc npc = new Npc();
            npc.x = fpsCamera.position.x;
            npc.y = fpsCamera.position.y;
            npc.z = fpsCamera.position.z;
            npc.hitBox = new HitBox(0.5, 0.9, 0.8);
            npc.moveSpeed = 4.0;
            world.addCharacter(npc);
        }

        if (Keyboard.isKeyPressed(Key.Y)) {
            Npc npc = new Npc();
            npc.x = fpsCamera.position.x;
            npc.y = fpsCamera.position.y;
            npc.z = fpsCamera.position.z;
            npc.hitBox = new HitBox(0.5, 0.9, 0.8);
            npc.moveSpeed = 4.0;
            npc.model = CharacterModel.GOAT;
            world.addCharacter(npc);
        }

        if (Keyboard.isKeyPressed(Key.U)) {
            Npc npc = new Npc();
            npc.x = fpsCamera.position.x;
            npc.y = fpsCamera.position.y;
            npc.z = fpsCamera.position.z;
            npc.hitBox = new HitBox(0.5, 0.9, 0.8);
            npc.moveSpeed = 4.0;
            npc.model = CharacterModel.SLIME;
            npc.movementStyle = NpcMovementStyle.SLIME;
            world.addCharacter(npc);
        }

        Iterator<Long2ObjectMap.Entry<Chunk>> chunkMapIt = world.getChunkMapFastIterator();
        while (chunkMapIt.hasNext()) {
            Long2ObjectMap.Entry<Chunk> entry = chunkMapIt.next();
            long hashedPos = entry.getLongKey();
            Chunk chunk = entry.getValue();

            if (chunk.isBlocksModified()) {
                chunk.clearModificationFlags();
                clientDisplay.redrawChunk(hashedPos);
            }
        }

        clientDisplay.getCamera().moveTo((float) fpsCamera.position.x, (float) fpsCamera.position.y,
                (float) fpsCamera.position.z);
        clientDisplay.getCamera().orient((float) fpsCamera.yaw, (float) fpsCamera.pitch);
        clientDisplay.getCamera().setFov((float) Math.toRadians(60.0));
        clientDisplay.getCamera().resize(GraphicsSettings.WINDOW_WIDTH,
                GraphicsSettings.WINDOW_HEIGHT);
        clientDisplay.getCamera().setClipPlanes(0.0625f, 768.0f);

        clientDisplay.tick(elapsedTime);
    }

    private void render(double frameRate) {
        clientDisplay.display(frameRate);
        guiDisplay.display();
    }

    public static void main(String[] args) {
        GLDisplay display = new GLDisplay(GraphicsSettings.WINDOW_WIDTH,
                GraphicsSettings.WINDOW_HEIGHT, WINDOW_TITLE);
        GlfwKeyboardAdapter keyboardAdapter = new GlfwKeyboardAdapter();
        GlfwMouseAdapter mouseAdapter = new GlfwMouseAdapter(display.getUnderlyingGlfwWindow());
        GLFW.glfwSetKeyCallback(display.getUnderlyingGlfwWindow(), keyboardAdapter);
        GLFW.glfwSetCursorPosCallback(display.getUnderlyingGlfwWindow(), mouseAdapter);
        GLRenderContext.init();

        Client program = new Client();

        final double MILLIS_PER_TICK = SECONDS_PER_TICK * 1000.0;
        long now = System.currentTimeMillis();
        long prevTime = now;
        double unprocessedTicks = 0.0;

        int frameCounter = 0;
        long frameRateTimer = System.currentTimeMillis();
        int frameRate = 0;

        for (boolean running = true; running; running = Keyboard.isKeyUp(Key.ESCAPE)
                && !display.shouldClose())
        {
            now = System.currentTimeMillis();
            unprocessedTicks += (now - prevTime) / MILLIS_PER_TICK;
            prevTime = now;

            if (unprocessedTicks >= 1.0) {
                while (unprocessedTicks >= 1.0) {
                    mouseAdapter.tick();
                    program.tick(SECONDS_PER_TICK);
                    keyboardAdapter.nextInputFrame();
                    unprocessedTicks -= 1.0;
                }

                ++frameCounter;
                program.render(frameRate);
                display.swapBuffers();
            }

            if (System.currentTimeMillis() - frameRateTimer > 1000) {
                frameRateTimer += 1000;
                frameRate = frameCounter;
                frameCounter = 0;
            }

            while (TaskScheduler.pollTasks())
                TaskScheduler.dispatchTasks();

            GLDisplay.pollEvents();
        }

        program.close();
        display.close();
    }
}