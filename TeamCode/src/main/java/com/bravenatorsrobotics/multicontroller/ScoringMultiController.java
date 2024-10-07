package com.bravenatorsrobotics.multicontroller;

import com.bravenatorsrobotics.controllers.AbstractController;
import com.bravenatorsrobotics.controllers.IntakeController;
import com.bravenatorsrobotics.controllers.SwingArmController;

public class ScoringMultiController extends AbstractMultiController {

    // TODO: MAKE A LIFT CONTROLLER
    // protected final LiftController liftController;

    protected final IntakeController intakeController;
    protected final SwingArmController swingArmController;

    public ScoringMultiController(IntakeController intakeController, SwingArmController swingArmController) {
        this.intakeController = intakeController;
        this.swingArmController = swingArmController;
    }

    @Override
    public void update() {
        // DO NOT UPDATE CHILD CONTROLLERS HERE JUST LOGIC
    }
}
