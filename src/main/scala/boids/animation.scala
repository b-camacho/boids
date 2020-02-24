package boids

import org.scalajs.dom.raw.CanvasRenderingContext2D
import org.scalajs.dom.window
import scala.util.Random

class Animation(ctx: CanvasRenderingContext2D) {
  var boids = IndexedSeq[Boid]()
  var prevTime = 0.0
  def setup() = {
    //boids = IndexedSeq[Boid](
    //  Boid(20, Vec2(100, 100), Vec2(0.1, 0)),
    //  Boid(20, Vec2(200, 120), Vec2(-0.1, 0))
    //)

    boids = (1 to 30)
      .map(i => {
        val b = Boid(20)
        b.p = Vec2(
          Random.nextDouble() * 500,
          Random.nextDouble() * 500
        )
        b.v = Vec2(
          (Random.nextDouble() - 0.5) * 0.05,
          (Random.nextDouble() - 0.5) * 0.05
        )
        b
      })
  }

  def loop(time: Double): Unit = {
    println(boids.map(b => (b.p, b.v)))
    val elapsed = time - prevTime
    ctx.canvas.width = ctx.canvas.width
    for (b <- boids) {
      b.render(ctx)
    }
    var left = IndexedSeq[Boid]()
    var right = boids.tail
    var newBoids = IndexedSeq[Boid]()
    boids = boids
      .map(b => b.step(elapsed, boids.diff(IndexedSeq(b))))
      .map(b => torusPlane(ctx, b))
    //for (i <- 0 until boids.length) {
    //  newBoids = newBoids :+ torusPlane(
    //    ctx, boids(i).step(elapsed, left ++ boids)
    //  )
    //  if (i < boids.length - 1) {
    //    left = left :+ boids(i)
    //    right = right.tail
    //  }
    //}
    //boids = newBoids

    prevTime = time
    if (time < 20000)
      window.setTimeout(() => window.requestAnimationFrame(loop), 1)

  }

  def torusPlane(ctx: CanvasRenderingContext2D, b: Boid): Boid = {
    if (b.p.x < 0) b.p.x = ctx.canvas.offsetWidth
    if (b.p.y < 0) b.p.y = ctx.canvas.offsetHeight
    if (b.p.x > ctx.canvas.offsetWidth) b.p.x = 0
    if (b.p.y > ctx.canvas.offsetHeight) b.p.y = 0
    return b
  }

}
