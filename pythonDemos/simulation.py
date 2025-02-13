from body import Body
from tree import Tree
from quad import Quad
import pygame
import numpy as np
# from concurrent.futures import ThreadPoolExecutor, ProcessPoolExecutor
# import os
import sys

sys.setrecursionlimit(3000)

class Simulation:
    __slots__ = ['bodies', 'dt', 'radius', 'N','reCenter']
    WIDTH:int = 800
    HEIGHT:int = 800
    def __init__(self, bodies:list[Body], dt:float, radius:float) -> None:
        self.bodies = bodies
        self.dt = dt
        self.radius = radius
        self.N = len(bodies)
        self.reCenter = True
        # self.collisionThreshold = collisionThreshold

    def simulate(self,Theta,drawBarnesHuts:bool = False,frames_per_second:int = 60) -> None:
        pygame.init()
        screen = pygame.display.set_mode((Simulation.WIDTH, Simulation.HEIGHT),pygame.RESIZABLE)
        clock = pygame.time.Clock()
        font = pygame.font.SysFont("Arial", 18)

        running = True
        while running:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    running = False

            radius = max(screen.get_width(), screen.get_height())
            quad = Quad(screen.get_width()/2, screen.get_height()/2, radius) #:TODO generalize to rectangles
            tree = Tree(quad,Theta = Theta)
            # Insert all bodies into the tree
            for body in self.bodies:
                if body.inQuad(quad):
                    tree.insert(body)
            # Update forces and positions and draw
            self.__draw(screen,tree,drawBarnesHuts)

            # Display FPS
            fps = clock.get_fps()
            fps_text = font.render(f"FPS: {fps:.2f}", True, pygame.Color('white'))
            numBodies_text = font.render(f"Number of bodies: {tree.numBodies}", True, pygame.Color('white'))
            screen.blit(fps_text, (10, 10))
            screen.blit(numBodies_text, (10, 30))

            pygame.display.flip()
            clock.tick(frames_per_second)
            # print(tree.getExternalBodies())
        pygame.quit()
    
    def __draw(self,screen:pygame.display,tree:Tree,drawBarnesHuts:bool)->None:
        screen.fill((0, 0, 0))
        if drawBarnesHuts:
            tree.draw(screen)
        #update forces and positions
        self.bhGravity(screen,tree)
        # self.bruteForceGravity(screen)
        # self.bruteForce(screen)

        # collision detection
        self.bhHandleCollisions(screen,tree)
        if self.reCenter:
            # Cetnre of mass recentering. If the system run's away, you still see it. 
            offset = max([body.scaledRadius+5 for body in self.bodies])
            com_x = sum(body._rx*body._mass for body in self.bodies) / sum(body._mass for body in self.bodies) + offset
            com_y = sum(body._ry*body._mass for body in self.bodies) / sum(body._mass for body in self.bodies) + offset

            for body in self.bodies:
                body._rx += (screen.get_width()/2 - com_x)
                body._ry += (screen.get_height()/2 - com_y)

        for body in self.bodies:
            body.update(self.dt)

        for body in self.bodies:              
            body.draw(screen)     

    def bhGravity(self, screen:pygame.display, tree:Tree)->None:
        for body in self.bodies:
            # body.draw(screen)
            body.resetForce()
            tree.updateForce(body)
            # body.update(self.dt)
    
    def bhHandleCollisions(self,screen:pygame.display,tree:Tree)->None:
        for body in self.bodies:
            # body.draw(screen)
            tree.updateCollisions(body,threshold=tree.numBodies/10)

    def bruteForceGravity(self, screen:pygame.display)->None:
        for body in self.bodies:
            body.draw(screen)
            body.resetForce()
        for i in range(self.N):
            for j in range(i+1,self.N):
                self.bodies[i].addForce(self.bodies[j])
                self.bodies[j].addForce(self.bodies[i])
        for body in self.bodies:
            body.update(self.dt)
    
    def bruteForceHandleCollisions(self):
        # brute force collision detection
        for i in range(self.N):
            for j in range(i+1,self.N):
                if self.bodies[i].distanceTo(self.bodies[j]) < self.bodies[i].scaledRadius + self.bodies[j].scaledRadius:
                    idx1 = i if self.bodies[i]._mass > self.bodies[j]._mass else j
                    idx2 = i if idx1 == j else j
                    self.bodies[idx1] = self.bodies[idx1].plus(self.bodies[idx2])
                    self.bodies.pop(idx2)
                    self.N -= 1
                    break

if __name__ == "__main__":
    ## make a solar system
    np.random.seed(10)

    bodies = []
    centre = np.array([Simulation.WIDTH / 2, Simulation.HEIGHT / 2])
    SUN: Body = Body(centre[0]+50, centre[1]+50, 0.0, 0.0, 10**13, (255, 0, 0))
    bodies.append(SUN)
    widthFactor = min(Simulation.WIDTH / 2, Simulation.HEIGHT / 2)
    nrings = 3
    # rings = [widthFactor * 0.125, widthFactor * 0.25, widthFactor * 0.5, widthFactor * 0.625, widthFactor * 0.75, widthFactor * 0.875]
    rings = [widthFactor*(i+1)/(nrings+1) for i in range(nrings)][::-1]
    masses = [10**(0.5*(i)) for i in range(nrings)]
    # make nrings distinct colors
    colors = [(255, 255 * i // nrings, 255 * (nrings - i) // nrings) for i in range(nrings)]
    
    for i in range(20):
        theta = 2 * np.pi * np.random.random()
        radius = rings[i%nrings] + 1* np.random.randn()
        x = SUN._rx + radius * np.cos(theta)
        y = SUN._ry + radius * np.sin(theta)
        v = np.sqrt(SUN.G * SUN._mass / radius)
        vx = -v * np.sin(theta)
        vy = v * np.cos(theta)
        mass = masses[i%nrings]+max(0,np.random.randn()*10**3)
        body = Body(x, y, vx, vy, 10**10, (0, 255, 0))
        bodies.append(body)

    for i in range(100):
        theta = 2 * np.pi * np.random.random()
        radius = rings[i%nrings] + 1* np.random.randn()
        x = SUN._rx + radius * np.cos(theta)
        y = SUN._ry + radius * np.sin(theta)
        v = np.sqrt(SUN.G * SUN._mass / radius)
        vx = -v * np.sin(theta)
        vy = v * np.cos(theta)
        mass = masses[i%nrings]+max(0,np.random.randn()*10**3)
        body = Body(x, y, vx, vy, 1, colors[i%nrings])
        bodies.append(body)    

    # single orbiting mass
    theta = 2 * np.pi * np.random.random()
    radius = 400
    x = SUN._rx + radius * np.cos(theta)
    y = SUN._ry + radius * np.sin(theta)
    v = np.sqrt(SUN.G * SUN._mass / radius)
    vx = -v * np.sin(theta)
    vy = v * np.cos(theta)
    mass = 10**0
    body = Body(x, y, vx, vy, mass, (0, 0, 255))
    bodies.append(body)


    # # two particles, stationary initially
    # bodies = []
    # bodies.append(Body(1, 200, 50, 0, 1**12, (255, 0, 0)))
    # bodies.append(Body(799, 200, -150, 0, 1**12, (0, 255, 0)))

    # print(bodies)

    sim = Simulation(bodies, dt =0.1, radius=Simulation.WIDTH)
    sim.simulate(Theta = float('inf'),drawBarnesHuts = False, frames_per_second = 120)