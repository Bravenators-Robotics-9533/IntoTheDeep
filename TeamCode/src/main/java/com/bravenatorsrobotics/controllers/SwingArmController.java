package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.SwingArmComponent;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * TODO: README
 *
 * Please go through this file, read all comments and make changes.
 * Don't forget to find your experimental values
 *
 * When this is this controller will only control the swing arm component
 *
 * You then need to make a multicontroller system that will control the swing arm, intake basket,
 * and lift at the same time.
 *
 * This will be a MULTICONTROLLER system. Last year I called it a multicomponent system but this isn't
 * the right naming for the project we have this year. so call it a mulitcontroller system
 *
 * Link to last years mulitcomponent system: https://github.com/Bravenators-Robotics-9533/CenterStage/blob/master/TeamCode/src/main/java/com/bravenatorsrobotics/multiComponentSystem/LiftMultiComponentSystem.java
 *
 * Remember this will be different this year. and this is not an easy task AT ALL! Like this actually
 * is difficult. So I will probably have to help you. Try to follow along with the example above. But note
 * you will be brining in ComponentControllers instead of Components because of our new abstraction
 * pattern.
 *
 * I made a the ScoringMultiController in the multiController package
 */

@Config
public class SwingArmController extends AbstractController {

    private static final double MAX_MOTOR_VELOCITY = 2800;

    public static final double SWING_ARM_SPEED = 1.0; // TODO: Adjust

    /*
     * TODO: READ
     *
     * I have literally no idea what the heck all of these names are
     * Like literally none. what is fold vs rest vs sp scoring vs basket
     *
     * for reference last year i used positions INTAKE and SCORING
     *
     * you could use like INTAKE, SCORING_..., SCORING_... whatever
     *
     * naming just doesn't work
     *
     * then after you change them change it all over the file
     */
    public static final int SWING_ARM_POSITION_START = 0; // TODO: Fill in
    public static final int SWING_ARM_POSITION_REST = 0; // TODO: Fill in
    public static final int SWING_ARM_POSITION_SCORING_SPECIMEN = 0; // TODO: Fill in
    public static final int SWING_ARM_POSITION_SCORING_BASKET = 0; // TODO: Fill in


    protected enum SwingArmState {

        START,
        REST,
        SCORING_SPECIMEN,
        SCORING_BASKET

    }

    protected final SwingArmComponent swingArmComponent;

    protected SwingArmState state = SwingArmState.START;

    public SwingArmController(SwingArmComponent swingArmComponent) {

        this.swingArmComponent = swingArmComponent;

    }


    @Override
    public void initialize() {

    }

    @Override
    public void update() {



        switch (state) {

            case START:
               asyncRunToPosition(SWING_ARM_POSITION_START);
               break;

           case SCORING_BASKET:
                asyncRunToPosition(SWING_ARM_POSITION_SCORING_BASKET);
               break;

            case SCORING_SPECIMEN:
                asyncRunToPosition(SWING_ARM_POSITION_SCORING_SPECIMEN);
                break;

            default:
            case REST:
                asyncRunToPosition(SWING_ARM_POSITION_REST);
                break;

        }

    }

    private void asyncRunToPosition(int targetPosition) {

        if(this.swingArmComponent.getTargetPosition() == targetPosition)
            return;

        this.swingArmComponent.setTargetPosition(targetPosition);
        this.swingArmComponent.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.swingArmComponent.setPower(SWING_ARM_SPEED);

    }

    public void goToRestPosition() { this.state = SwingArmState.REST; }
    public void goToScoringSpecimenPosition() { this.state = SwingArmState.SCORING_SPECIMEN; }
    public void goToStartPosition() { this.state = SwingArmState.START; }
    public void goToScoringBasketPosition() { this.state = SwingArmState.SCORING_BASKET; }

}