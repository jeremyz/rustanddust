package ch.asynk.rustanddust;

import ch.asynk.rustanddust.engine.Pawn;
import ch.asynk.rustanddust.engine.HeadedPawn;
import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.Board;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.engine.SearchBoard;
import ch.asynk.rustanddust.engine.PathBuilder;

public class Helpers
{
    public static class FakePawn extends HeadedPawn
    {
        @Override public int getSpentMovementPoints()               { return 0; }
        @Override public int getMovementPoints()                    { return 3; }
        @Override public int getRoadMarchBonus()                    { return 1; }
        @Override public int getFlankSides()                        { return orientation.getBackSides(); }
        @Override public int getAngleOfAttack()                     { return orientation.getFrontSides(); }

        @Override public int getDefense(Tile tile)                  { return 8; }
        @Override public int getEngagementRangeFrom(Tile tile)      { return 3; }
        @Override public boolean preventDefenseOn(Tile tile)        { return true; }

        @Override public boolean isA(PawnCode id)                   { return true; }
        @Override public boolean isA(PawnType type)                 { return true; }
        @Override public boolean isHq()                             { return true; }
        @Override public boolean isHqOf(Pawn other)                 { return true; }
        @Override public boolean isUnit()                           { return true; }
        @Override public boolean isHardTarget()                     { return true; }
        @Override public boolean isEnemy(Pawn other)                { return true; }

        @Override public boolean canMove()                          { return true; }
        @Override public boolean canRotate()                        { return true; }
        @Override public boolean canBreak()                         { return true; }
        @Override public boolean canEngage()                        { return true; }
        @Override public boolean canEngage(Pawn other)              { return true; }
        @Override public boolean canAssistEngagementWithoutLos()    { return true; }

        @Override public void move()                                { }
        @Override public void engage()                              { }

        @Override public void reset()                               { }
        @Override public void revertLastMove()                      { }

        @Override public float getWidth()                           { return 24.0f; }
        @Override public float getHeight()                          { return 24.0f; }
        @Override public void setPosition(float x, float y, float z) { }

        public void setOrientation(Orientation o)                   { this.orientation = o; }
    }

    public static class FakeTile extends Tile
    {
        public boolean offMap;
        public boolean blockLineOfSight;

        public FakeTile(int col, int row)
        {
            super(col, row, 1, null);
            offMap = false;
            blockLineOfSight = false;
        }

        public FakeTile(int col, int row, boolean offMap)
        {
            super(col, row, 1, null);
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
        @Override public boolean roadFrom(Orientation side)         { return false; }
        @Override public boolean atLeastOneMove(Pawn pawn)          { return true; }
        @Override public boolean blockLineOfSight(Tile from, Tile to) { return blockLineOfSight; }

        public void setBlockLineOfSight(boolean block)              { blockLineOfSight = block; }
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

        public Node get(int col, int row) { return getNode(col, row); }
    }

    public static class FakeBoard extends Board
    {
        public FakePawn pawn;
        public FakeTile fakeTiles[];
        public PathBuilder pathBuilder;

        public FakeBoard(int cols, int rows)
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

            pawn = new FakePawn();
            pathBuilder = new PathBuilder(this, 10, 20, 5, 10);
        }

        @Override protected Board.Config getConfig() { return null; }

        @Override public void animationsDone() {}

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

        public int togglePoint(int x, int y, boolean quick)
        {
            return pathBuilder.toggleCtrlTile(getTile(x, y), quick);
        }
    }
}
