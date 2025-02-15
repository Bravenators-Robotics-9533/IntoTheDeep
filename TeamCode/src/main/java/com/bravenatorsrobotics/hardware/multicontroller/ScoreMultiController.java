package com.bravenatorsrobotics.hardware.multicontroller;

import com.bravenatorsrobotics.hardware.controllers.LiftController;
import com.bravenatorsrobotics.hardware.controllers.OuttakeController;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ScoreMultiController extends AbstractMultiController {

    private final LiftController liftController;
    private final OuttakeController outtakeController;

    private final Telemetry telemetry;

    public ScoreMultiController(LiftController liftController, OuttakeController outtakeController, Telemetry telemetry) {

        this.liftController = liftController;
        this.outtakeController = outtakeController;

        this.telemetry = telemetry;

    }

    @Override
    public void update() {

    }

    public void grabAndScore() {

    }

}
