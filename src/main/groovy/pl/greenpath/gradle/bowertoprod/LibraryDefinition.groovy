package pl.greenpath.gradle.bowertoprod

class LibraryDefinition {
  String name
  String buildDir
  List<String> includes

  public List<String> getCustomFiles() {
    if (includes.empty) {
      return ['**']
    }
    return includes*.replaceAll("^(\\./)?${buildDir}(/)?", '')
  }
}
