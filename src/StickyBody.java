// This class inherits from body but overrides the collide method so that two bodies appear to merge into one.
// They don't actually merge ie my first thought was to remove both from the particles list and add a new one with new mass etc.
// But it's actually easier to just change their momentums, mass etc. and stop drawing the smaller body. 
import java.awt.Color;
import java.awt.Graphics;


final class StickyBody extends Body{
    
    public StickyBody(double x, double y, double vx, double vy, double mass, Color color) {
        super(x, y, vx, vy, mass, color);
    }

    public int scaledRadius(){
        double _radius = Math.pow(mass, 1.0/3.0);
        if(this.overRiddenRadius == null){
            return (int) Math.min(Math.max(1.0, _radius/1000.0),25.0);
        }else{
            return this.overRiddenRadius.intValue();
        }
    }    
    
    public void collide(Body other){
        switch (other) {   
            case StickyBody sb -> privateCollide(this, sb); 
            default -> super.privateCollide(this, other);
        }
    
    }

    public static void privateCollide(Body b1, Body b2){
        // large body absorbs the small body.
        Body largeBody = b1.mass > b2.mass ? b1 : b2;
        Body smallBody = b1.mass > b2.mass ? b2 : b1;

        largeBody.vx = (largeBody.vx * largeBody.mass + smallBody.vx * smallBody.mass) / (largeBody.mass + smallBody.mass);
        largeBody.vy = (largeBody.vy * largeBody.mass + smallBody.vy * smallBody.mass) / (largeBody.mass + smallBody.mass);
        
        smallBody.vx = 0;
        smallBody.vy = 0;
        
        largeBody.mass += smallBody.mass-EPSILON;
        smallBody.mass = EPSILON;

        smallBody.collided = true; //smaller one get's branded


    }

    public void draw(Graphics g){
        if (this.collided){ //don't draw if collided
            return;
        }
        int posX = (int) Math.round(this.rx);
        int posY = (int) Math.round(this.ry);
        Color _color = (this.changeColorOnCollision && this.collided) ? this.SwitchColor : this.color;

        g.setColor(_color);
        g.fillOval(posX - this.scaledRadius(), posY - this.scaledRadius(), 2 * this.scaledRadius(), 2 * this.scaledRadius());

    }    
   
}
