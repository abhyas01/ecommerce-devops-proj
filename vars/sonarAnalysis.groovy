/**
 * sonarAnalysis
 * runs sonarQube scanner using sonar-project.properties in the workspace root
 * then waits for the quality gate result
 *
 *   - SonarQube server configured with name 'sq1'
 *   - SonarQube Scanner tool named 'SonarScanner'
 *
 * Usage:
 *  sonarAnalysis(projectKey: 'ecommerce-product-service')
 */
 
def call(Map config = [:]) {
  def projectKey = config.projectKey ?: error('sonarAnalysis: projectKey is required')
  def sonarEnv   = config.sonarEnv   ?: 'sq1'
  def waitGate   = config.containsKey('waitGate') ? config.waitGate : true

  def scannerHome = tool 'SonarScanner'

  withSonarQubeEnv(sonarEnv) {
    sh """
      set -eu
      ${scannerHome}/bin/sonar-scanner \
        -Dsonar.projectKey=${projectKey} \
        -Dproject.settings=sonar-project.properties
    """
  }

  if (waitGate) {
    timeout(time: 5, unit: 'MINUTES') {
      waitForQualityGate abortPipeline: true
    }
  }
}