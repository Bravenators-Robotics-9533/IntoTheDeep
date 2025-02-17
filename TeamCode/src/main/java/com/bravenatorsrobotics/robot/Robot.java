package com.bravenatorsrobotics.robot;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.bravenatorsrobotics.hardware.components.ControlSystemComponent;
import com.bravenatorsrobotics.hardware.components.IntakeComponent;
import com.bravenatorsrobotics.hardware.components.LiftComponent;
import com.bravenatorsrobotics.hardware.components.OuttakeComponent;
import com.bravenatorsrobotics.hardware.components.SlideComponent;
import com.bravenatorsrobotics.hardware.controllers.ControlSystemController;
import com.bravenatorsrobotics.hardware.controllers.IntakeController;
import com.bravenatorsrobotics.hardware.controllers.LiftController;
import com.bravenatorsrobotics.hardware.controllers.OuttakeController;
import com.bravenatorsrobotics.hardware.controllers.SlideController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import roadrunner.drive.MecanumDrive;

public class Robot {

    public final MecanumDrive drive;

    // Controllers
    public final ControlSystemController controlSystemController;
    public final IntakeController intakeController;
    public final OuttakeController outtakeController;
    public final LiftController liftController;
    public final SlideController slideController;

    protected final OpMode opMode;
    protected final UpdateHandler updateHandler;

    public Robot(ControlSystemController.Strategy updateStrategy, Pose2d startingPosition, OpMode opMode, UpdateHandler updateHandler) {

        this.opMode = opMode;
        this.updateHandler = updateHandler;

        // TODO: SET THIS STARTING POSITION BETTER
        this.drive = new MecanumDrive(opMode.hardwareMap, startingPosition);

        // Create the components
        ControlSystemComponent controlSystemComponent = new ControlSystemComponent(opMode.hardwareMap);
        IntakeComponent intakeComponent = new IntakeComponent(opMode.hardwareMap);
        OuttakeComponent outtakeComponent = new OuttakeComponent(opMode.hardwareMap);
        LiftComponent liftComponent = new LiftComponent(opMode.hardwareMap);
        SlideComponent slideComponent = new SlideComponent(opMode.hardwareMap);

        // Create the controllers
        this.controlSystemController = new ControlSystemController(controlSystemComponent, updateStrategy);
        this.intakeController = new IntakeController(intakeComponent, opMode.telemetry);
        this.outtakeController = new OuttakeController(outtakeComponent);
        this.liftController = new LiftController(liftComponent, opMode.telemetry);
        this.slideController = new SlideController(slideComponent, opMode.telemetry);

    }

    public Robot(ControlSystemController.Strategy strategy, Pose2d startingPosition, OpMode opMode) {
        this(strategy, startingPosition, opMode, null);
    }

    public Robot(OpMode opMode, UpdateHandler updateHandler) { this(ControlSystemController.Strategy.MANUAL, new Pose2d(0, 0, 0), opMode, updateHandler); }

    public void initialize() {

        // Initialize the controllers
        this.controlSystemController.initialize();
        this.intakeController.initialize();
        this.outtakeController.initialize();
        this.liftController.initialize();
        this.slideController.initialize();

    }

    public void update() {

        // Revalidate Cache
        this.controlSystemController.update();

        // Call Update Handler
        if(this.updateHandler != null)
            this.updateHandler.update();

        // Update Component Controllers
        this.intakeController.update();
        this.outtakeController.update();
        this.liftController.update();
        this.slideController.update();

    }

    public interface UpdateHandler { void update(); }


}
