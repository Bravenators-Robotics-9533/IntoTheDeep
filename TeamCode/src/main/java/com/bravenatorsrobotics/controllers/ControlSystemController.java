package com.bravenatorsrobotics.controllers;

import com.bravenatorsrobotics.components.ControlSystemComponent;
import com.qualcomm.hardware.lynx.LynxModule;

public class ControlSystemController extends AbstractController {

    public enum Strategy {
        AUTO,
        MANUAL
    }

    protected final ControlSystemComponent controlSystem;
    protected final Strategy strategy;

    public ControlSystemController(ControlSystemComponent controlSystem, Strategy strategy) {
        this.controlSystem = controlSystem;
        this.strategy = strategy;
    }

    public ControlSystemController(ControlSystemComponent controlSystem) {
        this(controlSystem, Strategy.AUTO);
    }

    @Override
    public void initialize() {

        this.controlSystem.setBulkCachingMode(
                this.strategy == Strategy.AUTO
                        ? LynxModule.BulkCachingMode.AUTO
                        : LynxModule.BulkCachingMode.MANUAL
        );

    }

    @Override
    public void update() {

        if(strategy == Strategy.MANUAL) {
            this.controlSystem.clearBulkCache();
        }

    }
}
