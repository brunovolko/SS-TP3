import java.util.Objects;

public class Wall {
    private WALL_DIRECTION direction;

    public Wall(WALL_DIRECTION direction) {
        this.direction = direction;
    }

    public WALL_DIRECTION getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wall wall = (Wall) o;
        return direction == wall.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction);
    }
}
