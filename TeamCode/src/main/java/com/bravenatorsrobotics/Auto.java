package com.bravenatorsrobotics;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.config.ConfigMap;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Config
@Autonomous(name="Autonomous", group="Competition")
public class Auto extends LinearOpMode {

    private void initialize() {

        telemetry.addData("Status", "Initialize...");
        telemetry.update();

        ConfigMap.load(super.hardwareMap.appContext);

        telemetry.addData("Status", "Initialized!");
        telemetry.update();
    }

    // Make efficient
    private void onWaitForStartup() {

    }

    private void onStop() {

    }

    @Override
    public void runOpMode() throws InterruptedException {

        this.initialize();

        // Wait for startup
        while(!isStarted() && opModeIsActive())
            onWaitForStartup();

        this.run();

        this.onStop();

    }

    private void run() {

        if(!super.opModeIsActive())
            return;

    }

}
