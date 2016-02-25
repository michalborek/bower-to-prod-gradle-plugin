package pl.greenpath.gradle.bowertoprod

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static pl.greenpath.gradle.bowertoprod.BuildscriptClasspathDefinitionGenerator.generateBuildscriptClasspathDefinition

class BowerToProdPluginFunctionalTest extends Specification {

  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()

  private File buildFile

  def setup() {
    File settingsFile = testProjectDir.newFile('settings.gradle')
    settingsFile << "rootProject.name = 'myProject'"
    buildFile = testProjectDir.newFile('build.gradle')
    buildFile << generateBuildscriptClasspathDefinition()
    testProjectDir.newFile('.bowerrc') << getBowerrc()
    testProjectDir.newFile('bower.json') << getMainBowerJson()
    testProjectDir.newFolder('app', 'components', 'almond')
    testProjectDir.newFile('app/components/almond/bower.json') << getLibBowerJson()
    buildFile << '''
        apply plugin: 'pl.greenpath.gradle.bowertoprod'

        bowerToProd {
          destinationDir file('.')
        }
    '''
  }

  def 'should apply a plugin and add copy task'() {

    when:
    BuildResult result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('copyBowerProductionDependencies', '--stacktrace')
        .build()
    then:
    println result.output
  }

  def 'should allow defining customizations'() {
    given:
    buildFile << '''
        bowerToProd {
          lib name: 'angular', buildDir: 'build', includes: ['angular.js']
        }
    '''
    when:
    BuildResult result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('copyBowerProductionDependencies')
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
         "almond": "~0.2.1"
        }
      }
    '''

  }

  private static String getLibBowerJson() {
    return '''
      {
       "main": ["./build/a.js", "build/b.js"]
      }
    '''
  }

}
