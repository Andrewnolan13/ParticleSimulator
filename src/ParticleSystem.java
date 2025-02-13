import java.util.ArrayList;

public abstract class ParticleSystem{
    public ArrayList<particle> particles;
    abstract void updateParticles();
    
    public ArrayList<particle> getParticles(){
        ArrayList<particle> res = new ArrayList<particle>();
        for (particle p : particles){
            res.add(p.copy());
        }
        return res;
    }
}