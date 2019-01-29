package net.survival.ui;

public abstract class UiElement
{
    public abstract <R> R accept(UiElementVisitor<R> visitor);

    public abstract void accept(UiElementVoidVisitor visitor);
}