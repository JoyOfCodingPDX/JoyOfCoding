# Testing main methods

- Test Java main methods with `edu.pdx.cs.joy.InvokeMainTestCase` from the shared `projects` test JAR. Use `invokeMain` to capture standard output and error rather than calling `System.setOut()` or implementing a separate capture mechanism in tests.
- Use `invokeMainAllowingMutableStaticFields` only when mutable static fields are intentional.
