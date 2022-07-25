package fr.lesaffrefreres.rh.minibodet.helpers;

import javafx.scene.paint.Color;

/**
 * This class is used as a toolbox to help manipulation of colors.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 */
public class ColorHelper {

    /**
     * This method is used to convert a color to its web representation.
     * @param color The color to convert.
     * @return The hexadecimal string.
     */
    public static String toRGBCode( Color color )
    {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }
}
