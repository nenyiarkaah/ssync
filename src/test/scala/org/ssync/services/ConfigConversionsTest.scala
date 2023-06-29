package org.ssync.services

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.ssync.configs.{Item, Settings}
import org.ssync.models.SsyncItem
class ConfigConversionsTest extends AnyFlatSpec with Matchers {

  val configConversions = new ConfigConversions()
  val source = "/source"
  val archive = "/archive"
  val extensions = Seq("jpg")
  val ignoredExtensions = Seq[String]()

  "convertSettingItemsToSyncItems" should "return an empty list when Items is empty" in {
    val items = List()
    val settings = Settings(Source = source,
      Archive = archive,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )
    val expected = List()
    val result =  configConversions.convertSettingItemsToSyncItems(settings)
    result shouldBe expected
  }
  it should "return a list of SyncItems with populated Items" in {
    val sub1path = "sub1"
    val sub2path = "sub2"
    val name1 = "sub 1"
    val name2 = "sub 2"

    val items = List(
      Item(Name = name1, Path = s"$sub1path", ProtectedDirectories = List[String]()),
      Item(Name = name2, Path = s"$sub2path", ProtectedDirectories = List[String]())
    )
    val settings = Settings(Source = source,
      Archive = archive,
      Extensions = extensions,
      IgnoredExtensions = ignoredExtensions,
      Items = items
    )
    val expected1 = List(SsyncItem(
        Name = name1,
        Source = s"$source/$sub1path",
        Archive = s"$archive/$sub1path",
        ProtectedDirectories = Seq[String](),
        Extensions = extensions,
        IgnoredExtensions = ignoredExtensions
      ),
      SsyncItem(
        Name = name2,
        Source = s"$source/$sub2path",
        Archive = s"$archive/$sub2path",
        ProtectedDirectories = Seq[String](),
        Extensions = extensions,
        IgnoredExtensions = ignoredExtensions
    ))
    val result = configConversions.convertSettingItemsToSyncItems(settings)
    result shouldEqual expected1
  }
}
