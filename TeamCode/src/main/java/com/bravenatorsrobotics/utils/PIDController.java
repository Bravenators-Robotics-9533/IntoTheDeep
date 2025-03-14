package com.bravenatorsrobotics.utils;

public class PIDController {

    private double kP, kI, kD, kF;
    private double errorSum = 0;
    private double lastError = 0;
    private double lastTime = 0;
    private double lastOutput = 0;

    private double maxIntegral = 1.0; // Prevent integral windup
    private double alpha = 0.8; // Smoothing factor for derivative term (0.0 - 1.0)

    public PIDController(double kP, double kI, double kD, double kF) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
    }

    public double calculate(double error, double feedforward) {
        double currentTime = System.nanoTime() / 1e9;
        double deltaTime = currentTime - lastTime;
        lastTime = currentTime;

        if (deltaTime <= 0) return lastOutput; // Avoid division by zero

        // **Integral Term with Clamping**
        errorSum += error * deltaTime;
        errorSum = Math.max(-maxIntegral, Math.min(maxIntegral, errorSum));

        // **Filtered Derivative Term**
        double derivative = (error - lastError) / deltaTime;
        derivative = alpha * derivative + (1 - alpha) * lastOutput;

        lastError = error;

        // **PID Output with Feedforward**
        double output = (kP * error) + (kI * errorSum) + (kD * derivative) + (kF * feedforward);
        lastOutput = output;

        return output;
    }

    public void reset() {
        errorSum = 0;
        lastError = 0;
        lastTime = System.nanoTime() / 1e9;
    }

}
