package ch.asynk.rustanddust.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.engine.util.Collection;

public class List extends Widget
{
    public interface ListElement
    {
        public String s();
    }

    private BitmapFont font;
    private GlyphLayout layout;
    private float itemHeight;
    private Integer idx;
    private Bg selected;
    private Collection<ListElement> items;

    public List(RustAndDust game)
    {
        this(game, 15f);
    }

    public List(RustAndDust game, float padding)
    {
        super();
        this.font = game.font;
        this.padding = padding;
        this.layout = new GlyphLayout();
        this.idx = null;
        this.selected = new Bg(game.getUiRegion(game.UI_SELECT));
    }

    @Override
    public boolean hit(float x, float y)
    {
        float t = (getTop() - (int) padding);
        idx = (int) Math.floor((t - y) / itemHeight);
        if ((idx >= 0) && (idx < items.size()))
            selected.setPosition(rect.x, (t - itemHeight - (idx * itemHeight)), rect.width, (itemHeight + padding));
        return true;
    }

    public void setItems(int clipN, Collection<ListElement> items)
    {
        this.items = items;
        compute();
    }

    private void compute()
    {
        float w = 0f;
        for (ListElement e: items) {
            layout.setText(font, e.s());
            if (layout.width > w) w = layout.width;
        }
        itemHeight = (layout.height + padding);

        rect.width = w + (2 * padding);
        rect.height = padding + (itemHeight * items.size());
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void draw(Batch batch)
    {
        if (!visible) return;

        if (idx != null)
            selected.draw(batch);
        float x = rect.x + padding;
        float y = rect.y + rect.height - padding;
        for (ListElement e : items) {
            font.draw(batch, e.s(), x, y);
            y -= itemHeight;
        }
    }
}
