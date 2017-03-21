/************
 * Copyright (C) 2004 - 2017 UCWeb Inc. All Rights Reserved.
 * Description :
 * <p>
 * Creation    : 2017/3/21
 * Author      : jiaxin, jx124336@alibaba-inc.com
 */
package com.jsample.game.model;

import com.badlogic.gdx.math.Vector2;

public class CarModel {
    private Vector2 pos;
    private Vector2 size;

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

}
