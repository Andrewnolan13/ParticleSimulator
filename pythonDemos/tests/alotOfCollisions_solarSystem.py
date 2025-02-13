import sys
import os
thisDir = os.path.dirname(os.path.abspath(__file__))
upDir = os.path.dirname(thisDir)
sys.path.append(upDir)
print(upDir)


from body import Body
from tree import Tree
from quad import Quad
import pygame
import numpy as np
# from concurrent.futures import ThreadPoolExecutor, ProcessPoolExecutor
# import os

from simulation import Simulation




def  main():
    try:        
        ## make a solar system
        np.random.seed(10)

        bodies = []
        centre = np.array([Simulation.WIDTH / 2, Simulation.HEIGHT / 2])
        SUN: Body = Body(centre[0]+35, centre[1]+35, 0.0, 0.0, 10**13, (255, 0, 0))
        bodies.append(SUN)

        print(SUN)

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
            v = np.sqrt(SUN.G * SUN._mass / radius)*0
            vx = -v * np.sin(theta)
            vy = v * np.cos(theta)
            mass = masses[i%nrings]+max(0,np.random.randn()*10**3)
            body = Body(x, y, vx, vy, 10**10, (0, 255, 0))
            bodies.append(body)

        for i in range(1000):
            theta = 2 * np.pi * np.random.random()
            radius = rings[i%nrings] + 1* np.random.randn()
            x = SUN._rx + radius * np.cos(theta)
            y = SUN._ry + radius * np.sin(theta)
            v = np.sqrt(SUN.G * SUN._mass / radius**2)
            vx = -v * np.sin(theta)
            vy = v * np.cos(theta)
            mass = masses[i%nrings]+max(0,np.random.randn()*10**3)
            body = Body(x, y, vx, vy, 1, colors[i%nrings])
            bodies.append(body)    

        # single orbiting mass
        theta = 2 * np.pi * np.random.random()
        radius = 50
        x = SUN._rx + radius * np.cos(theta)
        y = SUN._ry + radius * np.sin(theta)
        vx,vy = 0,0
        mass = 1
        body = Body(x, y, vx, vy, mass, (0, 0, 255))
        bodies.append(body)


        sim = Simulation(bodies, dt =1, radius=Simulation.WIDTH)
        sim.simulate(Theta = float('inf'), drawBarnesHuts = False, frames_per_second = 600)
    except KeyboardInterrupt:
        print("Exiting...")
        return SUN
        
if __name__ == "__main__":
    SUN = main()
    print(SUN)
    exit(0)
