package ch.asynk.tankontank;

import java.util.List;

import org.junit.Test;
import org.junit.Before;

import ch.asynk.tankontank.engine.SearchBoard;

import static org.junit.Assert.assertTrue;

public class BoardUtils
{
    private SearchBoard sb;

    @Before
    public void initialize()
    {
        sb = new SearchBoard(null, 10, 9);
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
