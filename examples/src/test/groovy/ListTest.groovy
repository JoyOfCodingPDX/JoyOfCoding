class ListTest extends GroovyTestCase {

  void testListSize() {
    def list = [1, 2]
    assertEquals(2, list.size())
  }

}
