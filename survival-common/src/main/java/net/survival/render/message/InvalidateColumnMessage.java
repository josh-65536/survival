package net.survival.render.message;

import net.survival.block.Column;
import net.survival.interaction.InteractionContext;
import net.survival.interaction.MessagePriority;

public class InvalidateColumnMessage extends RenderMessage
{
    public final long columnPos;
    public final Column column;

    public InvalidateColumnMessage(long columnPos, Column column) {
        this.columnPos = columnPos;
        this.column = column;
    }

    @Override
    public void accept(RenderMessageVisitor visitor, InteractionContext ic) {
        visitor.visit(ic, this);
    }

    @Override
    public int getPriority() {
        return MessagePriority.RESERVED_DRAW;
    }
}