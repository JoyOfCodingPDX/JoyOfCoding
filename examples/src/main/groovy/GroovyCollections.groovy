def strings = ['one', '2', 'three']
println strings['number']  // isNumber
println strings*.length()
switch ('one') {
  case strings:
    println "Found it"
    break
  default:
    println "Where was it?"
    break
}