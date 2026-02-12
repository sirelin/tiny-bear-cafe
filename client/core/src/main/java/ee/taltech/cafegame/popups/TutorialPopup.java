package ee.taltech.cafegame.popups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class TutorialPopup extends Window {

    public TutorialPopup(String imagePath) {
        super("", new WindowStyle(new BitmapFont(), Color.WHITE, null));
        this.setBackground((Drawable) null);

        Texture popupTexture = new Texture(Gdx.files.internal(imagePath));
        Image bg = new Image(popupTexture);
        float popupWidth = Gdx.graphics.getWidth() * 0.8f;
        float popupHeight = Gdx.graphics.getHeight() * 0.7f;

        bg.setSize(popupWidth, popupHeight);
        bg.setPosition(0, 0);
        bg.setTouchable(Touchable.disabled);

        this.setSize(popupWidth, popupHeight);
        this.setPosition(
            (Gdx.graphics.getWidth() - popupWidth) / 2f,
            (Gdx.graphics.getHeight() - popupHeight) / 2f
        );
        this.addActor(bg);
    }
}
