
package com.jsample.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jsample.game.MyGdxGame;

public class Musician extends Image {

    ParticleEffect effect;
    ParticleEffect effect2;

    public Musician() {
        super(new Texture("musician.png"));
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("musician.p"), MyGdxGame.textureAtlas);
        //effect.start();

        effect2 = new ParticleEffect();
        effect2.load(Gdx.files.internal("firebomb.p"), Gdx.files.internal("1.png"));
        //effect2.start();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        effect.draw(batch);
        //effect2.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        effect.setPosition(this.getWidth()+this.getX(),this.getHeight()+this.getY());
        effect.update(delta);

        //effect2.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        //effect2.update(delta);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
    }
}
