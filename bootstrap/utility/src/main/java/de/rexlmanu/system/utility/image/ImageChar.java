/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.utility.image;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImageChar {
    BLOCK("BLOCK", 0, '\u2588'),
    DARK_SHADE("DARK_SHADE", 1, '\u2593'),
    MEDIUM_SHADE("MEDIUM_SHADE", 2, '\u2592'),
    LIGHT_SHADE("LIGHT_SHADE", 3, '\u2591');

    private String name;
    private int ordinal;
    private char unicode;
}
