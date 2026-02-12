package ee.taltech.cafegame.popups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CakeMakingGame extends Window {

    private final Stage stage;
    private boolean orderCompleted = false;
    private int keyPressCount = 0;

    private static final int[] REQUIRED_KEY_SEQUENCE = {
        Input.Keys.C, Input.Keys.A, Input.Keys.K, Input.Keys.E
    };

    public CakeMakingGame(String orderText, Skin skin, OrderPopup.OrderAcceptedListener listener) {
        super("", skin);
        setBackground((Drawable) null);
        setModal(true);
        setMovable(false);
        setResizable(false);
        setTouchable(Touchable.enabled);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        Texture frameTexture = loadTexture("skin/taustvalge.png");
        Texture gameTexture = loadTexture("skin/cakegame.png");

        float scale = calculateScale(screenWidth, screenHeight, frameTexture);
        float frameWidth = frameTexture.getWidth() * scale;
        float frameHeight = frameTexture.getHeight() * scale;

        setSize(frameWidth, frameHeight);
        setPosition((screenWidth - frameWidth) / 2f, (screenHeight - frameHeight) / 2f);

        Image frameImage = createImage(frameTexture, frameWidth, frameHeight, 0, 0);
        Image gameImage = createImage(
            gameTexture,
            gameTexture.getWidth() * scale,
            gameTexture.getHeight() * scale,
            (frameWidth - gameTexture.getWidth() * scale) / 2f,
            (frameHeight - gameTexture.getHeight() * scale) / 2f
        );

        clearChildren();
        addActor(frameImage);
        addActor(gameImage);

        this.stage = new Stage(new ScreenViewport());
        stage.addActor(this);

        setupInput(orderText, listener);
    }

    private void setupInput(String orderText, OrderPopup.OrderAcceptedListener listener) {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!orderCompleted && keycode == REQUIRED_KEY_SEQUENCE[keyPressCount]) {
                    keyPressCount++;
                    if (keyPressCount == REQUIRED_KEY_SEQUENCE.length) {
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

    private Texture loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    private Image createImage(Texture texture, float width, float height, float x, float y) {
        Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        image.setSize(width, height);
        image.setPosition(x, y);
        image.setTouchable(Touchable.disabled);
        return image;
    }

    private float calculateScale(float screenWidth, float screenHeight, Texture baseTexture) {
        float maxWidth = screenWidth * 0.6f;
        float maxHeight = screenHeight * 0.6f;
        float scaleX = maxWidth / baseTexture.getWidth();
        float scaleY = maxHeight / baseTexture.getHeight();
        return Math.min(scaleX, scaleY);
    }

    public Stage getStage() {
        return stage;
    }

    public boolean getOrderCompleted() {
        return orderCompleted;
    }
}
