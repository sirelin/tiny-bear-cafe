package ee.taltech.cafegame.interactables;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import ee.taltech.cafegame.ClientMain;

public class Table extends Interactables {
    public Table(World world, TiledMap map, RectangleMapObject mapObject, ClientMain game) {
        super(world, map, mapObject, game);
    }

    @Override
    public void onInteract() {
        System.out.println("Table interact");
    }
}
