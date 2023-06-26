package org.ssync.models

import better.files.File
import org.ssync.models.FileItemState._

case class FileItem(
                     Item: File,
                     Archive: File,
                     State: FileItemState = INITIAL
                   )
