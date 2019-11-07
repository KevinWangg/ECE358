To run the code you must first compile the files using javac *.java

For example to all persistent CSMACD one you would run:

java Main 1 > output.txt where output.txt can be any file name

For example to all non persistent CSMACD one you would run:

java Main 2 > output.txt where output.txt can be any file name

For example to run a custom persistent CSMACD test you would run with simulation time of 50, arrival rate of 10 and number of nodes is 100 you would run:

**WARNING**
For when A = 20 and N = 100 simulations will take a long time, so try to keep simulation time low
**WARNING**

java Main 3 50 10 100 > output.txt where output.txt can be any file name

For example to run a custom non persistent CSMACD test you would run with simulation time of 50, arrival rate of 10 and number of nodes is 100 you would run:

**WARNING**
For when A = 20 and N = 100 simulations will take a long time, so try to keep simulation time low
**WARNING**

java Main 4 50 10 100 > output.txt where output.txt can be any file name

All of the data produced by each simulation is stored in the .txt file that you specified






