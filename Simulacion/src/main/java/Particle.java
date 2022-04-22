import java.util.Objects;

public class Particle {
    private double x, y, radius, mass, vx, vy;
    private boolean fixed;

    public Particle(double x, double y, double radius, double mass, double vx, double vy, boolean fixed) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.mass = mass;
        this.vx = vx;
        this.vy = vy;
        this.fixed = fixed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public boolean isFixed() {
        return fixed;
    }

    public boolean isTouching(Particle other) {
        return Math.pow(this.getX() - other.getX(), 2) + Math.pow(this.getY() - other.getY(), 2) <= Math.pow(this.getRadius() + other.getRadius(),2);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return Double.compare(particle.x, x) == 0 && Double.compare(particle.y, y) == 0 && Double.compare(particle.radius, radius) == 0 && Double.compare(particle.mass, mass) == 0 && Double.compare(particle.vx, vx) == 0 && Double.compare(particle.vy, vy) == 0 && fixed == particle.fixed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, radius, mass, vx, vy, fixed);
    }
}
