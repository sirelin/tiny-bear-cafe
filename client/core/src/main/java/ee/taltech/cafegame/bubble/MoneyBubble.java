package ee.taltech.cafegame.bubble;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import ee.taltech.cafegame.screens.PlayScreen;

public class MoneyBubble extends Actor {
    private Texture texture;
    private float startX, startY;

    public MoneyBubble(Texture texture, float x, float y) {
        this.texture = texture;
        this.startX = x;
        this.startY = y;
        setPosition(x, y);
        setSize(32f, 32f);
        // Add actions: float up and fade out over 1 second, then remove
        addAction(Actions.sequence(
            Actions.delay(1f),
            Actions.fadeOut(0.5f),
            Actions.removeActor()
        ));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }
}
