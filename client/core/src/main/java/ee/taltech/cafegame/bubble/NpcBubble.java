package ee.taltech.cafegame.bubble;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.Viewport;
import ee.taltech.cafegame.ai.NPC;

public class NpcBubble extends Actor {
    private Texture texture;
    private NPC npc;
    private Viewport viewport;

    public NpcBubble(Texture texture, NPC npc, Viewport viewport) {
        this.texture = texture;
        this.npc = npc;
        this.viewport = viewport;

        setSize(32f, 32f); // screen pixels
        getColor().a = 1f;

    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Recalculate position from world to screen every frame
        Vector2 npcPos = npc.getBody().getPosition();
        Vector2 screenPos = viewport.project(new Vector2(npcPos.x - 0.05f, npcPos.y + 0.1f));
        setPosition(screenPos.x, screenPos.y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor()); // enable fading
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        batch.setColor(1, 1, 1, 1); // reset
    }
}
