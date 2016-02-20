package ch.asynk.rustanddust.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.rustanddust.RustAndDust;
import ch.asynk.rustanddust.ui.Widget;

public class MenuCtrl implements Disposable
{
    private MainMenu mainMenu;
    private PlayMenu playMenu;
    private OptionsMenu optionsMenu;
    private TutorialsMenu tutorialsMenu;
    private Widget currentMenu;

    public boolean visible;

    public MenuCtrl(final RustAndDust game)
    {
        this.mainMenu = new MainMenu(game);
        this.playMenu = new PlayMenu(game);
        this.optionsMenu = new OptionsMenu(game);
        this.tutorialsMenu = new TutorialsMenu(game);

        this.currentMenu = mainMenu;
        this.currentMenu.visible = true;
        this.visible = true;
    }

    public boolean hit(float x, float y)
    {
        boolean ret = false;

        if (currentMenu.hit(x, y)) {
            currentMenu.visible = false;
            if (currentMenu == mainMenu) {
                showNextMenu();
            } else if (currentMenu == playMenu) {
                currentMenu = mainMenu;
                if (playMenu.launch)
                    ret = true;
            } else {
                currentMenu = mainMenu;
            }
            currentMenu.visible = true;
        }

        return ret;
    }

    private void showNextMenu()
    {
        switch(mainMenu.getMenu()) {
            case PLAY: currentMenu = playMenu; break;
            case OPTIONS: currentMenu = optionsMenu; break;
            case TUTORIALS: currentMenu = tutorialsMenu; break;
        }
    }

    public void draw(SpriteBatch batch)
    {
        if (visible)
            currentMenu.draw(batch);
    }

    public void setPosition()
    {
        mainMenu.setPosition();
        playMenu.setPosition();
        optionsMenu.setPosition();
        tutorialsMenu.setPosition();
    }

    @Override
    public void dispose()
    {
        mainMenu.dispose();
        playMenu.dispose();
        optionsMenu.dispose();
        tutorialsMenu.dispose();
    }
}
