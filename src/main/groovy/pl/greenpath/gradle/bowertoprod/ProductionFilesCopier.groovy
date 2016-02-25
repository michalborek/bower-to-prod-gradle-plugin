package pl.greenpath.gradle.bowertoprod

import org.gradle.api.Project

class ProductionFilesCopier {

  private String libraryName
  private String bowerComponentsPath
  private Project project
  private BowerToProdExtension bowerToProdExtension

  ProductionFilesCopier(String libraryName, String bowerComponentsPath, Project project) {
    this.project = project
    this.bowerComponentsPath = bowerComponentsPath
    this.libraryName = libraryName
    bowerToProdExtension = project.getExtensions().getByType(BowerToProdExtension)
  }

  void copy() {
    List<String> filesToCopy = getFilesToCopy()
    String libraryPath = getLibraryPath()
    new AntBuilder().copy(todir: getDestinationPath()) {
      fileset(dir: libraryPath) {
        include filesToCopy
      }
    }
  }

  private List<String> getFilesToCopy() {
    if (hasCustomization()) {
      return bowerToProdExtension.getCustomization(libraryName).getCustomFiles()
    } else {
      return new ProductionFilesExtractor(getLibraryPath(), project).getProductionFiles()
    }
  }

  private boolean hasCustomization() {
    return bowerToProdExtension.getCustomizations().containsKey(libraryName)
  }

  private String getLibraryPath() {
    bowerComponentsPath + '/' + libraryName + '/'
  }

  private String getDestinationPath() {
    bowerToProdExtension.destinationDir.absolutePath + '/' + libraryName
  }

}
