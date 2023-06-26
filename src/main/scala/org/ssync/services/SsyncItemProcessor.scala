package org.ssync.services

import better.files.File
import com.typesafe.scalalogging.LazyLogging
import org.ssync.models.FileItemState._
import org.ssync.models.{DeleteItem, FileItem, SsyncItem}
import org.ssync.models.SsyncItemState._
import org.ssync.models.DeleteState._

class SsyncItemProcessor(ioCapabilities: IoCapabilities) extends LazyLogging {

  def cleanSource(ssyncItem: SsyncItem): Seq[DeleteItem] = {
    val source = ssyncItem.Source
    val protectedDirectories = ssyncItem.ProtectedDirectories
    val isDirectory = true
    val subDirectories = ioCapabilities.collectFiles(source, isDirectory)
      .sorted(File.Order.byDepth).reverse
    //    try {
    subDirectories.map { sub =>
      if (isDirectoryEmpty(sub)) {
        if(protectedDirectories.contains(sub.name)) {
          DeleteItem(sub, PROTECTED)
        } else {
          deleteDirectory(sub)
          DeleteItem(sub, DELETED)
        }
      } else {
        DeleteItem(sub, NOT_EMPTY)
      }
    }
    //    }
  }

  private def deleteDirectory(directory: File) = {
    ioCapabilities.deleteDirectory(directory)
  }

  private def isDirectoryEmpty(directory: File) = {
    ioCapabilities.isDirectoryEmpty(directory)
  }

  def processSsyncItem(ssyncItem: SsyncItem):SsyncItem = {
    val source = ssyncItem.Source

    ioCapabilities.doesSourceExist(source) match {
      case true =>
        val extensions = ssyncItem.Extensions
        val ignoredExtensions = ssyncItem.IgnoredExtensions
        val isDirectory = false
        val files = ioCapabilities.collectFiles(source, isDirectory)
          .filter(filterFilesBasedOnExtensions(_, extensions, extensions.contains("*")))
          .filterNot(filterFilesBasedOnExtensions(_, ignoredExtensions, extensions.isEmpty))

        val ssyncFileItem = files.map { file =>
          constructSsyncItemFileWithArchive(ssyncItem, file)
        }
        val movedSsyncFileItems = ssyncFileItem.map { item =>
          moveSsyncFileItem(item)
        }

        //clean source
        val cleanedSource = cleanSource(ssyncItem)
        SsyncItem(
          Name = ssyncItem.Name,
          Source = ssyncItem.Source,
          Archive = ssyncItem.Archive,
          ProtectedDirectories = ssyncItem.ProtectedDirectories,
          Extensions = ssyncItem.Extensions,
          IgnoredExtensions = ssyncItem.IgnoredExtensions,
          FileItem = movedSsyncFileItems,
          State = PROCESSED
        )
      case false =>
        SsyncItem(
          Name = ssyncItem.Name,
          Source = ssyncItem.Source,
          Archive = ssyncItem.Archive,
          ProtectedDirectories = ssyncItem.ProtectedDirectories,
          Extensions = ssyncItem.Extensions,
          IgnoredExtensions = ssyncItem.IgnoredExtensions,
          State = DOES_NOT_EXIST
        )
    }
  }

  def moveSsyncFileItem(fileItem: FileItem): FileItem = {
    ioCapabilities.doesSourceExist(fileItem.Archive.pathAsString) match {
      case false =>
        ioCapabilities.moveFileItem(fileItem)
      case true =>
        val newFileItem = renameFileBecauseItAlreadyExists(fileItem)
        moveSsyncFileItem(newFileItem)
    }
  }

  def renameFileBecauseItAlreadyExists(fileItem: FileItem): FileItem = {
    val file = fileItem.Item
    val archive = fileItem.Archive
    val fileName = file.name
    val name = file.nameWithoutExtension
    val extension = file.extension(true).get
    val random = randomString
    val renamedFileName = name + "_" + random + extension
    val renamedFile = ioCapabilities.renameFile(file, renamedFileName)
    val renamedArchiveFile = constructArchiveFromSource(renamedFile, renamedFile.parent.pathAsString, archive.parent.pathAsString)
    logger.info(s"Renamed file from $fileName to $renamedFileName")
    FileItem(Item = renamedFile, Archive = renamedArchiveFile, State = RENAMED)
  }

  def constructSsyncItemFileWithArchive(ssyncItem: SsyncItem, file: File): FileItem = {
    val source = ssyncItem.Source
    val archive = ssyncItem.Archive
    val archiveFile = constructArchiveFromSource(file, source, archive)
    FileItem(Item = file, Archive = archiveFile, State = INITIAL)
  }

  private def constructArchiveFromSource(file: File, source: String, archive: String) = {
    File(file.canonicalPath.replace(source, archive))
  }

  private def filterFilesBasedOnExtensions(file: File, extensions: Seq[String], predicate: Boolean): Boolean = {
    predicate match {
      case true => true
      case false =>
        extensions.contains(file.extension(false, false, true).get)
    }
  }

  private def randomString = ioCapabilities.randomString
}
