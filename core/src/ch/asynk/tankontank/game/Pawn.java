package ch.asynk.tankontank.game;

import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;

public class Pawn extends Image
{
    public static final int DRAGGED_Z_INDEX = 10;
    private static final float MOVE_TIME = 0.3f;

    private HexMap map;
    private ArrayDeque<GridPoint3> path = new ArrayDeque<GridPoint3>();

    public Pawn(TextureRegion region, HexMap map)
    {
        super(region);
        this.map = map;
        setOrigin((getWidth() / 2.f), (getHeight() / 2.f));
    }

    public GridPoint3 getHex()
    {
        if (path.size() == 0) return null;
        return path.getFirst();
    }

    public void moveTo(GridPoint2 hex)
    {
        moveTo(new GridPoint3(hex.x, hex.y, (int) getRotation()));
    }

    public void moveTo(int col, int row, int angle)
    {
        moveTo(new GridPoint3(col, row, angle));
    }

    private void moveTo(GridPoint3 hex)
    {
        if ((hex.x == -1) || (hex.y == -1)) {
            resetMoves();
        } else {
            map.setPawnOn(this, hex);
            path.push(hex);
        }
    }

    public void resetMoves()
    {
        final Pawn self = this;
        final GridPoint3 finalHex = path.getLast();

        SequenceAction seq = new SequenceAction();
        while(path.size() != 0) {
            Vector2 v = map.getPawnPosAt(this, path.pop());
            seq.addAction(Actions.moveTo(v.x, v.y, MOVE_TIME));
        }

        seq.addAction( Actions.run(new Runnable() {
            @Override
            public void run() {
                map.setPawnOn(self, finalHex);
                path.push(finalHex);
            }
        }));

        addAction(seq);
    }

    public void moveDone()
    {
        GridPoint3 hex = path.pop();
        path.clear();
        path.push(hex);
    }
}
