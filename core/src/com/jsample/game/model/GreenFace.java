
package com.jsample.game.model;

import com.badlogic.gdx.math.Vector2;

public class GreenFace {

    private Vector2 pos;
    private Vector2 size;
    private float rotation;

    public void setSize(Vector2 size) {
        this.size = size;
    }

    public Vector2 getSize() {
        return size;
    }

    public void setPosition(Vector2 pos) {
        this.pos = pos;
    }

    public float getPosX() {
        return pos.x;
    }

    public float getPosY() {
        return pos.y;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
