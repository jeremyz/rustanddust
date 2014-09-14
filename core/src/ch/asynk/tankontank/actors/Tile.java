package ch.asynk.tankontank.actors;

import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.GridPoint3;

public class Tile extends Image
{
    public static final int DRAGGED_Z_INDEX = 10;
    private static final float MOVE_TIME = 0.3f;

    public GridPoint3 cell = new GridPoint3(-1, -1, 0);

    private HexMap map;
    private ArrayDeque<GridPoint3> path = new ArrayDeque<GridPoint3>();

    public Tile(TextureRegion region, HexMap map)
    {
        super(region);
        this.map = map;
        setOrigin((getWidth() / 2.f), (getHeight() / 2.f));
    }

    public void setRotation(int angle)
    {
        super.setRotation(angle);
        cell.z = angle;
    }

    public void moveTo(int col, int row)
    {
        moveTo(new GridPoint3(col, row, cell.z));
    }

    public void moveTo(int col, int row, int angle)
    {
        moveTo(new GridPoint3(col, row, angle));
    }

    private void moveTo(GridPoint3 nextCell)
    {
        if ((nextCell.x == -1) || (nextCell.y == -1)) {
            resetMoves();
        } else {
            map.setTileOn(this, nextCell);
            path.push(nextCell);
            cell = nextCell;
        }
    }

    public void resetMoves()
    {
        final Tile self = this;
        final GridPoint3 finalPos = path.getLast();

        SequenceAction seq = new SequenceAction();
        while(path.size() != 0) {
            Vector2 v = map.getTilePosAt(this, path.pop());
            seq.addAction(Actions.moveTo(v.x, v.y, MOVE_TIME));
        }

        seq.addAction( Actions.run(new Runnable() {
            @Override
            public void run() {
                map.setTileOn(self, finalPos);
                path.push(finalPos);
                cell = finalPos;
            }
        }));

        addAction(seq);
    }

    public void done()
    {
        GridPoint3 p = path.pop();
        path.clear();
        path.push(p);
    }
}
