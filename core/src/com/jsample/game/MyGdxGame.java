package com.jsample.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jsample.game.screens.TitleScreen;

public class MyGdxGame extends Game {

	private Stage stage;
	public static Skin skin;
	public static TextureAtlas textureAtlas;
	public static SpriteBatch batch;

	@Override
	public void create () {

		skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
		textureAtlas = new TextureAtlas();
		textureAtlas.addRegion("note", new TextureRegion(new Texture("note.png")));
		batch = new SpriteBatch();
		this.setScreen(new TitleScreen(this));

//		stage = new Stage(new ScreenViewport());
//		Texture img = new Texture("badlogic.jpg");
//
//		Image image1 = new Image(img);
//		image1.setPosition(Gdx.graphics.getWidth() / 2 - image1.getWidth() / 2,
//				Gdx.graphics.getHeight() - image1.getHeight() - 10);
//		stage.addActor(image1);
//
//		Image image2 = new Image(img);
//		image2.setPosition(Gdx.graphics.getWidth() / 2 - image2.getWidth() / 2,
//				Gdx.graphics.getHeight() - image1.getHeight() - image2.getHeight() - 30);
//		image2.setOrigin(image2.getWidth() / 2, image2.getHeight() / 2);
//		image2.rotateBy(-45);
//		stage.addActor(image2);
//
//		Image image3 = new Image(img);
//		image3.setSize(img.getWidth() / 2, img.getHeight() / 2);
//		image3.setPosition(Gdx.graphics.getWidth() / 2 - image3.getWidth() / 2,
//				Gdx.graphics.getHeight() - image1.getHeight() - image2.getHeight() - 30 - image3.getHeight());
//		stage.addActor(image3);
//
//		img.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
//		TextureRegion region = new TextureRegion(img);
//		region.setRegion(0, 0, img.getWidth() * 4, img.getHeight() * 2);
//		Image image4 = new Image(region);
//		image4.setSize(256 * 4, 256 * 2);
//		image4.setPosition(0, 0);
//		stage.addActor(image4);

	}

	@Override
	public void render () {
		super.render();
//		Gdx.gl.glClearColor(1, 1, 1, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		stage.act();
//		stage.draw();

	}

	@Override
	public void dispose() {
		skin.dispose();
		textureAtlas.dispose();
	}
}
