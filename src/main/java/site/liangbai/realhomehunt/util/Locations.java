package site.liangbai.realhomehunt.util;

import org.bukkit.Location;
import org.bukkit.World;
import site.liangbai.realhomehunt.residence.Residence;

import java.util.Arrays;

public final class Locations {
    public static boolean isInResidence(Location location, Residence residence) {
        return isInZone(location, residence.getLeft(), residence.getRight());
    }

    public static boolean isInZone(Location location, Location loc1, Location loc2) {
        if (location.getWorld() != null && !location.getWorld().equals(loc1.getWorld())) return false;

        double[] dim = new double[2];

        dim[0] = loc1.getX();
        dim[1] = loc2.getX();

        Arrays.sort(dim);

        if (location.getX() > dim[1] || location.getX() < dim[0])
            return false;

        dim[0] = loc1.getY();
        dim[1] = loc2.getY();

        Arrays.sort(dim);

        if (location.getY() > dim[1] || location.getY() < dim[0])
            return false;

        dim[0] = loc1.getZ();
        dim[1] = loc2.getZ();

        Arrays.sort(dim);

        return !(location.getZ() > dim[1]) && !(location.getZ() < dim[0]);
    }

    public static Location toBlockLocation(Location location) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch());
    }

    public static Location getAverageLocation(World world, Location left, Location right) {
        int defaultX = (left.getBlockX() + right.getBlockX()) / 2;

        int defaultY = (left.getBlockY() + right.getBlockY()) / 2;

        int defaultZ = (left.getBlockZ() + right.getBlockZ()) / 2;

        return new Location(world, defaultX, defaultY, defaultZ);
    }

    public static DistanceInfo countDistanceInfo(Location left, Location right) {
        DistanceInfo.Builder builder = new DistanceInfo.Builder();

        double[] dim = new double[2];

        dim[0] = left.getX();
        dim[1] = right.getX();

        Arrays.sort(dim);

        builder.x(dim[1] - dim[0]);

        dim[0] = left.getY();
        dim[1] = right.getY();

        Arrays.sort(dim);

        builder.y(dim[1] - dim[0]);

        dim[0] = left.getZ();
        dim[1] = right.getZ();

        Arrays.sort(dim);

        builder.z(dim[1] - dim[0]);

        return builder.build();
    }

    public static LocationSortInfo sort(Location left, Location right) {
        double[] dim = new double[2];

        dim[0] = left.getX();
        dim[1] = right.getX();

        Arrays.sort(dim);

        double minX = dim[0];

        double maxX = dim[1];

        dim[0] = left.getY();
        dim[1] = right.getY();

        Arrays.sort(dim);

        double minY = dim[0];

        double maxY = dim[1];

        dim[0] = left.getZ();
        dim[1] = right.getZ();

        Arrays.sort(dim);

        double minZ = dim[0];

        double maxZ = dim[1];

        Location min = new Location(left.getWorld(), minX, minY, minZ);

        Location max = new Location(right.getWorld(), maxX, maxY, maxZ);

        return new LocationSortInfo(min, max);
    }

    public static final class LocationSortInfo {
        private final Location min;

        private final Location max;

        public LocationSortInfo(Location min, Location max) {
            this.min = min;
            this.max = max;
        }

        public Location getMax() {
            return max;
        }

        public Location getMin() {
            return min;
        }
    }

    public static final class DistanceInfo {
        private final double x;

        private final double y;

        private final double z;

        private DistanceInfo(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public static final class Builder {
            private double x;

            private double y;

            private double z;

            public void x(double x) {
                this.x = x;
            }

            public void y(double y) {
                this.y = y;
            }

            public void z(double z) {
                this.z = z;
            }

            public DistanceInfo build() {
                return new DistanceInfo(x, y, z);
            }
        }
    }
}
