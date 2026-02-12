package ee.taltech.cafegame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ee.taltech.cafegame.ClientMain;

public class MenuScreen implements Screen {

    private final ClientMain game;
    private Stage stage;
    private final Viewport viewport;
    private SpriteBatch spriteBatch;
    private Skin skin;
    private TextureAtlas atlas;
    private Texture backgroundTexture;
    private Music backgroundMusic;

    public MenuScreen(ClientMain game) {
        this.game = game;
        this.spriteBatch = game.getBatch();
        this.backgroundTexture = new Texture("skin/kirjaproov3.png");
        viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport.apply();
        stage = new Stage(viewport);
    }

    @Override
    public void show() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("main_menu.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(game.getSoundVolume());
        backgroundMusic.play();

        atlas = new TextureAtlas(Gdx.files.internal("skin/menuSkin.atlas"));
        skin = new Skin(atlas);
        spriteBatch = new SpriteBatch();

        BitmapFont font = new BitmapFont(Gdx.files.internal("skin/bitfont.fnt"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, new Color(0.980f, 0.584f, 0.827f, 1f));

        Image background = new Image(new TextureRegion(backgroundTexture));
        background.setFillParent(true);
        stage.addActor(background);

        Label titleLabel = new Label("", labelStyle);
        Label subtitleLabel = new Label("<3", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = new Color(0.980f, 0.584f, 0.827f, 1f);
        buttonStyle.up = new TextureRegionDrawable(skin.getRegion("label"));
        buttonStyle.down = new TextureRegionDrawable(skin.getRegion("label"));

        TextButton startButton = new TextButton("Start", buttonStyle);
        TextButton exitButton = new TextButton("Exit", buttonStyle);
        TextButton settingsButton = new TextButton("Settings", buttonStyle);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (backgroundMusic != null) {
                    backgroundMusic.stop();
                    backgroundMusic.dispose();
                }
                IntroScreen introScreen = new IntroScreen(game); // ainult ClientMain
                game.setScreen(introScreen);
                dispose();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingScreen(game));
                //dispose();
            }
        });

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float buttonWidth = screenWidth * 0.1f;
        float buttonHeight = screenHeight * 0.07f;

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(titleLabel).padBottom(20).row();
        table.add(subtitleLabel).padBottom(20).row();
        table.add(startButton).size(buttonWidth, buttonHeight).padBottom(10).row();
        table.add(settingsButton).size(buttonWidth, buttonHeight).padBottom(10).row();
        table.add(exitButton).size(buttonWidth, buttonHeight).padBottom(10).row();

        stage.addActor(background);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override
    public void hide() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }


    @Override
    public void dispose() {
        stage.dispose();
        spriteBatch.dispose();
        atlas.dispose();
        skin.dispose();
        backgroundTexture.dispose();
        Gdx.input.setInputProcessor(null);
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
    }
}
