package org.ssync.configs

case class Item(
                 Name: String,
                 Path: String,
                 ProtectedDirectories: Seq[String]
               )
