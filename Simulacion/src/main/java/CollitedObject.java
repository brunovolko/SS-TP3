public class CollitedObject {
    private final Object object;
    public CollitedObject(Object object) throws Exception {
        if(object.getClass() != Particle.class && object.getClass() != Wall.class)
            throw new Exception("A collited object must be a Particle or a wall");
        this.object = object;
    }
    public Class<?> getObjectType() {
        return this.object.getClass();
    }
    public Object getObject() {
        return this.object;
    }

}
