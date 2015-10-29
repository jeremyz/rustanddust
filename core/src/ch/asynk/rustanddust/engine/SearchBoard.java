package ch.asynk.rustanddust.engine;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Collection;

public class SearchBoard
{
    public class Node
    {
        public int col;
        public int row;
        public int search;
        public int remaining;
        public Node parent;
        public boolean roadMarch;

        public Node(int col, int row)
        {
            this.col = col;
            this.row = row;
        }

        @Override
        public String toString()
        {
            return col + ";" + row;
        }
    }

    private int cols;
    private int rows;
    private Board board;
    private int searchCount;
    private Node nodes[];

    private ArrayDeque<Node> stack;
    private LinkedList<Node> queue;
    private ArrayDeque<Node> roadMarch;
    private List<Node> los;

    public SearchBoard(Board board, int cols, int rows)
    {
        this.cols = cols;
        this.rows = rows;
        this.board = board;
        this.searchCount = 0;

        this.nodes = new Node[cols * rows];
        for (int j = 0; j < rows; j++) {
            int dx = ((j + 1) / 2);
            for (int i = 0; i < cols; i++)
                nodes[i + (j * cols)] = new Node((i + dx), j);
        }

        this.queue = new LinkedList<Node>();
        this.stack = new ArrayDeque<Node>(20);
        this.roadMarch = new ArrayDeque<Node>(5);
        this.los = new ArrayList<Node>(10);
    }

    private boolean inMap(int col, int row)
    {
        if ((row < 0) || (row >= rows))
            return false;

        int colOffset = ((row + 1) / 2);
        if ((col < colOffset) || ((col - colOffset) >= cols))
            return false;

        return true;
    }

    private Tile getTile(Node node)
    {
        return board.getTile(node.col, node.row);
    }

    private Node getNode(Tile tile)
    {
        return getNode(tile.col, tile.row);
    }

    protected Node getNode(int col, int row)
    {
        int colOffset = ((row + 1) / 2);
        if ((col < colOffset) || (row < 0) || (row >= rows) || ((col - colOffset) >= cols))
            return null;

        return nodes[((col - colOffset)) + (row * cols)];
    }

    public int distance(Node from, Node to)
    {
        return board.distance(from.col, from.row, to.col, to.row);
    }

    public void adjacentMoves(Node src, Node a[])
    {
        // move to enter dst by sides[i]
        a[0] = getNode((src.col - 1), src.row);
        a[1] = getNode(src.col, (src.row + 1));
        a[2] = getNode((src.col + 1), (src.row + 1));
        a[3] = getNode((src.col + 1), src.row);
        a[4] = getNode(src.col, (src.row - 1));
        a[5] = getNode((src.col - 1), (src.row - 1));
    }

    public int possibleMovesFrom(Pawn pawn, Collection<Tile> moves)
    {
        moves.clear();
        searchCount += 1;

        Node adjacents[] = new Node[6];

        Node from = getNode(pawn.getTile());
        from.parent = null;
        from.search = searchCount;
        from.remaining = pawn.getMovementPoints();
        from.roadMarch = true;

        if (from.remaining <= 0)
            return moves.size();

        int roadMarchBonus = pawn.getRoadMarchBonus();
        boolean first = true;

        stack.push(from);

        while (stack.size() != 0) {
            Node src = stack.pop();

            if (src.remaining < 0)
                continue;
            if (src.remaining == 0) {
                if (src.roadMarch)
                    roadMarch.push(src);
                continue;
            }

            adjacentMoves(src, adjacents);

            for(int i = 0; i < 6; i++) {
                Node dst = adjacents[i];
                if (dst != null) {

                    Tile t = getTile(dst);
                    int cost = t.costFrom(pawn, board.getSide(i));
                    boolean mayMoveOne = first && t.atLeastOneMove(pawn);
                    int r = src.remaining - cost;
                    boolean roadMarch = (src.roadMarch && t.road(board.getSide(i)));

                    if (dst.search == searchCount) {
                        if ((r >= 0) && ((r > dst.remaining) || (roadMarch && ((r + roadMarchBonus) >= dst.remaining)))) {
                            dst.remaining = r;
                            dst.parent = src;
                            dst.roadMarch = roadMarch;
                            stack.push(dst);
                            moves.add(getTile(dst));
                        }
                    } else {
                        dst.search = searchCount;
                        if ((r >= 0) || mayMoveOne) {
                            dst.parent = src;
                            dst.remaining = r;
                            dst.roadMarch = roadMarch;
                            stack.push(dst);
                            moves.add(getTile(dst));
                        } else {
                            dst.parent = null;
                            dst.remaining = -1;
                        }
                    }
                }
            }
            first = false;
        }

        for (Node n : roadMarch) n.remaining = roadMarchBonus;
        while(roadMarch.size() != 0) {
            Node src = roadMarch.pop();

            adjacentMoves(src, adjacents);

            for(int i = 0; i < 6; i++) {
                Node dst = adjacents[i];
                if (dst != null) {

                    Tile t = getTile(dst);
                    if (!t.road(board.getSide(i)))
                        continue;
                    int cost = t.costFrom(pawn, board.getSide(i));
                    int r = src.remaining - cost;

                    if (dst.search == searchCount) {
                        if ((r >= 0) && (r > dst.remaining)) {
                            dst.remaining = r;
                            dst.parent = src;
                            dst.roadMarch = true;
                            roadMarch.push(dst);
                            moves.add(getTile(dst));
                        }
                    } else {
                        dst.search = searchCount;
                        if (r >= 0) {
                            dst.parent = src;
                            dst.remaining = r;
                            dst.roadMarch = true;
                            roadMarch.push(dst);
                            moves.add(getTile(dst));
                        } else {
                            dst.parent = null;
                            dst.remaining = -1;
                        }
                    }
                }
            }
        }

        return moves.size();
    }

    private void adjacentTargets(Node src, int angle, Node a[])
    {
        // move in allowed directions
        if (Orientation.NORTH.isInSides(angle))
            a[0] = getNode((src.col + 1), src.row);
        else
            a[0] = null;

        if (Orientation.NORTH_EAST.isInSides(angle))
            a[1] = getNode(src.col, (src.row - 1));
        else
            a[1] = null;

        if (Orientation.SOUTH_EAST.isInSides(angle))
            a[2] = getNode((src.col - 1), (src.row - 1));
        else
            a[2] = null;

        if (Orientation.SOUTH.isInSides(angle))
            a[3] = getNode((src.col - 1), src.row);
        else
            a[3] = null;

        if (Orientation.SOUTH_WEST.isInSides(angle))
            a[4] = getNode(src.col, (src.row + 1));
        else
            a[4] = null;

        if (Orientation.NORTH_WEST.isInSides(angle))
            a[5] = getNode((src.col + 1), (src.row + 1));
        else
            a[5] = null;
    }

    public int possibleTargetsFrom(Pawn shooter, Collection<Pawn> targets)
    {
        targets.clear();
        searchCount += 1;

        Node adjacents[] = new Node[6];

        int range = shooter.getEngagementRangeFrom(shooter.getTile());
        int angle = shooter.getAngleOfAttack();
        int extendedAngle = shooter.getOrientation().opposite().allBut();

        Node from = getNode(shooter.getTile());
        from.search = searchCount;
        from.remaining = range;

        if (range <= 0)
            return targets.size();

        queue.add(from);

        boolean first = true;
        while (queue.size() != 0) {
            Node src = queue.remove();

            if (src.remaining <= 0)
                continue;

            if (!first && (((range - src.remaining) % 2) == 0))
                adjacentTargets(src, extendedAngle, adjacents);
            else
                adjacentTargets(src, angle, adjacents);

            first = false;
            int rangeLeft = src.remaining - 1;

            for(int i = 0; i < 6; i++) {
                Node dst = adjacents[i];
                if (dst != null) {
                    if (dst.search == searchCount) {
                        if ((rangeLeft > dst.remaining))
                            dst.remaining = rangeLeft;
                    } else {
                        dst.search = searchCount;
                        dst.remaining = rangeLeft;
                        queue.add(dst);
                        Tile t = getTile(dst);
                        if (hasClearLineOfSight(from, dst, angle)) {
                            Iterator<Pawn> it = t.iterator();
                            while (it.hasNext()) {
                                Pawn target = it.next();
                                if (shooter.canEngage(target))
                                    targets.add(target);
                            }
                        }
                    }
                }
            }
        }

        return targets.size();
    }

    public boolean canAttack(Pawn shooter, Pawn target, boolean clearVisibility)
    {
        Node from = getNode(shooter.getTile());
        Node to = getNode(target.getTile());

        shooter.setAttack(target, distance(from, to));

        if (shooter.attack.distance > shooter.getEngagementRangeFrom(shooter.getTile()))
            return false;

        List<Node> los = lineOfSight(from.col, from.row, to.col, to.row, clearVisibility);
        if (los.get(los.size() -1) != to)
            return false;

        if (!validatePathAngle(shooter.getAngleOfAttack(), los))
            return false;

        shooter.attack.isClear = isClearAttack(getTile(from), los);
        shooter.attack.isFlank = isFlankAttack(target.getFlankSides(), los);

        return true;
    }

    private boolean hasClearLineOfSight(Node from, Node to, int angleOfAttack)
    {
        List<Node> los = lineOfSight(from.col, from.row, to.col, to.row, true);
        Node last = los.get(los.size() -1);
        if ((last.col != to.col) || (last.row != to.row))
            return false;
        return validatePathAngle(angleOfAttack, los);
    }

    private boolean isFlankAttack(int angle, List<Node> los)
    {
        Node from = los.get(los.size() - 2);
        Node to = los.get(los.size() - 1);
        Orientation o = Orientation.fromMove(to.col, to.row, from.col, from.row);

        if (los.size() < 3)
            return o.isInSides(angle);

        Node before = los.get(los.size() - 3);
        if (distance(before, to) > 1)
            return o.isInSides(angle);

        Orientation o2 = Orientation.fromMove(to.col, to.row, before.col, before.row);
        return (o.isInSides(angle) && o2.isInSides(angle));
    }

    private boolean isClearAttack(Tile from, List<Node> los)
    {
        int n = los.size() - 1;
        for (int i = 1; i < n; i++) {
            if (getTile(los.get(i)).blockLineOfSightFrom(from))
                return false;
        }
        return true;
    }

    private boolean validatePathAngle(int angle, List<Node> los)
    {
        int forth = 0;
        Node prev = null;
        for (Node next : los) {
            if (prev != null) {
                Orientation o = Orientation.fromMove(prev.col, prev.row, next.col, next.row);
                if (!o.isInSides(angle)) {
                    forth -= 1;
                    if (forth < 0)
                        return false;
                }
                forth += 1;
            }
            prev = next;
        }

        return true;
    }

    public List<Node> lineOfSight(int x0, int y0, int x1, int y1, boolean clearVisibility)
    {
        los.clear();
        Tile from = board.getTile(x0, y0);

        // orthogonal axis
        int ox0 = x0 - ((y0 +1) / 2);
        int ox1 = x1 - ((y1 +1) / 2);

        int dy = y1 - y0;
        int dx = ox1 - ox0;

        int xs = 1;
        int ys = 1;
        if (dx < 0) xs = -1;
        if (dy < 0) ys = -1;
        boolean sig = !(((dx < 0) && (dy >= 0)) || ((dx >= 0) && (dy < 0)));

        dy = Math.abs(dy);
        dx = Math.abs(2 * dx);
        if ((dy % 2) == 1) {
            if ((y0 % 2) == 0) dx += xs;
            else {
                dx -= xs;
                Math.abs(dx);
            }
        }

        if (dx == 0)
            return verticalLineOfSight(x0, y0, x1, y1, clearVisibility);
        if (dx == (3 * dy))
            return diagonalLineOfSight(x0, y0, x1, y1, clearVisibility);

        int dx3 = 3 * dx;
        int dy3 = 3 * dy;

        int x = x0;
        int y = y0;
        int e = -2 * dx;

        boolean flat = (dx > (3 * dy));
        boolean diag = (dx == (3 * dy));

        los.add(getNode(x, y));
        while((x != x1) || (y != y1)) {
            if (e > 0) {
                e -= (dy3 + dx3);
                y += ys;
                if (!sig)
                    x -= xs;
            } else {
                e += dy3;
                if ((e > -dx) || (!flat && (e == -dx))) {
                    e -= dx3;
                    y += ys;
                    if (sig)
                        x += xs;
                } else if ((e < -dx3) || (diag && (e == -dx3))) {
                    e += dx3;
                    y -= ys;
                    if (!sig)
                        x += xs;
                } else {
                    e += dy3;
                    x += xs;
                }
            }
            los.add(getNode(x, y));
            if(clearVisibility && board.getTile(x, y).blockLineOfSightFrom(from)) return los;
        }

        return los;
    }

    private List<Node> verticalLineOfSight(int x0, int y0, int x1, int y1, boolean clearVisibility)
    {
        Tile from = board.getTile(x0, y0);

        int d = ( (y1 > y0) ? 1 : -1);
        int x = x0;
        int y = y0;

        Tile t = null;
        los.add(getNode(x, y));
        while((x != x1) || (y != y1)) {
            boolean ok = !clearVisibility;

            y += d;
            t = board.getTile(x, y);
            if (!t.isOffMap()) los.add(getNode(x, y));
            if (!ok && !t.blockLineOfSightFrom(from))
                ok = true;

            x += d;
            t = board.getTile(x, y);
            if (!t.isOffMap()) los.add(getNode(x, y));
            if (!ok && !t.blockLineOfSightFrom(from))
                ok = true;

            if (!ok)
                return los;

            y += d;
            t = board.getTile(x, y);
            if (!t.isOffMap()) los.add(getNode(x, y));
        }

        return los;
    }

    private List<Node> diagonalLineOfSight(int x0, int y0, int x1, int y1, boolean clearVisibility)
    {
        Tile from = board.getTile(x0, y0);

        int dy = ( (y1 > y0) ? 1 : -1);
        int dx = ( (x1 > x0) ? 1 : -1);
        boolean sig = !(((dx < 0) && (dy >= 0)) || ((dx >= 0) && (dy < 0)));

        int x = x0;
        int y = y0;

        Tile t = null;
        los.add(getNode(x, y));
        while((x != x1) || (y != y1)) {
            boolean ok = !clearVisibility;

            x += dx;
            t = board.getTile(x, y);
            if (!t.isOffMap()) los.add(getNode(x, y));
            if (!ok && !t.blockLineOfSightFrom(from))
                ok = true;

            y += dy;
            if (!sig)
                x -= dx;
            t = board.getTile(x, y);
            if (!t.isOffMap()) los.add(getNode(x, y));
            if (!ok && !t.blockLineOfSightFrom(from))
                ok = true;

            if (!ok)
                return los;

            x += dx;
            t = board.getTile(x, y);
            if (!t.isOffMap()) los.add(getNode(x, y));
        }

        return los;
    }
}
