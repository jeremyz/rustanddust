package ch.asynk.tankontank;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;
import org.junit.Before;

import com.badlogic.gdx.math.GridPoint2;

import ch.asynk.tankontank.engine.SearchBoard;

import static org.junit.Assert.assertTrue;

public class BoardUtils
{
    private SearchBoard sb;
    private Helpers.FakeBoard b;

    @Before
    public void initialize()
    {
        int cols = 10;
        int rows = 9;
        int mvt = 3;
        b = new Helpers.FakeBoard(cols, rows, 3);
        sb = new SearchBoard(b, cols, rows);
    }

    @Test
    public void testPaths()
    {
        List<ArrayList<SearchBoard.Node>> paths = b.buildPossiblePaths(2, 2, 4, 3);

        assertTrue(paths.size() == 8);

        for(ArrayList<SearchBoard.Node> path : paths) {

            assertTrue((path.size() == 3) || (path.size() == 4));
            SearchBoard.Node n = path.get(0);
            assertTrue(n.col == 2);
            assertTrue(n.row == 2);
            n = path.get(path.size() - 1);
            assertTrue(n.col == 4);
            assertTrue(n.row == 3);

            int i = 1;
            if (path.size() == 3) {
                n = path.get(i);
                assertTrue(n.col == 3);
                assertTrue((n.row == 3) || (n.row == 2));
            } else {
                n = path.get(i);
                if (n.col == 2) {
                    i += 1;
                    if (n.row == 1) {
                        n = path.get(i);
                        assert(n.col == 3);
                        assert(n.row == 2);
                    } else {
                        assert(n.row == 3);
                        n = path.get(i);
                        assert(n.col == 3);
                        assert(n.row == 3);
                    }
                } else if (n.col == 3) {
                    i += 1;
                    if (n.row == 2) {
                        n = path.get(i);
                        if (n.col == 3)
                            assert(n.row == 3);
                        else {
                            assert(n.col == 4);
                            assert(n.row == 2);
                        }
                    } else {
                        assert(n.row == 3);
                        n = path.get(i);
                        if (n.col == 3)
                            assert(n.row == 2);
                        else {
                            assert(n.col == 4);
                            assert(n.row == 4);
                        }
                    }
                } else {
                    assertTrue(false);
                }
            }
        }
    }

    @Test
    public void testPathSet()
    {
        HashSet<GridPoint2> points = new HashSet<GridPoint2>();

        int n = b.buildPossiblePaths(2, 2, 3, 3, points);
        assertTrue(n == 1);
        assertTrue(points.size() == 0);

        n = b.buildPossiblePaths(2, 2, 4, 3, points);
        assertTrue(n == 8);
        assertTrue(points.size() == 6);

        n = b.togglePoint(3, 3);
        assertTrue(n == 1);

        n = b.buildPossiblePaths(2, 2, 5, 3, points);
        assertTrue(n == 3);

        n = b.togglePoint(3, 3);
        assertTrue(n == 1);

        n = b.togglePoint(3, 3);
        assertTrue(n == 3);

        n = b.togglePoint(3, 2);
        assertTrue(n == 2);

        n = b.togglePoint(4, 2);
        assertTrue(n == 1);

    }

    @Test
    public void testDistance()
    {
        assertTrue(sb.distance(6, 4, 6, 4) == 0);
        assertTrue(sb.distance(6, 4, 5, 4) == 1);
        assertTrue(sb.distance(6, 4, 6, 5) == 1);
        assertTrue(sb.distance(6, 4, 7, 5) == 1);
        assertTrue(sb.distance(6, 4, 7, 4) == 1);
        assertTrue(sb.distance(6, 4, 6, 3) == 1);
        assertTrue(sb.distance(6, 4, 5, 3) == 1);
        assertTrue(sb.distance(6, 4, 4, 4) == 2);
        assertTrue(sb.distance(6, 4, 5, 5) == 2);
        assertTrue(sb.distance(6, 4, 6, 6) == 2);
        assertTrue(sb.distance(6, 4, 7, 6) == 2);
        assertTrue(sb.distance(6, 4, 8, 6) == 2);
        assertTrue(sb.distance(6, 4, 8, 5) == 2);
        assertTrue(sb.distance(6, 4, 8, 4) == 2);
        assertTrue(sb.distance(6, 4, 7, 3) == 2);
        assertTrue(sb.distance(6, 4, 6, 2) == 2);
        assertTrue(sb.distance(6, 4, 5, 2) == 2);
        assertTrue(sb.distance(6, 4, 4, 2) == 2);
        assertTrue(sb.distance(6, 4, 4, 3) == 2);

        assertTrue(sb.distance(6, 4, 9, 7) == 3);
        assertTrue(sb.distance(6, 4, 10, 8) == 4);
        assertTrue(sb.distance(6, 4, 6, 1) == 3);
        assertTrue(sb.distance(6, 4, 9, 6) == 3);
        assertTrue(sb.distance(6, 4, 9, 5) == 3);
        assertTrue(sb.distance(6, 4, 10, 6) == 4);
        assertTrue(sb.distance(6, 4, 3, 1) == 3);
        assertTrue(sb.distance(6, 4, 2, 0) == 4);

        assertTrue(sb.distance(6, 4, 9, 4) == 3);
        assertTrue(sb.distance(6, 4, 8, 4) == 2);
        assertTrue(sb.distance(6, 4, 9, 5) == 3);
        assertTrue(sb.distance(6, 4, 10, 5) == 4);
        assertTrue(sb.distance(6, 4, 10, 4) == 4);
        assertTrue(sb.distance(6, 4, 9, 3) == 4);
        assertTrue(sb.distance(6, 4, 8, 3) == 3);

        assertTrue(sb.distance(6, 4, 8, 2) == 4);
        assertTrue(sb.distance(6, 4, 7, 2) == 3);
        assertTrue(sb.distance(6, 4, 8, 3) == 3);
        assertTrue(sb.distance(6, 4, 9, 3) == 4);
        assertTrue(sb.distance(6, 4, 9, 2) == 5);
        assertTrue(sb.distance(6, 4, 8, 1) == 5);
        assertTrue(sb.distance(6, 4, 7, 1) == 4);

        assertTrue(sb.distance(6, 4, 2, 2) == 4);
        assertTrue(sb.distance(6, 4, 1, 2) == 5);
        assertTrue(sb.distance(6, 4, 2, 3) == 4);
        assertTrue(sb.distance(6, 4, 3, 3) == 3);
        assertTrue(sb.distance(6, 4, 3, 2) == 3);
        assertTrue(sb.distance(6, 4, 2, 1) == 4);
        assertTrue(sb.distance(6, 4, 1, 1) == 5);

        assertTrue(sb.distance(6, 4, 5, 7) == 4);
        assertTrue(sb.distance(6, 4, 4, 7) == 5);
        assertTrue(sb.distance(6, 4, 5, 8) == 5);
        assertTrue(sb.distance(6, 4, 6, 8) == 4);
        assertTrue(sb.distance(6, 4, 6, 7) == 3);
        assertTrue(sb.distance(6, 4, 5, 6) == 3);
        assertTrue(sb.distance(6, 4, 4, 6) == 4);

        assertTrue(sb.distance(6, 4, 8, 8) == 4);
        assertTrue(sb.distance(6, 4, 7, 8) == 4);
        assertTrue(sb.distance(6, 4, 8, 9) == 5);
        assertTrue(sb.distance(6, 4, 9, 9) == 5);
        assertTrue(sb.distance(6, 4, 9, 8) == 4);
        assertTrue(sb.distance(6, 4, 8, 7) == 3);
        assertTrue(sb.distance(6, 4, 7, 7) == 3);
    }

}
