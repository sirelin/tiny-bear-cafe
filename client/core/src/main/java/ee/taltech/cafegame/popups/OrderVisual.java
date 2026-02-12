package ee.taltech.cafegame.popups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class OrderVisual {
    private Image image;
    private Texture texture;


    public OrderVisual(String texturePath) {
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        this.image.setSize(20, 20); // or make this configurable
    }

    public Image getImage() {
        return image;
    }

    public void setPosition(float x, float y) {
        image.setPosition(x, y);
    }

    public void remove() {
        image.remove();
    }

    public void dispose() {
        texture.dispose();
    }
}
