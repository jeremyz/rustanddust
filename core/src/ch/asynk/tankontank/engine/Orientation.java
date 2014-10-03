package ch.asynk.tankontank.engine;

// import java.util.Set;
// import java.util.List;
// import java.util.Vector;
// import java.util.Iterator;
// import java.util.LinkedHashSet;

// import com.badlogic.gdx.Gdx;

// import com.badlogic.gdx.utils.Disposable;

// import com.badlogic.gdx.graphics.Texture;
// import com.badlogic.gdx.graphics.g2d.Batch;
// import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

// import com.badlogic.gdx.utils.Pool;
// import com.badlogic.gdx.math.Vector2;
// import com.badlogic.gdx.math.Vector3;
// import com.badlogic.gdx.math.GridPoint2;
// import com.badlogic.gdx.math.GridPoint3;
// import com.badlogic.gdx.math.Matrix4;

// import ch.asynk.tankontank.engine.gfx.Image;
// import ch.asynk.tankontank.engine.gfx.Animation;
// import ch.asynk.tankontank.engine.gfx.animations.AnimationSequence;
// import ch.asynk.tankontank.engine.gfx.animations.RunnableAnimation;

public enum Orientation
{
    ALL(0, 63),
    KEEP(0, 0),
    NORTH(270, 1),
    NORTH_EAST(210, 2),
    SOUTH_EAST(150, 4),
    SOUTH(90, 8),
    SOUTH_WEST (30, 16),
    NORTH_WEST(330, 32);

    public static int offset = 0;
    public static float delta = 5f;
    private final int r;
    public final int s;

    Orientation(int r, int s) { this.r = r; this.s = s; }

    public float r() { return offset + r; }

    public boolean isInSides(int sides)
    {
        return ((sides & s) == s);
    }

    public Orientation left()
    {
        if (this == NORTH) return NORTH_WEST;
        else return fromSide(s >> 1);
    }

    public Orientation right()
    {
        if (this == NORTH_WEST) return NORTH;
        else return fromSide(s << 1);
    }

    public Orientation opposite()
    {
        return left().left().left();
    }

    public int allBut()
    {
        return ALL.s & (s ^ 0xFFFF);
    }

    public int getFrontSides()
    {
        return s | left().s | right().s;
    }

    public int getBackSides()
    {
        return opposite().getFrontSides();
    }

    public static Orientation fromSide(int s)
    {
        if (s == 1) return NORTH;
        else if (s == NORTH_EAST.s) return NORTH_EAST;
        else if (s == SOUTH_EAST.s) return SOUTH_EAST;
        else if (s == SOUTH.s) return SOUTH;
        else if (s == SOUTH_WEST.s) return SOUTH_WEST;
        else if (s == NORTH_WEST.s) return NORTH_WEST;
        else return KEEP;
    }

    public static Orientation fromRotation(float r)
    {
        if (r < 0) r += 360f;
        if ((r > (NORTH.r - 5f)) && (r < (NORTH.r + 5f))) return NORTH;
        else if ((r > (NORTH_EAST.r - delta)) && (r < (NORTH_EAST.r + delta))) return NORTH_EAST;
        else if ((r > (SOUTH_EAST.r - delta)) && (r < (SOUTH_EAST.r + delta))) return SOUTH_EAST;
        else if ((r > (SOUTH.r - delta)) && (r < (SOUTH.r + delta))) return SOUTH;
        else if ((r > (SOUTH_WEST.r - delta)) && (r < (SOUTH_WEST.r + delta))) return SOUTH_WEST;
        else if ((r > (NORTH_WEST.r - delta)) && (r < (NORTH_WEST.r + delta))) return NORTH_WEST;
        else return KEEP;
    }
}
