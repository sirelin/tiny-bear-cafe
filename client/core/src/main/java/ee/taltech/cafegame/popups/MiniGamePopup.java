package ee.taltech.cafegame.popups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ee.taltech.cafegame.ClientMain;

public abstract class MiniGamePopup implements Screen {
    protected Stage stage;
    protected Skin skin;

    protected ClientMain game;
    protected boolean completed = false;
    private TextureAtlas atlas;


    /**
     *
     * Basic struktuur minigamei popupi jaoks, peab edasi implemteerima ja disainima skini ja rakendama maini
     */
    public MiniGamePopup(ClientMain game) {
        this.game = game;
        this.stage = new Stage();
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                closePopup();
            }
        });
        table.add(closeButton);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);

    }

    public void closePopup() {
        if (completed) {
            onSuccess();
        } else {
            onFailure();
        }
    }

    public abstract void onSuccess();
    public abstract void onFailure();

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}
