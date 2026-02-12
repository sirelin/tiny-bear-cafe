package ee.taltech.cafegame.interactables;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import ee.taltech.cafegame.ClientMain;

public class Stove extends Interactables {
    public Stove(World world, TiledMap map, RectangleMapObject mapObject, ClientMain game) {
        super(world, map, mapObject, game);
    }

    public void onInteract() {
        System.out.println("Player interacted with the stove!");
        // Add interaction logic here
    }
}
