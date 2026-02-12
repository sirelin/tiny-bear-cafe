package ee.taltech.cafegame.utils;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class TiledMapLoader {
    private TiledMap map;
    private List<Rectangle> rectangles = new ArrayList<>();

    /**
     * @param path path to the map, starting from /assets folder
     */
    public TiledMapLoader(String path) {
        this.map = new TmxMapLoader().load(path);
    }

    /**
     * @return OrthogonalTiledMapRenderer of the TiledMap
     */
    public OrthogonalTiledMapRenderer setupMap() {
        return new OrthogonalTiledMapRenderer(map);
    }

    public TiledMapLoader parseAllObjects() {
        for (MapLayer mapLayer : map.getLayers()) {
            System.out.println("Layer: " + mapLayer.getName());
            for (MapObject mapObject : mapLayer.getObjects()) {
                if (mapObject instanceof RectangleMapObject rectangleMapObject) {
                    Rectangle rectangle = rectangleMapObject.getRectangle();
                    if (rectangle != null) {
                        rectangles.add(rectangle);
                    }
                } else {
                    System.out.println("MapObject is not a RectangleMapObject: " + mapObject);
                }
            }
        }
        return this;
    }
}
