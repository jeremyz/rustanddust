package ch.asynk.rustanddust;

import org.junit.Test;
import org.junit.Before;

import ch.asynk.rustanddust.engine.Tile;
import ch.asynk.rustanddust.engine.Path;

import static org.junit.Assert.assertTrue;

public class BoardUtils
{
    private Helpers.FakeBoard b;
    private Helpers.FakeSearchBoard sb;

    @Before
    public void initialize()
    {
        int cols = 10;
        int rows = 9;
        int mvt = 3;
        b = new Helpers.FakeBoard(cols, rows);
        sb = new Helpers.FakeSearchBoard(b, cols, rows);
    }

    private void assertTile(Tile t, int col, int row)
    {
        assertTrue(t.getCol() == col);
        assertTrue(t.getRow() == row);
    }

    @Test
    public void testPaths()
    {
        int n = b.buildPathBuilder(2, 2, 4, 3);

        assertTrue(b.pathBuilder.distance == 2);
        assertTrue(n == 8);

        for(int p = 0; p < n; p++) {

            Path path = b.pathBuilder.getPath(p);
            int z = path.tiles.size();
            assertTrue(path.cost == (z + 1));

            Tile t;
            int i = 0;
            if (z == 1) {
                t = path.tiles.get(i);
                assertTrue(t.getCol() == 3);
                assertTrue((t.getRow() == 2) || (t.getRow() == 3));;
            } else if (z == 2) {
                t = path.tiles.get(i);
                if (t.getCol() == 2) {
                    i += 1;
                    if (t.getRow() == 1) {
                        t = path.tiles.get(i);
                        assertTile(t, 3, 2);
                    } else {
                        assert(t.getRow()== 3);
                        t = path.tiles.get(i);
                        assertTile(t, 3, 3);
                    }
                } else if (t.getCol() == 3) {
                    i += 1;
                    if (t.getRow() == 2) {
                        t = path.tiles.get(i);
                        if (t.getCol() == 3)
                            assert(t.getRow()== 3);
                        else {
                            assertTile(t, 4, 2);
                        }
                    } else {
                        assert(t.getRow() == 3);
                        t = path.tiles.get(i);
                        if (t.getCol() == 3)
                            assert(t.getRow() == 2);
                        else {
                            assertTile(t, 4, 4);
                        }
                    }
                } else {
                    assertTrue(false);
                }
            } else {
                assertTrue(false);
            }
        }
    }

    @Test
    public void testPathSet()
    {
        int n = b.buildPathBuilder(2, 2, 3, 3);
        assertTrue(n == 18);

        n = b.buildPathBuilder(2, 2, 4, 3);
        assertTrue(n == 8);

        n = b.togglePoint(3, 3, false);
        assertTrue(n == 5);

        n = b.togglePoint(3, 2, false);
        assertTrue(n == 2);

        n = b.togglePoint(3, 3, false);
        assertTrue(n == 5);

        n = b.togglePoint(3, 2, false);
        assertTrue(n == 8);

        n = b.togglePoint(3, 3, true);
        assertTrue(n == 1);

        n = b.buildPathBuilder(2, 2, 5, 3);
        assertTrue(n == 3);

        n = b.togglePoint(3, 3, true);
        assertTrue(n == 1);

        n = b.togglePoint(3, 3, true);
        assertTrue(n == 3);

        n = b.togglePoint(3, 3, false);
        assertTrue(n == 1);

        n = b.togglePoint(3, 3, false);
        assertTrue(n == 3);

        n = b.togglePoint(3, 2, true);
        assertTrue(n == 2);

        n = b.togglePoint(4, 2, true);
        assertTrue(n == 1);

        n = b.togglePoint(4, 2, true);
        assertTrue(n == 2);

        n = b.togglePoint(3, 2, true);
        assertTrue(n == 3);

        n = b.togglePoint(3, 2, false);
        assertTrue(n == 2);

        n = b.togglePoint(4, 2, false);
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
        assertTrue(sb.distance(6, 4, 1, 1) == 5);
        assertTrue(sb.distance(6, 4, 7, 0) == 5);
        assertTrue(sb.distance(6, 4, 9, 8) == 4);
        assertTrue(sb.distance(6, 4, 8, 7) == 3);
        assertTrue(sb.distance(6, 4, 7, 7) == 3);
    }

}
