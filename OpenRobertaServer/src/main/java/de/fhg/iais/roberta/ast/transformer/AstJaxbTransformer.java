package de.fhg.iais.roberta.ast.transformer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import de.fhg.iais.roberta.ast.syntax.Phrase;
import de.fhg.iais.roberta.ast.syntax.Phrase.Kind;
import de.fhg.iais.roberta.ast.syntax.action.DriveAction;
import de.fhg.iais.roberta.ast.syntax.action.LightAction;
import de.fhg.iais.roberta.ast.syntax.action.MotorGetPowerAction;
import de.fhg.iais.roberta.ast.syntax.action.MotorOnAction;
import de.fhg.iais.roberta.ast.syntax.action.MotorSetPowerAction;
import de.fhg.iais.roberta.ast.syntax.action.MotorStopAction;
import de.fhg.iais.roberta.ast.syntax.action.PlayFileAction;
import de.fhg.iais.roberta.ast.syntax.action.ShowPictureAction;
import de.fhg.iais.roberta.ast.syntax.action.ShowTextAction;
import de.fhg.iais.roberta.ast.syntax.action.ToneAction;
import de.fhg.iais.roberta.ast.syntax.action.TurnAction;
import de.fhg.iais.roberta.ast.syntax.action.VolumeAction;
import de.fhg.iais.roberta.ast.syntax.expr.Binary;
import de.fhg.iais.roberta.ast.syntax.expr.BoolConst;
import de.fhg.iais.roberta.ast.syntax.expr.ColorConst;
import de.fhg.iais.roberta.ast.syntax.expr.ExprList;
import de.fhg.iais.roberta.ast.syntax.expr.FunctionExpr;
import de.fhg.iais.roberta.ast.syntax.expr.ListCreate;
import de.fhg.iais.roberta.ast.syntax.expr.MathConst;
import de.fhg.iais.roberta.ast.syntax.expr.NumConst;
import de.fhg.iais.roberta.ast.syntax.expr.SensorExpr;
import de.fhg.iais.roberta.ast.syntax.expr.StringConst;
import de.fhg.iais.roberta.ast.syntax.expr.Unary;
import de.fhg.iais.roberta.ast.syntax.expr.Var;
import de.fhg.iais.roberta.ast.syntax.functions.Func;
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
import de.fhg.iais.roberta.ast.syntax.functions.MathRandomIntFunct;
import de.fhg.iais.roberta.ast.syntax.functions.MathSingleFunct;
import de.fhg.iais.roberta.ast.syntax.functions.TextChangeCaseFunct;
import de.fhg.iais.roberta.ast.syntax.functions.TextCharAtFunct;
import de.fhg.iais.roberta.ast.syntax.functions.TextJoinFunct;
import de.fhg.iais.roberta.ast.syntax.functions.TextPrintFunct;
import de.fhg.iais.roberta.ast.syntax.functions.TextPromptFunct;
import de.fhg.iais.roberta.ast.syntax.functions.TextTrimFunct;
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
import de.fhg.iais.roberta.ast.syntax.sensor.TimerSensor;
import de.fhg.iais.roberta.ast.syntax.sensor.TouchSensor;
import de.fhg.iais.roberta.ast.syntax.sensor.UltrasonicSensor;
import de.fhg.iais.roberta.ast.syntax.sensor.UltrasonicSensorMode;
import de.fhg.iais.roberta.ast.syntax.stmt.ActionStmt;
import de.fhg.iais.roberta.ast.syntax.stmt.AssignStmt;
import de.fhg.iais.roberta.ast.syntax.stmt.ExprStmt;
import de.fhg.iais.roberta.ast.syntax.stmt.IfStmt;
import de.fhg.iais.roberta.ast.syntax.stmt.RepeatStmt;
import de.fhg.iais.roberta.ast.syntax.stmt.SensorStmt;
import de.fhg.iais.roberta.ast.syntax.stmt.Stmt;
import de.fhg.iais.roberta.ast.syntax.stmt.StmtFlowCon;
import de.fhg.iais.roberta.ast.syntax.stmt.StmtList;
import de.fhg.iais.roberta.ast.syntax.stmt.WaitStmt;
import de.fhg.iais.roberta.ast.syntax.tasks.ActivityTask;
import de.fhg.iais.roberta.ast.syntax.tasks.StartActivityTask;
import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.blockly.generated.Comment;
import de.fhg.iais.roberta.blockly.generated.Field;
import de.fhg.iais.roberta.blockly.generated.Mutation;
import de.fhg.iais.roberta.blockly.generated.Repetitions;
import de.fhg.iais.roberta.blockly.generated.Statement;
import de.fhg.iais.roberta.blockly.generated.Value;
import de.fhg.iais.roberta.dbc.Assert;
import de.fhg.iais.roberta.dbc.DbcException;

public class AstJaxbTransformer<V> {

    public Block astToBlock(Phrase<V> astSource) {
        String fieldValue;
        Phrase<V> value;
        Block jaxbDestination = new Block();
        Mutation mutation;

        setBasicProperties(astSource, jaxbDestination);

        switch ( astSource.getKind() ) {
            case BOOL_CONST:
                fieldValue = String.valueOf(((BoolConst<V>) astSource).isValue()).toUpperCase();
                addField(jaxbDestination, "BOOL", fieldValue);

                return jaxbDestination;

            case PICK_COLOR_CONST:
                fieldValue = ((ColorConst<V>) astSource).getValue().getHex();
                addField(jaxbDestination, "COLOUR", fieldValue.toLowerCase());

                return jaxbDestination;

            case MATH_CONST:
                fieldValue = ((MathConst<V>) astSource).getMathConst().name();
                addField(jaxbDestination, "CONSTANT", fieldValue);

                return jaxbDestination;

            case NULL_CONST:
                return jaxbDestination;

            case STRING_CONST:
                fieldValue = ((StringConst<V>) astSource).getValue();
                addField(jaxbDestination, "TEXT", fieldValue);

                return jaxbDestination;

            case NUM_CONST:
                fieldValue = ((NumConst<V>) astSource).getValue();
                addField(jaxbDestination, "NUM", fieldValue);

                return jaxbDestination;

            case VAR:
                fieldValue = ((Var<V>) astSource).getValue();
                addField(jaxbDestination, "VAR", fieldValue);

                return jaxbDestination;

            case UNARY:
                value = ((Unary<V>) astSource).getExpr();
                if ( astSource.getProperty().getBlockType().equals("math_single") ) {
                    fieldValue = ((Unary<V>) astSource).getOp().name();
                    addField(jaxbDestination, "OP", fieldValue);
                    addValue(jaxbDestination, "NUM", value);
                } else {
                    addValue(jaxbDestination, "BOOL", value);
                }

                return jaxbDestination;

            case BINARY:
                Binary<V> binary = (Binary<V>) astSource;
                switch ( binary.getOp() ) {

                    case MATH_CHANGE:
                        addField(jaxbDestination, "VAR", ((Var<V>) binary.getLeft()).getValue());
                        addValue(jaxbDestination, "DELTA", binary.getRight());
                        return jaxbDestination;
                    case TEXT_APPEND:
                        addField(jaxbDestination, "VAR", ((Var<V>) binary.getLeft()).getValue());
                        addValue(jaxbDestination, "TEXT", binary.getRight());
                        return jaxbDestination;

                    case MOD:
                        addValue(jaxbDestination, "DIVIDEND", binary.getLeft());
                        addValue(jaxbDestination, "DIVISOR", binary.getRight());
                        return jaxbDestination;

                    default:
                        addField(jaxbDestination, "OP", binary.getOp().name());
                        addValue(jaxbDestination, "A", binary.getLeft());
                        addValue(jaxbDestination, "B", binary.getRight());
                        return jaxbDestination;

                }

            case SENSOR_EXPR:
                return astToBlock(((SensorExpr<V>) astSource).getSens());

            case FUNCTION_EXPR:
                return astToBlock(((FunctionExpr<V>) astSource).getFunction());

            case EMPTY_LIST:
                return jaxbDestination;

            case CLEAR_DISPLAY_ACTION:
                return jaxbDestination;

            case DRIVE_ACTION:
                DriveAction<V> driveAction = (DriveAction<V>) astSource;

                fieldValue = driveAction.getDirection().name();
                value = driveAction.getParam().getSpeed();

                addField(jaxbDestination, "DIRECTION", fieldValue);
                addValue(jaxbDestination, "POWER", value);

                if ( driveAction.getParam().getDuration() != null ) {
                    addValue(jaxbDestination, driveAction.getParam().getDuration().getType().name(), driveAction.getParam().getDuration().getValue());
                }

                return jaxbDestination;

            case LIGHT_ACTION:
                LightAction<V> lightAction = (LightAction<V>) astSource;

                addField(jaxbDestination, "SWITCH_COLOR", lightAction.getColor().name());
                addField(jaxbDestination, "SWITCH_BLINK", lightAction.getBlinkMode().name());

                return jaxbDestination;

            case LIGHT_STATUS_ACTION:
                return jaxbDestination;

            case STOP_ACTION:
                return jaxbDestination;

            case MOTOR_GET_POWER_ACTION:
                fieldValue = ((MotorGetPowerAction<V>) astSource).getPort().name();
                addField(jaxbDestination, "MOTORPORT", fieldValue);

                return jaxbDestination;

            case MOTOR_ON_ACTION:
                MotorOnAction<V> motorOnAction = (MotorOnAction<V>) astSource;
                fieldValue = motorOnAction.getPort().name();
                value = motorOnAction.getParam().getSpeed();

                addField(jaxbDestination, "MOTORPORT", fieldValue);
                addValue(jaxbDestination, "POWER", value);

                if ( motorOnAction.getParam().getDuration() != null ) {
                    addField(jaxbDestination, "MOTORROTATION", motorOnAction.getDurationMode().name());
                    addValue(jaxbDestination, "VALUE", motorOnAction.getDurationValue());
                }

                return jaxbDestination;

            case MOTOR_SET_POWER_ACTION:
                MotorSetPowerAction<V> motorSetPowerAction = (MotorSetPowerAction<V>) astSource;
                fieldValue = motorSetPowerAction.getPort().name();
                value = motorSetPowerAction.getPower();

                addField(jaxbDestination, "MOTORPORT", fieldValue);
                addValue(jaxbDestination, "POWER", value);

                return jaxbDestination;

            case MOTOR_STOP_ACTION:
                MotorStopAction<V> motorStopAction = (MotorStopAction<V>) astSource;
                fieldValue = motorStopAction.getPort().name();
                addField(jaxbDestination, "MOTORPORT", fieldValue);
                addField(jaxbDestination, "MODE", motorStopAction.getMode().name());

                return jaxbDestination;

            case PLAY_FILE_ACTION:
                fieldValue = ((PlayFileAction<V>) astSource).getFileName();
                addField(jaxbDestination, "FILE", fieldValue);

                return jaxbDestination;

            case SHOW_PICTURE_ACTION:
                fieldValue = ((ShowPictureAction<V>) astSource).getPicture().name();
                addField(jaxbDestination, "PICTURE", fieldValue);
                addValue(jaxbDestination, "X", ((ShowPictureAction<V>) astSource).getX());
                addValue(jaxbDestination, "Y", ((ShowPictureAction<V>) astSource).getY());

                return jaxbDestination;

            case SHOW_TEXT_ACTION:
                addValue(jaxbDestination, "OUT", ((ShowTextAction<V>) astSource).getMsg());
                addValue(jaxbDestination, "COL", ((ShowTextAction<V>) astSource).getX());
                addValue(jaxbDestination, "ROW", ((ShowTextAction<V>) astSource).getY());

                return jaxbDestination;

            case TONE_ACTION:
                addValue(jaxbDestination, "FREQUENCE", ((ToneAction<V>) astSource).getFrequency());
                addValue(jaxbDestination, "DURATION", ((ToneAction<V>) astSource).getDuration());

                return jaxbDestination;

            case TURN_ACTION:
                TurnAction<V> turnAction = (TurnAction<V>) astSource;
                fieldValue = turnAction.getDirection().name();
                value = turnAction.getParam().getSpeed();

                addField(jaxbDestination, "DIRECTION", fieldValue);
                addValue(jaxbDestination, "POWER", value);

                if ( turnAction.getParam().getDuration() != null ) {
                    addValue(jaxbDestination, turnAction.getParam().getDuration().getType().name(), turnAction.getParam().getDuration().getValue());
                }

                return jaxbDestination;

            case VOLUME_ACTION:
                if ( ((VolumeAction<V>) astSource).getMode() == VolumeAction.Mode.SET ) {
                    addValue(jaxbDestination, "VOLUME", ((VolumeAction<V>) astSource).getVolume());
                }

                return jaxbDestination;

            case TOUCH_SENSING:
                fieldValue = ((TouchSensor<V>) astSource).getPort().getPortNumber();
                addField(jaxbDestination, "SENSORPORT", fieldValue);

                return jaxbDestination;

            case ULTRASONIC_SENSING:
                UltrasonicSensor<V> ultrasonicSensor = (UltrasonicSensor<V>) astSource;
                fieldValue = ultrasonicSensor.getPort().getPortNumber();
                addField(jaxbDestination, "SENSORPORT", fieldValue);
                if ( ultrasonicSensor.getMode() != UltrasonicSensorMode.GET_MODE && ultrasonicSensor.getMode() != UltrasonicSensorMode.GET_SAMPLE ) {
                    addField(jaxbDestination, "MODE", ultrasonicSensor.getMode().name());
                }

                return jaxbDestination;

            case COLOR_SENSING:
                ColorSensor<V> colorSensor = (ColorSensor<V>) astSource;
                fieldValue = colorSensor.getPort().getPortNumber();
                addField(jaxbDestination, "SENSORPORT", fieldValue);
                if ( colorSensor.getMode() != ColorSensorMode.GET_MODE && colorSensor.getMode() != ColorSensorMode.GET_SAMPLE ) {
                    addField(jaxbDestination, "MODE", colorSensor.getMode().name());
                }

                return jaxbDestination;

            case INFRARED_SENSING:
                InfraredSensor<V> infraredSensor = (InfraredSensor<V>) astSource;
                fieldValue = infraredSensor.getPort().getPortNumber();
                addField(jaxbDestination, "SENSORPORT", fieldValue);
                if ( infraredSensor.getMode() != InfraredSensorMode.GET_MODE && infraredSensor.getMode() != InfraredSensorMode.GET_SAMPLE ) {
                    addField(jaxbDestination, "MODE", infraredSensor.getMode().name());
                }

                return jaxbDestination;

            case ENCODER_SENSING:
                EncoderSensor<V> encoderSensor = (EncoderSensor<V>) astSource;
                fieldValue = encoderSensor.getMotor().name();
                addField(jaxbDestination, "MOTORPORT", fieldValue);
                if ( encoderSensor.getMode() == MotorTachoMode.DEGREE || encoderSensor.getMode() == MotorTachoMode.ROTATION ) {
                    addField(jaxbDestination, "MODE", encoderSensor.getMode().name());
                }

                return jaxbDestination;

            case BRICK_SENSIG:
                fieldValue = ((BrickSensor<V>) astSource).getKey().name();
                addField(jaxbDestination, "KEY", fieldValue);

                return jaxbDestination;

            case GYRO_SENSIG:
                GyroSensor<V> gyroSensor = (GyroSensor<V>) astSource;
                fieldValue = gyroSensor.getPort().getPortNumber();
                addField(jaxbDestination, "SENSORPORT", fieldValue);
                if ( gyroSensor.getMode() == GyroSensorMode.ANGLE || gyroSensor.getMode() == GyroSensorMode.RATE ) {
                    addField(jaxbDestination, "MODE", gyroSensor.getMode().name());
                }

                return jaxbDestination;

            case TIMER_SENSING:
                fieldValue = String.valueOf(((TimerSensor<V>) astSource).getTimer());
                addField(jaxbDestination, "SENSORNUM", fieldValue);
                return jaxbDestination;

            case SENSOR_GET_SAMPLE:
                GetSampleSensor<V> getSampleSensor = (GetSampleSensor<V>) astSource;
                mutation = new Mutation();
                mutation.setInput(getSampleSensor.getSensorType().name());
                jaxbDestination.setMutation(mutation);
                addField(jaxbDestination, "SENSORTYPE", getSampleSensor.getSensorType().name());
                addField(jaxbDestination, getSampleSensor.getSensorType().getPortTypeName(), getSampleSensor.getSensorPort());

                return jaxbDestination;

            case EXPR_STMT:
                return astToBlock(((ExprStmt<V>) astSource).getExpr());

            case AKTION_STMT:
                return astToBlock(((ActionStmt<V>) astSource).getAction());

            case SENSOR_STMT:
                return astToBlock(((SensorStmt<V>) astSource).getSensor());

            case IF_STMT:
                IfStmt<V> ifStmt = (IfStmt<V>) astSource;
                if ( ifStmt.getProperty().getBlockType().equals("logic_ternary") ) {
                    addValue(jaxbDestination, "IF", ifStmt.getExpr().get(0));
                    addValue(jaxbDestination, "THEN", ifStmt.getThenList().get(0).get().get(0));
                    addValue(jaxbDestination, "ELSE", ifStmt.getElseList().get().get(0));
                    return jaxbDestination;
                }
                int _else = ifStmt.get_else();
                int _elseIf = ifStmt.get_elseIf();

                StmtList<V> elseList = ifStmt.getElseList();
                int expr = 0;
                expr = ifStmt.getExpr().size();

                if ( _else != 0 || _elseIf != 0 ) {
                    mutation = new Mutation();
                    if ( _else != 0 ) {
                        mutation.setElse(BigInteger.ONE);
                    }
                    if ( _elseIf > 0 ) {
                        mutation.setElseif(BigInteger.valueOf(_elseIf));
                    }
                    jaxbDestination.setMutation(mutation);
                    Repetitions repetitions = new Repetitions();
                    for ( int i = 0; i < expr; i++ ) {
                        addValue(repetitions, "IF" + i, ifStmt.getExpr().get(i));
                        addStatement(repetitions, "DO" + i, ifStmt.getThenList().get(i));
                    }
                    if ( elseList.get().size() != 0 ) {
                        addStatement(repetitions, "ELSE", ifStmt.getElseList());
                    }
                    jaxbDestination.setRepetitions(repetitions);
                    return jaxbDestination;
                }

                addValue(jaxbDestination, "IF0", ifStmt.getExpr().get(0));
                addStatement(jaxbDestination, "DO0", ifStmt.getThenList().get(0));
                if ( elseList.get().size() != 0 ) {
                    addStatement(jaxbDestination, "ELSE", ifStmt.getElseList());
                }

                return jaxbDestination;

            case REPEAT_STMT:
                RepeatStmt<V> repeatStmt = (RepeatStmt<V>) astSource;
                switch ( repeatStmt.getMode() ) {
                    case TIMES:
                        addValue(jaxbDestination, "TIMES", ((Binary<V>) ((ExprList<V>) repeatStmt.getExpr()).get().get(1)).getRight());
                        break;

                    case WAIT:
                    case UNTIL:
                        addField(jaxbDestination, "MODE", repeatStmt.getMode().name());
                        addValue(jaxbDestination, "BOOL", ((Unary<V>) repeatStmt.getExpr()).getExpr());
                        break;

                    case WHILE:
                        addField(jaxbDestination, "MODE", repeatStmt.getMode().name());
                        addValue(jaxbDestination, "BOOL", (repeatStmt.getExpr()));
                        break;

                    case FOR:
                        ExprList<V> exprList = (ExprList<V>) repeatStmt.getExpr();
                        addField(jaxbDestination, "VAR", ((Var<V>) ((Binary<V>) exprList.get().get(0)).getLeft()).getValue());
                        addValue(jaxbDestination, "FROM", ((Binary<V>) exprList.get().get(0)).getRight());
                        addValue(jaxbDestination, "TO", ((Binary<V>) exprList.get().get(1)).getRight());
                        addValue(jaxbDestination, "BY", ((Binary<V>) exprList.get().get(2)).getRight());
                        break;

                    case FOR_EACH:
                        Binary<V> exprBinary = (Binary<V>) repeatStmt.getExpr();
                        addField(jaxbDestination, "VAR", ((Var<V>) exprBinary.getLeft()).getValue());
                        addValue(jaxbDestination, "LIST", exprBinary.getRight());
                        break;
                    default:
                        break;
                }
                addStatement(jaxbDestination, "DO", repeatStmt.getList());

                return jaxbDestination;

            case STMT_FLOW_CONTROL:
                addField(jaxbDestination, "FLOW", ((StmtFlowCon<V>) astSource).getFlow().name());
                return jaxbDestination;

            case ASSIGN_STMT:
                AssignStmt<V> assignStmt = (AssignStmt<V>) astSource;
                addField(jaxbDestination, "VAR", assignStmt.getName().getValue());
                addValue(jaxbDestination, "VALUE", assignStmt.getExpr());

                return jaxbDestination;

            case WAIT_STMT:
                WaitStmt<V> waitStmt = (WaitStmt<V>) astSource;
                StmtList<V> waitStmtList = waitStmt.getStatements();
                int numOfWait = waitStmtList.get().size();
                if ( numOfWait == 1 ) {
                    addValue(jaxbDestination, "WAIT0", ((RepeatStmt<V>) waitStmtList.get().get(0)).getExpr());
                    addStatement(jaxbDestination, "DO0", ((RepeatStmt<V>) waitStmtList.get().get(0)).getList());
                    return jaxbDestination;
                }
                mutation = new Mutation();
                mutation.setWait(BigInteger.valueOf(numOfWait - 1));
                jaxbDestination.setMutation(mutation);
                Repetitions repetitions = new Repetitions();
                for ( int i = 0; i < numOfWait; i++ ) {
                    addValue(repetitions, "WAIT" + i, ((RepeatStmt<V>) waitStmtList.get().get(i)).getExpr());
                    addStatement(repetitions, "DO" + i, ((RepeatStmt<V>) waitStmtList.get().get(i)).getList());
                }
                jaxbDestination.setRepetitions(repetitions);
                return jaxbDestination;

            case MAIN_TASK:
                return jaxbDestination;

            case ACTIVITY_TASK:
                addValue(jaxbDestination, "ACTIVITY", ((ActivityTask<V>) astSource).getActivityName());

                return jaxbDestination;

            case START_ACTIVITY_TASK:
                addValue(jaxbDestination, "ACTIVITY", ((StartActivityTask<V>) astSource).getActivityName());
                return jaxbDestination;

            case TEXT_INDEX_OF_FUNCT:
                IndexOfFunct<V> textIndexOfFunct = (IndexOfFunct<V>) astSource;
                addField(jaxbDestination, "END", textIndexOfFunct.getFunctName().name());
                addValue(jaxbDestination, "VALUE", textIndexOfFunct.getParam().get(0));
                addValue(jaxbDestination, "FIND", textIndexOfFunct.getParam().get(1));
                return jaxbDestination;

            case TEXT_CHAR_AT_FUNCT:
                TextCharAtFunct<V> textCharAtFunct = (TextCharAtFunct<V>) astSource;
                mutation = new Mutation();
                mutation.setAt(false);
                addField(jaxbDestination, "WHERE", textCharAtFunct.getFunctName().name());
                addValue(jaxbDestination, "VALUE", textCharAtFunct.getParam().get(0));
                if ( textCharAtFunct.getFunctName() == Functions.FROM_START || textCharAtFunct.getFunctName() == Functions.FROM_END ) {
                    mutation.setAt(true);
                    addValue(jaxbDestination, "AT", textCharAtFunct.getParam().get(1));
                }
                jaxbDestination.setMutation(mutation);
                return jaxbDestination;

            case GET_SUB_FUNCT:
                GetSubFunct<V> getSubFunct = (GetSubFunct<V>) astSource;
                mutation = new Mutation();
                mutation.setAt1(false);
                mutation.setAt2(false);
                addField(jaxbDestination, "WHERE1", getSubFunct.getStrParam().get(0));
                addField(jaxbDestination, "WHERE2", getSubFunct.getStrParam().get(1));
                if ( getSubFunct.getFunctName() == Functions.GET_SUBLIST ) {
                    addValue(jaxbDestination, "LIST", getSubFunct.getParam().get(0));
                } else {
                    addValue(jaxbDestination, "STRING", getSubFunct.getParam().get(0));
                }
                if ( Functions.get(getSubFunct.getStrParam().get(0)) == Functions.FROM_START
                    || Functions.get(getSubFunct.getStrParam().get(0)) == Functions.FROM_END ) {
                    mutation.setAt1(true);
                    addValue(jaxbDestination, "AT1", getSubFunct.getParam().get(1));
                }
                if ( Functions.get(getSubFunct.getStrParam().get(1)) == Functions.FROM_START
                    || Functions.get(getSubFunct.getStrParam().get(1)) == Functions.FROM_END ) {
                    mutation.setAt2(true);
                    addValue(jaxbDestination, "AT2", getSubFunct.getParam().get(getSubFunct.getParam().size() - 1));
                }
                jaxbDestination.setMutation(mutation);
                return jaxbDestination;

            case MATH_NUM_PROP_FUNCT:
                MathNumPropFunct<V> mathNumPropFunct = (MathNumPropFunct<V>) astSource;
                mutation = new Mutation();
                mutation.setDivisorInput(false);
                addField(jaxbDestination, "PROPERTY", mathNumPropFunct.getFunctName().name());
                addValue(jaxbDestination, "NUMBER_TO_CHECK", mathNumPropFunct.getParam().get(0));
                if ( mathNumPropFunct.getFunctName() == Functions.DIVISIBLE_BY ) {
                    addValue(jaxbDestination, "DIVISOR", mathNumPropFunct.getParam().get(1));
                    mutation.setDivisorInput(true);
                }
                jaxbDestination.setMutation(mutation);
                return jaxbDestination;

            case MATH_SINGLE_FUNCT:
                MathSingleFunct<V> mathSingleFunct = (MathSingleFunct<V>) astSource;
                addField(jaxbDestination, "OP", mathSingleFunct.getFunctName().name());
                addValue(jaxbDestination, "NUM", mathSingleFunct.getParam().get(0));
                return jaxbDestination;

            case MATH_ON_LIST_FUNCT:
                MathOnListFunct<V> mathOnListFunct = (MathOnListFunct<V>) astSource;
                addField(jaxbDestination, "OP", mathOnListFunct.getFunctName().name());
                addValue(jaxbDestination, "LIST", mathOnListFunct.getParam().get(0));
                return jaxbDestination;

            case MATH_CONSTRAIN_FUNCT:
                MathConstrainFunct<V> mathConstrainFunct = (MathConstrainFunct<V>) astSource;
                addValue(jaxbDestination, "VALUE", mathConstrainFunct.getParam().get(0));
                addValue(jaxbDestination, "LOW", mathConstrainFunct.getParam().get(1));
                addValue(jaxbDestination, "HIGH", mathConstrainFunct.getParam().get(2));
                return jaxbDestination;

            case MATH_RANDOM_INT_FUNCT:
                MathRandomIntFunct<V> mathRandomIntFunct = (MathRandomIntFunct<V>) astSource;
                addValue(jaxbDestination, "FROM", mathRandomIntFunct.getParam().get(0));
                addValue(jaxbDestination, "TO", mathRandomIntFunct.getParam().get(1));
                return jaxbDestination;

            case MATH_RANDOM_FLOAT_FUNCT:
                return jaxbDestination;

            case TEXT_JOIN_FUNCT:
                TextJoinFunct<V> textJoinFunct = (TextJoinFunct<V>) astSource;
                ExprList<V> strExprList = (ExprList<V>) textJoinFunct.getParam().get(0);
                int numOfStrings = (strExprList.get().size());
                mutation = new Mutation();
                mutation.setItems(BigInteger.valueOf(numOfStrings));
                jaxbDestination.setMutation(mutation);
                for ( int i = 0; i < numOfStrings; i++ ) {
                    addValue(jaxbDestination, "ADD" + i, strExprList.get().get(i));
                }
                return jaxbDestination;

            case LENGHT_OF_IS_EMPTY_FUNCT:
                LenghtOfIsEmptyFunct<V> lenghtOfFunct = (LenghtOfIsEmptyFunct<V>) astSource;
                addValue(jaxbDestination, "VALUE", lenghtOfFunct.getParam().get(0));
                return jaxbDestination;

            case TEXT_CHANGE_CASE_FUNCT:
                TextChangeCaseFunct<V> textChangeCaseFunct = (TextChangeCaseFunct<V>) astSource;
                addField(jaxbDestination, "CASE", textChangeCaseFunct.getFunctName().name());
                addValue(jaxbDestination, "TEXT", textChangeCaseFunct.getParam().get(0));
                return jaxbDestination;

            case TEXT_TRIM_FUNCT:
                TextTrimFunct<V> textTrimFunct = (TextTrimFunct<V>) astSource;
                addField(jaxbDestination, "MODE", textTrimFunct.getFunctName().name());
                addValue(jaxbDestination, "TEXT", textTrimFunct.getParam().get(0));
                return jaxbDestination;

            case TEXT_PRINT_FUNCT:
                TextPrintFunct<V> textPrintFunct = (TextPrintFunct<V>) astSource;
                addValue(jaxbDestination, "TEXT", textPrintFunct.getParam().get(0));
                return jaxbDestination;

            case TEXT_PROMPT_FUNCT:
                TextPromptFunct<V> textPromptFunct = (TextPromptFunct<V>) astSource;
                addField(jaxbDestination, "TYPE", textPromptFunct.getFunctName().name());
                addField(jaxbDestination, "TEXT", textPromptFunct.getText());
                return jaxbDestination;

            case LIST_CREATE:
                ListCreate<V> listCreate = (ListCreate<V>) astSource;
                ExprList<V> exprList = listCreate.getValue();
                int numOfItems = (exprList.get().size());
                mutation = new Mutation();
                mutation.setItems(BigInteger.valueOf(numOfItems));
                jaxbDestination.setMutation(mutation);
                for ( int i = 0; i < numOfItems; i++ ) {
                    addValue(jaxbDestination, "ADD" + i, exprList.get().get(i));
                }
                return jaxbDestination;

            case FUNCTIONS:
                Func<V> funct = (Func<V>) astSource;
                switch ( funct.getFunctName() ) {
                    case POWER:
                        addField(jaxbDestination, "OP", funct.getFunctName().name());
                        addValue(jaxbDestination, "A", funct.getParam().get(0));
                        addValue(jaxbDestination, "B", funct.getParam().get(1));
                        return jaxbDestination;

                    default:
                        return null;
                }

            case LIST_REPEAT_FUNCT:
                ListRepeat<V> listRepeat = (ListRepeat<V>) astSource;
                addValue(jaxbDestination, "ITEM", listRepeat.getParam().get(0));
                addValue(jaxbDestination, "NUM", listRepeat.getParam().get(1));
                return jaxbDestination;

            case LIST_INDEX_OF:
                ListGetIndex<V> listGetIndex = (ListGetIndex<V>) astSource;
                mutation = new Mutation();
                mutation.setAt(false);
                mutation.setStatement(listGetIndex.getMode().isStatment());
                addField(jaxbDestination, "MODE", listGetIndex.getMode().name());
                addField(jaxbDestination, "WHERE", listGetIndex.getFunctName().name());
                addValue(jaxbDestination, "VALUE", listGetIndex.getParam().get(0));
                if ( listGetIndex.getParam().size() > 1 ) {
                    addValue(jaxbDestination, "AT", listGetIndex.getParam().get(1));
                    mutation.setAt(true);
                }
                jaxbDestination.setMutation(mutation);
                return jaxbDestination;

            case LIST_SET_INDEX:
                ListSetIndex<V> listSetIndex = (ListSetIndex<V>) astSource;
                mutation = new Mutation();
                mutation.setAt(false);
                addField(jaxbDestination, "MODE", listSetIndex.getMode().name());
                addField(jaxbDestination, "WHERE", listSetIndex.getFunctName().name());
                addValue(jaxbDestination, "LIST", listSetIndex.getParam().get(0));
                if ( listSetIndex.getParam().size() > 2 ) {
                    addValue(jaxbDestination, "AT", listSetIndex.getParam().get(1));
                    addValue(jaxbDestination, "TO", listSetIndex.getParam().get(2));
                    mutation.setAt(true);
                } else {
                    addValue(jaxbDestination, "TO", listSetIndex.getParam().get(1));
                }
                jaxbDestination.setMutation(mutation);
                return jaxbDestination;

            case EMPTY_EXPR:
            case LOCATION:
                return null;
            default:
                throw new DbcException("Invalid AST object!");
        }

    }

    private void setBasicProperties(Phrase<V> astSource, Block jaxbDestination) {
        if ( astSource.getProperty() == null ) {
            return;
        }
        String blockType;
        blockType = astSource.getProperty().getBlockType();
        setProperties(astSource, jaxbDestination, blockType);
        addComment(astSource, jaxbDestination);
    }

    private List<Block> extractStmtList(Phrase<V> phrase) {
        List<Block> result = new ArrayList<Block>();
        Assert.isTrue(phrase.getKind() == Kind.STMT_LIST, "Phrase is not StmtList!");
        StmtList<V> stmtList = (StmtList<V>) phrase;
        for ( Stmt<V> stmt : stmtList.get() ) {
            result.add(astToBlock(stmt));
        }
        return result;
    }

    private void addStatement(Block block, String name, Phrase<V> value) {
        Assert.isTrue(value.getKind() == Phrase.Kind.STMT_LIST, "Phrase is not STMT_LIST");
        if ( ((StmtList<V>) value).get().size() != 0 ) {
            Statement statement = new Statement();
            statement.setName(name);
            statement.getBlock().addAll(extractStmtList(value));
            block.getStatement().add(statement);
        }
    }

    private void addStatement(Repetitions repetitions, String name, Phrase<V> value) {
        Assert.isTrue(value.getKind() == Phrase.Kind.STMT_LIST, "Phrase is not STMT_LIST");
        if ( ((StmtList<V>) value).get().size() != 0 ) {
            Statement statement = new Statement();
            statement.setName(name);
            statement.getBlock().addAll(extractStmtList(value));
            repetitions.getValueAndStatement().add(statement);
        }
    }

    private void addValue(Block block, String name, Phrase<V> value) {
        if ( value.getKind() != Kind.EMPTY_EXPR ) {
            Value blockValue = new Value();
            blockValue.setName(name);
            blockValue.setBlock(astToBlock(value));
            block.getValue().add(blockValue);
        }
    }

    private void addValue(Repetitions repetitions, String name, Phrase<V> value) {
        if ( value.getKind() != Kind.EMPTY_EXPR ) {
            Value blockValue = new Value();
            blockValue.setName(name);
            blockValue.setBlock(astToBlock(value));
            repetitions.getValueAndStatement().add(blockValue);
        }
    }

    private void addField(Block block, String name, String value) {
        Field field = new Field();
        field.setName(name);
        field.setValue(value);
        block.getField().add(field);
    }

    private void setProperties(Phrase<V> astObject, Block block, String type) {
        block.setType(type);
        block.setId(astObject.getProperty().getBlocklyId());
        setDisabled(astObject, block);
        setCollapsed(astObject, block);
        setInline(astObject, block);
        setDeletable(astObject, block);
    }

    private void setInline(Phrase<V> astObject, Block block) {
        if ( astObject.getProperty().isInline() != null ) {
            block.setInline(astObject.getProperty().isInline());
        }
    }

    private void setCollapsed(Phrase<V> astObject, Block block) {
        if ( astObject.getProperty().isCollapsed() ) {
            block.setCollapsed(astObject.getProperty().isCollapsed());
        }
    }

    private void setDisabled(Phrase<V> astObject, Block block) {
        if ( astObject.getProperty().isDisabled() ) {
            block.setDisabled(astObject.getProperty().isDisabled());
        }
    }

    private void setDeletable(Phrase<V> astObject, Block block) {
        if ( astObject.getProperty().isDeletable() != null ) {
            block.setDeletable(astObject.getProperty().isDeletable());
        }
    }

    private void addComment(Phrase<V> astObject, Block block) {
        if ( astObject.getComment() != null ) {
            Comment comment = new Comment();
            comment.setValue(astObject.getComment().getComment());
            comment.setPinned(astObject.getComment().isPinned());
            comment.setH(astObject.getComment().getHeight());
            comment.setW(astObject.getComment().getWidth());
            block.setComment(comment);
        }
    }
}