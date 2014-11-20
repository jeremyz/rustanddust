package ch.asynk.tankontank.game;

import java.util.Collection;
import java.util.LinkedHashSet;

import ch.asynk.tankontank.engine.Tile;

public class HexSet extends LinkedHashSet<Hex>
{
    private final Map map;

    public HexSet(Map map, int n)
    {
        super(n);
        this.map = map;
    }

    public void enable(int i, boolean enable)
    {
        for (Hex hex : this)
            map.enableOverlayOn(hex, i, enable);
    }

    @SuppressWarnings("unchecked")
    public Collection<Tile> asTiles()
    {
        return (Collection) this;
    }
}
