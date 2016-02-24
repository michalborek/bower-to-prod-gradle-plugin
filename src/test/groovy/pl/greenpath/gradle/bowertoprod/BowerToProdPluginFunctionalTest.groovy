package pl.greenpath.gradle.bowertoprod

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static pl.greenpath.gradle.bowertoprod.BuildscriptClasspathDefinitionGenerator.generateBuildscriptClasspathDefinition

class BowerToProdPluginFunctionalTest extends Specification {

  @Rule
  public final TemporaryFolder testProjectDir = new TemporaryFolder();
  private File buildFile;

  def setup() {
    File settingsFile = testProjectDir.newFile('settings.gradle')
    settingsFile << "rootProject.name = 'myProject'"
    buildFile = testProjectDir.newFile('build.gradle')
    buildFile << generateBuildscriptClasspathDefinition()
    testProjectDir.newFile('.bowerrc') << getBowerrc()
    testProjectDir.newFile('bower.json') << getMainBowerJson()
  }

  def 'should apply a plugin and add copy task'() {
    given:
    buildFile << '''
        apply plugin: 'pl.greenpath.gradle.bowertoprod'
    '''
    when:
    BuildResult result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('copyBowerProductionDependencies', '--stacktrace')
        .build()
    then:
    println result.output
  }

  private static String getBowerrc() {
    '''
      {
        "directory": "app/components/"
      }
    '''
  }

  private static String getMainBowerJson() {
    '''
      {
       "dependencies": {
         "spoonjs": "*",
         "requirejs": "~2.1.2",
         "requirejs-text": "~2.0.3",
         "require-css": "*",
         "almond": "~0.2.1"
        }
      }
    '''

  }

}
