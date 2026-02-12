package ee.taltech.cafegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

public class MoneyBar {

    private float money = 0f;
    private float goal;

    private final Label label;
    private final Table container;
    private final Image fill;
    private final Group barGroup;

    private final float originalWidth;
    private final float originalHeight;

    private float scaleRatio = 1f;
    private final float fillXUnscaled = 18f;
    private final float fillYUnscaled = 0f;
    private float fillHeightUnscaled;
    private float scaledFillHeight;

    private boolean moneyStarted = false;

    public MoneyBar(Skin skin, float goal) {
        this.goal = goal;

        Texture bgTexture = loadTexture("skin/tellimused-taust2.png");
        Texture barTexture = loadTexture("skin/taust-lilled3.png");
        Texture fillTexture = loadTexture("skin/1pixeltaide.png");

        originalWidth = barTexture.getWidth();
        originalHeight = barTexture.getHeight();
        fillHeightUnscaled = originalHeight;

        fill = createFillImage(fillTexture);
        barGroup = createBarGroup(barTexture);
        label = createLabel(skin);

        Stack stackedUI = createStackedUI(bgTexture, label, barGroup);
        container = createContainer(stackedUI);
    }

    private Texture loadTexture(String path) {
        return new Texture(Gdx.files.internal(path));
    }

    private Image createFillImage(Texture fillTexture) {
        Image img = new Image(new TextureRegionDrawable(new TextureRegion(fillTexture)));
        img.setScaling(Scaling.stretch);
        img.setSize(0, fillHeightUnscaled);
        return img;
    }

    private Group createBarGroup(Texture barTexture) {
        Group group = new Group();
        Image barBackground = new Image(new TextureRegionDrawable(new TextureRegion(barTexture)));
        group.addActor(barBackground);
        group.addActor(fill);
        group.setSize(originalWidth, originalHeight);
        return group;
    }

    private Label createLabel(Skin skin) {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("font");
        labelStyle.fontColor = new Color(0.988f, 0.514f, 0.882f, 1f);
        return new Label(getMoneyText(), labelStyle);
    }

    private Stack createStackedUI(Texture bgTexture, Label label, Group barGroup) {
        Image bgImage = new Image(new TextureRegionDrawable(new TextureRegion(bgTexture)));

        Table content = new Table();
        content.top().left().pad(5);
        content.add(label).padRight(10);
        content.add(barGroup).width(originalWidth).height(originalHeight);

        Stack stack = new Stack();
        stack.add(bgImage);
        stack.add(content);
        return stack;
    }

    private Table createContainer(Stack stack) {
        Table table = new Table();
        table.top().left().pad(5);
        table.add(stack);
        table.setVisible(false);
        return table;
    }

    public void addMoney(float amount) {
        money += amount;

        if (!moneyStarted) {
            moneyStarted = true;
            container.setVisible(true);
        }

        updateFill();
        updateLabel();
    }

    private void updateFill() {
        float maxFillWidth = (originalWidth - fillXUnscaled * 2) * scaleRatio;
        float progress = Math.min(money / goal, 1f);
        float width = maxFillWidth * progress;
        float x = fillXUnscaled * scaleRatio;
        float y = fillYUnscaled * scaleRatio;

        fill.setWidth(width);
        fill.setPosition(x, y);
    }

    private void updateLabel() {
        label.setText(getMoneyText());
    }

    public void setGoal(float goal) {
        this.goal = goal;
        updateLabel();
        updateFill();
    }

    public void setScaleByScreen(int screenWidth, int screenHeight) {
        scaleRatio = Math.min(Math.max(screenWidth / 1600f, 0.75f), 1f);

        float fontScale = (originalHeight * scaleRatio) / 22f;
        label.setFontScale(fontScale);

        scaledFillHeight = fillHeightUnscaled * scaleRatio;
        fill.setHeight(scaledFillHeight);

        barGroup.setSize(originalWidth * scaleRatio, originalHeight * scaleRatio);
        updateFill();
    }

    public Actor getView() {
        return container;
    }

    public float getMoney() {
        return money;
    }

    public float getGoal() {
        return goal;
    }

    private String getMoneyText() {
        return "$" + (int) money + " / $" + (int) goal;
    }
}
