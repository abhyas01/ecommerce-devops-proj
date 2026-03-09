/**
 * buildAndPushImage
 * builds a docker image and pushes to Docker Hub.
 * requires jenkins credential with id 'dockerhub-credentials'
 *
 *   buildAndPushImage(
 *     imageName: '01abhyas/product-service',
 *     dockerTag: env.DOCKER_TAG,
 *     context: '.',
 *     target: 'production'
 *   )
 */
def call(Map config = [:]) {
  def imageName  = config.imageName  ?: error('buildAndPushImage: imageName is required')
  def dockerTag  = config.dockerTag  ?: error('buildAndPushImage: dockerTag is required')
  def context    = config.context    ?: '.'
  def dockerfile = config.dockerfile ?: 'Dockerfile'
  def target     = config.target     ?: ''

  def targetArg = target ? "--target ${target}" : ''
  def fullImage = "${imageName}:${dockerTag}"

  sh """
    set -eu
    docker build ${targetArg} -f ${dockerfile} -t ${fullImage} ${context}
    echo "Built: ${fullImage}"
  """

  withCredentials([usernamePassword(
    credentialsId: 'dockerhub-credentials',
    usernameVariable: 'DH_USER',
    passwordVariable: 'DH_PASS',
  )]) {
    sh """
      set -eu
      echo "\$DH_PASS" | docker login -u "\$DH_USER" --password-stdin
      docker push ${fullImage}
      docker logout
      echo "Pushed: ${fullImage}"
    """
  }
}