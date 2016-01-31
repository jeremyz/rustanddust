package ch.asynk.rustanddust;

public interface Backend
{
    public void init(RustAndDust game);

    public int getMyId();

    public int getOpponentId();
}
