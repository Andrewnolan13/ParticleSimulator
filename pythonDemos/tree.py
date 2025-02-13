from quad import Quad
from body import Body
import pygame

EPSILON = 10**-4

class Tree:
    __slots__ = ['_quad', '_body', '_NW', '_NE', '_SW', '_SE', 'Theta', 'numBodies']
    def __init__(self, var1:Quad, Theta)->None:
        self._quad = var1
        self._body = None
        self._NW = None
        self._NE = None
        self._SW = None
        self._SE = None
        self.Theta:float = Theta
        self.numBodies:int = 0
    
    def insert(self, b:Body)->None:
        if self._body == None:
            self._body = b
        #internal node
        if not self.isExternal():
            self._body = self._body.plus(b)
            self.putBody(b)

        elif self.numBodies == 1:
            self._NW = Tree(self._quad.NW(),Theta = self.Theta)
            self._NE = Tree(self._quad.NE(),Theta = self.Theta)
            self._SW = Tree(self._quad.SW(),Theta = self.Theta)
            self._SE = Tree(self._quad.SE(),Theta = self.Theta)            
            self.putBody(self._body)
            self.putBody(b)
            # IT htink this is the bug. In java, this might just reassign a new body, where as this is potentially mutating the original object
            # print(self._body,'hash',self._body.__hash__())
            self._body = self._body.copy.plus(b)
            # print(self._body,'hash',self._body.__hash__())
            # exit()
        
        else:
            self._body = self._body.plus(b)
        self.numBodies += 1

    def putBody(self, b:Body)->None:
        if b.inQuad(self._quad.NW()):
            self._NW.insert(b)
        elif b.inQuad(self._quad.NE()):
            self._NE.insert(b)
        elif b.inQuad(self._quad.SE()):
            self._SE.insert(b)
        elif b.inQuad(self._quad.SW()):
            self._SW.insert(b)
    
    def isExternal(self)->bool:
        return (self._NW == None) and (self._NE == None) and (self._SW == None) and (self._SE == None)
    
    def updateForce(self, b:Body)->None:
        if self._body == None or self._body == b:
            return              
        elif self.isExternal():
            b.addForce(self._body)  
        elif b.inQuad(self._quad):
            self._NW.updateForce(b)
            self._NE.updateForce(b)
            self._SW.updateForce(b)
            self._SE.updateForce(b)            
        else:
            s = abs(self._quad.length())
            d = self._body.distanceTo(b)+EPSILON
            if (s / d) < self.Theta:
                b.addForce(self._body)
                # print(1)
            else:
                self._NW.updateForce(b)
                self._NE.updateForce(b)
                self._SW.updateForce(b)
                self._SE.updateForce(b)
    
    def updateCollisions(self, b:Body,threshold:int)->None:
        '''
        check particle collisions piecewise if there are less than 'threshold' particles in the quad
        '''
        # not bothered checking borders
        if not b.inQuad(self._quad) or self.isExternal() or self._body == None:
            return
        elif self.numBodies <= threshold:
            bodies = self.getExternalBodies() # get all external node children
            for body in bodies:
                if b.distanceTo(body) < b.scaledRadius + body.scaledRadius:
                    # print(bodies)
                    self.collide(b,body)

        else:
            self._NW.updateCollisions(b,threshold)
            self._NE.updateCollisions(b,threshold)
            self._SW.updateCollisions(b,threshold)
            self._SE.updateCollisions(b,threshold)
    
    def getExternalBodies(self)->list[Body]:
        if self.isExternal():
            # print(self.numBodies)
            # print(self)
            # print('NW',self._NW)
            # print('NE',self._NE)
            # print('SW',self._SW)
            # print('SE',self._SE)
            # exit()
            return [self._body] if self._body != None else []
        else:
            return self._NW.getExternalBodies() + self._NE.getExternalBodies() + self._SW.getExternalBodies() + self._SE.getExternalBodies()

    def collide(self, b1:Body, b2:Body)->None:
        '''
        Elastic collision between two particles
        '''
        if b1 == b2:
            return
        print(b1," hash ",b1.__hash__())
        print(b2," hash ",b2.__hash__())
        print('colliding')
        m1 = b1._mass
        m2 = b2._mass
        r1 = b1.scaledRadius
        r2 = b2.scaledRadius
        x1 = b1._rx
        y1 = b1._ry
        x2 = b2._rx
        y2 = b2._ry
        vx1 = b1._vx
        vy1 = b1._vy
        vx2 = b2._vx
        vy2 = b2._vy

        dx = x2 - x1
        dy = y2 - y1
        dist = b1.distanceTo(b2)+EPSILON
        nx = dx / dist
        ny = dy / dist
        tx = -ny
        ty = nx
        dpTan1 = vx1 * tx + vy1 * ty
        dpTan2 = vx2 * tx + vy2 * ty
        dpNorm1 = vx1 * nx + vy1 * ny
        dpNorm2 = vx2 * nx + vy2 * ny
        m1f = (dpNorm1 * (m1 - m2) + 2 * m2 * dpNorm2) / (m1 + m2)
        m2f = (dpNorm2 * (m2 - m1) + 2 * m1 * dpNorm1) / (m1 + m2)
        b1._vx = tx * dpTan1 + nx * m1f
        b1._vy = ty * dpTan1 + ny * m1f
        b2._vx = tx * dpTan2 + nx * m2f
        b2._vy = ty * dpTan2 + ny * m2f
        # move particles so they don't overlap
        overlap = r1 + r2 - dist
        x1 -= overlap * nx
        y1 -= overlap * ny
        x2 += overlap * nx
        y2 += overlap * ny
        b1._rx = x1
        b1._ry = y1
        b2._rx = x2
        b2._ry = y2  

    def __str__(self)->str:
        if self.isExternal():
            return " " + str(self._body) + "\n"
        else:
            return "*" + str(self._body) + "\n" + str(self._NW) + str(self._NE) + str(self._SW) + str(self._SE)

    def draw(self, screen:pygame.display)->None:
        if self.numBodies == 1:
            self._quad.draw(screen)
        if not self.isExternal():
            self._NW.draw(screen)
            self._NE.draw(screen)
            self._SW.draw(screen)
            self._SE.draw(screen)
        
