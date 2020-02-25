package com.jsample.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.jsample.game.model.BodyModel;
import com.jsample.game.screens.JointGameScreen;
import com.jsample.game.utils.FixtureDefBuilder;
import com.jsample.game.utils.RevoluteJointDefBuilder;

public class BodyManager {

    private World world;
    private Body bodyBody, headBody, leftArmBody1, leftArmBody2, rightArmBody1, rightArmBody2,
            leftLegBody1, leftLegBody2, rightLegBody1, rightLegBody2, platformBody;
    private RevoluteJoint headBodyRevJoint, leftLegBodyRevJoint, rightLegBodyRevJoint, leftArmBodyRevJoint,
            rightArmBodyRevJoint, leftLeg1KneeRevJoint, rightLeg1KneeRevJoint,
            leftLeg2KneeRevJoint, rightLeg2KneeRevJoint, leftArm1ElbowRevJoint, rightArm1ElbowRevJoint,
            leftArm2ElbowRevJoint, rightArm2ElbowRevJoint;
    private PrismaticJoint headBodyPriJoint;
    private RevoluteJoint platformDistanceJoint, platformDistanceJoint2, platformDistanceJoint3;
    private DistanceJoint armDistanceJoint;

    float bodyWidth;
    float bodyHeight;
    float cameraWidth;
    float cameraHeight;

    public BodyManager(World world) {
        this.world = world;
        cameraWidth = Gdx.graphics.getWidth() / JointGameScreen.PXTM;
        cameraHeight = Gdx.graphics.getHeight() / JointGameScreen.PXTM;

        /** Body: floor */
        PolygonShape floorShape = getBoxShape(cameraWidth, cameraHeight / 32);

        FixtureDef floorFixtureDef = FixtureDefBuilder.newInstance()
                .setShape(floorShape).setDensity(0).setFriction(0.1f).setRestitution(0).build();

        BodyDef floorBodyDef = new BodyDef();
        floorBodyDef.position.set(0, -cameraHeight / 2);
        Body floorBody = world.createBody(floorBodyDef);
        floorBody.createFixture(floorFixtureDef);
        floorShape.dispose();

        /** Body: body */
        bodyWidth = cameraWidth / 36;
        bodyHeight = cameraWidth / 12;
        PolygonShape bodyShape = getBoxShape(bodyWidth, bodyHeight);

        FixtureDef bodyFixtureDef = FixtureDefBuilder.newInstance()
                .setShape(bodyShape).setDensity(0.5f).setFriction(1f).setRestitution(0.1f).build();
        bodyFixtureDef.filter.groupIndex = -1;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(-cameraWidth / 4, 0);
        bodyBody = world.createBody(bodyDef);
        Vector2 bodySize = new Vector2(bodyWidth, bodyHeight);
        BodyModel bodyModel = new BodyModel();
        bodyModel.setSize(bodySize);
        bodyBody.setUserData(bodyModel);
        bodyBody.createFixture(bodyFixtureDef);
        bodyShape.dispose();

        /** Body: head */
        CircleShape headShape = getCircleShape(bodyWidth * 2);

        FixtureDef headFixtureDef = FixtureDefBuilder.newInstance()
                .setShape(headShape).setDensity(0.5f).setFriction(1f).setRestitution(0.1f).build();
        headFixtureDef.filter.groupIndex = -1;

        BodyDef headBodyDef = new BodyDef();
        headBodyDef.type = BodyDef.BodyType.DynamicBody;
        headBodyDef.position.set(-cameraWidth / 4, bodyBody.getPosition().y + bodyHeight + bodyWidth * 2);
        headBody = world.createBody(headBodyDef);
        headBody.createFixture(headFixtureDef);
        headShape.dispose();

        /** Joint: body-head */
        RevoluteJointDef revoluteJointDef = RevoluteJointDefBuilder.newInstance(bodyBody, headBody,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyHeight))
                .setEnableMotor(true).setEnableLimit(true).setMaxMotorTorque(1000)
                .setLowerAngle(-0.15f).setUpperAngle(0.15f).build();
        headBodyRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Body: leftLeg1 */
        PolygonShape legArmShape = getBoxShape(bodyWidth / 2, bodyHeight / 2);

        FixtureDef legFixtureDef = FixtureDefBuilder.newInstance()
                .setShape(legArmShape).setDensity(0.5f).setFriction(0.1f).setRestitution(0.1f).build();
        legFixtureDef.filter.groupIndex = -1;

        BodyDef legArmBodyDef = new BodyDef();
        legArmBodyDef.type = BodyDef.BodyType.DynamicBody;

        legArmBodyDef.position.set(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight - bodyHeight / 2);
        leftLegBody1 = world.createBody(legArmBodyDef);
        leftLegBody1.createFixture(legFixtureDef);

        /** Joint: body-leftLeg */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(bodyBody, leftLegBody1,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight))
                .setEnableMotor(true).setEnableLimit(true).setMaxMotorTorque(1000)
                .setLowerAngle(-0.07f).setUpperAngle(0.07f).build();
        leftLegBodyRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
        leftLegBodyRevJoint.setMotorSpeed(-0.5f);

        /** Body: leftKnee */
        CircleShape kneeShape = getCircleShape(bodyWidth / 2);

        FixtureDef kneeFixtureDef = FixtureDefBuilder.newInstance()
                .setShape(kneeShape).setFriction(0.1f).setDensity(0.5f).setRestitution(0.1f).build();
        kneeFixtureDef.filter.groupIndex = -1;

        BodyDef kneeBodyDef = new BodyDef();
        kneeBodyDef.type = BodyDef.BodyType.DynamicBody;
        kneeBodyDef.position.set(leftLegBody1.getPosition().x, leftLegBody1.getPosition().y - bodyHeight / 2);
        Body leftKneeBody = world.createBody(kneeBodyDef);
        leftKneeBody.createFixture(kneeFixtureDef);

        /** Joint: leftLeg1-knee */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(leftLegBody1, leftKneeBody,
                leftKneeBody.getWorldCenter()).setEnableMotor(true).setEnableLimit(true)
                .setMaxMotorTorque(1000).setLowerAngle(0).setUpperAngle(0).build();

        leftLeg1KneeRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
//        leftLeg1KneeRevJoint.setMotorSpeed(-0.5f);

        /** Body: leftLeg2 */
        legArmBodyDef.position.set(leftLegBody1.getPosition().x, leftLegBody1.getPosition().y - bodyHeight);
        leftLegBody2 = world.createBody(legArmBodyDef);
        leftLegBody2.createFixture(legFixtureDef);

        /** Joint: leftLeg2-knee */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(leftLegBody2, leftKneeBody,
                leftKneeBody.getWorldCenter()).setEnableMotor(true).setEnableLimit(true)
                .setMaxMotorTorque(1000).setLowerAngle(-0.07f).setUpperAngle(0.07f).build();

        leftLeg2KneeRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
        leftLeg2KneeRevJoint.setMotorSpeed(0.5f);

        /** Body: rightLeg1 */
        legArmBodyDef.position.set(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight - bodyHeight / 2);
        rightLegBody1 = world.createBody(legArmBodyDef);
        rightLegBody1.createFixture(legFixtureDef);

        /** Joint: body-rightLeg1 */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(bodyBody, rightLegBody1,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight))
                .setEnableMotor(true).setEnableLimit(true).setMaxMotorTorque(1000)
                .setLowerAngle(-0.1f).setUpperAngle(0.1f).build();

        rightLegBodyRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
        rightLegBodyRevJoint.setMotorSpeed(0.5f);

        /** Body: rightKnee */
        kneeBodyDef.position.set(rightLegBody1.getPosition().x, rightLegBody1.getPosition().y - bodyHeight / 2);
        Body rightKneeBody = world.createBody(kneeBodyDef);
        rightKneeBody.createFixture(kneeFixtureDef);

        /** Joint: rightLeg1-knee */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(rightLegBody1, rightKneeBody,
                rightKneeBody.getWorldCenter()).setEnableMotor(true).setEnableLimit(true)
                .setMaxMotorTorque(1000).setLowerAngle(0).setUpperAngle(0).build();

        rightLeg1KneeRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
//        rightLeg1KneeRevJoint.setMotorSpeed(-1);

        /** Body: rightLeg2 */
        legArmBodyDef.position.set(rightLegBody1.getPosition().x, rightLegBody1.getPosition().y - bodyHeight);
        rightLegBody2 = world.createBody(legArmBodyDef);
        rightLegBody2.createFixture(legFixtureDef);

        /** Joint: rightLeg2-knee */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(rightLegBody2, rightKneeBody,
                rightKneeBody.getWorldCenter()).setEnableMotor(true).setEnableLimit(true)
                .setMaxMotorTorque(1000).setLowerAngle(-0.07f).setUpperAngle(0.07f).build();

        rightLeg2KneeRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
        rightLeg2KneeRevJoint.setMotorSpeed(0.5f);

        /** Body: leftArm1 */
        legArmBodyDef.position.set(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyWidth);
        leftArmBody1 = world.createBody(legArmBodyDef);
        leftArmBody1.createFixture(legFixtureDef);

        /** Joint: body-leftArm1 */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(bodyBody, leftArmBody1,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyHeight - bodyWidth / 2))
                .setEnableMotor(true).setEnableLimit(true).setMaxMotorTorque(3000)
                .setLowerAngle(0.1f).setUpperAngle(0.9f).build();

        leftArmBodyRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Body: leftElbow */
        kneeBodyDef.position.set(leftArmBody1.getPosition().x, leftArmBody1.getPosition().y - bodyHeight / 2);
        Body leftElbowBody = world.createBody(kneeBodyDef);
        leftElbowBody.createFixture(kneeFixtureDef);

        /** Joint: leftArm1-elbow */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(leftArmBody1, leftElbowBody,
                leftElbowBody.getWorldCenter()).setEnableMotor(true).setEnableLimit(true)
                .setMaxMotorTorque(1000).setLowerAngle(0).setUpperAngle(0).build();

        leftArm1ElbowRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Body: leftArm2 */
        legArmBodyDef.position.set(leftArmBody1.getPosition().x, leftArmBody1.getPosition().y - bodyHeight);
        leftArmBody2 = world.createBody(legArmBodyDef);
        leftArmBody2.createFixture(legFixtureDef);

        /** Joint: leftArm2-elbow */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(leftArmBody2, leftElbowBody,
                leftElbowBody.getWorldCenter()).setEnableMotor(true).setEnableLimit(true)
                .setMaxMotorTorque(1000).setLowerAngle(0).setUpperAngle(0).build();

        leftArm2ElbowRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Body: rightArm1 */
        legArmBodyDef.position.set(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyWidth);
        rightArmBody1 = world.createBody(legArmBodyDef);
        rightArmBody1.createFixture(legFixtureDef);

        /** Joint: body-rightArm1 */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(bodyBody, rightArmBody1,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyHeight - bodyWidth / 2))
                .setEnableMotor(true).setEnableLimit(true).setMaxMotorTorque(1000)
                .setLowerAngle(-0.9f).setUpperAngle(0.9f).build();

        rightArmBodyRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
//        rightArmBodyRevJoint.setMotorSpeed(0.5f);

        /** Body: rightElbow */
        kneeBodyDef.position.set(rightArmBody1.getPosition().x, rightArmBody1.getPosition().y - bodyHeight / 2);
        Body rightElbowBody = world.createBody(kneeBodyDef);
        rightElbowBody.createFixture(kneeFixtureDef);

        /** Joint: rightArm1-elbow */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(rightArmBody1, rightElbowBody,
                leftElbowBody.getWorldCenter()).setEnableMotor(true).setEnableLimit(true)
                .setMaxMotorTorque(1000).setLowerAngle(0).setUpperAngle(0).build();

        rightArm1ElbowRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Body: rightArm2 */
        legArmBodyDef.position.set(rightArmBody1.getPosition().x, rightArmBody1.getPosition().y - bodyHeight);
        rightArmBody2 = world.createBody(legArmBodyDef);
        rightArmBody2.createFixture(legFixtureDef);

        /** Joint: rightArm2-elbow */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(rightArmBody2, rightElbowBody,
                leftElbowBody.getWorldCenter()).setEnableMotor(true).setEnableLimit(true)
                .setMaxMotorTorque(3000).setLowerAngle(-0.9f).setUpperAngle(-0.2f).build();

        rightArm2ElbowRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
//        rightArm2ElbowRevJoint.setMotorSpeed(-1f);

        /** Body: platform */
        PolygonShape platformShape = getBoxShape(cameraWidth / 8, cameraHeight / 64);

        FixtureDef platformFixtureDef = FixtureDefBuilder.newInstance()
                .setShape(platformShape).setDensity(0).setFriction(0.1f).setRestitution(0).build();

        BodyDef platformBodyDef = new BodyDef();
        platformBodyDef.position.set(bodyBody.getPosition().x, rightLegBody2.getPosition().y - bodyHeight / 2);
        platformBody = world.createBody(platformBodyDef);
        platformBody.createFixture(platformFixtureDef);
        platformShape.dispose();

        kneeShape.dispose();
        legArmShape.dispose();

    }

    public void createPlatformDistanceJoint() {
        /** Joint: platformRevJoint1 */
        RevoluteJointDef revoluteJointDef = RevoluteJointDefBuilder.newInstance(platformBody, leftLegBody2,
                platformBody.getWorldCenter()).setEnableMotor(true).setEnableLimit(true)
                .setMaxMotorTorque(1000).setLowerAngle(0).setUpperAngle(0).build();

        platformDistanceJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Joint: platformRevJoint2 */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(platformBody, rightLegBody2,
                platformBody.getWorldCenter()).setEnableMotor(true).setEnableLimit(true)
                .setMaxMotorTorque(1000).setLowerAngle(0).setUpperAngle(0).build();

        platformDistanceJoint2 = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Joint: platformRevJoint3 */
        revoluteJointDef = RevoluteJointDefBuilder.newInstance(platformBody, bodyBody,
                platformBody.getWorldCenter()).setEnableMotor(true).setEnableLimit(true)
                .setMaxMotorTorque(1000).setLowerAngle(0).setUpperAngle(0).build();

        platformDistanceJoint3 = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Joint: armDistanceJoint */
        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.initialize(leftArmBody2, rightArmBody2, leftArmBody2.getWorldCenter(),
                leftArmBody2.getWorldCenter());
        armDistanceJoint = (DistanceJoint) world.createJoint(distanceJointDef);
    }

    private PolygonShape getBoxShape(float width, float height) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height);
        return shape;
    }

    private CircleShape getCircleShape(float radius) {
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        return shape;
    }

    public RevoluteJoint getLeftArmBodyRevJoint() {
        return leftArmBodyRevJoint;
    }

    public DistanceJoint getArmDistanceJoint() {
        return armDistanceJoint;
    }

}
