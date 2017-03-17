/************
 * Copyright (C) 2004 - 2017 UCWeb Inc. All Rights Reserved.
 * Description :
 * <p>
 * Creation    : 2017/3/16
 * Author      : jiaxin, jx124336@alibaba-inc.com
 */
package com.jsample.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Transform {

    /**
     * @param x_m				body所在x坐标
     * @param y_m				body所在y坐标
     * @param wh				(x,y)body的宽高
     * @param scale				缩放比例
     * @return					（x,y）直接设置为图片的position可使图片与body重合
     */
    public static Vector2 mtp(float x_m, float y_m, Vector2 wh, float scale){
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        Vector2 vector2 = new Vector2();
        vector2.x = x_m * scale + screenWidth / 2 - wh.x * scale;
        vector2.y = y_m * scale + screenHeight / 2 - wh.y * scale;
        return vector2;
    }

    public static int DpToPx(float x) {
        int result = 0;
        final float scale = Gdx.graphics.getDensity();
        result = (int) (x * scale + 0.5f);
        return result;
    }
}
