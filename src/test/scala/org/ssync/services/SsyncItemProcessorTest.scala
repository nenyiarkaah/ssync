package org.ssync.services

import better.files.{File, Resource}
import com.softwaremill.macwire.wire
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers
import org.ssync.models.DeleteState.DELETED
import org.ssync.models.FileItemState._
import org.ssync.models.{DeleteItem, FileItem, SsyncItem}
import org.ssync.models.SsyncItemState._

class SsyncItemProcessorTest extends AnyFlatSpec with Matchers with MockitoSugar {

  val ioCapabilities = mock[IoCapabilities]
  val ssyncItemProcessor = wire[SsyncItemProcessor]

  "processSsyncItem" should "return DOESNOTEXIST when source does not exist" in {
    val source = "/source/sub1"
    val archive = "/archive/sub1"
    val extensions = Seq("jpg")
    val ignoredExtensions = Seq[String]()
    val name1 = "sub 1"

    val ssyncItem = SsyncItem(
      Name = name1,
      Source = source,
      Archive = archive,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions
    )
    val expected = SsyncItem(
      Name = name1,
      Source = source,
      Archive = archive,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      State = DOES_NOT_EXIST
    )
    when(ioCapabilities.doesSourceExist(source)) thenReturn false
    val result = ssyncItemProcessor.processSsyncItem(ssyncItem)
     result shouldBe expected
  }
  it should "return PROCESSED when source exists" in {
    val source = "/source/sub1"
    val archive = "/archive/sub1"
    val extensions = Seq("csv")
    val ignoredExtensions = Seq[String]()
    val name1 = "sub 1"
    val isDirectory = false
    val isDirectory2 = true

    val ssyncItem = SsyncItem(
      Name = name1,
      Source = source,
      Archive = archive,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions
    )
    val files = List(File(s"$source/file1.csv"), File(s"$source/file2.csv"), File(s"$source/file3.csv"))
    val fileItemInitial1 = FileItem(Item = File(s"$source/file1.csv"), Archive = File(s"$archive/file1.csv"), State = INITIAL)
    val fileItemInitial2 = FileItem(Item = File(s"$source/file2.csv"), Archive = File(s"$archive/file2.csv"), State = INITIAL)
    val fileItemInitial3 = FileItem(Item = File(s"$source/file3.csv"), Archive = File(s"$archive/file3.csv"), State = INITIAL)
    val fileItem1 = FileItem(Item = File(s"$source/file1.csv"), Archive = File(s"$archive/file1.csv"), State = MOVED)
    val fileItem2 = FileItem(Item = File(s"$source/file2.csv"), Archive = File(s"$archive/file2.csv"), State = MOVED)
    val fileItem3 = FileItem(Item = File(s"$source/file3.csv"), Archive = File(s"$archive/file3.csv"), State = MOVED)
    val fileItems = List(fileItem1, fileItem2, fileItem3)
    val expected = SsyncItem(
      Name = name1,
      Source = source,
      Archive = archive,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED
    )

    when(ioCapabilities.doesSourceExist(source)) thenReturn true
    when(ioCapabilities.collectFiles(source, isDirectory)) thenReturn files
    when(ioCapabilities.collectFiles(source, isDirectory2)) thenReturn List()
    when(ioCapabilities.moveFileItem(fileItemInitial1)) thenReturn fileItem1
    when(ioCapabilities.moveFileItem(fileItemInitial2)) thenReturn fileItem2
    when(ioCapabilities.moveFileItem(fileItemInitial3)) thenReturn fileItem3

    val result = ssyncItemProcessor.processSsyncItem(ssyncItem)
    result shouldBe expected
  }
  it should "return PROCESSED and filter based on extensions when source exists" in {
    val source = "/source/sub1"
    val archive = "/archive/sub1"
    val extensions = Seq("jpg")
    val ignoredExtensions = Seq[String]()
    val name1 = "sub 1"
    val isDirectory = false

    val ssyncItem = SsyncItem(
      Name = name1,
      Source = source,
      Archive = archive,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions
    )
    val files = List(
      File(s"$source/file1.jpg"),
      File(s"$source/file2.jpg"),
      File(s"$source/file3.jpg"),
      File(s"$source/file4.csv"))
    val fileItemInitial1 = FileItem(Item = File(s"$source/file1.jpg"), Archive = File(s"$archive/file1.jpg"), State = INITIAL)
    val fileItemInitial2 = FileItem(Item = File(s"$source/file2.jpg"), Archive = File(s"$archive/file2.jpg"), State = INITIAL)
    val fileItemInitial3 = FileItem(Item = File(s"$source/file3.jpg"), Archive = File(s"$archive/file3.jpg"), State = INITIAL)
    val fileItem1 = FileItem(Item = File(s"$source/file1.jpg"), Archive = File(s"$archive/file1.jpg"), State = MOVED)
    val fileItem2 = FileItem(Item = File(s"$source/file2.jpg"), Archive = File(s"$archive/file2.jpg"), State = MOVED)
    val fileItem3 = FileItem(Item = File(s"$source/file3.jpg"), Archive = File(s"$archive/file3.jpg"), State = MOVED)
    val fileItems = List(fileItem1, fileItem2, fileItem3)
    val expected = SsyncItem(
      Name = name1,
      Source = source,
      Archive = archive,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED
    )

    when(ioCapabilities.doesSourceExist(source)) thenReturn true
    when(ioCapabilities.collectFiles(source, isDirectory)) thenReturn files
    when(ioCapabilities.moveFileItem(fileItemInitial1)) thenReturn fileItem1
    when(ioCapabilities.moveFileItem(fileItemInitial2)) thenReturn fileItem2
    when(ioCapabilities.moveFileItem(fileItemInitial3)) thenReturn fileItem3
    val result = ssyncItemProcessor.processSsyncItem(ssyncItem)
    result shouldBe expected
  }
  it should "return PROCESSED and filter based on ignored extensions when source exists" in {
    val source = "/source/sub1"
    val archive = "/archive/sub1"
    val extensions = Seq("*")
    val ignoredExtensions = Seq[String]("jpg")
    val name1 = "sub 1"
    val isDirectory = false

    val ssyncItem = SsyncItem(
      Name = name1,
      Source = source,
      Archive = archive,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions
    )
    val files = List(
      File(s"$source/file1.jpg"),
      File(s"$source/file2.jpg"),
      File(s"$source/file3.jpg"),
      File(s"$source/file4.csv"))
    val fileItemInitial = FileItem(Item = File(s"$source/file4.csv"), Archive = File(s"$archive/file4.csv"), State = INITIAL)
    val fileItem = FileItem(Item = File(s"$source/file4.csv"), Archive = File(s"$archive/file4.csv"), State = MOVED)
    val fileItems = List(
      fileItem
    )
    val expected = SsyncItem(
      Name = name1,
      Source = source,
      Archive = archive,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED
    )

    when(ioCapabilities.doesSourceExist(source)) thenReturn true
    when(ioCapabilities.collectFiles(source, isDirectory)) thenReturn files
    when(ioCapabilities.moveFileItem(fileItemInitial)) thenReturn fileItem

    val result = ssyncItemProcessor.processSsyncItem(ssyncItem)
    result shouldBe expected
  }

  "moveSsyncFileItem" should "move FileItem to archive" in {
    val source = "/source/sub1"
    val archive = "/archive/sub1"
    val fileSource = File(s"$source/file1.jpg")
    val fileArchive = File(s"$archive/file1.jpg")
    val expected = FileItem(Item = fileSource, Archive = fileArchive, State = MOVED)
    val fileItem = FileItem(Item = fileSource, Archive = fileArchive)

    when(ioCapabilities.doesSourceExist(source)) thenReturn false
    when(ioCapabilities.moveFileItem(fileItem)) thenReturn expected

    val result = ssyncItemProcessor.moveSsyncFileItem(fileItem)
    result shouldBe expected
  }
  it should "move FileItem to archive when archive already exists" in {
    val source = "/source/sub1"
    val archive = "/archive/sub1"
    val randomString = "kdjhsdkjash"
    val fileSource = File(s"$source/file1.jpg")
    val fileArchive = File(s"$archive/file1.jpg")
    val renamedSourceFile = File(s"$source/file1_$randomString.jpg")
    val renamedArchiveFile = File(s"$archive/file1_$randomString.jpg")
    val expected = FileItem(Item = renamedSourceFile, Archive = renamedArchiveFile, State = MOVED)
    val fileItem = FileItem(Item = fileSource, Archive = fileArchive)
    val fileItemRenamed = FileItem(Item = renamedSourceFile, Archive = renamedArchiveFile, RENAMED)

    when(ioCapabilities.doesSourceExist(source)) thenReturn false
    when(ioCapabilities.doesSourceExist(s"$archive/file1.jpg")) thenReturn true
    when(ioCapabilities.moveFileItem(fileItemRenamed)) thenReturn expected
    when(ioCapabilities.randomString) thenReturn randomString
    when(ioCapabilities.renameFile(fileSource, s"file1_$randomString.jpg")) thenReturn renamedSourceFile

    val result = ssyncItemProcessor.moveSsyncFileItem(fileItem)
    result shouldBe expected
  }

  "cleanSource" should "remove all empty source directories" in {
    val source = "/source/sub1"
    val archive = "/archive/sub1"
    val isDirectory = true
    val ssyncItem = SsyncItem(
      Name = "sub 1",
      Source = source,
      Archive = archive,
      ProtectedDirectories = Seq(),
      Extensions = Seq("*"),
      IgnoredExtensions = Seq(),
      FileItem = Seq(),
      State = PROCESSED
    )

    val innerSub1 = File(s"$source/innersub1")
    val innerSub2 = File(s"$source/innersub2")
    val subdirectories = List(innerSub1, innerSub2)
    val expected: Seq[DeleteItem] = Seq(
      DeleteItem(innerSub1, DELETED),
      DeleteItem(innerSub2, DELETED)
    )

    when(ioCapabilities.collectFiles(source, isDirectory)) thenReturn subdirectories
    when(ioCapabilities.isDirectoryEmpty(innerSub1)) thenReturn true
    when(ioCapabilities.isDirectoryEmpty(innerSub2)) thenReturn true
    when(ioCapabilities.deleteDirectory(innerSub1)) thenReturn innerSub1
    when(ioCapabilities.deleteDirectory(innerSub1)) thenReturn innerSub1

    val result = ssyncItemProcessor.cleanSource(ssyncItem)
    result  should contain theSameElementsAs  expected
  }
}
