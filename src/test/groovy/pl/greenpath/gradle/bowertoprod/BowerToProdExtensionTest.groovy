package pl.greenpath.gradle.bowertoprod

import spock.lang.Specification

class BowerToProdExtensionTest extends Specification {

  def 'should store customizations for given files'() {
    given:
    BowerToProdExtension extension = new BowerToProdExtension()
    when:
    extension.lib name: 'angular', buildDir: 'build', includes: ['angular.js']
    then:
    extension.getCustomizations().size() == 1
    LibraryDefinition angularLib = extension.getCustomization('angular')
    angularLib.getName() == 'angular'
    angularLib.getBuildDir() == 'build'
    angularLib.getIncludes() == ['angular.js']
  }

  def 'should strip build dir from includes'() {
    given:
    BowerToProdExtension extension = new BowerToProdExtension()
    when:
    extension.lib name: 'angular', buildDir: 'build', includes: ['./build/angular.js', 'build/angular2.js']
    then:
    extension.getCustomization('angular').getCustomFiles() == ['angular.js', 'angular2.js']
  }
}
