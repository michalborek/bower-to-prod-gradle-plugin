package pl.greenpath.gradle.bowertoprod

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static pl.greenpath.gradle.bowertoprod.BuildscriptClasspathDefinitionGenerator.generateBuildscriptClasspathDefinition

class BowerToProdPluginFunctionalTest extends Specification {

  public static final String COPY_TASK_NAME = ':copyBowerProductionDependencies'
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
    runTask()
    then:
    new File(testProjectDir.getRoot(), 'dest/almond/build/a.js').exists()
    new File(testProjectDir.getRoot(), 'dest/almond/build/b.js').exists()
    new File(testProjectDir.getRoot(), 'dest/test/a.js').exists()
  }

  def 'should skip consecutive builds, when nothing changed'() {
    given:
    testProjectDir.newFolder('app', 'components', 'almond', 'build')
    testProjectDir.newFile('app/components/almond/build/a.js')
    testProjectDir.newFile('app/components/almond/build/b.js')
    when:
    BuildResult firstBuild = runTask()
    BuildResult secondBuild = runTask()
    then:
    firstBuild.task(COPY_TASK_NAME).outcome == TaskOutcome.SUCCESS
    secondBuild.task(COPY_TASK_NAME).outcome == TaskOutcome.UP_TO_DATE
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
    runTask()
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
    runTask()
    then:
    new File(testProjectDir.getRoot(), 'dest/almond/build/a.js').exists()
    new File(testProjectDir.getRoot(), 'dest/almond/build/b.js').exists()
    new File(testProjectDir.getRoot(), 'dest/test/a.js').exists() == false
  }

  private BuildResult runTask() {
    return GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments(COPY_TASK_NAME, '--stacktrace')
        .build()
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
