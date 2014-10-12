package ch.asynk.tankontank.game;

import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

// import com.badlogic.gdx.math.Vector2;
// import com.badlogic.gdx.math.Vector3;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.engine.gfx.Drawable;

public class Player implements Drawable, Disposable
{
    private static final float MOVE_TIME = 0.4f;

    private static Random rand = new Random();

    private Army army;
    private Image flag;
    private ArrayList<Pawn> units;
    private ArrayList<Pawn> losses;
    private ArrayList<Pawn> reinforcement;
    private int actionPoints;

    public Player(Army army, TextureAtlas atlas, String name, int size)
    {
        this.army = army;
        this.flag = new Image(atlas.findRegion(name));
        this.units = new ArrayList<Pawn>(size);
        this.losses = new ArrayList<Pawn>(size);
        this.reinforcement = new ArrayList<Pawn>(size);
        setActionPoints();
    }

    public String toString()
    {
        return "Player : " + army + " AP: " + actionPoints +
            " units:" + units.size() + " losses:" + losses.size() + " reinforcement:" + reinforcement.size();
    }

    @Override
    public void dispose()
    {
        flag.dispose();
    }

    public void addUnit(Pawn pawn)
    {
        units.add(pawn);
    }

    public Image getFlag()
    {
        return flag;
    }

    public boolean apExhausted()
    {
        return (actionPoints <= 0);
    }

    public void burnDownOneAp()
    {
        actionPoints -= 1;
        if (actionPoints < 0) System.err.println("AP < 0, damn that's very wrong");
    }

    public void turnEnd()
    {
    }

    public void turnStart()
    {
        for (Pawn pawn : units)
            pawn.reset();
        setActionPoints();
    }

    private void setActionPoints()
    {
        this.actionPoints = 2 + rand.nextInt(3);
    }

    public boolean isEnemy(Pawn pawn)
    {
        return ((Unit) pawn).isEnemy(army);
    }

    public void setPosition(float x, float y)
    {
        flag.setPosition(x, y);
    }

    public Iterator<Pawn> unitIterator()
    {
        return units.iterator();
    }

    @Override
    public void draw(Batch batch)
    {
        flag.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        flag.drawDebug(debugShapes);
    }
}
