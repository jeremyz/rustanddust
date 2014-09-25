package ch.asynk.tankontank.engine;

import java.util.List;
import java.util.ArrayDeque;
import java.util.Vector;

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
    }

    private int cols;
    private int rows;
    private Board board;
    private Node nodes[];
    private int searchCount;
    private ArrayDeque<Node> stack;
    private ArrayDeque<Node> roadMarch;
    private List<Node> result;
    private Node adjacents[];
    private Board.Orientation directions[];

    public SearchBoard(Board board, int cols, int rows)
    {
        this.cols = cols;
        this.rows = rows;
        this.board = board;
        this.nodes = new Node[cols * rows];
        for (int j = 0; j < rows; j++)
            for (int i = 0; i < cols; i++)
                nodes[i + (j * cols)] = new Node(i, j);
        this.searchCount = 0;
        this.stack = new ArrayDeque<Node>(20);
        this.roadMarch = new ArrayDeque<Node>(5);
        this.result = new Vector<Node>(10);
        this.adjacents = new Node[6];
        this.directions = new Board.Orientation[6];
        directions[0] = Board.Orientation.NORTH;
        directions[1] = Board.Orientation.NORTH_EAST;
        directions[2] = Board.Orientation.SOUTH_EAST;
        directions[3] = Board.Orientation.SOUTH;
        directions[4] = Board.Orientation.SOUTH_WEST;
        directions[5] = Board.Orientation.NORTH_WEST;
    }

    private Node getNode(int col, int row)
    {
        if ((col < 0) || (col >= cols) || (row < 0) || (row >= rows)) return null;
        return nodes[col + (row * cols)];
    }

    public void adjacentMoves(Node src)
    {
        // move to enter dst by
        adjacents[0] = getNode((src.col - 1), src.row);
        adjacents[3] = getNode((src.col + 1), src.row);
        if ((src.row % 2) == 0) {
            adjacents[1] = getNode((src.col - 1), (src.row + 1));
            adjacents[2] = getNode(src.col, (src.row + 1));
            adjacents[4] = getNode(src.col, (src.row - 1));
            adjacents[5] = getNode((src.col - 1), (src.row - 1));
        } else {
            adjacents[1] = getNode(src.col, (src.row + 1));
            adjacents[2] = getNode((src.col + 1), (src.row + 1));
            adjacents[4] = getNode((src.col + 1), (src.row - 1));
            adjacents[5] = getNode(src.col, (src.row - 1));
        }
    }

    public List<Node> reachableFrom(Pawn pawn, int col, int row)
    {
        searchCount += 1;
        result.clear();

        Node start = getNode(col, row);
        start.parent = null;
        start.search = searchCount;
        start.remaining = pawn.getMovementPoints();
        start.roadMarch = true;

        if (start.remaining <= 0)
            return result;

        int roadMarchBonus = pawn.getRoadMarchBonus();
        boolean first = true;

        stack.push(start);

        while (stack.size() != 0) {
            Node src = stack.pop();

            if (src.remaining <= 0) {
                if (src.roadMarch) {
                    src.remaining = roadMarchBonus;
                    roadMarch.push(src);
                }
                continue;
            }

            adjacentMoves(src);

            for(int i = 0; i < 6; i++) {
                Node dst = adjacents[i];
                if (dst != null) {

                    Tile t = board.getTile(dst.col, dst.row);
                    boolean road = t.road(directions[i]);
                    int cost = t.costFrom(pawn, directions[i], road);
                    boolean mayMoveOne = first && t.atLeastOneMove(pawn);
                    int r = src.remaining - cost;
                    boolean roadMarch = road && src.roadMarch;

                    if (dst.search == searchCount) {
                        if ((r >= 0) && ((r > dst.remaining) || (roadMarch && ((r + roadMarchBonus) >= dst.remaining)))) {
                            dst.remaining = r;
                            dst.parent = src;
                            dst.roadMarch = roadMarch;
                        }
                    } else {
                        dst.search = searchCount;
                        if ((r >= 0) || mayMoveOne) {
                            dst.parent = src;
                            dst.remaining = r;
                            dst.roadMarch = roadMarch;
                            stack.push(dst);
                            result.add(dst);
                        } else {
                            dst.parent = null;
                            dst.remaining = Integer.MAX_VALUE;
                        }
                    }
                }
            }
            first = false;
        }

        while(roadMarch.size() != 0) {
            Node src = roadMarch.pop();

            adjacentMoves(src);

            for(int i = 0; i < 6; i++) {
                Node dst = adjacents[i];
                if (dst != null) {

                    Tile t = board.getTile(dst.col, dst.row);
                    if (!t.road(directions[i]))
                        continue;
                    int cost = t.costFrom(pawn, directions[i], true);
                    int r = src.remaining - cost;

                    if (dst.search == searchCount) {
                        if ((r >= 0) && (r > dst.remaining)) {
                            dst.remaining = r;
                            dst.parent = src;
                            dst.roadMarch = true;
                        }
                    } else {
                        dst.search = searchCount;
                        if (r >= 0) {
                            dst.parent = src;
                            dst.remaining = r;
                            dst.roadMarch = true;
                            roadMarch.push(dst);
                            result.add(dst);
                        } else {
                            dst.parent = null;
                            dst.remaining = Integer.MAX_VALUE;
                        }
                    }
                }
            }
        }

        return result;
    }

    private void adjacentAttacks(Node src, int attackAngle)
    {
        // move in allowed directions
        if (Board.Orientation.NORTH.isInSides(attackAngle))
            adjacents[0] = getNode((src.col + 1), src.row);
        if (Board.Orientation.SOUTH.isInSides(attackAngle))
            adjacents[3] = getNode((src.col - 1), src.row);
        if ((src.row % 2) == 0) {
            if (Board.Orientation.NORTH_EAST.isInSides(attackAngle))
                adjacents[1] = getNode(src.col, (src.row - 1));
            if (Board.Orientation.SOUTH_EAST.isInSides(attackAngle))
                adjacents[2] = getNode((src.col - 1), (src.row - 1));
            if (Board.Orientation.NORTH_WEST.isInSides(attackAngle))
                adjacents[4] = getNode(src.col, (src.row + 1));
            if (Board.Orientation.SOUTH_WEST.isInSides(attackAngle))
                adjacents[5] = getNode((src.col - 1), (src.row + 1));
        } else {
            if (Board.Orientation.NORTH_EAST.isInSides(attackAngle))
                adjacents[1] = getNode((src.col + 1), (src.row - 1));
            if (Board.Orientation.SOUTH_EAST.isInSides(attackAngle))
                adjacents[2] = getNode(src.col, (src.row - 1));
            if (Board.Orientation.NORTH_WEST.isInSides(attackAngle))
                adjacents[4] = getNode((src.col + 1), (src.row + 1));
            if (Board.Orientation.SOUTH_WEST.isInSides(attackAngle))
                adjacents[5] = getNode(src.col, (src.row + 1));
        }
    }

    private boolean hasClearLineOfSight(Tile from, Tile to)
    {
        // FIXME
        return true;
    }

    public List<Node> openToAttackFrom(Pawn pawn, int col, int row)
    {
        searchCount += 1;
        result.clear();

        Tile tile = board.getTile(col, row);

        Node start = getNode(col, row);
        start.search = searchCount;
        start.remaining = pawn.getAttackRangeFrom(tile);

        if (start.remaining <= 0)
            return result;

        adjacents[0] = null;
        adjacents[1] = null;
        adjacents[2] = null;
        adjacents[3] = null;
        adjacents[4] = null;
        adjacents[5] = null;

        int attackAngle = pawn.getAngleOfAttack();

        stack.push(start);

        while (stack.size() != 0) {
            Node src = stack.pop();

            if (src.remaining <= 0)
                continue;

            adjacentAttacks(src, attackAngle);
            int rangeLeft = src.remaining - 1;

            for(int i = 0; i < 6; i++) {
                Node dst = adjacents[i];
                if (dst != null) {
                    if (dst.search == searchCount) {
                        if ((rangeLeft > dst.remaining)) {
                            dst.remaining = rangeLeft;
                            stack.push(dst);
                        }
                    } else {
                        dst.search = searchCount;
                        dst.remaining = rangeLeft;
                        stack.push(dst);
                        Tile t = board.getTile(dst.col, dst.row);
                        if (t.hasTargetsFor(pawn) && hasClearLineOfSight(tile, t)) result.add(dst);
                    }
                }
            }
        }

        return result;
    }
}

