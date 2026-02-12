package ee.taltech.cafegame.popups;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;

public class OrderPopup extends Window {

    public interface OrderAcceptedListener {
        boolean onOrderAccepted(String orderText);
    }

    private final Stage stage;
    private boolean accepted = false;

    public OrderPopup(String orderText, Skin skin, Stage stage, OrderAcceptedListener listener) {
        super("", skin);
        this.stage = stage;

        setWindowProperties();
        float popupWidth = Gdx.graphics.getWidth() * 0.15f;
        float popupHeight = Gdx.graphics.getHeight() * 0.5f;

        setSize(popupWidth, popupHeight);
        centerOnScreen(popupWidth, popupHeight);

        Texture bgTexture = new Texture(Gdx.files.internal("skin/pinknote.png"));
        Image background = createBackground(bgTexture, popupWidth, popupHeight);
        Label orderLabel = createLabel(orderText, popupHeight, popupWidth);

        clearChildren();
        addActor(background);
        addActor(orderLabel);

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

    private void centerOnScreen(float width, float height) {
        setPosition(
            (Gdx.graphics.getWidth() - width) / 2f,
            (Gdx.graphics.getHeight() - height) / 2f
        );
    }

    private Image createBackground(Texture texture, float width, float height) {
        Image bg = new Image(new TextureRegionDrawable(texture));
        bg.setSize(width, height);
        bg.setPosition(0, 0);
        bg.setTouchable(Touchable.disabled);
        return bg;
    }

    private Label createLabel(String text, float popupHeight, float popupWidth) {
        BitmapFont font = new BitmapFont(Gdx.files.internal("skin/bitfont.fnt"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, new Color(84 / 255f, 84 / 255f, 84 / 255f, 1f));
        Label label = new Label(text, labelStyle);

        float fontScale = MathUtils.clamp(popupHeight / 400f, 0.4f, 1.5f);
        label.setFontScale(fontScale);
        label.setPosition(popupWidth * 0.2f, popupHeight * 0.5f);
        return label;
    }

    private void setupInput(String orderText, OrderAcceptedListener listener) {
        InputMultiplexer multiplexer = new InputMultiplexer();

        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.A && !accepted) {
                    accepted = true;
                    remove();

                    boolean success = listener.onOrderAccepted(orderText);
                    if (!success) {
                        Gdx.app.postRunnable(() -> {
                            LimitPopup limitPopup = new LimitPopup("skin/limitpopup.png");
                            limitPopup.toFront();
                            stage.addActor(limitPopup);
                        });
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
}
