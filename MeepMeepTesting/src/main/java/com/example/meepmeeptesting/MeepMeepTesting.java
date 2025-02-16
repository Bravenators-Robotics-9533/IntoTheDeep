package com.example.meepmeeptesting;


import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {

    public static void main(String[] args) {

        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(new Vector2d(-5, 33), Math.toRadians(270)))
//                .setTangent(Math.toRadians(120))
                .strafeToConstantHeading(new Vector2d(-5, 35))
                .splineToLinearHeading(new Pose2d(new Vector2d(-37.5, 10), Math.toRadians(90)), new Rotation2d(Math.toRadians(0), Math.toRadians(-180)))
//                .splineToConstantHeading(new Vector2d(-50, 64), new Rotation2d(Math.toRadians(0), Math.toRadians(90)))
//                .splineToConstantHeading(new Vector2d(-34, 20), new Rotation2d(Math.toRadians(0), Math.toRadians(-180)))
//                .strafeToConstantHeading(new Vector2d(-50, 8))
//                .strafeToConstantHeading(new Vector2d(-50, 60))
//                .strafeToConstantHeading(new Vector2d(-40, 8))
//                .strafeToConstantHeading(new Vector2d(-60, 60))
            .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();

    }

}