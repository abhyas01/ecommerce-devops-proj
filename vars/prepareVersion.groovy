/**
 * prepareVersion
 * generates a semver docker tag from package.json + build number + git SHA.
 * writes artifacts/version.env and returns a map with DOCKER_TAG, BRANCH_SAFE, VERSION.
 *
 *   def ver = prepareVersion(packageJsonPath: 'src/package.json')
 *   env.DOCKER_TAG = ver.DOCKER_TAG
 */
def call(Map config = [:]) {
  def pkgPath = config.packageJsonPath ?: 'src/package.json'

  sh """
    set -eu
    mkdir -p artifacts

    BASE_VERSION=\$(node -p "require('./${pkgPath}').version")
    GIT_SHA=\$(git rev-parse --short HEAD)
    BRANCH_SAFE=\$(echo "\${BRANCH_NAME:-unknown}" | tr '/' '-')

    VERSION="\${BASE_VERSION}+build.\${BUILD_NUMBER}.\${GIT_SHA}"
    DOCKER_TAG="\${VERSION//+/-}"

    echo "\${VERSION}" | tee artifacts/VERSION.txt
    printf "VERSION=%s\nDOCKER_TAG=%s\nBRANCH_SAFE=%s\n" \
      "\${VERSION}" "\${DOCKER_TAG}" "\${BRANCH_SAFE}" > artifacts/version.env

    echo "Version  : \${VERSION}"
    echo "DockerTag: \${DOCKER_TAG}"
  """

  return readProperties(file: 'artifacts/version.env')
}