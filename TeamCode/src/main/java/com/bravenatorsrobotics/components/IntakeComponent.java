package com.bravenatorsrobotics.components;

import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class IntakeComponent extends AbstractComponent {


    protected final Servo pivotServoY;
    protected final Servo tensionServoL;
    protected final Servo tensionServoR;

    public IntakeComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        pivotServoY      = hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_PIVOT_Y);
        tensionServoL    = hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_TENSION_L);
        tensionServoR    = hardwareMap.get(Servo.class, HardwareMapIdentities.INTAKE_TENSION_R);
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
    public void setTensionServoLPosition(double position) {
        tensionServoL.setPosition(position);
    }



    /**
     * @param position range [0.0, 1.0]
     */
    public void setTensionServoRPosition(double position) {
        tensionServoR.setPosition(position);
    }



    public double getTargetPivotServoYPosition() { return pivotServoY.getPosition(); }
    public double getTargetTensionServoLPosition() { return tensionServoL.getPosition(); }
    public double getTargetTensionServoRPosition() { return tensionServoR.getPosition(); }

}
