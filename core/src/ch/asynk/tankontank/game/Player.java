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
import ch.asynk.tankontank.engine.Army;
import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.gfx.Image;
import ch.asynk.tankontank.engine.gfx.Drawable;
import ch.asynk.tankontank.game.hud.Msg;

public class Player implements Drawable, Disposable
{
    private static final float MOVE_TIME = 0.4f;

    private static Random rand = new Random();

    private Army army;
    private Image flag;
    private Msg status;
    private int turn;
    private int apSpent;
    private int actionPoints;
    private ArrayList<Pawn> units;
    private ArrayList<Pawn> casualties;
    private ArrayList<Pawn> reinforcement;

    public Player(final TankOnTank game, Army army, BitmapFont font, TextureAtlas atlas, String name, int size)
    {
        this.army = army;
        this.turn = 0;
        this.actionPoints = 0;
        this.flag = new Image(atlas.findRegion(name));
        this.units = new ArrayList<Pawn>(size);
        this.casualties = new ArrayList<Pawn>(size);
        this.reinforcement = new ArrayList<Pawn>(size);
        this.status = new Msg(font, atlas.findRegion("disabled"));
    }

    public String getName()
    {
        return army.toString();
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
        return (apSpent == actionPoints);
    }

    public void burnDownOneAp()
    {
        apSpent += 1;
        updateInfo();
        if (apSpent > actionPoints) System.err.println("ERROR: spent too much AP, please report");
    }

    public void turnEnd()
    {
    }

    public void turnStart()
    {
        turn += 1;
        for (Pawn pawn : units)
            pawn.reset();
        computeActionPoints();
        updateInfo();
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
        apSpent = 0;
    }

    private void updateInfo()
    {
        status.write("Turn: " + turn + " AP: " + (apSpent + 1), flag.getX(), (flag.getY() - 40), 0, 10);
    }

    public boolean isEnemy(Pawn pawn)
    {
        return pawn.isEnemy(army);
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
