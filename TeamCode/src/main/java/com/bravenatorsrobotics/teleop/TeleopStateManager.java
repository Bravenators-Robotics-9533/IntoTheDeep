package com.bravenatorsrobotics.teleop;

public class TeleopStateManager {

    public interface OnTeleopStateChangeCallback { void onTeleopStateChangeCallback(TeleopState previousState, TeleopState currentState); }

    private TeleopState state = TeleopState.MANUAL;
    private OnTeleopStateChangeCallback callback = null;

    public TeleopStateManager() {}

    public TeleopStateManager(OnTeleopStateChangeCallback callback) {
        this.callback = callback;
    }

    public TeleopState getState() { return this.state; }

    public void setState(TeleopState state) {

        if(this.callback != null) {
            this.callback.onTeleopStateChangeCallback(this.state, state);
        }

        this.state = state;
    }

}
