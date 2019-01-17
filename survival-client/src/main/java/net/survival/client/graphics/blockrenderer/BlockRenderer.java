package net.survival.client.graphics.blockrenderer;

import net.survival.blocktype.Block;
import net.survival.blocktype.BlockFace;
import net.survival.blocktype.BlockID;
import net.survival.blocktype.BlockModel;
import net.survival.blocktype.BlockType;
import net.survival.client.graphics.BlockTextureAtlas;
import net.survival.client.graphics.opengl.GLDisplayList;

public abstract class BlockRenderer
{
    // TODO: Make private, and create accessor methods.
    public static BlockTextureAtlas topFaceTextures = new BlockTextureAtlas(BlockFace.TOP);
    public static BlockTextureAtlas bottomFaceTextures = new BlockTextureAtlas(BlockFace.BOTTOM);
    public static BlockTextureAtlas leftFaceTextures = new BlockTextureAtlas(BlockFace.LEFT);
    public static BlockTextureAtlas rightFaceTextures = new BlockTextureAtlas(BlockFace.RIGHT);
    public static BlockTextureAtlas frontFaceTextures = new BlockTextureAtlas(BlockFace.FRONT);
    public static BlockTextureAtlas backFaceTextures = new BlockTextureAtlas(BlockFace.BACK);
    private static boolean texturesInit = false;

    private static final BlockRenderer[] blockRenderers = new BlockRenderer[BlockModel.getCachedValues().length];
    private static final BlockRenderer defaultBlockRenderer = new DefaultBlockRenderer();

    protected static final boolean[] blockToBlockingTopTable = new boolean[BlockType.getAllBlocks().length];
    protected static final boolean[] blockToBlockingBottomTable = new boolean[BlockType.getAllBlocks().length];
    protected static final boolean[] blockToBlockingLeftTable = new boolean[BlockType.getAllBlocks().length];
    protected static final boolean[] blockToBlockingRightTable = new boolean[BlockType.getAllBlocks().length];
    protected static final boolean[] blockToBlockingFrontTable = new boolean[BlockType.getAllBlocks().length];
    protected static final boolean[] blockToBlockingBackTable = new boolean[BlockType.getAllBlocks().length];

    static {
        for (int i = 0; i < blockRenderers.length; ++i)
            blockRenderers[i] = defaultBlockRenderer;

        blockRenderers[BlockModel.INVISIBLE.id] = new InvisibleBlockRenderer();
        blockRenderers[BlockModel.FENCE.id] = new FenceRenderer();
        blockRenderers[BlockModel.SAPLING.id] = new SaplingRenderer();
        blockRenderers[BlockModel.BOTTOM_SLAB.id] = new BottomSlabRenderer();
        blockRenderers[BlockModel.TOP_SLAB.id] = new TopSlabRenderer();
        blockRenderers[BlockModel.NORTH_STAIRS.id] = new NorthStairsRenderer();
        blockRenderers[BlockModel.SOUTH_STAIRS.id] = new SouthStairsRenderer();
        blockRenderers[BlockModel.EAST_STAIRS.id] = new EastStairsRenderer();
        blockRenderers[BlockModel.WEST_STAIRS.id] = new WestStairsRenderer();
        blockRenderers[BlockModel.NORTH_CEILING_STAIRS.id] = new NorthCeilingStairsRenderer();
        blockRenderers[BlockModel.SOUTH_CEILING_STAIRS.id] = new SouthCeilingStairsRenderer();
        blockRenderers[BlockModel.EAST_CEILING_STAIRS.id] = new EastCeilingStairsRenderer();
        blockRenderers[BlockModel.WEST_CEILING_STAIRS.id] = new WestCeilingStairsRenderer();
        blockRenderers[BlockModel.FARMLAND.id] = new FarmlandRenderer();
        blockRenderers[BlockModel.PRESSURE_PLATE_OFF.id] = new PressurePlateRenderer(0.0625f);
        blockRenderers[BlockModel.PRESSURE_PLATE_ON.id] = new PressurePlateRenderer(0.03125f);

        for (int i = 0; i < blockToBlockingTopTable.length; ++i) {
            Block block = BlockType.getAllBlocks()[i];
            if (block != null)
                blockToBlockingTopTable[i] = block.getModel().isBlockingTop();
        }

        for (int i = 0; i < blockToBlockingBottomTable.length; ++i) {
            Block block = BlockType.getAllBlocks()[i];
            if (block != null)
                blockToBlockingBottomTable[i] = block.getModel().isBlockingBottom();
        }

        for (int i = 0; i < blockToBlockingLeftTable.length; ++i) {
            Block block = BlockType.getAllBlocks()[i];
            if (block != null)
                blockToBlockingLeftTable[i] = block.getModel().isBlockingLeft();
        }

        for (int i = 0; i < blockToBlockingRightTable.length; ++i) {
            Block block = BlockType.getAllBlocks()[i];
            if (block != null)
                blockToBlockingRightTable[i] = block.getModel().isBlockingRight();
        }

        for (int i = 0; i < blockToBlockingFrontTable.length; ++i) {
            Block block = BlockType.getAllBlocks()[i];
            if (block != null)
                blockToBlockingFrontTable[i] = block.getModel().isBlockingFront();
        }

        for (int i = 0; i < blockToBlockingBackTable.length; ++i) {
            Block block = BlockType.getAllBlocks()[i];
            if (block != null)
                blockToBlockingBackTable[i] = block.getModel().isBlockingBack();
        }
    }

    public static final void initTextures() {
        if (!texturesInit) {
            topFaceTextures = new BlockTextureAtlas(BlockFace.TOP);
            bottomFaceTextures = new BlockTextureAtlas(BlockFace.BOTTOM);
            leftFaceTextures = new BlockTextureAtlas(BlockFace.LEFT);
            rightFaceTextures = new BlockTextureAtlas(BlockFace.RIGHT);
            frontFaceTextures = new BlockTextureAtlas(BlockFace.FRONT);
            backFaceTextures = new BlockTextureAtlas(BlockFace.BACK);
            texturesInit = true;
        }
    }

    public final boolean nonCubic;

    public BlockRenderer(boolean nonCubic) {
        this.nonCubic = nonCubic;
    }

    public static BlockRenderer byFullID(int blockID) {
        return blockRenderers[BlockType.getAllBlocks()[BlockID.typeIDFromFullID(blockID)].getModel().id];
    }

    public void pushNonCubic(int x, int y, int z, int blockID, GLDisplayList.Builder builder) {}

    public void pushTopFaces(int x, int y, int z, int blockID, int adjacentBlockID,
            GLDisplayList.Builder builder)
    {}

    public void pushBottomFaces(int x, int y, int z, int blockID, int adjacentBlockID,
            GLDisplayList.Builder builder)
    {}

    public void pushLeftFaces(int x, int y, int z, int blockID, int adjacentBlockID,
            GLDisplayList.Builder builder)
    {}

    public void pushRightFaces(int x, int y, int z, int blockID, int adjacentBlockID,
            GLDisplayList.Builder builder)
    {}

    public void pushFrontFaces(int x, int y, int z, int blockID, int adjacentBlockID,
            GLDisplayList.Builder builder)
    {}

    public void pushBackFaces(int x, int y, int z, int blockID, int adjacentBlockID,
            GLDisplayList.Builder builder)
    {}
}