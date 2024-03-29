package de.fhg.iais.roberta.ast.syntax.action;

import de.fhg.iais.roberta.ast.syntax.BlocklyBlockProperties;
import de.fhg.iais.roberta.ast.syntax.BlocklyComment;
import de.fhg.iais.roberta.ast.syntax.Phrase;
import de.fhg.iais.roberta.ast.syntax.expr.Expr;
import de.fhg.iais.roberta.ast.visitor.AstVisitor;
import de.fhg.iais.roberta.dbc.Assert;

/**
 * This class represents the <b>robActions_motor_setPower</b> block from Blockly into the AST (abstract syntax tree).
 * Object from this class will generate code for setting the power of the motor on given port.<br/>
 * <br/>
 * The client must provide the {@link ActorPort} on which the motor is connected.
 */
public class MotorSetPowerAction<V> extends Action<V> {
    private final ActorPort port;
    private final Expr<V> power;

    private MotorSetPowerAction(ActorPort port, Expr<V> power, BlocklyBlockProperties properties, BlocklyComment comment) {
        super(Phrase.Kind.MOTOR_SET_POWER_ACTION, properties, comment);
        Assert.isTrue(port != null && power.isReadOnly());
        this.port = port;
        this.power = power;
        setReadOnly();
    }

    /**
     * Creates instance of {@link MotorSetPowerAction}. This instance is read only and can not be modified.
     * 
     * @param port on which the motor is connected that we want to set,
     * @param properties of the block (see {@link BlocklyBlockProperties}),
     * @param comment added from the user,
     * @return read only object of class {@link MotorSetPowerAction}.
     */
    public static <V> MotorSetPowerAction<V> make(ActorPort port, Expr<V> power, BlocklyBlockProperties properties, BlocklyComment comment) {
        return new MotorSetPowerAction<V>(port, power, properties, comment);
    }

    /**
     * @return the port number on which the motor is connected.
     */
    public ActorPort getPort() {
        return this.port;
    }

    /**
     * @return value of the power of the motor.
     */
    public Expr<V> getPower() {
        return this.power;
    }

    @Override
    public String toString() {
        return "MotorSetPowerAction [port=" + this.port + ", power=" + this.power + "]";
    }

    @Override
    protected V accept(AstVisitor<V> visitor) {
        return visitor.visitMotorSetPowerAction(this);
    }
}
