package com.bravenatorsrobotics.autonomous.autonomousActions;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.bravenatorsrobotics.robot.Robot;

/**
 * Abstract class to create an autonomous action that can be run in the autonomous opMode.
 */
public abstract class AbstractAutonomousAction {

    // The inherited robot class
    protected final Robot robot;

    // The initial position when the autonomous action was created
    protected final Pose2d initialPosition;

    public AbstractAutonomousAction(Robot robot, Pose2d initialPosition) {

        this.robot = robot;
        this.initialPosition = initialPosition;

        this.initialize();

    }

    /**
     * Initializes all of the sub-actions and objects needed for the specific autonomous action.
     */
    protected abstract void initialize();

    /**
     * Creates a primary action that encapsulates the entire autonomous action and all its sequences.
     * @return the primary action to be run by the autonomous opMode
     */
    @NonNull
    public abstract Action primaryAction();

}
