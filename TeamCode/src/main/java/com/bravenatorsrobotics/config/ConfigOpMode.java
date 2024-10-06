package com.bravenatorsrobotics.config;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Config", group="_")
public class ConfigOpMode extends LinearOpMode {

    private static final SimpleMenu MENU = new SimpleMenu("Configuration Menu");

    @Override
    public void runOpMode() throws InterruptedException {

        ConfigMap.load(hardwareMap.appContext);

        MENU.clearOptions();

        MENU.addOption(ConfigMap.ALLIANCE_COLOR, ConfigMap.AllianceColor.class, ConfigMap.getAllianceColor());

        MENU.setGamepad(gamepad1);
        MENU.setTelemetry(telemetry);

        waitForStart();

        while(!isStopRequested()) {
            MENU.displayMenu();

            ConfigMap.setAllianceColor(ConfigMap.AllianceColor.toAllianceColor(MENU.getCurrentChoiceOf(ConfigMap.ALLIANCE_COLOR)));

            sleep(50); // Keep processor from dying lol
        }

        ConfigMap.save();

    }

}
