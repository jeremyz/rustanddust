package ch.asynk.tankontank;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import ch.asynk.tankontank.engine.Pawn;
import ch.asynk.tankontank.engine.Tile;
import ch.asynk.tankontank.engine.Board;
import ch.asynk.tankontank.engine.Orientation;
import ch.asynk.tankontank.engine.SearchBoard;
import ch.asynk.tankontank.engine.PathBuilder;

public class Helpers
{
    public static class FakePawn extends Pawn
    {
        private int mvt;
        public FakePawn (int mvt)
        {
            this.mvt = mvt;
        }

        @Override public int getMovementPoints()                    { return mvt; }
        @Override public int getRoadMarchBonus()                    { return 1; }
        @Override public int getAngleOfAttack()                     { return 0; }
        @Override public int getFlankSides()                        { return 0; }
        @Override public int getDefense(Tile tile)                  { return 8; }
        @Override public int getEngagementRangeFrom(Tile tile)      { return 3; }

        @Override public boolean isA(PawnId id)                     { return true; }
        @Override public boolean isA(PawnType type)                 { return true; }
        @Override public boolean isHq()                             { return true; }
        @Override public boolean isHqOf(Pawn other)                 { return true; }
        @Override public boolean isUnit()                           { return true; }
        @Override public boolean isHardTarget()                     { return true; }
        @Override public boolean isEnemy(Pawn other)                { return true; }

        @Override public boolean canMove()                          { return true; }
        @Override public boolean canRotate()                        { return true; }
        @Override public boolean canEngage()                        { return true; }
        @Override public boolean canEngage(Pawn other)              { return true; }
        @Override public boolean canAssistEngagementWithoutLos()    { return true; }

        @Override public void move()                                { }
        @Override public void engage()                              { }

        @Override public void reset()                               { }
        @Override public void revertLastMove()                      { }
    }

    public static class FakeTile extends Tile
    {
        public boolean offMap;

        public FakeTile(int col, int row)
        {
            super(col, row);
            offMap = false;
        }

        public FakeTile(int col, int row, boolean offMap)
        {
            super(col, row);
            this.offMap = offMap;
        }

        @Override public boolean isOffMap()
        {
            return offMap;
        }
        @Override public int exitCost()                             { return 1; }
        @Override public int costFrom(Pawn pawn, Orientation side)  { return 1; }
        @Override public int defense()                              { return 0; }

        @Override public boolean isA(TileTerrain terrain)           { return true; }
        @Override public boolean road(Orientation side)             { return false; }
        @Override public boolean atLeastOneMove(Pawn pawn)          { return true; }
        @Override public boolean blockLineOfSightFrom(Tile from)    { return false; }
    }

    public static class FakeSearchBoard extends SearchBoard
    {
        public FakeSearchBoard(Board b, int cols, int rows)
        {
            super(b, cols, rows);
        }

        public int distance(int col0, int row0, int col1, int row1)
        {
            return distance(getNode(col0, row0), getNode(col1, row1));
        }
    }

    public static class FakeBoard extends Board
    {
        public FakePawn pawn;
        public FakeTile fakeTiles[];
        public PathBuilder pathBuilder;

        public FakeBoard(int cols, int rows, int mvt)
        {
            super(cols, rows);
            fakeTiles = new FakeTile[(cols + 2) * (rows + 2)];

            int idx = 0;
            for (int i = -1; i < (rows + 1); i++) {
                for ( int j = -1; j < (cols + 1); j ++) {
                    boolean offmap = ((j < 0) || (i < 0) || (j >= cols) || (i >= rows));
                    fakeTiles[idx] = new FakeTile((j + ((i + 1) / 2)), i, offmap);
                    idx += 1;
                }
            }

            fakeTiles[ 36 - 2].offMap = true;
            fakeTiles[ 60 - 2].offMap = true;
            fakeTiles[ 84 - 2].offMap = true;
            fakeTiles[108 - 2].offMap = true;

            pawn = new FakePawn(mvt);
            pathBuilder = new PathBuilder(this, 10, 20, 5, 10);
        }

        @Override
        public Tile getTile(int col, int row)
        {
            int i = getTileOffset(col, row);
            if (i < 0)
                return null;
            return fakeTiles[i];
        }

        public int buildPathBuilder(int x0, int y0, int x1, int y1)
        {
            pathBuilder.init(pawn, getTile(x0, y0));
            return pathBuilder.build(getTile(x1, y1));
        }

        public int togglePoint(int x, int y)
        {
            return pathBuilder.toggleCtrlTile(getTile(x, y));
        }
    }
}
