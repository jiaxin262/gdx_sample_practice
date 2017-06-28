package com.jsample.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class RevoluteJointDefBuilder {

    private float referenceAngle = 0;

    private boolean enableLimit = false;

    private float lowerAngle = 0;

    private float upperAngle = 0;

    private boolean enableMotor = false;

    private float motorSpeed = 0;

    private float maxMotorTorque = 0;

    private Body body1, body2;

    private Vector2 anchor;

    public static RevoluteJointDefBuilder newInstance(Body body1, Body body2, Vector2 anchor) {
        RevoluteJointDefBuilder builder = new RevoluteJointDefBuilder();
        builder.body1 = body1;
        builder.body2 = body2;
        builder.anchor = anchor;
        return builder;
    }

    public RevoluteJointDefBuilder setReferenceAngle(float referenceAngle) {
        this.referenceAngle = referenceAngle;
        return this;
    }

    public RevoluteJointDefBuilder setEnableLimit(boolean enableLimit) {
        this.enableLimit = enableLimit;
        return this;
    }

    public RevoluteJointDefBuilder setLowerAngle(float lowerAngle) {
        this.lowerAngle = lowerAngle;
        return this;
    }

    public RevoluteJointDefBuilder setUpperAngle(float upperAngle) {
        this.upperAngle = upperAngle;
        return this;
    }

    public RevoluteJointDefBuilder setEnableMotor(boolean enableMotor) {
        this.enableMotor = enableMotor;
        return this;
    }

    public RevoluteJointDefBuilder setMotorSpeed(float motorSpeed) {
        this.motorSpeed = motorSpeed;
        return this;
    }

    public RevoluteJointDefBuilder setMaxMotorTorque(float maxMotorTorque) {
        this.maxMotorTorque = maxMotorTorque;
        return this;
    }

    public RevoluteJointDef build() {
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.initialize(body1, body2, anchor);
        revoluteJointDef.referenceAngle = this.referenceAngle;
        revoluteJointDef.enableLimit = this.enableLimit;
        revoluteJointDef.lowerAngle = this.lowerAngle;
        revoluteJointDef.upperAngle = this.upperAngle;
        revoluteJointDef.enableMotor = this.enableMotor;
        revoluteJointDef.motorSpeed = this.motorSpeed;
        revoluteJointDef.maxMotorTorque = this.maxMotorTorque;
        return revoluteJointDef;
    }
}
