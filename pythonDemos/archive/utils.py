1 
import numpy as np
import random

class Body:
    def __init__(self, pos, vel, mass, radius):
        self.pos = pos
        self.vel = vel
        self.mass = mass
        self.radius = radius

    @classmethod
    def new(cls, pos, vel, mass, radius):
        return cls(pos, vel, mass, radius)

def uniform_disc(n):
    random.seed(0)
    inner_radius = 25.0
    outer_radius = np.sqrt(n) * 5.0

    bodies = []

    m = 1e6
    center = Body.new(np.zeros(2), np.zeros(2), m, inner_radius)
    bodies.append(center)

    while len(bodies) < n:
        a = random.random() * 2 * np.pi
        sin_a, cos_a = np.sin(a), np.cos(a)
        t = inner_radius / outer_radius
        r = random.random() * (1.0 - t ** 2) + t ** 2
        pos = np.array([cos_a, sin_a]) * outer_radius * np.sqrt(r)
        vel = np.array([sin_a, -cos_a])
        mass = 1.0
        radius = mass ** (1 / 3)

        bodies.append(Body.new(pos, vel, mass, radius))

    bodies.sort(key=lambda b: np.linalg.norm(b.pos) ** 2)

    total_mass = 0.0
    for body in bodies:
        total_mass += body.mass
        if np.allclose(body.pos, np.zeros(2)):
            continue

        v = np.sqrt(total_mass / np.linalg.norm(body.pos))
        body.vel *= v

    return bodies
