package boids

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.window
import org.scalajs.dom.raw._
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.Random
object Main {
  def main(argv: Array[String]) {
    document.addEventListener("DOMContentLoaded", (e: Event) => {
      draw()
    })
  }

  def getCtx(): CanvasRenderingContext2D = {
    val canvas =
      document.querySelector("canvas#main").asInstanceOf[HTMLCanvasElement]
    canvas.setAttribute("width", "500")//canvas.offsetWidth.toString())
    canvas.setAttribute("height", "500")//canvas.offsetHeight.toString())
    canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  }

  def draw() {
    val ctx = getCtx()
    val anim = new Animation(ctx)
    anim.setup()
    window.requestAnimationFrame(anim.loop)
  }

  def testBoidSpin(ctx: CanvasRenderingContext2D) {
    val b = Boid(20.0)
    b.p = Vec2(10.0, 10.0)
    b.v.x = 1.0
    b.v.y = 1.0
    b.render(ctx)
    //for ( i <- 0.to(100) ) {
    //  b.v.y = (-1) + (2.0 / 100.0) * i.toDouble
    //  b.render(ctx)
    //}
  }

}
object Vec2 {
  def apply(x: Double, y: Double) = new Vec2(x, y)
}
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
  val MULT_CAVOID = 0.002
  val MULT_VMATCH = 0.03
  val THR_CAVOID = 100.0
  val THR_VMATCH = 150.0
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
    ctx.stroke()
  }
  
  def step(elapsed: Double, others: IndexedSeq[Boid]): Boid = {
    val nb = Boid(size)
    println(others.length)
    val gr = others.groupBy((o: Boid) => {
      val d = p.dist(o.p)
      println(s"$id<-${d}->${o.id}")
      if (d < Boid.THR_CAVOID) 0
      else if (d < Boid.THR_VMATCH) 1
      else 2
    })
    val cAvoid = gr.get(0) match {
      case Some(arr) => arr.map((b: Boid) =>
          p.diff(b.p)/(Math.pow(p.dist(b.p), 2)))
      case None => Nil 
    }
    val vMatch = gr.get(1) match {
      case Some(arr) => arr.map((b: Boid) => b.v / p.dist(b.p)) 
      case None => Nil
    }
    nb.p = p + v * elapsed
    println(cAvoid)
    println(s"cavoid diffs ${cAvoid.foldLeft(Vec2(.0, .0))(_ + _)}" + 
      s" vmatch diffs ${vMatch.fold(Vec2(.0, .0))(_ + _)}") 
    nb.v = (cAvoid.foldLeft(Vec2(.0, .0))(_ + _) * Boid.MULT_CAVOID +
      vMatch.fold(Vec2(.0, .0))(_ + _) * Boid.MULT_VMATCH) * elapsed + 
      v
    nb.v = (nb.v / nb.v.mod) * 0.1
    nb
  }

}
