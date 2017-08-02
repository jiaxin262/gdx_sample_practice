package com.jsample.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import com.jsample.game.MyGdxGame;

import java.util.HashMap;
import java.util.Random;

public class GameScreen6 implements Screen {

    public Game mGame;
    private Stage stage;
    private TextureAtlas mTextureAtlas;
    private Sprite mBananaSprite;
    private OrthographicCamera mCamera;
    private ExtendViewport mViewport;
    private HashMap<String, Sprite> mSpritesMap = new HashMap<String, Sprite>();

    private World mWord;
    private Body mGround;
    private Box2DDebugRenderer mDebugRender;

    private PhysicsShapeCache mPhysicsBodies;

    private static final float STEP_TIME = 1f / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final float SCALE = 0.05f;
    static final int COUNT = 16;
    Body[] fruitBodies = new Body[COUNT];
    String[] names = new String[COUNT];

    private float mAccumulator = 0;

    public GameScreen6(Game game) {
        this.mGame = game;
        Gdx.input.setCatchBackKey(true);
        Box2D.init();
        mWord = new World(new Vector2(0, -10), true);
        mDebugRender = new Box2DDebugRenderer();

        mViewport = new ExtendViewport(50, 50);
        stage = new Stage(mViewport);
        mCamera = (OrthographicCamera) stage.getCamera();
        mTextureAtlas = new TextureAtlas("fruits/sprites.txt");
        mBananaSprite = mTextureAtlas.createSprite("banana");
        addSprites();

        mPhysicsBodies = new PhysicsShapeCache("fruits/pgysics.xml");
        generateFruit();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            mGame.setScreen(new TitleScreen(mGame));
        }
        MyGdxGame.batch.setProjectionMatrix(mCamera.combined);
        stage.act();
        stage.draw();
        MyGdxGame.batch.begin();
        for (int i = 0; i < fruitBodies.length; i++) {
            Body body = fruitBodies[i];
            String name = names[i];

            Vector2 position = body.getPosition();
            float degrees = (float) Math.toDegrees(body.getAngle());
            drawSprite(name, position.x, position.y, degrees);
        }
        MyGdxGame.batch.end();

        stepWorld();

        mDebugRender.render(mWord, mCamera.combined);
    }

    @Override
    public void resize(int width, int height) {
        mViewport.update(width, height, true);
        createGround();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        mTextureAtlas.dispose();
        mSpritesMap.clear();
        mWord.dispose();
        mDebugRender.dispose();
    }

    private void addSprites() {
        Array<TextureAtlas.AtlasRegion> regions = mTextureAtlas.getRegions();
        for (TextureAtlas.AtlasRegion region : regions) {
            Sprite sprite = mTextureAtlas.createSprite(region.name);
            float width = sprite.getWidth() * SCALE;
            float height = sprite.getHeight() * SCALE;
            sprite.setSize(width, height);
            sprite.setOrigin(0, 0);
            mSpritesMap.put(region.name, sprite);
        }
    }

    private void drawSprite(String name, float posX, float posY, float degrees) {
        if (name == null || "".equals(name)) {
            return;
        }
        Sprite sprite = mSpritesMap.get(name);
        sprite.setPosition(posX, posY);
        sprite.setRotation(degrees);
        sprite.draw(MyGdxGame.batch);
    }

    private void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();
        mAccumulator += Math.min(delta, 0.25f);
        if (mAccumulator >= STEP_TIME) {
            mAccumulator -= STEP_TIME;
            mWord.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }

    private Body createBody(String name, float x, float y, float rotation) {
        Body body = mPhysicsBodies.createBody(name, mWord, SCALE, SCALE);
        body.setTransform(x, y, rotation);
        return body;
    }

    private void createGround() {
        if (mGround != null) {
            mWord.destroyBody(mGround);
        }
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 1;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(mCamera.viewportWidth / 2, 1);

        fixtureDef.shape = shape;

        mGround = mWord.createBody(bodyDef);
        mGround.createFixture(fixtureDef);
        mGround.setTransform(mCamera.viewportWidth / 2, 1, 0);

        shape.dispose();
    }

    private void generateFruit() {
        String[] fruitNames = new String[]{"banana", "cherries", "orange", "crate"};

        Random random = new Random();

        for (int i = 0; i < fruitBodies.length; i++) {
            String name = fruitNames[random.nextInt(fruitNames.length)];

            float x = random.nextFloat() * 50;
            float y = random.nextFloat() * 50 + 50;

            names[i] = name;
            fruitBodies[i] = createBody(name, x, y, 0);
        }
    }
}
