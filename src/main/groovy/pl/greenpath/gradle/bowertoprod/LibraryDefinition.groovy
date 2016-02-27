package pl.greenpath.gradle.bowertoprod

class LibraryDefinition {
  String name
  String buildDir

  private List<String> includes = []

  List<String> getIncludes() {
    return includes
  }

  void setIncludes(List<String> includes) {
    this.includes.addAll(includes)
  }

  public List<String> getCustomFiles() {
    return includes*.replaceAll("^(\\./)?${buildDir}(/)?", '')
  }
}
