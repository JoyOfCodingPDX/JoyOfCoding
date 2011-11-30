println( new StringBuilder().with {
  append "One"
  append " Two"
  return it.toString()
})

[1, 2].with {
  println "this is ${this}"
  println "owner is ${owner}"
  println "delegate is ${delegate}"
}
