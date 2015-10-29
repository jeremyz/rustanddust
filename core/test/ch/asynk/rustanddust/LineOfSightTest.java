package ch.asynk.rustanddust;

import java.util.List;

import org.junit.Test;
import org.junit.Before;

import ch.asynk.rustanddust.engine.SearchBoard.Node;

import static org.junit.Assert.assertTrue;

public class LineOfSightTest
{
    private Helpers.FakeBoard fakeBoard;
    private Helpers.FakeSearchBoard sb;

    @Before
    public void initialize()
    {
        int cols = 10;
        int rows = 9;
        fakeBoard = new Helpers.FakeBoard(cols, rows, 0);
        sb = new Helpers.FakeSearchBoard(fakeBoard, cols, rows);
    }

    private void block(int i, int j, boolean block)
    {
        ((Helpers.FakeTile) fakeBoard.getTile(i, j)).setBlockLineOfSight(block);
    }

    private void checkNode(List<Node> l, int i, int col, int row)
    {
        Node n = l.get(i);
        assertTrue(n.col == col);
        assertTrue(n.row == row);
    }

    private List<Node> lineOfSight(int x0, int y0, int x1, int y1)
    {
        return sb.lineOfSight(x0, y0, x1, y1, true);
    }

    // from bottom left
    @Test public void test_1() {
        List<Node> s = lineOfSight(0, 0, 2, 1);
        assertTrue(s.size() == 4);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 0);
        checkNode(s, 2, 1, 1);
        checkNode(s, 3, 2, 1);
    }

    @Test public void test_2() {
        List<Node> s = lineOfSight(0, 0, 5, 1);
        assertTrue(s.size() == 6);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 0);
        checkNode(s, 2, 2, 0);
        checkNode(s, 3, 3, 1);
        checkNode(s, 4, 4, 1);
        checkNode(s, 5, 5, 1);
    }

    @Test public void test_3() {
        List<Node> s = lineOfSight(0, 0, 8, 1);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 0);
        checkNode(s, 2, 2, 0);
        checkNode(s, 3, 3, 0);
        checkNode(s, 4, 4, 1);
        checkNode(s, 5, 4, 0);
        checkNode(s, 6, 5, 1);
        checkNode(s, 7, 6, 1);
        checkNode(s, 8, 7, 1);
        checkNode(s, 9, 8, 1);
    }

    @Test public void test_4() {
        List<Node> s = lineOfSight(0, 0, 1, 2);
        assertTrue(s.size() == 3);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 1, 2);
    }

    @Test public void test_5() {
        List<Node> s = lineOfSight(0, 0, 4, 2);
        assertTrue(s.size() == 7);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 0);
        checkNode(s, 2, 1, 1);
        checkNode(s, 3, 2, 1);
        checkNode(s, 4, 3, 1);
        checkNode(s, 5, 3, 2);
        checkNode(s, 6, 4, 2);
    }

    @Test public void test_6() {
        List<Node> s = lineOfSight(0, 0, 7, 2);
        assertTrue(s.size() == 8);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 0);
        checkNode(s, 2, 2, 1);
        checkNode(s, 3, 3, 1);
        checkNode(s, 4, 4, 1);
        checkNode(s, 5, 5, 1);
        checkNode(s, 6, 6, 2);
        checkNode(s, 7, 7, 2);
    }

    @Test public void test_7() {
        List<Node> s = lineOfSight(0, 0, 10, 2);
        assertTrue(s.size() == 11);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 0);
        checkNode(s, 2, 2, 0);
        checkNode(s, 3, 3, 1);
        checkNode(s, 4, 4, 1);
        checkNode(s, 5, 5, 1);
        checkNode(s, 6, 6, 1);
        checkNode(s, 7, 7, 1);
        checkNode(s, 8, 8, 2);
        checkNode(s, 9, 9, 2);
        checkNode(s, 10, 10, 2);
    }

    @Test public void test_8() {
        List<Node> s = lineOfSight(0, 0, 6, 3);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 0);
        checkNode(s, 2, 1, 1);
        checkNode(s, 3, 2, 1);
        checkNode(s, 4, 3, 1);
        checkNode(s, 5, 3, 2);
        checkNode(s, 6, 4, 2);
        checkNode(s, 7, 5, 2);
        checkNode(s, 8, 5, 3);
        checkNode(s, 9, 6, 3);
    }

    @Test public void test_9() {
        List<Node> s = lineOfSight(0, 0, 2, 4);
        assertTrue(s.size() == 5);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 1, 2);
        checkNode(s, 3, 2, 3);
        checkNode(s, 4, 2, 4);
    }

    @Test public void test_10() {
        List<Node> s = lineOfSight(0, 0, 5, 4);
        assertTrue(s.size() == 6);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 2, 2);
        checkNode(s, 3, 3, 2);
        checkNode(s, 4, 4, 3);
        checkNode(s, 5, 5, 4);
    }

    @Test public void test_11() {
        List<Node> s = lineOfSight(0, 0, 8, 4);
        assertTrue(s.size() == 13);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 0);
        checkNode(s, 2, 1, 1);
        checkNode(s, 3, 2, 1);
        checkNode(s, 4, 3, 1);
        checkNode(s, 5, 3, 2);
        checkNode(s, 6, 4, 2);
        checkNode(s, 7, 5, 2);
        checkNode(s, 8, 5, 3);
        checkNode(s, 9, 6, 3);
        checkNode(s, 10, 7, 3);
        checkNode(s, 11, 7, 4);
        checkNode(s, 12, 8, 4);
    }

    @Test public void test_12() {
        List<Node> s = lineOfSight(0, 0, 11, 4);
        assertTrue(s.size() == 12);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 0);
        checkNode(s, 2, 2, 1);
        checkNode(s, 3, 3, 1);
        checkNode(s, 4, 4, 1);
        checkNode(s, 5, 5, 2);
        checkNode(s, 6, 6, 2);
        checkNode(s, 7, 7, 3);
        checkNode(s, 8, 8, 3);
        checkNode(s, 9, 9, 3);
        checkNode(s, 10, 10, 4);
        checkNode(s, 11, 11, 4);
    }

    @Test public void test_13() {
        List<Node> s = lineOfSight(0, 0, 4, 5);
        assertTrue(s.size() == 6);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 2, 2);
        checkNode(s, 3, 2, 3);
        checkNode(s, 4, 3, 4);
        checkNode(s, 5, 4, 5);
    }

    @Test public void test_14() {
        List<Node> s = lineOfSight(0, 0, 7, 5);
        assertTrue(s.size() == 8);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 2, 1);
        checkNode(s, 3, 3, 2);
        checkNode(s, 4, 4, 3);
        checkNode(s, 5, 5, 4);
        checkNode(s, 6, 6, 4);
        checkNode(s, 7, 7, 5);
    }

    @Test public void test_15() {
        List<Node> s = lineOfSight(0, 0, 10, 5);
        assertTrue(s.size() == 16);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 0);
        checkNode(s, 2, 1, 1);
        checkNode(s, 3, 2, 1);
        checkNode(s, 4, 3, 1);
        checkNode(s, 5, 3, 2);
        checkNode(s, 6, 4, 2);
        checkNode(s, 7, 5, 2);
        checkNode(s, 8, 5, 3);
        checkNode(s, 9, 6, 3);
        checkNode(s, 10, 7, 3);
        checkNode(s, 11, 7, 4);
        checkNode(s, 12, 8, 4);
        checkNode(s, 13, 9, 4);
        checkNode(s, 14, 9, 5);
        checkNode(s, 15, 10, 5);
    }

    @Test public void test_16() {
        List<Node> s = lineOfSight(0, 0, 3, 6);
        assertTrue(s.size() == 7);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 1, 2);
        checkNode(s, 3, 2, 3);
        checkNode(s, 4, 2, 4);
        checkNode(s, 5, 3, 5);
        checkNode(s, 6, 3, 6);
    }

    @Test public void test_17() {
        List<Node> s = lineOfSight(0, 0, 12, 6);
        assertTrue(s.size() == 19);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 0);
        checkNode(s, 2, 1, 1);
        checkNode(s, 3, 2, 1);
        checkNode(s, 4, 3, 1);
        checkNode(s, 5, 3, 2);
        checkNode(s, 6, 4, 2);
        checkNode(s, 7, 5, 2);
        checkNode(s, 8, 5, 3);
        checkNode(s, 9, 6, 3);
        checkNode(s, 10, 7, 3);
        checkNode(s, 11, 7, 4);
        checkNode(s, 12, 8, 4);
        checkNode(s, 13, 9, 4);
        checkNode(s, 14, 9, 5);
        checkNode(s, 15, 10, 5);
        checkNode(s, 16, 11, 5);
        checkNode(s, 17, 11, 6);
        checkNode(s, 18, 12, 6);
    }

    @Test public void test_18() {
        List<Node> s = lineOfSight(0, 0, 5, 7);
        assertTrue(s.size() == 8);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 1, 2);
        checkNode(s, 3, 2, 3);
        checkNode(s, 4, 3, 4);
        checkNode(s, 5, 4, 5);
        checkNode(s, 6, 4, 6);
        checkNode(s, 7, 5, 7);
    }

    @Test public void test_19() {
        List<Node> s = lineOfSight(0, 0, 8, 7);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 2, 2);
        checkNode(s, 3, 3, 3);
        checkNode(s, 4, 4, 3);
        checkNode(s, 5, 4, 4);
        checkNode(s, 6, 5, 4);
        checkNode(s, 7, 6, 5);
        checkNode(s, 8, 7, 6);
        checkNode(s, 9, 8, 7);
    }

    @Test public void test_20() {
        List<Node> s = lineOfSight(0, 0, 11, 7);
        assertTrue(s.size() == 12);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 2, 1);
        checkNode(s, 3, 3, 2);
        checkNode(s, 4, 4, 3);
        checkNode(s, 5, 5, 3);
        checkNode(s, 6, 6, 4);
        checkNode(s, 7, 7, 4);
        checkNode(s, 8, 8, 5);
        checkNode(s, 9, 9, 6);
        checkNode(s, 10, 10, 6);
        checkNode(s, 11, 11, 7);
    }

    @Test public void test_21() {
        List<Node> s = lineOfSight(0, 0, 4, 8);
        assertTrue(s.size() == 9);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 1, 2);
        checkNode(s, 3, 2, 3);
        checkNode(s, 4, 2, 4);
        checkNode(s, 5, 3, 5);
        checkNode(s, 6, 3, 6);
        checkNode(s, 7, 4, 7);
        checkNode(s, 8, 4, 8);
    }

    @Test public void test_22() {
        List<Node> s = lineOfSight(0, 0, 7, 8);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 2, 2);
        checkNode(s, 3, 3, 3);
        checkNode(s, 4, 3, 4);
        checkNode(s, 5, 4, 4);
        checkNode(s, 6, 4, 5);
        checkNode(s, 7, 5, 6);
        checkNode(s, 8, 6, 7);
        checkNode(s, 9, 7, 8);
    }

    @Test public void test_23() {
        List<Node> s = lineOfSight(0, 0, 10, 8);
        assertTrue(s.size() == 11);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 2, 2);
        checkNode(s, 3, 3, 2);
        checkNode(s, 4, 4, 3);
        checkNode(s, 5, 5, 4);
        checkNode(s, 6, 6, 5);
        checkNode(s, 7, 7, 6);
        checkNode(s, 8, 8, 6);
        checkNode(s, 9, 9, 7);
        checkNode(s, 10, 10, 8);
    }

    @Test public void test_24() {
        List<Node> s = lineOfSight(0, 0, 13, 8);
        assertTrue(s.size() == 14);
        checkNode(s, 0, 0, 0);
        checkNode(s, 1, 1, 1);
        checkNode(s, 2, 2, 1);
        checkNode(s, 3, 3, 2);
        checkNode(s, 4, 4, 2);
        checkNode(s, 5, 5, 3);
        checkNode(s, 6, 6, 4);
        checkNode(s, 7, 7, 4);
        checkNode(s, 8, 8, 5);
        checkNode(s, 9, 9, 6);
        checkNode(s, 10, 10, 6);
        checkNode(s, 11, 11, 7);
        checkNode(s, 12, 12, 7);
        checkNode(s, 13, 13, 8);
    }

    // from top right
    @Test public void test_25() {
        List<Node> s = lineOfSight(13, 8, 11, 7);
        assertTrue(s.size() == 4);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 8);
        checkNode(s, 2, 12, 7);
        checkNode(s, 3, 11, 7);
    }

    @Test public void test_26() {
        List<Node> s = lineOfSight(13, 8, 8, 7);
        assertTrue(s.size() == 6);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 8);
        checkNode(s, 2, 11, 8);
        checkNode(s, 3, 10, 7);
        checkNode(s, 4, 9, 7);
        checkNode(s, 5, 8, 7);
    }

    @Test public void test_27() {
        List<Node> s = lineOfSight(13, 8, 5, 7);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 8);
        checkNode(s, 2, 11, 8);
        checkNode(s, 3, 10, 8);
        checkNode(s, 4, 9, 7);
        checkNode(s, 5, 9, 8);
        checkNode(s, 6, 8, 7);
        checkNode(s, 7, 7, 7);
        checkNode(s, 8, 6, 7);
        checkNode(s, 9, 5, 7);
    }

    @Test public void test_28() {
        List<Node> s = lineOfSight(13, 8, 12, 6);
        assertTrue(s.size() == 3);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 12, 6);
    }

    @Test public void test_29() {
        List<Node> s = lineOfSight(13, 8, 9, 6);
        assertTrue(s.size() == 7);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 8);
        checkNode(s, 2, 12, 7);
        checkNode(s, 3, 11, 7);
        checkNode(s, 4, 10, 7);
        checkNode(s, 5, 10, 6);
        checkNode(s, 6, 9, 6);
    }

    @Test public void test_30() {
        List<Node> s = lineOfSight(13, 8, 6, 6);
        assertTrue(s.size() == 8);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 8);
        checkNode(s, 2, 11, 7);
        checkNode(s, 3, 10, 7);
        checkNode(s, 4, 9, 7);
        checkNode(s, 5, 8, 7);
        checkNode(s, 6, 7, 6);
        checkNode(s, 7, 6, 6);
    }

    @Test public void test_31() {
        List<Node> s = lineOfSight(13, 8, 3, 6);
        assertTrue(s.size() == 11);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 8);
        checkNode(s, 2, 11, 8);
        checkNode(s, 3, 10, 7);
        checkNode(s, 4, 9, 7);
        checkNode(s, 5, 8, 7);
        checkNode(s, 6, 7, 7);
        checkNode(s, 7, 6, 7);
        checkNode(s, 8, 5, 6);
        checkNode(s, 9, 4, 6);
        checkNode(s, 10, 3, 6);
    }

    @Test public void test_32() {
        List<Node> s = lineOfSight(13, 8, 7, 5);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 8);
        checkNode(s, 2, 12, 7);
        checkNode(s, 3, 11, 7);
        checkNode(s, 4, 10, 7);
        checkNode(s, 5, 10, 6);
        checkNode(s, 6, 9, 6);
        checkNode(s, 7, 8, 6);
        checkNode(s, 8, 8, 5);
        checkNode(s, 9, 7, 5);
    }

    @Test public void test_33() {
        List<Node> s = lineOfSight(13, 8, 11, 4);
        assertTrue(s.size() == 5);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 12, 6);
        checkNode(s, 3, 11, 5);
        checkNode(s, 4, 11, 4);
    }

    @Test public void test_34() {
        List<Node> s = lineOfSight(13, 8, 8, 4);
        assertTrue(s.size() == 6);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 11, 6);
        checkNode(s, 3, 10, 6);
        checkNode(s, 4, 9, 5);
        checkNode(s, 5, 8, 4);
    }

    @Test public void test_35() {
        List<Node> s = lineOfSight(13, 8, 5, 4);
        assertTrue(s.size() == 13);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 8);
        checkNode(s, 2, 12, 7);
        checkNode(s, 3, 11, 7);
        checkNode(s, 4, 10, 7);
        checkNode(s, 5, 10, 6);
        checkNode(s, 6, 9, 6);
        checkNode(s, 7, 8, 6);
        checkNode(s, 8, 8, 5);
        checkNode(s, 9, 7, 5);
        checkNode(s, 10, 6, 5);
        checkNode(s, 11, 6, 4);
        checkNode(s, 12, 5, 4);
    }

    @Test public void test_36() {
        List<Node> s = lineOfSight(13, 8, 2, 4);
        assertTrue(s.size() == 12);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 8);
        checkNode(s, 2, 11, 7);
        checkNode(s, 3, 10, 7);
        checkNode(s, 4, 9, 7);
        checkNode(s, 5, 8, 6);
        checkNode(s, 6, 7, 6);
        checkNode(s, 7, 6, 5);
        checkNode(s, 8, 5, 5);
        checkNode(s, 9, 4, 5);
        checkNode(s, 10, 3, 4);
        checkNode(s, 11, 2, 4);
    }

    @Test public void test_37() {
        List<Node> s = lineOfSight(13, 8, 9, 3);
        assertTrue(s.size() == 6);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 11, 6);
        checkNode(s, 3, 11, 5);
        checkNode(s, 4, 10, 4);
        checkNode(s, 5, 9, 3);
    }

    @Test public void test_38() {
        List<Node> s = lineOfSight(13, 8, 6, 3);
        assertTrue(s.size() == 8);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 11, 7);
        checkNode(s, 3, 10, 6);
        checkNode(s, 4, 9, 5);
        checkNode(s, 5, 8, 4);
        checkNode(s, 6, 7, 4);
        checkNode(s, 7, 6, 3);
    }

    @Test public void test_39() {
        List<Node> s = lineOfSight(13, 8, 3, 3);
        assertTrue(s.size() == 16);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 8);
        checkNode(s, 2, 12, 7);
        checkNode(s, 3, 11, 7);
        checkNode(s, 4, 10, 7);
        checkNode(s, 5, 10, 6);
        checkNode(s, 6, 9, 6);
        checkNode(s, 7, 8, 6);
        checkNode(s, 8, 8, 5);
        checkNode(s, 9, 7, 5);
        checkNode(s, 10, 6, 5);
        checkNode(s, 11, 6, 4);
        checkNode(s, 12, 5, 4);
        checkNode(s, 13, 4, 4);
        checkNode(s, 14, 4, 3);
        checkNode(s, 15, 3, 3);
    }

    @Test public void test_40() {
        List<Node> s = lineOfSight(13, 8, 10, 2);
        assertTrue(s.size() == 7);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 12, 6);
        checkNode(s, 3, 11, 5);
        checkNode(s, 4, 11, 4);
        checkNode(s, 5, 10, 3);
        checkNode(s, 6, 10, 2);
    }

    @Test public void test_41() {
        List<Node> s = lineOfSight(13, 8, 1, 2);
        assertTrue(s.size() == 19);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 8);
        checkNode(s, 2, 12, 7);
        checkNode(s, 3, 11, 7);
        checkNode(s, 4, 10, 7);
        checkNode(s, 5, 10, 6);
        checkNode(s, 6, 9, 6);
        checkNode(s, 7, 8, 6);
        checkNode(s, 8, 8, 5);
        checkNode(s, 9, 7, 5);
        checkNode(s, 10, 6, 5);
        checkNode(s, 11, 6, 4);
        checkNode(s, 12, 5, 4);
        checkNode(s, 13, 4, 4);
        checkNode(s, 14, 4, 3);
        checkNode(s, 15, 3, 3);
        checkNode(s, 16, 2, 3);
        checkNode(s, 17, 2, 2);
        checkNode(s, 18, 1, 2);
    }

    @Test public void test_42() {
        List<Node> s = lineOfSight(13, 8, 8, 1);
        assertTrue(s.size() == 8);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 12, 6);
        checkNode(s, 3, 11, 5);
        checkNode(s, 4, 10, 4);
        checkNode(s, 5, 9, 3);
        checkNode(s, 6, 9, 2);
        checkNode(s, 7, 8, 1);
    }

    @Test public void test_43() {
        List<Node> s = lineOfSight(13, 8, 5, 1);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 11, 6);
        checkNode(s, 3, 10, 5);
        checkNode(s, 4, 9, 5);
        checkNode(s, 5, 9, 4);
        checkNode(s, 6, 8, 4);
        checkNode(s, 7, 7, 3);
        checkNode(s, 8, 6, 2);
        checkNode(s, 9, 5, 1);
    }

    @Test public void test_44() {
        List<Node> s = lineOfSight(13, 8, 2, 1);
        assertTrue(s.size() == 12);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 11, 7);
        checkNode(s, 3, 10, 6);
        checkNode(s, 4, 9, 5);
        checkNode(s, 5, 8, 5);
        checkNode(s, 6, 7, 4);
        checkNode(s, 7, 6, 4);
        checkNode(s, 8, 5, 3);
        checkNode(s, 9, 4, 2);
        checkNode(s, 10, 3, 2);
        checkNode(s, 11, 2, 1);
    }

    @Test public void test_45() {
        List<Node> s = lineOfSight(13, 8, 9, 0);
        assertTrue(s.size() == 9);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 12, 6);
        checkNode(s, 3, 11, 5);
        checkNode(s, 4, 11, 4);
        checkNode(s, 5, 10, 3);
        checkNode(s, 6, 10, 2);
        checkNode(s, 7, 9, 1);
        checkNode(s, 8, 9, 0);
    }

    @Test public void test_46() {
        List<Node> s = lineOfSight(13, 8, 6, 0);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 11, 6);
        checkNode(s, 3, 10, 5);
        checkNode(s, 4, 10, 4);
        checkNode(s, 5, 9, 4);
        checkNode(s, 6, 9, 3);
        checkNode(s, 7, 8, 2);
        checkNode(s, 8, 7, 1);
        checkNode(s, 9, 6, 0);
    }

    @Test public void test_47() {
        List<Node> s = lineOfSight(13, 8, 3, 0);
        assertTrue(s.size() == 11);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 11, 6);
        checkNode(s, 3, 10, 6);
        checkNode(s, 4, 9, 5);
        checkNode(s, 5, 8, 4);
        checkNode(s, 6, 7, 3);
        checkNode(s, 7, 6, 2);
        checkNode(s, 8, 5, 2);
        checkNode(s, 9, 4, 1);
        checkNode(s, 10, 3, 0);
    }

    @Test public void test_48() {
        List<Node> s = lineOfSight(13, 8, 0, 0);
        assertTrue(s.size() == 14);
        checkNode(s, 0, 13, 8);
        checkNode(s, 1, 12, 7);
        checkNode(s, 2, 11, 7);
        checkNode(s, 3, 10, 6);
        checkNode(s, 4, 9, 6);
        checkNode(s, 5, 8, 5);
        checkNode(s, 6, 7, 4);
        checkNode(s, 7, 6, 4);
        checkNode(s, 8, 5, 3);
        checkNode(s, 9, 4, 2);
        checkNode(s, 10, 3, 2);
        checkNode(s, 11, 2, 1);
        checkNode(s, 12, 1, 1);
        checkNode(s, 13, 0, 0);
    }

    // from top left
    @Test public void test_49() {
        List<Node> s = lineOfSight(4, 8, 5, 7);
        assertTrue(s.size() == 4);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 5, 8);
        checkNode(s, 2, 4, 7);
        checkNode(s, 3, 5, 7);
    }

    @Test public void test_50() {
        List<Node> s = lineOfSight(4, 8, 8, 7);
        assertTrue(s.size() == 6);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 5, 8);
        checkNode(s, 2, 6, 8);
        checkNode(s, 3, 6, 7);
        checkNode(s, 4, 7, 7);
        checkNode(s, 5, 8, 7);
    }

    @Test public void test_51() {
        List<Node> s = lineOfSight(4, 8, 11, 7);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 5, 8);
        checkNode(s, 2, 6, 8);
        checkNode(s, 3, 7, 8);
        checkNode(s, 4, 7, 7);
        checkNode(s, 5, 8, 8);
        checkNode(s, 6, 8, 7);
        checkNode(s, 7, 9, 7);
        checkNode(s, 8, 10, 7);
        checkNode(s, 9, 11, 7);
    }

    @Test public void test_52() {
        List<Node> s = lineOfSight(4, 8, 3, 6);
        assertTrue(s.size() == 3);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 3, 6);
    }

    @Test public void test_53() {
        List<Node> s = lineOfSight(4, 8, 6, 6);
        assertTrue(s.size() == 7);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 5, 8);
        checkNode(s, 2, 4, 7);
        checkNode(s, 3, 5, 7);
        checkNode(s, 4, 6, 7);
        checkNode(s, 5, 5, 6);
        checkNode(s, 6, 6, 6);
    }

    @Test public void test_54() {
        List<Node> s = lineOfSight(4, 8, 9, 6);
        assertTrue(s.size() == 8);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 5, 8);
        checkNode(s, 2, 5, 7);
        checkNode(s, 3, 6, 7);
        checkNode(s, 4, 7, 7);
        checkNode(s, 5, 8, 7);
        checkNode(s, 6, 8, 6);
        checkNode(s, 7, 9, 6);
    }

    @Test public void test_55() {
        List<Node> s = lineOfSight(4, 8, 12, 6);
        assertTrue(s.size() == 11);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 5, 8);
        checkNode(s, 2, 6, 8);
        checkNode(s, 3, 6, 7);
        checkNode(s, 4, 7, 7);
        checkNode(s, 5, 8, 7);
        checkNode(s, 6, 9, 7);
        checkNode(s, 7, 10, 7);
        checkNode(s, 8, 10, 6);
        checkNode(s, 9, 11, 6);
        checkNode(s, 10, 12, 6);
    }

    @Test public void test_56() {
        List<Node> s = lineOfSight(4, 8, 7, 5);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 5, 8);
        checkNode(s, 2, 4, 7);
        checkNode(s, 3, 5, 7);
        checkNode(s, 4, 6, 7);
        checkNode(s, 5, 5, 6);
        checkNode(s, 6, 6, 6);
        checkNode(s, 7, 7, 6);
        checkNode(s, 8, 6, 5);
        checkNode(s, 9, 7, 5);
    }

    @Test public void test_57() {
        List<Node> s = lineOfSight(4, 8, 2, 4);
        assertTrue(s.size() == 5);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 3, 6);
        checkNode(s, 3, 3, 5);
        checkNode(s, 4, 2, 4);
    }

    @Test public void test_58() {
        List<Node> s = lineOfSight(4, 8, 5, 4);
        assertTrue(s.size() == 6);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 4, 6);
        checkNode(s, 3, 5, 6);
        checkNode(s, 4, 5, 5);
        checkNode(s, 5, 5, 4);
    }

    @Test public void test_59() {
        List<Node> s = lineOfSight(4, 8, 8, 4);
        assertTrue(s.size() == 13);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 5, 8);
        checkNode(s, 2, 4, 7);
        checkNode(s, 3, 5, 7);
        checkNode(s, 4, 6, 7);
        checkNode(s, 5, 5, 6);
        checkNode(s, 6, 6, 6);
        checkNode(s, 7, 7, 6);
        checkNode(s, 8, 6, 5);
        checkNode(s, 9, 7, 5);
        checkNode(s, 10, 8, 5);
        checkNode(s, 11, 7, 4);
        checkNode(s, 12, 8, 4);
    }

    @Test public void test_60() {
        List<Node> s = lineOfSight(4, 8, 11, 4);
        assertTrue(s.size() == 12);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 5, 8);
        checkNode(s, 2, 5, 7);
        checkNode(s, 3, 6, 7);
        checkNode(s, 4, 7, 7);
        checkNode(s, 5, 7, 6);
        checkNode(s, 6, 8, 6);
        checkNode(s, 7, 8, 5);
        checkNode(s, 8, 9, 5);
        checkNode(s, 9, 10, 5);
        checkNode(s, 10, 10, 4);
        checkNode(s, 11, 11, 4);
    }

    @Test public void test_61() {
        List<Node> s = lineOfSight(4, 8, 3, 3);
        assertTrue(s.size() == 6);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 4, 6);
        checkNode(s, 3, 3, 5);
        checkNode(s, 4, 3, 4);
        checkNode(s, 5, 3, 3);
    }

    @Test public void test_62() {
        List<Node> s = lineOfSight(4, 8, 6, 3);
        assertTrue(s.size() == 8);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 5, 7);
        checkNode(s, 3, 5, 6);
        checkNode(s, 4, 5, 5);
        checkNode(s, 5, 5, 4);
        checkNode(s, 6, 6, 4);
        checkNode(s, 7, 6, 3);
    }

    @Test public void test_63() {
        List<Node> s = lineOfSight(4, 8, 9, 3);
        assertTrue(s.size() == 16);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 5, 8);
        checkNode(s, 2, 4, 7);
        checkNode(s, 3, 5, 7);
        checkNode(s, 4, 6, 7);
        checkNode(s, 5, 5, 6);
        checkNode(s, 6, 6, 6);
        checkNode(s, 7, 7, 6);
        checkNode(s, 8, 6, 5);
        checkNode(s, 9, 7, 5);
        checkNode(s, 10, 8, 5);
        checkNode(s, 11, 7, 4);
        checkNode(s, 12, 8, 4);
        checkNode(s, 13, 9, 4);
        checkNode(s, 14, 8, 3);
        checkNode(s, 15, 9, 3);
    }

    @Test public void test_64() {
        List<Node> s = lineOfSight(4, 8, 1, 2);
        assertTrue(s.size() == 7);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 3, 6);
        checkNode(s, 3, 3, 5);
        checkNode(s, 4, 2, 4);
        checkNode(s, 5, 2, 3);
        checkNode(s, 6, 1, 2);
    }

    @Test public void test_65() {
        List<Node> s = lineOfSight(4, 8, 10, 2);
        assertTrue(s.size() == 19);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 5, 8);
        checkNode(s, 2, 4, 7);
        checkNode(s, 3, 5, 7);
        checkNode(s, 4, 6, 7);
        checkNode(s, 5, 5, 6);
        checkNode(s, 6, 6, 6);
        checkNode(s, 7, 7, 6);
        checkNode(s, 8, 6, 5);
        checkNode(s, 9, 7, 5);
        checkNode(s, 10, 8, 5);
        checkNode(s, 11, 7, 4);
        checkNode(s, 12, 8, 4);
        checkNode(s, 13, 9, 4);
        checkNode(s, 14, 8, 3);
        checkNode(s, 15, 9, 3);
        checkNode(s, 16, 10, 3);
        checkNode(s, 17, 9, 2);
        checkNode(s, 18, 10, 2);
    }

    @Test public void test_66() {
        List<Node> s = lineOfSight(4, 8, 2, 1);
        assertTrue(s.size() == 8);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 3, 6);
        checkNode(s, 3, 3, 5);
        checkNode(s, 4, 3, 4);
        checkNode(s, 5, 3, 3);
        checkNode(s, 6, 2, 2);
        checkNode(s, 7, 2, 1);
    }

    @Test public void test_67() {
        List<Node> s = lineOfSight(4, 8, 5, 1);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 4, 6);
        checkNode(s, 3, 4, 5);
        checkNode(s, 4, 5, 5);
        checkNode(s, 5, 4, 4);
        checkNode(s, 6, 5, 4);
        checkNode(s, 7, 5, 3);
        checkNode(s, 8, 5, 2);
        checkNode(s, 9, 5, 1);
    }

    @Test public void test_68() {
        List<Node> s = lineOfSight(4, 8, 8, 1);
        assertTrue(s.size() == 12);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 5, 7);
        checkNode(s, 3, 5, 6);
        checkNode(s, 4, 5, 5);
        checkNode(s, 5, 6, 5);
        checkNode(s, 6, 6, 4);
        checkNode(s, 7, 7, 4);
        checkNode(s, 8, 7, 3);
        checkNode(s, 9, 7, 2);
        checkNode(s, 10, 8, 2);
        checkNode(s, 11, 8, 1);
    }

    @Test public void test_69() {
        List<Node> s = lineOfSight(4, 8, 0, 0);
        assertTrue(s.size() == 9);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 3, 6);
        checkNode(s, 3, 3, 5);
        checkNode(s, 4, 2, 4);
        checkNode(s, 5, 2, 3);
        checkNode(s, 6, 1, 2);
        checkNode(s, 7, 1, 1);
        checkNode(s, 8, 0, 0);
    }

    @Test public void test_70() {
        List<Node> s = lineOfSight(4, 8, 3, 0);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 4, 6);
        checkNode(s, 3, 4, 5);
        checkNode(s, 4, 3, 4);
        checkNode(s, 5, 4, 4);
        checkNode(s, 6, 3, 3);
        checkNode(s, 7, 3, 2);
        checkNode(s, 8, 3, 1);
        checkNode(s, 9, 3, 0);
    }

    @Test public void test_71() {
        List<Node> s = lineOfSight(4, 8, 6, 0);
        assertTrue(s.size() == 11);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 4, 6);
        checkNode(s, 3, 5, 6);
        checkNode(s, 4, 5, 5);
        checkNode(s, 5, 5, 4);
        checkNode(s, 6, 5, 3);
        checkNode(s, 7, 5, 2);
        checkNode(s, 8, 6, 2);
        checkNode(s, 9, 6, 1);
        checkNode(s, 10, 6, 0);
    }

    @Test public void test_72() {
        List<Node> s = lineOfSight(4, 8, 9, 0);
        assertTrue(s.size() == 14);
        checkNode(s, 0, 4, 8);
        checkNode(s, 1, 4, 7);
        checkNode(s, 2, 5, 7);
        checkNode(s, 3, 5, 6);
        checkNode(s, 4, 6, 6);
        checkNode(s, 5, 6, 5);
        checkNode(s, 6, 6, 4);
        checkNode(s, 7, 7, 4);
        checkNode(s, 8, 7, 3);
        checkNode(s, 9, 7, 2);
        checkNode(s, 10, 8, 2);
        checkNode(s, 11, 8, 1);
        checkNode(s, 12, 9, 1);
        checkNode(s, 13, 9, 0);
    }

    // from bottom right
    @Test public void test_73() {
        List<Node> s = lineOfSight(9, 0, 8, 1);
        assertTrue(s.size() == 4);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 8, 0);
        checkNode(s, 2, 9, 1);
        checkNode(s, 3, 8, 1);
    }

    @Test public void test_74() {
        List<Node> s = lineOfSight(9, 0, 5, 1);
        assertTrue(s.size() == 6);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 8, 0);
        checkNode(s, 2, 7, 0);
        checkNode(s, 3, 7, 1);
        checkNode(s, 4, 6, 1);
        checkNode(s, 5, 5, 1);
    }

    @Test public void test_75() {
        List<Node> s = lineOfSight(9, 0, 2, 1);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 8, 0);
        checkNode(s, 2, 7, 0);
        checkNode(s, 3, 6, 0);
        checkNode(s, 4, 6, 1);
        checkNode(s, 5, 5, 0);
        checkNode(s, 6, 5, 1);
        checkNode(s, 7, 4, 1);
        checkNode(s, 8, 3, 1);
        checkNode(s, 9, 2, 1);
    }

    @Test public void test_76() {
        List<Node> s = lineOfSight(9, 0, 10, 2);
        assertTrue(s.size() == 3);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 10, 2);
    }

    @Test public void test_77() {
        List<Node> s = lineOfSight(9, 0, 7, 2);
        assertTrue(s.size() == 7);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 8, 0);
        checkNode(s, 2, 9, 1);
        checkNode(s, 3, 8, 1);
        checkNode(s, 4, 7, 1);
        checkNode(s, 5, 8, 2);
        checkNode(s, 6, 7, 2);
    }

    @Test public void test_78() {
        List<Node> s = lineOfSight(9, 0, 4, 2);
        assertTrue(s.size() == 8);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 8, 0);
        checkNode(s, 2, 8, 1);
        checkNode(s, 3, 7, 1);
        checkNode(s, 4, 6, 1);
        checkNode(s, 5, 5, 1);
        checkNode(s, 6, 5, 2);
        checkNode(s, 7, 4, 2);
    }

    @Test public void test_79() {
        List<Node> s = lineOfSight(9, 0, 1, 2);
        assertTrue(s.size() == 11);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 8, 0);
        checkNode(s, 2, 7, 0);
        checkNode(s, 3, 7, 1);
        checkNode(s, 4, 6, 1);
        checkNode(s, 5, 5, 1);
        checkNode(s, 6, 4, 1);
        checkNode(s, 7, 3, 1);
        checkNode(s, 8, 3, 2);
        checkNode(s, 9, 2, 2);
        checkNode(s, 10, 1, 2);
    }

    @Test public void test_80() {
        List<Node> s = lineOfSight(9, 0, 6, 3);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 8, 0);
        checkNode(s, 2, 9, 1);
        checkNode(s, 3, 8, 1);
        checkNode(s, 4, 7, 1);
        checkNode(s, 5, 8, 2);
        checkNode(s, 6, 7, 2);
        checkNode(s, 7, 6, 2);
        checkNode(s, 8, 7, 3);
        checkNode(s, 9, 6, 3);
    }

    @Test public void test_81() {
        List<Node> s = lineOfSight(9, 0, 11, 4);
        assertTrue(s.size() == 5);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 10, 2);
        checkNode(s, 3, 10, 3);
        checkNode(s, 4, 11, 4);
    }

    @Test public void test_82() {
        List<Node> s = lineOfSight(9, 0, 8, 4);
        assertTrue(s.size() == 6);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 9, 2);
        checkNode(s, 3, 8, 2);
        checkNode(s, 4, 8, 3);
        checkNode(s, 5, 8, 4);
    }

    @Test public void test_83() {
        List<Node> s = lineOfSight(9, 0, 5, 4);
        assertTrue(s.size() == 13);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 8, 0);
        checkNode(s, 2, 9, 1);
        checkNode(s, 3, 8, 1);
        checkNode(s, 4, 7, 1);
        checkNode(s, 5, 8, 2);
        checkNode(s, 6, 7, 2);
        checkNode(s, 7, 6, 2);
        checkNode(s, 8, 7, 3);
        checkNode(s, 9, 6, 3);
        checkNode(s, 10, 5, 3);
        checkNode(s, 11, 6, 4);
        checkNode(s, 12, 5, 4);
    }

    @Test public void test_84() {
        List<Node> s = lineOfSight(9, 0, 2, 4);
        assertTrue(s.size() == 12);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 8, 0);
        checkNode(s, 2, 8, 1);
        checkNode(s, 3, 7, 1);
        checkNode(s, 4, 6, 1);
        checkNode(s, 5, 6, 2);
        checkNode(s, 6, 5, 2);
        checkNode(s, 7, 5, 3);
        checkNode(s, 8, 4, 3);
        checkNode(s, 9, 3, 3);
        checkNode(s, 10, 3, 4);
        checkNode(s, 11, 2, 4);
    }

    @Test public void test_85() {
        List<Node> s = lineOfSight(9, 0, 10, 5);
        assertTrue(s.size() == 6);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 9, 2);
        checkNode(s, 3, 10, 3);
        checkNode(s, 4, 10, 4);
        checkNode(s, 5, 10, 5);
    }

    @Test public void test_86() {
        List<Node> s = lineOfSight(9, 0, 7, 5);
        assertTrue(s.size() == 8);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 8, 1);
        checkNode(s, 3, 8, 2);
        checkNode(s, 4, 8, 3);
        checkNode(s, 5, 8, 4);
        checkNode(s, 6, 7, 4);
        checkNode(s, 7, 7, 5);
    }

    @Test public void test_87() {
        List<Node> s = lineOfSight(9, 0, 4, 5);
        assertTrue(s.size() == 16);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 8, 0);
        checkNode(s, 2, 9, 1);
        checkNode(s, 3, 8, 1);
        checkNode(s, 4, 7, 1);
        checkNode(s, 5, 8, 2);
        checkNode(s, 6, 7, 2);
        checkNode(s, 7, 6, 2);
        checkNode(s, 8, 7, 3);
        checkNode(s, 9, 6, 3);
        checkNode(s, 10, 5, 3);
        checkNode(s, 11, 6, 4);
        checkNode(s, 12, 5, 4);
        checkNode(s, 13, 4, 4);
        checkNode(s, 14, 5, 5);
        checkNode(s, 15, 4, 5);
    }

    @Test public void test_88() {
        List<Node> s = lineOfSight(9, 0, 12, 6);
        assertTrue(s.size() == 7);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 10, 2);
        checkNode(s, 3, 10, 3);
        checkNode(s, 4, 11, 4);
        checkNode(s, 5, 11, 5);
        checkNode(s, 6, 12, 6);
    }

    @Test public void test_89() {
        List<Node> s = lineOfSight(9, 0, 3, 6);
        assertTrue(s.size() == 19);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 8, 0);
        checkNode(s, 2, 9, 1);
        checkNode(s, 3, 8, 1);
        checkNode(s, 4, 7, 1);
        checkNode(s, 5, 8, 2);
        checkNode(s, 6, 7, 2);
        checkNode(s, 7, 6, 2);
        checkNode(s, 8, 7, 3);
        checkNode(s, 9, 6, 3);
        checkNode(s, 10, 5, 3);
        checkNode(s, 11, 6, 4);
        checkNode(s, 12, 5, 4);
        checkNode(s, 13, 4, 4);
        checkNode(s, 14, 5, 5);
        checkNode(s, 15, 4, 5);
        checkNode(s, 16, 3, 5);
        checkNode(s, 17, 4, 6);
        checkNode(s, 18, 3, 6);
    }

    @Test public void test_90() {
        List<Node> s = lineOfSight(9, 0, 11, 7);
        assertTrue(s.size() == 8);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 10, 2);
        checkNode(s, 3, 10, 3);
        checkNode(s, 4, 10, 4);
        checkNode(s, 5, 10, 5);
        checkNode(s, 6, 11, 6);
        checkNode(s, 7, 11, 7);
    }

    @Test public void test_91() {
        List<Node> s = lineOfSight(9, 0, 8, 7);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 9, 2);
        checkNode(s, 3, 9, 3);
        checkNode(s, 4, 8, 3);
        checkNode(s, 5, 9, 4);
        checkNode(s, 6, 8, 4);
        checkNode(s, 7, 8, 5);
        checkNode(s, 8, 8, 6);
        checkNode(s, 9, 8, 7);
    }

    @Test public void test_92() {
        List<Node> s = lineOfSight(9, 0, 5, 7);
        assertTrue(s.size() == 12);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 8, 1);
        checkNode(s, 3, 8, 2);
        checkNode(s, 4, 8, 3);
        checkNode(s, 5, 7, 3);
        checkNode(s, 6, 7, 4);
        checkNode(s, 7, 6, 4);
        checkNode(s, 8, 6, 5);
        checkNode(s, 9, 6, 6);
        checkNode(s, 10, 5, 6);
        checkNode(s, 11, 5, 7);
    }

    @Test public void test_93() {
        List<Node> s = lineOfSight(9, 0, 13, 8);
        assertTrue(s.size() == 9);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 10, 2);
        checkNode(s, 3, 10, 3);
        checkNode(s, 4, 11, 4);
        checkNode(s, 5, 11, 5);
        checkNode(s, 6, 12, 6);
        checkNode(s, 7, 12, 7);
        checkNode(s, 8, 13, 8);
    }

    @Test public void test_94() {
        List<Node> s = lineOfSight(9, 0, 10, 8);
        assertTrue(s.size() == 10);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 9, 2);
        checkNode(s, 3, 9, 3);
        checkNode(s, 4, 10, 4);
        checkNode(s, 5, 9, 4);
        checkNode(s, 6, 10, 5);
        checkNode(s, 7, 10, 6);
        checkNode(s, 8, 10, 7);
        checkNode(s, 9, 10, 8);
    }

    @Test public void test_95() {
        List<Node> s = lineOfSight(9, 0, 7, 8);
        assertTrue(s.size() == 11);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 9, 2);
        checkNode(s, 3, 8, 2);
        checkNode(s, 4, 8, 3);
        checkNode(s, 5, 8, 4);
        checkNode(s, 6, 8, 5);
        checkNode(s, 7, 8, 6);
        checkNode(s, 8, 7, 6);
        checkNode(s, 9, 7, 7);
        checkNode(s, 10, 7, 8);
    }

    @Test public void test_96() {
        List<Node> s = lineOfSight(9, 0, 4, 8);
        assertTrue(s.size() == 14);
        checkNode(s, 0, 9, 0);
        checkNode(s, 1, 9, 1);
        checkNode(s, 2, 8, 1);
        checkNode(s, 3, 8, 2);
        checkNode(s, 4, 7, 2);
        checkNode(s, 5, 7, 3);
        checkNode(s, 6, 7, 4);
        checkNode(s, 7, 6, 4);
        checkNode(s, 8, 6, 5);
        checkNode(s, 9, 6, 6);
        checkNode(s, 10, 5, 6);
        checkNode(s, 11, 5, 7);
        checkNode(s, 12, 4, 7);
        checkNode(s, 13, 4, 8);
    }

    private void testBlocking(Node from, Node to, Node blockA, Node blockB)
    {
        List<Node> s = null;

        // clear
        s = lineOfSight(from.col, from.row, to.col, to.row);
        assertTrue(s.size() == 4);
        checkNode(s, 0, from.col, from.row);
        checkNode(s, 3, to.col, to.row);

        // block A
        block(blockA.col, blockA.row, true);
        s = lineOfSight(from.col, from.row, to.col, to.row);
        assertTrue(s.size() == 4);
        checkNode(s, 0, from.col, from.row);
        checkNode(s, 3, to.col, to.row);
        block(blockA.col, blockA.row, false);

        // block B
        block(blockB.col, blockB.row, true);
        s = lineOfSight(from.col, from.row, to.col, to.row);
        assertTrue(s.size() == 4);
        checkNode(s, 0, from.col, from.row);
        checkNode(s, 3, to.col, to.row);
        block(blockB.col, blockB.row, false);

        // block A and B
        block(blockA.col, blockA.row, true);
        block(blockB.col, blockB.row, true);
        s = lineOfSight(from.col, from.row, to.col, to.row);
        assertTrue(s.size() == 3);
        checkNode(s, 0, from.col, from.row);
        block(blockA.col, blockA.row, false);
        block(blockB.col, blockB.row, false);

        // reverse
        // clear
        s = lineOfSight(to.col, to.row, from.col, from.row);
        assertTrue(s.size() == 4);
        checkNode(s, 0, to.col, to.row);
        checkNode(s, 3, from.col, from.row);

        // block A
        block(blockA.col, blockA.row, true);
        s = lineOfSight(to.col, to.row, from.col, from.row);
        assertTrue(s.size() == 4);
        checkNode(s, 0, to.col, to.row);
        checkNode(s, 3, from.col, from.row);
        block(blockA.col, blockA.row, false);

        // block B
        block(blockB.col, blockB.row, true);
        s = lineOfSight(to.col, to.row, from.col, from.row);
        assertTrue(s.size() == 4);
        checkNode(s, 0, to.col, to.row);
        checkNode(s, 3, from.col, from.row);
        block(blockB.col, blockB.row, false);

        // block A and B
        block(blockA.col, blockA.row, true);
        block(blockB.col, blockB.row, true);
        s = lineOfSight(to.col, to.row, from.col, from.row);
        assertTrue(s.size() == 3);
        checkNode(s, 0, to.col, to.row);
        block(blockA.col, blockA.row, false);
        block(blockB.col, blockB.row, false);

    }

    @Test public void test_97() {
        testBlocking(sb.get(6, 4), sb.get(7, 6), sb.get(6, 5), sb.get(7, 5));
    }

    @Test public void test_98() {
        testBlocking(sb.get(6, 4), sb.get(8, 5), sb.get(7, 5), sb.get(7, 4));
    }

    @Test public void test_99() {
        testBlocking(sb.get(6, 4), sb.get(7, 3), sb.get(7, 4), sb.get(6, 3));
    }

    @Test public void test_100() {
        testBlocking(sb.get(6, 4), sb.get(5, 2), sb.get(5, 3), sb.get(6, 3));
    }

    @Test public void test_101() {
        testBlocking(sb.get(6, 4), sb.get(4, 3), sb.get(5, 4), sb.get(5, 3));
    }

    @Test public void test_102() {
        testBlocking(sb.get(6, 4), sb.get(5, 5), sb.get(5, 4), sb.get(6, 5));
    }
}
