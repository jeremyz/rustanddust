package ch.asynk.tankontank.engine.gfx;

public interface Animation
{
    public Node getNode();

    public boolean act(float delta);

    public void free();
}
