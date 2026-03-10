def call(Map config = [:]) {
  def imageName  = config.imageName  ?: error('trivyScan: imageName is required')
  def dockerTag  = config.dockerTag  ?: error('trivyScan: dockerTag is required')
  def severity   = config.severity   ?: 'HIGH,CRITICAL'
  def failOnVuln = config.containsKey('failOnVuln') ? config.failOnVuln : false
  def exitCode   = failOnVuln ? '1' : '0'
  def fullImage  = "${imageName}:${dockerTag}"
  def reportFile = "artifacts/trivy-report.json"
  def trivyBin   = '/opt/homebrew/bin/trivy'

  sh """
    set -eu
    mkdir -p artifacts

    echo "=== Trivy scan: ${fullImage} ==="

    ${trivyBin} image \
      --severity ${severity} \
      --format json \
      --output ${reportFile} \
      --exit-code ${exitCode} \
      ${fullImage} || true

    ${trivyBin} image \
      --severity ${severity} \
      --format table \
      ${fullImage} || true

    echo "=== Scan complete. Report: ${reportFile} ==="
  """

  archiveArtifacts artifacts: reportFile, allowEmptyArchive: true
}