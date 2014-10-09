package ch.asynk.tankontank;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Tile;
import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.engine.SearchBoard;

public class Helpers
{
    public static class FakePawn extends Pawn
    {
        private int mvt;
        public FakePawn (int mvt)
        {
            this.mvt = mvt;
        }

        public int getMovementPoints()              { return mvt; }
        public int getRoadMarchBonus()              { return 1; }
        public boolean isHq()                       { return true; }
        public boolean isUnit()                     { return true; }
        public boolean isEnemy(Pawn other)          { return true; }
        public boolean canMove()                    { return true; }
        public boolean canRotate()                  { return true; }
        public boolean canAttack()                  { return true; }
        public boolean canAttack(Pawn other)        { return true; }
        public int getAngleOfAttack()               { return 0; }
        public int getAttackRangeFrom(Tile tile)    { return 3; }
    }

    public static class FakeTile extends Tile
    {
        public boolean offMap;

        public FakeTile()
        {
            super();
            offMap = false;
        }

        public FakeTile(boolean offMap)
        {
            this.offMap = offMap;
        }

        public boolean isOffMap()
        {
            return offMap;
        }
        public boolean blockLineOfSightFrom(Tile from)  { return false; }
        public boolean atLeastOneMove(Pawn pawn)        { return true; }
        public boolean road(Orientation side)           { return false; }
        public boolean hasTargetsFor(Pawn pawn)         { return false; }
        public int costFrom(Pawn pawn, Orientation side, boolean road) { return 1; }
    }

    public static class FakeBoard extends Board
    {
        private int cols;
        private int rows;
        public FakeTile fakeTiles[];
        public FakePawn pawn;

        public FakeBoard(int cols, int rows, int mvt)
        {
            super(cols, rows);
            this.cols = cols;
            this.rows = rows;
            fakeTiles = new FakeTile[cols * rows];
            for (int i = 0; i < rows; i++) {
                for ( int j = 0; j < cols; j ++)
                    fakeTiles[j + (i * cols)] = new FakeTile();
            }
            fakeTiles[19].offMap = true;
            fakeTiles[39].offMap = true;
            fakeTiles[59].offMap = true;
            fakeTiles[79].offMap = true;
            pawn = new FakePawn(mvt);
        }

        @Override
        public Tile getTile(int col, int row)
        {
            int colOffset = ((row + 1) / 2);
            if ((col < colOffset) || (row < 0) || (row >= rows) || ((col - colOffset) >= cols))
                return new FakeTile(true);
            int idx = ((col - colOffset)) + (row * cols);
            return fakeTiles[idx];
        }

        public List<ArrayList<SearchBoard.Node>> buildPossiblePaths(int x0, int y0, int x1, int y1)
        {
            buildPossiblePaths(pawn, new GridPoint2(x0, y0), new GridPoint2(x1, y1), new HashSet<GridPoint2>());
            return paths;
        }

        public int buildPossiblePaths(int x0, int y0, int x1, int y1, HashSet<GridPoint2> points)
        {
            return buildPossiblePaths(pawn, new GridPoint2(x0, y0), new GridPoint2(x1, y1), points);
        }

        public int togglePoint(int x, int y)
        {
            return possiblePathsFilterToggle(new GridPoint2(x, y), new HashSet<GridPoint2>());
        }
    }
}
