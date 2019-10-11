cd src
javac -d ../build *.java
cd ../build
jar cfe Minilang.jar Main Main.class *