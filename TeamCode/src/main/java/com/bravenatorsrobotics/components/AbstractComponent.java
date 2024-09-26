package com.bravenatorsrobotics.components;

import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class AbstractComponent {

    protected final HardwareMap hardwareMap;

    public AbstractComponent(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }

}
