package ee.taltech.cafegame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ee.taltech.cafegame.ClientMain;

public class IntroScreen implements Screen {

    private final ClientMain game;
    private final Viewport viewport;
    private final SpriteBatch spriteBatch;

    private final Texture[] introTextures;
    private int currentIndex = 0;
    private boolean transitionTriggered = false;
    private Music backgroundMusic;

    public IntroScreen(ClientMain game) {
        this.game = game;
        this.spriteBatch = game.getBatch();

        this.introTextures = new Texture[8];
        for (int i = 0; i < 8; i++) {
            introTextures[i] = new Texture("skin/intro" + (i + 1) + ".png");
        }

        viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport.apply();
    }

    @Override
    public void show() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("main_menu.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(game.getSoundVolume());
        backgroundMusic.play();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (currentIndex < introTextures.length - 1) {
                currentIndex++;
            } else if (!transitionTriggered) {
                transitionTriggered = true;
                game.setScreen(new PlayScreen(game));
                dispose();
                if (backgroundMusic != null) {
                    backgroundMusic.stop();
                    backgroundMusic.dispose();
                }
                return;
            }
        }

        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        spriteBatch.draw(introTextures[currentIndex], 0, 0,
            viewport.getWorldWidth(), viewport.getWorldHeight());
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
        for (Texture texture : introTextures) {
            if (texture != null) texture.dispose();
        }
    }
}
