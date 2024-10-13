package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.LiftComponent;
import com.qualcomm.robotcore.hardware.DcMotor;


@Config
public class LiftController extends AbstractController {

    private static final double MAX_MOTOR_VELOCITY = 2800;

    public static final double LIFT_SPEED = 1.0; // TODO: Adjust



    /*
        Manual in this may need to be changed since this will be a changing value
        dependant on where the lift currently is and the input on the controller
     */

    public static final int LIFT_POSITION_DOWN = 0; // TODO: Fill in
    public static final int LIFT_POSITION_UP = 0; // TODO: Fill in
    public static final int LIFT_MANUAL = 0; // TODO: Fill in


    // TODO: UPDATE THESE STATES TO THE NEW NAME
    protected enum LiftState {

        DOWN,
        UP,
        MANUAL,

    }

    protected final LiftComponent liftComponent;

    protected LiftState state = LiftState.DOWN;

    public LiftController(LiftComponent liftComponent) {

        this.liftComponent = liftComponent;

    }


    @Override
    public void initialize() {

    }

    @Override
    public void update() {



        switch (state) {


            case UP:
                asyncRunToPosition(LIFT_POSITION_UP);
                break;

            case MANUAL:
                asyncRunToPosition(LIFT_MANUAL);
                break;

            default:
            case DOWN:
                asyncRunToPosition(LIFT_POSITION_DOWN);
                break;

        }

    }

    private void asyncRunToPosition(int targetPosition) {

        if(this.liftComponent.getTargetPosition() == targetPosition)
            return;

        this.liftComponent.setTargetPosition(targetPosition);
        this.liftComponent.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.liftComponent.setPower(LIFT_SPEED);

    }

    public void goToDownPosition() { this.state = LiftState.DOWN; }
    public void goToUpPosition() { this.state = LiftState.UP; }
    public void goToManualPosition() { this.state = LiftState.MANUAL; }


}