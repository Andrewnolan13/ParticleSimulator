import math
import pygame
from quad import Quad

class Body:
    __slots__ = ['_rx', '_ry', '_vx', '_vy', '_mass', '_color', '_fx', '_fy', 'radius']
    G:float = 6.67430e-11
    color:tuple[int] = (0,0,0)
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
    
    @property
    def scaledRadius(self)->int:
        return min(max(1,int(self.radius/1000)),25)
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

    def draw(self, screen:pygame.display)->None:
        pygame.draw.circle(screen, self._color, (int(self._rx), int(self._ry)), self.scaledRadius*2)
    
    def inQuad(self,q:'Quad')->bool:
        return q.contains(self._rx, self._ry)
    
    def plus(self, b:'Body')->'Body':
        mass = self._mass + b._mass
        rx = (self._rx * self._mass + b._rx * b._mass) / mass
        ry = (self._ry * self._mass + b._ry * b._mass) / mass
        vx = (self._vx * self._mass + b._vx * b._mass) / mass
        vy = (self._vy * self._mass + b._vy * b._mass) / mass
        return Body(rx, ry, vx, vy, mass, self._color)
    
    def __str__(self)->str:
        return f"Body(rx={self._rx}, ry={self._ry}, vx={self._vx}, vy={self._vy}, mass={self._mass}, color={self._color}, hash={self.__hash__()})"
    
    def __repr__(self)->str:
        return self.__str__()
    
    def __equals__(self, b:'Body')->bool:
        return self.__hash__() == b.__hash__()
    

