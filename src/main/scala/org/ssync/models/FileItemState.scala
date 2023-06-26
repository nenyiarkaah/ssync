package org.ssync.models

object FileItemState extends Enumeration {
  type FileItemState = Value
  val INITIAL, FILE_EXISTS, MOVED, RENAMED = Value
}
