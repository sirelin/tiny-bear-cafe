package ee.taltech.cafegame.utils;


import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.cafegame.ClientMain;
import ee.taltech.cafegame.interactables.*;

import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Box2dWorldGenerator {
    private static Map<Vector2, Interactables> interactables;
    public Box2dWorldGenerator(World world, TiledMap map, ClientMain game) {
        interactables = new HashMap<>();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;
        float PPM = 100f;

        // world border
        for (RectangleMapObject object : map.getLayers().get("borders").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = object.getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(
                (rectangle.getX() + rectangle.getWidth() / 2) / PPM,
                (rectangle.getY() + rectangle.getHeight() / 2) / PPM
            );

            body = world.createBody(bdef);
            shape.setAsBox(rectangle.getWidth() / 2 / PPM, rectangle.getHeight() / 2 / PPM);

            fdef.shape = shape;
            body.createFixture(fdef);
        }

        for (RectangleMapObject object : map.getLayers().get("borders").getObjects().getByType(RectangleMapObject.class)) {
            String type = object.getProperties().get("type", String.class);
            if (type == null) {
                continue; // or log a warning
            }
            Interactables interactable = null;
            switch (type) {
                case "stove":
                    interactable = new Stove(world, map, object, game);
                    System.out.println(interactable);
                    break;
                case "fridge":
                    interactable = new Fridge(world, map, object, game);
                    break;
                case "table":
                    interactable = new Table(world, map, object, game);
                    break;
                case "coffee":
                    interactable = new CoffeeMachine(world, map, object, game);
                    break;
                case "cuttingboard":
                    interactable = new CuttingBoard(world, map, object, game);
                    break;
            }

            if (interactable != null) {
                Rectangle rect = object.getRectangle();
                Vector2 key = new Vector2(rect.x, rect.y);
                interactables.put(key, interactable);
            }
        }
    }

    public static Map<Vector2, Interactables> getInteractables() {
        return interactables;
    }
}
