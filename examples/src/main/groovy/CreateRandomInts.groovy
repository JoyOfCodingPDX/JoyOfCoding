new FileWriter(args[0]).withWriter { writer ->
  random = new Random()
  100.times {
    writer << random.nextInt()
    writer << "\n"
  }
}