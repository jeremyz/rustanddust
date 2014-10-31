package ch.asynk.tankontank.engine;

import java.util.LinkedHashSet;

public class PawnSet extends LinkedHashSet<Pawn> implements Board.PawnCollection
{
    private final Board board;

    public PawnSet(Board board, int n)
    {
        super(n);
        this.board = board;
    }

    public Pawn first()
    {
        if (isEmpty()) return null;
        return iterator().next();
    }

    public void enable(int i, boolean enable)
    {
        for (Pawn pawn : this)
            pawn.enableOverlay(i, enable);
    }
}
