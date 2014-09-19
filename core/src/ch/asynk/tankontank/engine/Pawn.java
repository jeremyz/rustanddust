package ch.asynk.tankontank.engine;

import com.badlogic.gdx.math.Vector3;

import ch.asynk.tankontank.engine.gfx.Node;
import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;

public interface Pawn extends Node
{
    public Vector3 getLastPosition();

    public void moveBy(float x, float y);

    public void pushMove(float x, float y, int z, Pawn.Orientation o);

    public AnimationSequence getResetMovesAnimation();

    public void moveDone();

    public enum Orientation
    {
        KEEP(0),
        WEST(180),
        NORTH_WEST(120),
        NORTH_EAST (60),
        EAST(0),
        SOUTH_EAST(-60),
        SOUTH_WEST(-120);

        public final int v;
        Orientation(int v) { this.v = v; }
    }
}
