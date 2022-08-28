package site.liangbai.realhomehunt.common.expand

import org.bukkit.Location
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.util.Pair

fun Residence.expand(expand: Expand, sizeParam: Double): Pair<Location, Location> {
    var size = sizeParam
    val left = left.clone()
    val right = right.clone()
    when (expand) {
        Expand.UP -> if (left.y > right.y) {
            left.add(0.0, size, 0.0)
        } else {
            right.add(0.0, size, 0.0)
        }
        Expand.DOWN -> if (left.y < right.y) {
            left.add(0.0, -size, 0.0)
        } else {
            right.add(0.0, -size, 0.0)
        }
        Expand.NORTH -> if (left.z < right.z) {
            left.add(0.0, 0.0, -size)
        } else {
            right.add(0.0, 0.0, -size)
        }
        Expand.SOUTH -> if (left.z > right.z) {
            left.add(0.0, 0.0, size)
        } else {
            right.add(0.0, 0.0, size)
        }
        Expand.WEST -> if (left.x < right.x) {
            left.add(-size, 0.0, 0.0)
        } else {
            right.add(-size, 0.0, 0.0)
        }
        Expand.EAST -> if (left.x > right.x) {
            left.add(size, 0.0, 0.0)
        } else {
            right.add(size, 0.0, 0.0)
        }
        Expand.ALL -> {
            size /= 2
            if (left.x > right.x) {
                left.add(size, 0.0, 0.0)
                right.add(-size, 0.0, 0.0)
            } else {
                left.add(-size, 0.0, 0.0)
                right.add(size, 0.0, 0.0)
            }
            if (left.y > right.y) {
                left.add(0.0, size, 0.0)
                right.add(0.0, -size, 0.0)
            } else {
                left.add(0.0, -size, 0.0)
                right.add(0.0, size, 0.0)
            }
            if (left.z > right.z) {
                left.add(0.0, 0.0, size)
                right.add(0.0, 0.0, -size)
            } else {
                left.add(0.0, 0.0, -size)
                right.add(0.0, 0.0, size)
            }
        }
    }
    return Pair(left, right)
}