package ch.asynk.rustanddust;

import java.util.List;
import java.util.Vector;

import org.junit.Test;
import org.junit.Before;

import ch.asynk.rustanddust.engine.Pawn;
import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.engine.SearchBoard.Node;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class Attacks
{
    private Helpers.FakeBoard fakeBoard;
    private Helpers.FakeSearchBoard sb;
    private Helpers.FakePawn shooter;
    private Helpers.FakePawn target;

    @Before
    public void initialize()
    {
        int cols = 10;
        int rows = 9;
        fakeBoard = new Helpers.FakeBoard(cols, rows);
        sb = new Helpers.FakeSearchBoard(fakeBoard, cols, rows);
        shooter = new Helpers.FakePawn();
        target = new Helpers.FakePawn();
        setPawn(shooter, sb.get(6, 4), Orientation.NORTH);
    }

    private void block(int i, int j, boolean block)
    {
        ((Helpers.FakeTile) fakeBoard.getTile(i, j)).setBlockLineOfSight(block);
    }

    private void setPawn(Helpers.FakePawn pawn, Node n, Orientation o)
    {
        pawn.setOnTile(fakeBoard.getTile(n.col, n.row), o.r());
        pawn.setOrientation(o);
    }

    @Test public void test_1() {
        Node []nodes = {
             sb.get(7, 5)
            ,sb.get(7, 4)
            ,sb.get(6, 3)
            ,sb.get(7, 6)
            ,sb.get(8, 6)
            ,sb.get(8, 5)
            ,sb.get(8, 4)
            ,sb.get(7, 3)
            ,sb.get(6, 2)
            ,sb.get(5, 2)
            ,sb.get(8, 7)
            ,sb.get(9, 7)
            ,sb.get(9 ,6)
            ,sb.get(9 ,5)
            ,sb.get(9, 4)
            ,sb.get(8, 3)
            ,sb.get(7, 2)
            ,sb.get(6, 1)
            ,sb.get(5, 1)
        };

        for (Node n : nodes) {
            setPawn(target, n, Orientation.NORTH);
            assertTrue(sb.canAttack(shooter, target, true));
            assertTrue(shooter.isClearAttack());
        }
    }

    @Test public void test_2() {
        Node []nodes = {
              sb.get(6, 5)
             ,sb.get(5, 4)
             ,sb.get(5, 3)
             ,sb.get(6, 6)
             ,sb.get(5, 5)
             ,sb.get(4, 4)
             ,sb.get(4, 3)
             ,sb.get(4, 2)
             ,sb.get(7, 7)
             ,sb.get(6, 7)
             ,sb.get(5, 6)
             ,sb.get(4, 5)
             ,sb.get(3, 4)
             ,sb.get(3, 3)
             ,sb.get(3, 2)
             ,sb.get(3, 1)
             ,sb.get(4, 1)
        };

        for (Node n : nodes) {
            setPawn(target, n, Orientation.NORTH);
            assertFalse(sb.canAttack(shooter, target, true));
            assertFalse(shooter.isClearAttack());
        }
    }

    @Test public void test_3() {
        Vector<Pawn> targets = new Vector<Pawn>(5);
        sb.possibleTargetsFrom(shooter, targets);

        setPawn(target, sb.get(7, 6), Orientation.NORTH);
        sb.possibleTargetsFrom(shooter, targets);

        setPawn(target, sb.get(5, 2), Orientation.NORTH);
        sb.possibleTargetsFrom(shooter, targets);
    }
}
