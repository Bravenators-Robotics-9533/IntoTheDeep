package com.bravenatorsrobotics.multicontroller;

import com.bravenatorsrobotics.controllers.AbstractController;
import com.bravenatorsrobotics.controllers.IntakeController;
import com.bravenatorsrobotics.controllers.LiftController;
import com.bravenatorsrobotics.controllers.SwingArmController;

/*

    TODO: READ
    YOU DID ADD THE MEMBER VARIABLE LIFT_CONTROLLER CORRECTLY THAT WILL WORK.
    AND ADDING THOSE METHODS BELOW WAS A GOOD IDEA. BUT PLEASE GO THROUGH AND FOLLOW
    PASSIVE NAMING CONVENTION. EG. GO_TO_START_POSITION SO IT SHOULD ALSO BE
    GO_TO_GRAB_POSITION NOT GO_TO_GRABBING_POSITION. JUST LITTLE THINGS BUT IT IS IMPORTANT FOR
    PROPER DOCUMENTATION AND CLIENT EXPECTATION

    BTW. SCORING POSITION IS FINE BUT GRABBING ISN'T

    !!! ALSO PLEASE PUT SPACES BEFORE THE {


 */
public class ScoringMultiController extends AbstractMultiController {

    protected final LiftController liftController;
    protected final IntakeController intakeController;
    protected final SwingArmController swingArmController;

    public ScoringMultiController(LiftController liftController, IntakeController intakeController, SwingArmController swingArmController) {
        this.liftController = liftController;
        this.intakeController = intakeController;
        this.swingArmController = swingArmController;
    }

    // TODO: Make elbow controller

    public void goToStartPosition(){
        // TODO: Fill in controller positions
    }

    public void goToRestPosition(){
        // TODO: Fill in controller positions
    }

    public void goToGrabbingPosition(){
        // TODO: Fill in controller positions
    }

    public void goToBucketScoringPosition(){
        // TODO: Fill in controller positions
    }

    public void goToSpecimenScoringPosition(){
        // TODO: Fill in controller positions
    }

    @Override
    public void update() {
        // DO NOT UPDATE CHILD CONTROLLERS HERE JUST LOGIC
    }
}
