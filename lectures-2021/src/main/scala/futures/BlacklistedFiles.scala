package futures

import java.io.File

import org.apache.commons.io.FileUtils._

import scala.collection.JavaConverters._
import scala.concurrent._
import scala.io.Source

/**
 * Example 11: Blacklisted files
 *
 * This is an exercise.
 *
 * @author Aleksandard Prokopec, Ilya Sergey
 */
object BlacklistedFiles {

  implicit val ec = ExecutionContext.global

  /**
   * Finds all patterns for black-listed files in the file with the given name.
   *
   * For instance, blacklistFile("./.gitignore") will give a list of patterns
   * of the files to ignore in the current Git project.
   *
   */
  def blacklistFile(name: String): Future[List[String]] = Future {
    val source = Source.fromFile(name)
    try {
      val lines = source.getLines.toList
      for (x <- lines if !x.startsWith("#") && !x.isEmpty) yield x
    } finally {
      source.close()
    }
  }

  /**
   * Finds all files whose paths match the provided patterns.  
   */
  def findFiles(patterns: List[String]): Future[List[String]] = {
    val root = new File(".")
    Future {
      for {
        f <- iterateFiles(root, null, true).asScala.toList
        pat <- patterns
        abspat = s"${root.getCanonicalPath}${File.separator}$pat"
        if f.getCanonicalPath.contains(abspat)
      } yield f.getCanonicalPath
    }
  }

  /**
   * Given the blacklist file (e.g., .gitignore), list all blacklisted files.
   */
  def getAllBlacklisted(blackListFile: String): Future[List[String]] =
  // TODO: Implement me
    for {
      patterns <- blacklistFile(blackListFile)
      files <- findFiles(patterns)
    } yield files

  def main(args: Array[String]): Unit = {

    // TODO: Print out all black-listed files from ".gitignore" of this project 

    // SUBST
    getAllBlacklisted(".gitignore")
      .foreach(ls => println(ls.mkString("\n")))
    // SOL

    Thread.sleep(2000)
  }

}
