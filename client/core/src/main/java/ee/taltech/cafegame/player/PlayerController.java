package ee.taltech.cafegame.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.InputProcessor;
import com.esotericsoftware.kryonet.Client;
import ee.taltech.cafegame.interactables.Interactables;
import ee.taltech.cafegame.packets.PacketSendCoordinates;
import ee.taltech.cafegame.screens.PlayScreen;
import ee.taltech.cafegame.utils.Box2dWorldGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerController extends Sprite implements InputProcessor  {
    private static final int MOVE_SPEED = 1;

    private Player player;
    private Client client;
    private Body body;
    private Map<Vector2, Interactables> interactables;

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

    public PlayerController(Player player, Client client, Map<Vector2, Interactables> interactables) {
        this.player = player;
        this.client = client;
        this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
        this.interactables = interactables;
    }

    public void update(Map<Vector2, Interactables> interactables) {
        // Movement logic
        boolean moved = false;
        Vector2 velocity = new Vector2();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -MOVE_SPEED;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = MOVE_SPEED;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            velocity.y = MOVE_SPEED;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            velocity.y = -MOVE_SPEED;
            moved = true;
        }

        if (moved) {
            body.setLinearVelocity(velocity);
            Vector2 pos = body.getPosition();
            player.setX(pos.x);
            player.setY(pos.y);
            sendCoordinatesToServer();
        } else {
            body.setLinearVelocity(0, 0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            for (Interactables interactable : interactables.values()) {
                if (isNear(interactable)) {
                    interactable.onInteract(); // You define this in the Interactables class
                    break;
                }
            }
        }
    }

    public void createBody(World world, float spawnX, float spawnY) {
        if (body != null) return;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(spawnX, spawnY);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6 / PlayScreen.PPM, 6 / PlayScreen.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;

        body.createFixture(fixtureDef);
        shape.dispose();

        this.body = body;
    }

    private boolean isNear(Interactables interactable) {
        Rectangle bounds = interactable.getBounds();
        Vector2 playerPos = body.getPosition().cpy().scl(PlayScreen.PPM);
        return bounds.contains(playerPos.x, playerPos.y);
    }


    private void sendCoordinatesToServer() {
        // Send the updated coordinates to the server
        PacketSendCoordinates packetSendCoordinates = new PacketSendCoordinates();
        packetSendCoordinates.setX(player.getX());
        packetSendCoordinates.setY(player.getY());
        float box2DX = body.getPosition().x;
        float box2DY = body.getPosition().y;
        packetSendCoordinates.setX(box2DX);
        packetSendCoordinates.setY(box2DY);
        packetSendCoordinates.setPlayerId(player.getId());
        packetSendCoordinates.setGameId(player.getGameId());
        client.sendTCP(packetSendCoordinates);
}

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
