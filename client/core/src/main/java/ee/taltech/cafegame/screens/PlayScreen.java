package ee.taltech.cafegame.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Client;
import ee.taltech.cafegame.ClientMain;
import ee.taltech.cafegame.MoneyBar;
import ee.taltech.cafegame.ai.NPC;
import ee.taltech.cafegame.bubble.MoneyBubble;
import ee.taltech.cafegame.bubble.NpcBubble;
import ee.taltech.cafegame.interactables.Interactables;
import ee.taltech.cafegame.packets.PacketNpcOrderTaken;
import ee.taltech.cafegame.packets.PacketRemoveNpcMessage;
import ee.taltech.cafegame.packets.PacketRequestNPC;
import ee.taltech.cafegame.player.Player;
import ee.taltech.cafegame.player.PlayerController;
import ee.taltech.cafegame.popups.*;
import ee.taltech.cafegame.utils.Box2dWorldGenerator;
import ee.taltech.cafegame.utils.TiledMapLoader;

import java.util.*;
import java.util.List;


public class PlayScreen implements Screen {
    private static final float TILE_SIZE = 16;
    private final ClientMain game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private float mapWidth;
    private float mapHeight;
    public SpriteBatch batch;
    private Texture playerTexture;
    private Texture otherTexture;
    //private final Texture npcTexture;
    private Texture moneyBubble;
    private Player player;
    private Client client;
    private Map<Integer, Player> players;
    public Map<Integer, NPC> aiNPCs;
    private PlayerController playerController;

    public World getWorld() {
        return world;
    }

    private World world;
    private Map<Vector2, Interactables> interactables;
    public Box2dWorldGenerator worldGenerator;
    public static final float PPM = 100f;

    private OrderVisual activeOrderVisual = null; // Order visual for the player
    private String currentOrderString = null;
    private Player activePlayer = null; // The player who made the order
    private boolean isGamePopupOpen = false;

    private int pendingMoneyReward = 0;
    private boolean hasOrderToDeliver = false;

    public Texture interactableIcon;
    private Vector2 interactableIconPosition;
    private boolean showInteractableIcon = false;

    private Stage uiStage;
    private Skin skin;
    private OrderManager orderManager;
    private final Map<Integer, PlayerController> playerControllers = new HashMap<>();

    public Texture waitingOrderTexture;
    private MoneyBar moneyBar;

    private final List<String> orderTypes = Arrays.asList("Coffee", "Sandwich", "Smoothie", "Cake", "Salad", "Egg");
    private int orderIndex = 0;

    private final Map<String, Texture> orderTextures = new HashMap<>();

    private LimitPopup activeLimitPopup = null;
    private Music backgroundMusic;

    private TutorialPopup tutorialPopup;
    private boolean tutorialVisible = false;

    private NPC npcToRemove;

    Integer orderCount = 0;      //et oleks automaatselt mängu alguses olemas esimesel 3 orderil

    public PlayScreen(ClientMain game) {
        this.game = game;

        initWorldAndUI();
        initOrderSystem();
        initTutorialPopup();
        initHintLabels();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void initWorldAndUI() {
        camera = new OrthographicCamera();
        float worldWidth = 16 * 20 / PPM;
        float worldHeight = 16 * 15 / PPM;
        viewport = new ExtendViewport(worldWidth, worldHeight, camera);

        TiledMapLoader mapLoader = new TiledMapLoader("map assets/cafemap.tmx");
        map = mapLoader.setupMap().getMap();
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / PPM);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        this.batch = game.getBatch();
        this.player = game.getPlayer();
        this.client = game.getClient();
        this.players = game.getPlayers();
        this.playerTexture = game.getCurrentPlayerTexture();
        this.otherTexture = new Texture("redpanda.png");
        this.world = new World(new Vector2(0, 0), true);
        this.skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        this.uiStage = new Stage(new ScreenViewport());

        interactableIcon = new Texture(Gdx.files.internal("skin/nool1.png"));
        moneyBubble = new Texture(Gdx.files.internal("skin/money.png"));

        worldGenerator = new Box2dWorldGenerator(world, map, game);
        this.interactables = Box2dWorldGenerator.getInteractables();
        this.playerController = new PlayerController(player, client, interactables);
        playerControllers.put(player.getId(), playerController);
        float spawnX = 420 / PPM;
        float spawnY = 241 / PPM;
        playerController.createBody(world, spawnX, spawnY);

        camera.zoom = 1.5f;
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(playerController);
        Gdx.input.setInputProcessor(multiplexer);

        this.aiNPCs = game.getNPCs();
        game.createNpcBodiesIfNeeded(world);
        waitingOrderTexture = new Texture("skin/neworder.png");

        for (NPC npc : aiNPCs.values()) {
            npc.setWaitingTexture(waitingOrderTexture);
        }

        moneyBar = new MoneyBar(skin, 100f);
        moneyBar.addMoney(0f);

        Table moneyTable = new Table();
        moneyTable.setFillParent(true);
        moneyTable.top().right();
        moneyTable.add(moneyBar.getView());
        uiStage.addActor(moneyTable);

        this.mapWidth = map.getProperties().get("width", Integer.class) * TILE_SIZE;
        this.mapHeight = map.getProperties().get("height", Integer.class) * TILE_SIZE;

    }

    private void initOrderSystem() {
        orderTextures.put("Coffee", new Texture("skin/coffeeorder.png"));
        orderTextures.put("Sandwich", new Texture("skin/sandwichorder.png"));
        orderTextures.put("Smoothie", new Texture("skin/smoothieorder.png"));
        orderTextures.put("Cake", new Texture("skin/cakeorder.png"));
        orderTextures.put("Salad", new Texture("skin/saladorder.png"));
        orderTextures.put("Egg", new Texture("skin/eggorder.png"));

        Texture orderTexture = new Texture(Gdx.files.internal("skin/TopDownHouse_SmallItems.png"));
        orderManager = new OrderManager(skin, orderTexture, uiStage);
        Table ordersView = orderManager.getView();
        ordersView.setFillParent(true);
        ordersView.top().left();
        uiStage.addActor(ordersView);
    }

    private void initTutorialPopup() {
        tutorialPopup = new TutorialPopup("skin/uusopetus.png");
        tutorialPopup.setVisible(false);
        uiStage.addActor(tutorialPopup);
    }

    private void initHintLabels() {
        Label escLabel = new Label("Press ESC to exit game", skin);
        Table escBox = new Table();
        escBox.setBackground(skin.newDrawable("round-white", new Color(1f, 1f, 1f, 0.5f)));
        escBox.add(escLabel).pad(6);
        escBox.pack();

        Label qLabel = new Label("Press Q to open the tutorial", skin);
        Table qBox = new Table();
        qBox.setBackground(skin.newDrawable("round-white", new Color(1f, 1f, 1f, 0.5f)));
        qBox.add(qLabel).pad(6);
        qBox.pack();

        Table wrapper = new Table();
        wrapper.setFillParent(true);
        wrapper.bottom().right().padBottom(10).padRight(10);
        wrapper.add(escBox).right().padBottom(5).row();
        wrapper.add(qBox).right();

        uiStage.addActor(wrapper);
    }

    @Override
    public void show() {
        try {
            // Load the internal music file using a stream
            FileHandle internalFile = Gdx.files.internal("game_music.mp3");

            // Create a temp file and copy the data into it
            File temp = File.createTempFile("game_music_", ".mp3");
            temp.deleteOnExit();

            // Copy the stream content into the temp file
            try (InputStream in = internalFile.read();
                 OutputStream out = new FileOutputStream(temp)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            // Now load the copied temp file
            FileHandle tempHandle = Gdx.files.absolute(temp.getAbsolutePath());
            backgroundMusic = Gdx.audio.newMusic(tempHandle);
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(game.getSoundVolume());
            backgroundMusic.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(float dt) {
        playerController.update(this.interactables);

        Vector2 pos = playerController.getBody().getPosition();
        player.setX(pos.x);
        player.setY(pos.y);
        for (NPC npc : aiNPCs.values()) {
            npc.update(dt);
        }
        for (NPC npc : aiNPCs.values()) {
            if (npc.getBody() == null) {
                npc.createBody(world);  // Correct position scaling;
            }
        }

        float halfWidth = camera.viewportWidth * camera.zoom / 2f;
        float halfHeight = camera.viewportHeight * camera.zoom / 2f;

        float mapWorldWidth = mapWidth / PPM;
        float mapWorldHeight = mapHeight / PPM;

        float camX = MathUtils.clamp(player.getX(), halfWidth, mapWorldWidth - halfWidth);
        float camY = MathUtils.clamp(player.getY(), halfHeight, mapWorldHeight - halfHeight);

        camera.position.set(camX, camY, 0);
        camera.update();

        camera.position.set(camX, camY, 0);
        world.step(1 / 60f, 6, 2);
        camera.update();
        mapRenderer.setView(camera);
        if (activeOrderVisual != null && activePlayer != null) {
            Vector2 playerPosition = playerController.getBody().getPosition();
            float offsetX = -5;
            float offsetY = -9;
            Vector2 playerScreenPos = viewport.project(new Vector2(playerPosition.x, playerPosition.y));
            float visualX = playerScreenPos.x + offsetX;
            float visualY = playerScreenPos.y + offsetY;
            activeOrderVisual.setPosition(visualX, visualY);

        }
    }

    /**
     * Renders the map and player collision with map border
     * tähtis järjekord!!!!
     * update
     * handle input
     * puhastab ekrrani
     * uuendab viewport ja kaamera
     * seab mapi
     * renderdab füüsikalise maailma
     * algab batch
     * player
     * teised playerid
     * npcd
     * lõppeb batch
     * uiskinid
     */
    @Override
    public void render(float delta) {
        update(delta);
        handleInput();

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        viewport.apply();

        mapRenderer.setView(camera);
        mapRenderer.render();

        //b2dr.render(world, camera.combined);

        game.getBatch().setProjectionMatrix(camera.combined);
        this.batch.begin();

        if (showInteractableIcon && interactableIconPosition != null) {
            float iconSize = 16 / PPM;
            batch.draw(interactableIcon,
                interactableIconPosition.x - iconSize / 2,
                interactableIconPosition.y,
                iconSize,
                iconSize);
        }

        renderNPCs();
        drawPlayer();
        drawOtherPlayers();

        this.batch.end();

        uiStage.act(delta);
        uiStage.draw();
    }

    private void renderNPCs() {
        for (NPC npc : aiNPCs.values()) {
            if (npc != null) {

                npc.update(1 / 60f);
                npc.draw(batch);

                Texture waitingTexture = npc.getWaitingTexture();
                if (waitingTexture != null && npc.getBody() != null) {
                    Vector2 pos = npc.getBody().getPosition();
                    float iconSize = 16 / PPM;
                    float offsetY = 10 / PPM;
                    batch.draw(waitingTexture,
                        pos.x - iconSize / 2,
                        pos.y + offsetY,
                        iconSize,
                        iconSize);
                }

                Texture orderTexture = npc.getOrderTexture();
                if (orderTexture != null && npc.getBody() != null) {
                    Vector2 pos = npc.getBody().getPosition();
                    float iconSize = 16 / PPM;
                    float offsetY = 10 / PPM; // offset above the NPC's head

                    batch.draw(orderTexture,
                        pos.x - iconSize / 2,
                        pos.y + offsetY,
                        iconSize,
                        iconSize);
                }
                if (!npc.canTakeOrder() && npc.canTakeOrderTimestamp > 0) {
                    long elapsed = System.currentTimeMillis() - npc.canTakeOrderTimestamp;
                    System.out.println("time:" + elapsed);
                    if (elapsed >= 60 * 1000) { // 2 minutes
                        PacketRemoveNpcMessage packetRemoveNpcMessage = new PacketRemoveNpcMessage(npc.getId());
                        client.sendTCP(packetRemoveNpcMessage);
                    }
                }
            }
        }
    }


    private void drawPlayer() {
        float playerWidth = 32f / PPM;
        float playerHeight = 32f / PPM;
        Vector2 pos = playerController.getBody().getPosition();
        float verticalOffset = 8 / PlayScreen.PPM;
        float drawX = pos.x - (16 / PlayScreen.PPM);
        float drawY = pos.y - (16 / PlayScreen.PPM) + verticalOffset;
        batch.draw(playerTexture, drawX, drawY, playerWidth, playerHeight);
    }

    private void ensurePlayerControllerExists(Player p) {
        if (!playerControllers.containsKey(p.getId())) {
            PlayerController remotePlayerController = new PlayerController(p, client, interactables);
            playerControllers.put(p.getId(), remotePlayerController);
            float spawnX = p.getX() / PlayScreen.PPM;
            float spawnY = p.getY() / PlayScreen.PPM;
            remotePlayerController.createBody(world, spawnX, spawnY);
        }
    }

    private void drawOtherPlayers() {
        float playerWidth = 32f / PPM;
        float playerHeight = 32f / PPM;
        for (Player p : players.values()) {
            if (p.getId() == player.getId()) continue;
            ensurePlayerControllerExists(p);
            PlayerController remotePlayerController = playerControllers.get(p.getId());
            if (remotePlayerController != null) {
                Body remoteBody = remotePlayerController.getBody();
                if (remoteBody != null) {
                    Vector2 bodyPosition = remoteBody.getPosition();
                    float verticalOffset = 8 / PlayScreen.PPM;
                    float drawX = bodyPosition.x - (playerWidth / 2);
                    float drawY = bodyPosition.y - (playerHeight / 2) + verticalOffset;
                    remoteBody.setTransform(p.getX(), p.getY(), 0);
                    batch.draw(otherTexture, drawX, drawY, playerWidth, playerHeight);
                }
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply(true);
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
        camera.update();
        if (uiStage != null) {
            uiStage.getViewport().update(width, height, true);
            uiStage.getViewport().apply();
        }
        orderManager.setIconScale(height);
        orderManager.updateIconSizes();
        moneyBar.setScaleByScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override
    public void hide() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
    }


    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        batch.dispose();
        //npcTexture.dispose();
        playerTexture.dispose();
        otherTexture.dispose();
        waitingOrderTexture.dispose();
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
    }

    public void safelyRemoveNPC(int npcId) {
        Gdx.app.postRunnable(() -> {
            System.out.println("safely remove npc");
            NPC npc = aiNPCs.get(npcId);
            if (npc != null) {
                System.out.println("not null");
                System.out.println("destroy textures");
                if (npc.getOrderTexture() != null) {
                    npc.setOrderTexture(null);
                }
                if (npc.getWaitingTexture() != null) {
                    npc.setWaitingTexture(null);
                }
                aiNPCs.remove(npcId);
                npc.setBody(null);
            }
        });
    }

    private boolean isPlayerNear(Vector2 targetPos, float radius) {
        Vector2 playerPos = playerController.getBody().getPosition();
        return playerPos.dst(targetPos) <= radius;
    }


    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (moneyBar.getMoney() > 100f) {
            game.setScreen(new VictoryScreen(game, new MenuScreen(game)));
        }

        if (orderCount <= 3 && !hasOrderToDeliver) {
            showInteractableIcon = false;
            interactableIconPosition = null;

            String currentOrder = orderManager.getNextOrder();
            if (currentOrder != null) {
                for (Interactables interactable : interactables.values()) {
                    if (isPlayerNear(interactable.getBody().getPosition(), 3f)) {
                        String interactableType = interactable.getClass().getSimpleName();

                        boolean isMatching =
                            (currentOrder.equals("Coffee") && interactableType.equals("CoffeeMachine")) ||
                                (currentOrder.equals("Sandwich") && interactableType.equals("CuttingBoard")) ||
                                (currentOrder.equals("Smoothie") && interactableType.equals("Fridge")) ||
                                (currentOrder.equals("Cake") && interactableType.equals("Stove")) ||
                                (currentOrder.equals("Salad") && interactableType.equals("Table")) ||
                                (currentOrder.equals("Egg") && interactableType.equals("Stove"));

                        if (isMatching) {
                            Vector2 pos = interactable.getBody().getPosition();
                            interactableIconPosition = new Vector2(pos.x, pos.y + 0.1f);
                            showInteractableIcon = true;
                            break;
                        }
                    }
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            showInteractableIcon = false;
            interactableIconPosition = null;

            String currentOrder = orderManager.getNextOrder();
            if (currentOrder == null) return;

            for (Interactables interactable : interactables.values()) {
                if (isPlayerNear(interactable.getBody().getPosition(), 3f)) {
                    String interactableType = interactable.getClass().getSimpleName();

                    boolean isMatching =
                        (currentOrder.equals("Coffee") && interactableType.equals("CoffeeMachine")) ||
                            (currentOrder.equals("Sandwich") && interactableType.equals("CuttingBoard")) ||
                            (currentOrder.equals("Smoothie") && interactableType.equals("Fridge")) ||
                            (currentOrder.equals("Cake") && interactableType.equals("Stove")) ||
                            (currentOrder.equals("Salad") && interactableType.equals("Table")) ||
                            (currentOrder.equals("Egg") && interactableType.equals("Stove"));

                    if (isMatching) {
                        Vector2 pos = interactable.getBody().getPosition();
                        interactableIconPosition = new Vector2(pos.x, pos.y + 0.1f);
                        showInteractableIcon = true;
                        break;
                    }
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (activeLimitPopup != null && activeLimitPopup.hasParent()) {
                activeLimitPopup.remove();
                activeLimitPopup = null;
                return;
            }

            boolean popupOpen = false;
            for (Actor actor : uiStage.getActors()) {
                if (actor instanceof Window && actor.isVisible()) {
                    popupOpen = true;
                    break;
                }
            }

            if (!popupOpen) {
                for (NPC npc : aiNPCs.values()) {
                    if (isPlayerNear(npc.getBody().getPosition(), 0.5f)
                        && npc.getGivenOrder() == null
                        && npc.getWaitingTexture() != null
                        && npc.canTakeOrder()) {

                        npc.setWaitingTexture(null);
                        client.sendTCP(new PacketNpcOrderTaken(npc.getId()));

                        Random random = new Random();
                        String order = orderTypes.get(random.nextInt(orderTypes.size()));
                        npc.setGivenOrder(order);
                        npc.setOrderTexture(orderTextures.get(order));

                        OrderPopup popup = new OrderPopup(
                            order,
                            skin,
                            uiStage,
                            acceptedOrder -> orderManager.tryAddOrder(acceptedOrder)
                        );

                        popup.toFront();
                        uiStage.addActor(popup);
                        break;
                    }
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            this.npcToRemove = null;
            boolean nearNpc = false;
            for (NPC npc : aiNPCs.values()) {
                if (isPlayerNear(npc.getBody().getPosition(), 0.5f)
                    && Objects.equals(npc.getGivenOrder(), currentOrderString)) {
                    nearNpc = true;
                    npc.setGivenOrder(null);
                    npc.setOrderTexture(null);
                    npcHeartTexture(npc);
                    npcToRemove = npc;
                    break;
                }
            }

            if (activeOrderVisual != null && nearNpc && hasOrderToDeliver) {
                moneyBar.addMoney(pendingMoneyReward);
                moneyBubbleTexture();
                pendingMoneyReward = 0;
                hasOrderToDeliver = false;

                activeOrderVisual.remove();
                activeOrderVisual.dispose();
                activeOrderVisual = null;
                activePlayer = null;

                if (npcToRemove != null) {
                    final NPC npcFinal = npcToRemove;
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            client.sendTCP(new PacketRemoveNpcMessage(npcFinal.getId()));
                            client.sendTCP(new PacketRequestNPC(1, true));
                        }
                    }, 3f);
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.M) && !isGamePopupOpen) {
            String orderToMake = orderManager.getNextOrder();
            if (orderToMake == null) return;

            for (Interactables interactable : interactables.values()) {
                if (isPlayerNear(interactable.getBody().getPosition(), 0.4f)) {
                    String type = interactable.getClass().getSimpleName();
                    isGamePopupOpen = true;

                    Actor popup = switch (orderToMake) {
                        case "Coffee" -> type.equals("CoffeeMachine") ?
                            new CoffeeMakingGame(orderToMake, skin, finalOrder -> {
                                completeOrder("skin/kohviikoon.png", "Coffee", 3, finalOrder);
                                return true;
                            }) : null;
                        case "Sandwich" -> type.equals("CuttingBoard") ?
                            new SandwichMakingGame(orderToMake, skin, finalOrder -> {
                                completeOrder("skin/võikuikoon.png", "Sandwich", 7, finalOrder);
                                return true;
                            }) : null;
                        case "Smoothie" -> type.equals("Fridge") ?
                            new SmoothieMakingGame(orderToMake, skin, finalOrder -> {
                                completeOrder("skin/smuutiikoon.png", "Smoothie", 6, finalOrder);
                                return true;
                            }) : null;
                        case "Cake" -> type.equals("Stove") ?
                            new CakeMakingGame(orderToMake, skin, finalOrder -> {
                                completeOrder("skin/koogiikoon.png", "Cake", 4, finalOrder);
                                return true;
                            }) : null;
                        case "Salad" -> type.equals("Table") ?
                            new SaladMakingGame(orderToMake, skin, finalOrder -> {
                                completeOrder("skin/salatiikoon.png", "Salad", 13, finalOrder);
                                return true;
                            }) : null;
                        case "Egg" -> type.equals("Stove") ?
                            new EggMakingGame(orderToMake, skin, finalOrder -> {
                                completeOrder("skin/munaikoon.png", "Egg", 10, finalOrder);
                                return true;
                            }) : null;
                        default -> null;
                    };

                    if (popup != null) {
                        popup.toFront();
                        uiStage.addActor(popup);
                    } else {
                        isGamePopupOpen = false;
                    }
                    break;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            tutorialVisible = !tutorialVisible;
            tutorialPopup.setVisible(tutorialVisible);
            tutorialPopup.toFront();
        }
    }

    private void moneyBubbleTexture() {
        float worldX = player.getX() - 0.05f;
        float worldY = player.getY() + 0.1f; // above player

        // Convert world coordinates to screen coordinates
        Vector2 screenPos = viewport.project(new Vector2(worldX, worldY));

        // Create the MoneyBubble at screen coordinates
        MoneyBubble bubble = new MoneyBubble(moneyBubble, screenPos.x, screenPos.y);

        // Because uiStage uses screen units, override PPM-based scaling
        bubble.setSize(32, 32); // pixels, not world units

        uiStage.addActor(bubble);
    }

    private void npcHeartTexture(NPC npc) {
        if (npc != null) {

            NpcBubble bubble = new NpcBubble(new Texture("skin/heart.png"), npc, viewport);
            uiStage.addActor(bubble);
            bubble.addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.fadeOut(0.5f),
                Actions.removeActor()
            ));
        }
    }

    private void removeInteractableIcon() {
        showInteractableIcon = false;
        interactableIconPosition = null;
    }

    private void showLimitPopup() {
        LimitPopup popup = new LimitPopup("skin/limitpop.png");

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.A) {
                    popup.remove();
                    Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, playerController));
                    return true;
                }
                return false;
            }
        });

        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);

        popup.toFront();
        uiStage.addActor(popup);
    }

    private void updateOrderVisual(String texturePath, String orderType) {
        if (activeOrderVisual != null) {
            activeOrderVisual.remove();
            activeOrderVisual.dispose();
        }
        currentOrderString = orderType;
        activeOrderVisual = new OrderVisual(texturePath);
        uiStage.addActor(activeOrderVisual.getImage());
        activePlayer = player;
    }

    private void completeOrder(String texturePath, String orderType, int reward, String finalOrder) {
        pendingMoneyReward = reward;
        hasOrderToDeliver = true;
        isGamePopupOpen = false;
        removeInteractableIcon();
        orderManager.removeOrder(finalOrder);
        updateOrderVisual(texturePath, orderType);
        orderCount++;
    }
}
