package de.fhg.iais.roberta.ast.syntax.stmt;

import org.junit.Test;

import de.fhg.iais.roberta.ast.syntax.codeGeneration.Helper;

public class WaitStmtTest {

    @Test
    public void test1() throws Exception {
        String a = "while(true){if(hal.isPressed(BrickKey.ENTER)==true){break;}}";

        Helper.assertCodeIsOk(a, "/ast/control/wait_stmt2.xml");
    }
}