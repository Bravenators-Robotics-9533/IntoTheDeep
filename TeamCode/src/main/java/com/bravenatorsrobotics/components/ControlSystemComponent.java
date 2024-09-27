package com.bravenatorsrobotics.components;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class ControlSystemComponent extends AbstractComponent {

    private final LynxModule[] controlHubs;

    public ControlSystemComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        List<LynxModule> lynxModules = hardwareMap.getAll(LynxModule.class);

        this.controlHubs = new LynxModule[lynxModules.size()];
        lynxModules.toArray(controlHubs);

    }

    public void setBulkCachingMode(LynxModule.BulkCachingMode bulkCachingMode) {
        for(LynxModule module : controlHubs)
            module.setBulkCachingMode(bulkCachingMode);
    }

    public void clearBulkCache() {
        for(LynxModule module : controlHubs)
            module.clearBulkCache();
    }

}
