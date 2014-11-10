package ch.asynk.tankontank.game.hud;

import com.badlogic.gdx.Gdx;
import ch.asynk.tankontank.game.Hud;

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

    public Position down()
    {
        Position p = BOTTOM_LEFT;
        switch(this) {
            case TOP_LEFT:
            case MIDDLE_LEFT:
            case BOTTOM_LEFT:
                p = BOTTOM_LEFT;
                break;
            case TOP_CENTER:
            case MIDDLE_CENTER:
            case BOTTOM_CENTER:
                p = BOTTOM_CENTER;
                break;
            case TOP_RIGHT:
            case MIDDLE_RIGHT:
            case BOTTOM_RIGHT:
                p = BOTTOM_RIGHT;
                break;
        }
        return p;
    }

    public Position up()
    {
        Position p = TOP_LEFT;
        switch(this) {
            case TOP_LEFT:
            case MIDDLE_LEFT:
            case BOTTOM_LEFT:
                p = TOP_LEFT;
                break;
            case TOP_CENTER:
            case MIDDLE_CENTER:
            case BOTTOM_CENTER:
                p = TOP_CENTER;
                break;
            case TOP_RIGHT:
            case MIDDLE_RIGHT:
            case BOTTOM_RIGHT:
                p = TOP_RIGHT;
                break;
        }
        return p;
    }

    public boolean isLeft()
    {
        boolean r = false;
        switch(this) {
            case TOP_LEFT:
            case MIDDLE_LEFT:
            case BOTTOM_LEFT:
                r = true;
                break;
            default:
                r = false;
                break;
        }
        return r;
    }

    public boolean isRight()
    {
        boolean r = false;
        switch(this) {
            case TOP_RIGHT:
            case MIDDLE_RIGHT:
            case BOTTOM_RIGHT:
                r = true;
                break;
            default:
                r = false;
                break;
        }
        return r;
    }

    public boolean isCenter()
    {
        boolean r = false;
        switch(this) {
            case TOP_CENTER:
            case MIDDLE_CENTER:
            case BOTTOM_CENTER:
                r = true;
                break;
            default:
                r = false;
                break;
        }
        return r;
    }

    public float getX(float width)
    {
        float x;
        switch(this) {
            case TOP_LEFT:
            case MIDDLE_LEFT:
            case BOTTOM_LEFT:
                x = Hud.OFFSET;
                break;
            case TOP_CENTER:
            case MIDDLE_CENTER:
            case BOTTOM_CENTER:
                x = ((Gdx.graphics.getWidth() - width) / 2);
                break;
            case TOP_RIGHT:
            case MIDDLE_RIGHT:
            case BOTTOM_RIGHT:
                x = (Gdx.graphics.getWidth() - width - Hud.OFFSET);
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
                y = (Gdx.graphics.getHeight() - height - Hud.OFFSET);
                break;
            case MIDDLE_LEFT:
            case MIDDLE_CENTER:
            case MIDDLE_RIGHT:
                y = ((Gdx.graphics.getHeight() - height) / 2);
                break;
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                y = Hud.OFFSET;
                break;
            default:
                y = ((Gdx.graphics.getHeight() - height) / 2);
                break;
        }
        return y;
    }

    public float getX(Widget widget, float width)
    {
        float x;
        switch(this) {
            case TOP_LEFT:
            case MIDDLE_LEFT:
            case BOTTOM_LEFT:
                x = widget.getX();
                break;
            case TOP_CENTER:
            case MIDDLE_CENTER:
            case BOTTOM_CENTER:
                x = (widget.getX() + ((widget.getWidth() - width) / 2));
                break;
            case TOP_RIGHT:
            case MIDDLE_RIGHT:
            case BOTTOM_RIGHT:
                x = (widget.getX() + widget.getWidth() - width);
                break;
            default:
                x = (widget.getX() + ((widget.getWidth() - width) / 2));
                break;
        }
        return x;
    }

    public float getY(Widget widget, float height)
    {
        float y;
        switch(this) {
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
                y = (widget.getY() + widget.getHeight() - height);
                break;
            case MIDDLE_LEFT:
            case MIDDLE_CENTER:
            case MIDDLE_RIGHT:
                y = (widget.getY() + ((widget.getHeight() - height) / 2));
                break;
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                y = widget.getY();
                break;
            default:
                y = (widget.getY() + ((widget.getHeight() - height) / 2));
                break;
        }
        return y;
    }
}
