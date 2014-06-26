package io.github.amutake.types

sealed trait Msg
case object Start extends Msg
case object Ping extends Msg
case object Pong extends Msg
