package ee.taltech.cafegame.ai;

import ee.taltech.cafegame.ServerMain;
import ee.taltech.cafegame.map.MapLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NPC {

    private static int nextId = 1;
    private static final float TILE_SIZE = 16;
    private static final Random random = new Random();
    private float[][] assignedTables;

    private final ServerMain serverMain;
    private AStar aStar;
    private int[][] collisionMap;
    private final int id;
    private float x, y;
    private String skinName;

    private static final float[][][] SPAWN_GROUPS = {
            {{8.28f, 1.69f}, {9.06f, 1.56f}, {7.74f, 1.57f}, {9.10f, 2.56f}},
            {{8.95f, 3.78f}, {8.22f, 3.20f}, {8.85f, 3.20f}},
            {{7.48f, 2.62f}, {7.34f, 3.57f}, {6.38f, 2.77f}, {7.55f, 4.23f}},
            {{5.87f, 2.29f}, {6.88f, 2.11f}, {6.55f, 1.59f}, {5.22f, 1.37f}, {4.89f, 2.27f}}
    };

    private static final float[][][] TABLE_GROUPS = {
            {{8.34f, 1.02f}, {8.93f, 1.03f}, {9.40f, 1.13f}, {9.52f, 2.70f}},
            {{9.52f, 3.67f}, {9.32f, 4.14f}, {7.75f, 3.69f}, {7.39f, 3.04f}},
            {{6.75f, 3.21f}, {6.64f, 3.98f}, {5.89f, 3.02f}, {5.35f, 3.32f}},
            {{4.60f, 3.14f}, {5.46f, 1.03f}, {5.86f, 1.55f}, {5.66f, 1.88f}, {6.24f, 1.88f}}
    };

    public NPC(float x, float y, ServerMain serverMain) {
        this.id = nextId++;
        this.x = x;
        this.y = y;
        this.serverMain = serverMain;

        try {
            loadMapData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        float[] safeSpawn = findSafeSpawnLocation();
        this.x = safeSpawn[0];
        this.y = safeSpawn[1];
    }

    private float[] findSafeSpawnLocation() {
        int groupIndex = random.nextInt(SPAWN_GROUPS.length);
        float[][] spawnGroup = SPAWN_GROUPS[groupIndex];
        float[] chosenSpawn = spawnGroup[random.nextInt(spawnGroup.length)];
        this.assignedTables = TABLE_GROUPS[groupIndex];
        return chosenSpawn;
    }

    private void loadMapData() throws IOException {
        MapLoader loader = new MapLoader(serverMain);
        loader.readTilemapData();
        this.collisionMap = loader.getCollisions();
        this.aStar = new AStar(collisionMap);
    }

    public void updateCollisionMap(int[][] collisionMap) {
        this.collisionMap = collisionMap;
        this.aStar = new AStar(collisionMap);
    }

    public void moveThread() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                while (true) {
                    if (assignedTables == null || collisionMap == null || aStar == null) return;

                    float ppm = 100f;
                    float npcPixelX = x * ppm;
                    float npcPixelY = y * ppm;

                    int mapHeight = collisionMap.length;
                    int startX = (int) (npcPixelX / TILE_SIZE);
                    int startY = mapHeight - 1 - (int) (npcPixelY / TILE_SIZE);

                    float[] target = assignedTables[random.nextInt(assignedTables.length)];
                    float targetX = target[0];
                    float targetY = target[1];
                    int goalX = (int) (targetX * ppm / TILE_SIZE);
                    int goalY = mapHeight - 1 - (int) (targetY * ppm / TILE_SIZE);

                    List<AStar.Node> path = aStar.findPath(startX, startY, goalX, goalY);
                    if (path == null) continue;

                    for (int i = 1; i < path.size(); i++) {
                        AStar.Node step = path.get(i);
                        int flippedY = mapHeight - 1 - step.y;
                        float newX = step.x * TILE_SIZE / ppm;
                        float newY = flippedY * TILE_SIZE / ppm;
                        setX(newX);
                        setY(newY);
                        serverMain.moveNpc(1, id, newX, newY);
                        Thread.sleep(500);
                    }
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "NPC-MoveThread-" + id).start();
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }

    public String getSkinName() {
        return skinName;
    }

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
