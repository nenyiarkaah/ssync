package org.ssync.models

object DeleteState extends Enumeration {
  type DeleteState = Value
  val DELETED, NOT_EMPTY, PROTECTED = Value
}
