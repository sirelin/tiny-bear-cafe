package ee.taltech.cafegame.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ee.taltech.cafegame.ClientMain;

public class SettingScreen implements Screen {

    private final ClientMain game;
    private final Stage stage;
    private final Viewport viewport;
    private final Texture background;
    private SpriteBatch spriteBatch;

    private Skin skin, skin2;
    private TextureAtlas atlas, atlas2;
    private Music backgroundMusic;

    public SettingScreen(ClientMain game) {
        this.game = game;
        this.background = new Texture("skin/settingstaust.png");
        this.viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.stage = new Stage(viewport);
    }

    @Override
    public void show() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("main_menu.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(game.getSoundVolume());
        backgroundMusic.play();

        spriteBatch = new SpriteBatch();
        loadSkins();

        TextureRegionDrawable sliderBg = new TextureRegionDrawable(new TextureRegion(new Texture("skin/taide3.png")));
        TextureRegionDrawable knobDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("skin/slider-knob.png")));
        knobDrawable.setMinSize(100, 100);

        Slider volumeSlider = initSlider(sliderBg, knobDrawable);
        TextButton backButton = initBackButton();

        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("skin/bitfont.fnt")), new Color(0.980f, 0.584f, 0.827f, 1f));
        Label titleLabel = new Label("Settings", labelStyle);
        Label volumeLabel = new Label("Music", labelStyle);

        Image bg = new Image(background);
        bg.setFillParent(true);
        bg.setTouchable(Touchable.disabled);
        stage.addActor(bg);

        initLayout(titleLabel, volumeLabel, volumeSlider, backButton);
        Gdx.input.setInputProcessor(stage);
    }

    private void loadSkins() {
        atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
        skin = new Skin(atlas);

        atlas2 = new TextureAtlas(Gdx.files.internal("skin/menuSkin.atlas"));
        skin2 = new Skin(atlas2);
    }

    private Slider initSlider(Drawable background, Drawable knob) {
        Slider.SliderStyle style = new Slider.SliderStyle(background, knob);
        Slider slider = new Slider(0f, 1f, 0.1f, false, style);
        slider.setValue(game.getSoundVolume());
        slider.setSize(300, 100);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = slider.getValue();
                game.setSoundVolume(volume);
                if (backgroundMusic != null) backgroundMusic.setVolume(volume);
            }
        });

        return slider;
    }

    private TextButton initBackButton() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = new BitmapFont(Gdx.files.internal("skin/bitfont.fnt"));
        style.fontColor = new Color(0.980f, 0.584f, 0.827f, 1f);
        style.up = new TextureRegionDrawable(skin2.getRegion("label"));
        style.down = new TextureRegionDrawable(skin2.getRegion("label"));

        TextButton button = new TextButton("Back", style);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        return button;
    }

    private void initLayout(Label titleLabel, Label volumeLabel, Slider volumeSlider, TextButton backButton) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        Table layout = new Table();
        layout.setFillParent(true);
        layout.center();
        stage.addActor(layout);

        Table volumeRow = new Table();
        volumeRow.add(volumeLabel).right().padRight(10);
        volumeRow.add(volumeSlider).left().width(300);

        layout.add(titleLabel).padBottom(40).row();
        layout.add(createSkinSelectorTable()).padBottom(30).row();
        layout.add(volumeRow).padBottom(20).row();
        layout.add(backButton).size(screenWidth * 0.2f, screenHeight * 0.08f).padTop(20).center().row();
    }

    public Table createSkinSelectorTable() {
        String[] skinNames = {"player", "redpanda", "purple", "yellow", "panda", "red", "green", "blue", "white", "pink"};
        String[] skinPaths = {
            "pruunkaru.png", "redpanda.png", "lillajaakaru.png", "kollanekaru.png", "panda.png",
            "punanekaru.png", "rohelinekaru.png", "sininekaru.png", "valgekaru.png", "roosakaru.png"
        };

        Table skinTable = new Table();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.7f, 1f, 0.7f, 0.5f);
        pixmap.fill();
        Drawable selectedBg = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        ButtonGroup<ImageButton> group = new ButtonGroup<>();
        group.setMaxCheckCount(1);
        group.setMinCheckCount(1);
        group.setUncheckLast(true);

        for (int i = 0; i < skinNames.length; i++) {
            final String skinPath = skinPaths[i];
            Texture original = new Texture(Gdx.files.internal(skinPath));
            Texture resized = resizeTexture(Gdx.files.internal(skinPath), 70, 70);

            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
            style.imageUp = new TextureRegionDrawable(new TextureRegion(resized));
            style.imageChecked = style.imageUp;
            style.checked = selectedBg;

            ImageButton button = new ImageButton(style);
            button.setSize(110, 110);
            button.getImage().setSize(95, 95);
            button.getImage().setScaling(Scaling.none);
            group.add(button);

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setCurrentPlayerTexture(original);
                }
            });

            skinTable.add(button).size(110, 110).pad(8);
            if (i == (skinNames.length / 2) - 1) skinTable.row();
        }

        for (ImageButton btn : group.getButtons()) {
            TextureRegionDrawable drawable = (TextureRegionDrawable) btn.getStyle().imageUp;
            if (drawable.getRegion().getTexture() == game.getCurrentPlayerTexture()) {
                btn.setChecked(true);
                break;
            }
        }

        return skinTable;
    }

    private Texture resizeTexture(FileHandle fileHandle, int width, int height) {
        Pixmap original = new Pixmap(fileHandle);
        Pixmap resized = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        resized.setFilter(Pixmap.Filter.BiLinear);
        resized.drawPixmap(original, 0, 0, original.getWidth(), original.getHeight(), 0, 0, width, height);
        Texture texture = new Texture(resized);
        original.dispose();
        resized.dispose();
        return texture;
    }

    @Override public void render(float delta) {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override public void hide() {
        if (backgroundMusic != null) backgroundMusic.stop();
    }

    @Override public void dispose() {
        stage.dispose();
        spriteBatch.dispose();
        atlas.dispose();
        skin.dispose();
        atlas2.dispose();
        skin2.dispose();
        background.dispose();
        Gdx.input.setInputProcessor(null);
        if (backgroundMusic != null) backgroundMusic.dispose();
    }
}
