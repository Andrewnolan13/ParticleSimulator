import sys
import os

thisDir = os.path.dirname(os.path.abspath(__file__))
upDir = os.path.dirname(thisDir)
sys.path.append(upDir)

from body import Body
from simulation import Simulation
import numpy as np
import argparse

np.random.seed(10)

def get_args():
    '''drawBarnesHuts:bool = False,frames_per_second:int = 60'''
    parser = argparse.ArgumentParser(description='Run the simulation')
    parser.add_argument('--drawBH', type=bool, default=False, help='Draw Barnes-Hut tree')
    parser.add_argument('--fps', type=int, default=60, help='Frames per second')

    return parser.parse_args()

def main():
    np.random.seed(10)
    bodies = []
    # Large = Body(0, 350, 100, 0, 10, (255, 0, 0))
    # bodies = [Large]
    # Large.overRiddenRadius = 10


    # square in the centre of the screen
    integer = 64
    for i in range(100):
        x = (integer/2-1)*Simulation.WIDTH/integer +np.random.random() * Simulation.WIDTH/integer*2
        y = Simulation.HEIGHT/3+ (np.random.random()-0.5) * Simulation.HEIGHT/integer*2
        b = Body(x, y, 0, 0, 10, (0, 255, 0))
        b.overRiddenRadius = 4
        bodies.append(b)

    # Create a line of small particles, coming from the left, one after the other moving at a constant speed.
    # they need to have spacing between them.
    # It will look like a gun shotting bullets
    # but obviously I haven't made the ability to spawn particles in yet.
    origin = (0,Simulation.HEIGHT/3)
    space = 1
    speed = 50
    mass = 0.1
    n = 10000
    radius = 1
    for i in range(n):
        x = origin[0] - i*(2*radius + space)
        y = origin[1]
        b = Body(x, y, speed, 0, mass, (0, 0, 255))
        b.overRiddenRadius = radius
        bodies.append(b)


    origin = (Simulation.WIDTH/2,Simulation.HEIGHT)
    space = 0.00005
    speed = 50
    mass = 0.1
    n = 10000
    radius = 1
    for i in range(n):
        x = origin[0] #+ i*(2*radius + space)
        y = origin[1] + i*(2*radius + space)
        b = Body(x, y, 0, -speed, mass, (255, 0, 0))
        b.overRiddenRadius = radius
        bodies.append(b)

    # Initialize simulation
    args = get_args() 

    sim = Simulation(bodies, dt=0.1, radius=Simulation.WIDTH)
    sim.reCenter = False
    sim.gravitationalForce = False
    sim.collisionDetection = True
    # sim.localGravity = 9.8
    # sim.boundaryCollisions = True
    # drawBarnesHuts = args.drawBH
    # frames_per_second = args.fps
    sim.simulate(Theta=float('inf'), drawBarnesHuts=args.drawBH, frames_per_second=args.fps)

if __name__ == '__main__':
    main()
