def call(Map config = [:]) {
  sh "echo Aplicacion ${config.name}. Su version es ${config.version}."
} 