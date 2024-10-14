package com.bravenatorsrobotics.components;

import com.bravenatorsrobotics.HardwareMapIdentities;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/*

    THIS CLASS LOOKS FINE BUT HONESTLY IT MAY BE WORTH JUST NOT EVEN HAVING ANOTHER COMPONENT
    BECAUSE IT REALLY ISN'T LIKE ANOTHER COMPONENT. YOU MAY WANT TO LUMP THIS HINGE ARM MOTOR IN
    WITH THE SWING ARM COMPONENT BECAUSE THEY ARE PRETTY MUCH THE SAME THING. YOU CAN DO IT THIS WAY
    BUT LIKE AS AN ANALOGY YOU MAY HAVE A HAND COMPONENT THAT SPLITS INTO WRIST COMPONENTS KNUCKLE COMPONENTS
    ETC. OR YOU COULD JUST PUT THE LOGIC FOR EACH KNUCKLE AND WRIST IN THE SAME CLASS

    PERSONALLY I WOULD PUT THIS LOGIC INTO THE SWING ARM COMPONENT AND SWING ARM CONTROLLER CLASS

    YOU COULD ALSO LOCALIZE THE VARIABLE NAMES. JUST IN THE CODE YOU COULD CALL THE VARIABLE FOR THIS
    THE KNUCKLE MOTOR AND THE MAIN SWING ARM MOTOR THE WRIST MOTOR. IT'S ACTUALLY COMMON PRACTICE TO DO
    THIS ON A ROBOTIC ARM. YOU COULD ALSO MAKE THE SWING ARM MAIN MOTOR BE THE ELBOW MOTOR AND THIS ONE
    BE CALLED THE WRIST MOTOR. YOU GET WHAT I'M SAYING. YOU DON'T HAVE TO DO ANYTHING THOUGH. THIS IS
    TECHNICALLY FINE, I JUST PERSONALLY WOULDN'T DO IT THIS WAY

 */

public class HingeArmComponent extends AbstractComponent {

    private static final int HINGE_ARM_TOLERANCE = 10;

    protected final DcMotorEx hingeArmMotor;

    public HingeArmComponent(HardwareMap hardwareMap) {
        super(hardwareMap);

        this.hingeArmMotor = hardwareMap.get(DcMotorEx.class, HardwareMapIdentities.HINGE_ARM_MOTOR);
        this.hingeArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.hingeArmMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.hingeArmMotor.setTargetPositionTolerance(HINGE_ARM_TOLERANCE);

        this.hingeArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.hingeArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     *
     * @param power range [-1.0, 1.0]
     */
    public void setPower(double power) {

        // TODO: RECALCULATE WITH VELOCITY
        this.hingeArmMotor.setPower(power);

    }

    public void setTargetPosition(int targetPosition) { this.hingeArmMotor.setTargetPosition(targetPosition); }
    public int getTargetPosition() { return this.hingeArmMotor.getTargetPosition(); }

    public void setMode(DcMotor.RunMode runMode) { this.hingeArmMotor.setMode(runMode); }
    public DcMotor.RunMode getMode() { return this.hingeArmMotor.getMode(); }

    public int getCurrentPosition() { return this.hingeArmMotor.getCurrentPosition(); }

    public boolean isMotorBusy() { return this.hingeArmMotor.isBusy(); }

}
