package com.bravenatorsrobotics.hardware.components;

import com.bravenatorsrobotics.hardware.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class IntakeComponent extends AbstractComponent {

    protected final Servo pivotServoX;
    protected final Servo pivotServoY;
    protected final Servo tensionServoL;
    protected final Servo tensionServoR;
    protected final RevColorSensorV3 colorSensor;

    public IntakeComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        pivotServoX = hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_PIVOT_X);
        pivotServoY = hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_PIVOT_Y);
        tensionServoL = hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_TENSION_L);
        tensionServoR = hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_TENSION_R);
        colorSensor = hardwareMap.get(RevColorSensorV3.class, HardwareMapIdentities.INTAKE_COLOR_SENSOR);
    }

    public void setPivotServoXPosition(double position) {
        pivotServoX.setPosition(position);
    }

    public void setPivotServoYPosition(double position) {
        pivotServoY.setPosition(position);
    }

    public void setTensionServoLPosition(double position) {
        tensionServoL.setPosition(position);
    }

    public void setTensionServoRPosition(double position) {
        tensionServoR.setPosition(position);
    }

    public double getTargetPivotServoXPosition() {
        return pivotServoX.getPosition();
    }

    public double getTargetPivotServoYPosition() {
        return pivotServoY.getPosition();
    }

    public double getTargetTensionServoLPosition() {
        return tensionServoL.getPosition();
    }

    public double getTargetTensionServoRPosition() {
        return tensionServoR.getPosition();
    }

    public String detectBlockColor() {
        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        float red = colors.red;
        float green = colors.green;
        float blue = colors.blue;

        if (red > blue && red > green) {
            return "Red";
        } else if (blue > red && blue > green) {
            return "Blue";
        } else {
            return "Unknown";
        }
    }

    public double getDistanceInCm() {
        return ((DistanceSensor) colorSensor).getDistance(DistanceUnit.INCH) * 2.54; // Convert inches to centimeters
    }
}
