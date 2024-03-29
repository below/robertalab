package de.fhg.iais.roberta.ast.transformer;

import java.util.ArrayList;
import java.util.List;

import de.fhg.iais.roberta.ast.syntax.BlocklyBlockProperties;
import de.fhg.iais.roberta.ast.syntax.BlocklyComment;
import de.fhg.iais.roberta.ast.syntax.Phrase;
import de.fhg.iais.roberta.ast.syntax.action.ActorPort;
import de.fhg.iais.roberta.ast.syntax.action.BlinkMode;
import de.fhg.iais.roberta.ast.syntax.action.BrickLedColor;
import de.fhg.iais.roberta.ast.syntax.action.ClearDisplayAction;
import de.fhg.iais.roberta.ast.syntax.action.DriveAction;
import de.fhg.iais.roberta.ast.syntax.action.DriveDirection;
import de.fhg.iais.roberta.ast.syntax.action.LightAction;
import de.fhg.iais.roberta.ast.syntax.action.LightStatusAction;
import de.fhg.iais.roberta.ast.syntax.action.MotionParam;
import de.fhg.iais.roberta.ast.syntax.action.MotorDriveStopAction;
import de.fhg.iais.roberta.ast.syntax.action.MotorDuration;
import de.fhg.iais.roberta.ast.syntax.action.MotorGetPowerAction;
import de.fhg.iais.roberta.ast.syntax.action.MotorMoveMode;
import de.fhg.iais.roberta.ast.syntax.action.MotorOnAction;
import de.fhg.iais.roberta.ast.syntax.action.MotorSetPowerAction;
import de.fhg.iais.roberta.ast.syntax.action.MotorStopAction;
import de.fhg.iais.roberta.ast.syntax.action.MotorStopMode;
import de.fhg.iais.roberta.ast.syntax.action.PlayFileAction;
import de.fhg.iais.roberta.ast.syntax.action.ShowPicture;
import de.fhg.iais.roberta.ast.syntax.action.ShowPictureAction;
import de.fhg.iais.roberta.ast.syntax.action.ShowTextAction;
import de.fhg.iais.roberta.ast.syntax.action.ToneAction;
import de.fhg.iais.roberta.ast.syntax.action.TurnAction;
import de.fhg.iais.roberta.ast.syntax.action.TurnDirection;
import de.fhg.iais.roberta.ast.syntax.action.VolumeAction;
import de.fhg.iais.roberta.ast.syntax.expr.Binary;
import de.fhg.iais.roberta.ast.syntax.expr.BoolConst;
import de.fhg.iais.roberta.ast.syntax.expr.EmptyExpr;
import de.fhg.iais.roberta.ast.syntax.expr.EmptyList;
import de.fhg.iais.roberta.ast.syntax.expr.Expr;
import de.fhg.iais.roberta.ast.syntax.expr.ExprList;
import de.fhg.iais.roberta.ast.syntax.expr.ListCreate;
import de.fhg.iais.roberta.ast.syntax.expr.NullConst;
import de.fhg.iais.roberta.ast.syntax.expr.NumConst;
import de.fhg.iais.roberta.ast.syntax.expr.Unary;
import de.fhg.iais.roberta.ast.syntax.expr.Unary.Op;
import de.fhg.iais.roberta.ast.syntax.expr.Var;
import de.fhg.iais.roberta.ast.syntax.expr.Var.TypeVar;
import de.fhg.iais.roberta.ast.syntax.functions.Functions;
import de.fhg.iais.roberta.ast.syntax.functions.GetSubFunct;
import de.fhg.iais.roberta.ast.syntax.functions.IndexOfFunct;
import de.fhg.iais.roberta.ast.syntax.functions.LenghtOfIsEmptyFunct;
import de.fhg.iais.roberta.ast.syntax.functions.ListGetIndex;
import de.fhg.iais.roberta.ast.syntax.functions.ListRepeat;
import de.fhg.iais.roberta.ast.syntax.functions.ListSetIndex;
import de.fhg.iais.roberta.ast.syntax.functions.MathConstrainFunct;
import de.fhg.iais.roberta.ast.syntax.functions.MathNumPropFunct;
import de.fhg.iais.roberta.ast.syntax.functions.MathOnListFunct;
import de.fhg.iais.roberta.ast.syntax.functions.MathRandomFloatFunct;
import de.fhg.iais.roberta.ast.syntax.functions.MathRandomIntFunct;
import de.fhg.iais.roberta.ast.syntax.functions.MathSingleFunct;
import de.fhg.iais.roberta.ast.syntax.functions.TextChangeCaseFunct;
import de.fhg.iais.roberta.ast.syntax.functions.TextCharAtFunct;
import de.fhg.iais.roberta.ast.syntax.functions.TextJoinFunct;
import de.fhg.iais.roberta.ast.syntax.functions.TextPrintFunct;
import de.fhg.iais.roberta.ast.syntax.functions.TextPromptFunct;
import de.fhg.iais.roberta.ast.syntax.functions.TextTrimFunct;
import de.fhg.iais.roberta.ast.syntax.sensor.BrickKey;
import de.fhg.iais.roberta.ast.syntax.sensor.BrickSensor;
import de.fhg.iais.roberta.ast.syntax.sensor.ColorSensor;
import de.fhg.iais.roberta.ast.syntax.sensor.ColorSensorMode;
import de.fhg.iais.roberta.ast.syntax.sensor.EncoderSensor;
import de.fhg.iais.roberta.ast.syntax.sensor.GetSampleSensor;
import de.fhg.iais.roberta.ast.syntax.sensor.GyroSensor;
import de.fhg.iais.roberta.ast.syntax.sensor.GyroSensorMode;
import de.fhg.iais.roberta.ast.syntax.sensor.InfraredSensor;
import de.fhg.iais.roberta.ast.syntax.sensor.InfraredSensorMode;
import de.fhg.iais.roberta.ast.syntax.sensor.MotorTachoMode;
import de.fhg.iais.roberta.ast.syntax.sensor.SensorPort;
import de.fhg.iais.roberta.ast.syntax.sensor.SensorType;
import de.fhg.iais.roberta.ast.syntax.sensor.TimerSensor;
import de.fhg.iais.roberta.ast.syntax.sensor.TimerSensorMode;
import de.fhg.iais.roberta.ast.syntax.sensor.TouchSensor;
import de.fhg.iais.roberta.ast.syntax.sensor.UltrasonicSensor;
import de.fhg.iais.roberta.ast.syntax.sensor.UltrasonicSensorMode;
import de.fhg.iais.roberta.ast.syntax.stmt.AssignStmt;
import de.fhg.iais.roberta.ast.syntax.stmt.ExprStmt;
import de.fhg.iais.roberta.ast.syntax.stmt.IfStmt;
import de.fhg.iais.roberta.ast.syntax.stmt.RepeatStmt;
import de.fhg.iais.roberta.ast.syntax.stmt.RepeatStmt.Mode;
import de.fhg.iais.roberta.ast.syntax.stmt.Stmt;
import de.fhg.iais.roberta.ast.syntax.stmt.StmtFlowCon;
import de.fhg.iais.roberta.ast.syntax.stmt.StmtFlowCon.Flow;
import de.fhg.iais.roberta.ast.syntax.stmt.StmtList;
import de.fhg.iais.roberta.ast.syntax.stmt.WaitStmt;
import de.fhg.iais.roberta.ast.syntax.tasks.ActivityTask;
import de.fhg.iais.roberta.ast.syntax.tasks.Location;
import de.fhg.iais.roberta.ast.syntax.tasks.MainTask;
import de.fhg.iais.roberta.ast.syntax.tasks.StartActivityTask;
import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.blockly.generated.BlockSet;
import de.fhg.iais.roberta.blockly.generated.Field;
import de.fhg.iais.roberta.blockly.generated.Instance;
import de.fhg.iais.roberta.blockly.generated.Mutation;
import de.fhg.iais.roberta.blockly.generated.Statement;
import de.fhg.iais.roberta.blockly.generated.Value;
import de.fhg.iais.roberta.dbc.Assert;
import de.fhg.iais.roberta.dbc.DbcException;

/**
 * JAXB to AST transformer. Client should provide tree of jaxb objects.
 */
public class JaxbBlocklyProgramTransformer<V> extends JaxbAstTransformer<V> {

    private int variable_counter = 0;

    /**
     * Converts object of type {@link BlockSet} to AST tree.
     *
     * @param program
     */
    public void transform(BlockSet set) {
        List<Instance> instances = set.getInstance();
        for ( Instance instance : instances ) {
            instanceToAST(instance);
        }
    }

    private void instanceToAST(Instance instance) {
        List<Block> blocks = instance.getBlock();
        Location<V> location = Location.make(instance.getX(), instance.getY());
        this.tree.add(location);
        for ( Block block : blocks ) {
            this.tree.add(blockToAST(block));
        }
    }

    @Override
    protected Phrase<V> blockToAST(Block block) {
        BlocklyComment comment = extractComment(block);
        BlocklyBlockProperties properties = extractBlockProperties(block);

        List<Value> values;
        List<Statement> statements;
        List<Field> fields;
        List<ExprParam> exprParams;
        List<String> strParams;
        List<Expr<V>> params;
        ExprList<V> exprList;

        Phrase<V> left;
        Phrase<V> right;
        Phrase<V> expr;
        Phrase<V> var;

        String mode;
        String port;
        String op;
        MotionParam<V> mp;
        MotorDuration<V> md;

        switch ( block.getType() ) {
        // ACTION
            case "robActions_motor_on":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "MOTORPORT", (short) 0);
                values = extractValues(block, (short) 1);
                expr = extractValue(values, new ExprParam("POWER", Integer.class));
                mp = new MotionParam.Builder<V>().speed((Expr<V>) expr).build();
                return MotorOnAction.make(ActorPort.get(port), mp, properties, comment);

            case "robActions_motor_on_for":
                fields = extractFields(block, (short) 2);
                port = extractField(fields, "MOTORPORT", (short) 0);
                mode = extractField(fields, "MOTORROTATION", (short) 1);
                values = extractValues(block, (short) 2);
                left = extractValue(values, new ExprParam("POWER", Integer.class));
                right = extractValue(values, new ExprParam("VALUE", Integer.class));
                md = new MotorDuration<V>(MotorMoveMode.get(mode), (Expr<V>) right);
                mp = new MotionParam.Builder<V>().speed((Expr<V>) left).duration(md).build();
                return MotorOnAction.make(ActorPort.get(port), mp, properties, comment);

            case "robActions_motorDiff_on":
                fields = extractFields(block, (short) 1);
                mode = extractField(fields, "DIRECTION", (short) 0);
                values = extractValues(block, (short) 1);
                expr = extractValue(values, new ExprParam("POWER", Integer.class));
                mp = new MotionParam.Builder<V>().speed((Expr<V>) expr).build();
                return DriveAction.make(DriveDirection.get(mode), mp, properties, comment);

            case "robActions_motorDiff_on_for":
                fields = extractFields(block, (short) 1);
                mode = extractField(fields, "DIRECTION", (short) 0);
                values = extractValues(block, (short) 2);
                left = extractValue(values, new ExprParam("POWER", Integer.class));
                right = extractValue(values, new ExprParam("DISTANCE", Integer.class));
                md = new MotorDuration<V>(MotorMoveMode.DISTANCE, (Expr<V>) right);
                mp = new MotionParam.Builder<V>().speed((Expr<V>) left).duration(md).build();
                return DriveAction.make(DriveDirection.get(mode), mp, properties, comment);

            case "robActions_motorDiff_turn":
                fields = extractFields(block, (short) 1);
                mode = extractField(fields, "DIRECTION", (short) 0);
                values = extractValues(block, (short) 1);
                expr = extractValue(values, new ExprParam("POWER", Integer.class));
                mp = new MotionParam.Builder<V>().speed((Expr<V>) expr).build();
                return TurnAction.make(TurnDirection.get(mode), mp, properties, comment);

            case "robActions_motorDiff_turn_for":
                fields = extractFields(block, (short) 1);
                mode = extractField(fields, "DIRECTION", (short) 0);
                values = extractValues(block, (short) 2);
                left = extractValue(values, new ExprParam("POWER", Integer.class));
                right = extractValue(values, new ExprParam("DEGREE", Integer.class));
                md = new MotorDuration<V>(MotorMoveMode.DEGREE, (Expr<V>) right);
                mp = new MotionParam.Builder<V>().speed((Expr<V>) left).duration(md).build();
                return TurnAction.make(TurnDirection.get(mode), mp, properties, comment);

            case "robActions_motorDiff_stop":
                return MotorDriveStopAction.make(properties, comment);

            case "robActions_motor_getPower":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "MOTORPORT", (short) 0);
                return MotorGetPowerAction.make(ActorPort.get(port), properties, comment);

            case "robActions_motor_setPower":
                fields = extractFields(block, (short) 1);
                values = extractValues(block, (short) 1);
                port = extractField(fields, "MOTORPORT", (short) 0);
                left = extractValue(values, new ExprParam("POWER", Integer.class));
                return MotorSetPowerAction.make(ActorPort.get(port), (Expr<V>) left, properties, comment);

            case "robActions_motor_stop":
                fields = extractFields(block, (short) 2);
                port = extractField(fields, "MOTORPORT", (short) 0);
                mode = extractField(fields, "MODE", (short) 1);
                return MotorStopAction.make(ActorPort.get(port), MotorStopMode.get(mode), properties, comment);

            case "robActions_display_text":
                values = extractValues(block, (short) 3);
                Phrase<V> msg = extractValue(values, new ExprParam("OUT", String.class));
                Phrase<V> col = extractValue(values, new ExprParam("COL", Integer.class));
                Phrase<V> row = extractValue(values, new ExprParam("ROW", Integer.class));
                return ShowTextAction.make(convertPhraseToExpr(msg), (Expr<V>) col, (Expr<V>) row, properties, comment);

            case "robActions_display_picture":
                fields = extractFields(block, (short) 1);
                values = extractValues(block, (short) 2);
                String pic = extractField(fields, "PICTURE", (short) 0);
                Phrase<V> x = extractValue(values, new ExprParam("X", Integer.class));
                Phrase<V> y = extractValue(values, new ExprParam("Y", Integer.class));
                return ShowPictureAction.make(ShowPicture.get(pic), (Expr<V>) x, (Expr<V>) y, properties, comment);

            case "robActions_display_clear":
                return ClearDisplayAction.make(properties, comment);

            case "robActions_play_tone":
                values = extractValues(block, (short) 2);
                left = extractValue(values, new ExprParam("FREQUENCE", Integer.class));
                right = extractValue(values, new ExprParam("DURATION", Integer.class));
                return ToneAction.make((Expr<V>) left, (Expr<V>) right, properties, comment);

            case "robActions_play_file":
                fields = extractFields(block, (short) 1);
                String filename = extractField(fields, "FILE", (short) 0);
                return PlayFileAction.make(filename, properties, comment);

            case "robActions_play_setVolume":
                values = extractValues(block, (short) 1);
                expr = extractValue(values, new ExprParam("VOLUME", Integer.class));
                return VolumeAction.make(VolumeAction.Mode.SET, (Expr<V>) expr, properties, comment);

            case "robActions_play_getVolume":
                expr = NullConst.make(properties, comment);
                return VolumeAction.make(VolumeAction.Mode.GET, (Expr<V>) expr, properties, comment);

            case "robActions_brickLight_on":
                fields = extractFields(block, (short) 2);
                String color = extractField(fields, "SWITCH_COLOR", (short) 0);
                String blink = extractField(fields, "SWITCH_BLINK", (short) 1);
                return LightAction.make(BrickLedColor.get(color), BlinkMode.get(blink), properties, comment);

            case "robActions_brickLight_off":
                return LightStatusAction.make(LightStatusAction.Status.OFF, properties, comment);

            case "robActions_brickLight_reset":
                return LightStatusAction.make(LightStatusAction.Status.RESET, properties, comment);

                // Sensoren
            case "robSensors_touch_isPressed":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "SENSORPORT", (short) 0);
                return TouchSensor.make(SensorPort.get(port), properties, comment);

            case "robSensors_ultrasonic_setMode":
                fields = extractFields(block, (short) 2);
                port = extractField(fields, "SENSORPORT", (short) 0);
                mode = extractField(fields, "MODE", (short) 1);
                return UltrasonicSensor.make(UltrasonicSensorMode.get(mode), SensorPort.get(port), properties, comment);

            case "robSensors_ultrasonic_getMode":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "SENSORPORT", (short) 0);
                return UltrasonicSensor.make(UltrasonicSensorMode.GET_MODE, SensorPort.get(port), properties, comment);

            case "robSensors_ultrasonic_getSample":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "SENSORPORT", (short) 0);
                return UltrasonicSensor.make(UltrasonicSensorMode.GET_SAMPLE, SensorPort.get(port), properties, comment);

            case "robSensors_colour_setMode":
                fields = extractFields(block, (short) 2);
                port = extractField(fields, "SENSORPORT", (short) 0);
                mode = extractField(fields, "MODE", (short) 1);
                return ColorSensor.make(ColorSensorMode.get(mode), SensorPort.get(port), properties, comment);

            case "robSensors_colour_getMode":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "SENSORPORT", (short) 0);
                return ColorSensor.make(ColorSensorMode.GET_MODE, SensorPort.get(port), properties, comment);

            case "robSensors_colour_getSample":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "SENSORPORT", (short) 0);
                return ColorSensor.make(ColorSensorMode.GET_SAMPLE, SensorPort.get(port), properties, comment);

            case "robSensors_infrared_setMode":
                fields = extractFields(block, (short) 2);
                port = extractField(fields, "SENSORPORT", (short) 0);
                mode = extractField(fields, "MODE", (short) 1);
                return InfraredSensor.make(InfraredSensorMode.get(mode), SensorPort.get(port), properties, comment);

            case "robSensors_infrared_getMode":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "SENSORPORT", (short) 0);
                return InfraredSensor.make(InfraredSensorMode.GET_MODE, SensorPort.get(port), properties, comment);

            case "robSensors_infrared_getSample":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "SENSORPORT", (short) 0);
                return InfraredSensor.make(InfraredSensorMode.GET_SAMPLE, SensorPort.get(port), properties, comment);

            case "robSensors_encoder_setMode":
                fields = extractFields(block, (short) 2);
                port = extractField(fields, "MOTORPORT", (short) 0);
                mode = extractField(fields, "MODE", (short) 1);
                return EncoderSensor.make(MotorTachoMode.get(mode), ActorPort.get(port), properties, comment);

            case "robSensors_encoder_getMode":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "MOTORPORT", (short) 0);
                return EncoderSensor.make(MotorTachoMode.GET_MODE, ActorPort.get(port), properties, comment);

            case "robSensors_encoder_getSample":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "MOTORPORT", (short) 0);
                return EncoderSensor.make(MotorTachoMode.GET_SAMPLE, ActorPort.get(port), properties, comment);

            case "robSensors_encoder_reset":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "MOTORPORT", (short) 0);
                return EncoderSensor.make(MotorTachoMode.RESET, ActorPort.get(port), properties, comment);

            case "robSensors_key_isPressed":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "KEY", (short) 0);
                return BrickSensor.make(BrickSensor.Mode.IS_PRESSED, BrickKey.get(port), properties, comment);

                // case "robSensors_key_waitForPress":
                // fields = extractFields(block, (short) 1);
                // port = extractField(fields, "KEY", (short) 0);
                // return BrickSensor.make(BrickSensor.Mode.WAIT_FOR_PRESS,
                // BrickKey.get(port));

            case "robSensors_key_isPressedAndReleased":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "KEY", (short) 0);
                return BrickSensor.make(BrickSensor.Mode.WAIT_FOR_PRESS_AND_RELEASE, BrickKey.get(port), properties, comment);

            case "robSensors_gyro_setMode":
                fields = extractFields(block, (short) 2);
                port = extractField(fields, "SENSORPORT", (short) 0);
                mode = extractField(fields, "MODE", (short) 1);
                return GyroSensor.make(GyroSensorMode.get(mode), SensorPort.get(port), properties, comment);

            case "robSensors_gyro_getMode":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "SENSORPORT", (short) 0);
                return GyroSensor.make(GyroSensorMode.GET_MODE, SensorPort.get(port), properties, comment);

            case "robSensors_gyro_getSample":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "SENSORPORT", (short) 0);
                return GyroSensor.make(GyroSensorMode.GET_SAMPLE, SensorPort.get(port), properties, comment);

            case "robSensors_gyro_reset":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "SENSORPORT", (short) 0);
                return GyroSensor.make(GyroSensorMode.RESET, SensorPort.get(port), properties, comment);

            case "robSensors_timer_getSample":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "SENSORNUM", (short) 0);
                return TimerSensor.make(TimerSensorMode.GET_SAMPLE, Integer.valueOf(port), properties, comment);

            case "robSensors_timer_reset":
                fields = extractFields(block, (short) 1);
                port = extractField(fields, "SENSORNUM", (short) 0);
                return TimerSensor.make(TimerSensorMode.RESET, Integer.valueOf(port), properties, comment);

            case "robSensors_getSample":
                fields = extractFields(block, (short) 2);
                mode = extractField(fields, "SENSORTYPE", (short) 0);
                port = extractField(fields, SensorType.get(mode).getPortTypeName(), (short) 1);
                return GetSampleSensor.make(SensorType.get(mode), port, properties, comment);

                // Logik
            case "logic_compare":
                return blockToBinaryExpr(block, new ExprParam("A", Integer.class), new ExprParam("B", Integer.class), "OP");

            case "logic_operation":
                return blockToBinaryExpr(block, new ExprParam("A", Boolean.class), new ExprParam("B", Boolean.class), "OP");

            case "logic_negate":
                return blockToUnaryExpr(block, new ExprParam("BOOL", Boolean.class), "NOT");

            case "logic_boolean":
                return blockToConst(block, "BOOL");

            case "logic_null":
                return NullConst.make(properties, comment);

            case "logic_ternary":
                values = block.getValue();
                Assert.isTrue(values.size() <= 3, "Number of values is not less or equal to 3!");
                Phrase<V> ifExpr = extractValue(values, new ExprParam("IF", Boolean.class));
                Phrase<V> thenStmt = extractValue(values, new ExprParam("THEN", Stmt.class));
                Phrase<V> elseStmt = extractValue(values, new ExprParam("ELSE", Stmt.class));
                StmtList<V> thenList = StmtList.make();
                thenList.addStmt(ExprStmt.make((Expr<V>) thenStmt));
                thenList.setReadOnly();
                StmtList<V> elseList = StmtList.make();
                elseList.addStmt(ExprStmt.make((Expr<V>) elseStmt));
                elseList.setReadOnly();
                return IfStmt.make((Expr<V>) ifExpr, thenList, elseList, properties, comment, 0, 0);

                // Mathematik
            case "math_number":
                return blockToConst(block, "NUM");

            case "math_arithmetic":
                if ( getOperation(block, "OP").equals("POWER") ) {
                    exprParams = new ArrayList<ExprParam>();
                    exprParams.add(new ExprParam("A", Integer.class));
                    exprParams.add(new ExprParam("B", Integer.class));
                    return blockToFunction(block, exprParams, "OP");
                } else {
                    return blockToBinaryExpr(block, new ExprParam("A", Integer.class), new ExprParam("B", Integer.class), "OP");
                }

            case "math_constant":
                return blockToConst(block, "CONSTANT");

            case "math_number_property":
                boolean divisorInput = block.getMutation().isDivisorInput();
                op = extractOperation(block, "PROPERTY");
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("NUMBER_TO_CHECK", Integer.class));

                if ( op.equals("DIVISIBLE_BY") ) {
                    Assert.isTrue(divisorInput, "Divisor input is not equal to true!");
                    exprParams.add(new ExprParam("DIVISOR", Integer.class));
                }
                params = extractExprParameters(block, exprParams);
                return MathNumPropFunct.make(Functions.get(op), params, extractBlockProperties(block), extractComment(block));

            case "math_change":
                values = extractValues(block, (short) 1);
                left = extractVar(block);
                right = extractValue(values, new ExprParam("DELTA", Integer.class));
                return Binary.make(Binary.Op.MATH_CHANGE, (Expr<V>) left, (Expr<V>) right, properties, comment);

            case "math_single":
            case "math_round":
            case "math_trig":
                if ( getOperation(block, "OP").equals("NEG") ) {
                    return blockToUnaryExpr(block, new ExprParam("NUM", Integer.class), "OP");
                }
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("NUM", Integer.class));
                op = getOperation(block, "OP");
                params = extractExprParameters(block, exprParams);
                return MathSingleFunct.make(Functions.get(op), params, extractBlockProperties(block), extractComment(block));

            case "math_on_list":
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("LIST", ArrayList.class));
                op = getOperation(block, "OP");
                params = extractExprParameters(block, exprParams);
                return MathOnListFunct.make(Functions.get(op), params, extractBlockProperties(block), extractComment(block));

            case "math_modulo":
                return blockToBinaryExpr(block, new ExprParam("DIVIDEND", Integer.class), new ExprParam("DIVISOR", Integer.class), "MOD");

            case "math_constrain":
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("VALUE", Integer.class));
                exprParams.add(new ExprParam("LOW", Integer.class));
                exprParams.add(new ExprParam("HIGH", Integer.class));
                params = extractExprParameters(block, exprParams);
                return MathConstrainFunct.make(params, extractBlockProperties(block), extractComment(block));

            case "math_random_int":
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("FROM", Integer.class));
                exprParams.add(new ExprParam("TO", Integer.class));
                params = extractExprParameters(block, exprParams);
                return MathRandomIntFunct.make(params, extractBlockProperties(block), extractComment(block));

            case "math_random_float":
                return MathRandomFloatFunct.make(extractBlockProperties(block), extractComment(block));

                // TEXT
            case "text":
                return blockToConst(block, "TEXT");

            case "robText_join":
            case "text_join":
                exprList = blockToExprList(block, String.class);
                List<Expr<V>> textList = new ArrayList<Expr<V>>();
                textList.add(exprList);
                return TextJoinFunct.make(textList, properties, comment);

            case "text_append":
                values = extractValues(block, (short) 1);
                left = extractVar(block);
                right = extractValue(values, new ExprParam("TEXT", String.class));
                return Binary.make(Binary.Op.TEXT_APPEND, (Expr<V>) left, (Expr<V>) right, properties, comment);

            case "text_isEmpty":
            case "text_length":
            case "lists_length":
            case "lists_isEmpty":
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("VALUE", String.class));
                params = extractExprParameters(block, exprParams);
                return LenghtOfIsEmptyFunct.make(Functions.get(block.getType()), params, extractBlockProperties(block), extractComment(block));

            case "text_indexOf":
            case "lists_indexOf":
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("VALUE", String.class));
                exprParams.add(new ExprParam("FIND", String.class));
                op = getOperation(block, "END");
                params = extractExprParameters(block, exprParams);
                return IndexOfFunct.make(Functions.get(op), params, extractBlockProperties(block), extractComment(block));

            case "text_charAt":
                boolean atArg = block.getMutation().isAt();
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("VALUE", String.class));
                if ( atArg == true ) {
                    exprParams.add(new ExprParam("AT", Integer.class));
                }
                op = getOperation(block, "WHERE");
                params = extractExprParameters(block, exprParams);
                return TextCharAtFunct.make(Functions.get(op), params, extractBlockProperties(block), extractComment(block));

            case "text_getSubstring":
                fields = extractFields(block, (short) 2);
                strParams = new ArrayList<String>();
                strParams.add(extractField(fields, "WHERE1", (short) 0));
                strParams.add(extractField(fields, "WHERE2", (short) 1));
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("STRING", String.class));
                if ( block.getMutation().isAt1() ) {
                    exprParams.add(new ExprParam("AT1", Integer.class));
                }
                if ( block.getMutation().isAt2() ) {
                    exprParams.add(new ExprParam("AT2", Integer.class));
                }
                params = extractExprParameters(block, exprParams);
                return GetSubFunct.make(Functions.get("SUBSTRING"), strParams, params, extractBlockProperties(block), extractComment(block));

            case "text_changeCase":
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("TEXT", String.class));
                op = getOperation(block, "CASE");
                params = extractExprParameters(block, exprParams);
                return TextChangeCaseFunct.make(Functions.get(op), params, extractBlockProperties(block), extractComment(block));

            case "text_trim":
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("TEXT", String.class));
                op = getOperation(block, "MODE");
                params = extractExprParameters(block, exprParams);
                return TextTrimFunct.make(Functions.get(op), params, extractBlockProperties(block), extractComment(block));

            case "text_prompt":
                fields = extractFields(block, (short) 2);
                String type = extractField(fields, "TYPE", (short) 0);
                String text = extractField(fields, "TEXT", (short) 1);
                return TextPromptFunct.make(Functions.get(type), text, properties, comment);

            case "text_print":
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("TEXT", String.class));
                params = extractExprParameters(block, exprParams);
                return TextPrintFunct.make(params, extractBlockProperties(block), extractComment(block));

                // LISTEN
            case "lists_create_empty":
                return EmptyList.make(extractBlockProperties(block), extractComment(block));

            case "lists_create_with":
            case "robLists_create_with":
                return ListCreate.make(blockToExprList(block, ArrayList.class), extractBlockProperties(block), extractComment(block));

            case "lists_repeat":
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("ITEM", List.class));
                exprParams.add(new ExprParam("NUM", Integer.class));
                params = extractExprParameters(block, exprParams);
                return ListRepeat.make(params, extractBlockProperties(block), extractComment(block));

            case "lists_getIndex":
                fields = extractFields(block, (short) 2);
                exprParams = new ArrayList<ExprParam>();
                op = extractField(fields, "MODE", (short) 0);
                exprParams.add(new ExprParam("VALUE", String.class));
                if ( block.getMutation().isAt() ) {
                    exprParams.add(new ExprParam("AT", Integer.class));
                }
                params = extractExprParameters(block, exprParams);
                return ListGetIndex.make(
                    ListGetIndex.Mode.get(op),
                    Functions.get(extractField(fields, "WHERE", (short) 1)),
                    params,
                    extractBlockProperties(block),
                    extractComment(block));

            case "lists_setIndex":
                fields = extractFields(block, (short) 2);
                op = extractField(fields, "MODE", (short) 0);

                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("LIST", String.class));
                if ( block.getMutation().isAt() ) {
                    exprParams.add(new ExprParam("AT", Integer.class));
                }
                exprParams.add(new ExprParam("TO", Integer.class));
                params = extractExprParameters(block, exprParams);
                return ListSetIndex.make(
                    ListSetIndex.Mode.get(op),
                    Functions.get(extractField(fields, "WHERE", (short) 1)),
                    params,
                    extractBlockProperties(block),
                    extractComment(block));

            case "lists_getSublist":
                fields = extractFields(block, (short) 2);
                strParams = new ArrayList<String>();
                strParams.add(extractField(fields, "WHERE1", (short) 0));
                strParams.add(extractField(fields, "WHERE2", (short) 1));
                exprParams = new ArrayList<ExprParam>();
                exprParams.add(new ExprParam("LIST", String.class));
                if ( block.getMutation().isAt1() ) {
                    exprParams.add(new ExprParam("AT1", Integer.class));
                }
                if ( block.getMutation().isAt2() ) {
                    exprParams.add(new ExprParam("AT2", Integer.class));
                }
                params = extractExprParameters(block, exprParams);
                return GetSubFunct.make(Functions.get("GET_SUBLIST"), strParams, params, extractBlockProperties(block), extractComment(block));

            case "robColour_picker":
                return blockToConst(block, "COLOUR");

                // VARIABLEN
            case "variables_set":
                values = extractValues(block, (short) 1);
                Phrase<V> p = extractValue(values, new ExprParam("VALUE", EmptyExpr.class));
                expr = convertPhraseToExpr(p);
                return AssignStmt.make((Var<V>) extractVar(block), (Expr<V>) expr, properties, comment);

            case "variables_get":
                return extractVar(block);

                // KONTROLLE
            case "controls_if":
            case "robControls_if":
            case "robControls_ifElse":
                int _else = 0;
                int _elseIf = 0;
                if ( block.getMutation() == null ) {
                    return blocksToIfStmt(block, _else, _elseIf);
                } else {
                    Mutation mutation = block.getMutation();
                    if ( mutation.getElse() != null ) {
                        _else = mutation.getElse().intValue();
                    }
                    if ( mutation.getElseif() != null ) {
                        _elseIf = mutation.getElseif().intValue();
                        return blocksToIfStmt(block, _else, _elseIf);
                    }
                    return blocksToIfStmt(block, _else, _elseIf);
                }
            case "robControls_wait_for":
            case "robControls_wait":
                StmtList<V> statement;
                StmtList<V> list = StmtList.make();
                int mutation = block.getMutation() == null ? 0 : block.getMutation().getWait().intValue();
                if ( mutation == 0 ) {
                    values = extractValues(block, (short) (mutation + 1));
                    statements = extractStatements(block, (short) (mutation + 1));
                } else {
                    List<Object> valAndStmt = block.getRepetitions().getValueAndStatement();
                    values = new ArrayList<Value>();
                    statements = new ArrayList<Statement>();
                    convertStmtValList(values, statements, valAndStmt);
                }
                for ( int i = 0; i <= mutation; i++ ) {
                    expr = extractValue(values, new ExprParam("WAIT" + i, Boolean.class));
                    statement = extractStatement(statements, "DO" + i);
                    list.addStmt(RepeatStmt.make(Mode.WAIT, convertPhraseToExpr(expr), statement, extractBlockProperties(block), extractComment(block)));
                }
                list.setReadOnly();
                return WaitStmt.make(list, properties, comment);

            case "robControls_loopForever":
                expr = BoolConst.make(true, properties, comment);
                return extractRepeatStatement(block, expr, RepeatStmt.Mode.WHILE.toString());

            case "controls_whileUntil":
                fields = extractFields(block, (short) 1);
                mode = extractField(fields, "MODE", (short) 0);
                values = extractValues(block, (short) 1);
                if ( RepeatStmt.Mode.UNTIL == RepeatStmt.Mode.get(mode) ) {
                    expr = Unary.make(Op.NOT, convertPhraseToExpr(extractValue(values, new ExprParam("BOOL", Boolean.class))), properties, comment);
                } else {
                    expr = extractValue(values, new ExprParam("BOOL", Boolean.class));
                }
                return extractRepeatStatement(block, expr, mode);

            case "controls_for":
                var = extractVar(block);
                values = extractValues(block, (short) 3);
                exprList = ExprList.make();
                Var<V> var1 = Var.make(((Var<V>) var).getValue(), TypeVar.INTEGER, properties, comment);

                Phrase<V> from = extractValue(values, new ExprParam("FROM", Integer.class));
                Phrase<V> to = extractValue(values, new ExprParam("TO", Integer.class));
                Phrase<V> by = extractValue(values, new ExprParam("BY", Integer.class));
                Binary<V> exprAssig = Binary.make(Binary.Op.ASSIGNMENT, (Expr<V>) var1, (Expr<V>) from, properties, comment);
                Binary<V> exprCondition = Binary.make(Binary.Op.LTE, (Expr<V>) var, (Expr<V>) to, properties, comment);
                Binary<V> exprBy = Binary.make(Binary.Op.ADD_ASSIGNMENT, (Expr<V>) var, (Expr<V>) by, properties, comment);
                exprList.addExpr(exprAssig);
                exprList.addExpr(exprCondition);
                exprList.addExpr(exprBy);
                exprList.setReadOnly();
                return extractRepeatStatement(block, exprList, "FOR");

            case "controls_forEach":
                var = extractVar(block);
                values = extractValues(block, (short) 1);
                expr = extractValue(values, new ExprParam("LIST", List.class));

                Binary<V> exprBinary = Binary.make(Binary.Op.IN, (Expr<V>) var, (Expr<V>) expr, properties, comment);
                return extractRepeatStatement(block, exprBinary, "FOR_EACH");

            case "controls_flow_statements":
                fields = extractFields(block, (short) 1);
                mode = extractField(fields, "FLOW", (short) 0);
                return StmtFlowCon.make(Flow.get(mode), properties, comment);

            case "controls_repeat_ext":
                values = extractValues(block, (short) 1);
                var = Var.make("i" + this.variable_counter, TypeVar.INTEGER, properties, comment);
                exprList = ExprList.make();
                from = NumConst.make("0", properties, comment);
                to = extractValue(values, new ExprParam("TIMES", Integer.class));
                by = NumConst.make("1", properties, comment);
                exprAssig = Binary.make(Binary.Op.ASSIGNMENT, (Expr<V>) var, (Expr<V>) from, properties, comment);
                var = Var.make("i" + this.variable_counter, TypeVar.NONE, properties, comment);
                exprCondition = Binary.make(Binary.Op.LT, (Expr<V>) var, (Expr<V>) to, properties, comment);
                Unary<V> increment = Unary.make(Unary.Op.POSTFIX_INCREMENTS, (Expr<V>) var, properties, comment);
                exprList.addExpr(exprAssig);
                exprList.addExpr(exprCondition);
                exprList.addExpr(increment);
                exprList.setReadOnly();

                this.variable_counter++;
                return extractRepeatStatement(block, exprList, "TIMES");

            case "robControls_start":
                return MainTask.make(properties, comment);

            case "robControls_activity":
                values = extractValues(block, (short) 1);
                expr = extractValue(values, new ExprParam("ACTIVITY", String.class));
                return ActivityTask.make((Expr<V>) expr, properties, comment);

            case "robControls_start_activity":
                values = extractValues(block, (short) 1);
                expr = extractValue(values, new ExprParam("ACTIVITY", String.class));
                return StartActivityTask.make((Expr<V>) expr, properties, comment);

            default:
                throw new DbcException("Invalid Block: " + block.getType());
        }
    }
}
