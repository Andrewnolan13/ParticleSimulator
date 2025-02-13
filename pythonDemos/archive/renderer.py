import math
import numpy as np
from threading import Lock
from collections import deque

from body import Body
from quadtree import Quadtree, Quad, Node

import math
from threading import Lock
from body import Body
from quadtree import Node, Quadtree
from quarkstrom import egui, WinitInputHelper
from palette import Hsluv
from ultraviolet import Vec2

PAUSED = False
UPDATE_LOCK = Lock()

BODIES = []
QUADTREE = []
SPAWN = []

class Renderer:
    def __init__(self):
        self.pos = Vec2.zero()
        self.scale = 3600.0

        self.settings_window_open = False
        self.show_bodies = True
        self.show_quadtree = False

        self.depth_range = (0, 0)
        self.spawn_body = None
        self.angle = None
        self.total = None
        self.confirmed_bodies = None

        self.bodies = []
        self.quadtree = []

    def input(self, input: WinitInputHelper, width: int, height: int):
        global PAUSED
        self.settings_window_open ^= input.key_pressed('E')

        if input.key_pressed('Space'):
            PAUSED = not PAUSED

        if input.mouse():
            mx, my = input.mouse()
            steps = 5.0
            zoom = (-input.scroll_diff() / steps).exp2()
            target = Vec2((mx * 2.0 - width) / width, (height - my * 2.0) / height)
            self.pos += target * self.scale * (1.0 - zoom)
            self.scale *= zoom

        if input.mouse_held(2):
            mdx, mdy = input.mouse_diff()
            self.pos.x -= mdx / height * self.scale * 2.0
            self.pos.y += mdy / height * self.scale * 2.0

        def world_mouse():
            mx, my = input.mouse() or (0, 0)
            mouse = Vec2(mx, my)
            mouse *= 2.0 / height
            mouse.y -= 1.0
            mouse.y *= -1.0
            mouse.x -= width / height
            return mouse * self.scale + self.pos

        if input.mouse_pressed(1):
            mouse = world_mouse()
            self.spawn_body = Body.new(mouse, Vec2.zero(), 1.0, 1.0)
            self.angle = None
            self.total = 0.0
        elif input.mouse_held(1):
            if self.spawn_body:
                mouse = world_mouse()
                if self.angle:
                    d = mouse - self.spawn_body.pos
                    angle2 = math.atan2(d.y, d.x)
                    a = angle2 - self.angle
                    a = (a + math.pi) % (2 * math.pi) - math.pi
                    total = self.total - a
                    self.spawn_body.mass = (total / math.tau).exp2()
                    self.angle = angle2
                    self.total = total
                else:
                    d = mouse - self.spawn_body.pos
                    angle = math.atan2(d.y, d.x)
                    self.angle = angle
                self.spawn_body.radius = self.spawn_body.mass ** (1/3)
                self.spawn_body.vel = mouse - self.spawn_body.pos
        elif input.mouse_released(1):
            self.confirmed_bodies = self.spawn_body
            self.spawn_body = None

    def render(self, ctx):
        global BODIES, QUADTREE, SPAWN

        with UPDATE_LOCK:
            BODIES, self.bodies = self.bodies, BODIES
            QUADTREE, self.quadtree = self.quadtree, QUADTREE

        if self.confirmed_bodies:
            self.bodies.append(self.confirmed_bodies)
            SPAWN.append(self.confirmed_bodies)
            self.confirmed_bodies = None

        ctx.clear_circles()
        ctx.clear_lines()
        ctx.clear_rects()
        ctx.set_view_pos(self.pos)
        ctx.set_view_scale(self.scale)

        if self.bodies:
            if self.show_bodies:
                for body in self.bodies:
                    ctx.draw_circle(body.pos, body.radius, [0xff, 0xff, 0xff, 0xff])

            if self.confirmed_bodies:
                ctx.draw_circle(self.confirmed_bodies.pos, self.confirmed_bodies.radius, [0xff, 0xff, 0xff, 0xff])
                ctx.draw_line(self.confirmed_bodies.pos, self.confirmed_bodies.pos + self.confirmed_bodies.vel, [0xff, 0xff, 0xff, 0xff])

            if self.spawn_body:
                ctx.draw_circle(self.spawn_body.pos, self.spawn_body.radius, [0xff, 0xff, 0xff, 0xff])
                ctx.draw_line(self.spawn_body.pos, self.spawn_body.pos + self.spawn_body.vel, [0xff, 0xff, 0xff, 0xff])

        if self.show_quadtree and self.quadtree:
            min_depth, max_depth = self.depth_range
            if min_depth >= max_depth:
                stack = [(Quadtree.ROOT, 0)]
                min_depth, max_depth = float('inf'), 0

                while stack:
                    node, depth = stack.pop()
                    node = self.quadtree[node]

                    if node.is_leaf():
                        min_depth = min(min_depth, depth)
                        max_depth = max(max_depth, depth)
                    else:
                        for i in range(4):
                            stack.append((node.children + i, depth + 1))

            stack = [(Quadtree.ROOT, 0)]
            while stack:
                node, depth = stack.pop()
                node = self.quadtree[node]

                if node.is_branch() and depth < max_depth:
                    for i in range(4):
                        stack.append((node.children + i, depth + 1))
                elif depth >= min_depth:
                    quad = node.quad
                    half = Vec2(0.5, 0.5) * quad.size
                    min_point = quad.center - half
                    max_point = quad.center + half

                    t = (depth - min_depth + int(not node.is_empty())) / (max_depth - min_depth + 1)
                    h = -100.0 + (80.0 - (-100.0)) * t
                    s = 100.0
                    l = t * 100.0
                    color = Hsluv(h, s, l).into_color().into_format()
                    ctx.draw_rect(min_point, max_point, color)

    def gui(self, ctx):
        egui.Window("Settings").open(self.settings_window_open).show(ctx, lambda ui: [
            ui.checkbox(self.show_bodies, "Show Bodies"),
            ui.checkbox(self.show_quadtree, "Show Quadtree"),
            ui.horizontal(lambda ui: [
                ui.label("Depth Range:"),
                ui.add(egui.DragValue(self.depth_range[0]).speed(0.05)),
                ui.label("to"),
                ui.add(egui.DragValue(self.depth_range[1]).speed(0.05))
            ]) if self.show_quadtree else None
        ])


# class Renderer:
#     def __init__(self):
#         self.pos = np.array([0.0, 0.0], dtype=np.float32)
#         self.scale = 3600.0
#         self.settings_window_open = False
#         self.show_bodies = True
#         self.show_quadtree = False
#         self.depth_range = (0, 0)
#         self.spawn_body = None
#         self.angle = None
#         self.total = None
#         self.confirmed_bodies = None
#         self.bodies = []
#         self.quadtree = []
#         self.PAUSED = False
#         self.UPDATE_LOCK = Lock()

#     def input(self, input_helper, width, height):
#         # Simulating the input actions
#         if input_helper.key_pressed('E'):
#             self.settings_window_open = not self.settings_window_open

#         if input_helper.key_pressed('Space'):
#             self.PAUSED = not self.PAUSED

#         if mx, my := input_helper.mouse():
#             steps = 5.0
#             zoom = (-input_helper.scroll_diff() / steps) ** 2
#             target = np.array([mx * 2.0 - width, height - my * 2.0]) / height
#             self.pos += target * self.scale * (1.0 - zoom)
#             self.scale *= zoom

#         if input_helper.mouse_held(2):
#             mdx, mdy = input_helper.mouse_diff()
#             self.pos[0] -= mdx / height * self.scale * 2.0
#             self.pos[1] += mdy / height * self.scale * 2.0

#         def world_mouse():
#             mx, my = input_helper.mouse() or (0.0, 0.0)
#             mouse = np.array([mx, my], dtype=np.float32)
#             mouse *= 2.0 / height
#             mouse[1] -= 1.0
#             mouse[1] *= -1.0
#             mouse[0] -= width / height
#             return mouse * self.scale + self.pos

#         if input_helper.mouse_pressed(1):
#             mouse = world_mouse()
#             self.spawn_body = Body.new(mouse, np.array([0.0, 0.0]), 1.0, 1.0)
#             self.angle = None
#             self.total = 0.0
#         elif input_helper.mouse_held(1):
#             if self.spawn_body:
#                 mouse = world_mouse()
#                 if self.angle is not None:
#                     d = mouse - self.spawn_body.pos
#                     angle2 = math.atan2(d[1], d[0])
#                     a = angle2 - self.angle
#                     a = (a + math.pi) % (2 * math.pi) - math.pi
#                     total = self.total - a
#                     self.spawn_body.mass = (total / (2 * math.pi)) ** 2
#                     self.angle = angle2
#                     self.total = total
#                 else:
#                     d = mouse - self.spawn_body.pos
#                     self.angle = math.atan2(d[1], d[0])
#                 self.spawn_body.radius = self.spawn_body.mass ** (1/3)
#                 self.spawn_body.vel = mouse - self.spawn_body.pos
#         elif input_helper.mouse_released(1):
#             self.confirmed_bodies = self.spawn_body
#             self.spawn_body = None

#     def render(self, ctx):
#         with self.UPDATE_LOCK:
#             if self.PAUSED:
#                 # Update bodies and quadtree from global state
#                 self.bodies = list(BODIES)  # Assuming BODIES is some shared data structure
#                 self.quadtree = list(QUADTREE)  # Same for QUADTREE

#             if self.confirmed_bodies:
#                 self.bodies.append(self.confirmed_bodies)
#                 SPAWN.append(self.confirmed_bodies)  # Assuming SPAWN is a global list

#         ctx.clear_circles()
#         ctx.clear_lines()
#         ctx.clear_rects()
#         ctx.set_view_pos(self.pos)
#         ctx.set_view_scale(self.scale)

#         if self.bodies:
#             if self.show_bodies:
#                 for body in self.bodies:
#                     ctx.draw_circle(body.pos, body.radius, [255, 255, 255, 255])

#             if self.confirmed_bodies:
#                 ctx.draw_circle(self.confirmed_bodies.pos, self.confirmed_bodies.radius, [255, 255, 255, 255])
#                 ctx.draw_line(self.confirmed_bodies.pos, self.confirmed_bodies.pos + self.confirmed_bodies.vel, [255, 255, 255, 255])

#             if self.spawn_body:
#                 ctx.draw_circle(self.spawn_body.pos, self.spawn_body.radius, [255, 255, 255, 255])
#                 ctx.draw_line(self.spawn_body.pos, self.spawn_body.pos + self.spawn_body.vel, [255, 255, 255, 255])

#         if self.show_quadtree and self.quadtree:
#             depth_range = self.depth_range
#             if depth_range[0] >= depth_range[1]:
#                 stack = deque([(Quadtree.ROOT, 0)])
#                 min_depth, max_depth = float('inf'), 0
#                 while stack:
#                     node, depth = stack.pop()
#                     node = self.quadtree[node]

#                     if node.is_leaf():
#                         min_depth = min(min_depth, depth)
#                         max_depth = max(max_depth, depth)
#                     else:
#                         stack.extend([(child, depth + 1) for child in range(4)])

#                 depth_range = (min_depth, max_depth)

#             stack = deque([(Quadtree.ROOT, 0)])
#             while stack:
#                 node, depth = stack.pop()
#                 node = self.quadtree[node]

#                 if node.is_branch() and depth < depth_range[1]:
#                     stack.extend([(child, depth + 1) for child in range(4)])
#                 elif depth >= depth_range[0]:
#                     quad = node.quad  # Assuming quad is a valid property in the node
#                     half = np.array([0.5, 0.5]) * quad.size
#                     min_pt = quad.center - half
#                     max_pt = quad.center + half

#                     t = (depth - depth_range[0] + (not node.is_empty())) / (depth_range[1] - depth_range[0] + 1)
#                     h = -100.0 + (80.0 - (-100.0)) * t
#                     s, l = 100.0, t * 100.0

#                     # Assume HSL to RGB conversion is done here
#                     color = (h, s, l)  # Placeholder for actual color conversion
#                     ctx.draw_rect(min_pt, max_pt, color)

#     def gui(self, ctx):
#         ctx.window("Settings", open=self.settings_window_open, show=(
#             lambda ui: ui.checkbox("Show Bodies", self.show_bodies) and
#                        ui.checkbox("Show Quadtree", self.show_quadtree) and
#                        (ui.horizontal(lambda ui: ui.label("Depth Range:") and
#                                       ui.add_drag_value(self.depth_range[0]) and
#                                       ui.label("to") and
#                                       ui.add_drag_value(self.depth_range[1])) if self.show_quadtree else None))
