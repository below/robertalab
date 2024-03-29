package de.fhg.iais.roberta.ast;

import org.junit.Assert;
import org.junit.Test;

import de.fhg.iais.roberta.ast.syntax.codeGeneration.Helper;

public class TaskTest {

    @Test
    public void mainTask() throws Exception {
        String a = "BlockAST [project=[[Location [x=11, y=35], MainTask []]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/task/task_mainTask.xml"));
    }

    @Test
    public void activityTask() throws Exception {
        String a = "BlockAST [project=[[Location [x=66, y=175], ActivityTask [activityName=Var [zwei]]]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/task/task_activityTask.xml"));
    }

    @Test
    public void startActivityTask() throws Exception {
        String a = "BlockAST [project=[[Location [x=1, y=66], StartActivityTask [activityName=Var [zwei]]]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/task/task_startActivityTask.xml"));
    }

    @Test
    public void reverseTransformatinMainTask() throws Exception {
        Helper.assertTransformationIsOk("/ast/task/task_mainTask.xml");
    }

    @Test
    public void reverseTransformatinActivityTask() throws Exception {
        Helper.assertTransformationIsOk("/ast/task/task_activityTask.xml");
    }

    @Test
    public void reverseTransformatinStartActivityTask() throws Exception {
        Helper.assertTransformationIsOk("/ast/task/task_startActivityTask.xml");
    }

}
