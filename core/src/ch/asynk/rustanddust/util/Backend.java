package ch.asynk.rustanddust.util;

import ch.asynk.rustanddust.RustAndDust;

public interface Backend
{
    public void init(RustAndDust game);

    public int getMyId();

    public int getOpponentId();
}
