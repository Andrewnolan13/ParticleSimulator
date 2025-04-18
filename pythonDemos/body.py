import math
import pygame
from quad import Quad

class Body:
    __slots__ = ['_rx', '_ry', '_vx', '_vy', '_mass', '_color', '_fx', '_fy', 'radius','overRiddenRadius']
    G:float = 6.67430e-11
    color:tuple[int] = (0,0,0)
    elastic:float = 1.0
    EPSILON:float = 10**-4
    def __init__(self, rx:float,ry:float,vx:float, vy:float,mass:float,color:tuple[int]):
        self._mass = mass
        self._rx = rx
        self._ry = ry
        self._vx = vx
        self._vy = vy
        self._color = color
        self._fx = 0.0
        self._fy = 0.0
        self.radius = self._mass ** (1/3)
        self.overRiddenRadius:int = None
    
    @property
    def scaledRadius(self)->int:
        return min(max(1,int(self.radius/1000)),25) if self.overRiddenRadius == None else self.overRiddenRadius
    @property
    def copy(self)->'Body':
        '''return a copy of the body'''
        newColor = (*self._color,) #defensive copy
        return Body(self._rx, self._ry, self._vx, self._vy, self._mass, newColor)
    
    def update(self, dt:float)->None:
        #rk4
        k1 = self._fx / self._mass
        l1 = self._fy / self._mass
        k2 = (self._fx + 0.5 * k1) / self._mass
        l2 = (self._fy + 0.5 * l1) / self._mass
        k3 = (self._fx + 0.5 * k2) / self._mass
        l3 = (self._fy + 0.5 * l2) / self._mass
        k4 = (self._fx + k3) / self._mass
        l4 = (self._fy + l3) / self._mass
        self._vx += dt * (k1 + 2 * k2 + 2 * k3 + k4) / 6
        self._vy += dt * (l1 + 2 * l2 + 2 * l3 + l4) / 6
        self._rx += dt * self._vx
        self._ry += dt * self._vy

    def distanceTo(self, b:'Body')->float:
        dx = b._rx - self._rx
        dy = b._ry - self._ry
        return math.sqrt(dx*dx + dy*dy)
    
    def resetForce(self)->None:
        self._fx = 0.0
        self._fy = 0.0

    def addForce(self, b:'Body')->None:
        # EPS = 3E4
        dx = b._rx - self._rx
        dy = b._ry - self._ry
        dist = max(math.sqrt(dx*dx + dy*dy),self.scaledRadius+b.scaledRadius)
        F = (Body.G * self._mass * b._mass) / (dist*dist)
        self._fx += F * dx / dist
        self._fy += F * dy / dist

    def inQuad(self,q:'Quad')->bool:
        return q.contains(self._rx, self._ry)
    
    def plus(self, b:'Body')->'Body':
        mass = self._mass + b._mass
        rx = (self._rx * self._mass + b._rx * b._mass) / mass
        ry = (self._ry * self._mass + b._ry * b._mass) / mass
        vx = (self._vx * self._mass + b._vx * b._mass) / mass
        vy = (self._vy * self._mass + b._vy * b._mass) / mass
        return Body(rx, ry, vx, vy, mass, self._color)

    def collide(self,other:'Body')->None:
        self.__collide(self,other)
    
    @staticmethod
    def __collide(b1:'Body',b2:'Body')->None:
    # def collide(self, b1:Body, b2:Body)->None:
        """
        Handles an elastic or inelastic collision between two bodies.
        The `elastic` coefficient (1.0 = perfectly elastic, 0.0 = perfectly inelastic) determines energy loss.
        """
        # print("colliding")
        # print(b1)
        # print(b2)
        # Extract properties
        m1, m2 = b1._mass, b2._mass
        r1, r2 = b1.scaledRadius, b2.scaledRadius
        x1, y1, x2, y2 = b1._rx, b1._ry, b2._rx, b2._ry
        vx1, vy1, vx2, vy2 = b1._vx, b1._vy, b2._vx, b2._vy
        e = Body.elastic  # Elasticity coefficient

        # Compute relative position and distance
        dx, dy = x2 - x1, y2 - y1
        dist = max((dx**2 + dy**2) ** 0.5, 1e-10)  # Avoid division by zero

        # Normal and tangent vectors
        nx, ny = dx / dist, dy / dist
        tx, ty = -ny, nx

        # Decompose velocities
        dpTan1 = vx1 * tx + vy1 * ty
        dpTan2 = vx2 * tx + vy2 * ty
        dpNorm1 = vx1 * nx + vy1 * ny
        dpNorm2 = vx2 * nx + vy2 * ny

        # Compute new normal velocities using elastic coefficient
        m1f = ((m1 - e * m2) * dpNorm1 + (1 + e) * m2 * dpNorm2) / (m1 + m2)
        m2f = ((m2 - e * m1) * dpNorm2 + (1 + e) * m1 * dpNorm1) / (m1 + m2)

        # Convert back to x, y components
        b1._vx, b1._vy = tx * dpTan1 + nx * m1f, ty * dpTan1 + ny * m1f
        b2._vx, b2._vy = tx * dpTan2 + nx * m2f, ty * dpTan2 + ny * m2f

        # Resolve overlap by shifting bodies apart in a mass-weighted manner
        overlap = r1 + r2 - dist
        if overlap > 0:
            # correction1 = (m2/ (m1 + m2)) * overlap
            # correction2 = (m1/ (m1 + m2)) * overlap
            # b1._rx -= correction1 * nx
            # b1._ry -= correction1 * ny
            # b2._rx += correction2 * nx
            # b2._ry += correction2 * ny
            # b1._rx -= overlap * nx
            # b1._ry -= overlap * ny
            # b2._rx += overlap * nx
            # b2._ry += overlap * ny



            largeBody = b1 if b1._mass > b2._mass else b2
            smallBody = b1 if largeBody == b2 else b2
            # smallbody get's shifted by the overlap, big one doesn't.
            dx = largeBody._rx - smallBody._rx
            dy = largeBody._ry - smallBody._ry
            dist = math.sqrt(dx*dx + dy*dy)+Body.EPSILON
            nx = dx / dist
            ny = dy / dist

            overlap = largeBody.scaledRadius + smallBody.scaledRadius - dist
            smallBody._rx -= overlap * nx
            smallBody._ry -= overlap * ny

            # smallBody._rx += overlap * nx
            # smallBody._ry += overlap * ny      

        
    def draw(self, screen:pygame.display)->None:
        position = (round(self._rx), round(self._ry))
        pygame.draw.circle(screen, self._color, (*position,), self.scaledRadius)
    
    def __str__(self)->str:
        return f"Body(rx={self._rx}, ry={self._ry}, vx={self._vx}, vy={self._vy}, mass={self._mass}, color={self._color}, hash={self.__hash__()})"
    
    def __repr__(self)->str:
        return self.__str__()
    
    def __equals__(self, b:'Body')->bool:
        return self.__hash__() == b.__hash__()
    

