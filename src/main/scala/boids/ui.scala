package UI

import org.scalajs.dom.raw.{Event, HTMLInputElement}
import org.scalajs.dom.document
import DeserializerImpls._
object UI {
  val multCAvoid = Component("mult-cavoid", 0.01)
  val multVMatch = Component("mult-vmatch", 0.008)
  val multLCohes = Component("mult-lcohes", 0.00001)
  val thrCAvoid = Component("thr-cavoid", 5.0)
  val thrVMatch = Component("thr-vmatch", 150.0)
  val thrLCohes = Component("thr-lcohes", 150.0)

  def init() = {
    multCAvoid.attach
    multVMatch.attach
    multLCohes.attach
    thrCAvoid.attach
    thrVMatch.attach
    thrLCohes.attach
  }
}

class Component[T](val bindsTo: HTMLInputElement, var value: T) {
  def attach(implicit deserializer: Deserializer[T]): Unit = {
    bindsTo.addEventListener("input", domUpdate)
    bindsTo.value = value.toString()
  }
  def domUpdate(e: Event)(implicit deserializer: Deserializer[T]): Unit = {
    val serializedValue = e.target.asInstanceOf[HTMLInputElement].value
    
    value = serializedValue match {
      case "" => value
      case v => deserializer.deserialize(serializedValue)
    }
    println(f"new: $value")
  }

  def update(newValue: T): Unit = {
    value = newValue
    bindsTo.value = value.toString()
  }

  def get: T = value

}

trait Deserializer[T] {
  def deserialize(in: String): T
}

object DeserializerImpls {
  implicit val intDeserializer: Deserializer[Int] = {
    new Deserializer[Int] {
      override def deserialize(in: String): Int = in.toInt
    }
  }

  implicit val doubleDeserializer: Deserializer[Double] = {
    new Deserializer[Double] {
      override def deserialize(in: String): Double = in.toDouble
    }
  }
}

object Component {
  // serialiser assumed as `T.toString`
  def apply[T](id: String, default: T): Component[T] = {
    val input = document.querySelector(f"#$id").asInstanceOf[HTMLInputElement]
    val comp = new Component[T](input, default)

    return comp
  }

}


