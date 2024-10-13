package com.bravenatorsrobotics.multicontroller;

import com.bravenatorsrobotics.controllers.AbstractController;
import com.bravenatorsrobotics.controllers.IntakeController;
import com.bravenatorsrobotics.controllers.LiftController;
import com.bravenatorsrobotics.controllers.SwingArmController;

public class ScoringMultiController extends AbstractMultiController {


    protected final LiftController liftController;
    protected final IntakeController intakeController;
    protected final SwingArmController swingArmController;

    public ScoringMultiController(LiftController liftController, IntakeController intakeController, SwingArmController swingArmController) {
        this.liftController = liftController;
        this.intakeController = intakeController;
        this.swingArmController = swingArmController;
    }

    @Override
    public void update() {
        // DO NOT UPDATE CHILD CONTROLLERS HERE JUST LOGIC
    }
}
