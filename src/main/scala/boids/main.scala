package boids

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.window
import org.scalajs.dom.raw._
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.Random

import UI._

object Main {
  def main(argv: Array[String]) {
    document.addEventListener("DOMContentLoaded", (e: Event) => {
      UI.init()
      draw()
      //bench(10 :: 50 :: 100 :: 300 :: 600 :: 1000 :: Nil)
    })
  }
  def makeCanvas(): CanvasRenderingContext2D = {
    val canvas = document.createElement("canvas").asInstanceOf[HTMLCanvasElement]
    canvas.setAttribute("width", window.innerWidth.toString())
    canvas.setAttribute("height", window.innerHeight.toString())
    canvas.setAttribute("id", "main")
    document.body.appendChild(canvas)
    canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  }
  def destroyCanvasIfExists(): Unit = {
    val canvas = document.querySelector("canvas")
    canvas.parentNode.removeChild(canvas)
  }
  def resizeCanvas(ctx: CanvasRenderingContext2D)(e: Event) = {
    ctx.canvas.width = window.innerWidth.toInt
    ctx.canvas.height = window.innerHeight.toInt
  }

  def draw() {
    val ctx = makeCanvas()
    val anim = new Animation(ctx, 0, 300)
    anim.setup(window.performance.now())
    window.requestAnimationFrame(anim.loop)
    window.addEventListener("resize", resizeCanvas(ctx))
  }
  def bench(paramList: List[Int]): Unit = {
    paramList match {
      case Nil => Unit
      case p :: ps => {
        val anim = new Animation(makeCanvas(), 2000, p)
        anim.setup(window.performance.now())
        window.requestAnimationFrame(anim.loop)
        window.setTimeout(() => {
          destroyCanvasIfExists()
          bench(ps)
        }, 2200)
      }
   }
  }

}
object Vec2 {
  def apply(x: Double, y: Double) = new Vec2(x, y)
}
// Vec2 defines common operations on a 2d vector
// Boid speed and position are expressed as Vec2
class Vec2(var x: Double, var y: Double) {
  override def toString(): String = s"$x, $y"
  def rot: Double = Math.atan2(x, y)
  def diff(o: Vec2): Vec2 = Vec2(x - o.x, y - o.y)
  def mod: Double = Math.sqrt(x*x + y*y)
  def dist(o: Vec2): Double = (diff(o)).mod
  def +(o: Vec2) = Vec2(x + o.x, y + o.y)
  def +(scalar: Double) = Vec2(x + scalar, y + scalar)
  def /(o: Vec2) = Vec2(x / o.x, y / o.y)
  def /(scalar: Double) = Vec2(x / scalar, y / scalar)
  def -(o: Vec2) = Vec2(x - o.x, y - o.y)
  def -(scalar: Double) = Vec2(x - scalar, y - scalar)
  def *(o: Vec2) = Vec2(x * o.x, y * o.y)
  def *(scalar: Double) = Vec2(x * scalar, y * scalar)
}
object Boid {
  var bIds = IndexedSeq[Int]()
  def apply(size: Double,
    pos: Vec2 = Vec2(0.0, 0.0),
    vel: Vec2 = Vec2(0.0, 0.0)): Boid = {
      var b = new Boid(size, if(Boid.bIds.isEmpty) 0 else bIds.last + 1)
      b.p = pos
      b.v = vel
      b
  }
  def MULT_CAVOID = UI.multCAvoid.get
  def MULT_VMATCH = UI.multVMatch.get
  def MULT_LCOHES = UI.multLCohes.get
  def THR_CAVOID = UI.thrCAvoid.get
  def THR_VMATCH = UI.thrVMatch.get
  def THR_LCOHES = UI.thrLCohes.get
  val SPEED = 0.15

  val COLOR = "#97b3f4"

}
class Boid(val size: Double, val id: Int) {
  var p, v = Vec2(.0, .0)
  def getRotation(): Double = {
    if (Math.abs(v.x) <= 0.001 && Math.abs(v.y) <= 0.001) 0.0
    else -Math.atan2(v.x, v.y)
  }
  def render(ctx: CanvasRenderingContext2D) {
    ctx.save()
    ctx.translate(p.x, p.y)
    ctx.rotate(getRotation())
    ctx.translate(-size / 2, -size / 3)
    drawBody(ctx)
    ctx.restore()
  }
  def drawBody(ctx: CanvasRenderingContext2D) {
    ctx.beginPath()
    ctx.lineWidth = 1.0
    ctx.moveTo(0, 0)
    ctx.lineTo(size, 0)
    ctx.lineTo(size / 2.0, (size * Math.sqrt(3.0) / 2.0))
    ctx.lineTo(0, 0)
    ctx.moveTo(size / 2.0, 0)
    ctx.lineTo(size / 2.0, (size * Math.sqrt(3.0) / 2.0))
    ctx.closePath()
    ctx.strokeStyle = Boid.COLOR
    ctx.stroke()
  }
  
  def step(elapsed: Double, others: Iterable[Boid]): Boid = {
    val nb = Boid(size)
    var cAvoid = Vec2(.0, .0)
    var vMatch = Vec2(.0, .0)
    var lCohes = Vec2(.0, .0)
    var cohesCnt = 0
    val gr = others.foreach((o: Boid) => {
      val d = p.dist(o.p)
      if (d < Boid.THR_CAVOID * size) {
        cAvoid = cAvoid + p.diff(o.p) / (d*d)
      }
      if (d < Boid.THR_VMATCH) {
        vMatch = vMatch + o.v / d
      }
      if (d < Boid.THR_LCOHES) {
        lCohes = lCohes + o.p
        cohesCnt += 1
      }
    })
    lCohes = if(cohesCnt > 0) (lCohes / cohesCnt) - p else Vec2(.0, .0)

    nb.p = p + v * elapsed
    nb.v = (
      cAvoid * Boid.MULT_CAVOID +
      vMatch * Boid.MULT_VMATCH + 
      lCohes * Boid.MULT_LCOHES) * elapsed + 
      v
    nb.v = (nb.v / nb.v.mod) * Boid.SPEED
    nb
  }

}
