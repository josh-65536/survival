package net.survival.blocktype;

public final class BlockType
{
    public static final BasicBlock AIR =
            register(basicBlock()
                    .withDisplayName("<air>")
                    .withHardness(0.0)
                    .withResistance(0.0)
                    .withSolidity(false)
                    .withModel(BlockModel.INVISIBLE)
                    .build());
    public static final BasicBlock BEDROCK =
            register(basicBlock()
                    .withDisplayName("Bedrock")
                    .build());
    public static final BasicBlock TEMP_SOLID =
            register(basicBlock()
                    .withDisplayName("<temp_solid>")
                    .build());
    public static final BasicBlock STONE =
            register(basicBlock()
                    .withDisplayName("Stone")
                    .withTextureOnAllFaces("ProgrammerArt-v3.0/textures/blocks/stone.png")
                    .build());
    public static final BasicBlock COBBLESTONE =
            register(basicBlock()
                    .withDisplayName("Cobblestone")
                    .withTextureOnAllFaces("ProgrammerArt-v3.0/textures/blocks/cobblestone.png")
                    .build());
    public static final BasicBlock DIRT =
            register(basicBlock()
                    .withTextureOnAllFaces("ProgrammerArt-v3.0/textures/blocks/dirt.png")
                    .build());
    public static final BasicBlock GRAVEL =
            register(basicBlock()
                    .withTextureOnAllFaces("ProgrammerArt-v3.0/textures/blocks/gravel.png")
                    .build());
    public static final BasicBlock SAND =
            register(basicBlock()
                    .withTextureOnAllFaces("ProgrammerArt-v3.0/textures/blocks/sand.png")
                    .build());
    public static final BasicBlock GRASS_BLOCK =
            register(basicBlock()
                    .withDisplayName("Grass Block")
                    .withTextureOnSides("ProgrammerArt-v3.0/textures/blocks/grass_side.png")
                    .withTexture(BlockFace.TOP, "ProgrammerArt-v3.0/textures/blocks/grass_top.png")
                    .withTexture(BlockFace.BOTTOM, "ProgrammerArt-v3.0/textures/blocks/dirt.png")
                    .build());
    public static final StairBlock STONE_STAIRS =
            register(stairBlock(
                    basicBlock()
                    .withDisplayName("Stone Stairs")
                    .withTextureOnAllFaces("ProgrammerArt-v3.0/textures/blocks/stone.png")
                    .build()));
    public static final SlabBlock STONE_SLABS =
            register(slabBlock(
                    basicBlock()
                    .withDisplayName("Stone Slabs")
                    .withTextureOnAllFaces("ProgrammerArt-v3.0/textures/blocks/stone.png")
                    .build()));
    public static final BasicBlock WATER =
            register(basicBlock()
                    .withDisplayName("Water")
                    .withTextureOnAllFaces("textures/blocks/water.png")
                    .build());

    private static Block[] blocks;
    private static int blockCount = 0;

    private BlockType() {}

    public static Block byTypeID(int typeID) {
        return blocks[typeID];
    }

    public static Block byFullID(int fullID) {
        short typeID = (short) ((fullID & 0xFFFF0000) >>> 16);
        short encodedState = (short) (fullID & 0xFFFF);
        return blocks[typeID].withState(encodedState);
    }

    public static Block[] getAllBlocks() {
        return blocks;
    }

    private static <T extends Block> T register(T block) {
        if (blocks == null)
            blocks = new Block[4096];
        blocks[blockCount++] = block;
        return block;
    }

    private static BasicBlock.Builder basicBlock() {
        return new BasicBlock.Builder((short) blockCount);
    }

    private static StairBlock stairBlock(BasicBlock baseBlock) {
        return new StairBlock((short) blockCount, (short) 0, baseBlock);
    }

    private static SlabBlock slabBlock(BasicBlock baseBlock) {
        return new SlabBlock((short) blockCount, (short) 0, baseBlock);
    }
}