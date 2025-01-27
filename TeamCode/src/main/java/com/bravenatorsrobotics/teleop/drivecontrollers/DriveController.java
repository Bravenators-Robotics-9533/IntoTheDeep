package com.bravenatorsrobotics.teleop.drivecontrollers;

import com.acmerobotics.roadrunner.drive.Drive;
import com.qualcomm.robotcore.hardware.Gamepad;

public abstract class DriveController {

    protected final Gamepad gamepad;

    public DriveController(Gamepad gamepad) {

        this.gamepad = gamepad;

    }

    public abstract void update();

}
