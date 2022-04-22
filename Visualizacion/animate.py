from audioop import add
from ovito.io import import_file, export_file
from ovito.modifiers import AffineTransformationModifier
from ovito.vis import Viewport, PythonViewportOverlay, TextLabelOverlay
import math
from ovito.qt_compat import QtCore

offset_top = 164
pixels_height = 273 #vale solo para 0.09 de alto

static_file = open('static_input.txt', 'r')

radius = float(static_file.readline())
mass = float(static_file.readline())
width = float(static_file.readline())
height = float(static_file.readline())
groove = float(static_file.readline())
cant = int(static_file.readline())

def add_vertical_line(vp):
    def render_vertical_line(args: PythonViewportOverlay.Arguments):
        args.painter.drawLine(400, offset_top, 400, offset_top + pixels_height)
    vp.overlays.append(PythonViewportOverlay(function = render_vertical_line))

    
def add_groove(vp, groove):
    groove_pixels = (0.02 * pixels_height) / 0.09
    def render_groove(args: PythonViewportOverlay.Arguments):
        args.painter.setPen(QtCore.Qt.white)
        start = offset_top + (pixels_height / 2) - (groove_pixels / 2)
        end = offset_top + (pixels_height / 2) + (groove_pixels / 2)
        args.painter.drawLine(400, start, 400, end)
    vp.overlays.append(PythonViewportOverlay(function = render_groove))
    


    


pipeline = import_file('dynamic_output.txt', columns = ["Position.X", "Position.Y", "Velocity.X", "Velocity.Y", "Radius", "Mass"])

pipeline.modifiers.append(AffineTransformationModifier(
    operate_on = {'cell', 'dislocations', 'voxels', 'surfaces'},
    relative_mode = False,
    target_cell = [[0.24, 0, 0, 0], [0, 0.09, 0, 0], [0, 0, 0, 0]]
))

pipeline.add_to_scene()
vp = Viewport(type = Viewport.Type.Top)
vp.zoom_all()


add_vertical_line(vp)
add_groove(vp, groove)

vp.render_anim(size=(800,600), filename="animation.avi", fps=200, every_nth=1)
#vp.render_image(size=(800,600), filename="image.png")


