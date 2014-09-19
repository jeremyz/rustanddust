package ch.asynk.tankontank.engine;

import java.util.LinkedList;
import java.util.Vector;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

import ch.asynk.tankontank.engine.gfx.Node;
import ch.asynk.tankontank.engine.gfx.Animation;

public class Layer
{
    public boolean visible;
    private final Viewport viewport;
    private final Batch batch;
    private final Vector<Animation> animations = new Vector<Animation>(5);
    private final Vector<Animation> nextAnimations = new Vector<Animation>(2);
    private final LinkedList<Node> nodes = new LinkedList<Node>();

    public Layer(Viewport viewport)
    {
        this.visible = true;
        this.viewport = viewport;
        this.batch = new SpriteBatch();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    public void resize(int width, int height)
    {
        viewport.update(width, height);
    }

    public void addNode(Node node)
    {
        node.setLayer(this);
        nodes.add(node);
    }

    public boolean removeNode(Node node)
    {
        node.setLayer(null);
        return nodes.remove(node);
    }

    public void goOnTop(Node node)
    {
        nodes.remove(node);
        node.setLayer(this);
        nodes.add(node);
    }

    public void addAnimation(Animation animation)
    {
        nextAnimations.add(animation);
    }

    public void act()
    {
        act(Math.min(Gdx.graphics.getDeltaTime(), (1 / 30f)));
    }

    public void act(float delta)
    {
        Iterator<Animation> iter = animations.iterator();
        while (iter.hasNext()) {
            Animation a = iter.next();
            Node n = a.getNode();
            if (n != null)
                goOnTop(n);
            if (a.act(delta)) iter.remove();
        }

        for (int i = 0, n = nodes.size(); i < n; i++)
            nodes.get(i).act(delta);

        for (int i = 0, n = nextAnimations.size(); i < n; i++)
            animations.add(nextAnimations.get(i));
        nextAnimations.clear();
    }

    public void draw()
    {
        Camera camera = viewport.getCamera();
        camera.update();
        if (!visible) return;
        Batch batch = this.batch;
        if (batch != null) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            for (int i = 0, n = nodes.size(); i < n; i++)
                nodes.get(i).draw(batch, 1);
            batch.end();
        }
    }

    public void drawDebug(ShapeRenderer debugShapes)
    {
        debugShapes.setProjectionMatrix(viewport.getCamera().combined);
        debugShapes.begin();
        for (int i = 0, n = nodes.size(); i < n; i++)
            nodes.get(i).drawDebug(debugShapes);
        debugShapes.end();
    }

    public void clear()
    {
        for (int i = 0, n = nodes.size(); i < n; i++)
            nodes.get(i).clear();
        nodes.clear();

        for (int i = 0, n = animations.size(); i < n; i++)
            animations.get(i).free();
        animations.clear();

        for (int i = 0, n = nextAnimations.size(); i < n; i++)
            nextAnimations.get(i).free();
        nextAnimations.clear();

        batch.dispose();
    }
}
