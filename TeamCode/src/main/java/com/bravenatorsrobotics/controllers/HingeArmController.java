package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.HingeArmComponent;
import com.qualcomm.robotcore.hardware.DcMotor;

/*

    SAME POINTS I STATED IN HINGE ARM COMPONENT CLASS. EVEN MORE SO HERE THOUGH BECAUSE YOU
    SEE HOW THE STATE MACHINE IS PRETTY MUCH THE SAME.

    IN PROGRAMMING THERES A PARADIME (this is spelled right btw, i googled it)
    CALLED DRY (DON'T REPEAT YOURSELF)

    DRY CODING IS JUST WRITING THINGS ONCE AND DONE AND REUSING.

    NOTICE HOW IF YOU MAKE CHANGES TO THE STATE MACHINE YOU WOULD HAVE TO DO IT IN TWO CLASSES.

    THIS IS CALLED UNINTENTIONAL DISCRETE LOGIC COUPLING. GENERALLY NOT RECOMMENDED.

 */

@Config
public class HingeArmController extends AbstractController {

    private static final double MAX_MOTOR_VELOCITY = 2800;

    public static final double HINGE_ARM_SPEED = 1.0; // TODO: Adjust


    public static final int HINGE_ARM_POSITION_START = 0; // TODO: Fill in
    public static final int HINGE_ARM_POSITION_REST = 0; // TODO: Fill in
    public static final int HINGE_ARM_POSITION_SCORING_SPECIMEN = 0; // TODO: Fill in
    public static final int HINGE_ARM_POSITION_SCORING_BASKET = 0; // TODO: Fill in


    protected enum HingeArmState {

        START,
        REST,
        SCORING_SPECIMEN,
        SCORING_BASKET

    }

    protected final HingeArmComponent hingeArmComponent;

    protected HingeArmState state = HingeArmState.START;

    public HingeArmController(HingeArmComponent hingeArmComponent) {

        this.hingeArmComponent = hingeArmComponent;

    }


    @Override
    public void initialize() {

    }

    @Override
    public void update() {



        switch (state) {

            case START:
                asyncRunToPosition(HINGE_ARM_POSITION_START);
                break;

            case SCORING_BASKET:
                asyncRunToPosition(HINGE_ARM_POSITION_SCORING_BASKET);
                break;

            case SCORING_SPECIMEN:
                asyncRunToPosition(HINGE_ARM_POSITION_SCORING_SPECIMEN);
                break;

            default:
            case REST:
                asyncRunToPosition(HINGE_ARM_POSITION_REST);
                break;

        }

    }

    private void asyncRunToPosition(int targetPosition) {

        if(this.hingeArmComponent.getTargetPosition() == targetPosition)
            return;

        this.hingeArmComponent.setTargetPosition(targetPosition);
        this.hingeArmComponent.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.hingeArmComponent.setPower(HINGE_ARM_SPEED);

    }

    public void goToRestPosition() { this.state = HingeArmState.REST; }
    public void goToScoringSpecimenPosition() { this.state = HingeArmState.SCORING_SPECIMEN; }
    public void goToStartPosition() { this.state = HingeArmState.START; }
    public void goToScoringBasketPosition() { this.state = HingeArmState.SCORING_BASKET; }

}