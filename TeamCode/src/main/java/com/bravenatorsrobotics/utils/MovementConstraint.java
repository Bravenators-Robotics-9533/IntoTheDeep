package com.bravenatorsrobotics.utils;

public class MovementConstraint {

    public final double independentConstraintValue;
    public final double dependentConstraintValue;

    public MovementConstraint(double independentConstraintValue, double dependentConstraintValue) {

        this.independentConstraintValue = independentConstraintValue;
        this.dependentConstraintValue = dependentConstraintValue;

    }

    public double deriveDependentValue(double independentActual, double dependentActual) {

        if(dependentActual > dependentConstraintValue && independentActual < independentConstraintValue)
            return dependentConstraintValue;

        return dependentActual;

    }

}
