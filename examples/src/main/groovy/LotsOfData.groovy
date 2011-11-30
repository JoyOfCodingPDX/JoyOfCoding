static def process(File file, Closure work) {
  FileReader reader = new FileReader(file)
  BufferedReader br = new BufferedReader(reader)
  try {
    while (br.ready()) {
      String line = br.readLine()
      int datum = Integer.parseInt(line)
      work.call(datum)
    }

  } finally {
    br.close()
  }
}

static def main(String[] args) {
  File file = new File(args[0])
  int sum = 0;
  process(file, {
    sum += it
  })
  println "Sum is $sum"

  int count = 0
  process(file, {
    count++
  })
  avg = ((double) sum) / count
  println "Average is $avg"

  int sumOfSquares = 0;
  process(file, {
    sumOfSquares += it ** 2
  })
  stdDev = Math.sqrt(sumOfSquares / count)
}
