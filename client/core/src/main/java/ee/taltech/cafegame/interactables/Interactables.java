package ee.taltech.cafegame.interactables;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.cafegame.ClientMain;

public abstract class Interactables {
    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;

    public Body getBody() {
        return body;
    }

    protected Body body;
    protected RectangleMapObject mapObject;
    protected final ClientMain game;

    protected Interactables(World world, TiledMap map, RectangleMapObject mapObject, ClientMain game) {
        this.game = game;
        this.world = world;
        this.map = map;
        this.mapObject = mapObject;

        this.bounds = (mapObject).getRectangle();

        float PPM = 100f;
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / PPM,
            (bounds.getY() + bounds.getHeight() / 2) / PPM);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(bounds.getWidth() / 2 / PPM, bounds.getHeight() / 2 / PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        this.body = world.createBody(bdef);
        this.body.createFixture(fdef);
    }

    public abstract void onInteract();

    public Rectangle getBounds() {
        return mapObject.getRectangle();
    }

}
