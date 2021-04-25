cd src
javac -d ../bin *.java
cd ../bin
jar cfe Minilang.jar Main Main.class *
cd ..
