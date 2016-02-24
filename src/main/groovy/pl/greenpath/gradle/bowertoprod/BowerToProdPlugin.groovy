package pl.greenpath.gradle.bowertoprod

import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project

class BowerToProdPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
//    project.extensions.create('bowerToProd', BowerToProdExtension, project)

    project.task('copyBowerProductionDependencies') << {
      println getBowerFilesDirectory(project)
      getBowerDependencies(project).collect(copyProductionFiles(it, project))
    }
  }

  void copyProductionFiles(String library, Project project) {
    // TODO check bower.json
  }

  private String getBowerFilesDirectory(Project project) {
    def defaultDirectory = 'bower_components'
    def file = project.file('.bowerrc')
    if (!file.exists()) {
      return defaultDirectory
    }
    return new JsonSlurper().parse(file)['directory'] ?: defaultDirectory
  }

  private List<String> getBowerDependencies(Project project) {
    def file = project.file('bower.json')
    if (!file.exists()) {
      return []
    }

    Map dependencies = new JsonSlurper().parse(file)['dependencies']
    return dependencies.keySet().toList()
  }
}