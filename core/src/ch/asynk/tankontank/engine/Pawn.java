package ch.asynk.tankontank.engine;

import com.badlogic.gdx.math.Vector3;

import ch.asynk.tankontank.engine.gfx.Node;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;

public interface Pawn extends Node
{
    public Vector3 getLastPosition();

    public void moveDone();

    public void pushMove(float x, float y, Pawn.Orientation o);

    public AnimationSequence getResetMovesAnimation();

    public enum Orientation
    {
        KEEP(0),
        NORTH_WEST(30),
        WEST(90),
        SOUTH_WEST(150),
        NORTH_EAST (-30),
        EAST(-90),
        SOUTH_EAST(-150);

        public final int v;
        Orientation(int v) { this.v = v; }
    }
}
