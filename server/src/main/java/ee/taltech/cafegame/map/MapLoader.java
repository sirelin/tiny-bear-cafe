package ee.taltech.cafegame.map;

import ee.taltech.cafegame.ServerMain;
import ee.taltech.cafegame.ai.NPC;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

    private int[][] collisions;
    private final ServerMain serverMain;
    private final List<NPC> aiBots = new ArrayList<>();
    private final List<int[]> tables = new ArrayList<>();  // List to store tables

    public MapLoader(ServerMain serverMain) {
        this.serverMain = serverMain;
        this.readTilemapData();
        this.generateAiBots();
    }

    private void generateAiBots() {
    }

    public int[][] getCollisions() {
        return collisions;
    }

    public List<int[]> getTables() {
        return tables;
    }

    // Main method to load the map
    public void readTilemapData() {
        try {
            tables.clear();
            File file = new File("assets/map assets/cafemap.tmx");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // For security
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            // Get all object groups
            Element mapElement = (Element) doc.getElementsByTagName("map").item(0);
            int mapWidth = Integer.parseInt(mapElement.getAttribute("width"));
            int mapHeight = Integer.parseInt(mapElement.getAttribute("height"));
            collisions = new int[mapHeight][mapWidth];

            NodeList objectGroups = doc.getElementsByTagName("objectgroup");

            for (int i = 0; i < objectGroups.getLength(); i++) {
                Element group = (Element) objectGroups.item(i);
                String groupName = group.getAttribute("name");

                if (!"borders".equals(groupName)) continue;

                NodeList objects = group.getElementsByTagName("object");

                for (int j = 0; j < objects.getLength(); j++) {
                    Element object = (Element) objects.item(j);

                    float px = Float.parseFloat(object.getAttribute("x"));
                    float py = Float.parseFloat(object.getAttribute("y"));
                    float pWidth = object.hasAttribute("width") ? Float.parseFloat(object.getAttribute("width")) : 0;
                    float pHeight = object.hasAttribute("height") ? Float.parseFloat(object.getAttribute("height")) : 0;

                    final int tileSize = 16;

                    int startTileX = (int) (px / tileSize);
                    int endTileX = (int) Math.ceil((px + pWidth) / tileSize);

                    int startTileY = (int) (py / tileSize);
                    int endTileY = (int) Math.ceil((py + pHeight) / tileSize);

                    // Clamp to map bounds if needed
                    startTileX = Math.max(0, startTileX);
                    endTileX = Math.min(mapWidth, endTileX);
                    startTileY = Math.max(0, startTileY);
                    endTileY = Math.min(mapHeight, endTileY);


                    for (int ty = startTileY; ty < endTileY; ty++) {
                        for (int tx = startTileX; tx < endTileX; tx++) {
                            collisions[ty][tx] = 1;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
