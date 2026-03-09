/**
 * deployToK8s
 * updates the image on a kubernetes deployment and waits for rollout
 *
 * Usage:
 *   deployToK8s(
 *     namespace:   'dev',
 *     serviceName: 'product-service',
 *     imageName:   '01abhyas/product-service',
 *     dockerTag:   env.DOCKER_TAG
 *   )
 */
def call(Map config = [:]) {
  def namespace   = config.namespace   ?: error('deployToK8s: namespace is required')
  def serviceName = config.serviceName ?: error('deployToK8s: serviceName is required')
  def imageName   = config.imageName   ?: error('deployToK8s: imageName is required')
  def dockerTag   = config.dockerTag   ?: error('deployToK8s: dockerTag is required')

  echo "Deploying ${serviceName}:${dockerTag} -> namespace '${namespace}'"

  sh """
    set -eu
    kubectl set image deployment/${serviceName} \
      ${serviceName}=${imageName}:${dockerTag} \
      -n ${namespace} --record || true

    kubectl rollout status deployment/${serviceName} \
      -n ${namespace} --timeout=120s
  """
}