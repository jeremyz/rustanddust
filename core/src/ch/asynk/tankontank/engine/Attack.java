package ch.asynk.tankontank.engine;

public class Attack
{
    Pawn assaulter;
    Pawn target;
    int distance;
    boolean isClear;
    boolean isFlank;

    public String toString()
    {
        return "engage : " + assaulter + " " + target + " distance:" + distance + " clear:" + isClear + " flank:" + isFlank;
    }

    public void reset()
    {
        assaulter = null;
        target = null;
        distance = 0;;
        isClear = false;
        isFlank = false;
    }
}
