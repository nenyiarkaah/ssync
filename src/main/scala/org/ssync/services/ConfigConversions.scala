package org.ssync.services

import com.typesafe.scalalogging.{LazyLogging}
import org.ssync.configs.{Item, Settings}
import org.ssync.models.SsyncItem

class ConfigConversions extends LazyLogging {
  def convertSettingItemsToSyncItems(settings: Settings, items: List[Item]): List[SsyncItem] = {
    items.map { item =>
      val source = mergeSettingsSourcePathWithItemsSourcePath(settings, item.Path)
      SsyncItem(Name = item.Name,
        Source = source,
        Archive = mergeArchivePathWithSyncItemPath(settings, source),
        ProtectedDirectories = item.ProtectedDirectories.toSeq,
        Extensions = settings.Extensions.toSeq,
        IgnoredExtensions = settings.IgnoredExtensions.toSeq
      )
    }
  }

  private def mergeArchivePathWithSyncItemPath(settings: Settings, syncItemPath: String) = {
    val source = settings.Source
    val archive = settings.Archive
    syncItemPath.replace(source, archive)
  }

  private def mergeSettingsSourcePathWithItemsSourcePath(settings: Settings, syncItemPath: String) = {
    val source = settings.Source
    s"$source/$syncItemPath"
  }
}
