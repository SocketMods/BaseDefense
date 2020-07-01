package sciwhiz12.basedefense.util;

import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

/**
 * Utility methods for block shapes.
 *
 * @author SciWhiz12
 */
public final class ShapeUtil {
    // Prevent instantiation
    private ShapeUtil() {}

    /**
     * Rotates the given {@link VoxelShape} along the horizontal plane according to
     * the given rotation direction.
     *
     * Assumes the given shape is within the bounds of 1 unit on each axis.
     * 
     * @param shape       The shape to rotate
     * @param rotationDir The rotation direction
     * @return The rotated shape
     */
    public static VoxelShape rotateCuboidShape(final VoxelShape shape, final Rotation rotationDir) {
        double x1 = shape.getStart(Direction.Axis.X), x2 = shape.getEnd(Direction.Axis.X);
        final double y1 = shape.getStart(Direction.Axis.Y), y2 = shape.getEnd(Direction.Axis.Y);
        double z1 = shape.getStart(Direction.Axis.Z), z2 = shape.getEnd(Direction.Axis.Z);

        if (rotationDir == Rotation.CLOCKWISE_90 || rotationDir == Rotation.COUNTERCLOCKWISE_90) {
            double temp = z1; // ]
            z1 = x1;   // ] x1 <-> z1
            x1 = temp; // ]

            temp = z2; // ]
            z2 = x2;   // ] x2 <-> z2
            x2 = temp; // ]
        }

        if (rotationDir == Rotation.CLOCKWISE_90 || rotationDir == Rotation.CLOCKWISE_180) {
            x1 = 1 - x1; // clockwise
            x2 = 1 - x2;
        }
        if (rotationDir == Rotation.COUNTERCLOCKWISE_90 || rotationDir == Rotation.CLOCKWISE_180) {
            z1 = 1 - z1; // counterclockwise
            z2 = 1 - z2;
        }

        return VoxelShapes.create(x1, y1, z1, x2, y2, z2);
    }
}
