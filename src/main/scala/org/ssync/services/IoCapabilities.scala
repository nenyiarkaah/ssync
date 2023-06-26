package org.ssync.services

import better.files.File
import org.ssync.models.FileItem
import org.ssync.models.FileItemState.MOVED

import java.util.UUID

class IoCapabilities {
  def moveFileItem(fileItem: FileItem): FileItem = {
    val source = fileItem.Item
    val archive = fileItem.Archive
    archive.parent.createDirectoryIfNotExists(createParents = true)
    source.moveTo(archive)
    FileItem(Item = fileItem.Item, Archive = fileItem.Archive, State = MOVED)
  }

  def doesSourceExist(path: String): Boolean = {
    val source = File(path)
    source.exists
  }

  def collectFiles(path: String, isDirectory: Boolean): List[File] = {
    val directory = File(path)
    directory
      .listRecursively.filter(_.isDirectory.equals(isDirectory)).toList
  }

  def randomString: String = {
    val begining = 0
    val end = 4
    val randomString = UUID.randomUUID.toString.substring(begining, end)
    randomString
  }

  def renameFile(file: File, renamedFileName: String): File = {
    file.renameTo(renamedFileName)
  }

  def deleteDirectory(file: File): File = {
    file.delete()
  }

  def isDirectoryEmpty(file: File): Boolean = {
    file.isEmpty
  }
}
