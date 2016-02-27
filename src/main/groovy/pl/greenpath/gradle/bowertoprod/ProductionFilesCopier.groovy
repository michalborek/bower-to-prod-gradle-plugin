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
    String buildDir = bowerToProdExtension.getBuildDirPath(libraryName)
    new AntBuilder().copy(todir: getDestinationPath()) {
      fileset(dir: new File(project.file(libraryPath), buildDir)) {
        for (String path : filesToCopy) {
          include(name: path)
        }
      }
    }
  }

  private List<String> getFilesToCopy() {
    ProductionFilesExtractor filesExtractor = new ProductionFilesExtractor(getLibraryPath(), project)
    if (!hasCustomization()) {
      return filesExtractor.getProductionFiles()
    }
    LibraryDefinition customization = bowerToProdExtension.getCustomization(libraryName)
    if (customization.customFiles.empty) {
      return filesExtractor.getProductionFiles(customization.getBuildDir())
    }
    return customization.getCustomFiles()

  }

  private boolean hasCustomization() {
    return bowerToProdExtension.getCustomizations().containsKey(libraryName)
  }

  private String getLibraryPath() {
    return bowerComponentsPath + '/' + libraryName + '/'
  }

  private String getDestinationPath() {
    return new File(bowerToProdExtension.destinationDir, libraryName).absolutePath
  }

}
