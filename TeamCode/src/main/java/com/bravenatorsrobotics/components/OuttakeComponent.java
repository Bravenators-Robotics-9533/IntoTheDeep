package com.bravenatorsrobotics.components;

import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class OuttakeComponent extends AbstractComponent {


    protected final Servo outtakeServo;

    public OuttakeComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        outtakeServo    = hardwareMap.get(Servo.class, HardwareMapIdentities.OUTTAKE_CLAW);
    }



    /**
     * @param position range [0.0, 1.0]
     */
    public void setOuttakeServoPosition(double position) {
        outtakeServo.setPosition(position);
    }

    public double getTargetOuttakeServoPosition() { return outtakeServo.getPosition(); }

}
