package de.fhg.iais.roberta.ast.syntax.action;

import de.fhg.iais.roberta.ast.syntax.BlocklyBlockProperties;
import de.fhg.iais.roberta.ast.syntax.BlocklyComment;
import de.fhg.iais.roberta.ast.syntax.Phrase;
import de.fhg.iais.roberta.ast.visitor.AstVisitor;
import de.fhg.iais.roberta.dbc.Assert;

/**
 * This class represents the <b>robActions_brickLight_on</b> block from Blockly
 * into the AST (abstract syntax tree). Object from this class will generate
 * code for turning the light on.<br/>
 * <br/>
 * The client must provide the {@link BrickLedColor} of the lights and the mode
 * of blinking.
 */
public class LightAction<V> extends Action<V> {
    private final BrickLedColor color;
    private final BlinkMode blinkMode;

    private LightAction(BrickLedColor color, BlinkMode blinkMode, BlocklyBlockProperties properties, BlocklyComment comment) {
        super(Phrase.Kind.LIGHT_ACTION, properties, comment);
        Assert.isTrue(color != null);
        this.color = color;
        this.blinkMode = blinkMode;
        setReadOnly();
    }

    /**
     * Creates instance of {@link LightAction}. This instance is read only and
     * can not be modified.
     *
     * @param color of the lights on the brick. All possible colors are defined in {@link BrickLedColor},
     * @param blinkMode type of the blinking,
     * @param properties of the block (see {@link BlocklyBlockProperties}),
     * @param comment added from the user,
     * @return read only object of class {@link LightAction}.
     */
    public static <V> LightAction<V> make(BrickLedColor color, BlinkMode blinkMode, BlocklyBlockProperties properties, BlocklyComment comment) {
        return new LightAction<V>(color, blinkMode, properties, comment);
    }

    /**
     * @return {@link BrickLedColor} of the lights.
     */
    public BrickLedColor getColor() {
        return this.color;
    }

    /**
     * @return type of blinking.
     */
    public BlinkMode getBlinkMode() {
        return this.blinkMode;
    }

    @Override
    public String toString() {
        return "LightAction [" + this.color + ", " + this.blinkMode + "]";
    }

    @Override
    protected V accept(AstVisitor<V> visitor) {
        return visitor.visitLightAction(this);
    }
}
