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
Simulation.WIDTH = 20
print(Simulation.WIDTH)
def get_args():
    '''drawBarnesHuts:bool = False,frames_per_second:int = 60'''
    parser = argparse.ArgumentParser(description='Run the simulation')
    parser.add_argument('--drawBH', type=bool, default=False, help='Draw Barnes-Hut tree')
    parser.add_argument('--fps', type=int, default=60, help='Frames per second')

    return parser.parse_args()

def main():
    np.random.seed(10)
    bodies = []

    # square in the centre of the screen
    integer = 5
    for i in range(500):
        x = (integer/2-1)*Simulation.WIDTH/integer +np.random.random() * Simulation.WIDTH/integer*2
        y = Simulation.HEIGHT+(np.random.random()-1) * Simulation.HEIGHT/integer*2-100
        b = Body(x, y, 0, 0, 0.01, (0,0 , 255))
        b.overRiddenRadius = 4
        bodies.append(b)


    # Initialize simulation
    args = get_args() 

    sim = Simulation(bodies, dt=0.01, radius=Simulation.WIDTH)
    sim.reCenter = False
    sim.gravitationalForce = False
    sim.collisionDetection = True
    sim.localGravity = 9.8
    sim.boundaryCollisions = True
    sim.simulate(Theta=float('inf'), drawBarnesHuts=args.drawBH, frames_per_second=args.fps)

if __name__ == '__main__':
    main()