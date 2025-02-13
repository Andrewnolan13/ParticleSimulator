import numpy as np
import utils
from quadtree import Quadtree, Quad


class Rect:
    def __init__(self, x_min, x_max, y_min, y_max):
        self.x_min = x_min
        self.x_max = x_max
        self.y_min = y_min
        self.y_max = y_max

    def intersects(self, other):
        return not (self.x_max < other.x_min or self.x_min > other.x_max or
                    self.y_max < other.y_min or self.y_min > other.y_max)


class Tree:
    def __init__(self, rects):
        self.rects = rects

    def find_colliding_pairs(self, collision_callback):
        n = len(self.rects)
        for i in range(n):
            for j in range(i + 1, n):
                if self.rects[i][0].intersects(self.rects[j][0]):
                    collision_callback(self.rects[i][1], self.rects[j][1])


class Simulation:
    def __init__(self):
        self.dt = 0.05
        self.frame = 0
        self.bodies = self.uniform_disc(100000)
        self.quadtree = Quadtree(theta=1.0, epsilon=1.0)

    def uniform_disc(self, n):
        # Assuming utils.uniform_disc is implemented elsewhere
        return utils.uniform_disc(n)

    def step(self):
        self.iterate()
        self.collide()
        self.attract()
        self.frame += 1

    def attract(self):
        quad = Quad.new_containing(self.bodies)
        self.quadtree.clear(quad)

        for body in self.bodies:
            self.quadtree.insert(body.pos, body.mass)

        self.quadtree.propagate()

        for body in self.bodies:
            body.acc = self.quadtree.acc(body.pos)

    def iterate(self):
        for body in self.bodies:
            body.update(self.dt)

    def collide(self):
        rects = []
        for index, body in enumerate(self.bodies):
            pos = body.pos
            radius = body.radius
            min_pos = pos - np.ones(2) * radius
            max_pos = pos + np.ones(2) * radius
            rects.append((Rect(min_pos[0], max_pos[0], min_pos[1], max_pos[1]), index))

        broccoli_tree = Tree(rects)

        def handle_collision(i, j):
            self.resolve(i, j)

        broccoli_tree.find_colliding_pairs(handle_collision)

    def resolve(self, i, j):
        b1 = self.bodies[i]
        b2 = self.bodies[j]

        p1 = b1.pos
        p2 = b2.pos

        r1 = b1.radius
        r2 = b2.radius

        d = p2 - p1
        r = r1 + r2

        if np.dot(d, d) > r * r:
            return

        v1 = b1.vel
        v2 = b2.vel

        v = v2 - v1
        d_dot_v = np.dot(d, v)

        m1 = b1.mass
        m2 = b2.mass

        weight1 = m2 / (m1 + m2)
        weight2 = m1 / (m1 + m2)

        if d_dot_v >= 0.0 and np.any(d != 0):
            tmp = d * (r / np.linalg.norm(d) - 1.0)
            b1.pos -= weight1 * tmp
            b2.pos += weight2 * tmp
            return

        v_sq = np.dot(v, v)
        d_sq = np.dot(d, d)
        r_sq = r * r

        t = (d_dot_v + np.sqrt(max(0.0, d_dot_v ** 2 - v_sq * (d_sq - r_sq)))) / v_sq

        b1.pos -= v1 * t
        b2.pos -= v2 * t

        d = b2.pos - b1.pos
        d_dot_v = np.dot(d, v)
        d_sq = np.dot(d, d)

        tmp = d * (1.5 * d_dot_v / d_sq)
        v1 += tmp * weight1
        v2 -= tmp * weight2

        b1.vel = v1
        b2.vel = v2
        b1.pos += v1 * t
        b2.pos += v2 * t


# class Simulation:
#     def __init__(self):
#         self.dt = 0.05
#         self.frame = 0
#         self.bodies = self.uniform_disc(100000)
#         self.quadtree = Quadtree(theta=1.0, epsilon=1.0)

#     def uniform_disc(self, n):
#         # Assuming utils.uniform_disc is implemented elsewhere
#         return utils.uniform_disc(n)

#     def step(self):
#         self.iterate()
#         self.collide()
#         self.attract()
#         self.frame += 1

#     def attract(self):
#         quad = Quad.new_containing(self.bodies)
#         self.quadtree.clear(quad)

#         for body in self.bodies:
#             self.quadtree.insert(body.pos, body.mass)

#         self.quadtree.propagate()

#         for body in self.bodies:
#             body.acc = self.quadtree.acc(body.pos)

#     def iterate(self):
#         for body in self.bodies:
#             body.update(self.dt)

#     def collide(self):
#         rects = []
#         for index, body in enumerate(self.bodies):
#             pos = body.pos
#             radius = body.radius
#             min_pos = pos - np.ones(2) * radius
#             max_pos = pos + np.ones(2) * radius
#             rects.append((Rect(min_pos[0], max_pos[0], min_pos[1], max_pos[1]), index))

#         broccoli_tree = broccoli.Tree(rects)

#         def handle_collision(i, j):
#             self.resolve(i, j)

#         broccoli_tree.find_colliding_pairs(handle_collision)

#     def resolve(self, i, j):
#         b1 = self.bodies[i]
#         b2 = self.bodies[j]

#         p1 = b1.pos
#         p2 = b2.pos

#         r1 = b1.radius
#         r2 = b2.radius

#         d = p2 - p1
#         r = r1 + r2

#         if np.dot(d, d) > r * r:
#             return

#         v1 = b1.vel
#         v2 = b2.vel

#         v = v2 - v1
#         d_dot_v = np.dot(d, v)

#         m1 = b1.mass
#         m2 = b2.mass

#         weight1 = m2 / (m1 + m2)
#         weight2 = m1 / (m1 + m2)

#         if d_dot_v >= 0.0 and np.any(d != 0):
#             tmp = d * (r / np.linalg.norm(d) - 1.0)
#             b1.pos -= weight1 * tmp
#             b2.pos += weight2 * tmp
#             return

#         v_sq = np.dot(v, v)
#         d_sq = np.dot(d, d)
#         r_sq = r * r

#         t = (d_dot_v + np.sqrt(max(0.0, d_dot_v ** 2 - v_sq * (d_sq - r_sq)))) / v_sq

#         b1.pos -= v1 * t
#         b2.pos -= v2 * t

#         d = b2.pos - b1.pos
#         d_dot_v = np.dot(d, v)
#         d_sq = np.dot(d, d)

#         tmp = d * (1.5 * d_dot_v / d_sq)
#         v1 += tmp * weight1
#         v2 -= tmp * weight2

#         b1.vel = v1
#         b2.vel = v2
#         b1.pos += v1 * t
#         b2.pos += v2 * t


# class Simulation:
#     def __init__(self):
#         self.dt = 0.05
#         self.frame = 0
#         self.bodies = utils.uniform_disc(100000)
#         self.quadtree = Quadtree(theta=1.0, epsilon=1.0)

#     def step(self):
#         self.iterate()
#         self.collide()
#         self.attract()
#         self.frame += 1

#     def attract(self):
#         quad = Quad.new_containing(self.bodies)
#         self.quadtree.clear(quad)

#         for body in self.bodies:
#             self.quadtree.insert(body.pos, body.mass)

#         self.quadtree.propagate()

#         for body in self.bodies:
#             body.acc = self.quadtree.acc(body.pos)

#     def iterate(self):
#         for body in self.bodies:
#             body.update(self.dt)

#     def collide(self):
#         rects = []
#         for index, body in enumerate(self.bodies):
#             pos = body.pos
#             radius = body.radius
#             min_pos = pos - np.ones(2) * radius
#             max_pos = pos + np.ones(2) * radius
#             rects.append((Rect(min_pos[0], max_pos[0], min_pos[1], max_pos[1]), index))

#         broccoli = Tree(rects)

#         def resolve_pair(i, j):
#             self.resolve(i, j)

#         broccoli.find_colliding_pairs(resolve_pair)

#     def resolve(self, i, j):
#         b1 = self.bodies[i]
#         b2 = self.bodies[j]

#         p1 = b1.pos
#         p2 = b2.pos

#         r1 = b1.radius
#         r2 = b2.radius

#         d = p2 - p1
#         r = r1 + r2

#         if np.dot(d, d) > r * r:
#             return

#         v1 = b1.vel
#         v2 = b2.vel

#         v = v2 - v1

#         d_dot_v = np.dot(d, v)

#         m1 = b1.mass
#         m2 = b2.mass

#         weight1 = m2 / (m1 + m2)
#         weight2 = m1 / (m1 + m2)

#         if d_dot_v >= 0.0 and not np.allclose(d, 0):
#             tmp = d * (r / np.linalg.norm(d) - 1.0)
#             self.bodies[i].pos -= weight1 * tmp
#             self.bodies[j].pos += weight2 * tmp
#             return

#         v_sq = np.dot(v, v)
#         d_sq = np.dot(d, d)
#         r_sq = r * r

#         t = (d_dot_v + np.sqrt(max(0.0, d_dot_v ** 2 - v_sq * (d_sq - r_sq)))) / v_sq

#         self.bodies[i].pos -= v1 * t
#         self.bodies[j].pos -= v2 * t

#         p1 = self.bodies[i].pos
#         p2 = self.bodies[j].pos
#         d = p2 - p1
#         d_dot_v = np.dot(d, v)
#         d_sq = np.dot(d, d)

#         tmp = d * (1.5 * d_dot_v / d_sq)
#         v1 = v1 + tmp * weight1
#         v2 = v2 - tmp * weight2

#         self.bodies[i].vel = v1
#         self.bodies[j].vel = v2
#         self.bodies[i].pos += v1 * t
#         self.bodies[j].pos += v2 * t
