package com.bravenatorsrobotics.teleop;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.util.ElapsedTime;

public abstract class TimeoutAction implements Action {

    private final ElapsedTime timer;
    private final double timeoutSeconds;

    private boolean isInitialized = false;

    public TimeoutAction(double timeoutSeconds) {
        this.timer = new ElapsedTime();
        this.timer.reset();

        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public boolean run(@NonNull TelemetryPacket telemetryPacket) {

        // TIMEOUT
        if(timer.seconds() >= timeoutSeconds)
            return false;

        if(!this.isInitialized) {
            this.initializeSequence();
            this.isInitialized = true;
        }

        return runSequence(telemetryPacket);
    }

    public abstract void initializeSequence();
    public abstract boolean runSequence(@NonNull TelemetryPacket telemetryPacket);

}
