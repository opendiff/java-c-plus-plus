// HelloWorld.java
public class HelloWorld {
  // Declare a native method that returns a byte array
  public native byte[] getBytes();

  // Load the shared library
  static {
      System.loadLibrary("hello");
  }

  public static void main(String[] args) {
      HelloWorld hw = new HelloWorld();
      byte[] bytes = hw.getBytes();
      System.out.println(new String(bytes));
  }
}
