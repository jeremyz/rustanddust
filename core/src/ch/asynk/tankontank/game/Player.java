package ch.asynk.tankontank.game;

import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
    private ArrayList<Pawn> casualties;
    private ArrayList<Pawn> reinforcement;
    private int actionPoints;

    public Player(Army army, TextureAtlas atlas, String name, int size)
    {
        this.army = army;
        this.flag = new Image(atlas.findRegion(name));
        this.units = new ArrayList<Pawn>(size);
        this.casualties = new ArrayList<Pawn>(size);
        this.reinforcement = new ArrayList<Pawn>(size);
        this.actionPoints = 0;
    }

    public String toString()
    {
        return army + " AP: " + actionPoints +
            " units:" + units.size() + " casualties:" + casualties.size() + " reinforcement:" + reinforcement.size();
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

    public void casualty(Pawn pawn)
    {
        units.remove(pawn);
        casualties.add(pawn);
        System.err.println("    casualty : " + pawn);
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
        if (actionPoints < 0) System.err.println("ERROR: AP < 0, damn that's very wrong, please report");
    }

    public void turnEnd()
    {
        System.err.println("TurnEnd   " + toString());
    }

    public void turnStart()
    {
        for (Pawn pawn : units)
            pawn.reset();
        setActionPoints();
        System.err.println("TurnStart " + toString());
    }

    public int d6()
    {
        return rand.nextInt(6) + 1;
    }

    private void setActionPoints()
    {
        this.actionPoints = 2;
        if (d6() > 2) {
            this.actionPoints += 1;
            if (d6() > 3)
                this.actionPoints += 1;
        }
    }

    public boolean isEnemy(Pawn pawn)
    {
        return ((Unit) pawn).isEnemy(army);
    }

    public boolean canPromote(Pawn pawn)
    {
        if (pawn.isHq()) return false;
        for (Pawn p: casualties)
            if (p.isHqOf(pawn)) return true;
        return false;
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
