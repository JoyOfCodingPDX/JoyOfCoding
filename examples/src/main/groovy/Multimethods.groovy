import edu.pdx.cs410J.lang.*

def buildZoo(Animal animal) {
  println "Build house for " + animal
}

def buildZoo(Bird bird) {
  println "Build aviary for " + bird
}

static def main(args) {
  animals = [new Cow("Bossie"), new Duck("Don"), new Ant("Bill")]
  for (animal in animals) {
    buildZoo(animal)
  }
}