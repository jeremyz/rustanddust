package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.Gdx;

public enum Position
{
    TOP_LEFT,
    TOP_RIGHT,
    TOP_CENTER,
    MIDDLE_LEFT,
    MIDDLE_RIGHT,
    MIDDLE_CENTER,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    BOTTOM_CENTER;
    private float offset = 40f;

    public float getX(float width)
    {
        float x;
        switch(this) {
            case TOP_LEFT:
            case MIDDLE_LEFT:
            case BOTTOM_LEFT:
                x = offset;
                break;
            case TOP_CENTER:
            case MIDDLE_CENTER:
            case BOTTOM_CENTER:
                x = ((Gdx.graphics.getWidth() - width) / 2);
                break;
            case TOP_RIGHT:
            case MIDDLE_RIGHT:
            case BOTTOM_RIGHT:
                x = (Gdx.graphics.getWidth() - width - offset);
                break;
            default:
                x = ((Gdx.graphics.getWidth() - width) / 2);
                break;
        }
        return x;
    }

    public float getY(float height)
    {
        float y;
        switch(this) {
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
                y = (Gdx.graphics.getHeight() - height - offset);
                break;
            case MIDDLE_LEFT:
            case MIDDLE_CENTER:
            case MIDDLE_RIGHT:
                y = ((Gdx.graphics.getHeight() - height) / 2);
                break;
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                y = offset;
                break;
            default:
                y = ((Gdx.graphics.getHeight() - height) / 2);
                break;
        }
        return y;
    }
}
