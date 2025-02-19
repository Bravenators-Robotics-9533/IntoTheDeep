package com.bravenatorsrobotics.teleop.controlAdapters;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.bravenatorsrobotics.hardware.controllers.LiftController;
import com.bravenatorsrobotics.io.FtcGamePad;
import com.bravenatorsrobotics.robot.Robot;
import com.bravenatorsrobotics.teleop.ActionQueue;
import com.bravenatorsrobotics.teleop.TimeoutAction;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class TeleopManualControlAdapter implements IControlAdapter {

    private final OpMode opMode;
    private final Robot robot;
    private final StatusLEDControlAdapter statusLEDControlAdapter;
    private final ActionQueue actionQueue;

    private FtcGamePad driverGamePad;
    private FtcGamePad operatorGamePad;

    private FieldCentricDriveControlAdapter driveAdapter;

    private boolean isScoringHighBar = false;

    public TeleopManualControlAdapter(OpMode opMode, Robot robot, StatusLEDControlAdapter statusLEDControlAdapter, ActionQueue actionQueue) {

        this.opMode = opMode;
        this.robot = robot;
        this.statusLEDControlAdapter = statusLEDControlAdapter;
        this.actionQueue = actionQueue;

    }

    @Override
    public void initialize() {

        // Setup GamePads
        this.driverGamePad      = new FtcGamePad("Primary Driver", this.opMode.gamepad1, this::onPrimaryDriverGamePadChange);
        this.operatorGamePad    = new FtcGamePad("Secondary Driver", this.opMode.gamepad2, this::onSecondaryDriverGamePadChange);

        this.driveAdapter       = new FieldCentricDriveControlAdapter(this.opMode.gamepad1, this.robot.drive);

    }

    @Override
    public void update() {

        this.driverGamePad.update();
        this.operatorGamePad.update();

        this.driveAdapter.update();
        this.handleOuttakePivotServo();
        this.handleIntakePivotServo();
        this.handleSlide();

        this.updateStatusLED();

    }

    private void onPrimaryDriverGamePadChange(FtcGamePad gamePad, int button, boolean isPressed) {

        switch (button) {

            case FtcGamePad.GAMEPAD_BACK:
                if(isPressed)
                    this.driveAdapter.resetOffsetHeading();
                break;

            case FtcGamePad.GAMEPAD_RBUMPER:
                if(isPressed) {
                    this.driveAdapter.toggleSlowMode();
                }

                break;

        }

    }

    private void onSecondaryDriverGamePadChange(FtcGamePad gamePad, int button, boolean isPressed) {

        switch (button) {

            case FtcGamePad.GAMEPAD_BTN_X:

                if(isPressed) {

                    this.actionQueue.queueAction(new InstantAction(() -> {
                        if(this.robot.intakeController.isIntakeActive()) {
                            this.robot.intakeController.stopIntake();
                        } else {
                            this.robot.intakeController.intakeSample();
                        }
                    }));

                }

                break;

            case FtcGamePad.GAMEPAD_RBUMPER:

                if(isPressed) {
                    this.actionQueue.queueAction(new SlideToStandbyAction());
                }

                break;

            case FtcGamePad.GAMEPAD_LBUMPER:

                if(isPressed) {
                    this.actionQueue.queueAction(new SlideToRetractAction());
                }

                break;

            case FtcGamePad.GAMEPAD_RSTICK_BTN:
                if(isPressed) {
                    if(!this.robot.intakeController.isFlipPositionInPassOff()) {
                        this.robot.intakeController.toggleSnapPivotPosition();
                    }
                }

                break;

            case FtcGamePad.GAMEPAD_DPAD_RIGHT:
                if(isPressed) {
                    if(!this.robot.intakeController.isFlipPositionInPassOff()) {
                        this.robot.intakeController.toggleFlipPosition();
                    }
                }

                break;

            case FtcGamePad.GAMEPAD_DPAD_DOWN:

                if(isPressed) {

                    if(this.robot.liftController.getLiftPosition() < 25)
                        break;

                    if(this.isScoringHighBar) {
                        actionQueue.queueAction(new SequentialAction(
                                this.robot.liftController.liftToHighBarReleasePosition(),
                                this.robot.outtakeController.openWallClawAction(),
                                new InstantAction(() -> {
                                    this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.REST);
                                    this.robot.outtakeController.setPassOffClawOpen();
                                    this.robot.outtakeController.setPassOffPivotInitial();
                                    this.robot.intakeController.setFlipPositionToPassOff();
                                    this.robot.intakeController.setPivotPositionToPassOff();
                                }
                        )));

                        this.isScoringHighBar = false;
                    } else {
                        this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.REST);
                        this.robot.outtakeController.setPassOffClawOpen();
                        this.robot.outtakeController.setPassOffPivotInitial();
                        this.robot.intakeController.setFlipPositionToPassOff();
                        this.robot.intakeController.setPivotPositionToPassOff();
                    }
                }

                break;

            case FtcGamePad.GAMEPAD_BTN_CIR:

                if(isPressed) {
                    this.robot.intakeController.expelSample();
                }

                break;

            case FtcGamePad.GAMEPAD_BTN_TRI:

                if(isPressed) {
                    this.actionQueue.queueAction(new GrabAndScoreHighBasketAction());
                }

                break;

            case FtcGamePad.GAMEPAD_BTN_SQR:

                if(isPressed) {
                    this.actionQueue.queueAction(new GrabAndScoreLowBasketAction());
                }

                break;

            case FtcGamePad.GAMEPAD_LSTICK_BTN:

                if(isPressed) {
                    if(this.robot.outtakeController.isPassOffPivotInScore()) {
                        this.robot.outtakeController.setPassOffClawOpen();
                    }
                }

                break;

            case FtcGamePad.GAMEPAD_DPAD_LEFT:

                if(isPressed) {
                    this.robot.outtakeController.toggleWallClawServoPosition();
                }

                break;

            case FtcGamePad.GAMEPAD_DPAD_UP:

                if(isPressed) {
                    this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.HIGH_BAR);
                    this.isScoringHighBar = true;
                }

                break;

//            case FtcGamePad.GAMEPAD_DPAD_DOWN:
//                if(isPressed) {
//
//                    this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.REST);
//                    this.robot.outtakeController.setPassOffClawOpen();
//                    this.robot.outtakeController.setPassOffPivotInitial();
//                    this.robot.intakeController.setFlipPositionToPassOff();
//                    this.robot.intakeController.setPivotPositionToPassOff();
//
//                    this.autoDisableSlowMode();
//
//                }
//
//                break;
//
//            case FtcGamePad.GAMEPAD_DPAD_LEFT:
//                if(isPressed) {
//
//                    this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.LOW_BAR);
//                    this.robot.intakeController.setFlipPositionToPassOff();
//
//                    this.autoDisableSlowMode();
//                }
//                break;
//
//            case FtcGamePad.GAMEPAD_DPAD_UP:
//
//                if(isPressed) {
//
//                    this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.HIGH_BAR);
//                    this.robot.intakeController.expelSample();
//                    this.robot.intakeController.setFlipPositionToPassOff();
//
//                    this.autoDisableSlowMode();
//
//                }
//
//                break;
//
//            case FtcGamePad.GAMEPAD_DPAD_RIGHT:
//                if(isPressed) {
//                    this.robot.intakeController.toggleFlipPosition();
//                }
//
//                break;
//
//            case FtcGamePad.GAMEPAD_A:
//                if(isPressed) {
//                    this.robot.intakeController.intakeSample();
//                }
//
//                break;
//
//            case FtcGamePad.GAMEPAD_B:
//                if(isPressed) {
//                    this.robot.intakeController.stopIntake();
//                }
//
//                break;
//
//            case FtcGamePad.GAMEPAD_X:
//
//                if(isPressed) {
//
//                    this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.BOTTOM_BASKET);
//                    this.robot.outtakeController.setPassOffClawClosed();
//                    this.robot.intakeController.setFlipPositionToPassOff();
//                    this.robot.intakeController.expelSample();
//                    this.shouldAutoDisableSlowMode = true;
//
//                    this.driveAdapter.setSlowModeEnabled(true);
//
//                }
//
//                break;
//
//            case FtcGamePad.GAMEPAD_Y:
//
//                if(isPressed) {
//
//                    this.robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.TOP_BASKET);
//                    this.robot.outtakeController.setPassOffClawClosed();
//                    this.robot.intakeController.setFlipPositionToPassOff();
//                    this.robot.intakeController.expelSample();
//                    this.shouldAutoDisableSlowMode = true;
//
//                    this.driveAdapter.setSlowModeEnabled(true);
//
//                }
//
//                break;
//
//            case FtcGamePad.GAMEPAD_LBUMPER:
//                if(isPressed) {
//                    this.robot.outtakeController.toggleWallClawServoPosition();
//                    this.autoDisableSlowMode();
//                }
//
//                break;
//
//            case FtcGamePad.GAMEPAD_RBUMPER:
//                if(isPressed) {
//                    this.robot.intakeController.expelSample();
//                    this.autoDisableSlowMode();
//                }
//
//                break;
//
//            case FtcGamePad.GAMEPAD_RSTICK_BTN:
//                if(isPressed) {
//                    this.robot.intakeController.toggleSnapPivotPosition();
//                    this.autoDisableSlowMode();
//                }
//
//                break;
//
//            case FtcGamePad.GAMEPAD_LSTICK_BTN:
//                if(isPressed) {
//                    this.robot.outtakeController.togglePassOffClaw();
//                    this.autoDisableSlowMode();
//                }
//
//                break;

        }

    }

    private void handleOuttakePivotServo() {

        double leftJoystickValue = this.opMode.gamepad2.left_stick_y;

        if (leftJoystickValue > 0.075) { // Joystick input detected
            this.robot.outtakeController.setPassOffPivotInitial();
        } else if (leftJoystickValue < -0.075) {
            this.robot.outtakeController.setPassOffPivotScore();
        }

    }

    private void handleIntakePivotServo() {

        if(this.robot.intakeController.isFlipPositionInPassOff()) {
            return;
        }

        double joystickValue = -this.opMode.gamepad2.right_stick_x;

        if (Math.abs(joystickValue) > 0.02) { // Joystick input detected
            this.robot.intakeController.setManualPivotOffsetPosition(joystickValue);
        }

    }

    private void handleSlide() {

        double manualSlidePower = this.opMode.gamepad2.right_trigger - this.opMode.gamepad2.left_trigger;
        this.robot.slideController.setManualSlidePower(manualSlidePower);

    }

    private void updateStatusLED() {

        StatusLEDControlAdapter.State ledState = this.statusLEDControlAdapter.getState();
        boolean isInReleasePosition = this.robot.intakeController.isExpellingSample();

        if(ledState == StatusLEDControlAdapter.State.DEFAULT && isInReleasePosition)
            this.statusLEDControlAdapter.setState(StatusLEDControlAdapter.State.INDICATE_RELEASE_POSITION);
        else if(ledState == StatusLEDControlAdapter.State.INDICATE_RELEASE_POSITION && !isInReleasePosition)
            this.statusLEDControlAdapter.setState(StatusLEDControlAdapter.State.DEFAULT);

    }


    public class SlideToStandbyAction extends TimeoutAction {

        private static final int SAFE_FLIP_POSITION = 600;

        public SlideToStandbyAction() {
            super(3.0);
        }

        @Override
        public void initializeSequence() {
            robot.slideController.setSlidePosition(1.0);
            robot.intakeController.setPivotPositionToPassOff();
        }

        @Override
        public boolean runSequence(@NonNull TelemetryPacket telemetryPacket) {

            if(robot.slideController.getSlideComponent().getCurrentPosition() >= SAFE_FLIP_POSITION) {
                robot.intakeController.setFlipPositionToStandby();
                return false;
            }

            return true;

        }

    }

    public class GrabAndScoreHighBasketAction extends TimeoutAction {

        private final Action action = new SequentialAction(
                new InstantAction(() -> {
                    robot.intakeController.expelSample();
                    robot.outtakeController.setPassOffClawClosed();
                }),
                new SleepAction(0.5),
                new ParallelAction(
                        new SequentialAction(
                                new InstantAction(() -> robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.TOP_BASKET)),
                                new SleepAction(0.5),
                                new InstantAction(robot.intakeController::stopIntake)
                        ),
                        new InstantAction(robot.outtakeController::setPassOffPivotScore)
                )
        );

        public GrabAndScoreHighBasketAction() {
            super(5.0);
        }

        @Override
        public void initializeSequence() {

        }

        @Override
        public boolean runSequence(@NonNull TelemetryPacket telemetryPacket) {

            return action.run(telemetryPacket);

        }

    }

    public class GrabAndScoreLowBasketAction extends TimeoutAction {

        private final Action action = new SequentialAction(
                new InstantAction(() -> {
                    robot.intakeController.expelSample();
                    robot.outtakeController.setPassOffClawClosed();
                }),
                new SleepAction(0.5),
                new ParallelAction(
                        new SequentialAction(
                                new InstantAction(() -> robot.liftController.setTargetLiftPosition(LiftController.LiftPosition.BOTTOM_BASKET)),
                                new SleepAction(0.5),
                                new InstantAction(robot.intakeController::stopIntake)
                        ),
                        new InstantAction(robot.outtakeController::setPassOffPivotScore)
                )
        );

        public GrabAndScoreLowBasketAction() {
            super(5.0);
        }

        @Override
        public void initializeSequence() {

        }

        @Override
        public boolean runSequence(@NonNull TelemetryPacket telemetryPacket) {

            return action.run(telemetryPacket);

        }

    }

    public class SlideToRetractAction extends TimeoutAction {

        private static final double DELAY_RETRACT_SECONDS_FAST = 0.5;
        private static final double DELAY_RETRACT_SECONDS_LONG = 0.8;

        private Action action = null;

        public SlideToRetractAction() {
            super(3.0);
        }

        @Override
        public void initializeSequence() {

            double delaySeconds = robot.intakeController.isFlipPositionInIntake() ?
                                        DELAY_RETRACT_SECONDS_LONG :
                                        DELAY_RETRACT_SECONDS_FAST;

            this.action = new SequentialAction(
                    new InstantAction(() -> {
                        robot.intakeController.setPivotPositionToPassOff();
                        robot.intakeController.setFlipPositionToPassOff();
                        robot.intakeController.stopIntake();
                    }),
                    new SleepAction(delaySeconds),
                    new InstantAction(() -> {
                        robot.slideController.setSlidePosition(0.0);
                    })
            );
        }

        @Override
        public boolean runSequence(@NonNull TelemetryPacket telemetryPacket) {

            return this.action.run(telemetryPacket);

        }

    }

}
