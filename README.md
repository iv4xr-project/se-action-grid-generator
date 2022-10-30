# se-action-grid-generator
Project that generated a grid of action sequences for two players, made specifically for the video game "Space Engineers".

In the project the MainApp.class has the function main that serves as a simple means of running on a console as well as providing an example of how to use the action grid generator. 

First, we generate a simple game map and instantiate the game simulator with it so it can generate bugs that can be detected in that map. Then we instantiate the action grid generator and start generating action sequence pairs until the whole grid has pairs on every cell. Finally, we use use the diversity calculator to calculate the total diversity and the least diverse match (lowest diversity between two pairs on the grid) of the grid as well as run all the action sequence pairs on the game simulator and record how many bugs are detected. If a bug was already detected by a pair on the same grid and another pair detects that same bug it won't be counted towards the number of bugs detected since it has already been detected. 
