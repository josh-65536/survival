package net.survival.graphics.blockrenderer;

import net.survival.blocktype.BlockId;
import net.survival.graphics.opengl.GLDisplayList;

class EastCeilingStairsRenderer extends DefaultBlockRenderer
{
    @Override
    public void pushBottomFaces(int x, int y, int z, int blockId, int adjacentBlockId,
            GLDisplayList.Builder builder)
    {
        var u1 = bottomFaceTextures.getTexCoordU1(blockId);
        var u2 = bottomFaceTextures.getTexCoordU2(blockId);
        var v1 = bottomFaceTextures.getTexCoordV1(blockId);
        var v2 = bottomFaceTextures.getTexCoordV2(blockId);

        if (!blockToBlockingTopTable[BlockId.typeIdFromFullId(adjacentBlockId)]) {
            builder.setTexCoord(u1, v1); builder.pushVertex(x + 0.5f, y, z       );
            builder.setTexCoord(u2, v1); builder.pushVertex(x + 1.0f, y, z       );
            builder.setTexCoord(u2, v2); builder.pushVertex(x + 1.0f, y, z + 1.0f);
            builder.setTexCoord(u2, v2); builder.pushVertex(x + 1.0f, y, z + 1.0f);
            builder.setTexCoord(u1, v2); builder.pushVertex(x + 0.5f, y, z + 1.0f);
            builder.setTexCoord(u1, v1); builder.pushVertex(x + 0.5f, y, z       );
        }

        builder.setTexCoord(u1, v1); builder.pushVertex(x,        y + 0.5f, z       );
        builder.setTexCoord(u2, v1); builder.pushVertex(x + 0.5f, y + 0.5f, z       );
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 0.5f, y + 0.5f, z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 0.5f, y + 0.5f, z + 1.0f);
        builder.setTexCoord(u1, v2); builder.pushVertex(x,        y + 0.5f, z + 1.0f);
        builder.setTexCoord(u1, v1); builder.pushVertex(x,        y + 0.5f, z       );
    }

    @Override
    public void pushLeftFaces(int x, int y, int z, int blockId, int adjacentBlockId,
            GLDisplayList.Builder builder)
    {
        var u1 = leftFaceTextures.getTexCoordU1(blockId);
        var u2 = leftFaceTextures.getTexCoordU2(blockId);
        var v1 = leftFaceTextures.getTexCoordV1(blockId);
        var v2 = leftFaceTextures.getTexCoordV2(blockId);

        if (!blockToBlockingRightTable[BlockId.typeIdFromFullId(adjacentBlockId)]) {
            builder.setTexCoord(u1, v1); builder.pushVertex(x, y + 0.5f, z       );
            builder.setTexCoord(u2, v1); builder.pushVertex(x, y + 0.5f, z + 1.0f);
            builder.setTexCoord(u2, v2); builder.pushVertex(x, y + 1.0f, z + 1.0f);
            builder.setTexCoord(u2, v2); builder.pushVertex(x, y + 1.0f, z + 1.0f);
            builder.setTexCoord(u1, v2); builder.pushVertex(x, y + 1.0f, z       );
            builder.setTexCoord(u1, v1); builder.pushVertex(x, y + 0.5f, z       );
        }

        builder.setTexCoord(u1, v1); builder.pushVertex(x + 0.5f, y,        z       );
        builder.setTexCoord(u2, v1); builder.pushVertex(x + 0.5f, y,        z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 0.5f, y + 0.5f, z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 0.5f, y + 0.5f, z + 1.0f);
        builder.setTexCoord(u1, v2); builder.pushVertex(x + 0.5f, y + 0.5f, z       );
        builder.setTexCoord(u1, v1); builder.pushVertex(x + 0.5f, y,        z       );
    }

    @Override
    public void pushFrontFaces(int x, int y, int z, int blockId, int adjacentBlockId,
            GLDisplayList.Builder builder)
    {
        if (blockToBlockingBackTable[BlockId.typeIdFromFullId(adjacentBlockId)])
            return;

        var u1 = frontFaceTextures.getTexCoordU1(blockId);
        var u2 = frontFaceTextures.getTexCoordU2(blockId);
        var v1 = frontFaceTextures.getTexCoordV1(blockId);
        var v2 = frontFaceTextures.getTexCoordV2(blockId);
        builder.setTexCoord(u1, v1); builder.pushVertex(x,        y + 0.5f, z + 1.0f);
        builder.setTexCoord(u2, v1); builder.pushVertex(x + 0.5f, y + 0.5f, z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 0.5f, y + 1.0f, z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 0.5f, y + 1.0f, z + 1.0f);
        builder.setTexCoord(u1, v2); builder.pushVertex(x,        y + 1.0f, z + 1.0f);
        builder.setTexCoord(u1, v1); builder.pushVertex(x,        y + 0.5f, z + 1.0f);

        builder.setTexCoord(u1, v1); builder.pushVertex(x + 0.5f, y,        z + 1.0f);
        builder.setTexCoord(u2, v1); builder.pushVertex(x + 1.0f, y,        z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 1.0f, y + 1.0f, z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 1.0f, y + 1.0f, z + 1.0f);
        builder.setTexCoord(u1, v2); builder.pushVertex(x + 0.5f, y + 1.0f, z + 1.0f);
        builder.setTexCoord(u1, v1); builder.pushVertex(x + 0.5f, y,        z + 1.0f);
    }

    @Override
    public void pushBackFaces(int x, int y, int z, int blockId, int adjacentBlockId,
            GLDisplayList.Builder builder)
    {
        if (blockToBlockingFrontTable[BlockId.typeIdFromFullId(adjacentBlockId)])
            return;

        var u1 = backFaceTextures.getTexCoordU1(blockId);
        var u2 = backFaceTextures.getTexCoordU2(blockId);
        var v1 = backFaceTextures.getTexCoordV1(blockId);
        var v2 = backFaceTextures.getTexCoordV2(blockId);
        builder.setTexCoord(u1, v1); builder.pushVertex(x + 0.5f, y + 0.5f, z);
        builder.setTexCoord(u2, v1); builder.pushVertex(x,        y + 0.5f, z);
        builder.setTexCoord(u2, v2); builder.pushVertex(x,        y + 1.0f, z);
        builder.setTexCoord(u2, v2); builder.pushVertex(x,        y + 1.0f, z);
        builder.setTexCoord(u1, v2); builder.pushVertex(x + 0.5f, y + 1.0f, z);
        builder.setTexCoord(u1, v1); builder.pushVertex(x + 0.5f, y + 0.5f, z);

        builder.setTexCoord(u1, v1); builder.pushVertex(x + 1.0f, y,        z);
        builder.setTexCoord(u2, v1); builder.pushVertex(x + 0.5f, y,        z);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 0.5f, y + 1.0f, z);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 0.5f, y + 1.0f, z);
        builder.setTexCoord(u1, v2); builder.pushVertex(x + 1.0f, y + 1.0f, z);
        builder.setTexCoord(u1, v1); builder.pushVertex(x + 1.0f, y,        z);
    }
}