package de.fhg.iais.roberta.ast.syntax.sensor;

import java.util.Locale;

import de.fhg.iais.roberta.dbc.DbcException;

/**
 * All sensor ports that a brick can have.
 */
public enum SensorPort {
    S1( "1" ), S2( "2" ), S3( "3" ), S4( "4" );

    private final String[] values;

    private SensorPort(String... values) {
        this.values = values;
    }

    /**
     * @return number of the port
     */
    public String getPortNumber() {
        return values[0];
    }

    /**
     * @return valid Java code name of the enumeration
     */
    public String getJavaCode() {
        return this.getClass().getSimpleName() + "." + this;
    }

    /**
     * get sensor port from {@link SensorPort} from string parameter. It is possible for one sensor port to have multiple string mappings.
     * Throws exception if the operator does not exists.
     *
     * @param name of the sensor port
     * @return sensor port from the enum {@link SensorPort}
     */
    public static SensorPort get(String s) {
        if ( s == null || s.isEmpty() ) {
            throw new DbcException("Sensor port missing");
        }
        String sUpper = s.trim().toUpperCase(Locale.GERMAN);
        for ( SensorPort sp : SensorPort.values() ) {
            if ( sp.toString().equals(sUpper) ) {
                return sp;
            }
            for ( String value : sp.values ) {
                if ( sUpper.equals(value) ) {
                    return sp;
                }
            }
        }
        throw new DbcException("Invalid sensor port: " + s);
    }
}
