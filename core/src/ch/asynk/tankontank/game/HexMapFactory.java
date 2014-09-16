package ch.asynk.tankontank.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class HexMapFactory
{
    public enum MapType
    {
        MAP_A,
        MAP_B
    }

    public static HexMap getMap(AssetManager manager, MapType t)
    {
        HexMap m = null;
        switch(t) {
            case MAP_A:
                m = new HexMapImage(11, 9, manager.get("images/map_a.png", Texture.class));
                break;
            case MAP_B:
                m = new HexMapImage(11, 9, manager.get("images/map_b.png", Texture.class));
                break;
        }

        return m;
    }
}
