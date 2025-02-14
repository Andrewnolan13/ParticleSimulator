import java.awt.Color;
import java.awt.Graphics;

public class Quad{
    private double xmid, ymid,length;
    
    public Quad(double xmid, double ymid, double length){
        this.xmid = xmid;
        this.ymid = ymid;
        this.length = length;
    }
    public double length(){
        return this.length;
    }
    public boolean contains(double x, double y){
        double halfLength = this.length / 2.0;
        return (x <= this.xmid + halfLength) && (x > this.xmid - halfLength) && (y < this.ymid + halfLength) && (y >= this.ymid - halfLength);
    }
    public Quad NW(){
        double _xmid = this.xmid - this.length / 4.0;
        double _ymid = this.ymid - this.length / 4.0;
        double _length = this.length / 2.0;
        return new Quad(_xmid, _ymid, _length);
    }
    public Quad NE(){
        double _xmid = this.xmid + this.length / 4.0;
        double _ymid = this.ymid - this.length / 4.0;
        double _length = this.length / 2.0;
        return new Quad(_xmid, _ymid, _length);
    }
    public Quad SW(){
        double _xmid = this.xmid - this.length / 4.0;
        double _ymid = this.ymid + this.length / 4.0;
        double _length = this.length / 2.0;
        return new Quad(_xmid, _ymid, _length);
    }
    public Quad SE(){
        double _xmid = this.xmid + this.length / 4.0;
        double _ymid = this.ymid + this.length / 4.0;
        double _length = this.length / 2.0;
        return new Quad(_xmid, _ymid, _length);
    }
    public void draw(Graphics g,Color color){
        g.setColor(color);
        g.drawRect((int)(this.xmid - this.length / 2.0), (int)(this.ymid - this.length / 2.0), (int)this.length, (int)this.length);
        
    }
}