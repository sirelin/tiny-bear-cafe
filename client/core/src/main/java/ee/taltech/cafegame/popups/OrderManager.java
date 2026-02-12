package ee.taltech.cafegame.popups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;

import java.util.*;

public class OrderManager {
    private static final int MAX_ORDERS = 10;

    private final Table orderTable;
    private final Table wrappedTable;
    private final Skin skin;
    private final Texture texture;
    private final TextureRegion[][] tiles;

    private float iconSize = 128f;

    private final LinkedList<String> orderQueue = new LinkedList<>();

    private final Stage uiStage;

    public OrderManager(Skin skin, Texture texture, Stage uiStage) {
        this.uiStage = uiStage;
        this.skin = skin;
        this.texture = texture;
        this.tiles = TextureRegion.split(texture, 16, 16);

        this.orderTable = new Table();
        orderTable.top().left();

        this.wrappedTable = new Table();
        updateWrapperSize();
    }

    public boolean tryAddOrder(String orderText) {
        if (orderQueue.size() >= MAX_ORDERS) {
            return false;
        }

        orderQueue.add(orderText);
        updateOrderTable();
        return true;
    }


    public void removeOrder(String orderText) {
        boolean removed = orderQueue.removeFirstOccurrence(orderText);

        if (removed) {
            System.out.println("Removed one '" + orderText + "' from queue.");
        } else {
            System.out.println("Nothing to remove for '" + orderText + "'.");
        }

        updateOrderTable();
    }

    public String getNextOrder() {
        return orderQueue.peek();
    }

    public List<String> getOrders() {
        return new ArrayList<>(orderQueue);
    }

    public void clearOrders() {
        orderQueue.clear();
        updateOrderTable();
    }

    public void setIconScale(int screenHeight) {
        float calculated = screenHeight / 18f;
        iconSize = Math.max(32f, Math.min(calculated, 128f));
    }

    public void updateIconSizes() {
        updateWrapperSize();
        updateOrderTable();
    }

    private void updateOrderTable() {
        orderTable.clearChildren();
        orderTable.padTop(10f);

        for (String orderText : orderQueue) {
            TextureRegion region = getTextureRegionForOrder(orderText);
            Image image = new Image(region);
            image.setScaling(Scaling.fit);
            orderTable.add(image).size(iconSize).left().pad(5).row();
        }

        orderTable.invalidateHierarchy();
    }

    private void updateWrapperSize() {
        wrappedTable.clearChildren();

        float oneIconHeight = iconSize + 8f;
        float targetHeight = oneIconHeight * 10f + 100f;
        float sidebarWidth = iconSize + 16f;

        Texture bgTexture = new Texture(Gdx.files.internal("skin/telliumus-taust.png"));
        Image background = new Image(new TextureRegion(bgTexture));
        background.setScaling(Scaling.stretch);

        Table stack = new Table();
        stack.stack(background, orderTable).expand().fill();

        wrappedTable.add(stack)
            .width(sidebarWidth)
            .height(targetHeight)
            .left()
            .padTop(10)
            .padLeft(10)
            .center();
    }

    private TextureRegion getTextureRegionForOrder(String orderText) {
        switch (orderText) {
            case "Coffee":
                Texture coffeTexture = new Texture(Gdx.files.internal("skin/kohviikoon.png"));
                return new TextureRegion(coffeTexture);
            case "Sandwich":
                Texture sandwichTexture = new Texture(Gdx.files.internal("skin/võikuikoon.png"));
                return new TextureRegion(sandwichTexture);
            case "Smoothie":
                Texture smoothieTexture = new Texture(Gdx.files.internal("skin/smuutiikoon.png"));
                return new TextureRegion(smoothieTexture);
            case "Cake":
                Texture cakeTexture = new Texture(Gdx.files.internal("skin/koogiikoon.png"));
                return new TextureRegion(cakeTexture);
            case "Salad":
                Texture saladTexture = new Texture(Gdx.files.internal("skin/salatiikoon.png"));
                return new TextureRegion(saladTexture);
            case "Egg":
                Texture eggTexture = new Texture(Gdx.files.internal("skin/munaikoon.png"));
                return new TextureRegion(eggTexture);
            default:
                return tiles[0][0];
        }
    }

    public Table getView() {
        return wrappedTable;
    }
}
