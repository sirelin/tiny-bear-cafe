package ee.taltech.cafegame.popups;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class EggMakingGame extends Window {

    private final Stage stage;
    private boolean orderCompleted = false;
    private int keyPressCount = 0;

    private final int[] requiredKeySequence = {
        Input.Keys.E, Input.Keys.G, Input.Keys.G
    };

    public EggMakingGame(String orderText, Skin skin, OrderPopup.OrderAcceptedListener listener) {
        super("", skin);
        setDefaults();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        Texture frameTexture = new Texture(Gdx.files.internal("skin/taustvalge.png"));
        Texture gameTexture = new Texture(Gdx.files.internal("skin/breakfastgame.png"));
        frameTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        gameTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        float scale = calculateScale(screenWidth, screenHeight, frameTexture);
        float frameWidth = frameTexture.getWidth() * scale;
        float frameHeight = frameTexture.getHeight() * scale;

        setSize(frameWidth, frameHeight);
        centerOnScreen(screenWidth, screenHeight, frameWidth, frameHeight);

        Image frameImage = createImage(frameTexture, frameWidth, frameHeight);
        Image gameImage = createImage(
            gameTexture,
            gameTexture.getWidth() * scale,
            gameTexture.getHeight() * scale
        );
        gameImage.setPosition(
            (frameWidth - gameImage.getWidth()) / 2f,
            (frameHeight - gameImage.getHeight()) / 2f
        );

        clearChildren();
        addActor(frameImage);
        addActor(gameImage);

        this.stage = new Stage(new ScreenViewport());
        this.stage.addActor(this);

        setupInput(orderText, listener);
    }

    private void setDefaults() {
        setBackground((Drawable) null);
        setModal(true);
        setMovable(false);
        setResizable(false);
        setTouchable(Touchable.enabled);
    }

    private float calculateScale(float screenWidth, float screenHeight, Texture texture) {
        float maxWidth = screenWidth * 0.6f;
        float maxHeight = screenHeight * 0.6f;
        float scaleX = maxWidth / texture.getWidth();
        float scaleY = maxHeight / texture.getHeight();
        return Math.min(scaleX, scaleY);
    }

    private void centerOnScreen(float screenWidth, float screenHeight, float width, float height) {
        setPosition(
            (screenWidth - width) / 2f,
            (screenHeight - height) / 2f
        );
    }

    private Image createImage(Texture texture, float width, float height) {
        Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        image.setSize(width, height);
        image.setPosition(0, 0);
        image.setTouchable(Touchable.disabled);
        return image;
    }

    private void setupInput(String orderText, OrderPopup.OrderAcceptedListener listener) {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!orderCompleted) {
                    if (keycode == requiredKeySequence[keyPressCount]) {
                        keyPressCount++;
                        if (keyPressCount == requiredKeySequence.length) {
                            orderCompleted = true;
                            remove();
                            listener.onOrderAccepted(orderText);
                        }
                    } else {
                        keyPressCount = 0;
                    }
                    return true;
                }
                return false;
            }
        });
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public Stage getStage() {
        return stage;
    }

    public boolean getOrderCompleted() {
        return orderCompleted;
    }
}
