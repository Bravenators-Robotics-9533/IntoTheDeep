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

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(51.5, 54, Math.toRadians(415)))
                .strafeToLinearHeading(new Vector2d(40, 10), Math.toRadians(180))
                .strafeToConstantHeading(new Vector2d(20, 10))
//                .splineToLinearHeading(new Pose2d(new Vector2d(20, 10), Math.toRadians(180)), Math.toRadians(90))

//                        .strafeToLinearHeading(new Vector2d(-9, 40), Math.toRadians(270))
//                        .strafeToConstantHeading(new Vector2d(-9, 32.5))
//                .splineToLinearHeading(new Pose2d(new Vector2d(-9, 32.5), Math.toRadians(270)), new Rotation2d(Math.toRadians(0), Math.toRadians(-90)))


//                .splineToConstantHeading(new Vector2d(-54, 25), Math.toRadians(90))
//                .strafeTo(new Vector2d(-54, 58))
        .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();

    }

}