package org.encryfoundation.common.serialization

case class SerializationException(msg: String) extends Exception(s"Serialization failed: $msg")
