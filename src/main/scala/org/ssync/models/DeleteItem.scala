package org.ssync.models

import better.files.File
import org.ssync.models.DeleteState.DeleteState

case class DeleteItem(
                       Item: File,
                       State: DeleteState
                     )
