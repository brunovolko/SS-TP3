public class Particle {
    private double x, y, radius, mass, vx, vy;

    public Particle(double x, double y, double radius, double mass, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.mass = mass;
        this.vx = vx;
        this.vy = vy;
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

    public boolean isTouching(Particle other) {
        return Math.pow(this.getX() - other.getX(), 2) + Math.pow(this.getY() - other.getY(), 2) <= Math.pow(this.getRadius() + other.getRadius(),2);
    }
}
