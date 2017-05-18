package ch.asynk.rustanddust.game.states;

import ch.asynk.rustanddust.engine.Orientation;
import ch.asynk.rustanddust.game.Hex;
import ch.asynk.rustanddust.game.Unit;
import ch.asynk.rustanddust.game.Order;
import ch.asynk.rustanddust.game.Ctrl.MsgType;
import ch.asynk.rustanddust.game.hud.ActionButtons.Buttons;

public class StateMove extends StateCommon
{
    enum State
    {
        SHOW,
        PATH,
        ROTATE,
        EXIT
    }

    private State state;
    private boolean enter;
    private boolean hqMode;
    private boolean notFirst;

    @Override
    public void enterFrom(StateType prevState)
    {
        state = State.SHOW;

        enter = false;
        hqMode = false;
        notFirst = false;

        map.pathsClear();
        if (prevState == StateType.SELECT) {
            map.hexSelect(selectedHex);
            map.pathsInit(selectedUnit());
            activate(selectedUnit());
            if (to == null) {
                hqMode = true;
                map.movesShow();
            } else {
                collectPaths(to);
            }
        } else if (prevState == StateType.REINFORCEMENT) {
            enter = true;
            map.hexSelect(selectedHex);
            map.pathsInit(selectedUnit());
            if (selectedUnit().getMovementPoints() > 0) {
                map.movesCollect(selectedUnit());
                map.movesShow();
            } else {
                to = selectedHex;
                showRotation(to);
                collectPaths(to);
            }
        } else {
            notFirst = hqMode = true;
            selectNextUnit();
        }

        if (hqMode)
            map.unitsActivableShow();
        activeUnit().hideActiveable();
        int n = (notFirst ? Buttons.DONE.b : 0);
        n |= (enter ? Buttons.ABORT.b : 0);
        ctrl.hud.actionButtons.show(n);
    }

    @Override
    public boolean processMsg(MsgType msg, Object data)
    {
        switch(msg)
        {
            case OK:
                if (state == State.EXIT) {
                    exit();
                    return true;
                }
                if (hqMode) {
                    endHqMode();
                    return true;
                }
                break;
            case CANCEL:
                if (enter || (state == State.PATH) || (state == State.ROTATE)) {
                    abortMove();
                    return true;
                }
                if (state == State.EXIT) {
                    abortExit();
                    return true;
                }
                break;
        }

        return false;
    }

    @Override
    public void touch(Hex hex)
    {
        if (state == State.ROTATE) {
            if (hex == to)
                abortMove();
            else {
                Orientation o = Orientation.fromAdj(to, hex);
                if (o != Orientation.KEEP)
                    move(o);
                else if (hex == activeUnit().getHex())
                    abortMove();
            }
            return;
         }

        if (hex == activeUnit().getHex()) {
            if (to != null)
                map.pathHide(to);
            map.pathsHide();
            map.pathsClear();
            map.pathsInit(activeUnit());
            collectPaths(hex);
            return;
        }

        int s = map.pathsSize();

        Unit unit = hex.getUnit();

        if (map.unitsActivableContains(unit)) {
            if (unit != activeUnit())
                selectUnit(unit);
        } else if ((s == 0) && map.movesContains(hex)) {
            collectPaths(hex);
        } else if ((s > 1) && hex == to) {
            s = map.pathsChooseBest();
            if (s == 1)
                showRotation(to);
        } else if (map.pathsContains(hex)) {
            togglePoint(hex, s);
        }
    }

    private void selectNextUnit()
    {
        if (selectedUnit().canMove())
            selectUnit(selectedUnit());
        else
            selectUnit(map.getFirstActivable());
    }

    private void selectUnit(Unit unit)
    {
        state = State.SHOW;
        if (activeUnit() != null ) {
            map.hexUnselect(activeUnit().getHex());
            if (activeUnit().canMove())
                activeUnit().showActiveable();
        }
        to = null;
        activate(unit);
        activeUnit().hideActiveable();
        map.hexSelect(activeUnit().getHex());
        map.pathsClear();
        map.pathsInit(activeUnit());
        map.movesHide();
        map.movesCollect(activeUnit());
        map.movesShow();
        ctrl.hud.notify(activeUnit().toString());
    }

    private void collectPaths(Hex hex)
    {
        state = State.PATH;
        to = hex;
        map.movesHide();
        map.hexMoveShow(to);
        if (!checkExit(to))
            completePath();
        ctrl.hud.actionButtons.show(Buttons.ABORT.b);
    }

    private void completePath()
    {
        int s = map.pathsBuild(to);
        if (cfg.autoPath && (s > 1))
            s = map.pathsChooseBest();
        map.pathsShow();
        if (s == 1)
            showRotation(to);
    }

    private void togglePoint(Hex hex, int s)
    {
        map.pathsHide();
        s = map.pathsToggleHex(hex);
        map.pathsShow();
        if (s == 1)
            showRotation(to);
    }

    private boolean checkExit(Hex hex)
    {
        if (enter)
            return false;
        if ((activeUnit().exitZone == null) || !activeUnit().exitZone.contains(hex))
            return false;

        int s = map.pathsBuild(to);

        if (!map.pathsCanExit(activeUnit().exitZone.orientation))
            return false;

        if (map.pathsChooseExit(activeUnit().exitZone.orientation) > 1)
            throw new RuntimeException(String.format("pathsChooseExit() -> %d", map.pathsSize()));
        map.pathShow(hex);

        state = State.EXIT;
        ctrl.hud.askExitBoard();
        return true;
    }

    private void showRotation(Hex hex)
    {
        state = State.ROTATE;
        map.movesHide();
        map.pathsHide();
        map.pathShow(hex);
        map.hexDirectionsShow(hex);
        ctrl.hud.actionButtons.show(Buttons.ABORT.b);
    }

    private void move(Orientation o)
    {
        map.pathsSetOrientation(o);
        if (enter)
            completeMove(map.getEnterOrder(selectedUnit(), hqMode));
        else
            completeMove(map.getMoveOrder(selectedUnit(), hqMode));
    }

    private void exit()
    {
        if (map.pathsTo() == null) {
            map.pathsBuild(to);
            if (map.pathsChooseExit(activeUnit().exitZone.orientation) > 1)
                throw new RuntimeException(String.format("pathsChooseExit() -> %d", map.pathsSize()));
        }

        completeMove(map.getExitOrder(selectedUnit(), hqMode));
    }

    private void completeMove(Order order)
    {
        map.pathHide(to);
        map.hexDirectionsHide(to);
        map.hexUnselect(selectedHex);
        if (order.cost == 0)
            ctrl.postOrder(order, StateType.MOVE);
        else
            ctrl.postOrder(order);
    }

    private void endHqMode()
    {
        if (selectedUnit().canMove())
            selectedUnit().setMoved();
        clear();
        ctrl.postOrder(Order.END);
    }

    private void abortMove()
    {
        if (enter) {
            map.revertEnter(activeUnit());
            ctrl.battle.getPlayer().revertUnitEntry(activeUnit());
            ctrl.hud.update();
        }
        clear();
        if (notFirst) {
            // FIXME abortMove : cfg.revertAllMoves
            // if (cfg.revertAllMoves) {
            //     map.revertMoves();
            //     ctrl.postTransitionToAborted();
            // } else {
                selectUnit(activeUnit());
                abortCompleted();
            // }
        }
        else
            ctrl.postActionAborted();
    }

    private void abortExit()
    {
        map.pathHide(to);
        if (to == null) {
            state = State.SHOW;
        } else {
            state = State.PATH;
            map.hexMoveShow(to);
            completePath();
        }
        abortCompleted();
    }

    private void abortCompleted()
    {
        if (hqMode)
            map.unitsActivableShow();
        activeUnit().hideActiveable();
        ctrl.hud.actionButtons.show((notFirst ? Buttons.DONE.b : 0));
    }

    private void clear()
    {
        state = State.SHOW;
        map.movesHide();
        map.pathsHide();
        activeUnit().hideActiveable();
        map.hexUnselect(activeUnit().getHex());
        map.unitsActivableHide();
        if (to != null) {
            map.pathHide(to);
            map.hexDirectionsHide(to);
        }
        map.pathsClear();
    }
}
