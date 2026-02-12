package ee.taltech.cafegame.ai;

import java.util.*;

// A* algoritmi klass, mida kasutatakse tee leidmiseks ruudustikus
public class AStar {
    private final int maxX; // Ruudustiku laius
    private final int maxY; // Ruudustiku kõrgus
    private final int[][] grid; // 2D ruudustik (0 = vaba, 1 = takistus)

    // Naabrite nihked, mis võimaldavad liikuda neljas suunas
    private final int[][] neighbours = {
            {-1, 0}, {0, -1}, {1, 0}, {0, 1},   // 4 cardinal directions
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // diagonals
    };
    // Konstruktor, mis määrab ruudustiku ja selle mõõtmed
    public AStar(int[][] grid) {
        this.grid = grid;
        this.maxX = grid[0].length;
        this.maxY = grid.length;
    }

    // Sõlm, mida kasutatakse A* algoritmi käigus
    public class Node {
        int x; // Sõlme x-koordinaat
        int y; // Sõlme y-koordinaat
        int gScore; // Tee maksumus alguspunktist sellesse sõlme
        int hScore; // Heuristiline kaugus sihtkohani

        Node parent; // Eelnev sõlm (tee jälgimiseks)

        // Konstruktor, mis loob uue sõlme
        Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.gScore = Integer.MAX_VALUE; // Alguses on teepikkus null
            this.hScore = 0; // Alguses ei arvutata heuristikat
            this.parent = null; // Pole veel eelnevat Node
        }

        // Heuristika (Manhattani kaugus), mis hindab kaugust sihtkohani
        void updateHScore(int dstX, int dstY) {
            this.hScore = Math.abs(x - dstX) + Math.abs(y - dstY);
        }

        // F-skoor, mida kasutatakse tee leidmisel (gScore + hScore)
        int getFScore() {
            return this.hScore + this.gScore;
        }

        // Meetod Node-ide võrdlemiseks (vajalik A* algoritmi jaoks)
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node node)) return false;
            return x == node.x && y == node.y;
        }

        // Meetod Node-ide unikaalse identifikaatori loomiseks
        @Override
        public int hashCode() {
            return Integer.hashCode(x + (y * maxY));
        }
    }
    // Find path
    public List<Node> findPath(int srcX, int srcY, int dstX, int dstY) {
        if (isBlocked(srcX, srcY) || isBlocked(dstX, dstY)) return null;

        Map<String, Node> allNodes = new HashMap<>();
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::getFScore));
        Set<Node> openSetHash = new HashSet<>();
        Set<Node> closedSet = new HashSet<>();

        Node start = new Node(srcX, srcY);
        start.gScore = 0;
        start.updateHScore(dstX, dstY);

        openSet.add(start);
        openSetHash.add(start);
        allNodes.put(key(srcX, srcY), start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            openSetHash.remove(current);

            if (current.x == dstX && current.y == dstY) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (int[] offset : neighbours) {
                int nx = current.x + offset[0];
                int ny = current.y + offset[1];

                if (!isValid(nx, ny) || isBlocked(nx, ny)) continue;

                if (Math.abs(offset[0]) == 1 && Math.abs(offset[1]) == 1) {
                    // Check adjacent sides
                    if (isBlocked(current.x + offset[0], current.y) || isBlocked(current.x, current.y + offset[1])) {
                        continue; // Can't move diagonally if either adjacent side blocked
                    }
                }

                String neighborKey = key(nx, ny);
                Node neighbor = allNodes.computeIfAbsent(neighborKey, k -> new Node(nx, ny));

                if (closedSet.contains(neighbor)) continue;

                int tentativeG = current.gScore + ((Math.abs(offset[0]) == 1 && Math.abs(offset[1]) == 1) ? 14 : 10);

                if (tentativeG < neighbor.gScore) {
                    neighbor.parent = current;
                    neighbor.gScore = tentativeG;
                    neighbor.updateHScore(dstX, dstY);

                    if (!openSetHash.contains(neighbor)) {
                        openSet.add(neighbor);
                        openSetHash.add(neighbor);
                    } else {
                        openSet.remove(neighbor);
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return null;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && y >= 0 && x < maxX && y < maxY;
    }

    private boolean isBlocked(int x, int y) {
        return grid[y][x] == 1;
    }

    private String key(int x, int y) {
        return x + "," + y;
    }

    private List<Node> reconstructPath(Node current) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }
}
