package net.survival.client.graphics;

import net.survival.block.BlockFace;
import net.survival.block.BlockType;
import net.survival.client.graphics.opengl.GLFilterMode;
import net.survival.client.graphics.opengl.GLTexture;
import net.survival.client.graphics.opengl.GLWrapMode;

// TODO: Remove hard-coding
// TODO: Make non-blocking
class BlockTextureAtlas implements GraphicsResource
{
    public final GLTexture blockTextures;
    public final BlockFace blockFace;
    
    private final float[] texCoords;

    public BlockTextureAtlas(BlockFace blockFace) {
        Bitmap atlas = new Bitmap(256, 128);
        for (BlockType blockType : BlockType.iterateAll()) {
            if (blockType.getTexture(blockFace) == null)
                continue;

            int dstIndex = blockType.getID();
            int dstX = (dstIndex % 16) * 16;
            int dstY = (dstIndex / 16) * 16;
            Bitmap blockBitmap = Bitmap.fromFile("../assets/" + blockType.getTexture(blockFace));
            Bitmap.blit(blockBitmap, 0, 0, 16, 16, atlas, dstX, dstY);
        }

        blockTextures = new GLTexture();
        blockTextures.beginBind()
            .setMinFilter(GLFilterMode.NEAREST_MIPMAP_NEAREST)
            .setMagFilter(GLFilterMode.NEAREST)
            .setWrapS(GLWrapMode.REPEAT)
            .setWrapT(GLWrapMode.REPEAT)
            .setMipmapEnabled(true)
            .setMinLod(0)
            .setMaxLod(4)
            .setData(atlas)
            .endBind();
        
        this.blockFace = blockFace;
        
        texCoords = new float[17 * 17 * 4];
        for (int i = 0; i < 128; ++i) {
            int indexU1 = i * 4;
            int indexV1 = indexU1 + 1;
            int indexU2 = indexU1 + 2;
            int indexV2 = indexU1 + 3;
            
            int tileU = (i % 16);
            int tileV = 15 - (i / 16);
            
            texCoords[indexU1] = tileU / 16.0f;
            texCoords[indexV1] = (tileV / 8.0f) + (1.0f / 8.0f);
            texCoords[indexU2] = (tileU / 16.0f) + (1.0f / 16.0f);
            texCoords[indexV2] = tileV / 8.0f;
        }
    }

    @Override
    public void close() {
        blockTextures.close();
    }

    public float getTexCoordU1(short blockID) {
        return texCoords[blockID << 2];
    }

    public float getTexCoordV1(short blockID) {
        return texCoords[(blockID << 2) + 1];
    }

    public float getTexCoordU2(short blockID) {
        return texCoords[(blockID << 2) + 2];
    }

    public float getTexCoordV2(short blockID) {
        return texCoords[(blockID << 2) + 3];
    }
}