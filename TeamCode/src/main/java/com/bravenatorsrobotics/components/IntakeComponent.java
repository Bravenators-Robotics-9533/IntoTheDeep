package com.bravenatorsrobotics.components;

import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class IntakeComponent extends AbstractComponent {

    protected final Servo pivotServo;
    protected final Servo tensionServo;

    public IntakeComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        pivotServo      = hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_PIVOT);
        tensionServo    = hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_TENSION);
    }

    /**
     * @param position range [0.0, 1.0]
     */
    public void setPivotServoPosition(double position) {
        pivotServo.setPosition(position);
    }

    /**
     * @param position range [0.0, 1.0]
     */
    public void setTensionServoPosition(double position) {
        tensionServo.setPosition(position);
    }

    public double getTargetPivotServoPosition() { return pivotServo.getPosition(); }
    public double getTargetTensionServoPosition() { return tensionServo.getPosition(); }

}
