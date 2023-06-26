package org.ssync.models

import org.ssync.models.SsyncItemState.{PRE_PROCESSED, SsyncItemState}

final case class SsyncItem(
                      Name: String,
                      Source: String,
                      Archive: String,
                      ProtectedDirectories: Seq[String],
                      Extensions: Seq[String],
                      IgnoredExtensions: Seq[String],
                      FileItem: Seq[FileItem] = Seq(),
                      State: SsyncItemState = PRE_PROCESSED
                    )
