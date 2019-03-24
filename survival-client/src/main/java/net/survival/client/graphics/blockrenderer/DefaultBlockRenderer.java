package net.survival.client.graphics.blockrenderer;

import net.survival.blocktype.BlockId;
import net.survival.client.graphics.opengl.GLDisplayList;

class DefaultBlockRenderer extends BlockRenderer
{
    public DefaultBlockRenderer() {
        super(false);
    }

    @Override
    public void pushTopFaces(int x, int y, int z, int blockId, int adjacentBlockId,
            GLDisplayList.Builder builder)
    {
        var typeId = BlockId.typeIdFromFullId(blockId);
        if (blockToBlockingBottomTable[BlockId.typeIdFromFullId(adjacentBlockId)])
            return;

        var u1 = topFaceTextures.getTexCoordU1(typeId);
        var u2 = topFaceTextures.getTexCoordU2(typeId);
        var v1 = topFaceTextures.getTexCoordV1(typeId);
        var v2 = topFaceTextures.getTexCoordV2(typeId);
        builder.setTexCoord(u1, v1); builder.pushVertex(x,        y + 1.0f, z + 1.0f);
        builder.setTexCoord(u2, v1); builder.pushVertex(x + 1.0f, y + 1.0f, z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 1.0f, y + 1.0f, z       );
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 1.0f, y + 1.0f, z       );
        builder.setTexCoord(u1, v2); builder.pushVertex(x,        y + 1.0f, z       );
        builder.setTexCoord(u1, v1); builder.pushVertex(x,        y + 1.0f, z + 1.0f);
    }

    @Override
    public void pushBottomFaces(int x, int y, int z, int blockId, int adjacentBlockId,
            GLDisplayList.Builder builder)
    {
        var typeId = BlockId.typeIdFromFullId(blockId);
        if (blockToBlockingTopTable[BlockId.typeIdFromFullId(adjacentBlockId)])
            return;

        var u1 = bottomFaceTextures.getTexCoordU1(typeId);
        var u2 = bottomFaceTextures.getTexCoordU2(typeId);
        var v1 = bottomFaceTextures.getTexCoordV1(typeId);
        var v2 = bottomFaceTextures.getTexCoordV2(typeId);
        builder.setTexCoord(u1, v1); builder.pushVertex(x,        y, z       );
        builder.setTexCoord(u2, v1); builder.pushVertex(x + 1.0f, y, z       );
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 1.0f, y, z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 1.0f, y, z + 1.0f);
        builder.setTexCoord(u1, v2); builder.pushVertex(x,        y, z + 1.0f);
        builder.setTexCoord(u1, v1); builder.pushVertex(x,        y, z       );
    }

    @Override
    public void pushLeftFaces(int x, int y, int z, int blockId, int adjacentBlockId,
            GLDisplayList.Builder builder)
    {
        var typeId = BlockId.typeIdFromFullId(blockId);
        if (blockToBlockingRightTable[BlockId.typeIdFromFullId(adjacentBlockId)])
            return;

        var u1 = leftFaceTextures.getTexCoordU1(typeId);
        var u2 = leftFaceTextures.getTexCoordU2(typeId);
        var v1 = leftFaceTextures.getTexCoordV1(typeId);
        var v2 = leftFaceTextures.getTexCoordV2(typeId);
        builder.setTexCoord(u1, v1); builder.pushVertex(x, y,        z       );
        builder.setTexCoord(u2, v1); builder.pushVertex(x, y,        z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x, y + 1.0f, z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x, y + 1.0f, z + 1.0f);
        builder.setTexCoord(u1, v2); builder.pushVertex(x, y + 1.0f, z       );
        builder.setTexCoord(u1, v1); builder.pushVertex(x, y,        z       );
    }

    @Override
    public void pushRightFaces(int x, int y, int z, int blockId, int adjacentBlockId,
            GLDisplayList.Builder builder)
    {
        var typeId = BlockId.typeIdFromFullId(blockId);
        if (blockToBlockingLeftTable[BlockId.typeIdFromFullId(adjacentBlockId)])
            return;

        var u1 = rightFaceTextures.getTexCoordU1(typeId);
        var u2 = rightFaceTextures.getTexCoordU2(typeId);
        var v1 = rightFaceTextures.getTexCoordV1(typeId);
        var v2 = rightFaceTextures.getTexCoordV2(typeId);
        builder.setTexCoord(u1, v1); builder.pushVertex(x + 1.0f, y,        z + 1.0f);
        builder.setTexCoord(u2, v1); builder.pushVertex(x + 1.0f, y,        z       );
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 1.0f, y + 1.0f, z       );
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 1.0f, y + 1.0f, z       );
        builder.setTexCoord(u1, v2); builder.pushVertex(x + 1.0f, y + 1.0f, z + 1.0f);
        builder.setTexCoord(u1, v1); builder.pushVertex(x + 1.0f, y,        z + 1.0f);
    }

    @Override
    public void pushFrontFaces(int x, int y, int z, int blockId, int adjacentBlockId,
            GLDisplayList.Builder builder)
    {
        var typeId = BlockId.typeIdFromFullId(blockId);
        if (blockToBlockingBackTable[BlockId.typeIdFromFullId(adjacentBlockId)])
            return;

        var u1 = frontFaceTextures.getTexCoordU1(typeId);
        var u2 = frontFaceTextures.getTexCoordU2(typeId);
        var v1 = frontFaceTextures.getTexCoordV1(typeId);
        var v2 = frontFaceTextures.getTexCoordV2(typeId);
        builder.setTexCoord(u1, v1); builder.pushVertex(x,        y,        z + 1.0f);
        builder.setTexCoord(u2, v1); builder.pushVertex(x + 1.0f, y,        z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 1.0f, y + 1.0f, z + 1.0f);
        builder.setTexCoord(u2, v2); builder.pushVertex(x + 1.0f, y + 1.0f, z + 1.0f);
        builder.setTexCoord(u1, v2); builder.pushVertex(x,        y + 1.0f, z + 1.0f);
        builder.setTexCoord(u1, v1); builder.pushVertex(x,        y,        z + 1.0f);
    }

    @Override
    public void pushBackFaces(int x, int y, int z, int blockId, int adjacentBlockId,
            GLDisplayList.Builder builder)
    {
        var typeId = BlockId.typeIdFromFullId(blockId);
        if (blockToBlockingFrontTable[BlockId.typeIdFromFullId(adjacentBlockId)])
            return;

        var u1 = backFaceTextures.getTexCoordU1(typeId);
        var u2 = backFaceTextures.getTexCoordU2(typeId);
        var v1 = backFaceTextures.getTexCoordV1(typeId);
        var v2 = backFaceTextures.getTexCoordV2(typeId);
        builder.setTexCoord(u1, v1); builder.pushVertex(x + 1.0f, y,        z);
        builder.setTexCoord(u2, v1); builder.pushVertex(x,        y,        z);
        builder.setTexCoord(u2, v2); builder.pushVertex(x,        y + 1.0f, z);
        builder.setTexCoord(u2, v2); builder.pushVertex(x,        y + 1.0f, z);
        builder.setTexCoord(u1, v2); builder.pushVertex(x + 1.0f, y + 1.0f, z);
        builder.setTexCoord(u1, v1); builder.pushVertex(x + 1.0f, y,        z);
    }
}