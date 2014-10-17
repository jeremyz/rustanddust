package ch.asynk.tankontank.game;

import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.tankontank.TankOnTank;
import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.engine.gfx.Drawable;
import ch.asynk.tankontank.game.hud.Msg;

public class Player implements Drawable, Disposable
{
    private static final float MOVE_TIME = 0.4f;

    private static Random rand = new Random();

    public Army army;
    public Image flag;
    public Msg status;
    public int actionPoints;
    public ArrayList<Pawn> units;
    public ArrayList<Pawn> casualties;
    public ArrayList<Pawn> reinforcement;

    public Player(final TankOnTank game, Army army, BitmapFont font, TextureAtlas atlas, String name, int size)
    {
        this.army = army;
        this.actionPoints = 0;
        this.flag = new Image(atlas.findRegion(name));
        this.units = new ArrayList<Pawn>(size);
        this.casualties = new ArrayList<Pawn>(size);
        this.reinforcement = new ArrayList<Pawn>(size);
        this.status = new Msg(font, atlas.findRegion("disabled"));
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

    public boolean apExhausted()
    {
        return (actionPoints <= 0);
    }

    public void burnDownOneAp()
    {
        actionPoints -= 1;
        updateInfo();
        System.err.println("1 AP burned   " + toString());
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
        computeActionPoints();
        updateInfo();
        System.err.println("TurnStart " + toString());
    }

    public int d6()
    {
        return rand.nextInt(6) + 1;
    }

    private void computeActionPoints()
    {
        this.actionPoints = 2;
        if (d6() > 2) {
            this.actionPoints += 1;
            if (d6() > 3)
                this.actionPoints += 1;
        }
    }

    private void updateInfo()
    {
        status.write("AP: " + actionPoints, flag.getX(), (flag.getY() - 40), 0, 10);
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

    public Pawn promote(Pawn pawn)
    {
        for (Pawn p: casualties) {
            if (p.isHqOf(pawn)) {
                units.remove(pawn);
                casualties.add(pawn);
                units.add(p);
                casualties.remove(p);
                return p;
            }
        }
        return null;
    }

    public boolean contains(float x, float y)
    {
        return flag.contains(x, y);
    }

    public void setTopLeft(float height, float offset)
    {
        flag.setPosition(offset, (height - flag.getHeight() - offset));
    }

    public Iterator<Pawn> unitIterator()
    {
        return units.iterator();
    }

    @Override
    public void draw(Batch batch)
    {
        flag.draw(batch);
        status.draw(batch);
    }

    @Override
    public void drawDebug(ShapeRenderer debugShapes)
    {
        flag.drawDebug(debugShapes);
    }
}
