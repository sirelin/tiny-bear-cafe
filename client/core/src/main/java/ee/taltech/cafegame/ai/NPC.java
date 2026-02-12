package ee.taltech.cafegame.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Client;
import ee.taltech.cafegame.packets.PacketNpcOrderTaken;
import ee.taltech.cafegame.packets.PacketRemoveNpcMessage;
import ee.taltech.cafegame.screens.PlayScreen;

public class NPC extends Sprite {
    public float x;
    public float y;
    public float moveX;
    public float moveY;
    private int id;
    private boolean canTakeOrder = true;
    public long canTakeOrderTimestamp = -1;
    private Body body;
    private static final float MOVE_SPEED = 0.5f;
    private static final float STOP_THRESHOLD = 0.05f;
    private final Vector2 targetPosition = new Vector2();
    private String givenOrder;
    private Texture orderTexture;
    private Texture waitingTexture;
    private Client client;
    private Texture skinTexture;

    public NPC(int id, float x, float y, Sprite sprite, Texture npcTexture) {
        super(sprite);
        this.id = id;
        this.x = x;
        this.y = y;
        this.targetPosition.set(x, y);
        this.moveX = this.x;
        this.moveY = this.y;
        setSize(32f / 100f, 32f / 100f);
    }

    // npc movement
    public void update(float delta) {
        if (body != null) {
            Vector2 currentPosition = body.getPosition();
            Vector2 toTarget = new Vector2(targetPosition).sub(currentPosition);
            float distance = toTarget.len();

            if (distance > STOP_THRESHOLD) {
                Vector2 velocity = toTarget.nor().scl(MOVE_SPEED);
                body.setLinearVelocity(velocity);
            } else {
                body.setLinearVelocity(0, 0);
            }
            setPosition(currentPosition.x - getWidth() / 2f, currentPosition.y - getHeight() / 2f);

        }
    }

    public void createBody(World world) {
        if (body != null) return;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(this.x, this.y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8 / PlayScreen.PPM, 8 / PlayScreen.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }


    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void draw(SpriteBatch batch) {
        update(Gdx.graphics.getDeltaTime());
        super.draw(batch);
    }

    public void setTarget(float x, float y) {
        targetPosition.set(x, y);
    }

    public int getId() {
        return id;
    }

    public String getGivenOrder() {
        return givenOrder;
    }

    public void setGivenOrder(String order) {
        this.givenOrder = order;
    }

    public void setOrderTexture(Texture texture) {
        this.orderTexture = texture;
    }

    public Texture getOrderTexture() {
        return orderTexture;
    }

    public void setWaitingTexture(Texture texture) {
        this.waitingTexture = texture;
    }

    public Texture getWaitingTexture() {
        return this.waitingTexture;
    }

    public boolean canTakeOrder() {
        return canTakeOrder;
    }

    public void setCanTakeOrder(boolean canTakeOrder) {
        this.canTakeOrder = canTakeOrder;
        if (!canTakeOrder) {
            this.canTakeOrderTimestamp = System.currentTimeMillis();
            System.out.println("order taken: " + canTakeOrderTimestamp);
        } else {
            this.canTakeOrderTimestamp = -1; // Reset
        }
    }

    public void setSkin(Texture texture) {
        if (this.skinTexture == null) {
            this.skinTexture = texture;
            setRegion(texture);
        }
    }

    public Texture getSkin() {
        return this.skinTexture;
    }


}
