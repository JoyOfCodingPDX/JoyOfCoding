/**
 * Student demonstrates the "properties" facility in Groovy
 */
class Student {
  final int id;  // read-only property
  def name;
  final List friends = new ArrayList();

  Student(id) {
    this.id = id;
  }

  def addFriend(friend) {
    friends.add(friend)
    this
  }

  def getFriends() {
    Collections.unmodifiableList(friends)
  }

  def leftShift(friend) {
    addFriend(friend)
  }
}
