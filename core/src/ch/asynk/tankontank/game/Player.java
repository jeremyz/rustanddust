package ch.asynk.tankontank.game;

import java.util.Random;

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

public class Player extends ch.asynk.tankontank.engine.Player implements Drawable, Disposable
{
    private static final float MOVE_TIME = 0.4f;

    private static Random rand = new Random();

    private int turn;
    private int apSpent;
    private int actionPoints;
    private Image flag;
    private Msg status;

    public Player(final TankOnTank game, Army army, BitmapFont font, TextureAtlas atlas, String name, int n)
    {
        super(army, n);
        this.turn = 0;
        this.actionPoints = 0;
        this.flag = new Image(atlas.findRegion(name));
        this.status = new Msg(font, atlas.findRegion("disabled"));
    }

    public String toString()
    {
        return faction + " AP: " + actionPoints +
            " units:" + units.size() + " casualties:" + casualties.size() + " reinforcement:" + reinforcement.size();
    }

    @Override
    public void dispose()
    {
        flag.dispose();
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

    @Override
    public void turnEnd()
    {
    }

    @Override
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

    public boolean canPromote(Pawn pawn)
    {
        if (pawn.isHq()) return false;
        for (Pawn p: casualties)
            if (p.isHqOf(pawn)) return true;
        return false;
    }

    public Unit promote(Unit unit)
    {
        for (Pawn p: casualties) {
            if (p.isHqOf(unit)) {
                units.remove(unit);
                casualties.add(unit);
                units.add(p);
                casualties.remove(p);
                return (Unit) p;
            }
        }
        return null;
    }

    public boolean contains(float x, float y)
    {
        return flag.contains(x, y);
    }

    public void setTopLeft(float x, float y)
    {
        flag.setPosition(x, (y - flag.getHeight()));
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
