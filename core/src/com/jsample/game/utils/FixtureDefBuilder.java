package com.jsample.game.utils;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

public class FixtureDefBuilder {

    private Shape shape;

    private float friction = 0.2f;

    private float restitution = 0;

    private float density = 0;

    private boolean isSensor = false;

    public static FixtureDefBuilder newInstance() {
        FixtureDefBuilder builder = new FixtureDefBuilder();
        return builder;
    }

    public FixtureDefBuilder setShape(Shape shape) {
        this.shape = shape;
        return this;
    }

    public FixtureDefBuilder setFriction(float friction) {
        this.friction = friction;
        return this;
    }

    public FixtureDefBuilder setRestitution(float restitution) {
        this.restitution = restitution;
        return this;
    }

    public FixtureDefBuilder setDensity(float density) {
        this.density = density;
        return this;
    }

    public FixtureDefBuilder setSensor(boolean sensor) {
        isSensor = sensor;
        return this;
    }

    public FixtureDef build() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = this.shape;
        fixtureDef.friction = this.friction;
        fixtureDef.restitution = this.restitution;
        fixtureDef.density = this.density;
        fixtureDef.isSensor = this.isSensor;
        return fixtureDef;
    }

}
