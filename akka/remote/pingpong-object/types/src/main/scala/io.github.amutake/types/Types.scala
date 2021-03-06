package io.github.amutake.types

sealed trait Msg
case object Start extends Msg
case class Ping(msg: String, n: Int) extends Msg
case class Pong(msg: String, n: Int) extends Msg
