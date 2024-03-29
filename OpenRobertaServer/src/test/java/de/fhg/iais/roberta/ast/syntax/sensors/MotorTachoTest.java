package de.fhg.iais.roberta.ast.syntax.sensors;

import org.junit.Test;

import de.fhg.iais.roberta.ast.syntax.codeGeneration.Helper;

public class MotorTachoTest {

    @Test
    public void setMotorTacho() throws Exception {
        String a = "\nhal.setMotorTachoMode(ActorPort.A, MotorTachoMode.ROTATION);";

        Helper.assertCodeIsOk(a, "/ast/sensors/sensor_setEncoder.xml");
    }

    @Test
    public void getMotorTachoMode() throws Exception {
        String a = "\nhal.getMotorTachoMode(ActorPort.A)";

        Helper.assertCodeIsOk(a, "/ast/sensors/sensor_getModeEncoder.xml");
    }

    @Test
    public void getSampleMotorTacho() throws Exception {
        String a = "\nhal.getRegulatedMotorTachoValue(ActorPort.A)";

        Helper.assertCodeIsOk(a, "/ast/sensors/sensor_getSampleEncoder.xml");
    }

    @Test
    public void resetMotorTacho() throws Exception {
        String a = "\nhal.resetMotorTacho(ActorPort.A);";

        Helper.assertCodeIsOk(a, "/ast/sensors/sensor_resetEncoder.xml");
    }
}
