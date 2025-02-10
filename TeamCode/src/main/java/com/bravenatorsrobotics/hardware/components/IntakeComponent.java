package com.bravenatorsrobotics.hardware.components;

import com.bravenatorsrobotics.hardware.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class IntakeComponent extends AbstractComponent {

    protected final Servo flipServo;
    protected final Servo pivotServo;

    protected final Servo tensionServoLeft;
    protected final Servo tensionServoRight;

    protected final RevColorSensorV3 colorSensor;

    public IntakeComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        this.pivotServo         = super.hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_PIVOT);
        this.flipServo          = super.hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_FLIP);

        this.tensionServoLeft   = super.hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_TENSION_L);
        this.tensionServoRight  = super.hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_TENSION_R);
        this.tensionServoRight.setDirection(Servo.Direction.REVERSE);

        this.colorSensor        = super.hardwareMap.get(RevColorSensorV3.class, HardwareMapIdentities.INTAKE_COLOR_SENSOR);

    }

    public void setPivotServoPosition(double position) { this.pivotServo.setPosition(position); }
    public void setFlipServoPosition(double position) { this.flipServo.setPosition(position); }

    public void setTensionServoPositions(double position) {
        this.tensionServoLeft.setPosition(position);
        this.tensionServoRight.setPosition(position);
    }

    public double getTargetPivotServoPosition() {
        return pivotServo.getPosition();
    }

    public double getTargetFlipServoPosition() {
        return flipServo.getPosition();
    }

    public double getTensionServoPosition() {
        return tensionServoLeft.getPosition();
    }

    public double getDistanceInMM() {
        return this.colorSensor.getDistance(DistanceUnit.MM);
    }
}
