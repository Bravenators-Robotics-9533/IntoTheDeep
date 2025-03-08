package com.bravenatorsrobotics.utils;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.ArrayList;

@Autonomous(name="Wheel Test", group = "Test")
public class WheelTestOpMode extends LinearOpMode {

    class InfoPacket {

        public final double nanoSeconds;
        public final double vel;

        public InfoPacket(double nanoSeconds, double vel) {
            this.nanoSeconds = nanoSeconds;
            this.vel = vel;
        }

    }

    private double average(ArrayList<InfoPacket> infoPackets) {

        double runningAverage = 0;

        for(InfoPacket infoPacket : infoPackets) {
            runningAverage += infoPacket.vel;
        }

        return runningAverage / infoPackets.size();

    }

    @Override
    public void runOpMode() throws InterruptedException {

        DcMotorEx fl = super.hardwareMap.get(DcMotorEx.class, "par0");
        DcMotorEx fr = super.hardwareMap.get(DcMotorEx.class, "perp");
        DcMotorEx bl = super.hardwareMap.get(DcMotorEx.class, "par1");
        DcMotorEx br = super.hardwareMap.get(DcMotorEx.class, "br");

        ElapsedTime timer = new ElapsedTime();

        waitForStart();
        timer.reset();

        fl.setPower(1.0);
        fr.setPower(1.0);
        bl.setPower(1.0);
        br.setPower(1.0);

        while(opModeIsActive() && timer.seconds() < 2) {}

        timer.reset();

        ArrayList<InfoPacket> flInfo = new ArrayList<>();
        ArrayList<InfoPacket> frInfo = new ArrayList<>();
        ArrayList<InfoPacket> blInfo = new ArrayList<>();
        ArrayList<InfoPacket> brInfo = new ArrayList<>();

        while(opModeIsActive() && timer.seconds() < 10) {

            flInfo.add(new InfoPacket(timer.nanoseconds(), fl.getVelocity(AngleUnit.RADIANS)));
            frInfo.add(new InfoPacket(timer.nanoseconds(), fr.getVelocity(AngleUnit.RADIANS)));
            blInfo.add(new InfoPacket(timer.nanoseconds(), bl.getVelocity(AngleUnit.RADIANS)));
            brInfo.add(new InfoPacket(timer.nanoseconds(), br.getVelocity(AngleUnit.RADIANS)));

        }

        fl.setPower(0);
        fr.setPower(0);
        bl.setPower(0);
        br.setPower(0);

        double flA = average(flInfo);
        double frA = average(frInfo);
        double blA = average(blInfo);
        double brA = average(brInfo);

        telemetry.addData("FL vA", flA);
        telemetry.addData("FR vA", frA);
        telemetry.addData("BL vA", blA);
        telemetry.addData("BR vA", brA);
        telemetry.update();

        while(opModeIsActive()) {}

    }

}