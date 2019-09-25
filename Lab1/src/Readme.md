To run the code you must first compile the files using javac *.java

For example to run question one you would run:
java Main 1 > output.txt where output.txt can be any file name

To run a M/M/1 simulation with custom parameters with a simulation time of 1000 and rho value of 0.25 you would run:

java Main 2 1000 0.25 > output.txt *you can change the values of the simulation time and rho values

To run the M/M/1 simulation with the predefined lab parameters you would run:

javaa Main 3 > output.txt

To run the M/M/1/K simulation with custom parameters with a simulation time of 1000, rho value of 0.25, queue size of 10 you would run:

java Main 4 1000 0.25 10 >output.txt

To run the M/M/1/K simulation with the predefined lab parameters you would run:

java Main 5 > output.txt


All of the data produced by each simulation is stored in the .txt file that you specified