package pl.greenpath.gradle.bowertoprod

import org.gradle.api.Project

class BowerToProdExtension {

  File destinationDir

  private Map<String, LibraryDefinition> customizations = [:]

  BowerToProdExtension(Project project) {
  }

  void lib(Map params) {
    customizations[params['name']] = new LibraryDefinition(params)
  }

  void destination(File destinationDir) {
    this.destinationDir = destinationDir
  }

  Map<String, LibraryDefinition> getCustomizations() {
    return customizations
  }

  LibraryDefinition getCustomization(String libraryName) {
    return customizations[libraryName]
  }

  String getBuildDirPath(String libraryName) {
    if (!customizations.containsKey(libraryName)) {
      return ''
    } else {
      return customizations[libraryName].buildDir ?: ''
    }
  }
}
