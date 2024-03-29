package de.fhg.iais.roberta.ast.syntax.action;

import de.fhg.iais.roberta.ast.syntax.BlocklyBlockProperties;
import de.fhg.iais.roberta.ast.syntax.BlocklyComment;
import de.fhg.iais.roberta.ast.syntax.Phrase;
import de.fhg.iais.roberta.ast.visitor.AstVisitor;
import de.fhg.iais.roberta.dbc.Assert;

/**
 * This class represents the <b>robActions_motor_stop</b> block from Blockly into the AST (abstract syntax tree).
 * Object from this class will generate code for turning off the motor.<br/>
 * <br/>
 * The client must provide the {@link ActorPort} and {@link MotorStopMode} (is the motor breaking or not).
 */
public class MotorStopAction<V> extends Action<V> {
    private final ActorPort port;
    private final MotorStopMode mode;

    private MotorStopAction(ActorPort port, MotorStopMode mode, BlocklyBlockProperties properties, BlocklyComment comment) {
        super(Phrase.Kind.MOTOR_STOP_ACTION, properties, comment);
        Assert.isTrue(port != null && mode != null);
        this.port = port;
        this.mode = mode;
        setReadOnly();
    }

    /**
     * Creates instance of {@link MotorStopAction}. This instance is read only and can not be modified.
     * 
     * @param port {@link ActorPort} on which the motor is connected,
     * @param mode of stopping {@link MotorStopMode},
     * @param properties of the block (see {@link BlocklyBlockProperties}),
     * @param comment added from the user,
     * @return read only object of class {@link MotorStopAction}.
     */
    public static <V> MotorStopAction<V> make(ActorPort port, MotorStopMode mode, BlocklyBlockProperties properties, BlocklyComment comment) {
        return new MotorStopAction<V>(port, mode, properties, comment);
    }

    /**
     * @return port on which the motor is connected.
     */
    public ActorPort getPort() {
        return this.port;
    }

    /**
     * @return stopping mode in which the motor is set.
     */
    public MotorStopMode getMode() {
        return this.mode;
    }

    @Override
    public String toString() {
        return "MotorStop [port=" + this.port + ", mode=" + this.mode + "]";
    }

    @Override
    protected V accept(AstVisitor<V> visitor) {
        return visitor.visitMotorStopAction(this);
    }

}
