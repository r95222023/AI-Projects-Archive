import com.sun.org.apache.xpath.internal.operations.Bool;

public class Utils {

    public static String pit = "pit";
    public static String wumpus = "wumpus";
    public static String okNeighbor = "ok";
    public static String breeze = "breeze";
    public static String stench = "stench";
    public static Integer worldSize = 4;
    public static Integer wumpusN = 1;
    public static Integer pitN = 2;
    public static Integer wumpusCellFlag = 4;
    public static Integer pitCellFlag = 4;
    public int[][] directions = {{0, 1}, {-1, 0}, {1, 0}, {0, -1}};

    public static class Coordinates {
        private final int x;
        private final int y;

        public Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Coordinates other = (Coordinates) obj;
            return this.x == other.x && this.y == other.y;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            return result;
        }
    }
}

