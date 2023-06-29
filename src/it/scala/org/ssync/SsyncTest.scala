package org.ssync

import better.files.{File, Resource}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.ssync.configs.{Item, Settings}
import org.ssync.models.SsyncItemState._
import org.ssync.models.{FileItem, SsyncItem}
import org.ssync.services.{ConfigConversions, IoCapabilities, SsyncItemProcessor}
import com.softwaremill.macwire.wire
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach}
import org.ssync.models.FileItemState._

class Ssync2Test extends AnyFlatSpec with Matchers with BeforeAndAfter with BeforeAndAfterEach {

  val sourcePath = Resource.getUrl("source")
  val sourceCopyPath = s"$sourcePath" + "_copy"
  val source = File(sourcePath)
  val sourceCopy = File(sourceCopyPath)

  val archivePath = Resource.getUrl("archive")
  val archiveCopyPath = s"$archivePath" + "_copy"
  val archive = File(archivePath)
  val archiveCopy = File(archiveCopyPath)

  val ioCapabilities = wire[IoCapabilities]
  val configConversions = wire[ConfigConversions]
  val ssyncItemProcessor = wire[SsyncItemProcessor]

  override def beforeEach(): Unit = {
    source.copyTo(sourceCopy, true)
    archive.copyTo(archiveCopy, true)
  }

  override def afterEach(): Unit = {
    sourceCopy.delete(false)
    archiveCopy.delete(false)
  }

  "Ssync" should "move all files from source-sub 1 to destination-sub 1" in {
    val name1 = "sub directory 1"
    val sourceSub1Path = s"$sourceCopyPath/sub 1"
    val archiveSub1Path = s"$archiveCopyPath/sub 1"
    val extensions = Seq("jpg")
    val ignoredExtensions = Seq[String]()
    val items = List(
      Item(Name = "sub directory 1", Path = "sub 1", ProtectedDirectories = Seq())
    )
    val settings = Settings(
      Source = sourceCopyPath,
      Archive = archiveCopyPath,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )

    val fileItems = List(
      FileItem(Item = File(s"$sourceSub1Path/firstj.jpg"), Archive = File(s"$archiveSub1Path/firstj.jpg"), State = MOVED)
    )
    val expected = SsyncItem(
      Name = name1,
      Source = sourceSub1Path,
      Archive = archiveSub1Path,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED
    )

    val expectedRemainingFiles = Seq(File(s"$sourceSub1Path/first.txt"))
    val ssyncItems = configConversions.convertSettingItemsToSyncItems(settings)
    val result = ssyncItems.map { i =>
      ssyncItemProcessor.processSsyncItem(i)
    }.head
    result.Name shouldBe expected.Name
    result.Source shouldBe expected.Source
    result.Archive shouldBe expected.Archive
    result.ProtectedDirectories shouldBe expected.ProtectedDirectories
    result.Extensions shouldBe expected.Extensions
    result.IgnoredExtensions shouldBe expected.IgnoredExtensions
    result.FileItem should contain theSameElementsAs expected.FileItem
    result.State shouldBe expected.State

    val remainingFiles = collectFiles(sourceSub1Path)
    remainingFiles shouldBe expectedRemainingFiles
  }
  it should "move all files from source-sub 2 to destination-sub 2" in {
    val name1 = "sub directory 2"
    val sub2 = "sub 2"
    val sourceSub2Path = s"$sourceCopyPath/$sub2"
    val archiveSub2Path = s"$archiveCopyPath/$sub2"
    val extensions = Seq("*")
    val ignoredExtensions = Seq[String]()
    val sourceSub2 = File(sourceSub2Path)

    val items = List(
      Item(Name = "sub directory 2", Path = sub2, ProtectedDirectories = Seq())
    )
    val settings = Settings(
      Source = sourceCopyPath,
      Archive = archiveCopyPath,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )
    val fileItems = Seq(
      FileItem(Item = File(s"$sourceSub2Path/first.txt"), Archive = File(s"$archiveSub2Path/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub2Path/second.TXT"), Archive = File(s"$archiveSub2Path/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub2Path/test.jpg"), Archive = File(s"$archiveSub2Path/test.jpg"), State = MOVED)
    )
    val expected = SsyncItem(
      Name = name1,
      Source = sourceSub2Path,
      Archive = archiveSub2Path,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED

    )
    val expectedRemainingFiles = Seq()
    val ssyncItems = configConversions.convertSettingItemsToSyncItems(settings)
    val result = ssyncItems.map { i =>
      ssyncItemProcessor.processSsyncItem(i)
    }.head
    result.Name shouldBe expected.Name
    result.Source shouldBe expected.Source
    result.Archive shouldBe expected.Archive
    result.ProtectedDirectories shouldBe expected.ProtectedDirectories
    result.Extensions shouldBe expected.Extensions
    result.IgnoredExtensions shouldBe expected.IgnoredExtensions
    result.FileItem should contain theSameElementsAs expected.FileItem
    result.State shouldBe expected.State

    val remainingFiles = collectFiles(sourceSub2Path)
    remainingFiles shouldBe expectedRemainingFiles
  }
  it should "move all files from source-sub 3 to destination-sub 3" in {
    val name1 = "sub directory 3"
    val sub3 = "sub 3"
    val sourceSub3Path = s"$sourceCopyPath/$sub3"
    val archiveSub3Path = s"$archiveCopyPath/$sub3"
    val extensions = Seq("*")
    val ignoredExtensions = Seq[String]()
    val sourceSub3 = File(sourceSub3Path)

    val items = List(
      Item(Name = "sub directory 3", Path = sub3, ProtectedDirectories = Seq())
    )
    val settings = Settings(
      Source = sourceCopyPath,
      Archive = archiveCopyPath,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )
    val fileItems = Seq(
      FileItem(Item = File(s"$sourceSub3Path/first.txt"), Archive = File(s"$archiveSub3Path/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/second.TXT"), Archive = File(s"$archiveSub3Path/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/test.jpg"), Archive = File(s"$archiveSub3Path/test.jpg"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/sub/first.txt"), Archive = File(s"$archiveSub3Path/sub/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/sub/second.TXT"), Archive = File(s"$archiveSub3Path/sub/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/sub/test.jpg"), Archive = File(s"$archiveSub3Path/sub/test.jpg"), State = MOVED)
    )
    val expected = SsyncItem(
      Name = name1,
      Source = sourceSub3Path,
      Archive = archiveSub3Path,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED

    )
    val expectedRemainingFiles = Seq()
    val ssyncItems = configConversions.convertSettingItemsToSyncItems(settings)
    val result = ssyncItems.map { i =>
      ssyncItemProcessor.processSsyncItem(i)
    }.head
    result.Name shouldBe expected.Name
    result.Source shouldBe expected.Source
    result.Archive shouldBe expected.Archive
    result.ProtectedDirectories shouldBe expected.ProtectedDirectories
    result.Extensions shouldBe expected.Extensions
    result.IgnoredExtensions shouldBe expected.IgnoredExtensions
    result.FileItem should contain theSameElementsAs expected.FileItem
    result.State shouldBe expected.State

    val remainingFiles = collectFiles(sourceSub3Path)
    remainingFiles shouldBe expectedRemainingFiles
  }
  it should "move all files from source-sub 4 to destination-sub 4" in {
    val name1 = "sub directory 4"
    val sub4 = "sub 4"
    val sourceSub4Path = s"$sourceCopyPath/$sub4"
    val archiveSub4Path = s"$archiveCopyPath/$sub4"
    val extensions = Seq("*")
    val ignoredExtensions = Seq[String]()
    val sourceSub4 = File(sourceSub4Path)

    val items = List(
      Item(Name = "sub directory 4", Path = sub4, ProtectedDirectories = Seq())
    )
    val settings = Settings(
      Source = sourceCopyPath,
      Archive = archiveCopyPath,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )
    val fileItems = Seq(
      FileItem(Item = File(s"$sourceSub4Path/first.txt"), Archive = File(s"$archiveSub4Path/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/second.TXT"), Archive = File(s"$archiveSub4Path/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/test.jpg"), Archive = File(s"$archiveSub4Path/test.jpg"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub/first.txt"), Archive = File(s"$archiveSub4Path/sub/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub/second.TXT"), Archive = File(s"$archiveSub4Path/sub/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub/test.jpg"), Archive = File(s"$archiveSub4Path/sub/test.jpg"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub/sub 1/first.txt"), Archive = File(s"$archiveSub4Path/sub/sub 1/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub/sub 1/second.TXT"), Archive = File(s"$archiveSub4Path/sub/sub 1/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub/sub 1/test.jpg"), Archive = File(s"$archiveSub4Path/sub/sub 1/test.jpg"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub 1/first.txt"), Archive = File(s"$archiveSub4Path/sub 1/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub 1/second.TXT"), Archive = File(s"$archiveSub4Path/sub 1/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub 1/test.jpg"), Archive = File(s"$archiveSub4Path/sub 1/test.jpg"), State = MOVED),
    )
    val expected = SsyncItem(
      Name = name1,
      Source = sourceSub4Path,
      Archive = archiveSub4Path,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED

    )
    val expectedRemainingFiles = Seq()
    val ssyncItems = configConversions.convertSettingItemsToSyncItems(settings)
    val result = ssyncItems.map { i =>
      ssyncItemProcessor.processSsyncItem(i)
    }.head
    result.Name shouldBe expected.Name
    result.Source shouldBe expected.Source
    result.Archive shouldBe expected.Archive
    result.ProtectedDirectories shouldBe expected.ProtectedDirectories
    result.Extensions shouldBe expected.Extensions
    result.IgnoredExtensions shouldBe expected.IgnoredExtensions
    result.FileItem should contain theSameElementsAs expected.FileItem
    result.State shouldBe expected.State

    val remainingFiles = collectFiles(sourceSub4Path)
    remainingFiles shouldBe expectedRemainingFiles
  }
  it should "move all files from source-sub 5 to destination-sub 5" in {
    val name1 = "sub directory 5"
    val sub5 = "sub 5"
    val sourceSub5Path = s"$sourceCopyPath/$sub5"
    val archiveSub5Path = s"$archiveCopyPath/$sub5"
    val extensions = Seq("*")
    val ignoredExtensions = Seq[String]()
    val sourceSub5 = File(sourceSub5Path)

    val items = List(
      Item(Name = "sub directory 5", Path = sub5, ProtectedDirectories = Seq())
    )
    val settings = Settings(
      Source = sourceCopyPath,
      Archive = archiveCopyPath,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )
    val fileItems = Seq(
      FileItem(Item = File(s"$sourceSub5Path/first.txt"), Archive = File(s"$archiveSub5Path/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub5Path/second.TXT"), Archive = File(s"$archiveSub5Path/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub5Path/test.jpg"), Archive = File(s"$archiveSub5Path/test.jpg"), State = MOVED),
      FileItem(Item = File(s"$sourceSub5Path/sub/first.txt"), Archive = File(s"$archiveSub5Path/sub/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub5Path/sub/second.TXT"), Archive = File(s"$archiveSub5Path/sub/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub5Path/sub/test.jpg"), Archive = File(s"$archiveSub5Path/sub/test.jpg"), State = MOVED),
      FileItem(Item = File(s"$sourceSub5Path/sub/sub 1/first.txt"), Archive = File(s"$archiveSub5Path/sub/sub 1/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub5Path/sub/sub 1/second.TXT"), Archive = File(s"$archiveSub5Path/sub/sub 1/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub5Path/sub/sub 1/test.jpg"), Archive = File(s"$archiveSub5Path/sub/sub 1/test.jpg"), State = MOVED),
      FileItem(Item = File(s"$sourceSub5Path/sub 1/first.txt"), Archive = File(s"$archiveSub5Path/sub 1/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub5Path/sub 1/second.TXT"), Archive = File(s"$archiveSub5Path/sub 1/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub5Path/sub 1/test.jpg"), Archive = File(s"$archiveSub5Path/sub 1/test.jpg"), State = MOVED),
    )
    val expected = SsyncItem(
      Name = name1,
      Source = sourceSub5Path,
      Archive = archiveSub5Path,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED

    )
    val expectedRemainingFiles = Seq()
    val ssyncItems = configConversions.convertSettingItemsToSyncItems(settings)
    val result = ssyncItems.map { i =>
      ssyncItemProcessor.processSsyncItem(i)
    }.head
    result.Name shouldBe expected.Name
    result.Source shouldBe expected.Source
    result.Archive shouldBe expected.Archive
    result.ProtectedDirectories shouldBe expected.ProtectedDirectories
    result.Extensions shouldBe expected.Extensions
    result.IgnoredExtensions shouldBe expected.IgnoredExtensions
    result.FileItem should contain theSameElementsAs expected.FileItem
    result.State shouldBe expected.State

    val remainingFiles = collectFiles(sourceSub5Path)
    remainingFiles shouldBe expectedRemainingFiles
  }
  it should "move just .txt extension files from source-sub 1 to destination-sub 1" in {
    val name1 = "sub directory 1"
    val sourceSub1Path = s"$sourceCopyPath/sub 1"
    val archiveSub1Path = s"$archiveCopyPath/sub 1"
    val extensions = Seq("txt")
    val ignoredExtensions = Seq[String]()
    val sourceSub1 = File(sourceSub1Path)

    val items = List(
      Item(Name = "sub directory 1", Path = "sub 1", ProtectedDirectories = Seq())
    )
    val settings = Settings(
      Source = sourceCopyPath,
      Archive = archiveCopyPath,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )
    val fileItems = List(
      FileItem(Item = File(s"$sourceSub1Path/first.txt"), Archive = File(s"$archiveSub1Path/first.txt"), State = MOVED)
    )
    val expected = SsyncItem(
      Name = name1,
      Source = sourceSub1Path,
      Archive = archiveSub1Path,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions.toSeq,
      IgnoredExtensions = ignoredExtensions.toSeq,
      FileItem = fileItems,
      State = PROCESSED
    )

    val expectedRemainingFiles = Seq(File(s"$sourceSub1Path/firstj.jpg"))
    val ssyncItems = configConversions.convertSettingItemsToSyncItems(settings)
    val result = ssyncItems.map { i =>
      ssyncItemProcessor.processSsyncItem(i)
    }.head
    result.Name shouldBe expected.Name
    result.Source shouldBe expected.Source
    result.Archive shouldBe expected.Archive
    result.ProtectedDirectories shouldBe expected.ProtectedDirectories
    result.Extensions shouldBe expected.Extensions
    result.IgnoredExtensions shouldBe expected.IgnoredExtensions
    result.FileItem should contain theSameElementsAs expected.FileItem
    result.State shouldBe expected.State

    val remainingFiles = collectFiles(sourceSub1Path)
    remainingFiles shouldBe expectedRemainingFiles
  }
  it should "move just .txt extension files from source-sub 2 to destination-sub 2" in {
    val name1 = "sub directory 2"
    val sub2 = "sub 2"
    val sourceSub2Path = s"$sourceCopyPath/$sub2"
    val archiveSub2Path = s"$archiveCopyPath/$sub2"
    val extensions = Seq("txt")
    val ignoredExtensions = Seq[String]()
    val sourceSub2 = File(sourceSub2Path)

    val items = List(
      Item(Name = "sub directory 2", Path = sub2, ProtectedDirectories = Seq())
    )
    val settings = Settings(
      Source = sourceCopyPath,
      Archive = archiveCopyPath,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )
    val fileItems = Seq(
      FileItem(Item = File(s"$sourceSub2Path/first.txt"), Archive = File(s"$archiveSub2Path/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub2Path/second.TXT"), Archive = File(s"$archiveSub2Path/second.TXT"), State = MOVED)
    )
    val expected = SsyncItem(
      Name = name1,
      Source = sourceSub2Path,
      Archive = archiveSub2Path,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED

    )
    val expectedRemainingFiles = Seq(File(s"$sourceSub2Path/test.jpg"))
    val ssyncItems = configConversions.convertSettingItemsToSyncItems(settings)
    val result = ssyncItems.map { i =>
      ssyncItemProcessor.processSsyncItem(i)
    }.head
    result.Name shouldBe expected.Name
    result.Source shouldBe expected.Source
    result.Archive shouldBe expected.Archive
    result.ProtectedDirectories shouldBe expected.ProtectedDirectories
    result.Extensions shouldBe expected.Extensions
    result.IgnoredExtensions shouldBe expected.IgnoredExtensions
    result.FileItem should contain theSameElementsAs expected.FileItem
    result.State shouldBe expected.State
    val remainingFiles = collectFiles(sourceSub2Path)
    remainingFiles shouldBe expectedRemainingFiles
  }
  it should "move just .txt extension files from source-sub 3 to destination-sub 3" in {
    val name1 = "sub directory 3"
    val sub3 = "sub 3"
    val sourceSub3Path = s"$sourceCopyPath/$sub3"
    val archiveSub3Path = s"$archiveCopyPath/$sub3"
    val extensions = Seq("txt")
    val ignoredExtensions = Seq[String]()
    val sourceSub3 = File(sourceSub3Path)

    val items = List(
      Item(Name = "sub directory 3", Path = sub3, ProtectedDirectories = Seq())
    )
    val settings = Settings(
      Source = sourceCopyPath,
      Archive = archiveCopyPath,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )
    val fileItems = Seq(
      FileItem(Item = File(s"$sourceSub3Path/first.txt"), Archive = File(s"$archiveSub3Path/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/second.TXT"), Archive = File(s"$archiveSub3Path/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/sub/first.txt"), Archive = File(s"$archiveSub3Path/sub/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/sub/second.TXT"), Archive = File(s"$archiveSub3Path/sub/second.TXT"), State = MOVED)
    )
    val expected = SsyncItem(
      Name = name1,
      Source = sourceSub3Path,
      Archive = archiveSub3Path,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED

    )
    val expectedRemainingFiles = Seq(
      File(s"$sourceSub3Path/test.jpg"),
      File(s"$sourceSub3Path/sub"),
      File(s"$sourceSub3Path/sub/test.jpg")
    )
    val ssyncItems = configConversions.convertSettingItemsToSyncItems(settings)
    val result = ssyncItems.map { i =>
      ssyncItemProcessor.processSsyncItem(i)
    }.head
    result.Name shouldBe expected.Name
    result.Source shouldBe expected.Source
    result.Archive shouldBe expected.Archive
    result.ProtectedDirectories shouldBe expected.ProtectedDirectories
    result.Extensions shouldBe expected.Extensions
    result.IgnoredExtensions shouldBe expected.IgnoredExtensions
    result.FileItem should contain theSameElementsAs expected.FileItem
    result.State shouldBe expected.State

    val remainingFiles = collectFiles(sourceSub3Path)
    remainingFiles should contain theSameElementsAs expectedRemainingFiles
  }
  it should "move all files from source-sub 3 to destination-sub 3 and leave source sub directory" in {
    val name = "sub directory 3"
    val sub3 = "sub 3"
    val sourceSub3Path = s"$sourceCopyPath/$sub3"
    val archiveSub3Path = s"$archiveCopyPath/$sub3"
    val extensions = Seq("*")
    val ignoredExtensions = Seq[String]()

    val items = List(
      Item(Name = name, Path = sub3, ProtectedDirectories = Seq("sub"))
    )
    val settings = Settings(
      Source = sourceCopyPath,
      Archive = archiveCopyPath,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )
    val fileItems = Seq(
      FileItem(Item = File(s"$sourceSub3Path/first.txt"), Archive = File(s"$archiveSub3Path/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/second.TXT"), Archive = File(s"$archiveSub3Path/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/test.jpg"), Archive = File(s"$archiveSub3Path/test.jpg"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/sub/first.txt"), Archive = File(s"$archiveSub3Path/sub/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/sub/second.TXT"), Archive = File(s"$archiveSub3Path/sub/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/sub/test.jpg"), Archive = File(s"$archiveSub3Path/sub/test.jpg"), State = MOVED)
    )
    val expected = SsyncItem(
      Name = name,
      Source = sourceSub3Path,
      Archive = archiveSub3Path,
      ProtectedDirectories = Seq("sub"),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED

    )
    val expectedRemainingFiles = Seq(
      File(s"$sourceSub3Path/sub")
    )

    val ssyncItems = configConversions.convertSettingItemsToSyncItems(settings)
    val result = ssyncItems.map { i =>
      ssyncItemProcessor.processSsyncItem(i)
    }.head

    result.Name shouldBe expected.Name
    result.Source shouldBe expected.Source
    result.Archive shouldBe expected.Archive
    result.ProtectedDirectories shouldBe expected.ProtectedDirectories
    result.Extensions shouldBe expected.Extensions
    result.IgnoredExtensions shouldBe expected.IgnoredExtensions
    result.FileItem should contain theSameElementsAs expected.FileItem
    result.State shouldBe expected.State

    val remainingFiles = collectFiles(sourceSub3Path)
    remainingFiles should contain theSameElementsAs expectedRemainingFiles
  }
  it should "move all files from source-sub 4 to destination-sub 4 and leave source sub and sub 1 directory" in {
    val name1 = "sub directory 4"
    val sub4 = "sub 4"
    val sourceSub4Path = s"$sourceCopyPath/$sub4"
    val archiveSub4Path = s"$archiveCopyPath/$sub4"
    val extensions = Seq("*")
    val ignoredExtensions = Seq[String]()

    val protectedDirectories = Seq("sub", "sub 1")
    val items = List(
      Item(Name = "sub directory 4", Path = sub4, ProtectedDirectories = protectedDirectories)
    )
    val settings = Settings(
      Source = sourceCopyPath,
      Archive = archiveCopyPath,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )
    val fileItems = Seq(
      FileItem(Item = File(s"$sourceSub4Path/first.txt"), Archive = File(s"$archiveSub4Path/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/second.TXT"), Archive = File(s"$archiveSub4Path/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/test.jpg"), Archive = File(s"$archiveSub4Path/test.jpg"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub/first.txt"), Archive = File(s"$archiveSub4Path/sub/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub/second.TXT"), Archive = File(s"$archiveSub4Path/sub/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub/test.jpg"), Archive = File(s"$archiveSub4Path/sub/test.jpg"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub/sub 1/first.txt"), Archive = File(s"$archiveSub4Path/sub/sub 1/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub/sub 1/second.TXT"), Archive = File(s"$archiveSub4Path/sub/sub 1/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub/sub 1/test.jpg"), Archive = File(s"$archiveSub4Path/sub/sub 1/test.jpg"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub 1/first.txt"), Archive = File(s"$archiveSub4Path/sub 1/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub 1/second.TXT"), Archive = File(s"$archiveSub4Path/sub 1/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub4Path/sub 1/test.jpg"), Archive = File(s"$archiveSub4Path/sub 1/test.jpg"), State = MOVED),
    )
    val expected = SsyncItem(
      Name = name1,
      Source = sourceSub4Path,
      Archive = archiveSub4Path,
      ProtectedDirectories = protectedDirectories,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED

    )
    val expectedRemainingFiles = Seq(
      File(s"$sourceSub4Path/sub"),
      File(s"$sourceSub4Path/sub/sub 1"),
      File(s"$sourceSub4Path/sub 1")
    )
    val ssyncItems = configConversions.convertSettingItemsToSyncItems(settings)
    val result = ssyncItems.map { i =>
      ssyncItemProcessor.processSsyncItem(i)
    }.head
    result.Name shouldBe expected.Name
    result.Source shouldBe expected.Source
    result.Archive shouldBe expected.Archive
    result.ProtectedDirectories shouldBe expected.ProtectedDirectories
    result.Extensions shouldBe expected.Extensions
    result.IgnoredExtensions shouldBe expected.IgnoredExtensions
    result.FileItem should contain theSameElementsAs expected.FileItem
    result.State shouldBe expected.State

    val remainingFiles = collectFiles(sourceSub4Path)
    remainingFiles should contain theSameElementsAs expectedRemainingFiles
  }
  it should "move just .txt extension files from source-sub 3 to destination-sub 3 with exclusion jpg" in {
    val name1 = "sub directory 3"
    val sub3 = "sub 3"
    val sourceSub3Path = s"$sourceCopyPath/$sub3"
    val archiveSub3Path = s"$archiveCopyPath/$sub3"
    val extensions = Seq("*")
    val ignoredExtensions = Seq[String]("jpg")

    val items = List(
      Item(Name = "sub directory 3", Path = sub3, ProtectedDirectories = Seq())
    )
    val settings = Settings(
      Source = sourceCopyPath,
      Archive = archiveCopyPath,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )
    val fileItems = Seq(
      FileItem(Item = File(s"$sourceSub3Path/first.txt"), Archive = File(s"$archiveSub3Path/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/second.TXT"), Archive = File(s"$archiveSub3Path/second.TXT"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/sub/first.txt"), Archive = File(s"$archiveSub3Path/sub/first.txt"), State = MOVED),
      FileItem(Item = File(s"$sourceSub3Path/sub/second.TXT"), Archive = File(s"$archiveSub3Path/sub/second.TXT"), State = MOVED)
    )
    val expected = SsyncItem(
      Name = name1,
      Source = sourceSub3Path,
      Archive = archiveSub3Path,
      ProtectedDirectories = Seq[String](),
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      FileItem = fileItems,
      State = PROCESSED
    )

    val expectedRemainingFiles = Seq(
      File(s"$sourceSub3Path/test.jpg"),
      File(s"$sourceSub3Path/sub/test.jpg"),
      File(s"$sourceSub3Path/sub")
    )
    val ssyncItems = configConversions.convertSettingItemsToSyncItems(settings)
    val result = ssyncItems.map { i =>
      ssyncItemProcessor.processSsyncItem(i)
    }.head
    result.Name shouldBe expected.Name
    result.Source shouldBe expected.Source
    result.Archive shouldBe expected.Archive
    result.ProtectedDirectories shouldBe expected.ProtectedDirectories
    result.Extensions shouldBe expected.Extensions
    result.IgnoredExtensions shouldBe expected.IgnoredExtensions
    result.FileItem should contain theSameElementsAs expected.FileItem
    result.State shouldBe expected.State

    val remainingFiles = collectFiles(sourceSub3Path)
    remainingFiles should contain theSameElementsAs expectedRemainingFiles
  }
  private def collectFiles(path: String) = {
    ioCapabilities.collectFiles(path, false) ++
      ioCapabilities.collectFiles(path, true)
  }
}
