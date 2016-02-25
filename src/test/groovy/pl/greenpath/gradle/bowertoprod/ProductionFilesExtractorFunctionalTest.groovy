package pl.greenpath.gradle.bowertoprod

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ProductionFilesExtractorFunctionalTest extends Specification {

  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()

  private File buildFile

  private Project project

  def setup() {
    project = ProjectBuilder.builder().build()
    project.setBuildDir(testProjectDir.root)
    testProjectDir.newFile('bower.json') << getBowerJson()
  }

  def 'should extract production files from bower.json'() {
    given:
    ProductionFilesExtractor extractor = new ProductionFilesExtractor(testProjectDir.root.absolutePath + '/', project)
    expect:
    extractor.getProductionFiles() == ['./build/a.js', 'build/b.js']

  }

  private static String getBowerJson() {
    '''
      {
       "main": ["./build/a.js", "build/b.js"]
      }
    '''
  }
}
