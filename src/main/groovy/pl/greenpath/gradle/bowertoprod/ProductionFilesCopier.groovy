package pl.greenpath.gradle.bowertoprod

import org.gradle.api.Project

class ProductionFilesCopier {

  private String bowerComponentsPath
  private Project project
  private BowerToProdExtension bowerToProdExtension

  ProductionFilesCopier(String bowerComponentsPath, Project project) {
    this.project = project
    this.bowerComponentsPath = bowerComponentsPath
    bowerToProdExtension = project.getExtensions().getByType(BowerToProdExtension)
  }

  void copy(String libraryName) {
    List<String> filesToCopy = getFilesToCopy(libraryName)
    String libraryPath = getLibraryPath(libraryName)
    String buildDir = bowerToProdExtension.getBuildDirPath(libraryName)
    File destinationDirectory = getDestination(libraryName)
    File sourcesDirectory = new File(project.file(libraryPath), buildDir)
    doCopy(destinationDirectory, sourcesDirectory, filesToCopy)
  }

  private void doCopy(File destination, File sourcesDirectory, List<String> filesToCopy) {
    new AntBuilder().copy(todir: destination) {
      fileset(dir: sourcesDirectory) {
        for (String path : filesToCopy) {
          include(name: path)
        }
      }
    }
  }

  private List<String> getFilesToCopy(String libraryName) {
    ProductionFilesExtractor filesExtractor = new ProductionFilesExtractor(getLibraryPath(libraryName), project)
    if (!bowerToProdExtension.hasCustomization(libraryName)) {
      return filesExtractor.getProductionFiles()
    }
    LibraryDefinition customization = bowerToProdExtension.getCustomization(libraryName)
    if (customization.customFiles.empty) {
      return filesExtractor.getProductionFiles(customization.buildDir)
    }
    return customization.getCustomFiles()

  }

  private String getLibraryPath(String libraryName) {
    return bowerComponentsPath + '/' + libraryName + '/'
  }

  private File getDestination(String libraryName) {
    LibraryDefinition customization = bowerToProdExtension.getCustomization(libraryName)
    if (customization != null && customization.destination != null) {
      return project.file(customization.destination)
    }

    return new File(bowerToProdExtension.destination, libraryName)
  }

}
