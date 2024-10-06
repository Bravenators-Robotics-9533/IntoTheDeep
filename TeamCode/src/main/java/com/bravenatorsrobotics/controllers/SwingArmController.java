package com.bravenatorsrobotics.controllers;

import com.acmerobotics.dashboard.config.Config;
import com.bravenatorsrobotics.components.SwingArmComponent;



@Config
public class SwingArmController extends AbstractController{

    private static final double MAX_MOTOR_VELOCITY = 2800;
    public static final double SWINGARM_SPEED = 1.0;

    public static final int SWINGARM_POSITION_FOLD = 0; //needs to be filled
    public static final int SWINGARM_POSITION_REST = 0; //needs to be filled
    public static final int SWINGARM_POSITION_SPSCORING = 0; //needs to be filled
    public static final int SWINGARM_POSITION_BASKET = 0; //needs to be filled

    protected final SwingArmComponent swingArmComponent;

    public SwingArmController(SwingArmComponent swingArmComponent) {

        this.swingArmComponent = swingArmComponent;

    }


    @Override
    public void initialize() {

         


    }

    @Override
    public void update() {

    }
}