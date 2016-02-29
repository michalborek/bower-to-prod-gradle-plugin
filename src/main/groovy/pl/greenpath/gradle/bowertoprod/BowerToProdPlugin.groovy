package pl.greenpath.gradle.bowertoprod

import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project

class BowerToProdPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    BowerToProdExtension bowerToProdExtension = project.extensions.create('bowerToProd', BowerToProdExtension, project)

    project.task('copyBowerProductionDependencies') << {
      String bowerComponentsDirectory = getBowerFilesDirectory(project)
      ProductionFilesCopier copier = new ProductionFilesCopier(bowerComponentsDirectory, project)
      getBowerDependencies(project).findAll {
        !bowerToProdExtension.isIgnored(it)
      }.forEach {
        copier.copy(it)
      }
    }
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
