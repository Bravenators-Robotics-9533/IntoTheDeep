package com.bravenatorsrobotics.concept;

import com.bravenatorsrobotics.components.ControlSystemComponent;
import com.bravenatorsrobotics.components.IntakeComponent;
import com.bravenatorsrobotics.controllers.ControlSystemController;
import com.bravenatorsrobotics.controllers.IntakeController;
import com.bravenatorsrobotics.io.FtcGamePad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Concept Intake", group="Concept")
public class ConceptIntakeOpMode extends LinearOpMode {

    private IntakeController intakeController;

    @Override
    public void runOpMode() throws InterruptedException {

        FtcGamePad gamepad = new FtcGamePad("Gamepad1", gamepad1, this::handleGamepad);

        ControlSystemComponent controlSystemComponent = new ControlSystemComponent(this.hardwareMap);
        IntakeComponent intakeComponent = new IntakeComponent(this.hardwareMap);

        this.intakeController = new IntakeController(intakeComponent);
        this.intakeController.initialize();

        ControlSystemController controlSystemController = new ControlSystemController(controlSystemComponent);
        controlSystemController.initialize();

        waitForStart();

        while(opModeIsActive()) {

            controlSystemController.update(); // Clear bulk cache (do first)

            gamepad.update();

            this.intakeController.update();

        }

    }

    public void handleGamepad(FtcGamePad gamepad, int button, boolean pressed) {

        switch (button) {

            case FtcGamePad.GAMEPAD_A:
                this.intakeController.tensionToInitialTension();
                break;

            case FtcGamePad.GAMEPAD_Y:
                this.intakeController.tensionToFullTension();
                break;

            case FtcGamePad.GAMEPAD_DPAD_LEFT:
                this.intakeController.pivotToInitialPivot();
                break;

            case FtcGamePad.GAMEPAD_DPAD_RIGHT:
                this.intakeController.pivotToFullPivot();
                break;

        }

    }

}
