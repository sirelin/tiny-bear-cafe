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

public class VictoryScreen implements Screen {

    private final ClientMain game;
    private final Viewport viewport;
    private final SpriteBatch spriteBatch;

    private final MenuScreen menuScreen;
    private Texture[] outroTextures;
    private int currentIndex = 0;
    private boolean transitionTriggered = false;
    private Music backgroundMusic;

    public VictoryScreen(ClientMain game, MenuScreen menuScreen) {
        this.game = game;
        this.menuScreen = menuScreen;
        this.spriteBatch = game.getBatch();
        this.outroTextures = new Texture[]{
            new Texture("skin/outro1.png"),
            new Texture("skin/outro2.png"),
            new Texture("skin/outro3.png")
        };
        viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport.apply();
    }

    @Override
    public void show() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("game_music.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.3f);
        backgroundMusic.play();
        Gdx.input.setInputProcessor(null); // ei kasuta stage'i
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (currentIndex < outroTextures.length - 1) {
                currentIndex++;
            } else if (!transitionTriggered) {
                transitionTriggered = true; // Et vältida mitmekordset vahetust
                game.setScreen(menuScreen);
                dispose(); // vabastame ressursid
                return; // väldime edasist joonistamist
            }
        }

        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        spriteBatch.draw(outroTextures[currentIndex], 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
        for (Texture texture : outroTextures) {
            if (texture != null) texture.dispose();
        }
    }
}
