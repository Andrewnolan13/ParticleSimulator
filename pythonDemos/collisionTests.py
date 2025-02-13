from body import Body
from tree import Tree
from quad import Quad
import pygame
import numpy as np
# from concurrent.futures import ThreadPoolExecutor, ProcessPoolExecutor
# import os
import sys
# from simulation import Simulation

sys.setrecursionlimit(3000)

class Simulation:
    __slots__ = ['bodies', 'dt', 'radius', 'N']
    WIDTH:int = 800
    HEIGHT:int = 800
    def __init__(self, bodies:list[Body], dt:float, radius:float) -> None:
        self.bodies = bodies
        self.dt = dt
        self.radius = radius
        self.N = len(bodies)

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
            numBodies_text = font.render(f"Number of bodies: {self.N}", True, pygame.Color('white'))
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
        self.bhHandleCollisions(tree)
        # Cetnre of mass recentering. If the system run's away, you still see it. 
        # com_x = sum(body._rx*body._mass for body in self.bodies) / sum(body._mass for body in self.bodies)
        # com_y = sum(body._ry*body._mass for body in self.bodies) / sum(body._mass for body in self.bodies)

        # for body in self.bodies:
        #     body._rx += (screen.get_width()/2 - com_x)
        #     body._ry += (screen.get_height()/2 - com_y)               

    def bhGravity(self, screen:pygame.display, tree:Tree)->None:
        for body in self.bodies:
            body.draw(screen)
            body.resetForce()
            tree.updateForce(body)
            body.update(self.dt)
    
    def bhHandleCollisions(self, tree:Tree):
        for body in self.bodies:
            tree.updateCollisions(body,threshold=10)

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


def  main():
    try:
        ## make a solar system
        np.random.seed(10)
        # # two particles, stationary initially
        bodies = []
        bodies.append(Body(1, 200, 50, 0, 10**12, (255, 0, 0)))
        bodies.append(Body(799, 200, -150, 0, 10**6, (0, 255, 0)))

        print(bodies)

        sim = Simulation(bodies, dt =0.01, radius=Simulation.WIDTH)
        sim.simulate(Theta = float('inf'), drawBarnesHuts = True, frames_per_second = 60)
    except KeyboardInterrupt:
        print("Exiting...")
        return bodies
        
if __name__ == "__main__":
    bodies = main()
    print(bodies)
    exit(0)

    ## make a system of two particles and then check the tree's external children
    # bodies:list[Body] = []
    # bodies.append(Body(1, 200, 50, 0, 1**12, (255, 0, 0)))
    # bodies.append(Body(799, 200, -150, 0, 1**12, (0, 255, 0)))

    # quad = Quad(400,400,800)
    # tree = Tree(quad,Theta = float('inf'))

    # for body in bodies:
    #     if body.inQuad(quad):
    #         tree.insert(body)
    
    # # print(tree.getExternalBodies())
    # def recurseTree(tree:Tree):
    #     return tree.getExternalBodies()
    #     # if all([tree._NE == None, tree._NW == None, tree._SE == None, tree._SW == None]):
    #     #     return [tree._body] if tree._body != None else []
    #     # else:
    #     #     return recurseTree(tree._NW) + recurseTree(tree._NE) + recurseTree(tree._SW) + recurseTree(tree._SE)
    
    # print(recurseTree(tree))
    # # print(bodies)
    # print(tree._body)
