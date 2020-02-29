package boids

import org.scalajs.dom.raw.CanvasRenderingContext2D
import org.scalajs.dom.window
import scala.util.Random

class Animation(ctx: CanvasRenderingContext2D,
  runUntil: Double,
  numBoids: Int) {
  var boids = List[Boid]()
  var prevTime = 0.0
  var frameCnt = 0
  var startTime = 0.0

  def setup(time: Double) = {
    boids = (1 to numBoids)
      .map(i => {
        val b = Boid(12)
        b.p = Vec2(
          Random.nextDouble() * ctx.canvas.height,
          Random.nextDouble() * ctx.canvas.width,
        )
        b.v = Vec2(
          (Random.nextDouble() - 0.5) * 0.05,
          (Random.nextDouble() - 0.5) * 0.05
        )
        b
      }).toList
      startTime = time
      prevTime = time
  }

  def oneVsRest[A](left:List[A], right: List[A]): List[(A, Iterable[A])] = {
    right match {
      case Nil => Nil
      case x :: xs => (x, Iterable.concat(left, xs)) :: oneVsRest(x :: left, xs)
    }
  }
  def loop(time: Double): Unit = {
    //println(boids.map(b => (b.p, b.v)))
    val elapsed = time - prevTime
    ctx.canvas.width = ctx.canvas.width
    for (b <- boids) {
      b.render(ctx)
    }

    boids = oneVsRest(List(), boids.toList)
      .map((t: (Boid, Iterable[Boid])) => t._1.step(elapsed, t._2))
      .map((b: Boid) => torusPlane(ctx, b))
    //boids = boids
    //  .map(b => b.step(elapsed, boids.diff(IndexedSeq(b))))
    //  .map(b => torusPlane(ctx, b))

    prevTime = time
    if (time - startTime < runUntil) {
      frameCnt += 1
      window.requestAnimationFrame(loop)
    }
    else {
      reportPerf(time) 
    }

  }

  def reportPerf(timestamp: Double):Unit = {
    println(s"fps: ${1000*frameCnt.toDouble/timestamp}" + 
      s"| runtime: $timestamp" + 
      s"| frameCnt: $frameCnt" +
      s"| Params(numBoids: $numBoids)")
  }

  def torusPlane(ctx: CanvasRenderingContext2D, b: Boid): Boid = {
    if (b.p.x < 0) b.p.x = ctx.canvas.offsetWidth
    if (b.p.y < 0) b.p.y = ctx.canvas.offsetHeight
    if (b.p.x > ctx.canvas.offsetWidth) b.p.x = 0
    if (b.p.y > ctx.canvas.offsetHeight) b.p.y = 0
    return b
  }

}
