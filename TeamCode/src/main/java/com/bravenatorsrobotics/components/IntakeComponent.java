package com.bravenatorsrobotics.components;

import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class IntakeComponent extends AbstractComponent {

    protected final Servo pivotServoX;
    protected final Servo pivotServoY;
    protected final Servo tensionServo;

    public IntakeComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        pivotServoX      = hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_PIVOT_X);
        pivotServoY      = hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_PIVOT_Y);
        tensionServo    = hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_TENSION);
    }

    /**
     * @param position range [0.0, 1.0]
     */
    public void setPivotServoXPosition(double position) {
        pivotServoX.setPosition(position);
    }



    /**
     * @param position range [0.0, 1.0]
     */
    public void setPivotServoYPosition(double position) {
        pivotServoY.setPosition(position);
    }



    /**
     * @param position range [0.0, 1.0]
     */
    public void setTensionServoPosition(double position) {
        tensionServo.setPosition(position);
    }

    public double getTargetPivotServoXPosition() { return pivotServoX.getPosition(); }
    public double getTargetPivotServoYPosition() { return pivotServoY.getPosition(); }
    public double getTargetTensionServoPosition() { return tensionServo.getPosition(); }

}
