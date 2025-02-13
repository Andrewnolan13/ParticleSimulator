import numpy as np

class Quad:
    def __init__(self, center, size):
        self.center = center
        self.size = size

    @staticmethod
    def new_containing(bodies):
        min_x = float('inf')
        min_y = float('inf')
        max_x = float('-inf')
        max_y = float('-inf')

        for body in bodies:
            min_x = min(min_x, body.pos[0])
            min_y = min(min_y, body.pos[1])
            max_x = max(max_x, body.pos[0])
            max_y = max(max_y, body.pos[1])

        center = np.array([min_x + max_x, min_y + max_y]) * 0.5
        size = max(max_x - min_x, max_y - min_y)

        return Quad(center, size)

    def find_quadrant(self, pos):
        return (pos[1] > self.center[1]) << 1 | (pos[0] > self.center[0])

    def into_quadrant(self, quadrant):
        size_half = self.size * 0.5
        center_x = self.center[0] + ((quadrant & 1) - 0.5) * size_half
        center_y = self.center[1] + ((quadrant >> 1) - 0.5) * size_half
        return Quad(np.array([center_x, center_y]), size_half)

    def subdivide(self):
        return [self.into_quadrant(i) for i in range(4)]

class Node:
    def __init__(self, next_node, quad):
        self.children = 0
        self.next = next_node
        self.pos = np.zeros(2)
        self.mass = 0.0
        self.quad = quad

    def is_leaf(self):
        return self.children == 0

    def is_branch(self):
        return self.children != 0

    def is_empty(self):
        return self.mass == 0.0

class Quadtree:
    ROOT = 0

    def __init__(self, theta, epsilon):
        self.t_sq = theta * theta
        self.e_sq = epsilon * epsilon
        self.nodes = []
        self.parents = []

    def clear(self, quad):
        self.nodes.clear()
        self.parents.clear()
        self.nodes.append(Node(0, quad))

    def subdivide(self, node):
        self.parents.append(node)
        children = len(self.nodes)
        self.nodes[node].children = children

        nexts = [children + i + 1 for i in range(3)] + [self.nodes[node].next]
        quads = self.nodes[node].quad.subdivide()

        for i in range(4):
            self.nodes.append(Node(nexts[i], quads[i]))

        return children

    def insert(self, pos, mass):
        node = self.ROOT

        while self.nodes[node].is_branch():
            quadrant = self.nodes[node].quad.find_quadrant(pos)
            node = self.nodes[node].children + quadrant

        if self.nodes[node].is_empty():
            self.nodes[node].pos = pos
            self.nodes[node].mass = mass
            return

        p, m = self.nodes[node].pos, self.nodes[node].mass
        if np.array_equal(pos, p):
            self.nodes[node].mass += mass
            return

        while True:
            children = self.subdivide(node)

            q1 = self.nodes[node].quad.find_quadrant(p)
            q2 = self.nodes[node].quad.find_quadrant(pos)

            if q1 == q2:
                node = children + q1
            else:
                n1 = children + q1
                n2 = children + q2

                self.nodes[n1].pos = p
                self.nodes[n1].mass = m
                self.nodes[n2].pos = pos
                self.nodes[n2].mass = mass
                return

    def propagate(self):
        for node in reversed(self.parents):
            i = self.nodes[node].children

            self.nodes[node].pos = sum(self.nodes[i + j].pos * self.nodes[i + j].mass for j in range(4))
            self.nodes[node].mass = sum(self.nodes[i + j].mass for j in range(4))

            mass = self.nodes[node].mass
            self.nodes[node].pos /= mass

    def acc(self, pos):
        acc = np.zeros(2)
        node = self.ROOT

        while True:
            n = self.nodes[node]

            d = n.pos - pos
            d_sq = np.dot(d, d)

            if n.is_leaf() or n.quad.size * n.quad.size < d_sq * self.t_sq:
                denom = (d_sq + self.e_sq) * np.sqrt(d_sq)
                acc += d * min(n.mass / denom, float('inf'))

                if n.next == 0:
                    break
                node = n.next
            else:
                node = n.children

        return acc
