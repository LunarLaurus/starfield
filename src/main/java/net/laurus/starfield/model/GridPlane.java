package net.laurus.starfield.model;

/**
 * Defines the plane (flat 2D surface in 3D space) on which the grid is
 * rendered.
 *
 * <p>
 * In this coordinate system:
 * </p>
 * <ul>
 * <li><b>X</b> axis → left / right</li>
 * <li><b>Y</b> axis → up / down</li>
 * <li><b>Z</b> axis → forward / backward (depth)</li>
 * </ul>
 *
 * <p>
 * A plane is created by choosing two axes to vary while the third axis remains
 * constant.
 * </p>
 */
public enum GridPlane {

    /**
     * Grid on the X–Z plane (Y is constant).
     *
     * <p>
     * This plane represents a horizontal surface, similar to a ground or floor.
     * Grid lines extend along the X and Z axes, while the Y coordinate is fixed.
     * </p>
     *
     * <p>
     * Typical use: ground reference, navigation grid, distance cues.
     * </p>
     */
    XZ,

    /**
     * Grid on the X–Y plane (Z is constant).
     *
     * <p>
     * This plane represents a vertical surface facing the camera. Grid lines extend
     * along the X and Y axes, while the Z coordinate is fixed.
     * </p>
     *
     * <p>
     * Typical use: front-facing reference plane, debugging projections, spatial
     * orientation.
     * </p>
     */
    XY,

    /**
     * Grid on the Y–Z plane (X is constant).
     *
     * <p>
     * This plane represents a vertical surface perpendicular to the X–Y plane. Grid
     * lines extend along the Y and Z axes, while the X coordinate is fixed.
     * </p>
     *
     * <p>
     * Typical use: side reference plane, depth and height visualization.
     * </p>
     */
    YZ
}