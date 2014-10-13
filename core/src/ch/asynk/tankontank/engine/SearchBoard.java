package ch.asynk.tankontank.engine;

import java.util.List;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.ArrayList;

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
    private Orientation sides[];

    private ArrayDeque<Node> stack;
    private LinkedList<Node> queue;
    private ArrayDeque<Node> roadMarch;

    private List<Node> moves;
    private List<Node> targets;
    private List<Node> los;

    private List<Node> path;
    private List<ArrayList<Node>> possiblePaths;
    private List<Node> possiblePathsFilters;

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

        this.sides = new Orientation[6];
        sides[0] = Orientation.NORTH;
        sides[1] = Orientation.NORTH_EAST;
        sides[2] = Orientation.SOUTH_EAST;
        sides[3] = Orientation.SOUTH;
        sides[4] = Orientation.SOUTH_WEST;
        sides[5] = Orientation.NORTH_WEST;

        this.queue = new LinkedList<Node>();
        this.stack = new ArrayDeque<Node>(20);
        this.roadMarch = new ArrayDeque<Node>(5);

        this.moves = new ArrayList<Node>(40);
        this.targets = new ArrayList<Node>(10);
        this.los = new ArrayList<Node>(10);

        this.path = new ArrayList<Node>(20);
        this.possiblePaths = new LinkedList<ArrayList<Node>>();
        this.possiblePathsFilters = new ArrayList<Node>(5);
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

    private Node getNode(int col, int row)
    {
        int colOffset = ((row + 1) / 2);
        if ((col < colOffset) || (row < 0) || (row >= rows) || ((col - colOffset) >= cols))
            return null;

        return nodes[((col - colOffset)) + (row * cols)];
    }

    public int distance(Node from, Node to)
    {
        return distance(from.col, from.row, to.col, to.row);
    }

    public int distance(int col0, int row0, int col1, int row1)
    {
        int dx = Math.abs(col1 - col0);
        int dy = Math.abs(row1 - row0);
        int dz = Math.abs((col0 - row0) - (col1 - row1));

        if (dx > dy) {
            if (dx > dz)
                return dx;
            else
                return dz;
        } else {
            if (dy > dz)
                return dy;
            else
                return dz;
        }
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

    public List<Node> possibleMovesFrom(Pawn pawn, int col, int row)
    {
        searchCount += 1;
        moves.clear();

        Node adjacents[] = new Node[6];

        Node from = getNode(col, row);
        from.parent = null;
        from.search = searchCount;
        from.remaining = pawn.getMovementPoints();
        from.roadMarch = true;

        if (from.remaining <= 0)
            return moves;

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

                    Tile t = board.getTile(dst.col, dst.row);
                    boolean road = t.road(sides[i]);
                    int cost = t.costFrom(pawn, sides[i], road);
                    boolean mayMoveOne = first && t.atLeastOneMove(pawn);
                    int r = src.remaining - cost;
                    boolean roadMarch = road && src.roadMarch;

                    if (dst.search == searchCount) {
                        if ((r >= 0) && ((r > dst.remaining) || (roadMarch && ((r + roadMarchBonus) >= dst.remaining)))) {
                            dst.remaining = r;
                            dst.parent = src;
                            dst.roadMarch = roadMarch;
                            stack.push(dst);
                            if (!moves.contains(dst)) moves.add(dst);
                        }
                    } else {
                        dst.search = searchCount;
                        if ((r >= 0) || mayMoveOne) {
                            dst.parent = src;
                            dst.remaining = r;
                            dst.roadMarch = roadMarch;
                            stack.push(dst);
                            moves.add(dst);
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

                    Tile t = board.getTile(dst.col, dst.row);
                    if (!t.road(sides[i]))
                        continue;
                    int cost = t.costFrom(pawn, sides[i], true);
                    int r = src.remaining - cost;

                    if (dst.search == searchCount) {
                        if ((r >= 0) && (r > dst.remaining)) {
                            dst.remaining = r;
                            dst.parent = src;
                            dst.roadMarch = true;
                            roadMarch.push(dst);
                            if (!moves.contains(dst)) moves.add(dst);
                        }
                    } else {
                        dst.search = searchCount;
                        if (r >= 0) {
                            dst.parent = src;
                            dst.remaining = r;
                            dst.roadMarch = true;
                            roadMarch.push(dst);
                            moves.add(dst);
                        } else {
                            dst.parent = null;
                            dst.remaining = -1;
                        }
                    }
                }
            }
        }

        return moves;
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

    public List<Node> possibleTargetsFrom(Pawn pawn, int col, int row)
    {
        searchCount += 1;
        targets.clear();

        Node adjacents[] = new Node[6];

        Tile tile = board.getTile(col, row);

        int range = pawn.getAttackRangeFrom(tile);
        int angle = pawn.getAngleOfAttack();
        int extendedAngle = pawn.getOrientation().opposite().allBut();

        Node from = getNode(col, row);
        from.search = searchCount;
        from.remaining = range;

        if (range <= 0)
            return targets;

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
                        Tile t = board.getTile(dst.col, dst.row);
                        if (t.hasTargetsFor(pawn) && hasClearLineOfSight(from, dst, angle)) targets.add(dst);
                    }
                }
            }
        }

        return targets;
    }

    public boolean buildAttack(Pawn pawn, Pawn target, int col0, int row0, int col1, int row1)
    {
        pawn.attack.isClear = false;
        pawn.attack.target = target;
        pawn.attack.distance = distance(col0, row0, col1, row1);

        if (pawn.attack.distance > pawn.getAttackRangeFrom(board.getTile(col0, row0)))
            return false;

        List<Node> los = lineOfSight(col0, row0, col1, row1);
        Node last = los.get(los.size() -1);
        if ((last.col != col1) || (last.row != row1))
            return false;

        if (!validatePathAngle(pawn.getAngleOfAttack(), los)) {
            System.err.println("angleOfAttack is not respected");
            return false;
        }

        pawn.attack.isClear = true;
        pawn.attack.isFlankAttack = isFlankAttack(target.getFlankSides(), los);

        return true;
    }

    private boolean hasClearLineOfSight(Node from, Node to, int angleOfAttack)
    {
        List<Node> los = lineOfSight(from.col, from.row, to.col, to.row);
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
        return o.isInSides(angle);
    }

    private boolean validatePathAngle(int angle, List<Node> los)
    {
        Node prev = null;
        for (Node next : los) {
            if (prev != null) {
                Orientation o = Orientation.fromMove(prev.col, prev.row, next.col, next.row);
                if (!o.isInSides(angle))
                    return false;
            }
            prev = next;
        }

        return true;
    }

    public List<Node> lineOfSight(int x0, int y0, int x1, int y1)
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
            return verticalLineOfSight(x0, y0, x1, y1);
        if (dx == (3 * dy))
            return diagonalLineOfSight(x0, y0, x1, y1);

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
            if(board.getTile(x, y).blockLineOfSightFrom(from)) return los;
        }

        return los;
    }

    private List<Node> verticalLineOfSight(int x0, int y0, int x1, int y1)
    {
        Tile from = board.getTile(x0, y0);

        int d = ( (y1 > y0) ? 1 : -1);
        int x = x0;
        int y = y0;

        Tile t = null;
        los.add(getNode(x, y));
        while((x != x1) || (y != y1)) {
            boolean ok = false;

            y += d;
            t = board.getTile(x, y);
            if (!t.isOffMap()) los.add(getNode(x, y));
            if (!t.blockLineOfSightFrom(from))
                ok = true;

            x += d;
            t = board.getTile(x, y);
            if (!t.isOffMap()) los.add(getNode(x, y));
            if (!t.blockLineOfSightFrom(from))
                ok = true;

            if (!ok)
                return los;

            y += d;
            t = board.getTile(x, y);
            if (!t.isOffMap()) los.add(getNode(x, y));
        }

        return los;
    }

    private List<Node> diagonalLineOfSight(int x0, int y0, int x1, int y1)
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
            boolean ok = false;

            x += dx;
            t = board.getTile(x, y);
            if (!t.isOffMap()) los.add(getNode(x, y));
            if (!t.blockLineOfSightFrom(from))
                ok = true;

            y += dy;
            if (!sig)
                x -= dx;
            t = board.getTile(x, y);
            if (!t.isOffMap()) los.add(getNode(x, y));
            if (!t.blockLineOfSightFrom(from))
                ok = true;

            if (!ok)
                return los;

            x += dx;
            t = board.getTile(x, y);
            if (!t.isOffMap()) los.add(getNode(x, y));
        }

        return los;
    }

    public void clearPossiblePaths()
    {
        path.clear();
        for (List<Node> v : possiblePaths)
            v.clear();
        possiblePaths.clear();
        possiblePathsFilters.clear();
    }

    public List<ArrayList<Node>> possiblePathsFilterToggle(int col, int row)
    {
        Node n = getNode(col, row);
        if (possiblePathsFilters.contains(n))
            possiblePathsFilters.remove(n);
        else
            possiblePathsFilters.add(n);
        return possiblePaths();
    }

    public List<ArrayList<Node>> possiblePaths()
    {
        int s = possiblePathsFilters.size();

        List<ArrayList<Node>> paths = new LinkedList<ArrayList<Node>>();
        for (ArrayList<Node> path : possiblePaths) {
            int ok = 0;
            for (Node filter : possiblePathsFilters) {
                if (path.contains(filter))
                    ok += 1;
            }
            if (ok == s) {
                if (path.size() == (s + 2)) {
                    paths.clear();
                    paths.add(path);
                    return paths;
                } else
                    paths.add(path);
            }
        }

        return paths;
    }

    public List<ArrayList<Node>> possiblePaths(Pawn pawn, int col0, int row0, int col1, int row1)
    {
        clearPossiblePaths();

        Node from = getNode(col0, row0);
        Node to = getNode(col1, row1);

        if (distance(from, to) == 1) {
            ArrayList<Node> temp = new ArrayList<Node>(2);
            temp.add(from);
            temp.add(to);
            possiblePaths.add(temp);
        } else {
            path.add(from);
            findAllPaths(pawn, from, to, pawn.getMovementPoints(), true, pawn.getRoadMarchBonus());
        }

        return possiblePaths;
    }

    private void findAllPaths(Pawn pawn, Node from, Node to, int mvtLeft, boolean roadMarch, int roadMarchBonus)
    {
        Node moves[] = new Node[6];
        adjacentMoves(from, moves);

        for(int i = 0; i < 6; i++) {
            Node next = moves[i];
            if (next == null) continue;

            Tile t = board.getTile(next.col, next.row);
            boolean road = t.road(sides[i]);
            int cost = t.costFrom(pawn, sides[i], road);
            int r = (mvtLeft - cost);
            if (roadMarch & road) r += roadMarchBonus;

            if ((distance(next, to) <= r)) {
                if (next == to) {
                    ArrayList<Node> temp = new ArrayList<Node>(path.size() + 1);
                    for (Node n: path)
                        temp.add(n);
                    temp.add(next);
                    possiblePaths.add(temp);
                } else {
                    path.add(next);
                    findAllPaths(pawn, next, to, (mvtLeft - cost), (roadMarch & road), roadMarchBonus);
                    path.remove(path.size() - 1);
                }
            }
        }
    }

    public int pathCost(Pawn pawn, List<Node> path)
    {
        int cost = 0;
        boolean roadMarch = true;
        Node prev = null;

        for (Node next : path) {
            if (prev != null) {
                Orientation o = Orientation.fromMove(next.col, next.row, prev.col, prev.row);
                Tile t = board.getTile(next.col, next.row);
                boolean road = t.road(o);
                cost += t.costFrom(pawn, o, road);
                roadMarch &= road;
            }
            prev = next;
        }

        if (roadMarch)
            cost -= pawn.getRoadMarchBonus();
        if (cost < 1)
            cost = 1;

        return cost;
    }
}
