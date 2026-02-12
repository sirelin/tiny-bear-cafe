package ee.taltech.cafegame.popups;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SmoothieMakingGame extends Window {

    private final Stage stage;
    private final int[] requiredKeySequence = {
        Input.Keys.S, Input.Keys.T, Input.Keys.R, Input.Keys.A, Input.Keys.W
    };

    private int keyPressCount = 0;
    private boolean orderCompleted = false;

    public SmoothieMakingGame(String orderText, Skin skin, OrderPopup.OrderAcceptedListener listener) {
        super("", skin);
        setWindowProperties();
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        Texture frameTexture = loadTexture("skin/taustvalge.png");
        Texture smoothieTexture = loadTexture("skin/smoothiegame.png");

        float scale = calculateScale(screenWidth, screenHeight, frameTexture.getWidth(), frameTexture.getHeight());
        float frameWidth = frameTexture.getWidth() * scale;
        float frameHeight = frameTexture.getHeight() * scale;

        setSize(frameWidth, frameHeight);
        setPosition((screenWidth - frameWidth) / 2f, (screenHeight - frameHeight) / 2f);

        buildUI(frameTexture, smoothieTexture, scale, frameWidth, frameHeight);
        this.stage = new Stage(new ScreenViewport());
        stage.addActor(this);

        setupInput(orderText, listener);
    }

    private void setWindowProperties() {
        setBackground((Drawable) null);
        setModal(true);
        setMovable(false);
        setResizable(false);
        setTouchable(Touchable.enabled);
    }

    private Texture loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    private float calculateScale(float screenWidth, float screenHeight, float texWidth, float texHeight) {
        float scaleX = screenWidth * 0.6f / texWidth;
        float scaleY = screenHeight * 0.6f / texHeight;
        return Math.min(scaleX, scaleY);
    }

    private void buildUI(Texture frameTex, Texture smoothieTex, float scale, float frameWidth, float frameHeight) {
        clearChildren();

        Image frameImage = new Image(new TextureRegionDrawable(new TextureRegion(frameTex)));
        frameImage.setSize(frameWidth, frameHeight);
        frameImage.setPosition(0, 0);
        frameImage.setTouchable(Touchable.disabled);
        addActor(frameImage);

        float smoothieWidth = smoothieTex.getWidth() * scale;
        float smoothieHeight = smoothieTex.getHeight() * scale;

        Image smoothieImage = new Image(new TextureRegionDrawable(new TextureRegion(smoothieTex)));
        smoothieImage.setSize(smoothieWidth, smoothieHeight);
        smoothieImage.setPosition((frameWidth - smoothieWidth) / 2f, (frameHeight - smoothieHeight) / 2f);
        smoothieImage.setTouchable(Touchable.disabled);
        addActor(smoothieImage);
    }

    private void setupInput(String orderText, OrderPopup.OrderAcceptedListener listener) {
        InputMultiplexer multiplexer = new InputMultiplexer();

        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (orderCompleted) return false;

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
