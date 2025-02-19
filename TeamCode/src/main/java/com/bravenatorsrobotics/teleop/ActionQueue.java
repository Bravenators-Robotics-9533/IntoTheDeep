package com.bravenatorsrobotics.teleop;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

public class ActionQueue {

    private final ArrayList<Action> actionQueue = new ArrayList<>();

    public void queueAction(Action action) {
        actionQueue.add(action);
    }

    public void printActions(Telemetry telemetry) {

        telemetry.addData("Number of Active Actions", this.actionQueue.size());

    }

    public void update(TelemetryPacket packet) {



        this.actionQueue.removeIf(action -> {

            action.preview(packet.fieldOverlay());
            return !action.run(packet);

        });

    }

}
