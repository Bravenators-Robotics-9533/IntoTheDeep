package com.bravenatorsrobotics.teleop;

public class TeleopStateManager {

    private TeleopState state = TeleopState.MANUAL;

    public TeleopStateManager() {}

    public TeleopState getState() { return this.state; }
    public void setState(TeleopState state) { this.state = state; }

}
