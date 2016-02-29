package pl.greenpath.gradle.bowertoprod

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
    testProjectDir.newFolder('app', 'components', 'test')
    testProjectDir.newFile('app/components/almond/bower.json') << getLibBowerJson('["./build/a.js", "build/b.js"]')
    testProjectDir.newFile('app/components/test/bower.json') << getLibBowerJson('"a.js"')
    testProjectDir.newFile('app/components/test/a.js') << 'dummy'
    buildFile << '''
        apply plugin: 'pl.greenpath.gradle.bowertoprod'

        bowerToProd {
          destination file('dest')
        }
    '''
  }

  def 'should copy files defined as main files'() {
    given:
    testProjectDir.newFolder('app', 'components', 'almond', 'build')
    testProjectDir.newFile('app/components/almond/build/a.js')
    testProjectDir.newFile('app/components/almond/build/b.js')
    when:
    GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('copyBowerProductionDependencies', '--stacktrace')
        .build()
    then:
    new File(testProjectDir.getRoot(), 'dest/almond/build/a.js').exists()
    new File(testProjectDir.getRoot(), 'dest/almond/build/b.js').exists()
    new File(testProjectDir.getRoot(), 'dest/test/a.js').exists()
  }

  def 'should strip build dir if build dir defined in extension'() {
    given:
    buildFile << '''
        apply plugin: 'pl.greenpath.gradle.bowertoprod'

        bowerToProd {
          lib name: 'almond', buildDir: 'build'
        }
    '''
    testProjectDir.newFolder('app', 'components', 'almond', 'build')
    testProjectDir.newFile('app/components/almond/build/a.js')
    testProjectDir.newFile('app/components/almond/build/b.js')
    when:
    GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('copyBowerProductionDependencies', '--stacktrace')
        .build()
    then:
    new File(testProjectDir.getRoot(), 'dest/almond/a.js').exists()
    new File(testProjectDir.getRoot(), 'dest/almond/b.js').exists()
    new File(testProjectDir.getRoot(), 'dest/test/a.js').exists()
  }

  def 'should not copy dependencies defined as ignored'() {
    given:
    testProjectDir.newFolder('app', 'components', 'almond', 'build')
    testProjectDir.newFile('app/components/almond/build/a.js')
    testProjectDir.newFile('app/components/almond/build/b.js')
    buildFile << '''
        bowerToProd {
          ignore 'test'
        }
    '''
    when:
    GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('copyBowerProductionDependencies')
        .build()
    then:
    new File(testProjectDir.getRoot(), 'dest/almond/build/a.js').exists()
    new File(testProjectDir.getRoot(), 'dest/almond/build/b.js').exists()
    new File(testProjectDir.getRoot(), 'dest/test/a.js').exists() == false
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
         "almond": "~0.2.1",
         "test": "1.1.1"
        }
      }
    '''

  }

  private static String getLibBowerJson(String mainFiles) {
    return """
      {
       "main": ${mainFiles}
      }
    """
  }

}
