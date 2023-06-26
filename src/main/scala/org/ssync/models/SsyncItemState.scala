package org.ssync.models

object SsyncItemState extends Enumeration {
  type SsyncItemState = Value
  val PRE_PROCESSED, DOES_NOT_EXIST, ARCHIVED, CLEANED_SOURCE, PROCESSED = Value
}
