package de.fhg.iais.roberta.ast.syntax.sensor;

import java.util.Locale;

import de.fhg.iais.roberta.dbc.DbcException;

/**
 * Modes in which the sensor can operate.
 */
public enum ColorSensorMode {
    COLOUR( "ColorID" ), RED( "Red" ), RGB( "RGB" ), AMBIENTLIGHT( "Ambient" ), GET_MODE(), GET_SAMPLE();

    private final String[] values;

    private ColorSensorMode(String... values) {
        this.values = values;
    }

    /**
     * @return valid Java code name of the enumeration
     */
    public String getJavaCode() {
        return this.getClass().getSimpleName() + "." + this;
    }

    /**
     * @return name that Lejos is using for this mode
     */
    public String getLejosModeName() {
        return this.values[0];
    }

    /**
     * get mode from {@link ColorSensorMode} from string parameter. It is possible for one mode to have multiple string mappings.
     * Throws exception if the mode does not exists.
     * 
     * @param name of the mode
     * @return mode from the enum {@link ColorSensorMode}
     */
    public static ColorSensorMode get(String s) {
        if ( s == null || s.isEmpty() ) {
            throw new DbcException("Invalid mode: " + s);
        }
        String sUpper = s.trim().toUpperCase(Locale.GERMAN);
        for ( ColorSensorMode mo : ColorSensorMode.values() ) {
            if ( mo.toString().equals(sUpper) ) {
                return mo;
            }
            for ( String value : mo.values ) {
                if ( sUpper.equals(value) ) {
                    return mo;
                }
            }
        }
        throw new DbcException("Invalid mode: " + s);
    }
}